package com.github.lwhite1.outlier;

import com.github.lwhite1.outlier.api.ColumnType;
import com.github.lwhite1.outlier.api.QueryHelper;
import com.github.lwhite1.outlier.io.CsvReader;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class TableFilteringTest {

  ColumnType[] types = {
      ColumnType.LOCAL_DATE,     // date of poll
      ColumnType.INTEGER,        // approval rating (pct)
      ColumnType.CATEGORY             // polling org
  };

  Table table;

  @Before
  public void setUp() throws Exception {
    table = CsvReader.read(types, "data/BushApproval.csv");
    System.out.println(table.columns());
  }

  @Test
  public void testFilter1() {
    Table result = table.selectIf(QueryHelper.column("approval").isLessThan(70));
    System.out.println(result.print());
  }

  @Test
  public void testFilter2() {
    Table result = table.selectIf(QueryHelper.column("date").isInApril());
    System.out.println(result.print());
  }

  @Test
  public void testFilter3() {
    Table result = table.selectIf(
        QueryHelper.both(QueryHelper.column("date").isInApril(),
             QueryHelper.column("approval").isGreaterThan(70)));

    System.out.println(result.print());
  }
}
