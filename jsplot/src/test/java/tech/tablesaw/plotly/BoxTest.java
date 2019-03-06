package tech.tablesaw.plotly;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.traces.BoxTrace;

@Disabled
public class BoxTest {

    private final Object[] x = {"sheep", "cows", "fish", "tree sloths", "sheep", "cows", "fish", "tree sloths", "sheep", "cows", "fish", "tree sloths"};
    private final double[] y = {1, 4, 9, 16, 3, 6, 8, 8, 2, 4, 7, 11};

    @Test
    public void testAsJavascript() {
        BoxTrace trace =
                BoxTrace.builder(x, y)
                        .build();

        System.out.println(trace.asJavascript(1));
    }

    @Test
    public void show() {

        BoxTrace trace = BoxTrace.builder(x, y).build();

        Figure figure = new Figure(trace);
        Plot.show(figure, "target");
    }
}
