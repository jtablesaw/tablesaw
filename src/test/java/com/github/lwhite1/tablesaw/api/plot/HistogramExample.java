package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.api.Table;

/**
 *
 */
public class HistogramExample {

  public static void main(String[] args) throws Exception {
    Table baseball = Table.createFromCsv("data/baseball.csv");
    NumericColumn x = baseball.nCol("BA");
    Histogram.show("Distribution of team batting averages", x);
  }
}