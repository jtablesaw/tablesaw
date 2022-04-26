package tech.tablesaw.io.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.*;
import tech.tablesaw.io.csv.CsvReadOptions;

class ArrowWriterTest {

  private static final String tempDir = System.getProperty("java.io.tmpdir");
  private final Table bush = Table.read().csv("../data/bush.csv");

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

    File f = Paths.get(tempDir, "tornado.arrows").toFile();
    writer.write(table, f);

    ArrowReader reader = new ArrowReader(f);
    Table result = reader.read();
    assertEquals(result.rowCount(), table.rowCount());
    assertEquals(result.columnCount(), table.columnCount());
  }

  @Test
  void write3() {
    ArrowWriter writer = new ArrowWriter();
    Table table =
        Table.create(
            "test",
            StringColumn.create("0"),
            BooleanColumn.create("1"),
            IntColumn.create("2"),
            LongColumn.create("3"),
            ShortColumn.create("4"),
            FloatColumn.create("5"),
            DoubleColumn.create("6"),
            DateTimeColumn.create("7"),
            DateColumn.create("8"),
            TimeColumn.create("9"),
            InstantColumn.create("10"));

    Row r = table.appendRow();
    r.setString(0, "test");
    r.setBoolean(1, true);
    r.setInt(2, 2);
    r.setLong(3, 3L);
    r.setShort(4, (short) 4);
    r.setFloat(5, 5.0f);
    r.setDouble(6, 6.0);
    r.setDateTime(7, LocalDateTime.of(2022, 2, 2, 2, 22, 22));
    r.setDate(8, LocalDate.of(2022, 2, 2));
    r.setTime(9, LocalTime.NOON);
    r.setInstant(10, Instant.EPOCH);

    File f = Paths.get(tempDir, "allDb.arrows").toFile();
    writer.write(table, f);

    ArrowReader reader = new ArrowReader(f);
    Table result = reader.read();
    assertEquals(result.rowCount(), table.rowCount());
    assertEquals(result.columnCount(), table.columnCount());
  }
}
