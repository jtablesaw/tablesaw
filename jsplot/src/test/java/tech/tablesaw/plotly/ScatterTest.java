package tech.tablesaw.plotly;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Font;
import tech.tablesaw.plotly.components.HoverLabel;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.components.Symbol;
import tech.tablesaw.plotly.components.TickSettings;
import tech.tablesaw.plotly.traces.ScatterTrace;

import java.io.File;
import java.nio.file.Paths;

@Disabled
public class ScatterTest {

    private final String[] text = {"acc", "dnax", "lc", "hc", "seq"};
    private final double[] vals = {1, 6, 14, 25, 39};

    private final double[] x = {1, 2, 3, 4, 5, 6};
    private final double[] y = {0, 1, 6, 14, 25, 39};

    private final String[] labels = {"a", "b", "c", "d", "e", "f"};

    @Test
    public void testAsJavascript() {
        ScatterTrace trace = ScatterTrace.builder(x, y)
                .text(labels)
                .build();

        System.out.println(trace.asJavascript(1));
    }

    @Test
    public void showScatter() {

        ScatterTrace trace = ScatterTrace.builder(x, y)
                .marker(Marker.builder()
                        .size(12.0)
                        .symbol(Symbol.DIAMOND_TALL)
                        .color("#c68486")
                        .build())
                .mode(ScatterTrace.Mode.MARKERS)
                .text(labels)
                .build();

        Figure figure = new Figure(trace);
        File outputFile = Paths.get("testoutput/output.html").toFile();
        Plot.show(figure, "target", outputFile);
    }

    @Test
    public void showLine() {
        Layout layout = Layout.builder()
                .title("test")
                .titleFont(
                        Font.builder()
                                .size(32)
                                .color("green")
                                .build())
                .showLegend(true)
                .height(700)
                .width(1200)
                .build();

        ScatterTrace trace = ScatterTrace.builder(x, y)
                .mode(ScatterTrace.Mode.LINE)
                .hoverLabel(
                        HoverLabel.builder()
                                .bgColor("red")
                                .font(Font.builder()
                                        .size(24)
                                        .build())
                                .build())
                .showLegend(true)
                .build();

        Figure figure = new Figure(layout, trace);
        File outputFile = Paths.get("testoutput/output.html").toFile();

        Plot.show(figure, "target", outputFile);
    }

    @Test
    public void showLineWithArrayTicks() {

        final double[] x1 = {13, 14, 15, 16, 17, 18};
        final double[] y1 = {0, 1, 6, 14, 25, 39};

        final double[] x2 = {7, 9, 11, 13};
        final double[] y2 = {0, 1, 6, 14 };

        Axis.Spikes spikes = Axis.Spikes.builder()
                .color("blue")
                .dash("solid")
                .thickness(1)
                .build();

        TickSettings tickSettings = TickSettings.builder()
                .tickMode(TickSettings.TickMode.ARRAY)
                .showTickLabels(true)
                .arrayTicks(vals, text)
                .build();

        Axis yAxis =
                Axis.builder()
                        .title("stages")
                        .tickSettings(tickSettings)
                        .autoRange(Axis.AutoRange.REVERSED)
                        .gridWidth(1)
                        .gridColor("grey")
                        .spikes(spikes)
                        .build();

        Layout layout = Layout.builder()
                .title("train time")
                .yAxis(yAxis)
                .xAxis(Axis.builder().spikes(spikes).build())
                .height(700)
                .width(1200)
                .hoverMode(Layout.HoverMode.CLOSEST)
                .build();

        ScatterTrace trace1 = ScatterTrace.builder(x, y)
                .mode(ScatterTrace.Mode.LINE)
                .build();

        ScatterTrace trace3 = ScatterTrace.builder(x1, y1)
                .mode(ScatterTrace.Mode.LINE)
                .build();

        ScatterTrace trace2 = ScatterTrace.builder(x2, y2)
                .mode(ScatterTrace.Mode.LINE)
                .build();

        Figure figure = new Figure(layout, trace1, trace2, trace3);
        File outputFile = Paths.get("testoutput/output.html").toFile();
        Plot.show(figure, "target", outputFile);
    }

    @Test
    public void showLineAndMarkers() {

        ScatterTrace trace = ScatterTrace.builder(x, y)
                .mode(ScatterTrace.Mode.LINE_AND_MARKERS)
                .build();

        Figure figure = new Figure(trace);
        File outputFile = Paths.get("testoutput/output.html").toFile();

        Plot.show(figure, "target", outputFile);
    }

    @Test
    public void showText() {

        ScatterTrace trace = ScatterTrace.builder(x, y)
                .mode(ScatterTrace.Mode.TEXT)
                .text(labels)
                .build();

        Figure figure = new Figure(trace);
        File outputFile = Paths.get("testoutput/output.html").toFile();

        Plot.show(figure, "target", outputFile);
    }
}