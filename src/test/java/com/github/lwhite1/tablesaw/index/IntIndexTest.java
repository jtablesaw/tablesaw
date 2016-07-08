package com.github.lwhite1.tablesaw.index;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.IntColumnUtils;
import com.github.lwhite1.tablesaw.io.csv.CsvReader;
import com.github.lwhite1.tablesaw.util.Selection;
import com.google.common.base.Stopwatch;
import org.junit.Before;
import org.junit.Test;

import static com.github.lwhite1.tablesaw.api.ColumnType.*;
import static org.junit.Assert.assertEquals;

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
    index = new IntIndex(table.intColumn("approval"));
  }

  @Test
  public void testGet() {
    Selection fromCol = table.intColumn("approval").select(IntColumnUtils.isEqualTo, 71);
    Selection fromIdx = index.get(71);
    assertEquals(fromCol, fromIdx);
  }

  @Test
  public void testGTE() {
    Selection fromCol = table.intColumn("approval").select(IntColumnUtils.isGreaterThanOrEqualTo, 71);
    Selection fromIdx = index.atLeast(71);
    assertEquals(fromCol, fromIdx);
  }

  @Test
  public void testLTE() {
    Selection fromCol = table.intColumn("approval").select(IntColumnUtils.isLessThanOrEqualTo, 71);
    Selection fromIdx = index.atMost(71);
    assertEquals(fromCol, fromIdx);
  }

  @Test
  public void testLT() {
    Selection fromCol = table.intColumn("approval").select(IntColumnUtils.isLessThan, 71);
    Selection fromIdx = index.lessThan(71);
    assertEquals(fromCol, fromIdx);
  }

  @Test
  public void testGT() {
    Selection fromCol = table.intColumn("approval").select(IntColumnUtils.isGreaterThan, 71);
    Selection fromIdx = index.greaterThan(71);
    assertEquals(fromCol, fromIdx);
  }
}