package tech.tablesaw.plotly;

import org.junit.Ignore;
import org.junit.Test;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.traces.PieTrace;

import java.io.File;
import java.nio.file.Paths;

@Ignore
public class PieTest {

    private final Object[] x = {"sheep", "cows", "fish", "tree sloths"};
    private final double[] y = {1, 4, 9, 16};

    @Test
    public void testAsJavascript()  {
        PieTrace trace = PieTrace.builder(x, y)
                .build();
        System.out.println(trace.asJavascript(1));
    }

    @Test
    public void show() {
        PieTrace trace = PieTrace.builder(x, y).build();
        Figure figure = new Figure(trace);
        File outputFile = Paths.get("testoutput/output.html").toFile();
        Plot.show(figure, "target", outputFile);
    }
}