package tech.tablesaw.examples;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.AreaPlot;
import tech.tablesaw.plotly.api.CandlestickPlot;
import tech.tablesaw.plotly.api.LinePlot;
import tech.tablesaw.plotly.api.OHLCPlot;
import tech.tablesaw.plotly.api.TimeSeriesPlot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.traces.ScatterTrace;

public class TimeSeriesVisualizations {

    public static void main(String[] args) throws Exception {
        Table bush = Table.read().csv("../data/bush.csv");
        Table foxOnly = bush.where(bush.stringColumn("who").equalsIgnoreCase("fox"));
        Figure foxPlot = TimeSeriesPlot.create("George W. Bush approval ratings", foxOnly, "date", "approval");
        Plot.show(foxPlot);

        Plot.show(TimeSeriesPlot.create("George W. Bush approval ratings", bush, "date", "approval", "who"));

        Table robberies = Table.read().csv("../data/boston-robberies.csv");
        Plot.show(LinePlot.create("Boston Robberies by month: Jan 1966-Oct 1975", robberies, "Record", "Robberies"));

        Plot.show(AreaPlot.create("Boston Robberies by month: Jan 1966-Oct 1975", robberies, "Record", "Robberies"));

        Layout layout =
                Layout.builder("Boston Robberies by month: Jan 1966-Oct 1975", "year", "robberies")
                        .build();

        ScatterTrace trace = ScatterTrace.builder(
                robberies.numberColumn("Record"),
                robberies.numberColumn("Robberies"))
                .mode(ScatterTrace.Mode.LINE)
                .marker(Marker.builder().color("#3D9970").build())
                .fill(ScatterTrace.Fill.TO_NEXT_Y)
                .build();
        Plot.show(new Figure(layout, trace));


        Table priceTable = Table.read().csv("../data/ohlcdata.csv");
        priceTable.numberColumn("Volume").setPrintFormatter(NumberColumnFormatter.intsWithGrouping());
        Plot.show(OHLCPlot.create("Prices", priceTable, "date", "open", "high", "low", "close"));
        Plot.show(CandlestickPlot.create("Prices", priceTable, "date", "open", "high", "low", "close"));

        // using a datetime column
        Table dateTable = Table.read().csv("../data/dateTimeTestFile.csv");
        Plot.show(TimeSeriesPlot.create("Value over time", "time", dateTable.dateTimeColumn("Time"), "values", dateTable.numberColumn("Value")));

        // using a datetime column2
        Plot.show(TimeSeriesPlot.createDateTimeSeries("Value over time", dateTable, "Time", "Value"));
    }
}
