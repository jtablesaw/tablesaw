package tech.tablesaw.table;

import org.junit.Test;
import tech.tablesaw.api.NumberColumn;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static tech.tablesaw.api.NumberColumn.MISSING_VALUE;

public class RollingColumnTest {

    @Test
    public void testRollingMean() {
        double[] data = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] sma5 = new double[]{MISSING_VALUE, MISSING_VALUE, MISSING_VALUE, MISSING_VALUE, 3, 4, 5, 6, 7, 8};
        NumberColumn result = new NumberColumn("data", data).rolling(5).mean();
        assertArrayEquals(sma5, result.asDoubleArray(), 0.000001);
        assertEquals("dataMean5", result.name());
    }

}
