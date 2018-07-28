package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;

public class OHLCPlot {

    private static final int HEIGHT = 600;
    private static final int WIDTH = 800;

    public static Figure create(String title, Table table, String xCol, String openCol, String highCol, String lowCol, String closeCol) {

        Layout layout = Layout.builder()
                .title(title)
                .height(HEIGHT)
                .width(WIDTH)
                .xAxis(Axis.builder()
                        .title(xCol)
                        .build())
                .build();

        ScatterTrace trace = ScatterTrace.builder(
                table.dateColumn(xCol),
                table.numberColumn(openCol),
                table.numberColumn(highCol),
                table.numberColumn(lowCol),
                table.numberColumn(closeCol))
                .type("ohlc")
                .build();
        return new Figure(layout, trace);
    }
}
