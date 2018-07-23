package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.BoxTrace;

public class BoxPlot {

    private static final int HEIGHT = 600;
    private static final int WIDTH = 800;

    public static void show(String title, Table table, String groupingColumn, String numericColumn) {
        Plot.show(asFigure(title, table, groupingColumn, numericColumn));
    }

    private static Figure asFigure(String title, Table table, String groupingColumn, String numericColumn) {
        Layout layout = Layout.builder()
                .title(title)
                .height(HEIGHT)
                .width(WIDTH)
                .build();

        BoxTrace trace = BoxTrace.builder(
                table.categoricalColumn(groupingColumn),
                table.nCol(numericColumn))
                .build();
        return new Figure(layout, trace);
    }
}
