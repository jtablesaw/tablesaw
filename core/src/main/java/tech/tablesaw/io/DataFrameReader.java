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

package tech.tablesaw.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.csv.CsvReader;
import tech.tablesaw.io.fixed.FixedWidthReadOptions;
import tech.tablesaw.io.fixed.FixedWidthReader;
import tech.tablesaw.io.html.HtmlTableReader;
import tech.tablesaw.io.jdbc.SqlResultSetReader;
import tech.tablesaw.io.json.JsonReader;
import tech.tablesaw.io.xlsx.XlsxReadOptions;
import tech.tablesaw.io.xlsx.XlsxReader;

public class DataFrameReader {

    /**
     * Optional dependencies must be added to call this method: com.univocity:univocity-parsers
     */
    public Table csv(String file) throws IOException {
        return csv(CsvReadOptions.builder(file));
    }

    /**
     * Optional dependencies must be added to call this method: com.univocity:univocity-parsers
     */
    public Table csv(String contents, String tableName) {
        try {
            return csv(new StringReader(contents), tableName);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Optional dependencies must be added to call this method: com.univocity:univocity-parsers
     */
    public Table csv(File file) throws IOException {
        return csv(CsvReadOptions.builder(file));
    }

    /**
     * Optional dependencies must be added to call this method: com.univocity:univocity-parsers
     */
    public Table csv(InputStream stream, String tableName) throws IOException {
        return csv(CsvReadOptions.builder(stream, tableName));
    }

    /**
     * Optional dependencies must be added to call this method: com.univocity:univocity-parsers
     */
    public Table csv(Reader reader, String tableName) throws IOException {
        return csv(CsvReadOptions.builder(reader, tableName));
    }

    /**
     * Optional dependencies must be added to call this method: com.univocity:univocity-parsers
     */
    public Table csv(CsvReadOptions.Builder options) throws IOException {
        return csv(options.build());
    }

    /**
     * Optional dependencies must be added to call this method: com.univocity:univocity-parsers
     */
    public Table csv(CsvReadOptions options) throws IOException {
        return new CsvReader().read(options);
    }

    /**
     * Optional dependencies must be added to call this method:
     * com.fasterxml.jackson.core:jackson-databind
     * com.github.wnameless:json-flattener
     */
    public Table json(String url) throws MalformedURLException, IOException {
        try (Scanner scanner = new Scanner(new URL(url).openStream(), StandardCharsets.UTF_8.toString())) {
            scanner.useDelimiter("\\A");
            return json(new StringReader(scanner.hasNext() ? scanner.next() : ""), url);
	}
    }

    /**
     * Optional dependencies must be added to call this method:
     * com.fasterxml.jackson.core:jackson-databind
     * com.github.wnameless:json-flattener
     */
    public Table json(Reader contents, String tableName) throws IOException {
	return new JsonReader().read(ReadOptions.builder(contents, tableName).build());
    }

    /**
     * Optional dependencies must be added to call this method: com.univocity:univocity-parsers
     */
    public Table fixedWidth(String file) throws IOException {
        return fixedWidth(FixedWidthReadOptions.builder(file));
    }

    /**
     * Optional dependencies must be added to call this method: com.univocity:univocity-parsers
     */
    public Table fixedWidth(String contents, String tableName) {
        try {
            return fixedWidth(new StringReader(contents), tableName);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Optional dependencies must be added to call this method: com.univocity:univocity-parsers
     */
    public Table fixedWidth(File file) throws IOException {
        return fixedWidth(FixedWidthReadOptions.builder(file));
    }

    /**
     * Optional dependencies must be added to call this method: com.univocity:univocity-parsers
     */
    public Table fixedWidth(InputStream stream, String tableName) throws IOException {
        return fixedWidth(FixedWidthReadOptions.builder(stream, tableName));
    }

    /**
     * Optional dependencies must be added to call this method: com.univocity:univocity-parsers
     */
    public Table fixedWidth(Reader reader, String tableName) throws IOException {
        return fixedWidth(FixedWidthReadOptions.builder(reader, tableName));
    }

    /**
     * Optional dependencies must be added to call this method: com.univocity:univocity-parsers
     */
    public Table fixedWidth(FixedWidthReadOptions.Builder options) throws IOException {
        return fixedWidth(options.build());
    }

    /**
     * Optional dependencies must be added to call this method: com.univocity:univocity-parsers
     */
    public Table fixedWidth(FixedWidthReadOptions options) throws IOException {
        return new FixedWidthReader().read(options);
    }

    public Table db(ResultSet resultSet, String tableName) throws SQLException {
        return SqlResultSetReader.read(resultSet, tableName);
    }

    /**
     * Optional dependencies must be added to call this method: org.jsoup:jsoup
     */
    public Table html(String url) throws IOException {
        return new HtmlTableReader().read(url);
    }

    /**
     * Optional dependencies must be added to call this method: org.apache.poi:poi-ooxml
     */
    public List<Table> xlsx(XlsxReadOptions options) throws IOException {
        return new XlsxReader().read(options);
    }

    /**
     * Optional dependencies must be added to call this method: org.apache.poi:poi-ooxml
     */
    public List<Table> xlsx(XlsxReadOptions.Builder options) throws IOException {
        return xlsx(options.build());
    }
}
