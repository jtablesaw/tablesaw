package com.deathrayresearch.outlier.columns;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

import java.time.LocalDate;

/**
 * A short localdate packed into a single int value. It uses a short for year so the range is about +-30,000 years
 * <p>
 * The bytes are packed into the int as:
 * First two bytes: short (year)
 * next byte (month of year)
 * last byte (day of month)
 */
public class PackedLocalDate {

  public static byte getDayOfMonth(int date) {
    return (byte) date;  // last byte
  }

  public static short getYear(int date) {
    // get first two bytes, then convert to a short
    byte byte1 = (byte) (date >> 24);
    byte byte2 = (byte) (date >> 16);
    return (short) ((byte2 << 8) + (byte1 & 0xFF));
  }

  public static LocalDate asLocalDate(int date) {
    if (date == Integer.MIN_VALUE) {
      return null;
    }

    // get first two bytes, then each of the other two
    byte yearByte1 = (byte) (date >> 24);
    byte yearByte2 = (byte) (date >> 16);

    return LocalDate.of(
        (short) ((yearByte2 << 8) + (yearByte1 & 0xFF)),
        (byte) (date >> 8),
        (byte) date);
  }

  public static byte getMonthValue(int date) {
    // get the third byte
    return (byte) (date >> 8);
  }

  public static int pack(LocalDate date) {
    short year = (short) date.getYear();
    byte byte1 = (byte) year;
    byte byte2 = (byte) ((year >> 8) & 0xff);
    return Ints.fromBytes(
        byte1,
        byte2,
        (byte) date.getMonthValue(),
        (byte) date.getDayOfMonth());
  }

  public static int pack(short yr, byte m, byte d) {
    byte byte1 = (byte) yr;
    byte byte2 = (byte) ((yr >> 8) & 0xff);
    return Ints.fromBytes(
        byte1,
        byte2,
        m,
        d);
  }

  public static String toDateString(int date) {
    if (date == Integer.MIN_VALUE) {
      return "NA";
    }

    // get first two bytes, then each of the other two
    byte yearByte1 = (byte) (date >> 24);
    byte yearByte2 = (byte) (date >> 16);

    return String.format("%d-%s-%s",
        (short) ((yearByte2 << 8) + (yearByte1 & 0xFF)),
        Strings.padStart(Byte.toString((byte) (date >> 8)), 2, '0'),
        Strings.padStart(Byte.toString((byte) date), 2, '0'));
  }
}
