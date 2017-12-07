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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import tech.tablesaw.api.ColumnType;

@Value @Accessors(fluent = true)
@Builder(builderMethodName = "hiddenBuilder")
public class CsvReadOptions {

  private final File file;
  private final Reader reader;
  private final String tableName;
  private final ColumnType[] columnTypes;
  @Builder.Default private final boolean header = true;
  @Builder.Default private final char separator = ',';
  @Builder.Default private final boolean sample = true;

  /**
   * This method buffers the entire InputStream. Use the method taking a File for large input
   */
  public static CsvReadOptionsBuilder builder(InputStream stream, String tableName) {
    return builder(new InputStreamReader(stream), tableName);
  }

  /**
   * This method buffers the entire InputStream. Use the method taking a File for large input
   */
  public static CsvReadOptionsBuilder builder(Reader reader, String tableName) {
    return hiddenBuilder().reader(reader).tableName(tableName);
  }

  public static CsvReadOptionsBuilder builder(File file) throws FileNotFoundException {
    return hiddenBuilder().file(file).tableName(file.getName());
  }

  public static CsvReadOptionsBuilder builder(String file) throws FileNotFoundException {
    return builder(new File(file));
  }

}
