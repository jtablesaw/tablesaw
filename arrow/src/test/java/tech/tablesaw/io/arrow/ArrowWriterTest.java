package tech.tablesaw.io.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

class ArrowWriterTest {

  private static final String tempDir = System.getProperty("java.io.tmpdir");
  Table bush = Table.read().csv("../data/bush.csv");

  @BeforeEach
  void setUp() {
    // System.out.println(bush.structure());
  }

  @Test
  void write() {
    ArrowWriter writer = new ArrowWriter();
    File f = Paths.get(tempDir, "bush.arrows").toFile();
    writer.write(bush, f);

    ArrowReader reader = new ArrowReader(f);
    Table result = reader.read();
    assertEquals(result.rowCount(), bush.rowCount());
    assertEquals(result.columnCount(), bush.columnCount());
    assertEquals(result.row(1).getString("who"), bush.row(1).getString("who"));
    assertEquals(result.row(1).getInt("approval"), bush.row(1).getInt("approval"));
    assertEquals(result.row(1).getDate("date"), bush.row(1).getDate("date"));
  }

  @Test
  void write2() {
    ArrowWriter writer = new ArrowWriter();
    Table table =
        Table.read().csv(CsvReadOptions.builder(new File("../data/tornadoes_1950-2014.csv")));

    System.out.println(table);
    File f = Paths.get(tempDir, "tornado.arrows").toFile();
    writer.write(table, f);

    ArrowReader reader = new ArrowReader(f);
    Table result = reader.read();
    System.out.println(result);
    assertEquals(result.rowCount(), table.rowCount());
    assertEquals(result.columnCount(), table.columnCount());
    /*
        assertEquals(result.row(1).getString("who"), bush.row(1).getString("who"));
        assertEquals(result.row(1).getInt("approval"), bush.row(1).getInt("approval"));
        assertEquals(result.row(1).getDate("date"), bush.row(1).getDate("date"));
    */
  }
}
