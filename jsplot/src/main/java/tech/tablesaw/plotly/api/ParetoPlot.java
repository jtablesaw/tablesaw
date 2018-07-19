package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.traces.BarTrace.Orientation;

public class ParetoPlot {

    public static void show(Orientation orientation, String title, Table table, String groupColName, String numberColName) {

        Table sorted = table.sortDescendingOn(numberColName);
        BarPlot.show(orientation, title, sorted, groupColName, numberColName);
    }

    public static void showHorizontal(String title, Table table, String groupColName, String numberColName) {
        show(Orientation.HORIZONTAL, title, table, groupColName, numberColName);
    }

    public static void showVertical(String title, Table table, String groupColName, String numberColName) {
        show(Orientation.VERTICAL, title, table, groupColName, numberColName);
    }
}
