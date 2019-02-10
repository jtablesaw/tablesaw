/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.io.fixed;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.univocity.parsers.fixed.FixedWidthFormat;
import com.univocity.parsers.fixed.FixedWidthParser;
import com.univocity.parsers.fixed.FixedWidthParserSettings;
import org.apache.commons.lang3.tuple.Pair;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.AbstractParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.AddCellToColumnException;
import tech.tablesaw.io.ColumnTypeDetector;
import tech.tablesaw.io.TableBuildingUtils;

import javax.annotation.concurrent.Immutable;
import java.io.*;
import java.util.*;

import static tech.tablesaw.api.ColumnType.SKIP;

@Immutable
public class FixedWidthReader {

    private List<ColumnType> typeArrayOverrides = null;
    /**
     * Constructs a FixedWidthReader
     */
    public FixedWidthReader() {
    }

    /**
     * Constructs a FixedWidthReader with the given list of ColumnTypes
     * <p>
     * These are the only types that the FixedWidthReader can detect and parse
     */
    public FixedWidthReader(List<ColumnType> typeDetectionList) {
        this.typeArrayOverrides = typeDetectionList;
    }

    /**
     * Determines column types if not provided by the user
     * Reads all input into memory unless File was provided
     */
    private Pair<Reader, ColumnType[]> getReaderAndColumnTypes(FixedWidthReadOptions options) throws IOException {
        ColumnType[] types = options.columnTypes();
        byte[] bytesCache = null;

        if (types == null) {
            Reader reader = TableBuildingUtils.createReader(options, bytesCache);
            if (options.file() == null) {
                bytesCache = CharStreams.toString(reader).getBytes();
                // create a new reader since we just exhausted the existing one
                reader = TableBuildingUtils.createReader(options, bytesCache);
            }
            types = detectColumnTypes(reader, options);
        }

        return Pair.of(TableBuildingUtils.createReader(options, bytesCache), types);
    }

    public Table read(FixedWidthReadOptions options) throws IOException {
        return read(options, false);
    }

    private Table read(FixedWidthReadOptions options, boolean headerOnly) throws IOException {
        Pair<Reader, ColumnType[]> pair = getReaderAndColumnTypes(options);
        Reader reader = pair.getLeft();
        ColumnType[] types = pair.getRight();

        FixedWidthParser parser = fixedWidthParser(options);

        try {
            parser.beginParsing(reader);
            Table table = Table.create(options.tableName());

            List<String> headerRow = Lists.newArrayList(getHeaderNames(options, types, parser));

            for (int x = 0; x < types.length; x++) {
                if (types[x] != SKIP) {
                    String columnName = cleanName(headerRow.get(x));
                    if (Strings.isNullOrEmpty(columnName)) {
                        columnName = "Column " + table.columnCount();
                    }
                    Column<?> newColumn = types[x].create(columnName);
                    table.addColumns(newColumn);
                }
            }

            if (!headerOnly) {
                String[] columnNames = selectColumnNames(headerRow, types);
                int[] columnIndexes = new int[columnNames.length];
                for (int i = 0; i < columnIndexes.length; i++) {
                    // get the index in the original table, which includes skipped fields
                    columnIndexes[i] = headerRow.indexOf(columnNames[i]);
                }
                addRows(options, types, parser, table, columnIndexes);
            }

            return table;
        } finally {
            if (options.reader() == null) {
                // if we get a reader back from options it means the client opened it, so let the client close it
                // if it's null, we close it here.
                parser.stopParsing();
                reader.close();
            }
        }
    }

    private String[] getHeaderNames(FixedWidthReadOptions options, ColumnType[] types, FixedWidthParser parser) {
        if (options.header()) {
            String[] headerNames = parser.parseNext();
            // work around issue where Univocity returns null if a column has no header.
            for (int i = 0; i < headerNames.length; i++) {
                if (headerNames[i] == null) {
                    headerNames[i] = "C" + i;
                }
            }
            return headerNames;
        } else {
            // Placeholder column names for when the file read has no header
            String[] headerNames = new String[types.length];
            for (int i = 0; i < types.length; i++) {
                headerNames[i] = "C" + i;
            }
            return headerNames;
        }
    }

