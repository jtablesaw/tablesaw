package tech.tablesaw.plotly.api;

import java.util.List;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.components.threeD.Scene;
import tech.tablesaw.plotly.traces.Scatter3DTrace;
import tech.tablesaw.table.TableSliceGroup;

public class Scatter3DPlot {

  private static final int HEIGHT = 800;
  private static final int WIDTH = 1000;

  public static Figure create(
      String title, Table table, String xCol, String yCol, String zCol, String groupCol) {

    TableSliceGroup tables = table.splitOn(table.categoricalColumn(groupCol));

    Layout layout = standardLayout(title, xCol, yCol, zCol, true);

    Scatter3DTrace[] traces = new Scatter3DTrace[tables.size()];
    for (int i = 0; i < tables.size(); i++) {
      List<Table> tableList = tables.asTableList();
      traces[i] =
          Scatter3DTrace.builder(
                  tableList.get(i).numberColumn(xCol),
                  tableList.get(i).numberColumn(yCol),
                  tableList.get(i).numberColumn(zCol))
              .showLegend(true)
              .name(tableList.get(i).name())
              .build();
    }
    return new Figure(layout, traces);
  }

  public static Figure create(String title, Table table, String xCol, String yCol, String zCol) {

    Layout layout = standardLayout(title, xCol, yCol, zCol, false);

    Scatter3DTrace trace =
        Scatter3DTrace.builder(
                table.numberColumn(xCol), table.numberColumn(yCol), table.numberColumn(zCol))
            .build();
    return new Figure(layout, trace);
  }

  public static Figure create(
      String title,
      Table table,
      String xCol,
      String yCol,
      String zCol,
      String sizeColumn,
      String groupCol) {

    TableSliceGroup tables = table.splitOn(table.categoricalColumn(groupCol));

    Layout layout = standardLayout(title, xCol, yCol, zCol, false);

    Scatter3DTrace[] traces = new Scatter3DTrace[tables.size()];
    for (int i = 0; i < tables.size(); i++) {

      List<Table> tableList = tables.asTableList();
      Marker marker =
          Marker.builder()
              .size(tableList.get(i).numberColumn(sizeColumn))
              // .opacity(.75)
              .build();

      traces[i] =
          Scatter3DTrace.builder(
                  tableList.get(i).numberColumn(xCol),
                  tableList.get(i).numberColumn(yCol),
                  tableList.get(i).numberColumn(zCol))
              .marker(marker)
              .showLegend(true)
              .name(tableList.get(i).name())
              .build();
    }
    return new Figure(layout, traces);
  }

  private static Layout standardLayout(
      String title, String xCol, String yCol, String zCol, boolean showLegend) {
    return Layout.builder()
        .title(title)
        .height(HEIGHT)
        .width(WIDTH)
        .showLegend(showLegend)
        .scene(
            Scene.sceneBuilder()
                .xAxis(Axis.builder().title(xCol).build())
                .yAxis(Axis.builder().title(yCol).build())
                .zAxis(Axis.builder().title(zCol).build())
                .build())
        .build();
  }
}
