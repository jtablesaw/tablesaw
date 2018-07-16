package tech.tablesaw.plotly.api;

import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.HistogramTrace;

public class Histogram {

    public static void show(String title, double[] data) {
        Layout layout = Layout.builder()
                .title(title)
                .height(600)
                .width(800)
                .build();

        HistogramTrace trace = HistogramTrace.builder(data)
                .build();

        Plot.show(new Figure(layout, trace));
    }
}
