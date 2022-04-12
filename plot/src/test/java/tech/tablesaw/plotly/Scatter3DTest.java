package tech.tablesaw.plotly;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.api.Scatter3DPlot;
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
    Scatter3DTrace trace = Scatter3DTrace.builder(x, y, z).text(labels).build();
    assertNotNull(trace.asJavascript(1));
  }

  @Test
  public void showScatter() {

    Scatter3DTrace trace =
        Scatter3DTrace.builder(x, y, z).mode(Scatter3DTrace.Mode.MARKERS).text(labels).build();

    Layout layout = Layout.builder().xAxis(Axis.builder().title("x title").build()).build();
    assertEquals("x title", layout.getTitle());
    Plot.show(new Figure(layout, trace));
  }

  @Test
  public void showLineAndMarkers() {

    Scatter3DTrace trace =
        Scatter3DTrace.builder(x, y, z).mode(Scatter3DTrace.Mode.LINE_AND_MARKERS).build();
    Layout layout = Layout.builder().xAxis(Axis.builder().title("x title").build()).build();

    Plot.show(new Figure(layout, trace));
  }

  @Test
  public void showText() {

    Scatter3DTrace trace =
        Scatter3DTrace.builder(x, y, z).mode(Scatter3DTrace.Mode.TEXT).text(labels).build();

    Plot.show(new Figure(trace));
  }

  @Test
  void createScatter3D() {
    DoubleColumn xData = DoubleColumn.create("x", new double[] {2, 2, 1});
    DoubleColumn yData = DoubleColumn.create("y", new double[] {1, 2, 3});
    DoubleColumn zData = DoubleColumn.create("z", new double[] {1, 4, 1});

    Table data = Table.create().addColumns(xData, yData, zData);
    assertNotNull(Scatter3DPlot.create("3D plot", data, "x", "y", "z"));
  }
}
