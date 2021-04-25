package tech.tablesaw.plotly;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.api.Heatmap;
import tech.tablesaw.plotly.api.Scatter3DPlot;
import tech.tablesaw.plotly.api.ScatterPlot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.traces.*;

public class MissingValuePlotTest {

  @Test
  public void boxTraceMissingValueTest() {
    IntColumn xWithMissingValue = IntColumn.create("x", new Integer[] {2, 2, 1, null});
    DoubleColumn yWithMissingValue = DoubleColumn.create("y", new Number[] {2, 2, 1, 8});
    IntColumn x = IntColumn.create("x", new Integer[] {2, 2, 1});
    DoubleColumn y = DoubleColumn.create("y", new Number[] {2, 2, 1});
    BoxTrace traceWithMissingValue = BoxTrace.builder(xWithMissingValue, yWithMissingValue).build();
    String jsWithMissingValue = traceWithMissingValue.asJavascript(1);

    BoxTrace trace = BoxTrace.builder(x, y).build();
    String js = trace.asJavascript(1);
    assertEquals(js, jsWithMissingValue);
  }

  @Test
  public void barTraceMissingValueTest() {
    Object[] x = {"sheep", "cows", "fish", "tree sloths"};
    double[] y = {1, 4, 9, 16};
    Object[] xWithMissingValue = {"sheep", "cows", "fish", null, "tree sloths"};
    double[] yWithMissingValue = {1, 4, 9, 10, 16};

    BarTrace trace = BarTrace.builder(x, y).build();
    String js = trace.asJavascript(1);
    BarTrace traceWithMissingValue = BarTrace.builder(xWithMissingValue, yWithMissingValue).build();
    String jsWithMissingValue = traceWithMissingValue.asJavascript(1);

    assertEquals(js, jsWithMissingValue);
  }

  @Test
  public void heatmapTraceMissingValueTest() {
    IntColumn y1WithMissingValue = IntColumn.create("y1", new Integer[] {1, 2, 3, null});
    IntColumn y2WithMissingValue = IntColumn.create("y2", new Integer[] {1, 2, 3, 3});
    IntColumn y1 = IntColumn.create("y1", new Integer[] {1, 2, 3});
    IntColumn y2 = IntColumn.create("y2", new Integer[] {1, 2, 3});

    Table tableWithMissingValue = Table.create().addColumns(y1WithMissingValue, y2WithMissingValue);
    Figure heatmapWithMissingValue = Heatmap.create("Plot", tableWithMissingValue, "y1", "y2");

    Table table = Table.create().addColumns(y1, y2);
    Figure heatmap = Heatmap.create("Plot", table, "y1", "y2");
    assertEquals(
        Arrays.toString(heatmap.getTraces()), Arrays.toString(heatmapWithMissingValue.getTraces()));
  }

  @Test
  public void scatter3DTraceWithMissingValue() {
    DoubleColumn xWithMissingValue = DoubleColumn.create("x", new Number[] {2, 2, 1});
    DoubleColumn yWithMissingValue = DoubleColumn.create("y", new Number[] {1, 2, 3});
    DoubleColumn zWithMissingValue = DoubleColumn.create("z", new Number[] {1, 4, null});

    Table tableWithMissingValue =
        Table.create().addColumns(xWithMissingValue, yWithMissingValue, zWithMissingValue);
    Figure scatterWithMissingValue =
        Scatter3DPlot.create("3D plot", tableWithMissingValue, "x", "y", "z");

    DoubleColumn x = DoubleColumn.create("x", new Number[] {2, 2});
    DoubleColumn y = DoubleColumn.create("y", new Number[] {1, 2});
    DoubleColumn z = DoubleColumn.create("z", new Number[] {1, 4});

    Table table = Table.create().addColumns(x, y, z);
    Figure scatter = Scatter3DPlot.create("3D plot", table, "x", "y", "z");
    assertEquals(
        Arrays.toString(scatter.getTraces()), Arrays.toString(scatterWithMissingValue.getTraces()));
  }

