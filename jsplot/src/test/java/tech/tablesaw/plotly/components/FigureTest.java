package tech.tablesaw.plotly.components;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;

class FigureTest {

  private String divName = "target";

  private double[] x = {1, 2, 3, 4, 5};
  private double[] y = {1, 4, 9, 16, 25};

  @Test
  void asJavascript() {

    Trace trace = ScatterTrace.builder(x, y).build();
    Figure figure = new Figure(trace);

    assertEquals(
        "    <script>\n"
            + "        var target_target = document.getElementById('target');\n"
            + "        \n"
            + "var trace0 =\n"
            + "{\n"
            + "x: [\"1.0\",\"2.0\",\"3.0\",\"4.0\",\"5.0\"],\n"
            + "y: [\"1.0\",\"4.0\",\"9.0\",\"16.0\",\"25.0\"],\n"
            + "mode: 'markers',\n"
            + "xaxis: 'x',\n"
            + "yaxis: 'y',\n"
            + "type: 'scatter',\n"
            + "name: '',\n"
            + "};\n"
            + "\n"
            + "        var data = [ trace0];\n"
            + "Plotly.newPlot(target_target, data);            </script>\n",
        figure.asJavascript(divName));
  }

  @Test
  void asJavascript2() {

    Trace trace = ScatterTrace.builder(x, y).build();

    Layout layout =
        Layout.builder()
            .title("A test title")
            .xAxis(
                Axis.builder()
                    .title("x Axis 1")
                    .visible(true)
                    .type(Axis.Type.DEFAULT)
                    .titleFont(
                        Font.builder().family(Font.Family.ARIAL).size(8).color("red").build())
                    .build())
            .margin(Margin.builder().top(200).left(200).build())
            .showLegend(true)
            .build();

    Figure figure = new Figure(layout, trace);

    assertEquals(
        "    <script>\n"
            + "        var target_target = document.getElementById('target');\n"
            + "        var layout = {\n"
            + "    title: 'A test title',\n"
            + "    height: 450,\n"
            + "    width: 700,\n"
            + "    showlegend: true,\n"
            + "    margin: {\n"
            + "    t: 200,\n"
            + "    b: 80,\n"
            + "    l: 200,\n"
            + "    r: 80,\n"
            + "    pad: 0,\n"
            + "    autoexpand: true},\n"
            + "    xaxis: {\n"
            + "    title: 'x Axis 1',\n"
            + "        titlefont: {\n"
            + "    family: 'arial',\n"
            + "    size: 8,\n"
            + "    color: 'red'\n"
            + "}\n"
            + ",\n"
            + "    },\n"
            + "\n"
            + "\n"
            + "};\n"
            + "\n"
            + "var trace0 =\n"
            + "{\n"
            + "x: [\"1.0\",\"2.0\",\"3.0\",\"4.0\",\"5.0\"],\n"
            + "y: [\"1.0\",\"4.0\",\"9.0\",\"16.0\",\"25.0\"],\n"
            + "mode: 'markers',\n"
            + "xaxis: 'x',\n"
            + "yaxis: 'y',\n"
            + "type: 'scatter',\n"
            + "name: '',\n"
            + "};\n"
            + "\n"
            + "        var data = [ trace0];\n"
            + "Plotly.newPlot(target_target, data, layout);            </script>\n",
        figure.asJavascript(divName));
  }

  @Test
  void builder() {
    String title = "A test title";
    Layout layout = Layout.builder().title(title).showLegend(true).build();

    Trace trace = ScatterTrace.builder(x, y).build();

    Figure figure = Figure.builder().layout(layout).addTraces(trace).build();

    assertEquals(layout, figure.getLayout());

    figure.asJavascript(divName); // force the context to get created
    assertTrue(String.valueOf(figure.getContext().get("figure")).contains(title));
  }
}
