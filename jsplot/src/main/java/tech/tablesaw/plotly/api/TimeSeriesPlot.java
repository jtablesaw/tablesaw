package tech.tablesaw.plotly.api;

import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.table.TableSliceGroup;

import java.util.List;

public class TimeSeriesPlot {

    public static Figure create(String title, Table table, String dateColX, String yCol, String groupCol) {

        TableSliceGroup tables = table.splitOn(table.categoricalColumn(groupCol));

        Layout layout = Layout.builder(title, dateColX, yCol).build();

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
        Layout layout = Layout.builder(title, dateColXName, yColName).build();
        ScatterTrace trace = ScatterTrace.builder(
                table.column(dateColXName),
                table.numberColumn(yColName))
                .mode(ScatterTrace.Mode.LINE)
                .build();
        return new Figure(layout, trace);
    }

    public static Figure create(String title, String xTitle, DateColumn xCol, String yTitle, NumberColumn<?> yCol) {
        Layout layout = Layout.builder(title, xTitle, yTitle).build();
        ScatterTrace trace = ScatterTrace.builder(xCol, yCol)
                .mode(ScatterTrace.Mode.LINE)
                .build();
        return new Figure(layout, trace);
    }

}
