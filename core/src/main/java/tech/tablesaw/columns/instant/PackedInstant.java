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

package tech.tablesaw.columns.instant;

import static tech.tablesaw.columns.datetimes.DateTimeColumnType.missingValueIndicator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import com.google.common.base.Strings;

import tech.tablesaw.columns.dates.PackedLocalDate;
import tech.tablesaw.columns.datetimes.DateTimePredicates;
import tech.tablesaw.columns.times.PackedLocalTime;

/*
 * TODO(lwhite): Extend missing-value handling on predicates to DateColumn and TimeColumn
 *
 * TODO(lwhite): Handle missing values on non-boolean (predicate) methods
 */

/**
 * A short localdatetime packed into a single long value. The long is comprised of an int for the date and an int
 * for the time
 * <p>
 * The bytes are packed into the date int as:
 * First two bytes: short (year)
 * next byte (month of year)
 * last byte (day of month)
 * <p>
 * The bytes are packed into the time int as
 * First byte: hourOfDay
 * next byte: minuteOfHour
 * last two bytes (short): millisecond of minute
 * <p>
 * Storing the millisecond of minute in an short requires that we treat the short as if it were unsigned. Unfortunately,
 * Neither Java nor Guava provide unsigned short support so we use char, which is a 16-bit unsigned int to
 * store values of up to 60,000 milliseconds (60 secs * 1000)
 */
public class PackedInstant {

    protected PackedInstant() {}

    public static Instant asInstant(long dateTime) {
        if (dateTime == missingValueIndicator()) {
            return null;
        }
        int date = date(dateTime);
        int time = time(dateTime);
        LocalDate d = PackedLocalDate.asLocalDate(date);
        LocalTime t = PackedLocalTime.asLocalTime(time);
        if (d == null || t == null) {
            return null;
        }
        return LocalDateTime.of(d, t).toInstant(ZoneOffset.UTC);
    }

    protected static long pack(LocalDate date, LocalTime time) {
        if (date == null || time == null) {
            return missingValueIndicator();
        }
        int d = PackedLocalDate.pack(date);
        int t = PackedLocalTime.pack(time);
        return (((long) d) << 32) | (t & 0xffffffffL);
    }

    public static long pack(Instant instant) {
        if (instant == null) {
            return missingValueIndicator();
        }
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        return (pack(date, time));
    }

    public static int date(long packedDateTIme) {
        return (int) (packedDateTIme >> 32);
    }

    public static int time(long packedDateTIme) {
        return (int) packedDateTIme;
    }

    public static String toString(long dateTime) {
        if (dateTime == Long.MIN_VALUE) {
            return "";
        }
        int date = date(dateTime);
        int time = time(dateTime);

        return
                "" + PackedLocalDate.getYear(date)
                        + "-"
                        + Strings.padStart(Byte.toString(PackedLocalDate.getMonthValue(date)), 2, '0')
                        + "-"
                        + Strings.padStart(Byte.toString(PackedLocalDate.getDayOfMonth(date)), 2, '0')
                        + "T"
                        + Strings.padStart(Byte.toString(PackedLocalTime.getHour(time)), 2, '0')
                        + ":"
                        + Strings.padStart(Byte.toString(PackedLocalTime.getMinute(time)), 2, '0')
                        + ":"
                        + Strings.padStart(Byte.toString(PackedLocalTime.getSecond(time)), 2, '0')
                        + "."
                        + Strings.padStart(String.valueOf(PackedLocalTime.getMilliseconds(time)), 3, '0')
                        + "Z";
    }

    /**
     * Returns the given packedDateTime with amtToAdd of temporal units added
     *
     * TODO(lwhite): Replace with a native implementation that doesn't convert everything to LocalDateTime
     */
    public static long plus(long packedDateTime, long amountToAdd, TemporalUnit unit) {
        Instant dateTime = asInstant(packedDateTime);
        return pack(dateTime.plus(amountToAdd, unit));
    }

    public static boolean isAfter(long packedDateTime, long value) {
        return (packedDateTime != missingValueIndicator()) && packedDateTime > value;
    }

    public static boolean isBefore(long packedDateTime, long value) {
        return (packedDateTime != missingValueIndicator()) && packedDateTime < value;
    }

    public static long create(int date, int time) {
        return (((long) date) << 32) | (time & 0xffffffffL);
    }

    // TODO: packed support for minutesUntil and hoursUnit. These implementations are inefficient
    public static long minutesUntil(long packedDateTimeEnd, long packedDateTimeStart) {
        return ChronoUnit.MINUTES.between(asInstant(packedDateTimeStart),
                asInstant(packedDateTimeEnd));
    }

    public static long hoursUntil(long packedDateTimeEnd, long packedDateTimeStart) {
        return ChronoUnit.HOURS.between(asInstant(packedDateTimeStart), asInstant(packedDateTimeEnd));
    }

    public static int daysUntil(long packedDateTimeEnd, long packedDateTimeStart) {
        return (int) (PackedLocalDate.toEpochDay(date(packedDateTimeEnd))
                - PackedLocalDate.toEpochDay(date(packedDateTimeStart)));
    }

    public static int weeksUntil(long packedDateTimeEnd, long packedDateStart) {
        return daysUntil(packedDateTimeEnd, packedDateStart)/7;
    }

    public static boolean isEqualTo(long packedDateTime, long value) {
        return DateTimePredicates.isEqualTo.test(packedDateTime, value);
    }

    public static boolean isOnOrAfter(long valueToTest, long valueToTestAgainst) {
        return valueToTest >= valueToTestAgainst;
    }

    public static boolean isOnOrBefore(long valueToTest, long valueToTestAgainst) {
        return isBefore(valueToTest, valueToTestAgainst)
                || isEqualTo(valueToTest, valueToTestAgainst);
    }
}
