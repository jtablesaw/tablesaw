package tech.tablesaw.plotly;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.api.TimeSeriesPlot;

public class TimeSeriesVisualizations {

    public static void main(String[] args) throws Exception {
        Table bush = Table.read().csv("../data/bush.csv");
        TimeSeriesPlot.show("George W. Bush approval ratings", bush, "date", "approval", "who");
    }
}
