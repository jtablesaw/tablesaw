package tech.tablesaw.examples;

import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.ColorBar;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Gradient;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Line;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.components.Symbol;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;

public class MarkerOptionsExample {

  private final Table baseball;
  private final NumericColumn<?> x;
  private final NumericColumn<?> y;

  private MarkerOptionsExample() throws Exception {
    this.baseball = Table.read().csv("../data/baseball.csv");
    this.x = baseball.nCol("BA");
    this.y = baseball.nCol("W");
  }

  public static void main(String[] args) throws Exception {

    MarkerOptionsExample example = new MarkerOptionsExample();

    example.showDefault();
    example.showRedMarkers();
    example.showLargeMarkers();
    example.show50PctOpacity();
    example.showRGBColor();
    example.showBowTieSymbol();
    example.showCustomLine();
    example.showMarkerGradient();
    example.showColorScale();
    example.showColorScaleWithBar();
    example.showColorScaleWithCustomBar();
  }

  /** Shows a scatter with red markers */
  private void showRedMarkers() {
    Layout layout =
        Layout.builder()
            .title("red markers")
            .xAxis(Axis.builder().title("Batting Average").build())
            .yAxis(Axis.builder().title("Wins").build())
            .build();

    Trace trace = ScatterTrace.builder(x, y).marker(Marker.builder().color("red").build()).build();
    Plot.show(new Figure(layout, trace));
  }

  /**
   * Shows a scatter with color set as a color scale
   *
   * <p>The color scale requires that an array of numeric values be provided, here we just scale
   * according to the number of wins the team has.
   */
  private void showColorScale() {
    Layout layout =
        Layout.builder()
            .title("color scaled by # of wins")
            .xAxis(Axis.builder().title("Batting Average").build())
            .yAxis(Axis.builder().title("Wins").build())
            .build();

    IntColumn wins = baseball.intColumn("W");
    Trace trace =
        ScatterTrace.builder(x, y)
            .marker(
                Marker.builder()
                    .color(wins.asDoubleArray())
                    .cMinAndMax(wins.min(), wins.max())
                    .colorScale(Marker.Palette.YL_GN_BU)
                    .build())
            .build();
    Plot.show(new Figure(layout, trace));
  }

  /**
   * Shows a scatter with color set as a color scale
   *
   * <p>The color scale requires that an array of numeric values be provided, here we just scale
   * according to the number of wins the team has.
   */
  private void showColorScaleWithBar() {
    Layout layout =
        Layout.builder()
            .title("color scaled with color bar")
            .xAxis(Axis.builder().title("Batting Average").build())
            .yAxis(Axis.builder().title("Wins").build())
            .build();

    IntColumn wins = baseball.intColumn("W");
    Trace trace =
        ScatterTrace.builder(x, y)
            .marker(
                Marker.builder()
                    .color(wins.asDoubleArray())
                    .cMinAndMax(wins.min(), wins.max())
                    .colorScale(Marker.Palette.YL_GN_BU)
                    .showScale(true)
                    .build())
            .build();
    Plot.show(new Figure(layout, trace));
  }

  /**
   * Shows a scatter with color set as a color scale
   *
   * <p>The color scale requires that an array of numeric values be provided, here we just scale
   * according to the number of wins the team has.
   */
  private void showColorScaleWithCustomBar() {
    Layout layout =
        Layout.builder()
            .title("color scaled with custom color bar")
            .xAxis(Axis.builder().title("Batting Average").build())
            .yAxis(Axis.builder().title("Wins").build())
            .build();

    IntColumn wins = baseball.intColumn("W");
    Trace trace =
        ScatterTrace.builder(x, y)
            .marker(
                Marker.builder()
                    .color(wins.asDoubleArray())
                    .cMinAndMax(wins.min(), wins.max())
                    .colorScale(Marker.Palette.YL_GN_BU)
                    .colorBar(
                        ColorBar.builder()
                            .borderColor("blue")
                            .thickness(40)
                            .borderWidth(2)
                            .lenMode(ColorBar.LenMode.PIXELS)
                            .len(200)
                            .bgColor("rgb(255,255,204)")
                            .thicknessMode(ColorBar.ThicknessMode.PIXELS)
                            .build())
                    .showScale(true)
                    .build())
            .build();
    Figure figure = new Figure(layout, trace);
    Plot.show(figure);
  }

