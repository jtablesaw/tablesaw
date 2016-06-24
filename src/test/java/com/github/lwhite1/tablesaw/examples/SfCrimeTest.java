package com.github.lwhite1.tablesaw.examples;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.CategoryColumn;
import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.columns.IntColumn;
import com.github.lwhite1.tablesaw.io.CsvReader;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.github.lwhite1.tablesaw.api.ColumnType.*;

/**
 *
 */
public class SfCrimeTest {

  private ColumnType[] heading = {
      LOCAL_DATE_TIME,   // date and time
      CATEGORY,   // category
      CATEGORY,   // description
      CATEGORY,   // day of week
      CATEGORY,  // PD district
      CATEGORY, // resolution
      CATEGORY, // address
      FLOAT, // lon
      FLOAT, // lat
 };

  private Table table;

  @Before
  public void setUp() throws Exception {
    table = CsvReader.read(heading, "bigdata/train.csv");
  }

  @Test
  @Ignore  // uses a lot of memory
  public void test() {

    out(table.columnNames().toString());

    out(table.first(4).print());

    out(table.columnCount());
    CategoryColumn district = table.categoryColumn("PdDistrict");
    out(district.summary().print());

    CategoryColumn dayOfWeek = table.dateTimeColumn("Dates").dayOfWeek();
    table.addColumn(dayOfWeek);

    IntColumn dayOfYear = table.dateTimeColumn("Dates").dayOfYear();
    table.addColumn(dayOfYear);

    IntColumn year = table.dateTimeColumn("Dates").year();
    table.addColumn(year);

    out(table.first(100).print());

/*
    // eliminate the duplicate rows so we can sum by state correctly;
    table = table.selectWhere(column("SN").isEqualTo(1));

    Table xtab = table.xCount("State", "Year");
    out("Cross Tab: State by Year");
    out(xtab.print());

    Table xtab2 = table.xCount("State", "Month");
    out(xtab2.print());

    out(xtab.column("total").copy(0, 48).summary().print());

    out(xtab.selectWhere(column("value").isEqualTo("Total")).print());
    out(xtab.selectWhere(column("value").isEqualTo("TX")).print());
    out(xtab.selectWhere(column("value").isNotEqualTo("TX")).print());

    out(table.realColumn("Width").rowSummary().print());

    out(table.realColumn("Length").summary().print());

    out(table.selectWhere(column("Width").isGreaterThan(1500l)).print());

    long maxFatalities = Math.round(table.realColumn("Fatalities").max());

    Table max = table.selectWhere(column("Fatalities").isEqualTo(maxFatalities));
*/

   // out(max.print());
  }

  private static void out(Object str) {
    System.out.println(String.valueOf(str));
  }
}
