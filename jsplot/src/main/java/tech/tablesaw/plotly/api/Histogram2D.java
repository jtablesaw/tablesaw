package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.Histogram2DTrace;

public class Histogram2D {

    private static final int HEIGHT = 600;
    private static final int WIDTH = 800;

    public static void show(String title, Table table, String xCol, String yCol) {

        Histogram2DTrace trace = Histogram2DTrace.builder(table.numberColumn(xCol), table.numberColumn(yCol)).build();

        Plot.show(new Figure(standardLayout(title), trace));
    }

    private static Layout standardLayout(String title) {
        return Layout.builder()
                .title(title)
                .height(HEIGHT)
                .width(WIDTH)
                .build();
    }
}
