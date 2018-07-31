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
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.commons.lang3.StringUtils;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.TypeUtils;
import tech.tablesaw.io.UnicodeBOMInputStream;

import javax.annotation.concurrent.Immutable;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import static tech.tablesaw.api.ColumnType.*;

@Immutable
public class CsvReader {

    /**
     * Private constructor to prevent instantiation
     */
    private CsvReader() {}

    public static Table read(CsvReadOptions options) throws IOException {

        byte[] bytes = options.reader() != null
                ? CharStreams.toString(options.reader()).getBytes() : null;

        ColumnType[] types;
        if (options.columnTypes() != null) {
            types = options.columnTypes();
        } else {
            InputStream detectTypesStream = options.reader() != null
                    ? new ByteArrayInputStream(bytes)
                    : new FileInputStream(options.file());
            types = detectColumnTypes(detectTypesStream, options);
        }

        // All other read methods end up here, make sure we don't have leading Unicode BOM
        InputStream stream = options.reader() != null
                ? new ByteArrayInputStream(bytes)
                : new FileInputStream(options.file());

        UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(stream);
        ubis.skipBOM();

        CSVParser csvParser = new CSVParserBuilder()
                .withSeparator(options.separator())
                .build();

        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(ubis)).withCSVParser(csvParser).build()) {
            Table table = Table.create(options.tableName());

            String[] headerNames;
            if (options.header()) {
                headerNames = reader.readNext();
                if (headerNames == null) {
                    return table;
                }
            } else {
                headerNames = makeColumnNames(types);
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
                    Column newColumn = TypeUtils.newColumn(columnName, types[x]);
                    table.addColumns(newColumn);
                }
            }
            int[] columnIndexes = new int[columnNames.length];
            for (int i = 0; i < columnIndexes.length; i++) {
                // get the index in the original table, which includes skipped fields
                columnIndexes[i] = headerRow.indexOf(columnNames[i]);
            }

            long rowNumber = options.header() ? 1L : 0L;
            String[] nextLine;

            // Add the rows
            while ((nextLine = reader.readNext()) != null) {

                if (nextLine.length < types.length) {
                    if (nextLine.length == 1 && Strings.isNullOrEmpty(nextLine[0])) {
                        System.err.println("Warning: Invalid CSV file. Row "
                                + rowNumber
                                + " is empty. Continuing.");
                    } else {
                        Exception e = new IndexOutOfBoundsException("Row number " + rowNumber + " is too short.");
                        throw new AddCellToColumnException(e, 0, rowNumber, columnNames, nextLine);
                    }
                } else if (nextLine.length > types.length) {
                    throw new RuntimeException("Row number " + rowNumber + " is too long.");
                } else {
                    // for each column that we're including (not skipping)
                    int cellIndex = 0;
                    for (int columnIndex : columnIndexes) {
                        Column column = table.column(cellIndex);
                        StringParser parser = column.type().customParser(options);
                        try {
                            String value = nextLine[columnIndex];
                            column.appendCell(value, parser);
                        } catch (Exception e) {
                            throw new AddCellToColumnException(e, columnIndex, rowNumber, columnNames, nextLine);
                        }
                        cellIndex++;
                    }
                }
                rowNumber++;
            }
            return table;
        }
    }

    private static void cleanNames(List<String> headerRow) {
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
     * @param columnSeparator the delimiter
     * @param file            The fully specified file name. It is used to provide a default name for the table
     * @return A Relation containing the data in the csv file.
     * @throws IOException if file cannot be read
     */
    public static Table headerOnly(ColumnType types[], boolean header, char columnSeparator, File file)
            throws IOException {

        FileInputStream fis = new FileInputStream(file);
        // make sure we don't have leading Unicode BOM
        UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(fis);
        ubis.skipBOM();

        Reader reader = new InputStreamReader(ubis);
        BufferedReader streamReader = new BufferedReader(reader);

        Table table;
        CSVParser csvParser = new CSVParserBuilder()
                .withSeparator(columnSeparator)
                .build();
        try (CSVReader csvReader = new CSVReaderBuilder(streamReader).withCSVParser(csvParser).build()) {

            String[] columnNames;
            List<String> headerRow;

            String[] headerNames =
                    header ? csvReader.readNext() : makeColumnNames(types);

            headerRow = Lists.newArrayList(headerNames);
            columnNames = selectColumnNames(headerRow, types);

            table = Table.create(file.getName());
            for (int x = 0; x < types.length; x++) {
                if (types[x] != SKIP) {
                    Column newColumn = TypeUtils.newColumn(headerRow.get(x).trim(), types[x]);
                    table.addColumns(newColumn);
                }
            }
            int[] columnIndexes = new int[columnNames.length];
            for (int i = 0; i < columnIndexes.length; i++) {
                // get the index in the original table, which includes skipped fields
                columnIndexes[i] = headerRow.indexOf(columnNames[i]);
            }
        }
        return table;
    }

    /**
     * Returns the structure of the table given by {@code csvFileName} as detected by analysis of a sample of the data
     *
     * @throws IOException if file cannot be read
     */
    private static Table detectedColumnTypes(String csvFileName, boolean header, char delimiter, Locale locale) throws IOException {
        File file = new File(csvFileName);
        InputStream stream = new FileInputStream(file);

        CsvReadOptions options = CsvReadOptions.builder(stream, "")
                .separator(delimiter)
                .header(header)
                .locale(locale)
                .sample(false)
                .build();
        ColumnType[] types = detectColumnTypes(stream, options);
        Table t = headerOnly(types, header, delimiter, file);
        return t.structure();
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
     * SHORT_INT,  // 1     approval
     * STRING,   // 2     who
     * <p>
     * Note that the types are array separated, and that the index position and the column name are printed such that
     * they would be interpreted as comments if you paste the output into an array:
     * <p>
     * ColumnType[] types = {
     * LOCAL_DATE, // 0     date
     * SHORT_INT,  // 1     approval
     * STRING,   // 2     who
     * }
     *
     * @throws IOException if file cannot be read
     */
    public static String printColumnTypes(String csvFileName, boolean header, char delimiter, Locale locale) throws IOException {

        Table structure = detectedColumnTypes(csvFileName, header, delimiter, locale);

        StringBuilder buf = new StringBuilder();
        buf.append("ColumnType[] columnTypes = {");
        buf.append('\n');

        Column typeCol = structure.column("Column Type");
        Column indxCol = structure.column("Index");
        Column nameCol = structure.column("Column Name");

        // add the column headers
        int typeColIndex = structure.columnIndex(typeCol);
        int indxColIndex = structure.columnIndex(indxCol);
        int nameColIndex = structure.columnIndex(nameCol);

        int typeColWidth = typeCol.columnWidth();
        int indxColWidth = indxCol.columnWidth();
        int nameColWidth = nameCol.columnWidth();

        for (int r = 0; r < structure.rowCount(); r++) {
            String cell = StringUtils.rightPad(structure.get(r, typeColIndex) + ",", typeColWidth);
            buf.append(cell);
            buf.append(" // ");

            cell = StringUtils.rightPad(structure.get(r, indxColIndex), indxColWidth);
            buf.append(cell);
            buf.append(' ');

            cell = StringUtils.rightPad(structure.get(r, nameColIndex), nameColWidth);
            buf.append(cell);
            buf.append(' ');

            buf.append('\n');
        }
        buf.append("}");
        buf.append('\n');
        return buf.toString();
    }

    /**
     * Reads column names from header, skipping any for which the type == SKIP
     */
    private static String[] selectColumnNames(List<String> names, ColumnType types[]) {
        List<String> header = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            if (types[i] != SKIP) {
                header.add(names.get(i).trim());
            }
        }
        String[] result = new String[header.size()];
        return header.toArray(result);
    }

    /**
     * Provides placeholder column names for when the file read has no header
     */
    private static String[] makeColumnNames(ColumnType types[]) {
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
    public static ColumnType[] detectColumnTypes(InputStream stream, CsvReadOptions options)
            throws IOException {

        boolean header = options.header();
        char delimiter = options.separator();
        boolean useSampling = options.sample();

        int linesToSkip = header ? 1 : 0;

        // to hold the results
        List<ColumnType> columnTypes = new ArrayList<>();

        // to hold the data read from the file
        List<List<String>> columnData = new ArrayList<>();

        int rowCount = 0; // make sure we don't go over maxRows

        // make sure we don't have leading Unicode BOM
        UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(stream);
        ubis.skipBOM();

        CSVParser csvParser = new CSVParserBuilder()
                .withSeparator(delimiter)
                .build();
        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(ubis))
                .withCSVParser(csvParser)
                .withSkipLines(linesToSkip)
                .build()) {
            String[] nextLine;
            int nextRow = 0;
            while ((nextLine = reader.readNext()) != null) {
                // initialize the arrays to hold the strings. we don't know how many we need until we read the first row
                if (rowCount == 0) {
                    for (String aNextLine : nextLine) {
                        columnData.add(new ArrayList<>());
                    }
                }
                int columnNumber = 0;
                if (rowCount == nextRow) {
                    for (String field : nextLine) {
                        columnData.get(columnNumber).add(field);
                        columnNumber++;
                    }
                }
                if (rowCount == nextRow) {
                    if (useSampling) {
                        nextRow = nextRow(nextRow);
                    } else {
                        nextRow = nextRowWithoutSampling(nextRow);
                    }
                }
                rowCount++;
            }
        }

        // now detect
        for (List<String> valuesList : columnData) {
            ColumnType detectedType = detectType(valuesList, options);
            columnTypes.add(detectedType);
        }
        return columnTypes.toArray(new ColumnType[0]);
    }

    private static int nextRowWithoutSampling(int nextRow) {
        return nextRow + 1;
    }

    private static int nextRow(int nextRow) {
        if (nextRow < 100) {
            return nextRow + 1;
        }
        if (nextRow < 1000) {
            return nextRow + 10;
        }
        if (nextRow < 10_000) {
            return nextRow + 100;
        }
        if (nextRow < 100_000) {
            return nextRow + 1000;
        }
        if (nextRow < 1_000_000) {
            return nextRow + 10_000;
        }
        if (nextRow < 10_000_000) {
            return nextRow + 100_000;
        }
        if (nextRow < 100_000_000) {
            return nextRow + 1_000_000;
        }
        return nextRow + 10_000_000;
    }

    /**
     * Returns a predicted ColumnType derived by analyzing the given list of undifferentiated strings read from a
     * column in the file and applying the given Locale and options
     */
    private static ColumnType detectType(List<String> valuesList, CsvReadOptions options) {

        // Types to choose from. When more than one would work, we pick the first of the options
        ColumnType[] typeArray
                = // we leave out string, as that is the default type
                {LOCAL_DATE_TIME, LOCAL_TIME, LOCAL_DATE, BOOLEAN, NUMBER};

        List<StringParser> parsers = getParserList(typeArray, options);

        CopyOnWriteArrayList<ColumnType> typeCandidates = new CopyOnWriteArrayList<>(typeArray);

        for (String s : valuesList) {
            for (StringParser parser : parsers) {
                if (!parser.canParse(s)) {
                    typeCandidates.remove(parser.columnType());
                }
            }
        }
        return selectType(typeCandidates);
    }

    /**
     * Returns the selected candidate for a column of data, by picking the first value in the given list
     *
     * @param typeCandidates a possibly empty list of candidates. This list should be sorted in order of preference
     */
    private static ColumnType selectType(List<ColumnType> typeCandidates) {
        if (typeCandidates.isEmpty()) {
            return STRING;
        } else {
            return typeCandidates.get(0);
        }
    }

    /**
     * Returns the list of parsers to use for type detection
     *
     * @param typeArray Array of column types. The order specifies the order the types are applied
     * @param options CsvReadOptions to use to modify the default parsers for each type
     * @return  A list of parsers in the order they should be used for type detection
     */
    private static List<StringParser> getParserList(ColumnType[] typeArray, CsvReadOptions options) {
        // Types to choose from. When more than one would work, we pick the first of the options

        List<StringParser> parsers = new ArrayList<>();
        for (ColumnType type : typeArray) {
            parsers.add(type.customParser(options));
        }
        return parsers;
    }
}
