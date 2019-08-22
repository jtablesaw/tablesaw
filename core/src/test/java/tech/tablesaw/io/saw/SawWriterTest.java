package tech.tablesaw.io.saw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.tablesaw.api.ColumnType.TEXT;

import com.google.common.base.Stopwatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;

class SawWriterTest {

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

  @BeforeEach
  void setUp() {
    intsAndStrings.addColumns(intsAndStrings.intColumn("index1").asStringColumn());
    intsAndText.addColumns(intsAndText.intColumn("index1").asStringColumn().asTextColumn());
  }

  @Test
  void saveEmptyTable() {
    String path = SawWriter.saveTable("../testoutput", empty);
    Table table = SawReader.readTable(path);
    System.out.println(table);
  }

  @Test
  void saveNoDataTable() {
    String path = SawWriter.saveTable("../testoutput", noData);
    Table table = SawReader.readTable(path);
    System.out.println(table);
  }

  @Test
  void saveIntsOnly() {
    String path = SawWriter.saveTable("../testoutput", intsOnly);
    Table table = SawReader.readTable(path);
    System.out.println(table);
  }

  @Test
  void saveIntsAndStrings() {
    String path = SawWriter.saveTable("../testoutput", intsAndStrings);
    Table table = SawReader.readTable(path);
    System.out.println(table);
  }

  @Test
  void saveIntsAndText() {
    String path = SawWriter.saveTable("../testoutput", intsAndText);
    Table table = SawReader.readTable(path);
    assertTrue(table.column(1).size() > 0);
    assertEquals(TEXT, table.column(1).type());
    System.out.println(table);
  }

  @Test
  void bush() throws Exception {
    Table bush = Table.read().csv("../data/bush.csv");
    String path = SawWriter.saveTable("../testoutput/bush", bush);
    Table table = SawReader.readTable(path);
    assertTrue(table.column(1).size() > 0);
    System.out.println(table);
  }

  @Test
  void tornado() throws Exception {
    Table tornado = Table.read().csv("../data/tornadoes_1950-2014.csv");
    String path = SawWriter.saveTable("../testoutput/tornadoes_1950-2014", tornado);
    Table table = SawReader.readTable(path);
    assertTrue(table.column(1).size() > 0);
    System.out.println(table);
  }

  @Test
  void baseball() throws Exception {
    Table table = Table.read().csv("../data/baseball.csv");
    String path = SawWriter.saveTable("../testoutput/baseball", table);
    table = SawReader.readTable(path);
    assertTrue(table.column(1).size() > 0);
    System.out.println(table);
  }

  @Test
  void boston_roberies() throws Exception {
    Table table = Table.read().csv("../data/boston-robberies.csv");
    String path = SawWriter.saveTable("../testoutput/boston_robberies", table);
    table = SawReader.readTable(path);
    assertTrue(table.column(1).size() > 0);
    System.out.println(table);
  }

  @Test
  void sacramento() throws Exception {
    Table table = Table.read().csv("../data/sacramento_real_estate_transactions.csv");
    String path = SawWriter.saveTable("../testoutput/sacramento", table);
    table = SawReader.readTable(path);
    assertTrue(table.column(1).size() > 0);
    System.out.println(table);
  }

  @Test
  void test_wines() throws Exception {
    Table table = Table.read().csv("../data/test_wines.csv");
    String path = SawWriter.saveTable("../testoutput/test_wines", table);
    table = SawReader.readTable(path);
    assertTrue(table.column(1).size() > 0);
    System.out.println(table);
  }

  @Test
  void saveIntsLarger() {

    Stopwatch stopwatch = Stopwatch.createStarted();
    final Table intsOnlyLarger =
        Table.create(
            "Ints only, larger",
            IntColumn.indexColumn("index1", 100_000_000, 1),
            IntColumn.indexColumn("index2", 100_000_000, 1));
    System.out.println("created " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    String path = SawWriter.saveTable("../testoutput", intsOnlyLarger);
    System.out.println("saved " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    Table table = SawReader.readTable(path);
    System.out.println("read " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    System.out.println(table);
  }
}
