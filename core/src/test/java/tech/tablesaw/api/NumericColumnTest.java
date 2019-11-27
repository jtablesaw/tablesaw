package tech.tablesaw.api;

import org.junit.jupiter.api.Test;
import tech.tablesaw.columns.numbers.DoubleColumnType;

import static java.lang.Double.NaN;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

  @Test
  void testAutoCorrelation() {
    double[] sample =
        new double[] {0.397, 0.157, -0.083, -0.243, -0.323, -0.243, -0.083, 0.077, 0.347};
    DoubleColumn x = DoubleColumn.create("x", sample);
    double roundOff = (double) Math.round(x.autoCorrelation() * 100) / 100;
    double expected = 0.64;
    assertEquals(expected, roundOff);

    roundOff = (double) Math.round(x.autoCorrelation(4) * 100) / 100;
    expected = -0.93;
    assertEquals(expected, roundOff);

    double actual = x.autoCorrelation(8);
    expected = NaN;
    assertEquals(expected, actual);

    double[] sample2 = new double[] {0, 0, 0, 0};
    DoubleColumn y = DoubleColumn.create("y", sample2);
    actual = y.autoCorrelation();
    expected = NaN;
    assertEquals(expected, actual);
  }
}
