package tech.tablesaw.plotly.api;

import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.table.TableSliceGroup;

import java.util.List;

public class TimeSeriesPlot {

    private static final int HEIGHT = 600;
    private static final int WIDTH = 800;

    public static Figure create(String title, Table table, String dateColX, String yCol, String groupCol) {

        TableSliceGroup tables = table.splitOn(table.categoricalColumn(groupCol));

        Layout layout = standardLayout(title, dateColX, yCol);

        ScatterTrace[] traces  = new ScatterTrace[tables.size()];
        for (int i = 0; i < tables.size(); i++) {
            List<Table> tableList = tables.asTableList();
            Table t = tableList.get(i).sortOn(dateColX);
            traces[i] = ScatterTrace.builder(
                    t.dateColumn(dateColX),
                    t.numberColumn(yCol))
                    .showLegend(true)
                    .name(tableList.get(i).name())
                    .mode(ScatterTrace.Mode.LINE)
                    .build();
        }
        return new Figure(layout, traces);
    }

    public static Figure create(String title, Table table, String dateColXName, String yColName) {

        Layout layout = standardLayout(title, dateColXName, yColName);

        ScatterTrace trace = ScatterTrace.builder(
                table.column(dateColXName),
                table.numberColumn(yColName))
                .mode(ScatterTrace.Mode.LINE)
                .build();
        return new Figure(layout, trace);
    }

    public static Figure create(String title, String xTitle, DateColumn xCol, String yTitle, NumberColumn<?> yCol) {

        Layout layout = standardLayout(title, xTitle, yTitle);

        ScatterTrace trace = ScatterTrace.builder(xCol, yCol)
                .mode(ScatterTrace.Mode.LINE)
                .build();
        return new Figure(layout, trace);
    }

    private static Layout standardLayout(String title, String xTitle, String yTitle) {
        return Layout.builder()
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
    }
}
