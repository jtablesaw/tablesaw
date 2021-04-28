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

import static org.junit.jupiter.api.Assertions.*;
import static tech.tablesaw.aggregate.AggregateFunctions.mean;
import static tech.tablesaw.aggregate.AggregateFunctions.stdDev;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.dates.PackedLocalDate;
import tech.tablesaw.columns.numbers.IntColumnType;
import tech.tablesaw.io.csv.CsvReadOptions;

public class TableTest {

  private static final String LINE_END = System.lineSeparator();
  private static final int ROWS_BOUNDARY = 1000;
  private static final Random RANDOM = new Random();

  private Table table;
  private final DoubleColumn f1 = DoubleColumn.create("f1");
  private final DoubleColumn numberColumn = DoubleColumn.create("d1");

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
    assertEquals(
        "4.580805569368441",
        result.where(result.stringColumn("state").isEqualTo("AL")).doubleColumn(1).getString(0));
  }

  @Test
  void testColumn() {
    Column<?> column1 = table.column(0);
    assertNotNull(column1);
  }

  @Test
  void reorderColumns() throws Exception {
    Table t = Table.read().csv("../data/bush.csv");
    List<String> names = t.columnNames();
    assertEquals(names.get(0), "date");
    assertEquals(names.get(1), "approval");
    assertEquals(names.get(2), "who");

    Table reordered = t.reorderColumns("who", "approval", "date");
    List<String> reorderedNames = reordered.columnNames();

    assertEquals(reorderedNames.get(0), "who");
    assertEquals(reorderedNames.get(1), "approval");
    assertEquals(reorderedNames.get(2), "date");
  }

  @Test
  void testColumnSizeCheck() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          double[] a = {3, 4};
          double[] b = {3, 4, 5};
          Table.create("test", DoubleColumn.create("a", a), DoubleColumn.create("b", b));
        });
  }

  @Test
  void testRowWiseAddition() {
    double[] a = {3, 4, 5};
    double[] b = {3, 4, 5};
    double[] c = {3, 4, 5};
    Table t =
        Table.create(
            "test",
            DoubleColumn.create("a", a),
            DoubleColumn.create("b", b),
            DoubleColumn.create("c", c));

    DoubleColumn n = t.doubleColumn(0).add(t.doubleColumn(1)).add(t.doubleColumn(2));

    assertEquals(9, n.get(0), 0);
    assertEquals(12, n.get(1), 0);
    assertEquals(15, n.get(2), 0);
  }

  @Test
  void testRowWiseAddition2() {
    double[] a = {3, 4, 5};
    double[] b = {3, 4, 5};
    double[] c = {3, 4, 5};
    Table t =
        Table.create(
            "test",
            DoubleColumn.create("a", a),
            DoubleColumn.create("b", b),
            DoubleColumn.create("c", c));

    DoubleColumn n = sum(t.doubleColumn("a"), t.doubleColumn("b"), t.doubleColumn("c"));

    assertEquals(9, n.get(0), 0);
    assertEquals(12, n.get(1), 0);
    assertEquals(15, n.get(2), 0);
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
  void appendPopulatedColumnToEmptyTable() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          table.addColumns(StringColumn.create("test").append("test"));
        });
  }

  @Test
  void appendEmptyColumnToPopulatedTable() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          table.doubleColumn("f1").append(23);
          table.addColumns(StringColumn.create("test"));
        });
  }

  @Test
  void dropDuplicateRows() throws Exception {
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
  void dropDuplicateRowsWithMissingValue() throws Exception {
    // Add 4 rows to the table, two of which are duplicates and have missing values.
    int missing = IntColumnType.missingValueIndicator();
    Table t1 =
        Table.create(
            "T1",
            IntColumn.create("Id", 0, 1, 2, 1),
            StringColumn.create("Name", "Joe", "Jay", "Mike", "Jay"),
            IntColumn.create("ChildId", 100, missing, 101, missing));

    Table t2 = t1.dropDuplicateRows();
    assertEquals(3, t2.rowCount());
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
    int stratifiedFoxCount =
        results[0].where(results[0].stringColumn("who").equalsIgnoreCase("fox")).rowCount();

    assertEquals(.75, (double) stratifiedFoxCount / totalFoxCount, 0.0);
  }

  @Test
  void testDoWithEachRow() throws Exception {
    Table t =
        Table.read()
            .csv(CsvReadOptions.builder("../data/bush.csv").minimizeColumnSizes())
            .first(10);
    Short[] ratingsArray = {53, 58};
    List<Short> ratings = Lists.asList((short) 52, ratingsArray);

    Consumer<Row> doable =
        row -> {
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
    Consumer<Row> doable =
        row -> {
          if (row.getPackedDate("date") > dateTarget && row.getShort("approval") > ratingTarget) {
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
    Predicate<Row> doable =
        row -> (row.getPackedDate("date") > dateTarget && row.getShort("approval") > ratingTarget);
    assertTrue(t.stream().anyMatch(doable));
  }

  @Test
  void testRowToString() throws Exception {
    Table t = Table.read().csv("../data/bush.csv");
    Row row = new Row(t);
    row.at(0);
    assertEquals(
        "             bush.csv              "
            + LINE_END
            + "    date     |  approval  |  who  |"
            + LINE_END
            + "-----------------------------------"
            + LINE_END
            + " 2004-02-04  |        53  |  fox  |",
        row.toString());
  }

  @Test
  void stepWithRows() throws Exception {
    Table t =
        Table.read().csv(CsvReadOptions.builder("../data/bush.csv").minimizeColumnSizes()).first(6);

    final int sum1 = (int) t.shortColumn("approval").sum();

    RowConsumer rowConsumer = new RowConsumer();
    t.steppingStream(3).forEach(rowConsumer);
    assertEquals(sum1, rowConsumer.getSum());
  }

  @Test
  void melt() throws Exception {
    boolean dropMissing = false;
    String df =
        "subject, time, age, weight, height"
            + LINE_END
            + "John Smith,    1,  33,     90,   1.87"
            + LINE_END
            + "Mary Smith,    1,  NA,     NA,   1.54";
    StringReader reader = new StringReader(df);
    Table t = Table.read().csv(reader);
    t.columnNames();
    List<String> ids = ImmutableList.of("subject", "time");
    List<NumericColumn<?>> measures = t.numericColumns("age", "weight", "height");

    Table melted = t.melt(ids, measures, dropMissing);
    assertEquals(
        "                                              "
            + LINE_END
            + "  subject    |  time  |  variable  |  value  |"
            + LINE_END
            + "----------------------------------------------"
            + LINE_END
            + " John Smith  |     1  |       age  |     33  |"
            + LINE_END
            + " John Smith  |     1  |    weight  |     90  |"
            + LINE_END
            + " John Smith  |     1  |    height  |   1.87  |"
            + LINE_END
            + " Mary Smith  |     1  |       age  |         |"
            + LINE_END
            + " Mary Smith  |     1  |    weight  |         |"
            + LINE_END
            + " Mary Smith  |     1  |    height  |   1.54  |",
        melted.toString());
  }

  @Test
  void meltAndDropMissing() throws Exception {
    boolean dropMissing = true;
    String df =
        "subject, time, age, weight, height"
            + LINE_END
            + "John Smith,    1,  33,     90,   1.87"
            + LINE_END
            + "Mary Smith,    1,  NA,     NA,   1.54";
    StringReader reader = new StringReader(df);
    Table t = Table.read().csv(reader);
    t.columnNames();
    List<String> ids = ImmutableList.of("subject", "time");
    List<NumericColumn<?>> measures = t.numericColumns("age", "weight", "height");

    Table melted = t.melt(ids, measures, dropMissing);
    melted.write().csv("../data/molten_smiths_drop_missing.csv");
    assertEquals(
        "                                              "
            + LINE_END
            + "  subject    |  time  |  variable  |  value  |"
            + LINE_END
            + "----------------------------------------------"
            + LINE_END
            + " John Smith  |     1  |       age  |     33  |"
            + LINE_END
            + " John Smith  |     1  |    weight  |     90  |"
            + LINE_END
            + " John Smith  |     1  |    height  |   1.87  |"
            + LINE_END
            + " Mary Smith  |     1  |    height  |   1.54  |",
        melted.toString());
  }

  @Test
  void cast() throws IOException {
    Table molten = Table.read().csv("../data/molten_smiths.csv");
    Table cast = molten.cast();
    StringWriter writer = new StringWriter();
    cast.write().csv(writer);
    String writeString = writer.toString();
    assertEquals(
        "subject,time,weight,age,height"
            + LINE_END
            + "John Smith,1,90.0,33.0,1.87"
            + LINE_END
            + "Mary Smith,1,,,1.54"
            + LINE_END,
        writeString);
  }

  @Test
  void castWithDropMissing() throws IOException {
    Table molten = Table.read().csv("../data/molten_smiths_drop_missing.csv");
    Table cast = molten.cast();
    StringWriter writer = new StringWriter();
    cast.write().csv(writer);
    String writeString = writer.toString();
    assertEquals(
        "subject,time,weight,age,height"
            + LINE_END
            + "John Smith,1,90.0,33.0,1.87"
            + LINE_END
            + "Mary Smith,1,,,1.54"
            + LINE_END,
        writeString);
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
    Table t =
        Table.read().csv(CsvReadOptions.builder("../data/bush.csv").minimizeColumnSizes()).first(4);
    ShortColumn approval = t.shortColumn("approval");

    List<Integer> sums = new ArrayList<>();
    Consumer<Row[]> rowConsumer =
        rows -> {
          int sum = 0;
          for (Row row : rows) {
            sum += row.getShort("approval");
          }
          sums.add(sum);
        };
    t.rollingStream(2).forEach(rowConsumer);
    assertTrue(sums.contains((int) approval.getDouble(0) + (int) approval.getDouble(1)));
    assertTrue(sums.contains((int) approval.getDouble(1) + (int) approval.getDouble(2)));
    assertTrue(sums.contains((int) approval.getDouble(2) + (int) approval.getDouble(3)));
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
  void testAppendRow() throws Exception {
    Table table = Table.read().csv("../data/bush.csv");
    for (int i = 0; i < 2; i++) {
      Row row = table.appendRow();
      row.setString("who", "me");
      row.setDate("date", LocalDate.now());
      row.setInt("approval", 5);
    }
    assertEquals(5, table.intColumn("approval").get(table.rowCount() - 1));
  }

  @Test
  void testAppendMultipleColumns() {
    DoubleColumn column = DoubleColumn.create("e1");
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
    assertThrows(
        NullPointerException.class,
        () -> {
          table.append(null);
        });
  }

  void testAppendTableWithNonExistingColumns() {
    assertThrows(
        IllegalStateException.class,
        () -> {
          Table tableToAppend = Table.create("wrong", numberColumn);
          table.append(tableToAppend);
        });
  }

  void testAppendTableWithAnotherColumnName() {
    assertThrows(
        IllegalStateException.class,
        () -> {
          DoubleColumn column = DoubleColumn.create("42");
          Table tableToAppend = Table.create("wrong", column);
          table.append(tableToAppend);
        });
  }

  void testAppendTableWithDifferentShape() {
    assertThrows(
        IllegalStateException.class,
        () -> {
          DoubleColumn column = DoubleColumn.create("e1");
          table.addColumns(column);
          Table tableToAppend = Table.create("different", column);
          assertEquals(2, table.columns().size());
          assertEquals(1, tableToAppend.columns().size());
          table.append(tableToAppend);
        });
  }

  @Test
  void testReplaceColumn() {
    DoubleColumn first = DoubleColumn.create("c1", new double[] {1, 2, 3, 4, 5});
    DoubleColumn second = DoubleColumn.create("c2", new double[] {6, 7, 8, 9, 10});
    DoubleColumn replacement = DoubleColumn.create("c2", new double[] {10, 20, 30, 40, 50});

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
    DoubleColumn first = DoubleColumn.create("c1", new double[] {1L, 2L, 3L, 4L, 5L});
    DoubleColumn second = DoubleColumn.create("c2", new double[] {6.0f, 7.0f, 8.0f, 9.0f, 10.0f});
    DoubleColumn third = DoubleColumn.create("c3", new double[] {10.0, 20.0, 30.0, 40.0, 50.0});

    Table t = Table.create("table", first, second, third);
    double[][] matrix = t.as().doubleMatrix();
    assertEquals(5, matrix.length);
    assertArrayEquals(new double[] {1.0, 6.0, 10.0}, matrix[0], 0.0000001);
    assertArrayEquals(new double[] {2.0, 7.0, 20.0}, matrix[1], 0.0000001);
    assertArrayEquals(new double[] {3.0, 8.0, 30.0}, matrix[2], 0.0000001);
    assertArrayEquals(new double[] {4.0, 9.0, 40.0}, matrix[3], 0.0000001);
    assertArrayEquals(new double[] {5.0, 10.0, 50.0}, matrix[4], 0.0000001);
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
    for (Column<?> c : result.columns()) {
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
    for (Column<?> c : result.columns()) {
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
    for (Column<?> c : result.columns()) {
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
    for (Column<?> c : result.columns()) {
      for (int r = 0; r < result.rowCount(); r++) {
        assertEquals(
            result.getString(r, c.name()), table.getString(table.rowCount() - 20 + r, c.name()));
      }
    }
  }

  private DoubleColumn sum(DoubleColumn... columns) {
    int size = columns[0].size();
    DoubleColumn result = DoubleColumn.create("sum", size);
    for (int r = 0; r < size; r++) {
      double sum = 0;
      for (DoubleColumn nc : columns) {
        sum += nc.get(r);
      }
      result.set(r, sum);
    }
    return result;
  }

  @Test
  void ambiguousMethodCallError() {
    StringColumn s1 = StringColumn.create("1", "1", "2", "3");
    StringColumn s2 = StringColumn.create("2", "2", "2", "2");
    StringColumn s3 = StringColumn.create("3", "3", "2", "1");
    IntColumn s4 = IntColumn.create("4", 1, 2, 3);
    Table t = Table.create("t", s3, s2, s1, s4);
    assertDoesNotThrow(() -> t.where(t.intColumn("4").isIn((int) 1, (int) 2)));
  }

  @Test
  public void testToStringColumnsWithVaryingSizes() {
    IntColumn col11 = IntColumn.create("col1");
    IntColumn col12 = IntColumn.create("col2");
    Table t1 = Table.create("t1", col11, col12);
    col11.append(1).append(2);
    col12.append(1);
    try {
      assertNotNull(t1.toString());
    } catch (Exception e) {
      fail("toString shouldn't throw " + e);
    }
  }
}
