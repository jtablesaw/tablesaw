package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.table.TableSliceGroup;

import java.util.List;

public class ScatterPlot {

    private static final int HEIGHT = 600;
    private static final int WIDTH = 800;
    private static final double OPACITY = .75;

    public static Figure create(String title, Table table, String xCol, String yCol, String groupCol) {

        TableSliceGroup tables = table.splitOn(table.categoricalColumn(groupCol));

        Layout layout = Layout.builder()
                .title(title)
                .height(HEIGHT)
                .width(WIDTH)
                .showLegend(true)
                .xAxis(Axis.builder()
                        .title(xCol)
                        .build())
                .yAxis(Axis.builder()
                        .title(yCol)
                        .build())
                .build();

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

        Layout layout = Layout.builder()
                .title(title)
                .height(HEIGHT)
                .width(WIDTH)
                .xAxis(Axis.builder()
                        .title(xCol)
                        .build())
                .yAxis(Axis.builder()
                        .title(yCol)
                        .build())
                .build();

        ScatterTrace trace = ScatterTrace.builder(
                table.numberColumn(xCol),
                table.numberColumn(yCol))
                .build();
        return new Figure(layout, trace);
    }

    public static Figure create(String title, String xTitle, double[] xCol, String yTitle, double[] yCol) {

        Layout layout = Layout.builder()
                .title(title)
                .height(HEIGHT)
                .width(WIDTH)
                .xAxis(Axis.builder()
                        .title(xTitle)
                        .build())
                .yAxis(Axis.builder()
                        .title(yTitle)
                        .build())
                .build();

        ScatterTrace trace = ScatterTrace.builder(xCol, yCol).build();
        return new Figure(layout, trace);
    }
}
