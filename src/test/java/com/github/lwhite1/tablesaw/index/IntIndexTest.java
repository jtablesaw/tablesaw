package com.github.lwhite1.tablesaw.index;

import com.github.lwhite1.tablesaw.Table;
import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.columns.IntColumn;
import com.github.lwhite1.tablesaw.columns.IntColumnUtils;
import com.github.lwhite1.tablesaw.io.CsvReader;
import com.github.lwhite1.tablesaw.store.StorageManager;
import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.roaringbitmap.RoaringBitmap;

import java.util.concurrent.TimeUnit;

import static com.github.lwhite1.tablesaw.api.ColumnType.*;
import static java.lang.System.out;
import static org.junit.Assert.*;

/**
 *
 */
public class IntIndexTest {

  private ColumnType[] types = {
      LOCAL_DATE,     // date of poll
      INTEGER,        // approval rating (pct)
      CATEGORY        // polling org
  };

  private IntIndex index;
  private Table table;

  @Before
  public void setUp() throws Exception {
    Stopwatch stopwatch = Stopwatch.createStarted();
    table = CsvReader.read(types, "data/BushApproval.csv");
    out.println("Loaded from column store in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
    index = new IntIndex(table.intColumn("approval"));
  }

  @Test
  public void testConstructor() {
    out.println("Done");
  }

  @Test
  public void testGet() {
    RoaringBitmap fromCol = table.intColumn("approval").apply(IntColumnUtils.isEqualTo, 71);
    RoaringBitmap fromIdx = index.get(71);
    assertEquals(fromCol, fromIdx);
  }

  @Test
  public void testGTE() {
    RoaringBitmap fromCol = table.intColumn("approval").apply(IntColumnUtils.isGreaterThanOrEqualTo, 71);
    RoaringBitmap fromIdx = index.atLeast(71);
    assertEquals(fromCol, fromIdx);
  }

  @Test
  public void testLTE() {
    RoaringBitmap fromCol = table.intColumn("approval").apply(IntColumnUtils.isLessThanOrEqualTo, 71);
    RoaringBitmap fromIdx = index.atMost(71);
    assertEquals(fromCol, fromIdx);
  }

  @Test
  public void testLT() {
    RoaringBitmap fromCol = table.intColumn("approval").apply(IntColumnUtils.isLessThan, 71);
    RoaringBitmap fromIdx = index.lessThan(71);
    assertEquals(fromCol, fromIdx);
  }

  @Test
  public void testGT() {
    RoaringBitmap fromCol = table.intColumn("approval").apply(IntColumnUtils.isGreaterThan, 71);
    RoaringBitmap fromIdx = index.greaterThan(71);
    assertEquals(fromCol, fromIdx);
  }
}