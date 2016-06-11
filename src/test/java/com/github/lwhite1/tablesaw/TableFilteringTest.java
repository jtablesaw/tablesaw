package com.github.lwhite1.tablesaw;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.io.CsvReader;
import org.junit.Before;
import org.junit.Test;

import static com.github.lwhite1.tablesaw.api.QueryHelper.*;

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
    Table result = table.selectWhere(column("approval").isLessThan(70));
    System.out.println(result.print());
  }

  @Test
  public void testFilter2() {
    Table result = table.selectWhere(column("date").isInApril());
    System.out.println(result.print());
  }

  @Test
  public void testFilter3() {
    Table result = table.selectWhere(
        both(column("date").isInApril(),
             column("approval").isGreaterThan(70)));

    System.out.println(result.print());
  }

  @Test
  public void testFilter4() {
    Table result =
        table.select("who", "approval")
            .where(
                and(column("date").isInApril(),
                     column("approval").isGreaterThan(70)));
    System.out.println(result.print());
  }
}
