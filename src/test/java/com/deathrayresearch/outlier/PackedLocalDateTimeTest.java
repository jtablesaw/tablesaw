package com.deathrayresearch.outlier;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.*;

/**
 *
 */
public class PackedLocalDateTimeTest {

  @Test
  public void testGetDayOfMonth() {

  }

  @Test
  public void testGetYear() {

  }

  @Test
  public void testAsLocalDateTime() {
    LocalDateTime dateTime = LocalDateTime.now();
    long packed = PackedLocalDateTime.pack(dateTime.toLocalDate(), dateTime.toLocalTime());
    LocalDateTime upacked = PackedLocalDateTime.asLocalDateTime(packed);
    System.out.println(dateTime);
    System.out.println(upacked);
  }

  @Test
  public void testGetMonthValue() {
    long dateTime = PackedLocalDateTime.pack(LocalDate.now(), LocalTime.now());
    assertEquals(12, PackedLocalDateTime.getMonthValue(dateTime));
  }

  @Test
  public void testPack() {
    LocalDate date = LocalDate.now();
    LocalTime time = LocalTime.now();

    long packed = PackedLocalDateTime.pack(date, time);

    LocalDate d1 = PackedLocalDate.asLocalDate(PackedLocalDateTime.date(packed));
    LocalTime t1 = PackedLocalTime.asLocalTime(PackedLocalDateTime.time(packed));
    assertEquals(date, d1);
    System.out.println(d1.toString());
    System.out.println(time.toString());
    System.out.println(t1.toString());
  }

  @Test
  public void testPack1() {

  }

  @Test
  public void testToDateTimeString() {

  }

  @Test
  public void testDate() {

  }

  @Test
  public void testTime() {

  }
}