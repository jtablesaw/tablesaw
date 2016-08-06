package com.github.lwhite1.tablesaw.index;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.LongColumnUtils;
import com.github.lwhite1.tablesaw.io.csv.CsvReader;
import com.github.lwhite1.tablesaw.util.Selection;

import org.junit.Before;
import org.junit.Test;

import static com.github.lwhite1.tablesaw.api.ColumnType.CATEGORY;
import static com.github.lwhite1.tablesaw.api.ColumnType.LOCAL_DATE;
import static com.github.lwhite1.tablesaw.api.ColumnType.LONG_INT;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class LongIndexTest {
  private ColumnType[] types = {
      LOCAL_DATE,     // date of poll
      LONG_INT,       // approval rating (pct)
      CATEGORY        // polling org
  };

  private LongIndex index;
  private Table table;

  @Before
  public void setUp() throws Exception {
    table = CsvReader.read(types, "data/BushApproval.csv");
    index = new LongIndex(table.longColumn("approval"));
  }

  @Test
  public void testGet() {
    Selection fromCol = table.longColumn("approval").select(LongColumnUtils.isEqualTo, 71);
    Selection fromIdx = index.get(71);
    assertEquals(fromCol, fromIdx);
  }

  @Test
  public void testGTE() {
    Selection fromCol = table.longColumn("approval").select(LongColumnUtils.isGreaterThanOrEqualTo, 71);
    Selection fromIdx = index.atLeast(71);
    assertEquals(fromCol, fromIdx);
  }

  @Test
  public void testLTE() {
    Selection fromCol = table.longColumn("approval").select(LongColumnUtils.isLessThanOrEqualTo, 71);
    Selection fromIdx = index.atMost(71);
    assertEquals(fromCol, fromIdx);
  }

  @Test
  public void testLT() {
    Selection fromCol = table.longColumn("approval").select(LongColumnUtils.isLessThan, 71);
    Selection fromIdx = index.lessThan(71);
    assertEquals(fromCol, fromIdx);
  }

  @Test
  public void testGT() {
    Selection fromCol = table.longColumn("approval").select(LongColumnUtils.isGreaterThan, 71);
    Selection fromIdx = index.greaterThan(71);
    assertEquals(fromCol, fromIdx);
  }
}