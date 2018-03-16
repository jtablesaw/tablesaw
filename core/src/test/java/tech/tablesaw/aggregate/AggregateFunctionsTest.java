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

package tech.tablesaw.aggregate;

import org.junit.Before;
import org.junit.Test;
import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.table.ViewGroup;

import static org.junit.Assert.assertEquals;


public class AggregateFunctionsTest {

    private Table table;

    @Before
    public void setUp() throws Exception {
        table = Table.read().csv(CsvReadOptions.builder("../data/BushApproval.csv"));
    }

    @Test
    public void testMean() {
        double result = table.agg("approval", AggregateFunctions.mean);
        assertEquals(64.88235294117646, result, 0.01);
    }

    @Test
    public void testGroupMean() {
        CategoricalColumn byColumn = table.categoryColumn("who");
        ViewGroup group = new ViewGroup(table, byColumn);
        Table result = group.aggregate("approval", AggregateFunctions.mean, AggregateFunctions.stdDev);
        assertEquals(3, result.columnCount());
        assertEquals("who", result.column(0).name());
        assertEquals(6, result.rowCount());
        assertEquals("65.671875", result.get(0, 1));
        assertEquals("10.648876067826901", result.get(0, 2));
    }

    @Test
    public void test2ColumnGroupMean() {
        CategoricalColumn byColumn1 = table.categoryColumn("who");
        CategoricalColumn byColumn2 = table.categoricalColumn("date");
        ViewGroup group = new ViewGroup(table, byColumn1, byColumn2);
        Table result = group.aggregate("approval", AggregateFunctions.mean, AggregateFunctions.sum);
        assertEquals(4, result.columnCount());
        assertEquals("who", result.column(0).name());
        assertEquals(323, result.rowCount());
        assertEquals("46.0", result.get(0, 2));
    }
}