package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.table.TableSliceGroup;

import java.util.List;

public class Scatterplot {

    public static void show(String title, Table table, String xCol, String yCol, String groupCol) {

        TableSliceGroup tables = table.splitOn(table.categoricalColumn(groupCol));

        Layout layout = Layout.builder()
                .title(title)
                .height(600)
                .width(800)
                .xAxis(Axis.builder()
                        .title(xCol)
                        .build())
                .yAxis(Axis.builder()
                        .title(yCol)
                        .build())
                .build();

        ScatterTrace[] traces  = new ScatterTrace[2];
        for (int i = 0; i < tables.size(); i++) {
            List<Table> tableList = tables.asTableList();
            traces[i] = ScatterTrace.builder(
                    tableList.get(i).numberColumn(xCol),
                    tableList.get(i).numberColumn(yCol))
                    .build();
        }
        Figure figure = new Figure(layout, traces);
        Plot.show(figure);
    }

    public static void show(String title, Table table, String xCol, String yCol) {

        Layout layout = Layout.builder()
                .title(title)
                .height(600)
                .width(800)
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
        Figure figure = new Figure(layout, trace);
        Plot.show(figure);
    }

    public static void show(String title, String xTitle, double[] xCol, String yTitle, double[] yCol) {

        Layout layout = Layout.builder()
                .title(title)
                .height(600)
                .width(800)
                .xAxis(Axis.builder()
                        .title(xTitle)
                        .build())
                .yAxis(Axis.builder()
                        .title(yTitle)
                        .build())
                .build();

        ScatterTrace trace = ScatterTrace.builder(xCol, yCol).build();
        Figure figure = new Figure(layout, trace);
        Plot.show(figure);
    }
}
