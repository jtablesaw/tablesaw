package com.deathrayresearch.outlier.columns;

import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class PackedLocalTimeTest {

  @Test
  public void getNano() {
    LocalTime now = LocalTime.now();
    assertEquals(now.getNano(),
        PackedLocalTime.getNano(PackedLocalTime.pack(now)));
    System.out.println(PackedLocalTime.getNano(PackedLocalTime.pack(now)));
    System.out.println(now.getNano());
    System.out.println(PackedLocalTime.getMilliseconds(PackedLocalTime.pack(now)));
  }

  @Test
  public void getMillisecondOfMinute() {
    LocalTime now = LocalTime.now();
    int time = PackedLocalTime.pack(now);
    LocalTime then = PackedLocalTime.asLocalTime(time);

    System.out.println(now);
    System.out.println(then);
    char c = PackedLocalTime.getMillisecondsOfMinute(time);
    System.out.println(PackedLocalTime.getMillisecondsOfMinute(time));
    System.out.println(Character.getNumericValue(PackedLocalTime.getMillisecondsOfMinute(time)));
  }
}