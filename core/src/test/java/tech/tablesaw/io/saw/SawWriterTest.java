package tech.tablesaw.io.saw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;

class SawWriterTest {

  private final Table empty = Table.create("empty table");
  private final Table noData = Table.create("no data", IntColumn.create("empty int"),
          DoubleColumn.create("empty double"));

  private final Table intsOnly = Table.create("numbers only",
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
    SawWriter.saveTable("testoutput", intsOnly);

  }
}