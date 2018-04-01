/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.columns;

import org.junit.Test;
import tech.tablesaw.columns.datetimes.PackedLocalDateTime;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.List;

import static java.time.temporal.ChronoUnit.*;
import static tech.tablesaw.columns.times.PackedLocalTime.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for PackedLocalTime
 */
public class PackedLocalTimeTest {

    @Test
    public void testTruncatedTo() {
        List<LocalTime> times = List.of(
                LocalTime.of(5, 11, 24),
                LocalTime.of(21, 11, 24),
                LocalTime.MIDNIGHT,
                LocalTime.NOON,
                LocalTime.MIN,
                LocalTime.MAX);
        for (LocalTime time : times) {
            assertEquals(time.truncatedTo(SECONDS),
                    asLocalTime(truncatedTo(SECONDS, pack(time))));
            assertEquals(time.truncatedTo(MINUTES),
                    asLocalTime(truncatedTo(MINUTES, pack(time))));
            assertEquals(time.truncatedTo(HOURS),
                    asLocalTime(truncatedTo(HOURS, pack(time))));
            assertEquals(time.truncatedTo(HALF_DAYS),
                    asLocalTime(truncatedTo(HALF_DAYS, pack(time))));
            assertEquals(time.truncatedTo(DAYS),
                    asLocalTime(truncatedTo(DAYS, pack(time))));
        }
    }

    @Test
    public void testGetHour() {
        LocalTime now = LocalTime.now();
        assertEquals(now.getHour(), getHour(pack(now)));
    }

    @Test
    public void testGetMinute() {
        LocalTime now = LocalTime.now();
        assertEquals(now.getMinute(), getMinute(pack(now)));
    }

    @Test
    public void testGetSecond() {
        LocalTime now = LocalTime.now();
        assertEquals(now.getSecond(), getSecond(pack(now)));
    }

    @Test
    public void testGetSecondOfDay() {
        LocalTime now = LocalTime.now();
        assertEquals(now.get(ChronoField.SECOND_OF_DAY), getSecondOfDay(pack(now)), 0.0001);
    }

    @Test
    public void testGetMinuteOfDay() {
        LocalTime now = LocalTime.now();
        assertEquals(now.get(ChronoField.MINUTE_OF_DAY), getMinuteOfDay(pack(now)), 0.0001);
    }

    @Test
    public void testToNanoOfDay() {
        int pTime = of(7,18, 32, 232);
        LocalTime time = asLocalTime(pTime);
        assertEquals(time.getLong(ChronoField.NANO_OF_DAY), toNanoOfDay(pTime));
    }

    @Test
    public void testGetMillisecondOfDay() {
        LocalTime now = LocalTime.now();
        assertEquals(now.get(ChronoField.MILLI_OF_DAY), getMillisecondOfDay(pack(now)));
    }

    @Test
    public void testConstructors1() {
        LocalTime localTime = LocalTime.of(5, 11, 36);
        int packedTime = pack(localTime);

        int packedTime2 = of(5, 11, 36);

        assertEquals(
                getMillisecondOfDay(packedTime),
                getMillisecondOfDay(packedTime2)
                );
        assertEquals(localTime.getHour(), getHour(packedTime2));
        assertEquals(localTime.getMinute(), getMinute(packedTime2));
        assertEquals(localTime.getSecond(), getSecond(packedTime2));
    }

    @Test
    public void testConstructors2() {
        LocalTime localTime = LocalTime.of(5, 11);
        int packedTime = pack(localTime);

        int packedTime2 = of(5, 11);

        assertEquals(
                getMillisecondOfDay(packedTime),
                getMillisecondOfDay(packedTime2)
                );
        assertEquals(localTime.getHour(), getHour(packedTime2));
        assertEquals(localTime.getMinute(), getMinute(packedTime2));
        assertEquals(localTime.getSecond(), getSecond(packedTime2));
    }

