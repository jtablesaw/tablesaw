package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.Table;

import static com.github.lwhite1.tablesaw.api.plot.Bar.show;
import static com.github.lwhite1.tablesaw.reducing.NumericReduceUtils.sum;


/**
 *  Basic sample vertical bar chart
 */
public class BarExample {


  public static void main(String[] args) throws Exception {
    Table table = Table.createFromCsv("data/tornadoes_1950-2014.csv");
    Table t2 = table.countBy(table.categoryColumn("State"));
    //show("tornadoes by state", t2.categoryColumn("Category"), t2.numericColumn("Count"));

    //show("T", table.summarize("fatalities", sum).by("State"));
    show("T", table.summarize("fatalities", sum).by("Scale"));
  }
}
