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

package tech.tablesaw.io.jsonl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.Writer;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.DataWriter;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.RuntimeIOException;
import tech.tablesaw.io.WriterRegistry;

public class JsonlWriter implements DataWriter<JsonlWriteOptions> {

  private static final JsonlWriter INSTANCE = new JsonlWriter();
  private static final ObjectMapper mapper =
      new ObjectMapper().registerModule(new JavaTimeModule());

  static {
    register(Table.defaultWriterRegistry);
  }

  public static void register(WriterRegistry registry) {
    registry.registerExtension("jsonl", INSTANCE);
    registry.registerOptions(JsonlWriteOptions.class, INSTANCE);
  }

  public void write(Table table, JsonlWriteOptions options) {
    try (Writer writer = options.destination().createWriter()) {
      for (int r = 0; r < table.rowCount(); r++) {
        ObjectNode row = mapper.createObjectNode();
        for (int c = 0; c < table.columnCount(); c++) {
          row.set(table.column(c).name(), mapper.convertValue(table.get(r, c), JsonNode.class));
        }
        String str = mapper.writeValueAsString(row);
        writer.write(str);
        if (r < table.rowCount() - 1) {
          writer.write("\n");
        }
      }
    } catch (IOException e) {
      throw new RuntimeIOException(e);
    }
  }

  @Override
  public void write(Table table, Destination dest) {
    write(table, JsonlWriteOptions.builder(dest).build());
  }
}
