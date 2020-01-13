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

package tech.tablesaw.columns.times;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import tech.tablesaw.columns.numbers.IntColumnType;

/**
 * A localTime with millisecond precision packed into a single int value.
 *
 * <p>The bytes are packed into the int as: First byte: hourOfDay next byte: minuteOfHour last two
 * bytes (short): millisecond of minute
 *
 * <p>Storing the millisecond of minute in an short requires that we treat the short as if it were
 * unsigned. Unfortunately, Neither Java nor Guava provide unsigned short support so we use char,
 * which is a 16-bit unsigned int to store values of up to 60,000 milliseconds (60 secs * 1000)
 */
public class PackedLocalTime {

  private static final int MIDNIGHT = pack(LocalTime.MIDNIGHT);
  private static final int NOON = pack(LocalTime.NOON);

  private static final int HOURS_PER_DAY = 24;
  private static final int MINUTES_PER_HOUR = 60;
  private static final int MINUTES_PER_DAY = MINUTES_PER_HOUR * HOURS_PER_DAY;
  private static final int SECONDS_PER_MINUTE = 60;
  private static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;
  private static final int SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY;
  private static final int MILLIS_PER_DAY = SECONDS_PER_DAY * 1000;
  private static final long NANOS_PER_SECOND = 1000_000_000L;
  private static final long NANOS_PER_MINUTE = NANOS_PER_SECOND * SECONDS_PER_MINUTE;
  private static final long NANOS_PER_HOUR = NANOS_PER_MINUTE * MINUTES_PER_HOUR;
  private static final long NANOS_PER_DAY = NANOS_PER_HOUR * HOURS_PER_DAY;

  public static byte getHour(int time) {
    return (byte) (time >> 24);
  }

  public static int of(int hour, int minute) {
    ChronoField.HOUR_OF_DAY.checkValidValue(hour);
    ChronoField.MINUTE_OF_HOUR.checkValidValue(minute);
    return create(hour, minute, 0, 0);
  }

  public static int of(int hour, int minute, int second) {
    ChronoField.HOUR_OF_DAY.checkValidValue(hour);
    ChronoField.MINUTE_OF_HOUR.checkValidValue(minute);
    ChronoField.SECOND_OF_MINUTE.checkValidValue(second);
    return create(hour, minute, second, 0);
  }

  public static int of(int hour, int minute, int second, int millis) {
    ChronoField.HOUR_OF_DAY.checkValidValue(hour);
    ChronoField.MINUTE_OF_HOUR.checkValidValue(minute);
    ChronoField.SECOND_OF_MINUTE.checkValidValue(second);
    ChronoField.MILLI_OF_SECOND.checkValidValue(millis);
    return create(hour, minute, second, millis);
  }

  public static int truncatedTo(TemporalUnit unit, int packedTime) {
    if (unit == ChronoUnit.NANOS || unit == ChronoUnit.MILLIS) {
      return packedTime;
    }
    Duration unitDur = unit.getDuration();
    if (unitDur.getSeconds() > SECONDS_PER_DAY) {
      throw new UnsupportedTemporalTypeException("Unit is too large to be used for truncation");
    }

    int hour = PackedLocalTime.getHour(packedTime);
    int minute = PackedLocalTime.getMinute(packedTime);
    int second = PackedLocalTime.getSecond(packedTime);
    int milli = 0;

    if (unit == ChronoUnit.DAYS) {
      hour = 0;
      minute = 0;
      second = 0;
    } else if (unit == ChronoUnit.HALF_DAYS) {
      if (hour >= 12) {
        hour = 12;
      } else {
        hour = 0;
      }
      minute = 0;
      second = 0;
    } else if (unit == ChronoUnit.HOURS) {
      minute = 0;
      second = 0;
    } else if (unit == ChronoUnit.MINUTES) {
      second = 0;
    }
    return PackedLocalTime.create(hour, minute, second, milli);
  }

