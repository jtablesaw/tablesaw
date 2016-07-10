package com.github.lwhite1.tablesaw.examples;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.io.csv.CsvReader;
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
  }

  private static void out(Object str) {
    System.out.println(String.valueOf(str));
  }
}
