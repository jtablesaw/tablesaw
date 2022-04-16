package tech.tablesaw.examples;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.AreaPlot;

public class AreaPlotExample {

  /** Creates a simple area plot with a temporal x axis (month and year) */
  public static void main(String[] args) throws Exception {

    Table robberies = Table.read().csv("../data/boston-robberies.csv");

    Plot.show(
        AreaPlot.create(
            "Boston Robberies by month: Jan 1966-Oct 1975", robberies, "Record", "Robberies"));
  }
}
