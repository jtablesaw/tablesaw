package tech.tablesaw.interpolation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.columns.numbers.DoubleColumnType;

public class InterpolatorTest {

  private static final double missing = DoubleColumnType.missingValueIndicator();

  @Test
  public void testFrontfill() {
    DoubleColumn col =
        (DoubleColumn)
            DoubleColumn.create(
                    "testCol",
                    new double[] {missing, missing, 0.181, 0.186, missing, missing, 0.181})
                .interpolate()
                .frontfill();
    assertArrayEquals(
        new double[] {missing, missing, 0.181, 0.186, 0.186, 0.186, 0.181}, col.asDoubleArray());
  }

  @Test
  public void testBackfill() {
    DoubleColumn col =
        (DoubleColumn)
            DoubleColumn.create(
                    "testCol",
                    new double[] {missing, missing, 0.181, 0.186, missing, 0.181, missing})
                .interpolate()
                .backfill();
    assertArrayEquals(
        new double[] {0.181, 0.181, 0.181, 0.186, 0.181, 0.181, missing}, col.asDoubleArray());
  }
}
