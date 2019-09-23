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

package tech.tablesaw.io.saw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.tablesaw.api.ColumnType.TEXT;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

/** Tests for reading and writing saw files */
class SawStorageTest {

  private final Table empty = Table.create("empty table");

  private final Table noData =
      Table.create("no data", IntColumn.create("empty int"), DoubleColumn.create("empty double"));

  private final Table intsOnly =
      Table.create(
          "Ints only",
          IntColumn.indexColumn("index1", 100, 1),
          IntColumn.indexColumn("index2", 100, 1));

  private final Table intsAndStrings =
      Table.create("Ints and strings", IntColumn.indexColumn("index1", 100, 300));

  private final Table intsAndText =
      Table.create("Ints and text", IntColumn.indexColumn("index1", 100, 300));

  private static final int COUNT = 5;

  private static String tempDir = System.getProperty("java.io.tmpdir");
  private Table table = Table.create("t");
  private FloatColumn floatColumn = FloatColumn.create("float");
  private StringColumn categoryColumn = StringColumn.create("string");
  private DateColumn localDateColumn = DateColumn.create("date");
  private LongColumn longColumn = LongColumn.create("long");
  private BooleanColumn booleanColumn = BooleanColumn.create("bool");

  @BeforeEach
  void setUp() {

    intsAndStrings.addColumns(intsAndStrings.intColumn("index1").asStringColumn());
    intsAndText.addColumns(intsAndText.intColumn("index1").asStringColumn().asTextColumn());

    for (int i = 0; i < COUNT; i++) {
      floatColumn.append((float) i);
      localDateColumn.append(LocalDate.now());
      categoryColumn.append("Category " + i);
      longColumn.append(i);
      booleanColumn.append(i % 2 == 0);
    }
    table.addColumns(floatColumn);
    table.addColumns(localDateColumn);
    table.addColumns(categoryColumn);
    table.addColumns(longColumn);
    table.addColumns(booleanColumn);
  }

  @Test
  void testWriteTable() {
    SawWriter.saveTable(tempDir + "/zeta", table);
    Table t = SawReader.readTable(tempDir + "/zeta/t.saw");
    assertEquals(table.columnCount(), t.columnCount());
    assertEquals(table.rowCount(), t.rowCount());
    for (int i = 0; i < table.rowCount(); i++) {
      assertEquals(categoryColumn.get(i), t.stringColumn("string").get(i));
    }
    t.sortOn("string"); // exercise the column a bit
  }

  @Test
  void testWriteTable2() {

    SawWriter.saveTable(tempDir + "/zeta", table);
    Table t = SawReader.readTable(tempDir + "/zeta/t.saw");
    assertEquals(table.columnCount(), t.columnCount());
    assertEquals(table.rowCount(), t.rowCount());
    for (int i = 0; i < table.rowCount(); i++) {
      assertEquals(booleanColumn.get(i), t.booleanColumn("bool").get(i));
    }
    t.sortOn("string"); // exercise the column a bit
  }

  @Test
  void testWriteTableTwice() {

    SawWriter.saveTable(tempDir + "/mytables2", table);
    Table t = SawReader.readTable(tempDir + "/mytables2/t.saw");
    t.floatColumn("float").setName("a float column");

    SawWriter.saveTable(tempDir + "/mytables2", table);
    t = SawReader.readTable(tempDir + "/mytables2/t.saw");

    assertEquals(table.name(), t.name());
    assertEquals(table.rowCount(), t.rowCount());
    assertEquals(table.columnCount(), t.columnCount());
  }

  @Test
  void saveEmptyTable() {
    String path = SawWriter.saveTable("../testoutput", empty);
    Table table = SawReader.readTable(path);
    assertNotNull(table);
  }

  @Test
  void saveNoDataTable() {
    String path = SawWriter.saveTable("../testoutput", noData);
    Table table = SawReader.readTable(path);
    assertNotNull(table);
    assertTrue(table.columnCount() > 0);
    assertTrue(table.isEmpty());
  }

  @Test
  void saveIntsOnly() {
    String path = SawWriter.saveTable("../testoutput", intsOnly);
    Table table = SawReader.readTable(path);
    assertNotNull(table);
    assertEquals(intsOnly.rowCount(), table.rowCount());
  }

  @Test
  void saveIntsAndStrings() {
    String path = SawWriter.saveTable("../testoutput", intsAndStrings);
    Table table = SawReader.readTable(path);
    assertNotNull(table);
    assertEquals(intsAndStrings.rowCount(), table.rowCount());
  }

  @Test
  void saveIntsAndText() {
    String path = SawWriter.saveTable("../testoutput", intsAndText);
    Table table = SawReader.readTable(path);
    assertTrue(table.column(1).size() > 0);
    assertEquals(TEXT, table.column(1).type());
    assertEquals(intsAndText.rowCount(), table.rowCount());
  }

  @Test
  void bush() throws Exception {
    Table bush = Table.read().csv("../data/bush.csv");
    String path = SawWriter.saveTable("../testoutput/bush", bush);
    Table table = SawReader.readTable(path);
    assertEquals(table.column(1).size(), bush.rowCount());
  }

  @Test
  void tornado() throws Exception {
    Table tornado = Table.read().csv("../data/tornadoes_1950-2014.csv");
    String path = SawWriter.saveTable("../testoutput/tornadoes_1950-2014", tornado);
    Table table = SawReader.readTable(path);
    assertTrue(table.column(1).size() > 0);
    assertEquals(tornado.columnCount(), table.columnCount());
    assertEquals(tornado.rowCount(), table.rowCount());
  }

  @Test
  void baseball() throws Exception {
    Table baseball = Table.read().csv("../data/baseball.csv");
    String path = SawWriter.saveTable("../testoutput/baseball", baseball);
    Table table = SawReader.readTable(path);
    assertTrue(baseball.column(1).size() > 0);
    assertEquals(baseball.columnCount(), table.columnCount());
    assertEquals(baseball.rowCount(), table.rowCount());
  }

  @Test
  void boston_roberies() throws Exception {
    Table robereries = Table.read().csv("../data/boston-robberies.csv");
    String path = SawWriter.saveTable("../testoutput/boston_robberies", robereries);
    Table table = SawReader.readTable(path);
    assertEquals(robereries.columnCount(), table.columnCount());
    assertEquals(robereries.rowCount(), table.rowCount());
  }

  @Test
  void sacramento() throws Exception {
    Table sacramento = Table.read().csv("../data/sacramento_real_estate_transactions.csv");
    String path = SawWriter.saveTable("../testoutput/sacramento", sacramento);
    Table table = SawReader.readTable(path);
    assertEquals(sacramento.columnCount(), table.columnCount());
    assertEquals(sacramento.rowCount(), table.rowCount());
  }

  @Test
  void test_wines() throws Exception {
    Table wines = Table.read().csv("../data/test_wines.csv");
    String path = SawWriter.saveTable("../testoutput/test_wines", wines);
    Table table = SawReader.readTable(path);
    assertEquals(wines.columnCount(), table.columnCount());
    assertEquals(wines.rowCount(), table.rowCount());
  }

  @Test
  void saveIntsLarger() {
    final Table intsOnlyLarger =
        Table.create(
            "Ints only, larger",
            IntColumn.indexColumn("index1", 10_000_000, 1),
            IntColumn.indexColumn("index2", 10_000_000, 1));
    String path = SawWriter.saveTable("../testoutput", intsOnlyLarger);
    Table table = SawReader.readTable(path);
    assertEquals(10_000_000, table.rowCount());
  }
}
