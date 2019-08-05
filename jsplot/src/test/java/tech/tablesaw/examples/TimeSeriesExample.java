package tech.tablesaw.examples;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.TimeSeriesPlot;

public class TimeSeriesExample {

  public static void main(String[] args) throws Exception {
    Table bush = Table.read().csv("../data/bush.csv");
    bush = bush.where(bush.stringColumn("who").equalsIgnoreCase("fox"));
    Plot.show(
        TimeSeriesPlot.create("Fox approval ratings for George W. Bush", bush, "date", "approval"));
  }
}
