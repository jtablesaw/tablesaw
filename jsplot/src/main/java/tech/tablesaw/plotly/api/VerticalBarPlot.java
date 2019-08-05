package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.BarTrace.Orientation;

public class VerticalBarPlot extends BarPlot {

  public static Figure create(
      String title, Table table, String groupColName, String numberColName) {
    return BarPlot.create(Orientation.VERTICAL, title, table, groupColName, numberColName);
  }

  public static Figure create(
      String title,
      Table table,
      String groupColName,
      Layout.BarMode barMode,
      String... numberColNames) {
    return BarPlot.create(
        Orientation.VERTICAL, title, table, groupColName, barMode, numberColNames);
  }
}