    @Test
    public void testConstructors3() {
        LocalTime localTime = LocalTime.of(5, 11, 33, 811*1_000_000);
        int packedTime = pack(localTime);

        int packedTime2 = of(5, 11, 33, 811);

        assertEquals(
                getMillisecondOfDay(packedTime),
                getMillisecondOfDay(packedTime2)
                );
        check(localTime, packedTime2);
    }

    @Test
    public void testWithHour() {
        LocalTime localTime = LocalTime.of(5, 11, 33, 811*1_000_000);
        LocalTime localTime2 = localTime.withHour(7);

        int packedTime = pack(localTime);
        int packedTime2 = withHour(7, packedTime);
        check(localTime2, packedTime2);
    }

    @Test
    public void testWithMinute() {
        LocalTime localTime = LocalTime.of(5, 11, 33, 811*1_000_000);
        LocalTime localTime2 = localTime.withMinute(7);

        int packedTime = pack(localTime);
        int packedTime2 = withMinute(7, packedTime);
        check(localTime2, packedTime2);
    }

    @Test
    public void testWithSecond() {
        LocalTime localTime = LocalTime.of(5, 11, 33, 811*1_000_000);
        LocalTime localTime2 = localTime.withSecond(42);
        int packedTime = pack(localTime);
        int packedTime2 = withSecond(42, packedTime);
        check(localTime2, packedTime2);
    }

    @Test
    public void testPlusSeconds() {
        LocalTime localTime = LocalTime.of(5, 11, 33, 811*1_000_000);
        LocalTime localTime2 = localTime.plusSeconds(4340);

        int packedTime = pack(localTime);
        int packedTime2 = plusSeconds(4340, packedTime);
        check(localTime2, packedTime2);

        int packedTime3 = minusSeconds(4340, packedTime2);
        check(localTime, packedTime3);
    }

    @Test
    public void testPlusMinutes() {
        LocalTime localTime = LocalTime.of(5, 11, 33, 811*1_000_000);
        LocalTime localTime2 = localTime.plusMinutes(77);

        int packedTime = pack(localTime);
        int packedTime2 = plusMinutes(77, packedTime);
        check(localTime2, packedTime2);

        int packedTime3 = minusMinutes(77, packedTime2);
        check(localTime, packedTime3);
    }

    @Test
    public void testPlusHours() {
        LocalTime localTime = LocalTime.of(5, 11, 33, 811*1_000_000);
        LocalTime localTime2 = localTime.plusHours(3);

        int packedTime = pack(localTime);
        int packedTime2 = plusHours(3, packedTime);
        check(localTime2, packedTime2);

        int packedTime3 = minusHours(3, packedTime2);
        check(localTime, packedTime3);
    }

    @Test
    public void testPlusHours2() {
        LocalTime localTime = LocalTime.of(5, 11, 33, 811*1_000_000);
        LocalTime localTime2 = localTime.plusHours(20);

        int packedTime = pack(localTime);
        int packedTime2 = plusHours(20, packedTime);
        check(localTime2, packedTime2);

        int packedTime3 = minusHours(20, packedTime2);
        check(localTime, packedTime3);
    }

    @Test
    public void testPack() {
        LocalTime time = LocalTime.now();
        int packed = pack(time);

        LocalTime t1 = asLocalTime(PackedLocalDateTime.time(packed));
        assertNotNull(t1);
        assertEquals(time.getHour(), t1.getHour());
        assertEquals(time.getMinute(), t1.getMinute());
        assertEquals(time.getSecond(), t1.getSecond());
        assertEquals(time.get(ChronoField.MILLI_OF_SECOND), t1.get(ChronoField.MILLI_OF_SECOND));
    }

    private void check(LocalTime localTime2, int packedTime2) {
        assertEquals(localTime2.getHour(), getHour(packedTime2));
        assertEquals(localTime2.getMinute(), getMinute(packedTime2));
        assertEquals(localTime2.getSecond(), getSecond(packedTime2));
        assertEquals(localTime2.getNano(), getNano(packedTime2));
    }
}