package com.github.lwhite1.tablesaw.examples;

import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.ShortColumn;
import com.github.lwhite1.tablesaw.api.Table;

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


  public static void main(String[] args) throws Exception {

    Table table = Table.createFromCsv("/Users/larrywhite/IdeaProjects/testdata/bigdata/train.csv");

    out(table.columnNames().toString());

    out(table.first(4).print());

    out(table.columnCount());
    CategoryColumn district = table.categoryColumn("PdDistrict");
    out(district.summary().print());

    ShortColumn dayOfWeekValue = table.dateTimeColumn("Dates").dayOfWeekValue();
    table.addColumn(dayOfWeekValue);

    ShortColumn dayOfYear = table.dateTimeColumn("Dates").dayOfYear();
    table.addColumn(dayOfYear);

    ShortColumn year = table.dateTimeColumn("Dates").year();
    table.addColumn(year);

    out(table.first(100).print());
  }

  private static void out(Object str) {
    System.out.println(String.valueOf(str));
  }
}
