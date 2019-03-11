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
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.google.common.io.Files;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.csv.CsvReader;
import tech.tablesaw.io.fixed.FixedWidthReadOptions;
import tech.tablesaw.io.fixed.FixedWidthReader;
import tech.tablesaw.io.html.HtmlReadOptions;
import tech.tablesaw.io.html.HtmlTableReader;
import tech.tablesaw.io.jdbc.SqlResultSetReader;
import tech.tablesaw.io.json.JsonReadOptions;
import tech.tablesaw.io.json.JsonReader;
import tech.tablesaw.io.xlsx.XlsxReadOptions;
import tech.tablesaw.io.xlsx.XlsxReader;

public class DataFrameReader {

    private final ReaderRegistry registry;

    public DataFrameReader(ReaderRegistry registry) {
	this.registry = registry;
    }

    /**
     * Reads the given URL into a table using default options
     * Uses appropriate converter based on mime-type
     * Use {@link #withOptions(ReadOptions) withOptions} to use non-default options
     */
    public Table url(String url) throws IOException {
	return url(new URL(url));
    }

    /**
     * Reads the given URL into a table using default options
     * Uses appropriate converter based on mime-type
     * Use {@link #withOptions(ReadOptions) withOptions} to use non-default options
     */
    public Table url(URL url) throws IOException {
	URLConnection connection = url.openConnection();
	String mimeType = connection.getContentType();
	DataReader<?> reader = registry.getReaderForMimeType(mimeType);
	return reader.read(new Source(connection.getInputStream()));
    }

    /**
     * Reads the given string contents into a table using default options
     * Uses converter specified based on given file extension
     * Use {@link #withOptions(ReadOptions) withOptions} to use non-default options
     */
    public Table string(String s, String fileExtension) throws IOException {
	DataReader<?> reader = registry.getReaderForExtension(fileExtension);
	return reader.read(Source.fromString(s));
    }

    /**
     * Reads the given file into a table using default options
     * Uses converter specified based on given file extension
     * Use {@link #withOptions(ReadOptions) withOptions} to use non-default options
     */
    public Table file(String file) throws IOException {
        return file(new File(file));
    }

    /**
     * Reads the given file into a table using default options
     * Uses converter specified based on given file extension
     * Use {@link #withOptions(ReadOptions) withOptions} to use non-default options
     */
    public Table file(File file) throws IOException {
	String extension = Files.getFileExtension(file.getCanonicalPath());
	DataReader<?> reader = registry.getReaderForExtension(extension);
	return reader.read(new Source(file));
    }

    public Table usingOptions(ReadOptions options) throws IOException {
	DataReader<?> reader = registry.getReaderForOptions(options);
	return reader.read(options.source());
    }

    public Table usingOptions(ReadOptions.Builder builder) throws IOException {
	return usingOptions(builder.build());
    }


    // Legacy reader methods for backwards-compatibility
    
    public Table csv(String file) throws IOException {
        return csv(CsvReadOptions.builder(file));
    }

    public Table csv(String contents, String tableName) {
        try {
            return csv(CsvReadOptions.builder(new StringReader(contents)).tableName(tableName));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Table csv(File file) throws IOException {
        return csv(CsvReadOptions.builder(file));
    }

    public Table csv(InputStream stream) throws IOException {
        return csv(CsvReadOptions.builder(stream));
    }

    public Table csv(Reader reader) throws IOException {
        return csv(CsvReadOptions.builder(reader));
    }

    public Table csv(CsvReadOptions.Builder options) throws IOException {
        return csv(options.build());
    }

    public Table csv(CsvReadOptions options) throws IOException {
	return new CsvReader().read(options);
    }

    public Table fixedWidth(String file) throws IOException {
        return fixedWidth(FixedWidthReadOptions.builder(file));
    }

    public Table fixedWidth(File file) throws IOException {
        return fixedWidth(FixedWidthReadOptions.builder(file));
    }

    public Table fixedWidth(InputStream stream) throws IOException {
        return fixedWidth(FixedWidthReadOptions.builder(stream));
    }

    public Table fixedWidth(Reader reader) throws IOException {
        return fixedWidth(FixedWidthReadOptions.builder(reader));
    }

    public Table fixedWidth(FixedWidthReadOptions.Builder options) throws IOException {
        return fixedWidth(options.build());
    }

    public Table fixedWidth(FixedWidthReadOptions options) throws IOException {
        return new FixedWidthReader().read(options);
    }

    public Table db(ResultSet resultSet) throws SQLException {
        return SqlResultSetReader.read(resultSet);
    }

    public Table db(ResultSet resultSet, String tableName) throws SQLException {
        Table table = SqlResultSetReader.read(resultSet);
        table.setName(tableName);
        return table;
    }

    /**
     * Modules that call this method must add the optional dependency tech.tablesaw:tablesaw-json
     */
    public Table json(String url) throws MalformedURLException, IOException {
        return json(new StringReader(loadUrl(url)), url);
    }

    /**
     * Modules that call this method must add the optional dependency tech.tablesaw:tablesaw-json
     */
    public Table json(Reader contents, String tableName) throws IOException {
	return new JsonReader().read(JsonReadOptions.builder(contents, tableName).build());
    }

    /**
     * Modules that call this method must add the optional dependency tech.tablesaw:tablesaw-html
     */
    public Table html(String url) throws IOException {
        return html(HtmlReadOptions.builder(new StringReader(loadUrl(url)), url).build());
    }

    /**
     * Modules that call this method must add the optional dependency tech.tablesaw:tablesaw-html
     */
    public Table html(HtmlReadOptions options) throws IOException {
        return new HtmlTableReader().read(options);
    }

    /**
     * Modules that call this method must add the optional dependency tech.tablesaw:tablesaw-excel
     */
    public List<Table> xlsx(XlsxReadOptions options) throws IOException {
        return new XlsxReader().readMultiple(options);
    }

    /**
     * Modules that call this method must add the optional dependency tech.tablesaw:tablesaw-excel
     */
    public List<Table> xlsx(XlsxReadOptions.Builder options) throws IOException {
        return xlsx(options.build());
    }
    
    private String loadUrl(String url) throws IOException {
        try (Scanner scanner = new Scanner(new URL(url).openStream(), StandardCharsets.UTF_8.toString())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
	}
    }
}
