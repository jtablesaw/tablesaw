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
import java.io.OutputStream;
import java.io.Writer;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvWriter;
import tech.tablesaw.io.html.HtmlTableWriter;

public class DataFrameWriter {

  private final Table table;

  public DataFrameWriter(Table table) {
      this.table = table;
  }

  public void csv(String file) throws IOException {
      CsvWriter.write(table, file);
  }

  public void csv(File file) throws IOException {
      CsvWriter.write(table, file);
  }

  public void csv(OutputStream stream) throws IOException {
    CsvWriter.write(table, stream);
  }

  public void csv(Writer writer) throws IOException {
    CsvWriter.write(table, writer);
  }

  public void html(OutputStream stream) throws IOException {
      HtmlTableWriter.write(table, stream);
  }

}