  public static int plusHours(int hoursToAdd, int packedTime) {
    if (hoursToAdd == 0) {
      return packedTime;
    }
    int hour = PackedLocalTime.getHour(packedTime);
    int newHour = ((hoursToAdd % HOURS_PER_DAY) + hour + HOURS_PER_DAY) % HOURS_PER_DAY;
    return create(
        newHour,
        PackedLocalTime.getMinute(packedTime),
        PackedLocalTime.getSecond(packedTime),
        PackedLocalTime.getMilliseconds(packedTime));
  }

  public static int plusMinutes(int minutesToAdd, int packedTime) {
    if (minutesToAdd == 0) {
      return packedTime;
    }
    int hour = PackedLocalTime.getHour(packedTime);
    int minute = PackedLocalTime.getMinute(packedTime);
    int second = PackedLocalTime.getSecond(packedTime);
    int milli = PackedLocalTime.getMilliseconds(packedTime);

    int mofd = hour * MINUTES_PER_HOUR + minute;

    int newMofd = ((minutesToAdd % MINUTES_PER_DAY) + mofd + MINUTES_PER_DAY) % MINUTES_PER_DAY;
    if (mofd == newMofd) {
      return packedTime;
    }
    int newHour = newMofd / MINUTES_PER_HOUR;
    int newMinute = newMofd % MINUTES_PER_HOUR;
    return create(newHour, newMinute, second, milli);
  }

  public static int plusSeconds(int secondsToAdd, int packedTime) {
    if (secondsToAdd == 0) {
      return packedTime;
    }
    int hour = PackedLocalTime.getHour(packedTime);
    int minute = PackedLocalTime.getMinute(packedTime);
    int second = PackedLocalTime.getSecond(packedTime);
    int milli = PackedLocalTime.getMilliseconds(packedTime);

    int sofd = hour * SECONDS_PER_HOUR + minute * SECONDS_PER_MINUTE + second;
    int newSofd = ((secondsToAdd % SECONDS_PER_DAY) + sofd + SECONDS_PER_DAY) % SECONDS_PER_DAY;
    if (sofd == newSofd) {
      return packedTime;
    }
    int newHour = newSofd / SECONDS_PER_HOUR;
    int newMinute = (newSofd / SECONDS_PER_MINUTE) % MINUTES_PER_HOUR;
    int newSecond = newSofd % SECONDS_PER_MINUTE;
    return create(newHour, newMinute, newSecond, milli);
  }

  public static int plusMilliseconds(int msToAdd, int packedTime) {
    if (msToAdd == 0) {
      return packedTime;
    }
    long nanosToAdd = ((long) msToAdd % MILLIS_PER_DAY) * 1000_000;
    long nofd = toNanoOfDay(packedTime);
    long newNofd = ((nanosToAdd % NANOS_PER_DAY) + nofd + NANOS_PER_DAY) % NANOS_PER_DAY;
    if (nofd == newNofd) {
      return packedTime;
    }
    int newHour = (int) (newNofd / NANOS_PER_HOUR);
    int newMinute = (int) ((newNofd / NANOS_PER_MINUTE) % MINUTES_PER_HOUR);
    int newSecond = (int) ((newNofd / NANOS_PER_SECOND) % SECONDS_PER_MINUTE);
    int newNano = (int) (newNofd % NANOS_PER_SECOND);
    int newMilli = newNano / 1_000_000;
    return create(newHour, newMinute, newSecond, newMilli);
  }

  public static int minusHours(int hoursToSubtract, int packedTime) {
    return plusHours(-hoursToSubtract, packedTime);
  }

  public static int minusMinutes(int minutesToSubtract, int packedTime) {
    return plusMinutes(-minutesToSubtract, packedTime);
  }

  public static int minusSeconds(int secondsToSubtract, int packedTime) {
    return plusSeconds(-secondsToSubtract, packedTime);
  }

  public static int minusMilliseconds(int millisToSubtract, int packedTime) {
    return plusMilliseconds(-millisToSubtract, packedTime);
  }

