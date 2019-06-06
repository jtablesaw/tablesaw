package tech.tablesaw.api;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import tech.tablesaw.columns.numbers.DoubleColumnType;

public class NumericColumnTest {

    @Test
    public void testPctChange() {
        double[] data = new double[]{ 100, 100, 100, 100, 101, 102, 99, 98 };
        double missing = DoubleColumnType.missingValueIndicator();
        double[] pctChange = new double[]{ missing, missing, missing, missing, .01, .02, -.01, -.02 };
        DoubleColumn result = DoubleColumn.create("data", data).pctChange(4);
        assertArrayEquals(pctChange, result.asDoubleArray(), 0.000001);
        assertEquals("data 4-period Percent Change", result.name());
    }

}
