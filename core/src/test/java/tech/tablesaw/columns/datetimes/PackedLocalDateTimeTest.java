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

package tech.tablesaw.columns.datetimes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static tech.tablesaw.columns.datetimes.PackedLocalDateTime.asLocalDateTime;
import static tech.tablesaw.columns.datetimes.PackedLocalDateTime.date;
import static tech.tablesaw.columns.datetimes.PackedLocalDateTime.getDayOfMonth;
import static tech.tablesaw.columns.datetimes.PackedLocalDateTime.getDayOfWeek;
import static tech.tablesaw.columns.datetimes.PackedLocalDateTime.getHour;
import static tech.tablesaw.columns.datetimes.PackedLocalDateTime.getMillisecondOfDay;
import static tech.tablesaw.columns.datetimes.PackedLocalDateTime.getMinute;
import static tech.tablesaw.columns.datetimes.PackedLocalDateTime.getMinuteOfDay;
import static tech.tablesaw.columns.datetimes.PackedLocalDateTime.getMonthValue;
import static tech.tablesaw.columns.datetimes.PackedLocalDateTime.getSecond;
import static tech.tablesaw.columns.datetimes.PackedLocalDateTime.getSecondOfDay;
import static tech.tablesaw.columns.datetimes.PackedLocalDateTime.getYear;
import static tech.tablesaw.columns.datetimes.PackedLocalDateTime.ofEpochMilli;
import static tech.tablesaw.columns.datetimes.PackedLocalDateTime.pack;
import static tech.tablesaw.columns.datetimes.PackedLocalDateTime.time;
import static tech.tablesaw.columns.datetimes.PackedLocalDateTime.toEpochMilli;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

import org.junit.jupiter.api.Test;

import tech.tablesaw.columns.dates.PackedLocalDate;
import tech.tablesaw.columns.times.PackedLocalTime;

public class PackedLocalDateTimeTest {

    @Test
    public void testGetDayOfMonth() {
        LocalDateTime today = LocalDateTime.now();
        assertEquals(today.getDayOfMonth(),
                getDayOfMonth(pack(today)));
    }

    @Test
    public void testGetYear() {
        LocalDateTime today = LocalDateTime.now();
        assertEquals(today.getYear(), getYear(pack(today)));
    }

    @Test
    public void testAsLocalDateTime() {
        LocalDateTime dateTime = LocalDateTime.now();
        long packed = pack(dateTime.toLocalDate(), dateTime.toLocalTime());
        LocalDateTime upacked = asLocalDateTime(packed);
        assertEquals(dateTime.getDayOfYear(), upacked.getDayOfYear());
        assertEquals(dateTime.getHour(), upacked.getHour());
        assertEquals(dateTime.getMinute(), upacked.getMinute());
        assertEquals(dateTime.getSecond(), upacked.getSecond());
    }

    @Test
    public void testGetMonthValue() {
        long dateTime = pack(LocalDate.of(2015, 12, 25), LocalTime.now());
        assertEquals(12, getMonthValue(dateTime));
    }

    @Test
    public void testPack() {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

        long packed = pack(date, time);

        LocalDate d1 = PackedLocalDate.asLocalDate(date(packed));
        LocalTime t1 = PackedLocalTime.asLocalTime(time(packed));
        assertNotNull(d1);
        assertNotNull(t1);
        assertEquals(date.toString(), d1.toString());
    }

    @Test
    public void testGetHour() {
        LocalDateTime now = LocalDateTime.now();
        assertEquals(now.getHour(), getHour(pack(now)));
    }

    @Test
    public void testGetMinute() {
        LocalDateTime now = LocalDateTime.now();
        assertEquals(now.getMinute(), getMinute(pack(now)));
    }

    @Test
    public void testGetSecond() {
        LocalDateTime now = LocalDateTime.now();
        assertEquals(now.getSecond(), getSecond(pack(now)));
    }

    @Test
    public void testGetSecondOfDay() {
        LocalDateTime now = LocalDateTime.now();
        assertEquals(now.get(ChronoField.SECOND_OF_DAY), getSecondOfDay(pack(now)), 0.0001);
    }

    @Test
    public void testGetMinuteOfDay() {
        LocalDateTime now = LocalDateTime.now();
        assertEquals(now.get(ChronoField.MINUTE_OF_DAY), getMinuteOfDay(pack(now)));
    }

    @Test
    public void testGetMillisecondOfDay() {
        LocalDateTime now = LocalDateTime.now();
        assertEquals(now.get(ChronoField.MILLI_OF_DAY), getMillisecondOfDay(pack(now)));
    }

    @Test
    public void testGetDayOfWeek() {
        LocalDateTime now = LocalDateTime.now();
        assertEquals(now.get(ChronoField.DAY_OF_WEEK), getDayOfWeek(pack(now)).getValue());
    }

    @Test
    public void testToEpochMillis() {
        long now = pack(LocalDateTime.now());
        long millis = toEpochMilli(now, ZoneOffset.UTC);
        long now2 = ofEpochMilli(millis, ZoneId.of("UTC"));
        assertEquals(now, now2);
    }
}