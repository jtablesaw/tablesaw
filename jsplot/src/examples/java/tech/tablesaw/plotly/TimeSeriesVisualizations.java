package tech.tablesaw.plotly;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.api.AreaPlot;
import tech.tablesaw.plotly.api.LinePlot;
import tech.tablesaw.plotly.api.TimeSeriesPlot;
import tech.tablesaw.plotly.components.Figure;

public class TimeSeriesVisualizations {

    public static void main(String[] args) throws Exception {
        Table bush = Table.read().csv("../data/bush.csv");
        Table foxOnly = bush.where(bush.stringColumn("who").equalsIgnoreCase("fox"));
        Figure foxPlot = TimeSeriesPlot.create("George W. Bush approval ratings", foxOnly, "date", "approval");

        System.out.println(foxPlot.asJavascript("div"));
        Plot.show(TimeSeriesPlot.create("George W. Bush approval ratings", bush, "date", "approval", "who"));

        Table robberies = Table.read().csv("../data/boston-robberies.csv");
        Plot.show(LinePlot.create("Boston Robberies by month: Jan 1966-Oct 1975", robberies, "Record", "Robberies"));
        Plot.show(AreaPlot.create("Boston Robberies by month: Jan 1966-Oct 1975", robberies, "Record", "Robberies"));
    }
}
