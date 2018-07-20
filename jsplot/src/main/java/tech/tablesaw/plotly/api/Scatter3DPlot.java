package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.Scatter3DTrace;
import tech.tablesaw.table.TableSliceGroup;

import java.util.List;

public class Scatter3DPlot {

    private static final int HEIGHT = 800;
    private static final int WIDTH = 1000;

    public static void show(String title, Table table, String xCol, String yCol, String zCol, String groupCol) {

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

        Scatter3DTrace[] traces  = new Scatter3DTrace[tables.size()];
        for (int i = 0; i < tables.size(); i++) {
            List<Table> tableList = tables.asTableList();
            traces[i] = Scatter3DTrace.builder(
                    tableList.get(i).numberColumn(xCol),
                    tableList.get(i).numberColumn(yCol),
                    tableList.get(i).numberColumn(zCol))
                    .showLegend(true)
                    .name(tableList.get(i).name())
                    .build();
        }
        Figure figure = new Figure(layout, traces);
        Plot.show(figure);
    }

    public static void show(String title, Table table, String xCol, String yCol, String zCol) {

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

        Scatter3DTrace trace = Scatter3DTrace.builder(
                table.numberColumn(xCol),
                table.numberColumn(yCol),
                table.numberColumn(zCol))
                .build();
        Figure figure = new Figure(layout, trace);
        Plot.show(figure);
    }
}
