package tech.tablesaw.plotly;

import org.junit.Ignore;
import org.junit.Test;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.traces.HistogramTrace;

@Ignore
public class HistogramTraceTest {

    private final double[] y1 = {1, 4, 9, 16, 11, 4, -1, 20, 4, 7, 9, 12, 8, 6};
    private final double[] y2 = {3, 11, 19, 14, 11, 14, 5, 24, -4, 10, 15, 6, 5, 18};

    @Test
    public void testAsJavascript() {
        HistogramTrace trace1 = HistogramTrace.builder(y1).build();
        System.out.println(trace1.asJavascript(1));
    }

    @Test
    public void show() {

        HistogramTrace trace1 = HistogramTrace.builder(y1).build();
        HistogramTrace trace2 = HistogramTrace.builder(y2).build();
        Plot.show(new Figure(trace1, trace2));
    }
}