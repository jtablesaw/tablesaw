package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.api.Table;

/**
 *
 */
public class LinePlotExample {

  public static void main(String[] args) throws Exception {
    Table baseball = Table.createFromCsv("data/boston-robberies.csv");
    NumericColumn x = baseball.nCol("Record");
    NumericColumn y = baseball.nCol("Robberies");
    Line.show("Monthly Boston Armed Robberies Jan. 1966 - Oct. 1975", x, y);
  }
}