    private void addRows(FixedWidthReadOptions options, ColumnType[] types, FixedWidthParser reader, Table table, int[] columnIndexes) {
        String[] nextLine;

        Map<String, AbstractParser<?>> parserMap = getParserMap(options, table);

        // Add the rows
        for (long rowNumber = options.header() ? 1L : 0L; (nextLine = reader.parseNext()) != null; rowNumber++) {
            // validation
            if (nextLine.length < types.length) {
                if (nextLine.length == 1 && Strings.isNullOrEmpty(nextLine[0])) {
                    System.err.println("Warning: Invalid Fixed Width file. Row "
                            + rowNumber
                            + " is empty. Continuing.");
                    continue;
                } else {
                    Exception e = new IndexOutOfBoundsException("Row number " + rowNumber + " contains " + nextLine.length + " columns. "
                            + types.length + " expected.");
                    throw new AddCellToColumnException(e, 0, rowNumber, table.columnNames(), nextLine);
                }
            } else if (nextLine.length > types.length) {
                throw new IllegalArgumentException("Row number " + rowNumber + " contains " + nextLine.length + " columns. "
                        + types.length + " expected.");
            }

            // append each column that we're including (not skipping)
            int cellIndex = 0;
            for (int columnIndex : columnIndexes) {
                Column<?> column = table.column(cellIndex);
                AbstractParser<?> parser = parserMap.get(column.name());
                try {
                    String value = nextLine[columnIndex];
                    column.appendCell(value, parser);
                } catch (Exception e) {
                    throw new AddCellToColumnException(e, columnIndex, rowNumber, table.columnNames(), nextLine);
                }
                cellIndex++;
            }
        }
    }

    private Map<String, AbstractParser<?>> getParserMap(FixedWidthReadOptions options, Table table) {
        Map<String, AbstractParser<?>> parserMap = new HashMap<>();
        for (Column<?> column : table.columns()) {
            AbstractParser<?> parser = column.type().customParser(options);
            parserMap.put(column.name(), parser);
        }
        return parserMap;
    }

    private String cleanName(String name) {
        return name.trim();
    }

    /**
     * Returns a string representation of the column types in file {@code fixed widthFilename},
     * as determined by the type-detection algorithm
     * <p>
     * This method is intended to help analysts quickly fix any erroneous types, by printing out the types in a format
     * such that they can be edited to correct any mistakes, and used in an array literal
     * <p>
     * For example:
     * <p>
     * LOCAL_DATE, // 0     date
     * SHORT,      // 1     approval
     * STRING,     // 2     who
     * <p>
     * Note that the types are array separated, and that the index position and the column name are printed such that
     * they would be interpreted as comments if you paste the output into an array:
     * <p>
     *
     * @throws IOException if file cannot be read
     */
        public String printColumnTypes(FixedWidthReadOptions options) throws IOException {

        Table structure = read(options, true).structure();

        StringBuilder buf = new StringBuilder();
        buf.append("ColumnType[] columnTypes = {");
        buf.append(System.lineSeparator());

        Column<?> typeCol = structure.column("Column Type");
        Column<?> indxCol = structure.column("Index");
        Column<?> nameCol = structure.column("Column Name");

        // add the column headers
        int typeColIndex = structure.columnIndex(typeCol);
        int indxColIndex = structure.columnIndex(indxCol);
        int nameColIndex = structure.columnIndex(nameCol);

        int typeColWidth = typeCol.columnWidth();
        int indxColWidth = indxCol.columnWidth();
        int nameColWidth = nameCol.columnWidth();

        final char padChar = ' ';
        for (int r = 0; r < structure.rowCount(); r++) {
            String cell = Strings.padEnd(structure.get(r, typeColIndex) + ",", typeColWidth, padChar);
            buf.append(cell);
            buf.append(" // ");

            cell = Strings.padEnd(structure.getUnformatted(r, indxColIndex), indxColWidth, padChar);
            buf.append(cell);
            buf.append(' ');

            cell = Strings.padEnd(structure.getUnformatted(r, nameColIndex), nameColWidth, padChar);
            buf.append(cell);
            buf.append(' ');

            buf.append(System.lineSeparator());
        }
        buf.append("}");
        buf.append(System.lineSeparator());
        return buf.toString();
    }

