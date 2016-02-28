package com.deathrayresearch.outlier.columns;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.chrono.IsoChronology;

import static com.deathrayresearch.outlier.columns.PackedLocalDate.asLocalDate;

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
public class PackedLocalDateTime {

  public static byte getDayOfMonth(long date) {
    return (byte) date(date);  // last byte
  }

  public static short getYear(int date) {
    // get first two bytes, then convert to a short
    byte byte1 = (byte) (date >> 24);
    byte byte2 = (byte) (date >> 16);
    return (short) ((byte2 << 8) + (byte1 & 0xFF));
  }

  public static short getYear(long dateTime) {
    return getYear(date(dateTime));
  }

  public static LocalDateTime asLocalDateTime(long dateTime) {
    if (dateTime == Long.MIN_VALUE) {
      return null;
    }
    int date = date(dateTime);
    int time = time(dateTime);

    return LocalDateTime.of(asLocalDate(date), PackedLocalTime.asLocalTime(time));
  }

  public static byte getMonthValue(long dateTime) {
    int date = date(dateTime);
    return (byte) (date >> 8);
  }

  public static long pack(LocalDate date, LocalTime time) {
    int d = PackedLocalDate.pack(date);
    int t = PackedLocalTime.pack(time);
    return (((long) d) << 32) | (t & 0xffffffffL);
  }

  public static long pack(LocalDateTime dateTime) {
    LocalDate date = dateTime.toLocalDate();
    LocalTime time = dateTime.toLocalTime();
    int d = PackedLocalDate.pack(date);
    int t = PackedLocalTime.pack(time);
    return (((long) d) << 32) | (t & 0xffffffffL);
  }

  public static long pack(short yr, byte m, byte d, byte hr, byte min, byte s, byte n) {
    byte byte1 = (byte) yr;
    byte byte2 = (byte) ((yr >> 8) & 0xff);
    int date = Ints.fromBytes(
        byte1,
        byte2,
        m,
        d);

    int time = Ints.fromBytes(hr, min, s, n);

    return (((long) date) << 32) | (time & 0xffffffffL);
  }

  static int date(long packedDateTIme) {
    return (int) (packedDateTIme >> 32);
  }

  static int time(long packedDateTIme) {
    return (int) packedDateTIme;
  }

  public static String toString(long dateTime) {
    if (dateTime == Long.MIN_VALUE) {
      return "NA";
    }
    int date = date(dateTime);
    int time = time(dateTime);

    // get first two bytes, then each of the other two
    byte yearByte1 = (byte) (date >> 24);
    byte yearByte2 = (byte) (date >> 16);

    return String.format("%d-%s-%s:%d:%d:%d",
        (short) ((yearByte2 << 8) + (yearByte1 & 0xFF)),
        Strings.padStart(Byte.toString((byte) (date >> 8)), 2, '0'),
        Strings.padStart(Byte.toString((byte) date), 2, '0'),
        PackedLocalTime.getHour(time),
        PackedLocalTime.getMinute(time),
        PackedLocalTime.getSecond(time)
    );
  }

  public static int getDayOfYear(long packedDate) {
    return getMonth(packedDate).firstDayOfYear(isLeapYear(packedDate)) + getDayOfMonth(packedDate) - 1;
  }

  public static boolean isLeapYear(long packedDate) {
    return IsoChronology.INSTANCE.isLeapYear(getYear(packedDate));
  }

