package tech.tablesaw.plotly;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Page;
import tech.tablesaw.plotly.traces.BarTrace;

public class PageTest {

    private final Object[] x = {"sheep", "cows", "fish", "tree sloths"};
    private final double[] y = {1, 4, 9, 16};

    @Test
    public void testDefaultPlotlyJsLocation() {
        BarTrace trace = BarTrace.builder(x, y).build();
        Page page = Page.pageBuilder(new Figure(trace), "plot")
                .build();
        String html = page.asJavascript();
        assertTrue(html.indexOf("\"" + "https://cdn.plot.ly/plotly-latest.min.js" + "\"") > 0);
    }
    
    @Test
    public void testCustomPlotlyJsLocation() {
        BarTrace trace = BarTrace.builder(x, y).build();
        String location = this.getClass().getResource(this.getClass().getSimpleName() + ".class").toString();
        Page page = Page.pageBuilder(new Figure(trace), "plot")
                .plotlyJsLocation(location)
                .build();
        String html = page.asJavascript();
        assertTrue(html.indexOf("\"" + location + "\"") > 0);
    }
}
