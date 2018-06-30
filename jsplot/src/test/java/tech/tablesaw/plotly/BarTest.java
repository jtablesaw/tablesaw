package tech.tablesaw.plotly;

import org.junit.Ignore;
import org.junit.Test;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.traces.BarTrace;

@Ignore
public class BarTest {

    private final Object[] x = {"sheep", "cows", "fish", "tree sloths"};
    private final double[] y = {1, 4, 9, 16};

    @Test
    public void testAsJavascript() {
        BarTrace trace = BarTrace.builder(x, y).build();
        System.out.println(trace.asJavascript(1));
    }

    @Test
    public void show() {

        BarTrace trace = BarTrace.builder(x, y).build();
        Figure figure = new Figure(trace);
        Plot.show(figure, "target");
    }
}
