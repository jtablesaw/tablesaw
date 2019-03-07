package tech.tablesaw.components;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Font;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Margin;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;

import java.io.File;
import java.nio.file.Paths;

@Disabled
public class FigureTest {

    private String divName = "target";
    private File outputFile = Paths.get("testoutput/output.html").toFile();

    private double[] x = {1, 2, 3, 4, 5};
    private double[] y = {1, 4, 9, 16, 25};

    @Test
    public void asJavascript() {

        Trace trace = ScatterTrace.builder(x, y).build();
        Figure figure = new Figure(trace);

        System.out.println(figure.asJavascript(divName));
    }

    @Test
    public void asJavascript2() {

        Trace trace = ScatterTrace.builder(x, y)
                .build();

        Layout layout = Layout.builder()
                .title("A test title")
                .xAxis(Axis.builder()
                        .title("x Axis 1")
                        .visible(true)
                        .type(Axis.Type.DEFAULT)
                        .titleFont(Font.builder()
                                .family(Font.Family.ARIAL)
                                .size(8)
                                .color("red")
                                .build())
                        .build()
                )
                .margin(Margin.builder()
                        .top(200)
                        .left(200)
                        .build())
                .showLegend(true)

                .build();

        Figure figure = new Figure(layout, trace);

        System.out.println(figure.asJavascript(divName));
    }

    @Test
    public void show2()  {
        Trace trace = ScatterTrace.builder(x, y)
                .name("series one")
                .build();

        Layout layout = Layout.builder()
                .title("A test title")
                .xAxis(Axis.builder()
                                .title("x Axis 1")
                                .visible(true)
                                .type(Axis.Type.DEFAULT)
                                .titleFont(Font.builder()
                                        .family(Font.Family.ARIAL)
                                        .size(8)
                                        .color("red")
                                        .build())
                                .build())
                .margin(Margin.builder()
                        .top(200)
                        .left(200)
                        .build())
                .showLegend(true)
                .build();

        Figure figure = new Figure(layout, trace);

        Plot.show(figure, divName, outputFile);
    }

    @Test
    public void show()  {
        Trace trace = ScatterTrace.builder(x, y).build();

        Figure figure = new Figure(trace);

        Plot.show(figure, divName, outputFile);
    }
}