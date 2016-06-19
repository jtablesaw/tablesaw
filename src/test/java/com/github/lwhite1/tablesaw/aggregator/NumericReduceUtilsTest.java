package com.github.lwhite1.tablesaw.aggregator;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.table.TableGroup;
import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.io.CsvReader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class NumericReduceUtilsTest {

  private static ColumnType[] types = {
      ColumnType.LOCAL_DATE,     // date of poll
      ColumnType.INTEGER,        // approval rating (pct)
      ColumnType.CATEGORY        // polling org
  };

  private Table table;

  @Before
  public void setUp() throws Exception {
    table = CsvReader.read(types, "data/BushApproval.csv");
  }

  @Test
  public void testMean() {
    double result = table.reduce("approval", NumericReduceUtils.mean);
    assertEquals(64.88235294117646, result, 0.01);
  }

  @Test
  public void testGroupMean() {
    Column byColumn = table.column("who");
    TableGroup group = new TableGroup(table, byColumn);
    Table result = group.reduce("approval", NumericReduceUtils.mean);
    assertEquals(2, result.columnCount());
    assertEquals("Group", result.column(0).name());
    assertEquals(6, result.rowCount());
    assertEquals("65.671875", result.get(1, 0));
  }
}