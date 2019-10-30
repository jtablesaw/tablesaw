package tech.tablesaw.columns.numbers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.csv.CsvReader;

public class DoubleParserTest {

  @Test
  public void testPercentage() throws IOException {
    Table table =
        new CsvReader().read(CsvReadOptions.builder("../data/growth.csv").percentage(true).build());
    assertEquals("Coffee", table.get(0, 0));
    assertEquals(0.25, table.get(0, 1));
    assertEquals("Bread", table.get(1, 0));
    assertEquals(0.05, table.get(1, 1));
  }
}
