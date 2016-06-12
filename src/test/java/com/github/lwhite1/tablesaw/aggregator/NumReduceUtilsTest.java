package com.github.lwhite1.tablesaw.aggregator;

import com.github.lwhite1.tablesaw.Table;
import com.github.lwhite1.tablesaw.TableGroup;
import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.io.CsvReader;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class NumReduceUtilsTest {

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
    double result = table.reduce("approval", NumReduceUtils.mean);
    System.out.println(result);
  }

  @Test
  public void testGroupMean() {
    Column byColumn = table.column("who");
    TableGroup group = new TableGroup(table, byColumn);
    Table result = group.reduce("approval", NumReduceUtils.mean);
    System.out.println(result.print());
  }
}