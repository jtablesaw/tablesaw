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

import static tech.tablesaw.columns.datetimes.DateTimeColumnType.missingValueIndicator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.chrono.IsoChronology;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

import tech.tablesaw.columns.dates.PackedLocalDate;
import tech.tablesaw.columns.instant.PackedInstant;
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
public class PackedLocalDateTime extends PackedInstant {

    private PackedLocalDateTime() {}

    public static byte getDayOfMonth(long date) {
        return (byte) date(date);  // last byte
    }

    public static short getYear(long dateTime) {
        return PackedLocalDate.getYear(date(dateTime));
    }

    public static LocalDateTime asLocalDateTime(long dateTime) {
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
        return LocalDateTime.of(d, t);
    }

    public static byte getMonthValue(long dateTime) {
        int date = date(dateTime);
        return (byte) (date >> 8);
    }

    public static long pack(LocalDate date, LocalTime time) {
        if (date == null || time == null) {
            return missingValueIndicator();
        }
        int d = PackedLocalDate.pack(date);
        int t = PackedLocalTime.pack(time);
        return (((long) d) << 32) | (t & 0xffffffffL);
    }

    public static long pack(LocalDateTime dateTime) {
        if (dateTime == null) {
            return missingValueIndicator();
        }
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        return (pack(date, time));
    }

    public static long pack(short yr, byte m, byte d, byte hr, byte min, byte s, byte n) {
        int date = PackedLocalDate.pack(yr, m, d);

        int time = Ints.fromBytes(hr, min, s, n);

        return (((long) date) << 32) | (time & 0xffffffffL);
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
                        + Strings.padStart(String.valueOf(PackedLocalTime.getMilliseconds(time)), 3, '0');
    }

    public static int getDayOfYear(long packedDateTime) {
        return getMonth(packedDateTime).firstDayOfYear(isLeapYear(packedDateTime)) + getDayOfMonth(packedDateTime) - 1;
    }

    public static int getWeekOfYear(long packedDateTime) {
        LocalDateTime date = asLocalDateTime(packedDateTime);
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        return date.get(woy);
    }

    public static boolean isLeapYear(long packedDateTime) {
        if (packedDateTime == missingValueIndicator()) return false;
        return IsoChronology.INSTANCE.isLeapYear(getYear(packedDateTime));
    }

    public static Month getMonth(long packedDateTime) {
        return Month.of(getMonthValue(packedDateTime));
    }

    public static int lengthOfMonth(long packedDateTime) {
        switch (getMonthValue(packedDateTime)) {
            case 2:
                return (isLeapYear(packedDateTime) ? 29 : 28);
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
        }
    }

    public static DayOfWeek getDayOfWeek(long packedDateTime) {
        int date = PackedLocalDateTime.date(packedDateTime);
        return PackedLocalDate.getDayOfWeek(date);
    }

    /**
     * Returns the quarter of the year of the given date as an int from 1 to 4, or -1, if the argument is the
     * missingValueIndicator() for DateTimeColumn
     */
    public static int getQuarter(long packedDate) {
        if (packedDate == missingValueIndicator()) {
            return -1;
        }
        return PackedLocalDate.getQuarter(date(packedDate));
    }

    public static boolean isInQ1(long packedDateTime) {
        if (packedDateTime == missingValueIndicator()) return false;
        Month month = getMonth(packedDateTime);
        return month == Month.JANUARY ||
                month == Month.FEBRUARY ||
                month == Month.MARCH;
    }

    public static boolean isInQ2(long packedDateTime) {
        if (packedDateTime == missingValueIndicator()) return false;
        Month month = getMonth(packedDateTime);
        return month == Month.APRIL ||
                month == Month.MAY ||
                month == Month.JUNE;
    }

    public static boolean isInQ3(long packedDateTime) {
        if (packedDateTime == missingValueIndicator()) return false;
        Month month = getMonth(packedDateTime);
        return month == Month.JULY ||
                month == Month.AUGUST ||
                month == Month.SEPTEMBER;
    }

    public static boolean isInQ4(long packedDateTime) {
        if (packedDateTime == missingValueIndicator()) return false;
        Month month = getMonth(packedDateTime);
        return month == Month.OCTOBER ||
                month == Month.NOVEMBER ||
                month == Month.DECEMBER;
    }

