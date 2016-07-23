package com.github.lwhite1.tablesaw.api;

import com.github.lwhite1.tablesaw.filtering.IntPredicate;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
/**
 *  Tests for int columns
 */
public class IntColumnTest {

  private IntColumn column;

  @Before
  public void setUp() throws Exception {
    column = new IntColumn("t1");
  }

  @Test
  public void testSum() {
    for (int i = 0; i < 100; i++) {
      column.add(1);
    }
    assertEquals(100, column.sum());
  }

  @Test
  public void testMin() {
    for (int i = 0; i < 100; i++) {
      column.add(i);
    }
    assertEquals(0.0, column.min(), .001);
  }

  @Test
  public void testMax() {
    for (int i = 0; i < 100; i++) {
      column.add(i);
    }
    assertEquals(99, column.max(), .001);
  }

  @Test
  public void testIsLessThan() {
    for (int i = 0; i < 100; i++) {
      column.add(i);
    }
    assertEquals(50, column.isLessThan(50).size());
  }

  @Test
  public void testIsGreaterThan() {
    for (int i = 0; i < 100; i++) {
      column.add(i);
    }
    assertEquals(49, column.isGreaterThan(50).size());
  }

  @Test
  public void testIsGreaterThanOrEqualTo() {
    for (int i = 0; i < 100; i++) {
      column.add(i);
    }
    assertEquals(50, column.isGreaterThanOrEqualTo(50).size());
    assertEquals(50, column.isGreaterThanOrEqualTo(50).get(0));
  }

  @Test
  public void testIsLessThanOrEqualTo() {
    for (int i = 0; i < 100; i++) {
      column.add(i);
    }
    assertEquals(51, column.isLessThanOrEqualTo(50).size());
    assertEquals(49, column.isLessThanOrEqualTo(50).get(49));
  }

  @Test
  public void testIsEqualTo() {
    for (int i = 0; i < 100; i++) {
      column.add(i);
    }
    assertEquals(1, column.isEqualTo(10).size());
  }

  @Test
  public void testPercents() {
    for (int i = 0; i < 100; i++) {
      column.add(i);
    }
    FloatColumn floatColumn = column.asRatio();
    assertEquals(1.0, floatColumn.sum(), 0.1);
  }

  @Test
  public void testSelectIf() {

    for (int i = 0; i < 100; i++) {
      column.add(i);
    }

    IntPredicate predicate = value -> value < 10;
    IntColumn column1 = column.selectIf(predicate);
    assertEquals(10, column1.size());
    for (int i = 0; i < 10; i++) {
      assertTrue(column1.get(i) < 10);
    }
  }

  @Test
  public void testSelect() {

    for (int i = 0; i < 100; i++) {
      column.add(i);
    }

    IntPredicate predicate = value -> value < 10;
    IntColumn column1 = column.selectIf(predicate);
    assertEquals(10, column1.size());

    IntColumn column2 = column.select(column.select(predicate));
    assertEquals(10, column2.size());
    for (int i = 0; i < 10; i++) {
      assertTrue(column1.get(i) < 10);
    }
    for (int i = 0; i < 10; i++) {
      assertTrue(column2.get(i) < 10);
    }
  }

  @Test
  public void testDifference() {
    int[] originalValues = new int[] {32,42,40,57,52};
    int[] expectedValues = new int[] {IntColumn.MISSING_VALUE,10,-2,17,-5};

    IntColumn initial = new IntColumn("Test",originalValues.length);
    for (int value : originalValues) {
      initial.add(value);
    }
    IntColumn difference =  initial.difference();
    assertEquals("Both sets of data should be the same size.", expectedValues.length, difference.size());
    for (int index = 0; index < difference.size(); index++ ) {
      int actual = difference.get(index);
      assertEquals("difference operation at index:" + index + " failed",  expectedValues[index], actual);
    }
  }
}