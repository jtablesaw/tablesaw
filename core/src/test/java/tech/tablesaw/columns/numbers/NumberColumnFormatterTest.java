package tech.tablesaw.columns.numbers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class NumberColumnFormatterTest {

  @Test
  public void testFormatLong() {
    long value = 1588838400007002844L;
    assertEquals(Long.toString(value), NumberColumnFormatter.ints().format(value));
  }
}
