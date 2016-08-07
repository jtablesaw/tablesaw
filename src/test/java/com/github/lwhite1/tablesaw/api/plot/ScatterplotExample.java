package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.api.Table;

/**
 *
 */
public class ScatterplotExample {

  public static void main(String[] args) throws Exception {
    Table baseball = Table.createFromCsv("data/baseball.csv");
    NumericColumn x = baseball.nCol("BA");
    NumericColumn y = baseball.nCol("W");
    Scatter.show(x, y);

    Scatter.show("Regular season wins by year",
        baseball.numericColumn("W"),
        baseball.numericColumn("Year"),
        baseball.splitOn(baseball.column("Playoffs")));
  }
}