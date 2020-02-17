package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.traces.BarTrace.Orientation;

public class ParetoPlot extends BarPlot {

  public static Figure showHorizontal(
      String title, Table table, String groupColName, String numberColName) {
    return create(
        Orientation.HORIZONTAL,
        title,
        table.sortDescendingOn(numberColName),
        groupColName,
        numberColName);
  }

  public static Figure createVertical(
      String title, Table table, String groupColName, String numberColName) {
    return create(
        Orientation.VERTICAL,
        title,
        table.sortDescendingOn(numberColName),
        groupColName,
        numberColName);
  }

  public static Figure create(
      String title, Table table, String groupColName, String numberColName) {
    return createVertical(
        title, table.sortDescendingOn(numberColName), groupColName, numberColName);
  }
}