    public static boolean isAfter(long packedDateTime, long value) {
        return (packedDateTime != missingValueIndicator()) && packedDateTime > value;
    }

    public static boolean isBefore(long packedDateTime, long value) {
        return (packedDateTime != missingValueIndicator()) && packedDateTime < value;
    }

    public static boolean isSunday(long packedDateTime) {
        if (packedDateTime == missingValueIndicator()) return false;
        DayOfWeek dayOfWeek = getDayOfWeek(packedDateTime);
        return dayOfWeek == DayOfWeek.SUNDAY;
    }

    public static boolean isMonday(long packedDateTime) {
        if (packedDateTime == missingValueIndicator()) return false;
        DayOfWeek dayOfWeek = getDayOfWeek(packedDateTime);
        return dayOfWeek == DayOfWeek.MONDAY;
    }

    public static boolean isTuesday(long packedDateTime) {
        if (packedDateTime == missingValueIndicator()) return false;
        DayOfWeek dayOfWeek = getDayOfWeek(packedDateTime);
        return dayOfWeek == DayOfWeek.TUESDAY;
    }

    public static boolean isWednesday(long packedDateTime) {
        if (packedDateTime == missingValueIndicator()) return false;
        DayOfWeek dayOfWeek = getDayOfWeek(packedDateTime);
        return dayOfWeek == DayOfWeek.WEDNESDAY;
    }

    public static boolean isThursday(long packedDateTime) {
        if (packedDateTime == missingValueIndicator()) return false;
        DayOfWeek dayOfWeek = getDayOfWeek(packedDateTime);
        return dayOfWeek == DayOfWeek.THURSDAY;
    }

    public static boolean isFriday(long packedDateTime) {
        if (packedDateTime == missingValueIndicator()) return false;
        DayOfWeek dayOfWeek = getDayOfWeek(packedDateTime);
        return dayOfWeek == DayOfWeek.FRIDAY;
    }

    public static boolean isSaturday(long packedDateTime) {
        if (packedDateTime == missingValueIndicator()) return false;
        DayOfWeek dayOfWeek = getDayOfWeek(packedDateTime);
        return dayOfWeek == DayOfWeek.SATURDAY;
    }

    public static boolean isFirstDayOfMonth(long packedDateTime) {
        return (packedDateTime != missingValueIndicator()) && getDayOfMonth(packedDateTime) == 1;
    }

    public static boolean isInJanuary(long packedDateTime) {
        return (packedDateTime != missingValueIndicator()) && getMonth(packedDateTime) == Month.JANUARY;
    }

    public static boolean isInFebruary(long packedDateTime) {
        return (packedDateTime != missingValueIndicator()) && getMonth(packedDateTime) == Month.FEBRUARY;
    }

    public static boolean isInMarch(long packedDateTime) {
        return (packedDateTime != missingValueIndicator()) && getMonth(packedDateTime) == Month.MARCH;
    }

    public static boolean isInApril(long packedDateTime) {
        return (packedDateTime != missingValueIndicator()) && getMonth(packedDateTime) == Month.APRIL;
    }

    public static boolean isInMay(long packedDateTime) {
        return (packedDateTime != missingValueIndicator()) && getMonth(packedDateTime) == Month.MAY;
    }

    public static boolean isInJune(long packedDateTime) {
        return (packedDateTime != missingValueIndicator()) && getMonth(packedDateTime) == Month.JUNE;
    }

    public static boolean isInJuly(long packedDateTime) {
        return (packedDateTime != missingValueIndicator()) && getMonth(packedDateTime) == Month.JULY;
    }

    public static boolean isInAugust(long packedDateTime) {
        return (packedDateTime != missingValueIndicator()) && getMonth(packedDateTime) == Month.AUGUST;
    }

    public static boolean isInSeptember(long packedDateTime) {
        return (packedDateTime != missingValueIndicator()) && getMonth(packedDateTime) == Month.SEPTEMBER;
    }

    public static boolean isInOctober(long packedDateTime) {
        return (packedDateTime != missingValueIndicator()) && getMonth(packedDateTime) == Month.OCTOBER;
    }

