package com.deathrayresearch.outlier.columns;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class CategoryColumnTest {

  CategoryColumn column = new CategoryColumn("testing");

  @Before
  public void setUp() throws Exception {
    column.add("Value 1");
    column.add("Value 2");
    column.add("Value 3");
    column.add("Value 4");
  }

  @Test
  public void testType() {

  }

  @Test
  public void testGetString() {

  }

  @Test
  public void testSize() {

  }

  @Test
  public void testGetDummies() {
    List<BooleanColumn> dummies = column.getDummies();
    assertEquals(4, dummies.size());

  }

  @Test
  public void testToString() {
    System.out.println(column.toString());
  }
}