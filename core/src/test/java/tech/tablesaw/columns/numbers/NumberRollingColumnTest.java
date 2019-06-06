package tech.tablesaw.columns.numbers;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import tech.tablesaw.api.DoubleColumn;

public class NumberRollingColumnTest {

    @Test
    public void testRollingMean() {
        double[] data = new double[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        double missing = DoubleColumnType.missingValueIndicator();
        double[] sma5 = new double[]{ missing, missing, missing, missing, 3, 4, 5, 6, 7, 8 };
        DoubleColumn result = DoubleColumn.create("data", data).rolling(5).mean();
        assertArrayEquals(sma5, result.asDoubleArray(), 0.000001);
        assertEquals("data 5-period Mean", result.name());
    }

}
