package tech.tablesaw.plotly;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.api.LinePlot;
import tech.tablesaw.plotly.api.TimeSeriesPlot;

public class TimeSeriesVisualizations {

    public static void main(String[] args) throws Exception {
        Table bush = Table.read().csv("../data/bush.csv");
        TimeSeriesPlot.show("George W. Bush approval ratings", bush, "date", "approval", "who");


        Table robberies = Table.read().csv("../data/boston-robberies.csv");
        LinePlot.show("Boston Robberies by month: Jan 1966-Oct 1975", robberies, "Record", "Robberies");

    }
}
