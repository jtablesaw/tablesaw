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

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.dates.PackedLocalDate;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static tech.tablesaw.aggregate.AggregateFunctions.mean;
import static tech.tablesaw.aggregate.AggregateFunctions.stdDev;

/**
 * Tests for Table
 */
public class TableTest {

    private static final int ROWS_BOUNDARY = 1000;
    private static final Random RANDOM = new Random();

    private Table table;
    private DoubleColumn f1 =  DoubleColumn.create("f1");
    private DoubleColumn numberColumn =  DoubleColumn.create("d1");

    @Before
    public void setUp() {
        table = Table.create("t");
        table.addColumns(f1);
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
        Column<?> column1 = table.column(0);
        assertNotNull(column1);
    }

    @Test
    public void testRowWiseAddition() {
        double[] a = {3, 4, 5};
        double[] b = {3, 4, 5};
        double[] c = {3, 4, 5};
        Table t = Table.create("test",
                DoubleColumn.create("a", a),
                DoubleColumn.create("b", b),
                DoubleColumn.create("c", c));

        DoubleColumn n =
                t.doubleColumn(0)
                .add(t.doubleColumn(1))
                .add(t.doubleColumn(2));

        assertEquals(n.get(0), 9, 0);
        assertEquals(n.get(1), 12, 0);
        assertEquals(n.get(2), 15, 0);
    }

    @Test
    public void testRowWiseAddition2() {
        double[] a = {3, 4, 5};
        double[] b = {3, 4, 5};
        double[] c = {3, 4, 5};
        Table t = Table.create("test",
                DoubleColumn.create("a", a),
                DoubleColumn.create("b", b),
                DoubleColumn.create("c", c));

        DoubleColumn n = sum(t.numberColumns());

        assertEquals(n.get(0), 9, 0);
        assertEquals(n.get(1), 12, 0);
        assertEquals(n.get(2), 15, 0);
    }

    @Test
    public void testRemoveColumns() {
        StringColumn sc = StringColumn.create("0");
        StringColumn sc1 = StringColumn.create("1");
        StringColumn sc2 = StringColumn.create("2");
        StringColumn sc3 = StringColumn.create("3");
        Table t = Table.create("t", sc, sc1, sc2, sc3);
        t.removeColumns(1, 3);
        assertTrue(t.containsColumn(sc));
        assertTrue(t.containsColumn(sc2));
        assertFalse(t.containsColumn(sc1));
        assertFalse(t.containsColumn(sc3));
    }

    @Test
    public void printEmptyTable() {
        Table t = Table.create("Test");
        assertEquals("Test\n\n", t.print());

        StringColumn c1 = StringColumn.create("SC");
        t.addColumns(c1);
        assertEquals(" Test \n SC  |\n------", t.print());
    }

    @Test
    public void testDropDuplicateRows() throws Exception {
        Table t1 = Table.read().csv("../data/bush.csv");
        int rowCount = t1.rowCount();
        Table t2 = Table.read().csv("../data/bush.csv");
        Table t3 = Table.read().csv("../data/bush.csv");
        t1.append(t2).append(t3);
        assertEquals(3 * rowCount, t1.rowCount());
        t1 = t1.dropDuplicateRows();
        assertEquals(t1.rowCount(),rowCount);
    }

    @Test
    public void testMissingValueCounts() {
        StringColumn c1 = StringColumn.create("SC");
        DoubleColumn c2 = DoubleColumn.create("NC");
        DateColumn c3 = DateColumn.create("DC");
        Table t = Table.create("Test", c1, c2, c3);
        assertEquals(0, t.missingValueCounts().doubleColumn(1).get(0), 0.00001);
    }

    @Test
    public void testFullCopy() {
        numberColumn.append(2.23424);
        Table t = Table.create("test");
        t.addColumns(numberColumn);
        Table c = t.copy();
        DoubleColumn doubles = c.doubleColumn(0);
        assertNotNull(doubles);
        assertEquals(1, doubles.size());
    }

    @Test
    public void testColumnCount() {
        assertEquals(0, Table.create("t").columnCount());
        assertEquals(1, table.columnCount());
    }

    @Test
    public void testLast() throws IOException {
        Table t = Table.read().csv("../data/bush.csv");
        t = t.sortOn("date");
        Table t1 = t.last(3);
        assertEquals(3, t1.rowCount());
        assertEquals(LocalDate.of(2004, 2, 5), t1.dateColumn(0).get(2));
    }