  @Test
  public void scatterTraceWithMissingValue() {
    DoubleColumn xWithMissingValue = DoubleColumn.create("x", new Number[] {2, 2, 1});
    DoubleColumn yWithMissingValue = DoubleColumn.create("y", new Number[] {1, 2, 3});

    Table tableWithMissingValue = Table.create().addColumns(xWithMissingValue, yWithMissingValue);
    Figure scatterWithMissingValue = ScatterPlot.create("2D plot", tableWithMissingValue, "x", "y");

    DoubleColumn x = DoubleColumn.create("x", new Number[] {2, 2});
    DoubleColumn y = DoubleColumn.create("y", new Number[] {1, 2});

    Table table = Table.create().addColumns(x, y);
    Figure scatter = ScatterPlot.create("2D plot", table, "x", "y");
    assertEquals(
        Arrays.toString(scatter.getTraces()), Arrays.toString(scatterWithMissingValue.getTraces()));
  }

  @Test
  public void histogramTraceWithMissingValue() {
    DoubleColumn yWithMissingValue =
        DoubleColumn.create(
            "y", new Number[] {1, 4, null, 11, 4, -1, 20, 4, 7, 9, 12, 8, 6, 28, 12});
    DoubleColumn y =
        DoubleColumn.create("y", new Number[] {1, 4, 11, 4, -1, 20, 4, 7, 9, 12, 8, 6, 28, 12});

    HistogramTrace traceWithMissingValue = HistogramTrace.builder(yWithMissingValue).build();
    HistogramTrace trace = HistogramTrace.builder(y).build();
    assertEquals(trace.asJavascript(1), traceWithMissingValue.asJavascript(1));
  }

  @Test
  public void histogram2DTraceWithMissingValue() {
    DoubleColumn y1WithMissingValue =
        DoubleColumn.create(
            "y1", new Number[] {1, 4, null, 11, 4, -1, 20, 4, 7, 9, 12, 8, 6, 28, 12});
    DoubleColumn y2WithMissingValue =
        DoubleColumn.create(
            "y2", new Number[] {3, 11, 19, 14, 11, 14, 5, 24, -4, 10, 15, 6, 5, 18, 42});

    Table tableWithMissingValue = Table.create().addColumns(y1WithMissingValue, y2WithMissingValue);
    Histogram2DTrace traceWithMissingValue =
        Histogram2DTrace.builder(tableWithMissingValue.nCol("y1"), tableWithMissingValue.nCol("y2"))
            .build();

    DoubleColumn y1 =
        DoubleColumn.create("y1", new Number[] {1, 4, 11, 4, -1, 20, 4, 7, 9, 12, 8, 6, 28, 12});
    DoubleColumn y2 =
        DoubleColumn.create(
            "y2", new Number[] {3, 11, 14, 11, 14, 5, 24, -4, 10, 15, 6, 5, 18, 42});

    Table table = Table.create().addColumns(y1, y2);
    Histogram2DTrace trace = Histogram2DTrace.builder(table.nCol("y1"), table.nCol("y2")).build();

    assertEquals(trace.asJavascript(1), traceWithMissingValue.asJavascript(1));
  }

  @Test
  public void pieTraceWithMissingValueTest() {
    Object[] x = {"sheep", "cows", "fish", "tree sloths"};
    double[] y = {1, 4, 9, 16};
    Object[] xWithMissingValue = {"sheep", null, "cows", "fish", "tree sloths"};
    double[] yWithMissingValue = {1, 10, 4, 9, 16};
    PieTrace trace = PieTrace.builder(x, y).build();
    PieTrace traceWithMissingValue = PieTrace.builder(xWithMissingValue, yWithMissingValue).build();
    assertEquals(trace.asJavascript(1), traceWithMissingValue.asJavascript(1));
  }
}
