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

import com.google.common.io.CharStreams;
import com.univocity.parsers.common.AbstractParser;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.FileReader;
import tech.tablesaw.io.TableBuildingUtils;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.util.Pair;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

@Immutable
public class CsvReader extends FileReader {

    /**
     * Constructs a CsvReader
     */
    public CsvReader() {
        super();
    }

    /**
     * Constructs a CsvReader with the given list of ColumnTypes
     * <p>
     * These are the only types that the CsvReader can detect and parse
     */
    public CsvReader(List<ColumnType> typeDetectionList) {
        super(typeDetectionList);
    }

    /**
     * Determines column types if not provided by the user
     * Reads all input into memory unless File was provided
     */
    private Pair<Reader, ColumnType[]> getReaderAndColumnTypes(CsvReadOptions options) throws IOException {
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

        return Pair.create(TableBuildingUtils.createReader(options, bytesCache), types);
    }

    public Table read(CsvReadOptions options) throws IOException {
        return read(options, false);
    }

    private Table read(CsvReadOptions options, boolean headerOnly) throws IOException {
        Pair<Reader, ColumnType[]> pair = getReaderAndColumnTypes(options);
        Reader reader = pair.getKey();
        ColumnType[] types = pair.getValue();

        AbstractParser<?> parser = csvParser(options);

        try {
            return parseRows(options, headerOnly, reader, types, parser);
        } finally {
            if (options.reader() == null) {
                // if we get a reader back from options it means the client opened it, so let the client close it
                // if it's null, we close it here.
                parser.stopParsing();
                reader.close();
            }
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
    public String printColumnTypes(CsvReadOptions options) throws IOException {

        Table structure = read(options, true).structure();
        return getTypeString(structure);
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

        CsvParser parser = csvParser(options);

        try {
            return getTypes(reader, options, linesToSkip, parser);
        } finally {
            parser.stopParsing();
            // we don't close the reader since we didn't create it
        }
    }

    private CsvParser csvParser(CsvReadOptions options) {
        CsvParserSettings settings = new CsvParserSettings();
        settings.setLineSeparatorDetectionEnabled(options.lineSeparatorDetectionEnabled());
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
        if(options.commentPrefix() != null) {
            format.setComment(options.commentPrefix());
        }
        return format;
    }

}
