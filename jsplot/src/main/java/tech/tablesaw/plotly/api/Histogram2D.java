package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.Histogram2DTrace;

public class Histogram2D {

    private static final int HEIGHT = 600;
    private static final int WIDTH = 800;

    public static Figure create(String title, Table table, String xCol, String yCol) {

        Histogram2DTrace trace = Histogram2DTrace.builder(table.numberColumn(xCol), table.numberColumn(yCol))
                .build();

        return new Figure(standardLayout(title, xCol, yCol), trace);
    }

    private static Layout standardLayout(String title, String xCol, String yCol) {
        return Layout.builder()
                .title(title)
                .height(HEIGHT)
                .width(WIDTH)
                .xAxis(Axis.builder().title(xCol).build())
                .yAxis(Axis.builder().title(yCol).build())
                .build();
    }
}
