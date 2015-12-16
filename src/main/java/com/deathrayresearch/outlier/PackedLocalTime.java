package com.deathrayresearch.outlier;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

import java.time.LocalTime;

/**
 * A localTime with millisecond precision packed into a single int value.
 *
 * The bytes are packed into the int as:
 * First byte: hourOfDay
 * next byte: minuteOfHour
 * last two bytes (short): millisecond of minute
 *
 * Storing the millisecond of minute in an short requires that we treat the short as if it were unsigned. Unfortunately,
 * Neither Java nor Guava provide unsigned short support so we use char, which is a 16-bit unsigned int to
 * store values of up to 60,000 milliseconds (60 secs * 1000)
 */
public class PackedLocalTime {

  public static byte getHour(int time) {
    return (byte)(time >> 24);
  }

  public static char getMillisecondsOfMinute(int time) {
    byte byte1 = (byte) (time >> 8);
    byte byte2 = (byte) time;
    return (char) ((byte1 << 8) | (byte2 & 0xFF));
  }

  public static int getNano(int time) {
    long millis = getMillisecondsOfMinute(time);
    millis = millis * 1_000_000L; // convert to nanos of minute
    byte seconds = getSecond(time);
    long nanos = seconds * 1_000_000_000L;
    millis = millis - nanos;         // remove the part in seconds
    return (int) millis;
  }

  public static long toNanoOfDay(int time) {
    long nano = getHour(time) * 3_600_000_000_000L;
    nano += getMinute(time) * 60_000_000_000L;
    nano += getSecond(time) * 1_000_000_000L;
    nano += getNano(time);
    return nano;
  }

  public static LocalTime asLocalTime(int time) {
    if (time == -1) {
      return null;
    }

    byte hourByte = (byte)(time >> 24);
    byte minuteByte = (byte)(time >> 16);
    byte millisecondByte1 = (byte)(time >> 8);
    byte millisecondByte2 = (byte) time;
    char millis = (char) ((millisecondByte1 << 8) | (millisecondByte2 & 0xFF));
    int second = millis / 1000;
    int nanoOfSecond = (millis % 1000) * 1_000_000;
    return LocalTime.of(
        hourByte,
        minuteByte,
        second,
        nanoOfSecond);
  }

  public static byte getMinute(int time) {
    return (byte)(time >> 16);
  }

  public static int pack(LocalTime time) {
    byte hour = (byte) time.getHour();
    byte minute = (byte) time.getMinute();
    char millis = (char) (time.getNano() / 1_000_000.0);
    millis = (char) (millis + (char) (time.getSecond() * 1000));
    byte m1 = (byte)(millis >> 8);
    byte m2 = (byte) millis;

    return Ints.fromBytes(
        hour,
        minute,
        m1,
        m2);
  }

  public static byte getSecond(int packedLocalTime) {
    return (byte) (getMillisecondsOfMinute(packedLocalTime) / 1000);
  }

/*
  public static int pack(short yr, byte m, byte d) {
    byte byte1= (byte) yr;
    byte byte2= (byte) ((yr >> 8) & 0xff);
    return Ints.fromBytes(
        byte1,
        byte2,
        m,
        d);
  }
*/

  public static String toShortTimeString(int time) {
    if (time == -1) {
      return "NA";
    }

    byte hourByte = (byte)(time >> 24);
    byte minuteByte = (byte)(time >> 16);
    byte millisecondByte1 = (byte)(time >> 8);
    byte millisecondByte2 = (byte) time;
    char millis = (char) ((millisecondByte1 << 8) | (millisecondByte2 & 0xFF));
    int second = millis / 1000;
    int millisOnly = millis % 1000;

    return String.format("%s:%s:%s",
        Strings.padStart(Byte.toString(hourByte), 2, '0'),
        Strings.padStart(Byte.toString(minuteByte), 2, '0'),
        Strings.padStart(Integer.toString(second), 2, '0'));

  }
}
