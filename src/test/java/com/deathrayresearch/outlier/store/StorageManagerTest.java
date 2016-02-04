package com.deathrayresearch.outlier.store;

import com.deathrayresearch.outlier.FloatColumn;
import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.Relation;
import com.google.common.base.Stopwatch;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 *
 */
public class StorageManagerTest {

  private static final int COUNT = 100_000_000;

  Relation table = new Table("t");
  FloatColumn floatColumn = new FloatColumn("test", COUNT);

  @Before
  public void setUp() throws Exception {

    for (int i = 0; i < COUNT; i++) {
      floatColumn.add((float) i);
    }
    table.addColumn(floatColumn);
  }

  @Test
  public void testReadTable() {
    System.out.println(floatColumn.size());
  }

  @Test
  public void testWriteTable() throws IOException {
    Stopwatch stopwatch = Stopwatch.createStarted();
    StorageManager.write("databases", table);
    System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS));

  }

  @Test
  public void testRead() {

  }

  @Test
  public void testReadFloatColumn() throws IOException {
    Stopwatch stopwatch = Stopwatch.createStarted();
    FloatColumn floatColumn1 = StorageManager.readFloatColumn("test_col", "test");
    assertEquals(COUNT, floatColumn1.size());
    System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS));
  }

  @Test
  public void testWrite() {
  }

  @Test
  public void testWriteColumn() throws IOException {
    Stopwatch stopwatch = Stopwatch.createStarted();
    StorageManager.writeColumn("test_col", floatColumn);
    System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS));
  }
}