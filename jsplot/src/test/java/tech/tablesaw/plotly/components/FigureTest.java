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
        "    <script>"
            + System.lineSeparator()
            + "        var target_target = document.getElementById('target');"
            + System.lineSeparator()
            + "        "
            + System.lineSeparator()
            + "var trace0 ="
            + System.lineSeparator()
            + "{"
            + System.lineSeparator()
            + "x: [\"1.0\",\"2.0\",\"3.0\",\"4.0\",\"5.0\"],"
            + System.lineSeparator()
            + "y: [\"1.0\",\"4.0\",\"9.0\",\"16.0\",\"25.0\"],"
            + System.lineSeparator()
            + "mode: 'markers',"
            + System.lineSeparator()
            + "xaxis: 'x',"
            + System.lineSeparator()
            + "yaxis: 'y',"
            + System.lineSeparator()
            + "type: 'scatter',"
            + System.lineSeparator()
            + "name: '',"
            + System.lineSeparator()
            + "};"
            + System.lineSeparator()
            + ""
            + System.lineSeparator()
            + "        var data = [ trace0];"
            + System.lineSeparator()
            + "Plotly.newPlot(target_target, data);            </script>"
            + System.lineSeparator(),
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
        "    <script>"
            + System.lineSeparator()
            + "        var target_target = document.getElementById('target');"
            + System.lineSeparator()
            + "        var layout = {"
            + System.lineSeparator()
            + "    title: 'A test title',"
            + System.lineSeparator()
            + "    height: 450,"
            + System.lineSeparator()
            + "    width: 700,"
            + System.lineSeparator()
            + "    showlegend: true,"
            + System.lineSeparator()
            + "    margin: {"
            + System.lineSeparator()
            + "  \"autoexpand\" : true,"
            + System.lineSeparator()
            + "  \"b\" : 80,"
            + System.lineSeparator()
            + "  \"l\" : 200,"
            + System.lineSeparator()
            + "  \"pad\" : 0,"
            + System.lineSeparator()
            + "  \"r\" : 80,"
            + System.lineSeparator()
            + "  \"t\" : 200"
            + System.lineSeparator()
            + "},"
            + System.lineSeparator()
            + "    xaxis: {"
            + System.lineSeparator()
            + "    title: 'x Axis 1',"
            + System.lineSeparator()
            + "        titlefont: {"
            + System.lineSeparator()
            + "  \"color\" : \"red\","
            + System.lineSeparator()
            + "  \"family\" : \"arial\","
            + System.lineSeparator()
            + "  \"size\" : 8"
            + System.lineSeparator()
            + "},"
            + System.lineSeparator()
            + "    },"
            + System.lineSeparator()
            + ""
            + System.lineSeparator()
            + ""
            + System.lineSeparator()
            + "};"
            + System.lineSeparator()
            + ""
            + System.lineSeparator()
            + "var trace0 ="
            + System.lineSeparator()
            + "{"
            + System.lineSeparator()
            + "x: [\"1.0\",\"2.0\",\"3.0\",\"4.0\",\"5.0\"],"
            + System.lineSeparator()
            + "y: [\"1.0\",\"4.0\",\"9.0\",\"16.0\",\"25.0\"],"
            + System.lineSeparator()
            + "mode: 'markers',"
            + System.lineSeparator()
            + "xaxis: 'x',"
            + System.lineSeparator()
            + "yaxis: 'y',"
            + System.lineSeparator()
            + "type: 'scatter',"
            + System.lineSeparator()
            + "name: '',"
            + System.lineSeparator()
            + "};"
            + System.lineSeparator()
            + ""
            + System.lineSeparator()
            + "        var data = [ trace0];"
            + System.lineSeparator()
            + "Plotly.newPlot(target_target, data, layout);            </script>"
            + System.lineSeparator(),
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
