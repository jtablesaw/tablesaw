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

package tech.tablesaw.io.json;

import java.io.IOException;
import java.io.Writer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.DataWriter;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.WriterRegistry;

public class JsonWriter implements DataWriter<JsonWriteOptions> {

    private static final JsonWriter INSTANCE = new JsonWriter();
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        register(Table.defaultWriterRegistry);
    }

    public static void register(WriterRegistry registry) {
        registry.registerExtension("json", INSTANCE);
        registry.registerOptions(JsonWriteOptions.class, INSTANCE);
    }

    public void write(Table table, JsonWriteOptions options) throws IOException {
        ArrayNode output = mapper.createArrayNode();
        if (options.asObjects()) {
            for (int r = 0; r < table.rowCount(); r++) {
                ObjectNode row = mapper.createObjectNode();
                for (int c = 0; c < table.columnCount(); c++) {
                    row.set(table.column(c).name(), mapper.convertValue(table.get(r, c), JsonNode.class));
                }
                output.add(row);
            }
        } else {
            if (options.header()) {
                ArrayNode row = mapper.createArrayNode();
                for (int c = 0; c < table.columnCount(); c++) {
                    row.add(mapper.convertValue(table.column(c).name(), JsonNode.class));
                }
                output.add(row);
            }
            for (int r = 0; r < table.rowCount(); r++) {
                ArrayNode row = mapper.createArrayNode();
                for (int c = 0; c < table.columnCount(); c++) {
                    row.add(mapper.convertValue(table.get(r, c), JsonNode.class));
                }
                output.add(row);
            }            
        }

        String str = mapper.writeValueAsString(output);
        Writer writer = options.destination().createWriter();
        writer.write(str);
        writer.flush();
    }

    @Override
    public void write(Table table, Destination dest) throws IOException {
        write(table, JsonWriteOptions.builder(dest).build());
    }

}
