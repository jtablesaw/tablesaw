package tech.tablesaw.table;

import org.junit.Test;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static tech.tablesaw.aggregate.AggregateFunctions.*;
import static tech.tablesaw.api.NumberColumn.MISSING_VALUE;

public class RollingColumnTest {

    @Test
    public void testRollingMean() {
        double[] data = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] sma5 = new double[]{MISSING_VALUE, MISSING_VALUE, MISSING_VALUE, MISSING_VALUE, 3, 4, 5, 6, 7, 8};
        NumberColumn result = DoubleColumn.create("data", data).rolling(5).mean();
        assertArrayEquals(sma5, result.asDoubleArray(), 0.000001);
        assertEquals("dataMean5", result.name());
    }

    @Test
    public void testRollingMaxDate() {
        LocalDateTime[] data = new LocalDateTime[]{
                LocalDate.of(2011, 1, 1).atStartOfDay(),
                LocalDate.of(2011, 1, 3).atStartOfDay(),
                LocalDate.of(2011, 1, 5).atStartOfDay(),
                LocalDate.of(2011, 1, 7).atStartOfDay(),
                LocalDate.of(2011, 1, 9).atStartOfDay()
        };

        LocalDateTime[] sma5 = new LocalDateTime[]{
                null,
                LocalDate.of(2011, 1, 3).atStartOfDay(),
                LocalDate.of(2011, 1, 5).atStartOfDay(),
                LocalDate.of(2011, 1, 7).atStartOfDay(),
                LocalDate.of(2011, 1, 9).atStartOfDay()
        };

        DateTimeColumn result = (DateTimeColumn) DateTimeColumn.create("data", data).rolling(2).calc(latestDateTime);
        assertArrayEquals(sma5, result.asObjectArray());
    }

    @Test
    public void testRollingCountTrue() {
        Boolean[] data = new Boolean[]{true, false, false, true, true};

        DoubleColumn result = (DoubleColumn) BooleanColumn.create("data", data).rolling(2).calc(countTrue);

        assertEquals(Double.NaN, result.getDouble(0), 0.0);
        assertEquals(1, result.getDouble(1), 0.0);
        assertEquals(0, result.getDouble(2), 0.0);
        assertEquals(1, result.getDouble(3), 0.0);
        assertEquals(2, result.getDouble(4), 0.0);
    }
}
