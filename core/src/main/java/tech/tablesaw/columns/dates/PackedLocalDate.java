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

package tech.tablesaw.columns.dates;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import tech.tablesaw.columns.numbers.IntColumnType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.chrono.IsoChronology;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.time.Month.APRIL;
import static java.time.Month.AUGUST;
import static java.time.Month.DECEMBER;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.JULY;
import static java.time.Month.JUNE;
import static java.time.Month.MARCH;
import static java.time.Month.MAY;
import static java.time.Month.NOVEMBER;
import static java.time.Month.OCTOBER;
import static java.time.Month.SEPTEMBER;
import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.YEAR;
import static tech.tablesaw.columns.DateAndTimePredicates.isEqualTo;
import static tech.tablesaw.columns.DateAndTimePredicates.isGreaterThanOrEqualTo;
import static tech.tablesaw.columns.DateAndTimePredicates.isLessThan;
import static tech.tablesaw.columns.DateAndTimePredicates.isLessThanOrEqualTo;

/**
 * A short localdate packed into a single int value. It uses a short for year so the range is about +-30,000 years
 * <p>
 * The bytes are packed into the int as:
 * First two bytes: short (year)
 * next byte (month of year)
 * last byte (day of month)
 */
public class PackedLocalDate {

    /**
     * The number of days in a 400 year cycle.
     */
    private static final int DAYS_PER_CYCLE = 146097;
    /**
     * The number of days from year zero to year 1970.
     * There are five 400 year cycles from year zero to 2000.
     * There are 7 leap years from 1970 to 2000.
     */
    private static final long DAYS_0000_TO_1970 = (DAYS_PER_CYCLE * 5L) - (30L * 365L + 7L);

    public static byte getDayOfMonth(int date) {
        return (byte) date;  // last byte
    }

    public static short getYear(int date) {
        // get first two bytes, then convert to a short
        byte byte1 = (byte) (date >> 24);
        byte byte2 = (byte) (date >> 16);
        return (short) ((byte1 << 8) + (byte2 & 0xFF));
    }

    public static LocalDate asLocalDate(int date) {
        if (date == IntColumnType.missingValueIndicator()) {
            return null;
        }

        return LocalDate.of(
                getYear(date),
                getMonthValue(date),
                getDayOfMonth(date));
    }

    public static byte getMonthValue(int date) {
        // get the third byte
        return (byte) (date >> 8);
    }

    public static int pack(LocalDate date) {
        if (date == null) {
            return DateColumnType.missingValueIndicator();
        }
        return pack(
                (short) date.getYear(),
                (byte) date.getMonthValue(),
                (byte) date.getDayOfMonth());
    }

    public static int pack(short yr, byte m, byte d) {
        byte byte1 = (byte) ((yr >> 8) & 0xff);
        byte byte2 = (byte) yr;
        return Ints.fromBytes(
                byte1,
                byte2,
                m,
                d);
    }

    public static int pack(int yr, int m, int d) {
        byte byte1 = (byte) ((yr >> 8) & 0xff);
        byte byte2 = (byte) yr;
        return Ints.fromBytes(
                byte1,
                byte2,
                (byte) m,
                (byte) d);
    }

    public static String toDateString(int date) {
        if (date == Integer.MIN_VALUE) {
            return "";
        }

        return getYear(date)
                + "-"
                + Strings.padStart(Byte.toString(getMonthValue(date)), 2, '0')
                + "-"
                + Strings.padStart(Byte.toString(getDayOfMonth(date)), 2, '0');
    }

    public static int getDayOfYear(int packedDate) {
        return getMonth(packedDate).firstDayOfYear(isLeapYear(packedDate)) + getDayOfMonth(packedDate) - 1;
    }

    public static boolean isLeapYear(int packedDate) {
        return IsoChronology.INSTANCE.isLeapYear(getYear(packedDate));
    }

    public static Month getMonth(int packedDate) {
        return Month.of(getMonthValue(packedDate));
    }

    public static int lengthOfMonth(int packedDate) {
        switch (getMonthValue(packedDate)) {
            case 2:
                return (isLeapYear(packedDate) ? 29 : 28);
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
        }
    }

