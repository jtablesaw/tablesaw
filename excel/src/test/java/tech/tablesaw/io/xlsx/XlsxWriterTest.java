package tech.tablesaw.io.xlsx;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class XlsxWriterTest {

  @Test
  public void testXlsxWriter() throws IOException {
    Table table = Table.create("Employees")
        .addColumns(
            IntColumn.create("id", 1,2,3),
            DateColumn.create("start_date", toLocalDates("2020-01-10", "2021-12-01", "2023-04-20")),
            DoubleColumn.create("salary", 6723.5, 4879, 5512.8)
        );
    File file = File.createTempFile(table.name(), ".xlsx");
    table.write().usingOptions(XlsxWriteOptions.builder(file).build());

    Table table2 = Table.read().usingOptions(XlsxReadOptions.builder(file)
        .columnTypes(new ColumnType[]{ColumnType.INTEGER, ColumnType.STRING, ColumnType.DOUBLE}
    ).build());
    assertEquals(3, table2.rowCount(), "Number of rows");
    assertEquals(3, table2.columnCount(), "Number of columns");
    assertArrayEquals(new String[]{"id", "start_date", "salary"}, table2.columnNames().toArray(new String[]{}), "Column names");
    assertEquals(1, table2.get(0, 0), "First row. first column");
    assertEquals("2021-12-01", table2.get(1, 1), "Second row, second column");
    assertEquals(5512.8, table2.get(2, 2), "Third row, third column");
    file.deleteOnExit();
  }

  LocalDate[] toLocalDates(String... dates) {
    LocalDate[] d = new LocalDate[dates.length];
    for (int i = 0; i < dates.length; i++) {
      d[i] = LocalDate.parse(dates[i]);
    }
    return d;
  }
}
