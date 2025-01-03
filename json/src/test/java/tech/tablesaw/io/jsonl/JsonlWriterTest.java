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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;

public class JsonlWriterTest {

  @Test
  public void arrayOfObjects() {
    String json =
        "{\"a\":1453438800000,\"b\":-2.144}\n{\"a\":1454043600000,\"b\":-2.976}\n{\"a\":1454648400000,\"b\":-2.954}";
    Table table = Table.read().string(json, "jsonl");
    String output = table.write().toString("jsonl");
    assertEquals(json, output);
  }
}
