package tech.tablesaw.plotly.api;

import java.util.List;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.wrappers.Scatter;
import tech.tablesaw.table.TableSliceGroup;

public class AreaPlot {

  public static Figure create(
      String title, Table table, String xCol, String yCol, String groupCol) {
    TableSliceGroup tables = table.splitOn(table.categoricalColumn(groupCol));

    Layout layout = Layout.builder(title, xCol, yCol).showLegend(true).build();

    Scatter[] traces = new Scatter[tables.size()];
    for (int i = 0; i < tables.size(); i++) {
      List<Table> tableList = tables.asTableList();
      traces[i] =
          Scatter.builder(
                  tableList.get(i).numberColumn(xCol), tableList.get(i).numberColumn(yCol))
              .showLegend(true)
              .name(tableList.get(i).name())
              .mode(Scatter.Mode.LINE)
              .fill(Scatter.Fill.TO_NEXT_Y)
              .build();
    }
    return new Figure(layout, traces);
  }

  public static Figure create(String title, Table table, String xCol, String yCol) {
    Layout layout = Layout.builder(title, xCol, yCol).build();
    Scatter trace =
        Scatter.builder(table.numberColumn(xCol), table.numberColumn(yCol))
            .mode(Scatter.Mode.LINE)
            .fill(Scatter.Fill.TO_NEXT_Y)
            .build();
    return new Figure(layout, trace);
  }
}
