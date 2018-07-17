package tech.tablesaw.plotly.api;

import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.HistogramTrace;

public class Histogram {

    private static final int HEIGHT = 600;
    private static final int WIDTH = 800;

    public static void show(String title, double[] data) {
        Layout layout = Layout.builder()
                .title(title)
                .height(HEIGHT)
                .width(WIDTH)
                .build();

        HistogramTrace trace = HistogramTrace.builder(data).build();

        Plot.show(new Figure(layout, trace));
    }

    public static void show(String title, NumberColumn data) {
        Layout layout = Layout.builder()
                .title(title)
                .height(HEIGHT)
                .width(WIDTH)
                .build();

        HistogramTrace trace = HistogramTrace.builder(data).build();

        Plot.show(new Figure(layout, trace));
    }
}
