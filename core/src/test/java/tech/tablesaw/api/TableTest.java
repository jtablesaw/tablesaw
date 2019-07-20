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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.dates.PackedLocalDate;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.tablesaw.aggregate.AggregateFunctions.mean;
import static tech.tablesaw.aggregate.AggregateFunctions.stdDev;

public class TableTest {

    private static final String LINE_END = System.lineSeparator();
    private static final int ROWS_BOUNDARY = 1000;
    private static final Random RANDOM = new Random();

    private Table table;
    private DoubleColumn f1 =  DoubleColumn.create("f1");
    private DoubleColumn numberColumn =  DoubleColumn.create("d1");

    @BeforeEach
    void setUp() {
        table = Table.create("t");
        table.addColumns(f1);
    }

    @Test
    void testSummarize() throws Exception {
        Table table = Table.read().csv("../data/tornadoes_1950-2014.csv");
        Table result = table.summarize("Injuries", mean, stdDev).by("State");
        assertEquals(49, result.rowCount());
        assertEquals(3, result.columnCount());
        assertEquals("4.580805569368455", result.column(1).getString(0));
    }

    @Test
    void testColumn() {
        Column<?> column1 = table.column(0);
        assertNotNull(column1);
    }

    @Test
    void testColumnSizeCheck() {
        assertThrows(IllegalArgumentException.class, () -> {
            double[] a = {3, 4};
            double[] b = {3, 4, 5};
            Table.create("test",
                    DoubleColumn.create("a", a),
                    DoubleColumn.create("b", b));
        });
    }

