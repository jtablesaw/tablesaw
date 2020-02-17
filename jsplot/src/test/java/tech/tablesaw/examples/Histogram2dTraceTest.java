package tech.tablesaw.examples;

import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.traces.Histogram2DTrace;

public class Histogram2dTraceTest {

  private static final double[] x = {1, 4, 9, 16, 11, 4, -1, 20, 4, 7, 9, 12, 8, 6};
  private static final double[] y = {1, 4, 9, 16, 11, 4, -1, 20, 4, 7, 9, 12, 8, 6};

  public static void main(String[] args) {

    Histogram2DTrace trace = Histogram2DTrace.builder(x, y).build();
    Plot.show(new Figure(trace));
  }
}
