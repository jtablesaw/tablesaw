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
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvWriteOptions;
import tech.tablesaw.io.csv.CsvWriter;

public class DataFrameWriter {

  private final WriterRegistry registry;
  private final Table table;

  public DataFrameWriter(WriterRegistry registry, Table table) {
    this.registry = registry;
    this.table = table;
  }

  public void toFile(String file) {
    toFile(new File(file));
  }

  public void toFile(File file) {
    String extension = null;
    try {
      extension = Files.getFileExtension(file.getCanonicalPath());
    } catch (IOException e) {
      throw new RuntimeIOException(e);
    }
    DataWriter<?> dataWriter = registry.getWriterForExtension(extension);
    dataWriter.write(table, new Destination(file));
  }

  public void toStream(OutputStream stream, String extension) {
    DataWriter<?> dataWriter = registry.getWriterForExtension(extension);
    dataWriter.write(table, new Destination(stream));
  }

  public void toWriter(Writer writer, String extension) {
    DataWriter<?> dataWriter = registry.getWriterForExtension(extension);
    dataWriter.write(table, new Destination(writer));
  }

  public <T extends WriteOptions> void usingOptions(T options) {
    DataWriter<T> dataWriter = registry.getWriterForOptions(options);
    dataWriter.write(table, options);
  }

  public String toString(String extension) {
    StringWriter writer = new StringWriter();
    DataWriter<?> dataWriter = registry.getWriterForExtension(extension);
    dataWriter.write(table, new Destination(writer));
    return writer.toString();
  }

  // legacy methods left for backwards compatibility

  public void csv(String file) {
    CsvWriteOptions options = CsvWriteOptions.builder(file).build();
    new CsvWriter().write(table, options);
  }

  public void csv(File file) {
    CsvWriteOptions options = CsvWriteOptions.builder(file).build();
    new CsvWriter().write(table, options);
  }

  public void csv(CsvWriteOptions options) {
    new CsvWriter().write(table, options);
  }

  public void csv(OutputStream stream) {
    CsvWriteOptions options = CsvWriteOptions.builder(stream).build();
    new CsvWriter().write(table, options);
  }

  public void csv(Writer writer) {
    CsvWriteOptions options = CsvWriteOptions.builder(writer).build();
    new CsvWriter().write(table, options);
  }
}
