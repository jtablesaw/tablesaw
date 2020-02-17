package tech.tablesaw.examples;

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Line;
import tech.tablesaw.plotly.traces.ScatterTrace;

public class LineOptionsExample {

  private final NumericColumn<?> x;
  private final NumericColumn<?> y;

  private LineOptionsExample() throws Exception {
    Table robberies = Table.read().csv("../data/boston-robberies.csv");
    this.x = robberies.nCol("Record");
    this.y = robberies.nCol("Robberies");
  }

  public static void main(String[] args) throws Exception {

    LineOptionsExample example = new LineOptionsExample();
    example.showDefaultLines();
    example.showWideLines();
    example.showRedLines();
    example.showSmoothedLines();
    example.showSteppedLines();
    example.showSteppedLines2();
    example.showDashedLine();
  }

  private void showDefaultLines() {
    Layout layout = Layout.builder().title("Default Line Style").build();
    ScatterTrace trace = ScatterTrace.builder(x, y).mode(ScatterTrace.Mode.LINE).build();
    Plot.show(new Figure(layout, trace));
  }

  /** Sets the line width */
  private void showWideLines() {
    Layout layout = Layout.builder().title("Wide lines").build();
    ScatterTrace trace =
        ScatterTrace.builder(x, y)
            .mode(ScatterTrace.Mode.LINE)
            .line(Line.builder().width(4).build())
            .build();
    Plot.show(new Figure(layout, trace));
  }

  private void showRedLines() {
    Layout layout = Layout.builder().title("Red Lines").build();
    ScatterTrace trace =
        ScatterTrace.builder(x, y)
            .mode(ScatterTrace.Mode.LINE)
            .line(Line.builder().color("red").build())
            .build();
    Plot.show(new Figure(layout, trace));
  }

  /** Shows a smoothed line */
  private void showSmoothedLines() {
    Layout layout = Layout.builder().title("Smoothed lines").build();
    ScatterTrace trace =
        ScatterTrace.builder(x, y)
            .mode(ScatterTrace.Mode.LINE)
            .line(Line.builder().shape(Line.Shape.SPLINE).smoothing(1.2).build())
            .build();
    Plot.show(new Figure(layout, trace));
  }

  /** Shows a stepped line shape using Shape HV */
  private void showSteppedLines() {
    Layout layout = Layout.builder().title("Stepped lines using HV shape").build();
    ScatterTrace trace =
        ScatterTrace.builder(x, y)
            .mode(ScatterTrace.Mode.LINE)
            .line(Line.builder().shape(Line.Shape.HV).build())
            .build();
    Plot.show(new Figure(layout, trace));
  }

  /** Shows a stepped line shape using Shape VHV */
  private void showSteppedLines2() {
    Layout layout = Layout.builder().title("Stepped lines using VHV shape").build();
    ScatterTrace trace =
        ScatterTrace.builder(x, y)
            .mode(ScatterTrace.Mode.LINE)
            .line(Line.builder().shape(Line.Shape.VHV).build())
            .build();
    Plot.show(new Figure(layout, trace));
  }

  /** Shows a dashed line */
  private void showDashedLine() {
    Layout layout = Layout.builder().title("Dashed lines").build();
    ScatterTrace trace =
        ScatterTrace.builder(x, y)
            .mode(ScatterTrace.Mode.LINE)
            .line(Line.builder().dash(Line.Dash.LONG_DASH).build())
            .build();
    Plot.show(new Figure(layout, trace));
  }
}
