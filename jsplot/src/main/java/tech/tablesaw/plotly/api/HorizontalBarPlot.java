package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.traces.BarTrace.Orientation;

public class HorizontalBarPlot extends BarPlot {

    public static void show(String title, Table table, String groupColName, String numberColName) {
        BarPlot.show(Orientation.HORIZONTAL, title, table, groupColName, numberColName);
    }

    public static void show(String title, Table table, String groupColName, String... numberColNames) {
        BarPlot.show(Orientation.HORIZONTAL, title, table, groupColName, numberColNames);
    }
}
