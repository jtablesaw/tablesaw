package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.table.TableSliceGroup;

import java.util.List;

public class ScatterPlot {

    private static final double OPACITY = .75;

    public static Figure create(String title, Table table, String xCol, String yCol, String groupCol) {

        TableSliceGroup tables = table.splitOn(table.categoricalColumn(groupCol));

        Layout layout = Layout.builder(title, xCol, yCol).showLegend(true).build();

        ScatterTrace[] traces  = new ScatterTrace[tables.size()];
        Marker marker = Marker.builder().opacity(OPACITY).build();
        for (int i = 0; i < tables.size(); i++) {
            List<Table> tableList = tables.asTableList();
            traces[i] = ScatterTrace.builder(
                    tableList.get(i).numberColumn(xCol),
                    tableList.get(i).numberColumn(yCol))
                    .showLegend(true)
                    .marker(marker)
                    .name(tableList.get(i).name())
                    .build();
        }
        return new Figure(layout, traces);
    }

    public static Figure create(String title, Table table, String xCol, String yCol) {
        Layout layout = Layout.builder(title, xCol, yCol).build();
        ScatterTrace trace = ScatterTrace.builder(
                table.numberColumn(xCol),
                table.numberColumn(yCol))
                .build();
        return new Figure(layout, trace);
    }

    public static Figure create(String title, String xTitle, double[] xCol, String yTitle, double[] yCol) {
        Layout layout = Layout.builder(title, xTitle, yTitle).build();
        ScatterTrace trace = ScatterTrace.builder(xCol, yCol).build();
        return new Figure(layout, trace);
    }

}
