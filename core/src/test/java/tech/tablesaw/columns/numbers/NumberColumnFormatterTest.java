package tech.tablesaw.columns.numbers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.ColumnType;

public class NumberColumnFormatterTest {

  @Test
  public void testFormatLong() {
    long value = 1588838400007002844L;
    NumberColumnFormatter ncf = NumberColumnFormatter.ints();
    ncf.setColumnType(ColumnType.LONG);
    assertEquals(Long.toString(value), ncf.format(value));
  }
}
