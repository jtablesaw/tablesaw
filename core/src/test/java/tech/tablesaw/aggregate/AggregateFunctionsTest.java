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
import static tech.tablesaw.aggregate.AggregateFunctions.*;


public class AggregateFunctionsTest {

    private Table table;

    @Before
    public void setUp() throws Exception {
        table = Table.read().csv(CsvReadOptions.builder("../data/BushApproval.csv"));
    }

    @Test
    public void testMean() {
        double result = table.agg("approval", mean);
        assertEquals(64.88235294117646, result, 0.01);
    }

    @Test
    public void testGroupMean() {
        CategoricalColumn byColumn = table.categoryColumn("who");
        ViewGroup group = new ViewGroup(table, byColumn);
        Table result = group.aggregate("approval", mean, stdDev);
        assertEquals(3, result.columnCount());
        assertEquals("who", result.column(0).name());
        assertEquals(6, result.rowCount());
        assertEquals("65.671875", result.get(0, 1));
        assertEquals("10.648876067826901", result.get(0, 2));
    }

    @Test
    public void testGroupMean3() {
        SummaryFunction function = table.summarize("approval", mean, stdDev);
        Table result = function.by("Group", 10);
        assertEquals(32, result.rowCount());
    }

    @Test
    public void testGroupMeanByStep() {
        ViewGroup group = ViewGroup.create(table, "Step", 5);
        Table result = group.aggregate("approval", mean, AggregateFunctions.stdDev);
        assertEquals(3, result.columnCount());
        assertEquals("53.6", result.get(0, 1));
        assertEquals("2.5099800796022267", result.get(0, 2));
    }

    @Test
    public void test2ColumnGroupMean() {
        CategoricalColumn byColumn1 = table.categoryColumn("who");
        CategoricalColumn byColumn2 = table.categoricalColumn("date");
        ViewGroup group = new ViewGroup(table, byColumn1, byColumn2);
        Table result = group.aggregate("approval", mean, sum);
        assertEquals(4, result.columnCount());
        assertEquals("who", result.column(0).name());
        assertEquals(323, result.rowCount());
        assertEquals("46.0", result.get(0, 2));
    }
}