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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import tech.tablesaw.api.Table;

import javax.annotation.concurrent.Immutable;

@Immutable
final public class JsonWriter {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final JsonWriteOptions options;

    public JsonWriter(JsonWriteOptions options) {
        this.options = options;
    }

    public String write(Table table) throws JsonProcessingException {
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
        return mapper.writeValueAsString(output);
    }

}
