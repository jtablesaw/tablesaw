package tech.tablesaw.columns.datetimes;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.tablesaw.api.DateTimeColumn.create;
import static tech.tablesaw.columns.temporal.fillers.TemporalRangeIterable.range;

public class DateTimeFillersTest {

    private void assertContentEquals(Iterable<LocalDateTime> times, LocalDateTime... expected) {
        int num = 0;
        for (LocalDateTime value : times) {
            assertEquals(expected[num], value);
            num++;
        }
        assertEquals(expected.length, num);
    }

    @Test
    public void testFromToBy() {
        assertContentEquals(create("datetimes", new LocalDateTime[5]).fillWith(range(LocalDateTime.of(2018, 3, 1, 12, 30), // year, month, day, hour,
                                                                                          // minute
                LocalDateTime.of(2019, 3, 1, 12, 30), 1, ChronoUnit.DAYS)), LocalDateTime.of(2018, 3, 1, 12, 30), // year,
                                                                                                                  // month,
                                                                                                                  // day,
                                                                                                                  // hour,
                                                                                                                  // minute
                LocalDateTime.of(2018, 3, 2, 12, 30), LocalDateTime.of(2018, 3, 3, 12, 30),
                LocalDateTime.of(2018, 3, 4, 12, 30), LocalDateTime.of(2018, 3, 5, 12, 30));

        assertContentEquals(create("datetimes", new LocalDateTime[5]).fillWith(range(LocalDateTime.of(2018, 3, 1, 12, 30), // year, month, day, hour,
                                                                                          // minute
                LocalDateTime.of(2019, 3, 1, 12, 30), 1, ChronoUnit.MONTHS)), LocalDateTime.of(2018, 3, 1, 12, 30), // year,
                                                                                                                    // month,
                                                                                                                    // day,
                                                                                                                    // hour,
                                                                                                                    // minute
                LocalDateTime.of(2018, 4, 1, 12, 30), LocalDateTime.of(2018, 5, 1, 12, 30),
                LocalDateTime.of(2018, 6, 1, 12, 30), LocalDateTime.of(2018, 7, 1, 12, 30));
        assertContentEquals(create("datetimes", new LocalDateTime[5]).fillWith(range(LocalDateTime.of(2018, 3, 1, 12, 30), // year, month, day, hour,
                                                                                          // minute
                LocalDateTime.of(2019, 3, 1, 12, 30), 2, ChronoUnit.HOURS)), LocalDateTime.of(2018, 3, 1, 12, 30), // year,
                                                                                                                   // month,
                                                                                                                   // day,
                                                                                                                   // hour,
                                                                                                                   // minute
                LocalDateTime.of(2018, 3, 1, 14, 30), LocalDateTime.of(2018, 3, 1, 16, 30),
                LocalDateTime.of(2018, 3, 1, 18, 30), LocalDateTime.of(2018, 3, 1, 20, 30));
    }
}
