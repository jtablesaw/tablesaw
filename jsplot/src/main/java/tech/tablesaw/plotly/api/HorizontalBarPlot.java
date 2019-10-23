package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.BarTrace.Orientation;

public class HorizontalBarPlot extends BarPlot {

  public static Figure create(
      String title, Table table, String groupColName, String numberColName) {
    return BarPlot.create(Orientation.HORIZONTAL, title, table, groupColName, numberColName);
  }

  public static Figure create(
      Layout layout, Table table, String groupColName, String numberColName) {
    return BarPlot.create(Orientation.HORIZONTAL, layout, table, groupColName, numberColName);
  }

  public static Figure create(
      String title,
      Table table,
      String groupColName,
      Layout.BarMode barMode,
      String... numberColNames) {
    return BarPlot.create(
        Orientation.HORIZONTAL, title, table, groupColName, barMode, numberColNames);
  }

  public static Figure create(
      Layout layout, Table table, String groupColName, String... numberColNames) {
    return BarPlot.create(Orientation.HORIZONTAL, layout, table, groupColName, numberColNames);
  }
}
