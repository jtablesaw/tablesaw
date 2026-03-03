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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.Destination;

public class JsonWriterTest {

  @Test
  public void arrayOfArraysWithHeader() throws IOException {
    String json =
        "[[\"Date\",\"Value\"],[1453438800000,-2.144],[1454043600000,-2.976],[1454648400000,-2.9541]]";
    Table table = Table.read().string(json, "json");
    StringWriter writer = new StringWriter();
    table
        .write()
        .usingOptions(JsonWriteOptions.builder(writer).asObjects(false).header(true).build());
    assertEquals(json, writer.toString());
  }

  @Test
  public void arrayOfArraysNoHeader() throws IOException {
    String json = "[[1453438800000,-2.144],[1454043600000,-2.976],[1454648400000,-2.954]]";
    Table table = Table.read().string(json, "json");
    StringWriter writer = new StringWriter();
    table
        .write()
        .usingOptions(JsonWriteOptions.builder(writer).asObjects(false).header(false).build());
    assertEquals(json, writer.toString());
  }

  @Test
  public void arrayOfObjects() {
    String json =
        "[{\"a\":1453438800000,\"b\":-2.144},{\"a\":1454043600000,\"b\":-2.976},{\"a\":1454648400000,\"b\":-2.954}]";
    Table table = Table.read().string(json, "json");
    String output = table.write().toString("json");
    assertEquals(json, output);
  }

  @Test
  public void withCustomMapper() {
    var json = "[{\"a\":\"2021-01-01\"}, {\"a\":\"2021-02-01\"}]";
    var table = Table.read().string(json, "json");
    var mapper = new ObjectMapper();
    mapper.registerModule(new SimpleModule().addSerializer(new StdSerializer<LocalDate>(LocalDate.class) {
      final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
      @Override
      public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
              throws IOException {
        jsonGenerator.writeString(FORMATTER.format(localDate));
      }
    }));

    var baos = new ByteArrayOutputStream();
    new JsonWriter().write(table, new JsonWriteOptions.Builder(new Destination(baos)).mapper(mapper).build());
    assertEquals(new String(baos.toByteArray()), "[{\"a\":\"2021/01/01\"},{\"a\":\"2021/02/01\"}]");
  }
}
