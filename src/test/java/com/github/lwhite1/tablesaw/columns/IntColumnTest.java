package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.api.FloatColumn;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.filtering.IntPredicate;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;
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
    assertEquals(0, column.min());
  }

  @Test
  public void testMax() {
    for (int i = 0; i < 100; i++) {
      column.add(i);
    }
    assertEquals(99, column.max());
  }

  @Test
  public void testIsLessThan() {
    for (int i = 0; i < 100; i++) {
      column.add(i);
    }
    assertEquals(50, column.isLessThan(50).getCardinality());
  }

  @Test
  public void testIsGreaterThan() {
    for (int i = 0; i < 100; i++) {
      column.add(i);
    }
    assertEquals(49, column.isGreaterThan(50).getCardinality());
  }

  @Test
  public void testIsGreaterThanOrEqualTo() {
    for (int i = 0; i < 100; i++) {
      column.add(i);
    }
    assertEquals(50, column.isGreaterThanOrEqualTo(50).getCardinality());
    assertEquals(50, column.isGreaterThanOrEqualTo(50).select(0));
  }

  @Test
  public void testIsLessThanOrEqualTo() {
    for (int i = 0; i < 100; i++) {
      column.add(i);
    }
    assertEquals(51, column.isLessThanOrEqualTo(50).getCardinality());
    assertEquals(49, column.isLessThanOrEqualTo(50).select(49));
  }

  @Test
  public void testIsEqualTo() {
    for (int i = 0; i < 100; i++) {
      column.add(i);
    }
    assertEquals(1, column.isEqualTo(10).getCardinality());
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

    IntColumn column2 = column.select(column.apply(predicate));
    assertEquals(10, column2.size());
    for (int i = 0; i < 10; i++) {
      assertTrue(column1.get(i) < 10);
    }
    for (int i = 0; i < 10; i++) {
      assertTrue(column2.get(i) < 10);
    }
  }
}