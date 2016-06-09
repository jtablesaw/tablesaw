package com.deathrayresearch.outlier.examples;

import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.api.ColumnType;
import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.io.CsvReader;
import org.junit.Before;
import org.junit.Test;

import static com.deathrayresearch.outlier.api.ColumnType.*;

/**
 * Basic example code
 */
public class GettingStarted {

  private ColumnType[] types = {
      LOCAL_DATE,     // date of poll
      INTEGER,        // approval rating (pct)
      CATEGORY        // polling org
  };

  private Table table;

  @Before
  public void setUp() throws Exception {
    table = CsvReader.read(types, "data/BushApproval.csv");
  }

  @Test
  public void printStructure() throws Exception {
    out(table.structure().print());

    out(table.head(10).print());

    out(table.summary());

    out(table.columnNames());

    Column approval = table.column("approval");
    out(approval.summary().print());

    Column who = table.column("who");
    out(who.summary().print());

    Column date = table.column("date");
    out(date.summary().sortOn("date").print());
  }

  private synchronized void out(Object obj) {
    System.out.println(String.valueOf(obj));
  }

}
