package tech.tablesaw.plotly;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.traces.ViolinTrace;

@Disabled
class ViolinTest {

    private final Object[] x = {
            "sheep",
            "cows",
            "fish",
            "tree sloths",
            "sheep",
            "cows",
            "fish",
            "tree sloths",
            "sheep",
            "cows",
            "fish",
            "tree sloths"
    };
    private final double[] y = {1, 4, 9, 16, 3, 6, 8, 8, 2, 4, 7, 11};

    @Test
    void testAsJavascript() {
        ViolinTrace trace = ViolinTrace.builder(x, y).build();
        assertNotNull(trace.asJavascript(1));
    }

    @Test
    void show() {
        ViolinTrace trace = ViolinTrace.builder(x, y).build();
        Figure figure = new Figure(trace);
        assertNotNull(figure);
        Plot.show(figure, "target");
    }

    /** Test ensures that the name() method returns a ViolinTraceBuilder as expected. */
    @Test
    void name() {
        ViolinTrace trace = ViolinTrace.builder(x, y).name("my name").build();
        assertNotNull(trace);
    }
}
