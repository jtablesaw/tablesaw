package tech.tablesaw.columns.numbers;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;

public class NumberInterpolatorTest {

  private static final double missing = DoubleColumnType.missingValueIndicator();

  @Test
  public void linear() {
    DoubleColumn col =
        DoubleColumn.create(
                "testCol", new double[] {missing, 1.0, missing, missing, missing, 5.0, missing})
            .interpolate()
            .linear();
    assertArrayEquals(
        new double[] {missing, 1.0, 2.0, 3.0, 4.0, 5.0, missing}, col.asDoubleArray());
  }
}
