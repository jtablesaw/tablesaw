package com.github.lwhite1.tablesaw.smile.plot;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.api.Table;

/**
 *
 */
public class PlotTest {

  public static void main(String[] args) throws Exception {
    Table baseball = Table.createFromCsv("data/baseball.csv");
    NumericColumn runsScored = baseball.numericColumn("RS");
    NumericColumn wins = baseball.numericColumn("W");

    Scatter.show("Runs Scored x Wins", runsScored, wins);
    Density.show(wins);
  }
}