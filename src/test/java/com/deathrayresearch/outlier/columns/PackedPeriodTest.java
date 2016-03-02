package com.deathrayresearch.outlier.columns;

import org.junit.Test;

import java.time.Period;

import static org.junit.Assert.*;

/**
 *
 */
public class PackedPeriodTest {

  @Test
  public void testPack() {
    Period period = Period.of(3, 7, 10);
    int packedPeriod = PackedPeriod.pack(period);
    System.out.println(packedPeriod);

    Period period2 = Period.of(3, 7, 11);
    int packedPeriod2 = PackedPeriod.pack(period2);
    System.out.println(packedPeriod2);

    assertTrue(PackedPeriod.isShorterThan(packedPeriod, packedPeriod2));
  }

  @Test
  public void testPack1() {
    Period period = Period.of(3, 7, 11);
    int packedPeriod = PackedPeriod.pack(period);
    assertEquals(period, PackedPeriod.asPeriod(packedPeriod));
  }

  @Test
  public void testAsPeriod() {

  }

  @Test
  public void testIsShorterThan() {
    Period period = Period.of(3, 7, 10);
    int packedPeriod = PackedPeriod.pack(period);
    System.out.println(packedPeriod);

    Period period2 = Period.of(3, 7, 11);
    int packedPeriod2 = PackedPeriod.pack(period2);
    System.out.println(packedPeriod2);

    assertTrue(PackedPeriod.isShorterThan(packedPeriod, packedPeriod2));
    assertFalse(PackedPeriod.isShorterThan(packedPeriod, packedPeriod));
    assertFalse(PackedPeriod.isShorterThan(packedPeriod2, packedPeriod));

    Period period3 = Period.of(4, 1, 1);
    Period period4 = Period.of(3, 16, 10);
    int packedPeriod3 = PackedPeriod.pack(period3);
    int packedPeriod4 = PackedPeriod.pack(period4);

    assertTrue(PackedPeriod.isShorterThan(packedPeriod3, packedPeriod4));
    assertFalse(PackedPeriod.isShorterThan(packedPeriod4, packedPeriod3));
  }

  @Test
  public void testIsLongerThan() {

  }
}