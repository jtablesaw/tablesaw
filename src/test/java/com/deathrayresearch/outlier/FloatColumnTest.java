package com.deathrayresearch.outlier;

import com.google.common.base.Stopwatch;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Test;
import org.roaringbitmap.RoaringBitmap;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class FloatColumnTest {

  @Test
  public void testApplyFilter() {

    Relation table = new Table("t");
    FloatColumn floatColumn = new FloatColumn("test", 1_000_000_000);
    table.addColumn(floatColumn);
    for (int i = 0; i < 1_000_000_000; i++) {
      floatColumn.add((float) Math.random());
    }
    Stopwatch stopwatch = Stopwatch.createStarted();
    RoaringBitmap results = floatColumn.isLessThan(.5f);
    System.out.println("Search time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
  }

  @Test
  public void testIsLessThan() {

    Relation table = new Table("t");
    FloatColumn floatColumn = new FloatColumn("test", 1_000_000);
    table.addColumn(floatColumn);
    for (int i = 0; i < 1_000_000; i++) {
      floatColumn.add((float) Math.random());
    }
    Stopwatch stopwatch = Stopwatch.createStarted();
    RoaringBitmap results = floatColumn.isLessThan(.5f);
    System.out.println("Search time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));

    int count = 0;
    for (int i = 0; i < 1_000_000; i++) {
      if (results.contains(i)) {
        count++;
      }
    }
    System.out.println("Matches = " + count);
  }

  @Test
  public void testIsGreaterThan() {

    Relation table = new Table("t");
    FloatColumn floatColumn = new FloatColumn("test", 1_000_000);
    table.addColumn(floatColumn);
    for (int i = 0; i < 1_000_000; i++) {
      floatColumn.add((float) Math.random());
    }
    Stopwatch stopwatch = Stopwatch.createStarted();
    RoaringBitmap results = floatColumn.isGreaterThan(.5f);
    System.out.println("Search time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));

    int count = 0;
    for (int i = 0; i < 1_000_000; i++) {
      if (results.contains(i)) {
        count++;
      }
    }
    System.out.println("Matches = " + count);
  }

  @Test
  public void testSort() {
    FloatColumn floatColumn = new FloatColumn("test", 100_000_000);
    for (int i = 0; i < 100_000_000; i++) {
      floatColumn.add((float) Math.random());
    }
    System.out.println("Data loaded, beginning first sort");
    FloatColumn sorted = (FloatColumn) floatColumn.sortAscending();
    float last = Float.NEGATIVE_INFINITY;
    while (sorted.hasNext()) {
      float n = sorted.next();
      assertTrue(n >= last);
      last = n;
    }
    System.out.println("Beginning second sort");
    sorted = (FloatColumn) floatColumn.sortDescending();
    last = Float.POSITIVE_INFINITY;
    while (sorted.hasNext()) {
      float n = sorted.next();
      assertTrue(n <= last);
      last = n;
    }
  }

  @Test
  public void testIsEqualTo() {

    Relation table = new Table("t");
    FloatColumn floatColumn = new FloatColumn("test", 1_000_000);
    float[] floats = new float[1_000_000];
    table.addColumn(floatColumn);
    for (int i = 0; i < 1_000_000; i++) {
      float f = (float) Math.random();
      floatColumn.add(f);
      floats[i] = f;
    }

    Stopwatch stopwatch = Stopwatch.createStarted();
    RoaringBitmap results;
    RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
    int count = 0;
    for (int i = 0; i < 100; i++) {
      float f = floats[randomDataGenerator.nextInt(0, 999_999)];
      results = floatColumn.isEqualTo(f);
      count = count + results.getCardinality();
    }
    System.out.println("Search time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    System.out.println("Matches = " + count);
  }
}