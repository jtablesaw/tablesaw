package tech.tablesaw.columns.dates;

import org.junit.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;
import static tech.tablesaw.api.DateColumn.create;
import static tech.tablesaw.columns.datetimes.fillers.TemporalRangeIterable.range;

public class DateFillersTest {

    protected void testValues(Iterable<LocalDate> times, LocalDate... expected) {
        int num = 0;
        for (LocalDate value : times) {
            assertEquals(expected[num], value);
            num++;
        }
        assertEquals(expected.length, num);
    }

    @Test
    public void testFromToBy() {
        testValues(
                create("dates", new LocalDate[5])
                .fillWith(
                        range(LocalDate.of(2018, 3, 1), // year, month, day
                    LocalDate.of(2019, 3, 1), 1, ChronoUnit.DAYS)),
                LocalDate.of(2018, 3, 1), // year, month, day
                LocalDate.of(2018, 3, 2), LocalDate.of(2018, 3, 3),
                LocalDate.of(2018, 3, 4), LocalDate.of(2018, 3, 5));

        testValues(create("dates", new LocalDate[5])
                .fillWith(range(LocalDate.of(2018, 3, 1), // year, month, day
                LocalDate.of(2019, 3, 1), 1, ChronoUnit.MONTHS)), LocalDate.of(2018, 3, 1), // year, month, day
                LocalDate.of(2018, 4, 1), LocalDate.of(2018, 5, 1), LocalDate.of(2018, 6, 1), LocalDate.of(2018, 7, 1));
    }
}
