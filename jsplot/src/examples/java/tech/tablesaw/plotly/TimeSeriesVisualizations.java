package tech.tablesaw.plotly;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.api.AreaPlot;
import tech.tablesaw.plotly.api.LinePlot;
import tech.tablesaw.plotly.api.TimeSeriesPlot;

public class TimeSeriesVisualizations {

    public static void main(String[] args) throws Exception {
        Table bush = Table.read().csv("../data/bush.csv");
        Table foxOnly = bush.where(bush.stringColumn("who").equalsIgnoreCase("fox"));
        TimeSeriesPlot.show("George W. Bush approval ratings", foxOnly, "date", "approval");

        TimeSeriesPlot.show("George W. Bush approval ratings", bush, "date", "approval", "who");


        Table robberies = Table.read().csv("../data/boston-robberies.csv");
        LinePlot.show("Boston Robberies by month: Jan 1966-Oct 1975", robberies, "Record", "Robberies");
        AreaPlot.show("Boston Robberies by month: Jan 1966-Oct 1975", robberies, "Record", "Robberies");
    }
}
