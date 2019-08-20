package tech.tablesaw.io.saw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

class SawWriterTest {

  private final Table empty = Table.create("empty table");
  private final Table noData = Table.create("no data", IntColumn.create("empty int"),
          DoubleColumn.create("empty double"));

  private final Table intsOnly = Table.create("Ints only",
          IntColumn.indexColumn("index1", 100, 1),
          IntColumn.indexColumn("index2", 100, 1));

  @BeforeEach
  void setUp() {

  }

  @Test
  void saveEmptyTable() {
    String path = SawWriter.saveTable("testoutput", empty);
    Table table = SawReader.readTable(path);
    System.out.println(table);
  }

  @Test
  void saveNoDataTable() {
    String path = SawWriter.saveTable("testoutput", noData);
    Table table = SawReader.readTable(path);
    System.out.println(table);

  }

  @Test
  void saveIntsOnly() {
    String path = SawWriter.saveTable("testoutput", intsOnly);
    Table table = SawReader.readTable(path);
    System.out.println(table);
  }

  @Test
  void saveIntsLarger() {

    Stopwatch stopwatch = Stopwatch.createStarted();
    final Table intsOnlyLarger = Table.create("Ints only, larger",
            IntColumn.indexColumn("index1", 100_000_000, 1),
            IntColumn.indexColumn("index2", 100_000_000, 1));
    System.out.println("created " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    String path = SawWriter.saveTable("testoutput", intsOnlyLarger);
    System.out.println("saved " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    Table table = SawReader.readTable(path);
    System.out.println("read " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    System.out.println(table);
  }
  
  @Test
  void saveIntsLarger2() throws Exception {

    Stopwatch stopwatch = Stopwatch.createStarted();
    Table intsOnlyLarger = Table.create("Ints only, larger",
            IntColumn.indexColumn("index1", 100_000_000, 1),
            IntColumn.indexColumn("index2", 100_000_000, 1));
    System.out.println("created " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    intsOnlyLarger.write().csv("test1");
    System.out.println("saved " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    intsOnlyLarger = Table.read().csv("test1");
    System.out.println("read " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    System.out.println(intsOnlyLarger);
  }
}