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

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.table.SelectionTableSliceGroup;
import tech.tablesaw.table.StandardTableSliceGroup;
import org.junit.Before;
import org.junit.Test;
import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.table.TableSliceGroup;
import tech.tablesaw.io.csv.CsvReadOptions;

import static org.junit.Assert.assertEquals;
import static tech.tablesaw.aggregate.AggregateFunctions.*;

public class AggregateFunctionsTest {

    private Table table;

    @Before
    public void setUp() throws Exception {
        table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv"));
    }

    @Test
    public void testGroupMean() {
        CategoricalColumn byColumn = table.stringColumn("who");
        TableSliceGroup group = StandardTableSliceGroup.create(table, byColumn);
        Table result = group.aggregate("approval", mean, stdDev);
        assertEquals(3, result.columnCount());
        assertEquals("who", result.column(0).name());
        assertEquals(6, result.rowCount());
        assertEquals("65.671875", result.get(0, 1));
        assertEquals("10.648876067826901", result.get(0, 2));
    }

    @Test
    public void testGroupMean2() {
        Table result = table.summarize("approval", mean, stdDev).apply();
        assertEquals(2, result.columnCount());
    }

    @Test
    public void testGroupMean3() {
        Summarizer function = table.summarize("approval", mean, stdDev);
        Table result = function.by("Group", 10);
        assertEquals(32, result.rowCount());
    }

    @Test
    public void testGroupMean4() {
        table.addColumn(table.numberColumn("approval").cube());
        table.column(3).setName("cubed");
        Table result = table.summarize("approval", "cubed", mean, stdDev).apply();
        assertEquals(4, result.columnCount());
    }

    @Test
    public void testGroupMeanByStep() {
        TableSliceGroup group = SelectionTableSliceGroup.create(table, "Step", 5);
        Table result = group.aggregate("approval", mean, AggregateFunctions.stdDev);
        assertEquals(3, result.columnCount());
        assertEquals("53.6", result.get(0, 1));
        assertEquals("2.5099800796022267", result.get(0, 2));
    }

    @Test
    public void test2ColumnGroupMean() {
        CategoricalColumn byColumn1 = table.stringColumn("who");
        CategoricalColumn byColumn2 = table.categoricalColumn("date");
        Table result = table.summarize("approval", mean, sum).by(byColumn1, byColumn2);
        assertEquals(4, result.columnCount());
        assertEquals("who", result.column(0).name());
        assertEquals(323, result.rowCount());
        assertEquals("46.0", result.get(0, 2));
    }

    @Test
    public void testComplexSummarizing() {
        table.addColumn(table.numberColumn("approval").cube());
        table.column(3).setName("cubed");
        CategoricalColumn byColumn1 = table.stringColumn("who");
        CategoricalColumn byColumn2 = table.dateColumn("date").yearMonth();
        Table result = table.summarize("approval", "cubed", mean, sum).by(byColumn1, byColumn2);
        assertEquals(6, result.columnCount());
        assertEquals("who", result.column(0).name());
        assertEquals("date year & month", result.column(1).name());
    }

    @Test
    public void testMultipleColumnTypes() {

        boolean[] args = {true, false, true, false};
        BooleanColumn booleanColumn = BooleanColumn.create("b", args);

        double[] numbers = {1, 2, 3, 4};
        NumberColumn numberColumn = DoubleColumn.create("n", numbers);

        String[] strings = {"M", "F", "M", "F"};
        StringColumn stringColumn = StringColumn.create("s", strings);

        Table table = Table.create("test", booleanColumn, numberColumn);

        Table result = table.summarize(booleanColumn, numberColumn, countTrue, standardDeviation).by(stringColumn);
    }
}