    /**
     * Reads column names from header, skipping any for which the type == SKIP
     */
    private String[] selectColumnNames(List<String> names, ColumnType[] types) {
        List<String> header = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            if (types[i] != SKIP) {
                String name = names.get(i);
                name = name.trim();
                header.add(name);
            }
        }
        String[] result = new String[header.size()];
        return header.toArray(result);
    }

    /**
     * Provides placeholder column names for when the file read has no header
     */
    private String[] makeColumnNames(ColumnType[] types) {
        String[] header = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            header[i] = "C" + i;
        }
        return header;
    }

    /**
     * Estimates and returns the type for each column in the delimited text file {@code file}
     * <p>
     * The type is determined by checking a sample of the data in the file. Because only a sample of the data is
     * checked,
     * the types may be incorrect. If that is the case a Parse Exception will be thrown.
     * <p>
     * The method {@code printColumnTypes()} can be used to print a list of the detected columns that can be
     * corrected and
     * used to explicitly specify the correct column types.
     */
    public ColumnType[] detectColumnTypes(Reader reader, FixedWidthReadOptions options) throws IOException {

        boolean header = options.header();
        int linesToSkip = header ? 1 : 0;

        FixedWidthParser fixedWidthParser = fixedWidthParser(options);

        try {
            fixedWidthParser.beginParsing(reader);

            for (int i = 0; i < linesToSkip; i++) {
                fixedWidthParser.parseNext();
            }

            ColumnTypeDetector detector = typeArrayOverrides == null
                    ? new ColumnTypeDetector() : new ColumnTypeDetector(typeArrayOverrides);
            return detector.detectColumnTypes(new Iterator<String[]>() {

                String[] nextRow = fixedWidthParser.parseNext();

                @Override
                public boolean hasNext() {
                    return nextRow != null;
                }

                @Override
                public String[] next() {
                    String[] tmp = nextRow;
                    nextRow = fixedWidthParser.parseNext();
                    return tmp;
                }

            }, options);
        } finally {
            fixedWidthParser.stopParsing();
            // we don't close the reader since we didn't create it
        }
    }

    private FixedWidthParser fixedWidthParser(FixedWidthReadOptions options) {
        FixedWidthParserSettings settings = new FixedWidthParserSettings();

        if (options.columnSpecs() != null) {
            settings = new FixedWidthParserSettings(options.columnSpecs());
        }
        settings.setFormat(fixedWidthFormat(options));
        if (options.skipTrailingCharsUntilNewline()) {
            settings.setSkipTrailingCharsUntilNewline(options.skipTrailingCharsUntilNewline());
        }
        if (options.maxNumberOfColumns() != null) {
            settings.setMaxColumns(options.maxNumberOfColumns());
        }
        if (options.recordEndsOnNewline()) {
            settings.setRecordEndsOnNewline(true);
        }
        return new FixedWidthParser(settings);
    }

    private FixedWidthFormat fixedWidthFormat(FixedWidthReadOptions options) {
        FixedWidthFormat format = new FixedWidthFormat();
        if (options.padding() != ' ') {
            format.setPadding(options.padding());
        }
        if (options.lookupWildcard() != '?') {
            format.setLookupWildcard(options.lookupWildcard());
        }
        if (options.lineEnding() != null) {
            format.setLineSeparator(options.lineEnding());
        }
        return format;
    }

}
