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

package tech.tablesaw.io.csv;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.AbstractParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.ColumnTypeDetector;
import tech.tablesaw.io.ReadOptions;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static tech.tablesaw.api.ColumnType.*;

@Immutable
public class CsvReader {

    private List<ColumnType> typeArrayOverrides = null;

    /**
     * Constructs a CsvReader
     */
    public CsvReader() {}

    /**
     * Constructs a CsvReader with the given list of ColumnTypes
     *
     * These are the only types that the CsvReader can detect and parse
     */
    public CsvReader(List<ColumnType> typeDetectionList) {
        this.typeArrayOverrides = typeDetectionList;
    }

    /**
     * Determines column types if not provided by the user
     * Reads all input into memory unless File was provided
     */
    private Pair<Reader, ColumnType[]> getReaderAndColumnTypes(CsvReadOptions options) throws IOException {
        ColumnType[] types = options.columnTypes();
        byte[] bytesCache = null;

        if (types == null) {
            Reader reader = createReader(options, bytesCache);
            if (options.file() == null) {
        	bytesCache = CharStreams.toString(reader).getBytes();
        	// create a new reader since we just exhausted the existing one
        	reader = createReader(options, bytesCache);
            }
            types = detectColumnTypes(reader, options);
        }

        return Pair.of(createReader(options, bytesCache), types);
    }

    private Reader createReader(ReadOptions options, byte[] cachedBytes) throws IOException {
	if (cachedBytes != null) {
	    return new InputStreamReader(new ByteArrayInputStream(cachedBytes));
	}
        if (options.inputStream() != null) {
            return new InputStreamReader(options.inputStream());
        }
        if (options.reader() != null) {
            return options.reader();
        }
        return new FileReader(options.file());
    }

