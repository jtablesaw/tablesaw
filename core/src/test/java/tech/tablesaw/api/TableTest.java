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

package tech.tablesaw.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tech.tablesaw.columns.Column;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;
import static tech.tablesaw.aggregate.AggregateFunctions.mean;
import static tech.tablesaw.aggregate.AggregateFunctions.stdDev;

/**
 * Tests for Table
 */
public class TableTest {

    private static final int ROWS_BOUNDARY = 1000;
    private static final Random RANDOM = new Random();

    private Table table;
    private NumberColumn f1 =  DoubleColumn.create("f1");
    private NumberColumn numberColumn =  DoubleColumn.create("d1");

    @Before
    public void setUp() {
        table = Table.create("t");
        table.addColumn(f1);
    }

    @Test
    public void testSummarize() throws Exception {
        Table table = Table.read().csv("../data/tornadoes_1950-2014.csv");
        Table result = table.summarize("Injuries", mean, stdDev).by("State");
        assertEquals(49, result.rowCount());
        assertEquals(3, result.columnCount());
        Assert.assertEquals("4.580805569368455", result.column(1).getString(0));
    }

    @Test
    public void testColumn() {
        Column column1 = table.column(0);
        assertNotNull(column1);
    }

    @Test
    public void printEmptyTable() {
        Table t = Table.create("Test");
        assertEquals("Test\n\n", t.print());

        Column c = StringColumn.create("SC");
        t.addColumn(c);
        assertEquals(" Test \n SC  |\n------", t.print());
    }

    @Test
    public void testFullCopy() {
        numberColumn.append(2.23424);
        Table t = Table.create("test");
        t.addColumn(numberColumn);
        Table c = t.fullCopy();
        NumberColumn doubles = c.numberColumn(0);
        assertNotNull(doubles);
        assertEquals(1, doubles.size());
    }

    @Test
    public void testColumnCount() {
        assertEquals(0, Table.create("t").columnCount());
        assertEquals(1, table.columnCount());
    }

    @Test
    public void testSampleSplit() throws Exception {
        Table t = Table.read().csv("../data/bush.csv");
        Table[] results = t.sampleSplit(.75);
        assertEquals(t.rowCount(), results[0].rowCount() + results[1].rowCount());
    }

    @Test
    public void testDoWithEachRow() throws Exception {
        Table t = Table.read().csv("../data/bush.csv").first(10);
        System.out.println(t.print());
        Table.Doable doable = new Table.Doable() {

            @Override
            public void doWithRow(Row row) {
                if (row.getRowNumber() < 5) {
                    System.out.println("On "
                            + row.getPackedDate("date")
                            + ", his approval sucks: "
                            + row.getDouble("approval"));
                }
            }
        };

        t.doWithEachRow(doable);
    }

    @Test
    public void testCollectFromEachRow() throws Exception {
        Table t = Table.read().csv("../data/bush.csv");

        Table.Collectable collectable = new Table.Collectable(StringColumn.create("stringz")) {

            @Override
            void collectFromRow(Row row) {
                ((StringColumn) column())
                        .append(row.getString("who") + " can't predict "
                        + row.getDouble("approval"));
            }
        };

        Column result = t.collectFromEachRow(collectable);
        assertEquals("fox can't predict 53.0", (result.getString(0)));
        assertEquals("fox can't predict 53.0", (result.getString(1)));
    }

    @Test
    public void testPairs() throws Exception {
        Table t = Table.read().csv("../data/bush.csv");
        PairChild pairs = new PairChild();
        t.doWithRowPairs(pairs);
        System.out.println(pairs.runningAverage);
    }

    @Test
    public void testRolllWithNrows() throws Exception {
        Table t = Table.read().csv("../data/bush.csv").first(10);

        Table.MultiRowDoable multiRowDoable = rows -> {
            int sum = 0;
            for (Row row : rows) {
                sum += row.getDouble("approval");
            }
            System.out.println("Running avg = " + sum / (double) rows.length);
        };
        t.rollWithNrows(multiRowDoable,2);
    }

    private class PairChild implements Table.Pairs {

        List<Double> runningAverage = new ArrayList<>();

        @Override
        public void doWithPair(Row row1, Row row2) {
            double r1  = row1.getDouble("approval");
            double r2  = row2.getDouble("approval");
            runningAverage.add((r1 + r2) / 2.0);
        }

    }

    @Test
    public void testRowCount() {
        assertEquals(0, table.rowCount());
        NumberColumn floatColumn = this.f1;
        floatColumn.append(2f);
        assertEquals(1, table.rowCount());
        floatColumn.append(2.2342f);
        assertEquals(2, table.rowCount());
    }

    @Test
    public void testAppend() {
        int appendedRows = appendRandomlyGeneratedColumn(table);
        assertTableColumnSize(table, f1, appendedRows);
    }

    @Test
    public void testAppendEmptyTable() {
        appendEmptyColumn(table);
        assertTrue(table.isEmpty());
    }