    /**
     * Returns the epoch day in a form consistent with the java standard
     */
    public static long toEpochDay(int packedDate) {
        long y = getYear(packedDate);
        long m = getMonthValue(packedDate);
        long total = 0;
        total += 365 * y;
        if (y >= 0) {
            total += (y + 3) / 4 - (y + 99) / 100 + (y + 399) / 400;
        } else {
            total -= y / -4 - y / -100 + y / -400;
        }
        total += ((367 * m - 362) / 12);
        total += getDayOfMonth(packedDate) - 1;
        if (m > 2) {
            total--;
            if (!isLeapYear(packedDate)) {
                total--;
            }
        }
        return total - DAYS_0000_TO_1970;
    }

    public static DayOfWeek getDayOfWeek(int packedDate) {
        int dow0 = Math.floorMod((int) toEpochDay(packedDate) + 3, 7);
        return DayOfWeek.of(dow0 + 1);
    }

    /**
     * Returns the quarter of the year of the given date as an int from 1 to 4, or -1, if the argument is the
     * MISSING_VALUE for DateColumn
     */
    public static int getQuarter(int packedDate) {
        if (packedDate == DateColumnType.missingValueIndicator()) {
            return -1;
        }
        Month month = getMonth(packedDate);
        switch (month) {
            case JANUARY:
            case FEBRUARY:
            case MARCH:
                return 1;
            case APRIL:
            case MAY:
            case JUNE:
                return 2;
            case JULY:
            case AUGUST:
            case SEPTEMBER:
                return 3;
            case OCTOBER:
            case NOVEMBER:
            default:  // must be december
                return 4;
        }
    }

    public static boolean isInQ1(int packedDate) {
        return getQuarter(packedDate) == 1;
    }

    public static boolean isInQ2(int packedDate) {
        return getQuarter(packedDate) == 2;
    }

    public static boolean isInQ3(int packedDate) {
        return getQuarter(packedDate) == 3;
    }

    public static boolean isInQ4(int packedDate) {
        return getQuarter(packedDate) == 4;
    }

    public static boolean isAfter(int packedDate, int value) {
        return packedDate > value;
    }

    public static boolean isEqualTo(int packedDate, int value) {
        return isEqualTo.test(packedDate, value);
    }

    public static boolean isBefore(int packedDate, int value) {
        return isLessThan.test(packedDate, value);
    }

    public static boolean isOnOrBefore(int packedDate, int value) {
        return isLessThanOrEqualTo.test(packedDate, value);
    }

    public static boolean isOnOrAfter(int packedDate, int value) {
        return isGreaterThanOrEqualTo.test(packedDate, value);
    }

    public static boolean isDayOfWeek(int packedDate, DayOfWeek dayOfWeek) {
        DayOfWeek dow = getDayOfWeek(packedDate);
        return dayOfWeek == dow;
    }

    public static boolean isSunday(int packedDate) {
        return isDayOfWeek(packedDate, SUNDAY);
    }

    public static boolean isMonday(int packedDate) {
        return isDayOfWeek(packedDate, MONDAY);
    }

    public static boolean isTuesday(int packedDate) {
        return isDayOfWeek(packedDate, TUESDAY);
    }

    public static boolean isWednesday(int packedDate) {
        return isDayOfWeek(packedDate, WEDNESDAY);
    }

    public static boolean isThursday(int packedDate) {
        return isDayOfWeek(packedDate, THURSDAY);
    }

    public static boolean isFriday(int packedDate) {
        return isDayOfWeek(packedDate, FRIDAY);
    }

    public static boolean isSaturday(int packedDate) {
        return isDayOfWeek(packedDate, SATURDAY);
    }

    public static boolean isFirstDayOfMonth(int packedDate) {
        return getDayOfMonth(packedDate) == 1;
    }

    public static boolean isInJanuary(int packedDate) {
        return getMonth(packedDate) == JANUARY;
    }

    public static boolean isInFebruary(int packedDate) {
        return getMonth(packedDate) == FEBRUARY;
    }

    public static boolean isInMarch(int packedDate) {
        return getMonth(packedDate) == MARCH;
    }

