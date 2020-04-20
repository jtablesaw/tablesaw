package tech.tablesaw.plotly.api;

import java.util.List;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.components.Marker.MarkerBuilder;
import tech.tablesaw.plotly.components.Marker.SizeMode;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.table.TableSliceGroup;

public class BubblePlot {

  public static Figure create(
      String title, Table table, String xCol, String yCol, String sizeColumn, String groupCol) {

    TableSliceGroup tables = table.splitOn(table.categoricalColumn(groupCol));
    Layout layout = Layout.builder(title, xCol, yCol).showLegend(true).build();

    ScatterTrace[] traces = new ScatterTrace[tables.size()];
    for (int i = 0; i < tables.size(); i++) {
      List<Table> tableList = tables.asTableList();

      Marker marker =
          Marker.builder()
              .size(tableList.get(i).numberColumn(sizeColumn))
              // .opacity(.75)
              .build();

      traces[i] =
          ScatterTrace.builder(
                  tableList.get(i).numberColumn(xCol), tableList.get(i).numberColumn(yCol))
              .showLegend(true)
              .marker(marker)
              .name(tableList.get(i).name())
              .build();
    }
    return new Figure(layout, traces);
  }

  /**
   * create a bubble plot using more options including color/sizeMode/opacity
   *
   * @param title plot title
   * @param xColumn non-nullable, column data for x-axis
   * @param yColumn non-nullable, column data for y-axis
   * @param sizeColumn nullable, indicate the bubble size
   * @param color color for every data point
   * @param sizeMode check {@link SizeMode}
   * @param opacity display opacity
   * @return bubble plot created from given parameters
   */
  public static Figure create(
      String title,
      Column xColumn,
      Column yColumn,
      NumericColumn sizeColumn,
      double[] color,
      SizeMode sizeMode,
      Double opacity) {

    Layout layout = Layout.builder(title, xColumn.name(), yColumn.name()).build();

    Marker marker = null;
    MarkerBuilder builder = Marker.builder();
    if (sizeColumn != null) {
      builder.size(sizeColumn);
    }
    if (opacity != null) {
      builder.opacity(opacity);
    }
    if (color != null) {
      builder.color(color);
    }
    if (sizeMode != null) {
      builder.sizeMode(sizeMode);
    }
    marker = builder.build();

    ScatterTrace trace = ScatterTrace.builder(xColumn, yColumn).marker(marker).build();
    return new Figure(layout, trace);
  }

  /**
   * create a bubble plot using column names
   *
   * @param title plot title
   * @param table source {@link Table} to fetch plot datap points
   * @param xCol non-nullable, column name for x-axis
   * @param yCol non-nullable, column name for y-axis
   * @param sizeCol nullable, column name for bubble size
   * @return bubble plot created from given parameters
   */
  public static Figure create(String title, Table table, String xCol, String yCol, String sizeCol) {
    NumericColumn xColumn = table.numberColumn(xCol);
    NumericColumn yColumn = table.numberColumn(yCol);
    NumericColumn sizeColumn = sizeCol == null ? null : table.numberColumn(sizeCol);
    return create(title, xColumn, yColumn, sizeColumn, null, null, null);
  }

  public static Figure create(
      String title, String xTitle, double[] xCol, String yTitle, double[] yCol) {
    Layout layout = Layout.builder(title, xTitle, yTitle).build();
    ScatterTrace trace = ScatterTrace.builder(xCol, yCol).build();
    return new Figure(layout, trace);
  }
}
