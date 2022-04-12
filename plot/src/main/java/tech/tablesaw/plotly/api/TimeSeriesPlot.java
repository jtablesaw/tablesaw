package tech.tablesaw.plotly.api;

import java.util.List;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.InstantColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.wrappers.Scatter;
import tech.tablesaw.table.TableSliceGroup;

public class TimeSeriesPlot {

  public static Figure create(
      String title, Table table, String dateColX, String yCol, String groupCol) {

    TableSliceGroup tables = table.splitOn(table.categoricalColumn(groupCol));

    Layout layout = Layout.builder(title, dateColX, yCol).build();

    Scatter[] traces = new Scatter[tables.size()];
    for (int i = 0; i < tables.size(); i++) {
      List<Table> tableList = tables.asTableList();
      Table t = tableList.get(i).sortOn(dateColX);
      traces[i] =
          Scatter.builder(t.dateColumn(dateColX), t.numberColumn(yCol))
              .showLegend(true)
              .name(tableList.get(i).name())
              .mode(Scatter.Mode.LINE)
              .build();
    }
    return new Figure(layout, traces);
  }

  public static Figure create(String title, Table table, String dateColXName, String yColName) {
    Layout layout = Layout.builder(title, dateColXName, yColName).build();
    Scatter trace =
        Scatter.builder(table.column(dateColXName), table.numberColumn(yColName))
            .mode(Scatter.Mode.LINE)
            .build();
    return new Figure(layout, trace);
  }

  public static Figure create(
      String title, String xTitle, DateColumn xCol, String yTitle, NumericColumn<?> yCol) {
    Layout layout = Layout.builder(title, xTitle, yTitle).build();
    Scatter trace = Scatter.builder(xCol, yCol).mode(Scatter.Mode.LINE).build();
    return new Figure(layout, trace);
  }

  public static Figure create(
      String title, String xTitle, DateTimeColumn xCol, String yTitle, NumericColumn<?> yCol) {
    Layout layout = Layout.builder(title, xTitle, yTitle).build();
    Scatter trace = Scatter.builder(xCol, yCol).mode(Scatter.Mode.LINE).build();
    return new Figure(layout, trace);
  }

  public static Figure create(
      String title, String xTitle, InstantColumn xCol, String yTitle, NumericColumn<?> yCol) {
    Layout layout = Layout.builder(title, xTitle, yTitle).build();
    Scatter trace = Scatter.builder(xCol, yCol).mode(Scatter.Mode.LINE).build();
    return new Figure(layout, trace);
  }

  /**
   * Creates a time series where the x values are from a DateTimeColumn, rather than a DateColumn
   *
   * @param title The title of the plot
   * @param table The table containing the source data
   * @param dateTimeColumnName The name of a DateTimeColumn
   * @param numberColumnName The name of a NumberColumn
   * @return The figure to be displayed
   */
  public static Figure createDateTimeSeries(
      String title, Table table, String dateTimeColumnName, String numberColumnName) {

    DateTimeColumn xCol = table.dateTimeColumn(dateTimeColumnName);
    NumericColumn<?> yCol = table.numberColumn(numberColumnName);

    Layout layout = Layout.builder(title, xCol.name(), yCol.name()).build();

    Scatter trace = Scatter.builder(xCol, yCol).mode(Scatter.Mode.LINE).build();
    return new Figure(layout, trace);
  }
}
