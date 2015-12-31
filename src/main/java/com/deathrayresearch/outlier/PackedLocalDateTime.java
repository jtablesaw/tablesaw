package com.deathrayresearch.outlier;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

  public static byte getDayOfMonth(int date) {
    return (byte) date;  // last byte
  }

  public static short getYear(int date) {
    // get first two bytes, then convert to a short
    byte byte1 = (byte) (date >> 24);
    byte byte2 = (byte) (date >> 16);
    return (short) ((byte2 << 8) + (byte1 & 0xFF));
  }

  public static LocalDateTime asLocalDateTime(long dateTime) {
    if (dateTime == Long.MIN_VALUE) {
      return null;
    }
    int date = date(dateTime);
    int time = time(dateTime);

    return LocalDateTime.of(PackedLocalDate.asLocalDate(date), PackedLocalTime.asLocalTime(time));
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

}
