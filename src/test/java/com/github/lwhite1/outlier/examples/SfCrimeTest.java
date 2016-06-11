package com.github.lwhite1.outlier.examples;

import com.github.lwhite1.outlier.Table;
import com.github.lwhite1.outlier.columns.CategoryColumn;
import com.github.lwhite1.outlier.api.ColumnType;
import com.github.lwhite1.outlier.columns.IntColumn;
import com.github.lwhite1.outlier.io.CsvReader;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.github.lwhite1.outlier.api.ColumnType.*;

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

    out(table.head(4).print());

    out(table.columnCount());
    CategoryColumn district = table.categoryColumn("PdDistrict");
    out(district.summary().print());

    CategoryColumn dayOfWeek = table.localDateTimeColumn("Dates").dayOfWeek();
    table.addColumn(dayOfWeek);

    IntColumn dayOfYear = table.localDateTimeColumn("Dates").dayOfYear();
    table.addColumn(dayOfYear);

    IntColumn year = table.localDateTimeColumn("Dates").year();
    table.addColumn(year);

    out(table.head(100).print());

/*
    // eliminate the duplicate rows so we can sum by state correctly;
    table = table.selectIf(column("SN").isEqualTo(1));

    Table xtab = table.xCount("State", "Year");
    out("Cross Tab: State by Year");
    out(xtab.print());

    Table xtab2 = table.xCount("State", "Month");
    out(xtab2.print());

    out(xtab.column("total").copy(0, 48).summary().print());

    out(xtab.selectIf(column("value").isEqualTo("Total")).print());
    out(xtab.selectIf(column("value").isEqualTo("TX")).print());
    out(xtab.selectIf(column("value").isNotEqualTo("TX")).print());

    out(table.realColumn("Width").rowSummary().print());

    out(table.realColumn("Length").summary().print());

    out(table.selectIf(column("Width").isGreaterThan(1500l)).print());

    long maxFatalities = Math.round(table.realColumn("Fatalities").max());

    Table max = table.selectIf(column("Fatalities").isEqualTo(maxFatalities));
*/

   // out(max.print());
  }

  private static void out(Object str) {
    System.out.println(String.valueOf(str));
  }
}