    public static boolean isInApril(int packedDate) {
        return getMonth(packedDate) == APRIL;
    }

    public static boolean isInMay(int packedDate) {
        return getMonth(packedDate) == MAY;
    }

    public static boolean isInJune(int packedDate) {
        return getMonth(packedDate) == JUNE;
    }

    public static boolean isInJuly(int packedDate) {
        return getMonth(packedDate) == JULY;
    }

    public static boolean isInAugust(int packedDate) {
        return getMonth(packedDate) == AUGUST;
    }

    public static boolean isInSeptember(int packedDate) {
        return getMonth(packedDate) == SEPTEMBER;
    }

    public static boolean isInOctober(int packedDate) {
        return getMonth(packedDate) == OCTOBER;
    }

    public static boolean isInNovember(int packedDate) {
        return getMonth(packedDate) == NOVEMBER;
    }

    public static boolean isInDecember(int packedDate) {
        return getMonth(packedDate) == DECEMBER;
    }

    public static boolean isLastDayOfMonth(int packedDate) {
        return getDayOfMonth(packedDate) == lengthOfMonth(packedDate);
    }

    public static int withDayOfMonth(int dayOfMonth, int packedDate) {
        byte d = (byte) dayOfMonth;
        byte m = getMonthValue(packedDate);
        short y = getYear(packedDate);
        return pack(y, m, d);
    }

    public static int withMonth(int month, int packedDate) {
        byte day = getDayOfMonth(packedDate);
        byte _month = (byte) month;
        short year = getYear(packedDate);
        return pack(year, _month, day);
    }

    public static int withYear(int year, int packedDate) {
        byte day = getDayOfMonth(packedDate);
        byte month = getMonthValue(packedDate);
        short _year = (short) year;
        return pack(_year, month, day);
    }

    public static int plusYears(int yearsToAdd, int packedDate) {
        if (yearsToAdd == 0) {
            return packedDate;
        }
        byte d = getDayOfMonth(packedDate);
        byte m = getMonthValue(packedDate);
        short y = getYear(packedDate);

        int newYear = YEAR.checkValidIntValue(y + yearsToAdd);
        return resolvePreviousValid(newYear, m, d);
    }

    public static int minusYears(int years, int packedDate) {
        return plusYears(-years, packedDate);
    }

    public static int plusMonths(int months, int packedDate) {
        if (months == 0) {
            return packedDate;
        }

        byte d = getDayOfMonth(packedDate);
        byte m = getMonthValue(packedDate);
        short y = getYear(packedDate);

        long monthCount = y * 12L + (m - 1);
        long calcMonths = monthCount + months;
        int newYear = YEAR.checkValidIntValue(Math.floorDiv((int) calcMonths, 12));
        int newMonth = Math.floorMod((int) calcMonths, 12) + 1;
        return resolvePreviousValid(newYear, newMonth, d);
    }

    public static int minusMonths(int months, int packedDate) {
        return plusMonths(-months, packedDate);
    }

    public static int plusWeeks(int valueToAdd, int packedDate) {
        return plusDays(valueToAdd * 7, packedDate);
    }

    public static int minusWeeks(int valueToSubtract, int packedDate) {
        return minusDays(valueToSubtract * 7, packedDate);
    }

    public static int plusDays(int days, int packedDate) {
        if (days == 0) {
            return packedDate;
        }

        byte d = getDayOfMonth(packedDate);
        byte m = getMonthValue(packedDate);
        short y = getYear(packedDate);

        long dom = d + days;
        if (dom > 0) {
            if (dom <= 28) {
                return pack(y, m, (byte) dom);
            } else if (dom <= 59) { // 59th Jan is 28th Feb, 59th Feb is 31st Mar
                long monthLen = lengthOfMonth(packedDate);
                if (dom <= monthLen) {
                    return pack(y, m, (byte) dom);
                } else if (m < 12) {
                    return pack(y, (byte) (m + 1), (byte) (dom - monthLen));
                } else {
                    YEAR.checkValidValue(y + 1);
                    return pack((short) (y + 1), (byte) 1, (byte) (dom - monthLen));
                }
            }
        }

        long mjDay = Math.addExact(toEpochDay(packedDate), days);
        return ofEpochDay(mjDay);
    }

