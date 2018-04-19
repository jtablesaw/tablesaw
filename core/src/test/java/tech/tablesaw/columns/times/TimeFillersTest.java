package tech.tablesaw.columns.times;

import static org.junit.Assert.assertEquals;
import static tech.tablesaw.api.TimeColumn.createWith;
import static tech.tablesaw.columns.datetimes.fillers.TemporalRangeIterable.range;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

public class TimeFillersTest {

    protected void testValues(Iterable<LocalTime> times, LocalTime... expected) {
        int num = 0;
        for (LocalTime value : times) {
            assertEquals(expected[num], value);
            num++;
        }
        assertEquals(expected.length, num);
    }

    @Test
    public void testFromToBy() {
        testValues(createWith("times", 5, range(LocalTime.of(12, 30), // hour, minute
                LocalTime.of(21, 30), 2, ChronoUnit.HOURS)), LocalTime.of(12, 30), // year, month, day, hour, minute
                LocalTime.of(14, 30), LocalTime.of(16, 30), LocalTime.of(18, 30), LocalTime.of(20, 30));
    }
}