    public static boolean isInNovember(long packedDateTime) {
        return (packedDateTime != missingValueIndicator()) && getMonth(packedDateTime) == Month.NOVEMBER;
    }

    public static boolean isInDecember(long packedDateTime) {
        return (packedDateTime != missingValueIndicator()) && getMonth(packedDateTime) == Month.DECEMBER;
    }

    public static boolean isLastDayOfMonth(long packedDateTime) {
        return (packedDateTime != missingValueIndicator()) && getDayOfMonth(packedDateTime) == lengthOfMonth(packedDateTime);
    }

    public static boolean isInYear(long packedDateTime, int year) {
        return (packedDateTime != missingValueIndicator()) && getYear(packedDateTime) == year;
    }

    public static boolean isMidnight(long packedDateTime) {
        return (packedDateTime != missingValueIndicator()) && PackedLocalTime.isMidnight(time(packedDateTime));
    }

    public static boolean isNoon(long packedDateTime) {
        return (packedDateTime != missingValueIndicator()) && PackedLocalTime.isNoon(time(packedDateTime));
    }

    /**
     * Returns true if the time is in the AM or "before noon".
     * Note: we follow the convention that 12:00 NOON is PM and 12 MIDNIGHT is AM
     */
    public static boolean AM(long packedDateTime) {
        return (packedDateTime != missingValueIndicator()) && PackedLocalTime.AM(time(packedDateTime));
    }

    /**
     * Returns true if the time is in the PM or "after noon".
     * Note: we follow the convention that 12:00 NOON is PM and 12 MIDNIGHT is AM
     */
    public static boolean PM(long packedDateTime) {
        return (packedDateTime != missingValueIndicator()) && PackedLocalTime.PM(time(packedDateTime));
    }

    public static int getMinuteOfDay(long packedLocalDateTime) {
        return getHour(packedLocalDateTime) * 60 + getMinute(packedLocalDateTime);
    }

    public static byte getSecond(int packedLocalDateTime) {
        return (byte) (getMillisecondOfMinute(packedLocalDateTime) / 1000);
    }

    public static byte getHour(long packedLocalDateTime) {
        return PackedLocalTime.getHour(time(packedLocalDateTime));
    }

    public static byte getMinute(long packedLocalDateTime) {
        return PackedLocalTime.getMinute(time(packedLocalDateTime));
    }

    public static byte getSecond(long packedLocalDateTime) {
        return PackedLocalTime.getSecond(time(packedLocalDateTime));
    }

    public static int getSecondOfDay(long packedLocalDateTime) {
        return PackedLocalTime.getSecondOfDay(time(packedLocalDateTime));
    }

    public static short getMillisecondOfMinute(long packedLocalDateTime) {
        return (short) PackedLocalTime.getMillisecondOfMinute(time(packedLocalDateTime));
    }

    public static long getMillisecondOfDay(long packedLocalDateTime) {
        LocalDateTime localDateTime = PackedLocalDateTime.asLocalDateTime(packedLocalDateTime);
        long total = (long) localDateTime.get(ChronoField.MILLI_OF_SECOND);
        total += localDateTime.getSecond() * 1000;
        total += localDateTime.getMinute() * 60 * 1000;
        total += localDateTime.getHour() * 60 * 60 * 1000;
        return total;
    }

    public static int lengthOfYear(long packedDateTime) {
        return (isLeapYear(packedDateTime) ? 366 : 365);
    }

    public static int monthsUntil(long packedDateTimeEnd, long packedDateStart) {

        int start = getMonthInternal(packedDateStart) * 32 + getDayOfMonth(packedDateStart);
        int end = getMonthInternal(packedDateTimeEnd) * 32 + getDayOfMonth(packedDateTimeEnd);
        return (end - start) / 32;
    }

    public static int yearsUntil(long packedDateEnd, long packedDateStart) {
        return monthsUntil(packedDateEnd, packedDateStart)/12;
    }

    private static int getMonthInternal(long packedDateTime) {
        return (getYear(packedDateTime) * 12 + getMonthValue(packedDateTime) - 1);
    }

}
