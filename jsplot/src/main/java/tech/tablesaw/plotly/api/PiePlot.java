package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.PieTrace;

public class PiePlot {

    private static final int HEIGHT = 600;
    private static final int WIDTH = 800;

    public static void show(String title, Table table, String groupColName, String numberColName) {

        Layout layout = Layout.builder()
                .title(title)
                .height(HEIGHT)
                .width(WIDTH)
                .build();

        PieTrace trace = PieTrace.builder(
                table.categoricalColumn(groupColName),
                table.numberColumn(numberColName))
                .build();
        Plot.show(new Figure(layout, trace));
    }
}