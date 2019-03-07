package tech.tablesaw.plotly;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.Scatter3DTrace;

@Disabled
public class Scatter3DTest {

    private final double[] x = {1, 2, 3, 4, 5, 6};
    private final double[] y = {0, 1, 6, 14, 25, 39};
    private final double[] z = {-23, 11, -2, -7, 0.324, -11};

    private final String[] labels = {"apple", "bike", "car", "dog", "elephant", "fox"};

    @Test
    public void testAsJavascript() {
        Scatter3DTrace trace = Scatter3DTrace.builder(x, y, z)
                .text(labels)
                .build();

        System.out.println(trace.asJavascript(1));
    }

    @Test
    public void showScatter() {

        Scatter3DTrace trace = Scatter3DTrace.builder(x, y, z)
                .mode(Scatter3DTrace.Mode.MARKERS)
                .text(labels)
                .build();

        Layout layout = Layout.builder()
                .xAxis(Axis.builder().title("x title").build())
                .build();

        Plot.show(new Figure(layout, trace));
    }

    @Test
    public void showLineAndMarkers() {

        Scatter3DTrace trace = Scatter3DTrace.builder(x, y, z)
                .mode(Scatter3DTrace.Mode.LINE_AND_MARKERS)
                .build();
        Layout layout = Layout.builder()
                .xAxis(Axis.builder().title("x title").build())
                .build();

        Plot.show(new Figure(layout, trace));
    }

    @Test
    public void showText() {

        Scatter3DTrace trace = Scatter3DTrace.builder(x, y, z)
                .mode(Scatter3DTrace.Mode.TEXT)
                .text(labels)
                .build();

        Plot.show(new Figure(trace));
    }
}