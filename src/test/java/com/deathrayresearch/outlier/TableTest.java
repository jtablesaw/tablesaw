package com.deathrayresearch.outlier;

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

  private Relation table;
  private Column column = new FloatColumn("f1");

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
    FloatColumn floatColumn = (FloatColumn) column;
    floatColumn.add(2f);
    assertEquals(1, table.rowCount());

    floatColumn.add(2.2342f);
    assertEquals(2, table.rowCount());
  }

  @Test
  public void testSelectIf() {

  }

  @Test
  public void testRun() {
    Table table = new Table("daf");
    RandomDataGenerator random = new RandomDataGenerator();
    int max = 100_000_000;
    for (int i = 100; i <= max; i *= 10) {
      IntColumn column1 = new IntColumn("f1", max);
      table.addColumn(column1);
      for (int k = 0; k < max; k++) {
        column1.add(k);
      }

      Stopwatch stopwatch = Stopwatch.createStarted();

      View view = new View(table, "f1");
      int v = random.nextInt(0, max);
      View view2 = view.where(column1.isEqualTo(v));
      view2.print();

      System.out.println("Elapsed time in ms " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }
  }
}