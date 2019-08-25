package tech.tablesaw.api;

import static java.lang.Double.NaN;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import tech.tablesaw.columns.numbers.DoubleColumnType;

class NumericColumnTest {

  @Test
  void testPctChange() {
    double[] data = new double[] {100, 100, 100, 100, 101, 102, 99, 98};
    double missing = DoubleColumnType.missingValueIndicator();
    double[] pctChange = new double[] {missing, missing, missing, missing, .01, .02, -.01, -.02};
    DoubleColumn result = DoubleColumn.create("data", data).pctChange(4);
    assertArrayEquals(pctChange, result.asDoubleArray(), 0.000001);
    assertEquals("data 4-period Percent Change", result.name());
  }

  @Test
  void testRemainder() {
    double[] data = new double[] {100, 101, NaN, 105};
    DoubleColumn result = DoubleColumn.create("data", data).remainder(20);
    double[] expected = {0, 1, NaN, 5};
    assertArrayEquals(expected, result.asDoubleArray());
  }
}
