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

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.csv.CsvReader;
import tech.tablesaw.io.jdbc.SqlResultSetReader;

public class DataFrameReader {

  private final ReaderRegistry registry;

  public DataFrameReader(ReaderRegistry registry) {
    this.registry = registry;
  }

  /**
   * Reads the given URL into a table using default options Uses appropriate converter based on
   * mime-type Use {@link #usingOptions(ReadOptions) usingOptions} to use non-default options
   */
  public Table url(String url) throws IOException {
    return url(new URL(url));
  }

  /**
   * Reads the given URL into a table using default options Uses appropriate converter based on
   * mime-type Use {@link #usingOptions(ReadOptions) usingOptions} to use non-default options
   */
  public Table url(URL url) throws IOException {
    URLConnection connection = url.openConnection();
    String contentType = connection.getContentType();
    return url(url, getCharset(contentType), getMimeType(contentType));
  }

  private Table url(URL url, Charset charset, String mimeType) throws IOException {
    Optional<DataReader<?>> reader = registry.getReaderForMimeType(mimeType);
    if (reader.isPresent()) {
      return readUrl(url, charset, reader.get());
    }
    reader = registry.getReaderForExtension(getExtension(url));
    if (reader.isPresent()) {
      return readUrl(url, charset, reader.get());
    }
    throw new IllegalArgumentException("No reader registered for mime-type " + mimeType);
  }

  private Table readUrl(URL url, Charset charset, DataReader<?> reader) throws IOException {
    return reader.read(new Source(url.openConnection().getInputStream(), charset));
  }

  private String getMimeType(String contentType) {
    String[] pair = contentType.split(";");
    return pair[0].trim();
  }

  private Charset getCharset(String contentType) {
    String[] pair = contentType.split(";");
    return pair.length == 1
        ? Charset.defaultCharset()
        : Charset.forName(pair[1].split("=")[1].trim());
  }

  /**
   * Best effort method to get the extension from a URL.
   *
   * @param url the url to pull the extension from.
   * @return the extension.
   */
  private String getExtension(URL url) {
    return Files.getFileExtension(url.getPath());
  }

  /**
   * Reads the given string contents into a table using default options Uses converter specified
   * based on given file extension Use {@link #usingOptions(ReadOptions) usingOptions} to use
   * non-default options
   */
  public Table string(String s, String fileExtension) {
    Optional<DataReader<?>> reader = registry.getReaderForExtension(fileExtension);
    if (!reader.isPresent()) {
      throw new IllegalArgumentException("No reader registered for extension " + fileExtension);
    }

    try {
      return reader.get().read(Source.fromString(s));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Reads the given file into a table using default options Uses converter specified based on given
   * file extension Use {@link #usingOptions(ReadOptions) usingOptions} to use non-default options
   */
  public Table file(String file) throws IOException {
    return file(new File(file));
  }

  /**
   * Reads the given file into a table using default options Uses converter specified based on given
   * file extension Use {@link #usingOptions(ReadOptions) usingOptions} to use non-default options
   */
  public Table file(File file) throws IOException {
    String extension = Files.getFileExtension(file.getCanonicalPath());
    Optional<DataReader<?>> reader = registry.getReaderForExtension(extension);
    if (reader.isPresent()) {
      return reader.get().read(new Source(file));
    }
    throw new IllegalArgumentException("No reader registered for extension " + extension);
  }

  public <T extends ReadOptions> Table usingOptions(T options) throws IOException {
    DataReader<T> reader = registry.getReaderForOptions(options);
    return reader.read(options);
  }

  public Table usingOptions(ReadOptions.Builder builder) throws IOException {
    return usingOptions(builder.build());
  }

  public Table db(ResultSet resultSet) throws SQLException {
    return SqlResultSetReader.read(resultSet);
  }

  public Table db(ResultSet resultSet, String tableName) throws SQLException {
    Table table = SqlResultSetReader.read(resultSet);
    table.setName(tableName);
    return table;
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

  public Table csv(URL url) throws IOException {
    return readUrl(url, getCharset(url.openConnection().getContentType()), new CsvReader());
  }

  public Table csv(InputStream stream, String name) throws IOException {
    return csv(CsvReadOptions.builder(stream).tableName(name));
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
}