  public static int withHour(int hour, int packedTime) {
    if (PackedLocalTime.getHour(packedTime) == hour) {
      return packedTime;
    }
    ChronoField.HOUR_OF_DAY.checkValidValue(hour);
    return create(
        hour,
        PackedLocalTime.getMinute(packedTime),
        PackedLocalTime.getSecond(packedTime),
        PackedLocalTime.getMilliseconds(packedTime));
  }

  public static int withMinute(int minute, int packedTime) {
    if (PackedLocalTime.getMinute(packedTime) == minute) {
      return packedTime;
    }
    ChronoField.MINUTE_OF_HOUR.checkValidValue(minute);
    return create(
        PackedLocalTime.getHour(packedTime),
        minute,
        PackedLocalTime.getSecond(packedTime),
        PackedLocalTime.getMilliseconds(packedTime));
  }

  public static int withSecond(int second, int packedTime) {
    if (PackedLocalTime.getSecond(packedTime) == second) {
      return packedTime;
    }
    ChronoField.SECOND_OF_MINUTE.checkValidValue(second);
    return create(
        PackedLocalTime.getHour(packedTime),
        PackedLocalTime.getMinute(packedTime),
        second,
        PackedLocalTime.getMilliseconds(packedTime));
  }

  public static int withMillisecond(int milliseconds, int packedTime) {
    if (PackedLocalTime.getMilliseconds(packedTime) == milliseconds) {
      return packedTime;
    }
    ChronoField.MILLI_OF_SECOND.checkValidValue(milliseconds);

    return create(
        PackedLocalTime.getHour(packedTime),
        PackedLocalTime.getMinute(packedTime),
        PackedLocalTime.getSecond(packedTime),
        milliseconds);
  }

  private static int create(int hour, int minute, int second, int millis) {
    byte _hour = (byte) hour;
    byte _minute = (byte) minute;
    char _millis = (char) millis;
    _millis = (char) (_millis + (char) (second * 1000));
    return create(_hour, _minute, _millis);
  }

  public static char getMillisecondOfMinute(int time) {
    byte byte1 = (byte) (time >> 8);
    byte byte2 = (byte) time;
    return (char) ((byte1 << 8) | (byte2 & 0xFF));
  }

  public static int getNano(int time) {
    long millis = getMillisecondOfMinute(time);
    millis = millis * 1_000_000L; // convert to nanos of minute
    byte seconds = getSecond(time);
    long nanos = seconds * 1_000_000_000L;
    millis = millis - nanos; // remove the part in seconds
    return (int) millis;
  }

  public static int getMilliseconds(int time) {
    long millis = getMillisecondOfMinute(time);
    millis = millis * 1_000_000L; // convert to nanos of minute
    byte seconds = getSecond(time);
    long nanos = seconds * 1_000_000_000L;
    millis = millis - nanos; // remove the part in seconds
    return (int) (millis / 1_000_000L);
  }

  public static long toNanoOfDay(int time) {
    long nano = getHour(time) * 3_600_000_000_000L;
    nano += getMinute(time) * 60_000_000_000L;
    nano += getSecond(time) * 1_000_000_000L;
    nano += getNano(time);
    return nano;
  }

  public static LocalTime asLocalTime(int time) {
    if (time == TimeColumnType.missingValueIndicator()) {
      return null;
    }

    byte hourByte = (byte) (time >> 24);
    byte minuteByte = (byte) (time >> 16);
    byte millisecondByte1 = (byte) (time >> 8);
    byte millisecondByte2 = (byte) time;
    char millis = (char) ((millisecondByte1 << 8) | (millisecondByte2 & 0xFF));
    int second = millis / 1000;
    int nanoOfSecond = (millis % 1000) * 1_000_000;
    return LocalTime.of(hourByte, minuteByte, second, nanoOfSecond);
  }

  public static byte getMinute(int time) {
    return (byte) (time >> 16);
  }

