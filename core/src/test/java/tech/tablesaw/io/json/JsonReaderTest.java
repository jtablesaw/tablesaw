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

import org.junit.jupiter.api.Test;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;

public class JsonReaderTest {

    @Test
    public void arrayOfArraysWithHeader() {
	String json = "[[\"Date\",\"Value\"],[1453438800000,-2.1448117025014],[1454043600000,-2.9763153817574],[1454648400000,-2.9545283436391]]";
	Table table = Table.read().string(json, "json");
        assertEquals(2, table.columnCount());
        assertEquals(3, table.rowCount());
        assertEquals("Date", table.column(0).name());
        assertEquals("Value", table.column(1).name());
        assertEquals(ColumnType.LONG, table.columnTypes()[0]);
    }

    @Test
    public void arrayOfArraysNoHeader() {
	String json = "[[1453438800000,-2.1448117025014],[1454043600000,-2.9763153817574],[1454648400000,-2.9545283436391]]";
	Table table = Table.read().string(json, "json");
        assertEquals(2, table.columnCount());
        assertEquals(3, table.rowCount());
        assertEquals(ColumnType.LONG, table.columnTypes()[0]);
    }

    @Test
    public void arrayOfNestedObjects() {
	String json = "[{\"a\":1453438800000,\"b\":{\"c\":-2.1448117025014}},{\"a\":1454043600000,\"b\":{\"c\":-2.9763153817574}},{\"a\":1454648400000,\"b\":{\"c\":-2.9545283436391}}]";
	Table table = Table.read().string(json, "json");
        assertEquals(2, table.columnCount());
        assertEquals(3, table.rowCount());
        assertEquals("a", table.column(0).name());
        assertEquals("b.c", table.column(1).name());
        assertEquals(ColumnType.LONG, table.columnTypes()[0]);
    }

}