    @Test
    public void testAppendToNonEmptyTable() {
        populateColumn(f1);
        assertFalse(table.isEmpty());
        int initialSize = table.rowCount();
        int appendedRows = appendRandomlyGeneratedColumn(table);
        assertTableColumnSize(table, f1, initialSize + appendedRows);
    }

    @Test
    public void testAppendEmptyTableToNonEmptyTable() {
        populateColumn(f1);
        assertFalse(table.isEmpty());
        int initialSize = table.rowCount();
        appendEmptyColumn(table);
        assertTableColumnSize(table, f1, initialSize);
    }

    @Test
    public void testAppendMultipleColumns() {
        NumberColumn column =  DoubleColumn.create("e1");
        table.addColumn(column);
        NumberColumn first = f1.emptyCopy();
        NumberColumn second = column.emptyCopy();
        int firstColumnSize = populateColumn(first);
        int secondColumnSize = populateColumn(second);
        Table tableToAppend = Table.create("populated", first, second);
        table.append(tableToAppend);
        assertTableColumnSize(table, f1, firstColumnSize);
        assertTableColumnSize(table, column, secondColumnSize);
    }

    @Test(expected = NullPointerException.class)
    public void testAppendNull() {
        table.append(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testAppendTableWithNonExistingColumns() {
        Table tableToAppend = Table.create("wrong", numberColumn);
        table.append(tableToAppend);
    }

    @Test(expected = IllegalStateException.class)
    public void testAppendTableWithAnotherColumnName() {
        NumberColumn column =  DoubleColumn.create("42");
        Table tableToAppend = Table.create("wrong", column);
        table.append(tableToAppend);
    }

    @Test(expected = IllegalStateException.class)
    public void testAppendTableWithDifferentShape() {
        NumberColumn column =  DoubleColumn.create("e1");
        table.addColumn(column);
        Table tableToAppend = Table.create("different", column);
        assertTrue(table.columns().size() == 2);
        assertTrue(tableToAppend.columns().size() == 1);
        table.append(tableToAppend);
    }

    @Test
    public void testReplaceColumn() {
        NumberColumn first =  DoubleColumn.create("c1", new double[]{1, 2, 3, 4, 5});
        NumberColumn second =  DoubleColumn.create("c2", new double[]{6, 7, 8, 9, 10});
        NumberColumn replacement =  DoubleColumn.create("c2", new double[]{10, 20, 30, 40, 50});

        Table t = Table.create("populated", first, second);

        int colIndex = t.columnIndex(second);
        assertTrue(t.column("c2") == second);
        t.replaceColumn("c2", replacement);
        assertTrue(t.column("c1") == first);
        assertTrue(t.column("c2") == replacement);
        assertTrue(t.columnIndex(replacement) == colIndex);
    }

    private int appendRandomlyGeneratedColumn(Table table) {
        NumberColumn column = f1.emptyCopy();
        populateColumn(column);
        return appendColumn(table, column);
    }

    private void appendEmptyColumn(Table table) {
        NumberColumn column = f1.emptyCopy();
        appendColumn(table, column);
    }

    private int appendColumn(Table table, Column column) {
        Table tableToAppend = Table.create("populated", column);
        table.append(tableToAppend);
        return column.size();
    }

    private void assertTableColumnSize(Table table, Column column, int expected) {
        int actual = table.column(column.name()).size();
        assertEquals(expected, actual);
    }

    private int populateColumn(NumberColumn floatColumn) {
        int rowsCount = RANDOM.nextInt(ROWS_BOUNDARY);
        for (int i = 0; i < rowsCount; i++) {
            floatColumn.append(RANDOM.nextFloat());
        }
        assertTrue(floatColumn.size() == rowsCount);
        return rowsCount;
    }

    @Test
    public void testAsMatrix() {
        NumberColumn first =  DoubleColumn.create("c1", new double[]{1l, 2l, 3l, 4l, 5l});
        NumberColumn second =  DoubleColumn.create("c2", new double[]{6.0f, 7.0f, 8.0f, 9.0f, 10.0f});
        NumberColumn third =  DoubleColumn.create("c3", new double[]{10.0, 20.0, 30.0, 40.0, 50.0});

        Table t = Table.create("table", first, second, third);
        double[][] matrix = t.asMatrix();
        assertEquals(5, matrix.length);
        assertArrayEquals(new double[]{1.0, 6.0, 10.0}, matrix[0], 0.0000001);
        assertArrayEquals(new double[]{2.0, 7.0, 20.0}, matrix[1], 0.0000001);
        assertArrayEquals(new double[]{3.0, 8.0, 30.0}, matrix[2], 0.0000001);
        assertArrayEquals(new double[]{4.0, 9.0, 40.0}, matrix[3], 0.0000001);
        assertArrayEquals(new double[]{5.0, 10.0, 50.0}, matrix[4], 0.0000001);
    }

}
