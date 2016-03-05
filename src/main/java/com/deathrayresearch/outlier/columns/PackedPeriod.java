package com.deathrayresearch.outlier.columns;

import com.google.common.primitives.Ints;

import java.time.Period;

/**
 * Unlike java.time.Periods, PackedPeriods are always normalized, such that a period of 2 years and 18 months,
 * will be recorded as 3 years, 6 months.
 */
public class PackedPeriod {


  public static int pack(Period period) {
    return pack((short) period.getYears(), (byte) period.getMonths(), (byte) period.getDays());
  }

  public static int pack(short yr, byte m, byte d) {

    long totalMonths = toTotalMonths(yr, m);
    long splitYears = totalMonths / 12;
    int splitMonths = (int) (totalMonths % 12);
    if (splitYears == yr && splitMonths == m) {
      byte byte1 = (byte) yr;
      byte byte2 = (byte) ((yr >> 8) & 0xff);
      return Ints.fromBytes(
          byte1,
          byte2,
          m,
          d);
    } else {
      byte byte1 = (byte) splitYears;
      byte byte2 = (byte) ((splitYears >> 8) & 0xff);
      return Ints.fromBytes(
          byte1,
          byte2,
          m,
          d);
    }
  }

  public static Period asPeriod(int packedPeriod) {
    return Period.of(getYears(packedPeriod), getMonths(packedPeriod), getDays(packedPeriod));
  }

  public static boolean isShorterThan(int thisPeriod, int otherPeriod) {
    int result = Integer.compare(thisPeriod, otherPeriod);
    return result < 0;
  }

  public static boolean isLongerThan(int thisPeriod, int otherPeriod) {
    int result = Integer.compare(thisPeriod, otherPeriod);
    return result > 0;
  }

  public static byte getMonths(int date) {
    // get the third byte
    return (byte) (date >> 8);
  }

  public static short getYears(int date) {
    // get first two bytes, then convert to a short
    byte byte1 = (byte) (date >> 24);
    byte byte2 = (byte) (date >> 16);
    return (short) ((byte2 << 8) + (byte1 & 0xFF));
  }

  public static byte getDays(int date) {
    return (byte) date;  // last byte
  }

  public static long toTotalMonths(int years, int months) {
    return years * 12L + months;
  }

  public static boolean equalTo(int thisPackedPeriod, int otherPackedPeriod) {
    return Integer.compare(thisPackedPeriod, otherPackedPeriod) == 0;
  }
}