    public static int minusDays(int days, int packedDate) {
        return plusDays(-days, packedDate);
    }

    public static boolean isInYear(int next, int year) {
        return getYear(next) == year;
    }

    public static int lengthOfYear(int packedDate) {
        return (isLeapYear(packedDate) ? 366 : 365);
    }

    private static int resolvePreviousValid(int year, int month, int day) {
        int dayResult = day;
        switch (month) {
            case 2:
                dayResult = Math.min(day, IsoChronology.INSTANCE.isLeapYear(year) ? 29 : 28);
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                dayResult = Math.min(day, 30);
                break;
        }
        return pack((short) year, (byte) month, (byte) dayResult);
    }

    public static int getWeekOfYear(int packedDateTime) {
        LocalDate date = asLocalDate(packedDateTime);
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        return date.get(woy);
    }

    private static int ofEpochDay(long epochDay) {
        EPOCH_DAY.checkValidValue(epochDay);
        long zeroDay = epochDay + DAYS_0000_TO_1970;
        // find the march-based year
        zeroDay -= 60;  // adjust to 0000-03-01 so leap day is at end of four year cycle
        long adjust = 0;
        if (zeroDay < 0) {
            // adjust negative years to positive for calculation
            long adjustCycles = (zeroDay + 1) / DAYS_PER_CYCLE - 1;
            adjust = adjustCycles * 400;
            zeroDay += -adjustCycles * DAYS_PER_CYCLE;
        }
        long yearEst = (400 * zeroDay + 591) / DAYS_PER_CYCLE;
        long doyEst = zeroDay - (365 * yearEst + yearEst / 4 - yearEst / 100 + yearEst / 400);
        if (doyEst < 0) {
            // fix estimate
            yearEst--;
            doyEst = zeroDay - (365 * yearEst + yearEst / 4 - yearEst / 100 + yearEst / 400);
        }
        yearEst += adjust;  // reset any negative year
        int marchDoy0 = (int) doyEst;

        // convert march-based values back to january-based
        int marchMonth0 = (marchDoy0 * 5 + 2) / 153;
        int month = (marchMonth0 + 2) % 12 + 1;
        int dom = marchDoy0 - (marchMonth0 * 306 + 5) / 10 + 1;
        yearEst += marchMonth0 / 10;

        // check year now we are certain it is correct
        int year = YEAR.checkValidIntValue(yearEst);
        return pack((short) year, (byte) month, (byte) dom);
    }

    public static int plus(int valueToAdd, ChronoUnit unit, int packedDate) {
        switch (unit) {
            case YEARS: return plusYears(valueToAdd, packedDate);
            case MONTHS: return plusMonths(valueToAdd, packedDate);
            case WEEKS: return plusWeeks(valueToAdd, packedDate);
            case DAYS: return plusDays(valueToAdd, packedDate);
            default: throw new IllegalArgumentException("Unsupported Temporal Unit");
        }
    }

    public static int minus(int valueToAdd, ChronoUnit unit, int packedDate) {
        return plus(-valueToAdd, unit, packedDate);
    }

    public static int daysUntil(int packedDateEnd, int packedDateStart) {
        return (int) (toEpochDay(packedDateEnd) - toEpochDay(packedDateStart));
    }

    public static int weeksUntil(int packedDateEnd, int packedDateStart) {
        return (int) (toEpochDay(packedDateEnd) - toEpochDay(packedDateStart))/7;
    }

    public static int monthsUntil(int packedDateEnd, int packedDateStart) {

        int start = getMonthInternal(packedDateStart) * 32 + getDayOfMonth(packedDateStart);
        int end = getMonthInternal(packedDateEnd) * 32 + getDayOfMonth(packedDateEnd);
        return (end - start) / 32;
    }

    public static int yearsUntil(int packedDateEnd, int packedDateStart) {
        return monthsUntil(packedDateEnd, packedDateStart)/12;
    }

    private static int getMonthInternal(int packedDate) {
        return (getYear(packedDate) * 12 + getMonthValue(packedDate) - 1);
    }
}