  /**
   * Shows a scatter with a gradient. In this example we set both the type and the color (which is
   * used as the value to shade into). Color normally defaults to a dark neutral grey (black?)
   *
   * <p>The size is increased to make the gradient more visible
   */
  private void showMarkerGradient() {
    Layout layout =
        Layout.builder()
            .title("marker gradient")
            .xAxis(Axis.builder().title("Batting Average").build())
            .yAxis(Axis.builder().title("Wins").build())
            .build();

    Trace trace =
        ScatterTrace.builder(x, y)
            .marker(
                Marker.builder()
                    .size(10)
                    .gradient(
                        Gradient.builder().type(Gradient.Type.HORIZONTAL).color("red").build())
                    .build())
            .build();
    Plot.show(new Figure(layout, trace));
  }

  /** Shows a scatter with an outline on the marker */
  private void showCustomLine() {
    Layout layout =
        Layout.builder()
            .title("outline")
            .xAxis(Axis.builder().title("Batting Average").build())
            .yAxis(Axis.builder().title("Wins").build())
            .build();

    Trace trace =
        ScatterTrace.builder(x, y)
            .marker(
                Marker.builder()
                    .line(Line.builder().color("rgb(231, 99, 250)").width(1).build())
                    .build())
            .build();
    Plot.show(new Figure(layout, trace));
  }

  /**
   * Shows a scatter with a bowtie symbol instead of a circle. Many other options are available as
   * defined by the Symbol enum
   */
  private void showBowTieSymbol() {
    Layout layout =
        Layout.builder()
            .title("custom symbol type: Bow Tie")
            .xAxis(Axis.builder().title("Batting Average").build())
            .yAxis(Axis.builder().title("Wins").build())
            .build();

    Trace trace =
        ScatterTrace.builder(x, y).marker(Marker.builder().symbol(Symbol.BOWTIE).build()).build();
    Plot.show(new Figure(layout, trace));
  }

  /** Shows a scatter with 50% opacity */
  private void show50PctOpacity() {
    Layout layout =
        Layout.builder()
            .title("50% opacity")
            .xAxis(Axis.builder().title("Batting Average").build())
            .yAxis(Axis.builder().title("Wins").build())
            .build();

    Trace trace = ScatterTrace.builder(x, y).marker(Marker.builder().opacity(.5).build()).build();
    Plot.show(new Figure(layout, trace));
  }

  /** Shows a scatter with large markers */
  private void showLargeMarkers() {
    Layout layout =
        Layout.builder()
            .title("large markers")
            .xAxis(Axis.builder().title("Batting Average").build())
            .yAxis(Axis.builder().title("Wins").build())
            .build();

    Trace trace = ScatterTrace.builder(x, y).marker(Marker.builder().size(9).build()).build();
    Plot.show(new Figure(layout, trace));
  }

  /** Shows a scatter with color set as RGB value */
  private void showRGBColor() {
    Layout layout =
        Layout.builder()
            .title("RGB value used for marker color")
            .xAxis(Axis.builder().title("Batting Average").build())
            .yAxis(Axis.builder().title("Wins").build())
            .build();

    Trace trace =
        ScatterTrace.builder(x, y)
            .marker(Marker.builder().color("rgb(17, 157, 255)").build())
            .build();
    Plot.show(new Figure(layout, trace));
  }

  /** Shows a scatter with no marker customization */
  private void showDefault() {
    Layout layout =
        Layout.builder()
            .title("default")
            .xAxis(Axis.builder().title("Batting Average").build())
            .yAxis(Axis.builder().title("Wins").build())
            .build();

    Trace trace = ScatterTrace.builder(x, y).build();
    Plot.show(new Figure(layout, trace));
  }
}