    public Table read(CsvReadOptions options) throws IOException {
	Pair<Reader, ColumnType[]> pair = getReaderAndColumnTypes(options);
	Reader reader = pair.getLeft();
	ColumnType[] types = pair.getRight();

        CsvParser parser = csvParser(options);

        try {
            parser.beginParsing(reader);
            Table table = Table.create(options.tableName());

            String[] headerNames = getHeaderNames(options, types, parser);
            if (headerNames == null) {
                return table;
            }
            List<String> headerRow = Lists.newArrayList(headerNames);

            String[] columnNames = selectColumnNames(headerRow, types);

            cleanNames(headerRow);
            for (int x = 0; x < types.length; x++) {
                if (types[x] != SKIP) {
                    String columnName = headerRow.get(x);
                    if (Strings.isNullOrEmpty(columnName)) {
                        columnName = "Column " + table.columnCount();
                    }
                    Column<?> newColumn = types[x].create(columnName);
                    table.addColumns(newColumn);
                }
            }
            int[] columnIndexes = new int[columnNames.length];
            for (int i = 0; i < columnIndexes.length; i++) {
                // get the index in the original table, which includes skipped fields
                columnIndexes[i] = headerRow.indexOf(columnNames[i]);
            }

            addRows(options, types, parser, table, columnIndexes);
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

    private String[] getHeaderNames(CsvReadOptions options, ColumnType[] types, CsvParser parser) {
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

    private void addRows(CsvReadOptions options, ColumnType[] types, CsvParser reader, Table table, int[] columnIndexes) {
        String[] nextLine;

        Map<String, AbstractParser<?>> parserMap = getParserMap(options, table);

        // Add the rows
        for (long rowNumber = options.header() ? 1L : 0L; (nextLine = reader.parseNext()) != null; rowNumber++) {
            // validation
            if (nextLine.length < types.length) {
                if (nextLine.length == 1 && Strings.isNullOrEmpty(nextLine[0])) {
                    System.err.println("Warning: Invalid CSV file. Row "
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

    private Map<String, AbstractParser<?>> getParserMap(CsvReadOptions options, Table table) {
        Map<String, AbstractParser<?>> parserMap = new HashMap<>();
        for (Column<?> column : table.columns()) {
            AbstractParser<?> parser = column.type().customParser(options);
            parserMap.put(column.name(), parser);
        }
        return parserMap;
    }

    private void cleanNames(List<String> headerRow) {
        for (int i = 0; i < headerRow.size(); i++) {
            headerRow.set(i, headerRow.get(i).trim());
        }
    }

    /**
     * Returns a Table constructed from a CSV File with the given file name
     * <p>
     * The @code{fileName} is used as the initial table name for the new table
     *
     * @param types           An array of the types of columns in the file, in the order they appear
     * @param header          Is the first row in the file a header?
     * @param options         Sets the format for and instructions for parsing
     * @param file            The fully specified file name. It is used to provide a default name for the table
     * @return                A Relation containing the data in the csv file.
     * @throws IOException    if file cannot be read
     */
    private Table headerOnly(ColumnType[] types, boolean header, CsvReadOptions options, File file)
            throws IOException {

        FileInputStream fis = new FileInputStream(file);

        Reader reader = new InputStreamReader(fis);

        Table table;

        CsvParser csvParser = csvParser(options);
        try (BufferedReader streamReader = new BufferedReader(reader)) {
            csvParser.beginParsing(streamReader);

            String[] columnNames;
            List<String> headerRow;

            String[] headerNames = getHeaderNames(options, types, csvParser);

            headerRow = Lists.newArrayList(headerNames);
            columnNames = selectColumnNames(headerRow, types);

            table = Table.create(file.getName());
            for (int x = 0; x < types.length; x++) {
                if (types[x] != SKIP) {
                    Column<?> newColumn = types[x].create(headerRow.get(x).trim());
                    table.addColumns(newColumn);
                }
            }
            int[] columnIndexes = new int[columnNames.length];
            for (int i = 0; i < columnIndexes.length; i++) {
                // get the index in the original table, which includes skipped fields
                columnIndexes[i] = headerRow.indexOf(columnNames[i]);
            }
        } finally {
            // the stream is already closed
            csvParser.stopParsing();
        }
        return table;
    }

    /**
     * Returns the structure of the table given by {@code csvFileName} as detected by analysis of a sample of the data
     *
     * @throws IOException if file cannot be read
     */
    private Table detectedColumnTypes(String csvFileName, boolean header, char delimiter, Locale locale) throws IOException {
        File file = new File(csvFileName);
        try (Reader reader = new FileReader(file)) {

            CsvReadOptions options = CsvReadOptions.builder(reader, "")
                    .separator(delimiter)
                    .header(header)
                    .locale(locale)
                    .sample(true)
                    .build();
            ColumnType[] types = detectColumnTypes(reader, options);
            Table t = headerOnly(types, header, options, file);
            return t.structure();
        }
    }

    /**
     * Returns a string representation of the column types in file {@code csvFilename},
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
    public String printColumnTypes(String csvFileName, boolean header, char delimiter, Locale locale) throws IOException {

        Table structure = detectedColumnTypes(csvFileName, header, delimiter, locale);

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
    protected ColumnType[] detectColumnTypes(Reader reader, CsvReadOptions options) {

        boolean header = options.header();
        int linesToSkip = header ? 1 : 0;

        CsvParser csvParser = csvParser(options);

        try {
            csvParser.beginParsing(reader);

            for (int i = 0; i < linesToSkip; i++) {
                csvParser.parseNext();
            }

            ColumnTypeDetector detector = typeArrayOverrides == null
        	    ? new ColumnTypeDetector() : new ColumnTypeDetector(typeArrayOverrides);
            return detector.detectColumnTypes(new Iterator<String[]>() {

        	String[] nextRow = csvParser.parseNext();

		@Override
		public boolean hasNext() {
		    return nextRow != null;
		}

		@Override
		public String[] next() {
		    String[] tmp = nextRow;
		    nextRow = csvParser.parseNext();
		    return tmp;
		}
        	
            }, options);
        } finally {
            csvParser.stopParsing();
            // we don't close the reader since we didn't create it
        }
    }

    private CsvParser csvParser(CsvReadOptions options) {
        CsvParserSettings settings = new CsvParserSettings();
        settings.setFormat(csvFormat(options));
        if (options.maxNumberOfColumns() != null) {
            settings.setMaxColumns(options.maxNumberOfColumns());
        }
        return new CsvParser(settings);
    }

    private CsvFormat csvFormat(CsvReadOptions options) {
        CsvFormat format = new CsvFormat();
        if (options.separator() != null) {
            format.setDelimiter(options.separator());
        }
        if (options.lineEnding() != null) {
            format.setLineSeparator(options.lineEnding());
        }
        return format;
    }

}