  public static int pack(LocalTime time) {
    if (time == null) {
      return TimeColumnType.missingValueIndicator();
    }

    byte hour = (byte) time.getHour();
    byte minute = (byte) time.getMinute();
    char millis = (char) (time.getNano() / 1_000_000.0);
    millis = (char) (millis + (char) (time.getSecond() * 1000));
    return create(hour, minute, millis);
  }

  private static int create(byte hour, byte minute, char millis) {
    byte m1 = (byte) (millis >> 8);
    byte m2 = (byte) millis;

    return Ints.fromBytes(hour, minute, m1, m2);
  }

  public static byte getSecond(int packedLocalTime) {
    return (byte) (getMillisecondOfMinute(packedLocalTime) / 1000);
  }

  public static int getMinuteOfDay(int packedLocalTime) {
    if (packedLocalTime == TimeColumnType.missingValueIndicator()) {
      return IntColumnType.missingValueIndicator();
    }
    return getHour(packedLocalTime) * 60 + getMinute(packedLocalTime);
  }

  public static int getSecondOfDay(int packedLocalTime) {
    if (packedLocalTime == TimeColumnType.missingValueIndicator()) {
      return IntColumnType.missingValueIndicator();
    }
    int total = getHour(packedLocalTime) * 60 * 60;
    total += getMinute(packedLocalTime) * 60;
    total += getSecond(packedLocalTime);
    return total;
  }

  public static int getMillisecondOfDay(int packedLocalTime) {
    return (int) (toNanoOfDay(packedLocalTime) / 1000_000);
  }

  public static String toShortTimeString(int time) {
    if (time == TimeColumnType.missingValueIndicator()) {
      return "";
    }

    byte hourByte = (byte) (time >> 24);
    byte minuteByte = (byte) (time >> 16);
    byte millisecondByte1 = (byte) (time >> 8);
    byte millisecondByte2 = (byte) time;
    char millis = (char) ((millisecondByte1 << 8) | (millisecondByte2 & 0xFF));
    int second = millis / 1000;

    return String.format(
        "%s:%s:%s",
        Strings.padStart(Byte.toString(hourByte), 2, '0'),
        Strings.padStart(Byte.toString(minuteByte), 2, '0'),
        Strings.padStart(Integer.toString(second), 2, '0'));
  }

  public static boolean isMidnight(int packedTime) {
    return packedTime == MIDNIGHT;
  }

  public static boolean isNoon(int packedTime) {
    return packedTime == NOON;
  }

  public static boolean isAfter(int packedTime, int otherPackedTime) {
    return packedTime > otherPackedTime;
  }

  public static boolean isOnOrAfter(int packedTime, int otherPackedTime) {
    return packedTime >= otherPackedTime;
  }

  public static boolean isBefore(int packedTime, int otherPackedTime) {
    return packedTime < otherPackedTime;
  }

  public static boolean isOnOrBefore(int packedTime, int otherPackedTime) {
    return packedTime <= otherPackedTime;
  }

  public static boolean isEqualTo(int packedTime, int otherPackedTime) {
    return packedTime == otherPackedTime;
  }

  /**
   * Returns true if the time is in the AM or "before noon". Note: we follow the convention that
   * 12:00 NOON is PM and 12 MIDNIGHT is AM
   */
  public static boolean AM(int packedTime) {
    return packedTime < NOON;
  }

  /**
   * Returns true if the time is in the PM or "after noon". Note: we follow the convention that
   * 12:00 NOON is PM and 12 MIDNIGHT is AM
   */
  public static boolean PM(int packedTime) {
    return packedTime >= NOON;
  }

  public static int hoursUntil(int packedTimeEnd, int packedTimeStart) {
    return secondsUntil(packedTimeEnd, packedTimeStart) / 3600;
  }

  public static int minutesUntil(int packedTimeEnd, int packedTimeStart) {
    return secondsUntil(packedTimeEnd, packedTimeStart) / 60;
  }

  public static int secondsUntil(int packedTimeEnd, int packedTimeStart) {
    return (getSecondOfDay(packedTimeEnd) - getSecondOfDay(packedTimeStart));
  }
}
