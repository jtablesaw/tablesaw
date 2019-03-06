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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.tablesaw.aggregate.AggregateFunctions.allTrue;
import static tech.tablesaw.aggregate.AggregateFunctions.anyTrue;
import static tech.tablesaw.aggregate.AggregateFunctions.countFalse;
import static tech.tablesaw.aggregate.AggregateFunctions.countMissing;
import static tech.tablesaw.aggregate.AggregateFunctions.countTrue;
import static tech.tablesaw.aggregate.AggregateFunctions.countUnique;
import static tech.tablesaw.aggregate.AggregateFunctions.countWithMissing;
import static tech.tablesaw.aggregate.AggregateFunctions.earliestDate;
import static tech.tablesaw.aggregate.AggregateFunctions.latestDate;
import static tech.tablesaw.aggregate.AggregateFunctions.mean;
import static tech.tablesaw.aggregate.AggregateFunctions.noneTrue;
import static tech.tablesaw.aggregate.AggregateFunctions.percentile90;
import static tech.tablesaw.aggregate.AggregateFunctions.percentile95;
import static tech.tablesaw.aggregate.AggregateFunctions.percentile99;
import static tech.tablesaw.aggregate.AggregateFunctions.proportionFalse;
import static tech.tablesaw.aggregate.AggregateFunctions.proportionTrue;
import static tech.tablesaw.aggregate.AggregateFunctions.standardDeviation;
import static tech.tablesaw.aggregate.AggregateFunctions.stdDev;
import static tech.tablesaw.aggregate.AggregateFunctions.sum;

import org.apache.commons.math3.stat.StatUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.table.SelectionTableSliceGroup;
import tech.tablesaw.table.StandardTableSliceGroup;
import tech.tablesaw.table.TableSliceGroup;

public class AggregateFunctionsTest {

    private Table table;

    @BeforeEach
    public void setUp() throws Exception {
        table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv"));
    }

    @Test
    public void testGroupMean() {
        StringColumn byColumn = table.stringColumn("who");
        TableSliceGroup group = StandardTableSliceGroup.create(table, byColumn);
        Table result = group.aggregate("approval", mean, stdDev);
        assertEquals(3, result.columnCount());
        assertEquals("who", result.column(0).name());
        assertEquals(6, result.rowCount());
        assertEquals("65.671875", result.getUnformatted(0, 1));
        assertEquals("10.648876067826901", result.getUnformatted(0, 2));
    }

    @Test
    public void testDateMin() {
        StringColumn byColumn = table.dateColumn("date").yearQuarter();
        Table result = table.summarize("approval", "date", mean, earliestDate).by(byColumn);
        assertEquals(3, result.columnCount());
        assertEquals(13, result.rowCount());
    }

    @Test
    public void testBooleanAggregateFunctions() {
        boolean[] values = {true, false};
        BooleanColumn bc = BooleanColumn.create("test", values);
        assertTrue(anyTrue.summarize(bc));
        assertFalse(noneTrue.summarize(bc));
        assertFalse(allTrue.summarize(bc));
    }

    @Test
    public void testGroupMean2() {
        Table result = table.summarize("approval", mean, stdDev).apply();
        assertEquals(2, result.columnCount());
    }

    @Test
    public void testApplyWithNonNumericResults() {
        Table result = table.summarize("date", earliestDate, latestDate).apply();
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
        table.addColumns(table.numberColumn("approval").cube());
        table.column(3).setName("cubed");
        Table result = table.summarize("approval", "cubed", mean, stdDev).apply();
        assertEquals(4, result.columnCount());
    }

    @Test
    public void testGroupMeanByStep() {
        TableSliceGroup group = SelectionTableSliceGroup.create(table, "Step", 5);
        Table result = group.aggregate("approval", mean, stdDev);
        assertEquals(3, result.columnCount());
        assertEquals("53.6", result.getUnformatted(0, 1));
        assertEquals("2.5099800796022267", result.getUnformatted(0, 2));
    }

    @Test
    public void testSummaryWithACalculatedColumn() {
        Summarizer summarizer = new Summarizer(table, table.dateColumn("date").year(), mean);
        Table t = summarizer.apply();
        double avg = t.doubleColumn(0).get(0);
        assertTrue(avg > 2002 && avg < 2003);
    }

    @Test
    public void test2ColumnGroupMean() {
        StringColumn byColumn1 = table.stringColumn("who");
        DateColumn byColumn2 = table.dateColumn("date");
        Table result = table.summarize("approval", mean, sum).by(byColumn1, byColumn2);
        assertEquals(4, result.columnCount());
        assertEquals("who", result.column(0).name());
        assertEquals(323, result.rowCount());
        assertEquals("46.0", result.getUnformatted(0, 2));
    }

    @Test
    public void testComplexSummarizing() {
        table.addColumns(table.numberColumn("approval").cube());
        table.column(3).setName("cubed");
        StringColumn byColumn1 = table.stringColumn("who");
        StringColumn byColumn2 = table.dateColumn("date").yearMonth();
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
        DoubleColumn numberColumn = DoubleColumn.create("n", numbers);

        String[] strings = {"M", "F", "M", "F"};
        StringColumn stringColumn = StringColumn.create("s", strings);

        Table table = Table.create("test", booleanColumn, numberColumn);
        table.summarize(booleanColumn, numberColumn, countTrue, standardDeviation).by(stringColumn);
    }

    @Test
    public void testMultipleColumnTypesWithApply() {

        boolean[] args = {true, false, true, false};
        BooleanColumn booleanColumn = BooleanColumn.create("b", args);

        double[] numbers = {1, 2, 3, 4};
        DoubleColumn numberColumn = DoubleColumn.create("n", numbers);

        String[] strings = {"M", "F", "M", "F"};
        StringColumn stringColumn = StringColumn.create("s", strings);

        Table table = Table.create("test", booleanColumn, numberColumn, stringColumn);
        Table summarized = table.summarize(booleanColumn, numberColumn, countTrue, standardDeviation).apply();
        assertEquals(1.2909944487358056, summarized.doubleColumn(1).get(0), 0.00001);
    }

    @Test
    public void testBooleanFunctions() {
        BooleanColumn c = BooleanColumn.create("test");
        c.append(true);
        c.appendCell("");
        c.append(false);
        assertEquals(1, countTrue.summarize(c), 0.0001);
        assertEquals(1, countFalse.summarize(c), 0.0001);
        assertEquals(0.5, proportionFalse.summarize(c), 0.0001);
        assertEquals(0.5, proportionTrue.summarize(c), 0.0001);
        assertEquals(1, countMissing.summarize(c), 0.0001);
        assertEquals(3, countWithMissing.summarize(c), 0.0001);
        assertEquals(2, countUnique.summarize(c), 0.0001);
    }


    @Test
    public void testPercentileFunctions() {
        double[] values = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        DoubleColumn c = DoubleColumn.create("test", values);
        c.appendCell("");

        assertEquals(1, countMissing.summarize(c), 0.0001);
        assertEquals(11, countWithMissing.summarize(c), 0.0001);

        assertEquals(StatUtils.percentile(values, 90), percentile90.summarize(c), 0.0001);
        assertEquals(StatUtils.percentile(values, 95), percentile95.summarize(c), 0.0001);
        assertEquals(StatUtils.percentile(values, 99), percentile99.summarize(c), 0.0001);

        assertEquals(10, countUnique.summarize(c), 0.0001);
    }
}