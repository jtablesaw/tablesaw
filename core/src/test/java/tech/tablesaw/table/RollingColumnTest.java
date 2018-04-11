package tech.tablesaw.table;

import org.junit.Assert;
import org.junit.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;

import static org.junit.Assert.*;
import static tech.tablesaw.api.NumberColumn.MISSING_VALUE;

public class RollingColumnTest {

    @Test
    public void testRollingMean() {
        double[] data = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] sma5 = new double[]{MISSING_VALUE, MISSING_VALUE, MISSING_VALUE, MISSING_VALUE, 3, 4, 5, 6, 7, 8};
        NumberColumn result = DoubleColumn.create("data", data).rolling(5).mean();
        assertArrayEquals(sma5, result.asDoubleArray(), 0.000001);
        Assert.assertEquals("dataMean5", result.name());
        System.out.println(result.print());
    }

}