  public static Month getMonth(long packedDate) {
    return Month.of(getMonthValue(packedDate));
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

  public int lengthOfYear(long packedDateTime) {
    return (isLeapYear(packedDateTime) ? 366 : 365);
  }


  public static DayOfWeek getDayOfWeek(int packedDate) {
    int dow0 = (int) Math.floorMod(toEpochDay(packedDate) + 3, 7);
    return DayOfWeek.of(dow0 + 1);
  }

  private static long toEpochDay(int packedDateTime) {
    return PackedLocalDate.toEpochDay(date(packedDateTime));
  }

  public static boolean isInQ1(int packedDate) {
    Month month = getMonth(packedDate);
    return month == Month.JANUARY ||
        month == Month.FEBRUARY ||
        month == Month.MARCH;
  }

  public static boolean isInQ2(int packedDate) {
    Month month = getMonth(packedDate);
    return month == Month.APRIL ||
        month == Month.MAY ||
        month == Month.JUNE;
  }
  public static boolean isInQ3(int packedDate) {
    Month month = getMonth(packedDate);
    return month == Month.JULY ||
        month == Month.AUGUST ||
        month == Month.SEPTEMBER;
  }
  public static boolean isInQ4(int packedDate) {
    Month month = getMonth(packedDate);
    return month == Month.OCTOBER ||
        month == Month.NOVEMBER ||
        month == Month.DECEMBER;
  }

  public static boolean isAfter(int packedDate, int value) {
    return packedDate > value;
  }

  public static boolean isBefore(int packedDate, int value) {
    return packedDate < value;
  }

  public static boolean isSunday(int packedDate) {
    DayOfWeek dayOfWeek = getDayOfWeek(packedDate);
    return dayOfWeek == DayOfWeek.SUNDAY;
  }

  public static boolean isMonday(int packedDate) {
    DayOfWeek dayOfWeek = getDayOfWeek(packedDate);
    return dayOfWeek == DayOfWeek.MONDAY;
  }

  public static boolean isTuesday(int packedDate) {
    DayOfWeek dayOfWeek = getDayOfWeek(packedDate);
    return dayOfWeek == DayOfWeek.TUESDAY;
  }

  public static boolean isWednesday(int packedDate) {
    DayOfWeek dayOfWeek = getDayOfWeek(packedDate);
    return dayOfWeek == DayOfWeek.WEDNESDAY;
  }

  public static boolean isThursday(int packedDate) {
    DayOfWeek dayOfWeek = getDayOfWeek(packedDate);
    return dayOfWeek == DayOfWeek.THURSDAY;
  }

  public static boolean isFriday(int packedDate) {
    DayOfWeek dayOfWeek = getDayOfWeek(packedDate);
    return dayOfWeek == DayOfWeek.FRIDAY;
  }

  public static boolean isSaturday(int packedDate) {
    DayOfWeek dayOfWeek = getDayOfWeek(packedDate);
    return dayOfWeek == DayOfWeek.SATURDAY;
  }

  public static boolean isFirstDayOfMonth(int packedDate) {
    return getDayOfMonth(packedDate) == 1;
  }

  public static boolean isInJanuary(int packedDate) {
    return getMonth(packedDate) == Month.JANUARY;
  }

  public static boolean isInFebruary(int packedDate) {
    return getMonth(packedDate) == Month.FEBRUARY;
  }

  public static boolean isInMarch(int packedDate) {
    return getMonth(packedDate) == Month.MARCH;
  }

  public static boolean isInApril(int packedDate) {
    return getMonth(packedDate) == Month.APRIL;
  }

  public static boolean isInMay(int packedDate) {
    return getMonth(packedDate) == Month.MAY;
  }

  public static boolean isInJune(int packedDate) {
    return getMonth(packedDate) == Month.JUNE;
  }

  public static boolean isInJuly(int packedDate) {
    return getMonth(packedDate) == Month.JULY;
  }

  public static boolean isInAugust(int packedDate) {
    return getMonth(packedDate) == Month.AUGUST;
  }

  public static boolean isInSeptember(int packedDate) {
    return getMonth(packedDate) == Month.SEPTEMBER;
  }

  public static boolean isInOctober(int packedDate) {
    return getMonth(packedDate) == Month.OCTOBER;
  }

  public static boolean isInNovember(int packedDate) {
    return getMonth(packedDate) == Month.NOVEMBER;
  }

  public static boolean isInDecember(int packedDate) {
    return getMonth(packedDate) == Month.DECEMBER;
  }

  public static boolean isLastDayOfMonth(int packedDate) {
    return getDayOfMonth(packedDate) == lengthOfMonth(packedDate);
  }

  public static boolean isInYear(int next, int year) {
    return getYear(next) == year;
  }


}
