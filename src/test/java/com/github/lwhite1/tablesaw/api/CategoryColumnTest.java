package com.github.lwhite1.tablesaw.api;

import com.github.lwhite1.tablesaw.TestDataUtil;
import com.github.lwhite1.tablesaw.api.BooleanColumn;
import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.ColumnType;
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
  public void testDefaultReturnValue() {
    assertEquals(-1, column.dictionaryMap().get("test"));
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
    assertEquals("Category column: testing", column.toString());
  }

  @Test
  public void testMax() {
    CategoryColumn categoryColumn = CategoryColumn.create("US States");
    categoryColumn.addAll(TestDataUtil.usStates());
    assertTrue("Wyoming".equals(categoryColumn.top(5).get(0)));
  }

  @Test
  public void testMin() {
    CategoryColumn categoryColumn = CategoryColumn.create("US States");
    categoryColumn.addAll(TestDataUtil.usStates());
    assertTrue("Alabama".equals(categoryColumn.bottom(5).get(0)));
  }
}