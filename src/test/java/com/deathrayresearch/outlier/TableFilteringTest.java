package com.deathrayresearch.outlier;

import com.deathrayresearch.outlier.api.ColumnType;
import com.deathrayresearch.outlier.io.CsvReader;
import org.junit.Before;
import org.junit.Test;

import static com.deathrayresearch.outlier.api.QueryHelper.*;

import static com.deathrayresearch.outlier.api.ColumnType.LOCAL_DATE;
import static com.deathrayresearch.outlier.api.ColumnType.INTEGER;
import static com.deathrayresearch.outlier.api.ColumnType.CATEGORY;

/**
 *
 */
public class TableFilteringTest {

  ColumnType[] types = {
      LOCAL_DATE,     // date of poll
      INTEGER,        // approval rating (pct)
      CATEGORY             // polling org
  };

  Table table;

  @Before
  public void setUp() throws Exception {
    table = CsvReader.read(types, "data/BushApproval.csv");
    System.out.println(table.columns());
  }

  @Test
  public void testFilter1() {
    Table result = table.selectIf(column("approval").isLessThan(70));
    System.out.println(result.print());
  }

  @Test
  public void testFilter2() {
    Table result = table.selectIf(column("date").isInApril());
    System.out.println(result.print());
  }

  @Test
  public void testFilter3() {
    Table result = table.selectIf(
        both(column("date").isInApril(),
             column("approval").isGreaterThan(70)));

    System.out.println(result.print());
  }
}
