package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.table.TableSliceGroup;

import java.util.List;

public class AreaPlot {

    public static Figure create(String title, Table table, String xCol, String yCol, String groupCol) {
        TableSliceGroup tables = table.splitOn(table.categoricalColumn(groupCol));

        Layout layout = Layout.builder(title, xCol, yCol).showLegend(true).build();

        ScatterTrace[] traces  = new ScatterTrace[tables.size()];
        for (int i = 0; i < tables.size(); i++) {
            List<Table> tableList = tables.asTableList();
            traces[i] = ScatterTrace.builder(
                    tableList.get(i).numberColumn(xCol),
                    tableList.get(i).numberColumn(yCol))
                    .showLegend(true)
                    .name(tableList.get(i).name())
                    .mode(ScatterTrace.Mode.LINE)
                    .fill(ScatterTrace.Fill.TO_NEXT_Y)
                    .build();
        }
        return new Figure(layout, traces);
    }

    public static Figure create(String title, Table table, String xCol, String yCol) {
        Layout layout = Layout.builder(title, xCol, yCol).showLegend(true).build();
        ScatterTrace trace = ScatterTrace.builder(
                table.numberColumn(xCol),
                table.numberColumn(yCol))
                .mode(ScatterTrace.Mode.LINE)
                .fill(ScatterTrace.Fill.TO_NEXT_Y)
                .build();
        return new Figure(layout, trace);
    }
}
