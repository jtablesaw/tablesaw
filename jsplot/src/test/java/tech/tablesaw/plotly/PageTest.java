package tech.tablesaw.plotly;

import org.junit.Assert;
import org.junit.Test;

import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Page;
import tech.tablesaw.plotly.traces.BarTrace;

public class PageTest {

    private final Object[] x = {"sheep", "cows", "fish", "tree sloths"};
    private final double[] y = {1, 4, 9, 16};

    @Test
    public void testPlotlyJsLocation() {
        BarTrace trace = BarTrace.builder(x, y).build();
        String location = this.getClass().getResource(this.getClass().getSimpleName() + ".class").toString();
        Page page = Page.pageBuilder(new Figure(trace), "plot")
                .plotlyJsLocation(location)
                .build();
        String html = page.asJavascript();
        Assert.assertTrue(html.indexOf("\"" + location + "\"") > 0);
    }
}
