package com.deathrayresearch.outlier;

import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.FloatColumn;
import com.deathrayresearch.outlier.columns.IntColumn;
import com.google.common.base.Stopwatch;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for Table
 */
public class TableTest {

  private Table table;
  private FloatColumn column = new FloatColumn("f1");

  @Before
  public void setUp() throws Exception {
    table = new Table("t");
    table.addColumn(column);
  }

  @Test
  public void testColumn() throws Exception {
    Column column1 = table.column(0);
    assertNotNull(column1);
  }

  @Test
  public void testColumnCount() throws Exception {
    assertEquals(0, new Table("t").columnCount());
    assertEquals(1, table.columnCount());
  }

  @Test
  public void testRowCount() throws Exception {
    assertEquals(0, table.rowCount());
    FloatColumn floatColumn = column;
    floatColumn.add(2f);
    assertEquals(1, table.rowCount());

    floatColumn.add(2.2342f);
    assertEquals(2, table.rowCount());
  }
}