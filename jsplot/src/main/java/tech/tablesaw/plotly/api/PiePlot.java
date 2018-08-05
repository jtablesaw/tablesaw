package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.PieTrace;

public class PiePlot {

    private static final int HEIGHT = 600;
    private static final int WIDTH = 800;

    public static Figure create(String title, Table table, String groupColName, String numberColName) {

        Layout layout = Layout.builder()
                .title(title)
                .height(HEIGHT)
                .width(WIDTH)
                .build();

        PieTrace trace = PieTrace.builder(
                table.categoricalColumn(groupColName),
                table.numberColumn(numberColName))
                .showLegend(true)
                .build();
        return new Figure(layout, trace);
    }
}