    @Test
    void testRowWiseAddition() {
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
    void testRowWiseAddition2() {
        double[] a = {3, 4, 5};
        double[] b = {3, 4, 5};
        double[] c = {3, 4, 5};
        Table t = Table.create("test",
                DoubleColumn.create("a", a),
                DoubleColumn.create("b", b),
                DoubleColumn.create("c", c));

        DoubleColumn n = sum(
                t.doubleColumn("a"),
                t.doubleColumn("b"),
                t.doubleColumn("c"));

        assertEquals(n.get(0), 9, 0);
        assertEquals(n.get(1), 12, 0);
        assertEquals(n.get(2), 15, 0);
    }

    @Test
    void testRemoveColumns() {
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
    void printEmptyTable() {
        Table t = Table.create("Test");
        assertEquals("Test" + LINE_END + LINE_END, t.print());

        StringColumn c1 = StringColumn.create("SC");
        t.addColumns(c1);
        assertEquals(" Test " + LINE_END + " SC  |" + LINE_END + "------", t.print());
    }

    @Test
    void testDropDuplicateRows() throws Exception {
        Table t1 = Table.read().csv("../data/bush.csv");
        int rowCount = t1.rowCount();
        Table t2 = Table.read().csv("../data/bush.csv");
        Table t3 = Table.read().csv("../data/bush.csv");
        t1.append(t2).append(t3);
        assertEquals(3 * rowCount, t1.rowCount());
        t1 = t1.dropDuplicateRows();
        assertEquals(rowCount, t1.rowCount());
    }

    @Test
    void testMissingValueCounts() {
        StringColumn c1 = StringColumn.create("SC");
        DoubleColumn c2 = DoubleColumn.create("NC");
        DateColumn c3 = DateColumn.create("DC");
        Table t = Table.create("Test", c1, c2, c3);
        assertEquals(0, t.missingValueCounts().doubleColumn(1).get(0), 0.00001);
    }

    @Test
    void testFullCopy() {
        numberColumn.append(2.23424);
        Table t = Table.create("test");
        t.addColumns(numberColumn);
        Table c = t.copy();
        DoubleColumn doubles = c.doubleColumn(0);
        assertNotNull(doubles);
        assertEquals(1, doubles.size());
    }

    @Test
    void testColumnCount() {
        assertEquals(0, Table.create("t").columnCount());
        assertEquals(1, table.columnCount());
    }

    @Test
    void testLast() throws IOException {
        Table t = Table.read().csv("../data/bush.csv");
        t = t.sortOn("date");
        Table t1 = t.last(3);
        assertEquals(3, t1.rowCount());
        assertEquals(LocalDate.of(2004, 2, 5), t1.dateColumn(0).get(2));
    }

    @Test
    void testSelect1() throws Exception {
        Table t = Table.read().csv("../data/bush.csv");
        Table t1 = t.select(t.column(1), t.column(2));
        assertEquals(2, t1.columnCount());
    }

    @Test
    void testSelect2() throws Exception {
        Table t = Table.read().csv("../data/bush.csv");
        Table t1 = t.select(t.column(0), t.column(1), t.column(2), t.dateColumn(0).year());
        assertEquals(4, t1.columnCount());
        assertEquals("date year", t1.column(3).name());
    }

    @Test
    void testSampleSplit() throws Exception {
        Table t = Table.read().csv("../data/bush.csv");
        Table[] results = t.sampleSplit(.75);
        assertEquals(t.rowCount(), results[0].rowCount() + results[1].rowCount());
    }
    
   @Test
    void testStratifiedSampleSplit() throws Exception {
        Table t = Table.read().csv("../data/bush.csv");
        Table[] results = t.stratifiedSampleSplit(t.stringColumn("who"), .75);
        assertEquals(t.rowCount(), results[0].rowCount() + results[1].rowCount());
        int totalFoxCount = t.where(t.stringColumn("who").equalsIgnoreCase("fox")).rowCount();
        int stratifiedFoxCount = results[0].where(results[0].stringColumn("who").equalsIgnoreCase("fox")).rowCount();
        
        assertEquals(.75, (double) stratifiedFoxCount/totalFoxCount, 0.0);
    }

    @Test
    void testDoWithEachRow() throws Exception {
        Table t = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").minimizeColumnSizes()).first(10);
        Short[] ratingsArray = {53, 58};
        List<Short> ratings = Lists.asList((short) 52, ratingsArray);

        Consumer<Row> doable = row -> {
            if (row.getRowNumber() < 5) {
                assertTrue(ratings.contains(row.getShort("approval")));
            }
        };
        t.stream().forEach(doable);
    }

    @Test
    void testDoWithEachRow2() throws Exception {
        Table t = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").minimizeColumnSizes());
        int dateTarget = PackedLocalDate.pack(LocalDate.of(2002, 1, 1));
        double ratingTarget = 75;
        AtomicInteger count = new AtomicInteger(0);
        Consumer<Row> doable = row -> {
            if (row.getPackedDate("date") > dateTarget
                    && row.getShort("approval") > ratingTarget) {
                count.getAndIncrement();
            }
        };
        t.stream().forEach(doable);
        assertTrue(count.get() > 0);
    }

    @Test
    void testDetect() throws Exception {
        Table t = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").minimizeColumnSizes());
        int dateTarget = PackedLocalDate.pack(LocalDate.of(2002, 1, 1));
        double ratingTarget = 75;
        Predicate<Row> doable = row ->
                (row.getPackedDate("date") > dateTarget
                && row.getShort("approval") > ratingTarget);
        assertTrue(t.stream().anyMatch(doable));
    }

    @Test
    void testRowToString() throws Exception {
        Table t = Table.read().csv("../data/bush.csv");
        Row row = new Row(t);
        row.at(0);
        assertEquals("             bush.csv              " + LINE_END +
                "    date     |  approval  |  who  |" + LINE_END +
                "-----------------------------------" + LINE_END +
                " 2004-02-04  |        53  |  fox  |", row.toString());
    }

    @Test
    void testPairs() throws Exception {
        Table t = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").minimizeColumnSizes());
        PairChild pairs = new PairChild();
        t.doWithRows(pairs);
    }

    @Test
    void testPairs2() throws Exception {
        Table t = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").minimizeColumnSizes());

        Table.Pairs runningAvg =  new Table.Pairs() {

            private List<Double> values = new ArrayList<>();

            @Override
            public void doWithPair(Row row1, Row row2) {
                short r1  = row1.getShort("approval");
                short r2  = row2.getShort("approval");
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
    void stepWithRows() throws Exception {
        Table t = Table.read()
                .csv(CsvReadOptions.builder("../data/bush.csv").minimizeColumnSizes())
                .first(6);

        final int sum1 = (int) t.shortColumn("approval").sum();

        RowConsumer rowConsumer = new RowConsumer();
        t.stepWithRows(rowConsumer, 3);
        assertEquals(sum1, rowConsumer.getSum());

    }

    private static class RowConsumer implements Consumer<Row[]> {

        private int sum = 0;

        public int getSum() {
            return sum;
        }

        @Override
        public void accept(Row[] rows) {
            for (int i = 0; i < 3; i++) {
                sum += rows[i].getShort("approval");
            }
        }
    }

    @Test
    void testRollWithNrows2() throws Exception {
        Table t = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").minimizeColumnSizes()).first(4);
        ShortColumn approval = t.shortColumn("approval");

        List<Integer> sums = new ArrayList<>();
        Consumer<Row[]> rowConsumer = rows -> {
            int sum = 0;
            for (Row row : rows) {
                sum += row.getShort("approval");
            }
            sums.add(sum);
        };
        t.rollWithRows(rowConsumer,2);
        assertTrue(sums.contains((int) approval.getDouble(0) + (int) approval.getDouble(1)));
        assertTrue(sums.contains((int) approval.getDouble(1) + (int) approval.getDouble(2)));
        assertTrue(sums.contains((int) approval.getDouble(2) + (int) approval.getDouble(3)));
    }

    private class PairChild implements Table.Pairs {

        private List<Double> runningAverage = new ArrayList<>();

        @Override
        public void doWithPair(Row row1, Row row2) {
            double r1  = row1.getShort("approval");
            double r2  = row2.getShort("approval");
            runningAverage.add((r1 + r2) / 2.0);
        }
    }

    @Test
    void testRowCount() {
        assertEquals(0, table.rowCount());
        DoubleColumn floatColumn = this.f1;
        floatColumn.append(2f);
        assertEquals(1, table.rowCount());
        floatColumn.append(2.2342f);
        assertEquals(2, table.rowCount());
    }

    @Test
    void testAppend() {
        int appendedRows = appendRandomlyGeneratedColumn(table);
        assertTableColumnSize(table, f1, appendedRows);
    }

    @Test
    void testAppendEmptyTable() {
        appendEmptyColumn(table);
        assertTrue(table.isEmpty());
    }

    @Test
    void testAppendToNonEmptyTable() {
        populateColumn(f1);
        assertFalse(table.isEmpty());
        int initialSize = table.rowCount();
        int appendedRows = appendRandomlyGeneratedColumn(table);
        assertTableColumnSize(table, f1, initialSize + appendedRows);
    }

    @Test
    void testAppendEmptyTableToNonEmptyTable() {
        populateColumn(f1);
        assertFalse(table.isEmpty());
        int initialSize = table.rowCount();
        appendEmptyColumn(table);
        assertTableColumnSize(table, f1, initialSize);
    }

    @Test
    void testAppendMultipleColumns() {
        DoubleColumn column =  DoubleColumn.create("e1");
        table.addColumns(column);
        DoubleColumn first = f1.emptyCopy();
        DoubleColumn second = column.emptyCopy();
        int rowCount = RANDOM.nextInt(ROWS_BOUNDARY);
        int firstColumnSize = populateColumn(first, rowCount);
        int secondColumnSize = populateColumn(second, rowCount);
        Table tableToAppend = Table.create("populated", first, second);
        table.append(tableToAppend);
        assertTableColumnSize(table, f1, firstColumnSize);
        assertTableColumnSize(table, column, secondColumnSize);
    }

    void testAppendNull() {
        assertThrows(NullPointerException.class, () -> {
            table.append(null);
        });
    }

    void testAppendTableWithNonExistingColumns() {
        assertThrows(IllegalStateException.class, () -> {
            Table tableToAppend = Table.create("wrong", numberColumn);
            table.append(tableToAppend);
        });
    }

    void testAppendTableWithAnotherColumnName() {
        assertThrows(IllegalStateException.class, () -> {
            DoubleColumn column =  DoubleColumn.create("42");
            Table tableToAppend = Table.create("wrong", column);
            table.append(tableToAppend);
        });
    }

    void testAppendTableWithDifferentShape() {
        assertThrows(IllegalStateException.class, () -> {
            DoubleColumn column =  DoubleColumn.create("e1");
            table.addColumns(column);
            Table tableToAppend = Table.create("different", column);
            assertEquals(2, table.columns().size());
            assertEquals(1, tableToAppend.columns().size());
            table.append(tableToAppend);
        });
    }

    @Test
    void testReplaceColumn() {
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
        return populateColumn(floatColumn, rowsCount);
    }

    private int populateColumn(DoubleColumn floatColumn, int rowsCount) {
        for (int i = 0; i < rowsCount; i++) {
            floatColumn.append(RANDOM.nextFloat());
        }
        assertEquals(floatColumn.size(), rowsCount);
        return rowsCount;
    }

    @Test
    void testAsMatrix() {
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
    void testRowSort() throws Exception {
        Table bush = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").minimizeColumnSizes());

        Comparator<Row> rowComparator = Comparator.comparingDouble(o -> o.getShort("approval"));

        Table sorted = bush.sortOn(rowComparator);
        ShortColumn approval = sorted.shortColumn("approval");
        for (int i = 0; i < bush.rowCount() - 2; i++) {
            assertTrue(approval.get(i) <= approval.get(i + 1));
        }
    }

    @Test
    void testIterable() throws Exception {
        Table bush = Table.read().csv("../data/bush.csv");
        int rowNumber = 0;
        for (Row row : bush.first(10)) {
            assertEquals(row.getRowNumber(), rowNumber++);
        }
    }

    @Test
    void testCountBy1() throws Exception {
        Table bush = Table.read().csv("../data/bush.csv");
        Table result = bush.countBy(bush.categoricalColumn("who"));
        assertEquals(bush.categoricalColumn("who").countUnique(), result.rowCount());
    }

    @Test
    void testCountBy2() throws Exception {
        Table bush = Table.read().csv("../data/bush.csv");
        Table result = bush.countBy("who");
        assertEquals(bush.categoricalColumn("who").countUnique(), result.rowCount());
    }

    @Test
    void dropRangeStarting() throws IOException {
        Table table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv"));
        Table result = table.dropRange(20);
        assertEquals(table.rowCount() - 20, result.rowCount());
        for (Column<?> c: result.columns()) {
            for (int r = 0; r < result.rowCount(); r++) {
                assertEquals(result.getString(r, c.name()), table.getString(r + 20, c.name()));
            }
        }
    }

    @Test
    void dropRangeEnding() throws IOException {
        Table table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv"));
        Table result = table.dropRange(-20);
        assertEquals(table.rowCount() - 20, result.rowCount());
        for (Column<?> c: result.columns()) {
            for (int r = 0; r < result.rowCount(); r++) {
                assertEquals(result.getString(r, c.name()), table.getString(r, c.name()));
            }
        }
    }    

    @Test
    void inRangeStarting() throws IOException {
        Table table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv"));
        Table result = table.inRange(20);
        assertEquals(20, result.rowCount());
        for (Column<?> c: result.columns()) {
            for (int r = 0; r < result.rowCount(); r++) {
                assertEquals(result.getString(r, c.name()), table.getString(r, c.name()));
            }
        }
    }

    @Test
    void inRangeEnding() throws IOException {
        Table table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv"));
        Table result = table.inRange(-20);
        assertEquals(20, result.rowCount());
        for (Column<?> c: result.columns()) {
            for (int r = 0; r < result.rowCount(); r++) {
                assertEquals(result.getString(r, c.name()), table.getString(table.rowCount() - 20 + r, c.name()));
            }
        }
    }  

    private DoubleColumn sum(DoubleColumn... columns) {
        int size = columns[0].size();
        DoubleColumn result = DoubleColumn.create("sum", size);
        for (int r = 0; r < size; r++) {
            double sum = 0;
            for (NumberColumn<Double> nc : columns) {
                sum += nc.get(r);
            }
            result.set(r, sum);
        }
        return result;
    }
}
