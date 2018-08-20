package tech.tablesaw.plotly.api;

import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.HistogramTrace;

public class Histogram {

    private static final int HEIGHT = 600;
    private static final int WIDTH = 800;

    public static Figure create(String title, NumberColumn<?> data) {
        return create(title, data.asDoubleArray());
    }

    public static Figure create(String title, Table table, String numericColumnName) {
        return create(title, table.numberColumn(numericColumnName));
    }

    public static Figure create(String title, double[] data) {
        HistogramTrace trace = HistogramTrace.builder(data).build();
        return new Figure(standardLayout(title), trace);
    }

    private static Layout standardLayout(String title) {
        return Layout.builder()
                .title(title)
                .height(HEIGHT)
                .width(WIDTH)
                .build();
    }
}
