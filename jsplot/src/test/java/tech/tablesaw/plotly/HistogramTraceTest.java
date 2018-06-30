package tech.tablesaw.plotly;

import org.junit.Ignore;
import org.junit.Test;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.traces.HistogramTrace;

@Ignore
public class HistogramTraceTest {

    private final double[] y = {1, 4, 9, 16, 11, 4, -1, 20, 4, 7, 9, 12, 8, 6};

    @Test
    public void testAsJavascript() {
        HistogramTrace trace = HistogramTrace.builder(y).build();

        System.out.println(trace.asJavascript(1));
    }

    @Test
    public void show() {

        HistogramTrace trace = HistogramTrace.builder(y).build();
        Plot.show(new Figure(trace));
    }
}