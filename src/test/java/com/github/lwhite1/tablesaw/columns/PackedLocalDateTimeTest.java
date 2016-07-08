package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.columns.packeddata.PackedLocalDate;
import com.github.lwhite1.tablesaw.columns.packeddata.PackedLocalDateTime;
import com.github.lwhite1.tablesaw.columns.packeddata.PackedLocalTime;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;

import static org.junit.Assert.*;

/**
 *
 */
public class PackedLocalDateTimeTest {

  @Test
  public void testGetDayOfMonth() {
    LocalDateTime today = LocalDateTime.now();
    assertEquals(today.getDayOfMonth(),
        PackedLocalDateTime.getDayOfMonth(PackedLocalDateTime.pack(today)));
  }

  @Test
  public void testGetYear() {
    LocalDateTime today = LocalDateTime.now();
    assertEquals(today.getYear(), PackedLocalDateTime.getYear(PackedLocalDateTime.pack(today)));
  }

  @Test
  public void testAsLocalDateTime() {
    LocalDateTime dateTime = LocalDateTime.now();
    long packed = PackedLocalDateTime.pack(dateTime.toLocalDate(), dateTime.toLocalTime());
    LocalDateTime upacked = PackedLocalDateTime.asLocalDateTime(packed);
    assertEquals(dateTime.getDayOfYear(), upacked.getDayOfYear());
    assertEquals(dateTime.getHour(), upacked.getHour());
    assertEquals(dateTime.getMinute(), upacked.getMinute());
    assertEquals(dateTime.getSecond(), upacked.getSecond());
  }

  @Test
  public void testGetMonthValue() {
    long dateTime = PackedLocalDateTime.pack(LocalDate.of(2015, 12, 25), LocalTime.now());
    assertEquals(12, PackedLocalDateTime.getMonthValue(dateTime));
  }

  @Test
  public void testPack() {
    LocalDate date = LocalDate.now();
    LocalTime time = LocalTime.now();

    long packed = PackedLocalDateTime.pack(date, time);

    LocalDate d1 = PackedLocalDate.asLocalDate(PackedLocalDateTime.date(packed));
    LocalTime t1 = PackedLocalTime.asLocalTime(PackedLocalDateTime.time(packed));
    assertNotNull(d1);
    assertNotNull(t1);
    assertEquals(date.toString(), d1.toString());
  }

  @Test
  public void testGetHour() {
    LocalDateTime now = LocalDateTime.now();
    assertEquals(now.getHour(), PackedLocalDateTime.getHour(PackedLocalDateTime.pack(now)));
  }

  @Test
  public void testGetMinute() {
    LocalDateTime now = LocalDateTime.now();
    assertEquals(now.getMinute(), PackedLocalDateTime.getMinute(PackedLocalDateTime.pack(now)));
  }

  @Test
  public void testGetSecond() {
    LocalDateTime now = LocalDateTime.now();
    assertEquals(now.getSecond(), PackedLocalDateTime.getSecond(PackedLocalDateTime.pack(now)));
  }

  @Test
  public void testGetSecondOfDay() {
    LocalDateTime now = LocalDateTime.now();
    assertEquals(now.get(ChronoField.SECOND_OF_DAY), PackedLocalDateTime.getSecondOfDay(PackedLocalDateTime.pack(now)));
  }

  @Test
  public void testGetMinuteOfDay() {
    LocalDateTime now = LocalDateTime.now();
    assertEquals(now.get(ChronoField.MINUTE_OF_DAY), PackedLocalDateTime.getMinuteOfDay(PackedLocalDateTime.pack(now)));
  }

  @Test
  public void testGetMillisecondOfDay() {
    LocalDateTime now = LocalDateTime.now();
    assertEquals(now.get(ChronoField.MILLI_OF_DAY), PackedLocalDateTime.getMillisecondOfDay(PackedLocalDateTime.pack(now)));
  }
}