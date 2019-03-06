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

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import tech.tablesaw.api.Table;

public class JsonWriterTest {

    @Test
    public void arrayOfArraysWithHeader() throws IOException {
	String json = "[[\"Date\",\"Value\"],[1453438800000,-2.144],[1454043600000,-2.976],[1454648400000,-2.9541]]";
	Table table = Table.read().json(new StringReader(json), "jsonTable");
	String output = table.write().json(JsonWriteOptions.builder().asObjects(false).header(true).build());
        assertEquals(json, output);
    }

    @Test
    public void arrayOfArraysNoHeader() throws IOException {
	String json = "[[1453438800000,-2.144],[1454043600000,-2.976],[1454648400000,-2.954]]";
	Table table = Table.read().json(new StringReader(json), "jsonTable");
	String output = table.write().json(JsonWriteOptions.builder().asObjects(false).header(false).build());
        assertEquals(json, output);
    }

    @Test
    public void arrayOfObjects() throws IOException {
	String json = "[{\"a\":1453438800000,\"b\":-2.144},{\"a\":1454043600000,\"b\":-2.976},{\"a\":1454648400000,\"b\":-2.954}]";
	Table table = Table.read().json(new StringReader(json), "jsonTable");
	String output = table.write().json();
        assertEquals(json, output);
    }

}
