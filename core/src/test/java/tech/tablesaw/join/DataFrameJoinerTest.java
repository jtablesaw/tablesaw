package tech.tablesaw.join;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import tech.tablesaw.api.Table;

public class DataFrameJoinerTest {

  private static final Table ONE_YEAR = Table.read().csv(
      "Date,1 Yr Treasury Rate\n"
          + "\"Dec 1, 2017\",1.65%\n"
          + "\"Nov 1, 2017\",1.56%\n"
          + "\"Oct 1, 2017\",1.40%\n"
          + "\"Sep 1, 2017\",1.28%\n"
          + "\"Aug 1, 2017\",1.23%\n"
          + "\"Jul 1, 2017\",1.22%\n",
      "1 Yr Treasury Rate");

  private static final Table SP500 = Table.read().csv(
      "Date,S&P 500\n"
          + "\"Nov 1, 2017\",2579.36\n"
          + "\"Oct 1, 2017\",2521.20\n"
          + "\"Sep 1, 2017\",2474.42\n"
          + "\"Aug 1, 2017\",2477.10\n"
          + "\"Jul 1, 2017\",2431.39\n"
          + "\"Jun 1, 2017\",2430.06\n",
      "S&P 500");

  @Test
  public void innerJoin() {
    Table joined = SP500.join("Date").inner(ONE_YEAR, "Date");
    assertEquals(3, joined.columnCount());
    assertEquals(5, joined.rowCount());
  }

}
