package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.api.BooleanColumn;
import com.github.lwhite1.tablesaw.api.FloatColumn;
import com.github.lwhite1.tablesaw.table.Relation;
import com.google.common.base.Stopwatch;
import io.codearte.jfairy.Fairy;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.StatUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.roaringbitmap.RoaringBitmap;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class FloatColumnTest {

  @Ignore
  @Test
  public void testApplyFilter() {

    Fairy fairy = Fairy.create();
    fairy.baseProducer().trueOrFalse();

    com.github.lwhite1.tablesaw.api.Table table = new com.github.lwhite1.tablesaw.api.Table("t");
    FloatColumn floatColumn = new FloatColumn("test", 1_000_000_000);
    BooleanColumn booleanColumn = new BooleanColumn("bools", 1_000_000_000);
    table.addColumn(floatColumn);
    table.addColumn(booleanColumn);
    for (int i = 0; i < 1_000_000_000; i++) {
      floatColumn.add((float) Math.random());
      booleanColumn.add(fairy.baseProducer().trueOrFalse());
    }
    Stopwatch stopwatch = Stopwatch.createStarted();
    table.sortOn("test");
    System.out.println("Sort time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    stopwatch.reset().start();
    System.out.println(floatColumn.describe());
    stopwatch.reset().start();
    floatColumn.isLessThan(.5f);
    System.out.println("Search time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
  }

  @Ignore
  @Test
  public void testSortAndApplyFilter1() {

    FloatColumn floatColumn = new FloatColumn("test", 1_000_000_000);
    for (int i = 0; i < 1_000_000_000; i++) {
      floatColumn.add((float) Math.random());
    }
    Stopwatch stopwatch = Stopwatch.createStarted();
    System.out.println(floatColumn.sum());
    System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS));
    stopwatch.reset().start();
    floatColumn.sortAscending();
    System.out.println("Sort time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));

    stopwatch.reset().start();
    floatColumn.isLessThan(.5f);
    System.out.println("Search time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
  }

  @Ignore
  @Test
  public void testSort1() throws Exception {
    FloatColumn floatColumn = new FloatColumn("test", 1_000_000_000);
    System.out.println("Adding floats to column");
    for (int i = 0; i < 1_000_000_000; i++) {
      floatColumn.add((float) Math.random());
    }
    System.out.println("Sorting");
    Stopwatch stopwatch = Stopwatch.createStarted();
    floatColumn.sortAscending();
    System.out.println("Sort time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));

  }

  @Test
  public void testIsLessThan() {
    int size = 1_000_000;
    Relation table = new com.github.lwhite1.tablesaw.api.Table("t");
    FloatColumn floatColumn = new FloatColumn("test", size);
    table.addColumn(floatColumn);
    for (int i = 0; i < size; i++) {
      floatColumn.add((float) Math.random());
    }
    RoaringBitmap results = floatColumn.isLessThan(.5f);
    int count = 0;
    for (int i = 0; i < size; i++) {
      if (results.contains(i)) {
        count++;
      }
    }
    // Probabilistic answer.
    assertTrue(count < 575_000);
    assertTrue(count > 425_000);
  }

  @Test
  public void testIsGreaterThan() {
    int size = 1_000_000;
    Relation table = new com.github.lwhite1.tablesaw.api.Table("t");
    FloatColumn floatColumn = new FloatColumn("test", size);
    table.addColumn(floatColumn);
    for (int i = 0; i < size; i++) {
      floatColumn.add((float) Math.random());
    }
    RoaringBitmap results = floatColumn.isGreaterThan(.5f);

    int count = 0;
    for (int i = 0; i < size; i++) {
      if (results.contains(i)) {
        count++;
      }
    }
    // Probabilistic answer.
    assertTrue(count < 575_000);
    assertTrue(count > 425_000);
  }

  @Test
  public void testSort() {
    int records = 1_000_000;
    FloatColumn floatColumn = new FloatColumn("test", records);
    for (int i = 0; i < records; i++) {
      floatColumn.add((float) Math.random());
    }
    floatColumn.sortAscending();
    float last = Float.NEGATIVE_INFINITY;
    for (float n : floatColumn) {
      assertTrue(n >= last);
      last = n;
    }
    floatColumn.sortDescending();
    last = Float.POSITIVE_INFINITY;
    for (float n : floatColumn) {
      assertTrue(n <= last);
      last = n;
    }
    records = 10;
    floatColumn = new FloatColumn("test", records);
    for (int i = 0; i < records; i++) {
      floatColumn.add((float) Math.random());
    }
    floatColumn.sortDescending();
    last = Float.POSITIVE_INFINITY;
    for (float n : floatColumn) {
      assertTrue(n <= last);
      last = n;
    }
  }

  @Test
  public void testIsEqualTo() {

    Relation table = new com.github.lwhite1.tablesaw.api.Table("t");
    FloatColumn floatColumn = new FloatColumn("test", 1_000_000);
    float[] floats = new float[1_000_000];
    table.addColumn(floatColumn);
    for (int i = 0; i < 1_000_000; i++) {
      float f = (float) Math.random();
      floatColumn.add(f);
      floats[i] = f;
    }
    RoaringBitmap results;
    RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
    for (int i = 0; i < 100; i++) { // pick a hundred values at random and see if we can find them
      float f = floats[randomDataGenerator.nextInt(0, 999_999)];
      results = floatColumn.isEqualTo(f);
      assertEquals(f, floatColumn.get(results.getIntIterator().next()), .001);
    }
  }

  @Test
  public void testMaxAndMin() {
    FloatColumn floats = new FloatColumn("floats", 100);
    for (int i = 0; i < 100; i++) {
      floats.add(RandomUtils.nextFloat(0, 10_000));
    }
    FloatArrayList floats1 = floats.top(50);
    FloatArrayList floats2 = floats.bottom(50);
    double[] doubles1 = new double[50];
    double[] doubles2 = new double[50];
    for (int i = 0; i < floats1.size(); i++) {
      doubles1[i] = floats1.getFloat(i);
    }
    for (int i = 0; i < floats2.size(); i++) {
      doubles2[i] = floats2.getFloat(i);
    }
    // the smallest item in the max set is >= the largest in the min set
    assertTrue(StatUtils.min(doubles1) >= StatUtils.max(doubles2));
  }
}