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
import java.sql.ResultSet;
import java.sql.SQLException;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.csv.CsvReadOptions.CsvReadOptionsBuilder;
import tech.tablesaw.io.csv.CsvReader;
import tech.tablesaw.io.html.HtmlTableReader;
import tech.tablesaw.io.jdbc.SqlResultSetReader;

public class DataFrameReader {

  public Table csv(String file) throws IOException {
    return csv(CsvReadOptions.builder(file));
  }

  public Table csv(String contents, String tableName) {
    try {
      return csv(new StringReader(contents), tableName);
    }  catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public Table csv(File file) throws IOException {
    return csv(CsvReadOptions.builder(file));
  }

  public Table csv(InputStream stream, String tableName) throws IOException {
    return csv(CsvReadOptions.builder(stream, tableName));
  }

  public Table csv(Reader reader, String tableName) throws IOException {
    return csv(CsvReadOptions.builder(reader, tableName));
  }

  public Table csv(CsvReadOptionsBuilder options) throws IOException {
    return csv(options.build());
  }

  public Table csv(CsvReadOptions options) throws IOException {
    return CsvReader.read(options);
  }

  public Table db(ResultSet resultSet, String tableName) throws SQLException {
    return SqlResultSetReader.read(resultSet, tableName);
  }

  public Table html(String url) throws IOException {
    return csv(new HtmlTableReader().tableToCsv(url), url);
  }

}
