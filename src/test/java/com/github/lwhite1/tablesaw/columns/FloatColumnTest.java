package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.Relation;
import com.github.lwhite1.tablesaw.Table;
import com.google.common.base.Stopwatch;
import io.codearte.jfairy.Fairy;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Ignore;
import org.junit.Test;
import org.roaringbitmap.RoaringBitmap;

import java.util.concurrent.TimeUnit;

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

    Table table = new Table("t");
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
    //System.out.println(floatColumn.describe());
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
    int records = 100_000_000;
    FloatColumn floatColumn = new FloatColumn("test", records);
    for (int i = 0; i < records; i++) {
      floatColumn.add((float) Math.random());
    }
    System.out.println("Data loaded, beginning first sort");
    Stopwatch stopwatch = Stopwatch.createStarted();
    floatColumn.sortAscending();
    float last = Float.NEGATIVE_INFINITY;
    for (float n : floatColumn) {
      assertTrue(n >= last);
      last = n;
    }
    System.out.println(String.format("Sorted %d records in %d seconds", records, stopwatch.elapsed(TimeUnit.SECONDS)));
    System.out.println("Beginning second sort");
    stopwatch.reset().start();
    floatColumn.sortDescending();
    last = Float.POSITIVE_INFINITY;
    for (float n : floatColumn) {
      assertTrue(n <= last);
      last = n;
    }
    System.out.println(String.format("Sorted %d records in %d seconds", records, stopwatch.elapsed(TimeUnit.SECONDS)));

    records = 10;
    floatColumn = new FloatColumn("test", records);
    for (int i = 0; i < records; i++) {
      floatColumn.add((float) Math.random());
    }
    floatColumn.sortDescending();
    System.out.println(floatColumn.print());

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

  @Test
  public void testMax() {
    FloatColumn floats = new FloatColumn("floats", 100);
    for (int i = 0; i < 100; i++) {
      floats.add(RandomUtils.nextFloat(0, 10_000));
    }
    FloatArrayList floats1 = floats.max(5);
    System.out.println(floats1);
  }

  @Test
  public void testMin() {
    FloatColumn floats = new FloatColumn("floats", 100);
    for (int i = 0; i < 100; i++) {
      floats.add(RandomUtils.nextFloat(0, 10_000));
    }
    FloatArrayList floats1 = floats.min(5);
    System.out.println(floats1);

  }
}