    @Test
    public void testSelect1() throws Exception {
        Table t = Table.read().csv("../data/bush.csv");
        Table t1 = t.select(t.column(1), t.column(2));
        assertEquals(2, t1.columnCount());
    }

    @Test
    public void testSelect2() throws Exception {
        Table t = Table.read().csv("../data/bush.csv");
        Table t1 = t.select(t.column(0), t.column(1), t.column(2), t.dateColumn(0).year());
        assertEquals(4, t1.columnCount());
        assertEquals("date year", t1.column(3).name());
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
        Integer[] ratingsArray = {53, 58};
        List<Integer> ratings = Lists.asList(52, ratingsArray);

        Consumer<Row> doable = row -> {
            if (row.getRowNumber() < 5) {
                assertTrue(ratings.contains(row.getInt("approval")));
            }
        };
        t.doWithRows(doable);
    }

    @Test
    public void testDoWithEachRow2() throws Exception {
        Table t = Table.read().csv("../data/bush.csv");
        int dateTarget = PackedLocalDate.pack(LocalDate.of(2002, 1, 1));
        double ratingTarget = 75;
        AtomicInteger count = new AtomicInteger(0);
        Consumer<Row> doable = row -> {
            if (row.getPackedDate("date") > dateTarget
                    && row.getInt("approval") > ratingTarget) {
                count.getAndIncrement();
            }
        };
        t.doWithRows(doable);
        assertTrue(count.get() > 0);
    }

    @Test
    public void testDetect() throws Exception {
        Table t = Table.read().csv("../data/bush.csv");
        int dateTarget = PackedLocalDate.pack(LocalDate.of(2002, 1, 1));
        double ratingTarget = 75;
        Predicate<Row> doable = row ->
                (row.getPackedDate("date") > dateTarget
                && row.getInt("approval") > ratingTarget);
        assertTrue(t.detect(doable));
    }

    @Test
    public void testRowToString() throws Exception {
        Table t = Table.read().csv("../data/bush.csv");
        Row row = new Row(t);
        row.at(0);
        assertEquals("             bush.csv              \n" +
                "    date     |  approval  |  who  |\n" +
                "-----------------------------------\n" +
                " 2004-02-04  |        53  |  fox  |", row.toString());
    }

    @Test
    public void testPairs() throws Exception {
        Table t = Table.read().csv("../data/bush.csv");
        PairChild pairs = new PairChild();
        t.doWithRows(pairs);
    }

    @Test
    public void testPairs2() throws Exception {
        Table t = Table.read().csv("../data/bush.csv");

        Table.Pairs runningAvg =  new Table.Pairs() {

            private List<Double> values = new ArrayList<>();

            @Override
            public void doWithPair(Row row1, Row row2) {
                double r1  = row1.getInt("approval");
                double r2  = row2.getInt("approval");
                values.add((r1 + r2) / 2.0);
            }

            @Override
            public List<Double> getResult() {
                return values;
            }
        };

        t.doWithRows(runningAvg);
    }

    @Test
    public void testRollWithNrows2() throws Exception {
        Table t = Table.read().csv("../data/bush.csv").first(4);
        IntColumn approval = t.intColumn("approval");

        List<Integer> sums = new ArrayList<>();
        Consumer<Row[]> rowConsumer = rows -> {
            int sum = 0;
            for (Row row : rows) {
                sum += row.getInt("approval");
            }
            sums.add(sum);
        };
        t.rollWithRows(rowConsumer,2);
        assertTrue(sums.contains((int) approval.getDouble(0) + (int) approval.getDouble(1)));
        assertTrue(sums.contains((int) approval.getDouble(1) + (int) approval.getDouble(2)));
        assertTrue(sums.contains((int) approval.getDouble(2) + (int) approval.getDouble(3)));
    }

    private class PairChild implements Table.Pairs {

        List<Double> runningAverage = new ArrayList<>();

        @Override
        public void doWithPair(Row row1, Row row2) {
            double r1  = row1.getInt("approval");
            double r2  = row2.getInt("approval");
            runningAverage.add((r1 + r2) / 2.0);
        }
    }

    @Test
    public void testRowCount() {
        assertEquals(0, table.rowCount());
        DoubleColumn floatColumn = this.f1;
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
        DoubleColumn column =  DoubleColumn.create("e1");
        table.addColumns(column);
        DoubleColumn first = f1.emptyCopy();
        DoubleColumn second = column.emptyCopy();
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
        DoubleColumn column =  DoubleColumn.create("42");
        Table tableToAppend = Table.create("wrong", column);
        table.append(tableToAppend);
    }

