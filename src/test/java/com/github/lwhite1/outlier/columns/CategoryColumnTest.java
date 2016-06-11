package com.github.lwhite1.outlier.columns;

import com.github.lwhite1.outlier.api.ColumnType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class CategoryColumnTest {

  private CategoryColumn column = CategoryColumn.create("testing");

  @Before
  public void setUp() throws Exception {
    column.add("Value 1");
    column.add("Value 2");
    column.add("Value 3");
    column.add("Value 4");
  }

  @Test
  public void testType() {
    Assert.assertEquals(ColumnType.CATEGORY, column.type());
  }

  @Test
  public void testGetString() {
    assertEquals("Value 2", column.getString(1));
  }

  @Test
  public void testSize() {
    assertEquals(4, column.size());
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