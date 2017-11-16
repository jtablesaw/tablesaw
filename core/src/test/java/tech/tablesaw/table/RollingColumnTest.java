package tech.tablesaw.table;

import static org.junit.Assert.assertArrayEquals;
import static tech.tablesaw.api.DoubleColumn.MISSING_VALUE;

import org.junit.Test;

import tech.tablesaw.api.DoubleColumn;

public class RollingColumnTest {

  @Test
  public void testRollingMean() {
    double[] data = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
    double[] sma5 = new double[] { MISSING_VALUE, MISSING_VALUE, MISSING_VALUE, MISSING_VALUE, 3, 4, 5, 6, 7, 8 };
    assertArrayEquals(sma5, new DoubleColumn("data", data).rolling(5).mean("sma").toDoubleArray(), 0.000001);
  }

}