    @Test(expected = IllegalStateException.class)
    public void testAppendTableWithDifferentShape() {
        DoubleColumn column =  DoubleColumn.create("e1");
        table.addColumns(column);
        Table tableToAppend = Table.create("different", column);
        assertEquals(2, table.columns().size());
        assertEquals(1, tableToAppend.columns().size());
        table.append(tableToAppend);
    }

    @Test
    public void testReplaceColumn() {
        DoubleColumn first =  DoubleColumn.create("c1", new double[]{1, 2, 3, 4, 5});
        DoubleColumn second =  DoubleColumn.create("c2", new double[]{6, 7, 8, 9, 10});
        DoubleColumn replacement =  DoubleColumn.create("c2", new double[]{10, 20, 30, 40, 50});

        Table t = Table.create("populated", first, second);

        int colIndex = t.columnIndex(second);
        assertSame(t.column("c2"), second);
        t.replaceColumn("c2", replacement);
        assertSame(t.column("c1"), first);
        assertSame(t.column("c2"), replacement);
        assertEquals(t.columnIndex(replacement), colIndex);
    }

    private int appendRandomlyGeneratedColumn(Table table) {
        DoubleColumn column = f1.emptyCopy();
        populateColumn(column);
        return appendColumn(table, column);
    }

    private void appendEmptyColumn(Table table) {
        DoubleColumn column = f1.emptyCopy();
        appendColumn(table, column);
    }

    private int appendColumn(Table table, Column<?> column) {
        Table tableToAppend = Table.create("populated", column);
        table.append(tableToAppend);
        return column.size();
    }

    private void assertTableColumnSize(Table table, Column<?> column, int expected) {
        int actual = table.column(column.name()).size();
        assertEquals(expected, actual);
    }

    private int populateColumn(DoubleColumn floatColumn) {
        int rowsCount = RANDOM.nextInt(ROWS_BOUNDARY);
        for (int i = 0; i < rowsCount; i++) {
            floatColumn.append(RANDOM.nextFloat());
        }
        assertEquals(floatColumn.size(), rowsCount);
        return rowsCount;
    }

    @Test
    public void testAsMatrix() {
        DoubleColumn first =  DoubleColumn.create("c1", new double[]{1L, 2L, 3L, 4L, 5L});
        DoubleColumn second =  DoubleColumn.create("c2", new double[]{6.0f, 7.0f, 8.0f, 9.0f, 10.0f});
        DoubleColumn third =  DoubleColumn.create("c3", new double[]{10.0, 20.0, 30.0, 40.0, 50.0});

        Table t = Table.create("table", first, second, third);
        double[][] matrix = t.as().doubleMatrix();
        assertEquals(5, matrix.length);
        assertArrayEquals(new double[]{1.0, 6.0, 10.0}, matrix[0], 0.0000001);
        assertArrayEquals(new double[]{2.0, 7.0, 20.0}, matrix[1], 0.0000001);
        assertArrayEquals(new double[]{3.0, 8.0, 30.0}, matrix[2], 0.0000001);
        assertArrayEquals(new double[]{4.0, 9.0, 40.0}, matrix[3], 0.0000001);
        assertArrayEquals(new double[]{5.0, 10.0, 50.0}, matrix[4], 0.0000001);
    }

    @Test
    public void testRowSort() throws Exception {
        Table bush = Table.read().csv("../data/bush.csv");

        Comparator<Row> rowComparator = Comparator.comparingDouble(o -> o.getInt("approval"));

        Table sorted = bush.sortOn(rowComparator);
        IntColumn approval = sorted.intColumn("approval");
        for (int i = 0; i < bush.rowCount() - 2; i++) {
            assertTrue(approval.get(i) <= approval.get(i + 1));
        }
    }

    @Test
    public void testIterable() throws Exception {
        Table bush = Table.read().csv("../data/bush.csv");
        int rowNumber = 0;
        for (Row row : bush.first(10)) {
            assertEquals(row.getRowNumber(), rowNumber++);
        }
    }

    private DoubleColumn sum(DoubleColumn ... columns) {
        int size = columns[0].size();
        DoubleColumn result = DoubleColumn.create("sum", size);
        for (int r = 0; r < size; r++) {
            double sum = 0;
            for (DoubleColumn nc : columns) {
                sum += nc.get(r);
            }
            result.append(sum);
        }
        return result;
    }
}
