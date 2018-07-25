package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.traces.BarTrace.Orientation;

public class ParetoPlot extends BarPlot {

    public static void showHorizontal(String title, Table table, String groupColName, String numberColName) {
        show(Orientation.HORIZONTAL, title, table.sortDescendingOn(numberColName), groupColName, numberColName);
    }

    public static void showVertical(String title, Table table, String groupColName, String numberColName) {
        show(Orientation.VERTICAL, title, table.sortDescendingOn(numberColName), groupColName, numberColName);
    }

    public static void show(String title, Table table, String groupColName, String numberColName) {
        showVertical(title, table.sortDescendingOn(numberColName), groupColName, numberColName);
    }
}
