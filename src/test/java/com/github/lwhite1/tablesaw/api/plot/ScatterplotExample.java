package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.api.Table;

import static com.github.lwhite1.tablesaw.api.QueryHelper.column;

/**
 *
 */
public class ScatterplotExample {

  public static void main(String[] args) throws Exception {
    Table baseball = Table.createFromCsv("data/baseball.csv");
    NumericColumn x = baseball.nCol("BA");
    NumericColumn y = baseball.nCol("W");
   // System.out.println(baseball.summarize("w", sum).by("year").print());
    Scatter.show(x, y);

    //baseball = baseball.selectWhere(column("playoffs").isLessThan(1));
    baseball = baseball.selectWhere(column("year").isLessThan(1980));
    Scatter.show("Regular season wins by year",
        baseball.numericColumn("W"),
        baseball.numericColumn("Year"),
        baseball.splitOn(baseball.column("Playoffs")));
  }
}