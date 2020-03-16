package tech.tablesaw.plotly.api;

import java.util.List;
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
  
  public static Figure create(
	      String title, Table table, 
	      String xCol, Column xColumn,
	      String yCol, Column yColumn,
	      String sizeColumn, 
	      double[] color, SizeMode sizeMode, Double opacity) {
		Layout layout = Layout.builder(title, xCol, yCol).build();

		Marker marker = null;
		MarkerBuilder builder = Marker.builder();
		if (sizeColumn != null) {
			builder.size(table.numberColumn(sizeColumn));
		}
		if (opacity != null) {
//		   builder.opacity(.75);
			builder.opacity(opacity);
		}
		if (color != null) {
			builder.color(color);
		}
		if (sizeMode != null) {
			builder.sizeMode(sizeMode);
		}
		marker = builder.build();

		xColumn = (xColumn == null) ? table.numberColumn(xCol) : xColumn;
		yColumn = (yColumn == null) ? table.numberColumn(yCol) : yColumn;

		ScatterTrace trace = ScatterTrace.builder(xColumn, yColumn).marker(marker).build();
		return new Figure(layout, trace);
  }

  public static Figure create(
      String title, Table table, String xCol, String yCol, String sizeColumn) {
        return create(title, table, xCol, null, yCol, null, sizeColumn, null, null, null);
  }

  public static Figure create(
      String title, String xTitle, double[] xCol, String yTitle, double[] yCol) {
    Layout layout = Layout.builder(title, xTitle, yTitle).build();
    ScatterTrace trace = ScatterTrace.builder(xCol, yCol).build();
    return new Figure(layout, trace);
  }
}
