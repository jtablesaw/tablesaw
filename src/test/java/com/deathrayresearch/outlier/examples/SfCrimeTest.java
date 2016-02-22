package com.deathrayresearch.outlier.examples;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.columns.CategoryColumn;
import com.deathrayresearch.outlier.columns.ColumnType;
import com.deathrayresearch.outlier.columns.IntColumn;
import com.deathrayresearch.outlier.io.CsvReader;
import org.junit.Before;
import org.junit.Test;

import static com.deathrayresearch.outlier.columns.ColumnType.*;

/**
 *
 */
public class SfCrimeTest {

  ColumnType[] heading = {
      LOCAL_DATE_TIME,   // date and time
      CAT,   // category
      TEXT,   // description
      CAT,   // day of week
      CAT,  // PD district
      CAT, // resolution
      TEXT, // address
      FLOAT, // lon
      FLOAT, // lat
 };

  Table table;

  @Before
  public void setUp() throws Exception {
    table = CsvReader.read("data/train.csv", heading);
  }

  @Test
  public void test() {

    out(table.columnNames().toString());

    out(table.head(4).print());

    out("" + table.columnCount());
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

  private void out(String str) {
    System.out.println(str);
  }
}
