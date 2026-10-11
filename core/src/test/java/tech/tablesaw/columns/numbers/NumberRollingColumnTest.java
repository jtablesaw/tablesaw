package tech.tablesaw.columns.numbers;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;

public class NumberRollingColumnTest {

  @Test
  public void testRollingMean() {
    double[] data = new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    double missing = DoubleColumnType.missingValueIndicator();
    double[] sma5 = new double[] {missing, missing, missing, missing, 3, 4, 5, 6, 7, 8};
    DoubleColumn result = DoubleColumn.create("data", data).rolling(5).mean();
    assertArrayEquals(sma5, result.asDoubleArray(), 0.000001);
    assertEquals("data 5-period Mean", result.name());
  }

  @ParameterizedTest
  @ValueSource(ints = {4, 5, 10, Integer.MAX_VALUE})
  public void testWindowLargerThanColumnPreservesRowCount(int window) {
    DoubleColumn data = DoubleColumn.create("data", 10, 20, 30);
    DoubleColumn result = data.rolling(window).mean();
    double missing = DoubleColumnType.missingValueIndicator();

    assertArrayEquals(new double[] {missing, missing, missing}, result.asDoubleArray());
    assertEquals(data.size(), result.size());
    Table.create("data", data).addColumns(result);
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 10, Integer.MAX_VALUE})
  public void testEmptyColumnRemainsEmpty(int window) {
    assertEquals(0, DoubleColumn.create("data").rolling(window).sum().size());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
  public void testNonPositiveWindowIsRejected(int window) {
    assertThrows(
        IllegalArgumentException.class, () -> DoubleColumn.create("data", 10, 20).rolling(window));
  }

  @Test
  public void testWindowEqualToColumnSize() {
    double missing = DoubleColumnType.missingValueIndicator();
    DoubleColumn result = IntColumn.create("data", 10, 20, 30).rolling(3).sum();
    assertArrayEquals(new double[] {missing, missing, 60}, result.asDoubleArray());
  }

  @Test
  public void testSingleRowWindow() {
    double[] data = {10, 20, 30};
    assertArrayEquals(data, DoubleColumn.create("data", data).rolling(1).mean().asDoubleArray());
  }

  @Test
  public void testMissingValuesInsideCompleteWindows() {
    DoubleColumn data = DoubleColumn.create("data", 10, Double.NaN, 30, 40);
    double missing = DoubleColumnType.missingValueIndicator();

    assertArrayEquals(
        new double[] {missing, missing, 20, 35}, data.rolling(3).mean().asDoubleArray());
  }

  @Test
  public void testRollingMeanExcludingCurrentRow() {
    DoubleColumn data = DoubleColumn.create("sales", 10, 20, 30, 40, 50);
    DoubleColumn trailingMean = data.rolling(3).mean();
    double missing = DoubleColumnType.missingValueIndicator();

    assertArrayEquals(new double[] {missing, missing, 20, 30, 40}, trailingMean.asDoubleArray());
    assertArrayEquals(
        new double[] {missing, missing, missing, 20, 30}, trailingMean.lag(1).asDoubleArray());
    Table table = Table.create("Sales", data);
    table.addColumns(trailingMean);
    table.addColumns(trailingMean.lag(1).setName("previous 3-row mean"));
    assertEquals(5, table.rowCount());
    assertArrayEquals(new double[] {10, 20, 30, 40, 50}, data.asDoubleArray());
  }
}
