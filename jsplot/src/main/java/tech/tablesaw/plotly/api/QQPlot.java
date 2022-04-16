package tech.tablesaw.plotly.api;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import org.apache.commons.math3.stat.StatUtils;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;

public class QQPlot {

  /**
   * Returns a figure containing a QQ Plot describing the differences between the distribution of
   * values in the columns of interest
   *
   * @param title A title for the plot
   * @param table The table containing the columns of interest
   * @param columnName1 The name of the first numeric column containing the data to plot
   * @param columnName2 The name of the second numeric column containing the data to plot
   * @return A quantile plot
   */
  public static Figure create(String title, Table table, String columnName1, String columnName2) {

    NumericColumn<?> xCol = table.nCol(columnName1);
    NumericColumn<?> yCol = table.nCol(columnName2);

    return create(title, xCol.name(), xCol.asDoubleArray(), yCol.name(), yCol.asDoubleArray());
  }

  /**
   * Returns a figure containing a QQ Plot describing the differences between the distribution of
   * values in the columns of interest
   *
   * @param title A title for the plot
   * @param xTitle The name of the first numeric column containing the data to plot
   * @param xData The data to plot on the x Axis
   * @param yTitle The name of the second numeric column containing the data to plot
   * @param yData The data to plot on the y Axis
   * @return A quantile plot
   */
  public static Figure create(
      String title, String xTitle, double[] xData, String yTitle, double[] yData) {

    Preconditions.checkArgument(xData.length != 0, "x Data array is empty");
    Preconditions.checkArgument(yData.length != 0, "x Data array is empty");

    if (xData.length != yData.length) {
      double[] interpolatedData;
      if (xData.length < yData.length) {
        interpolatedData = interpolate(yData, xData.length);
        yData = interpolatedData;
      } else {
        interpolatedData = interpolate(xData, yData.length);
        xData = interpolatedData;
      }
    }

    Arrays.sort(xData);
    Arrays.sort(yData);
    double min = Math.min(xData[0], yData[0]);
    double max = Math.max(xData[xData.length - 1], yData[yData.length - 1]);
    double[] line = {min, max};

    // Draw the 45 degree line indicating equal distributions
    ScatterTrace trace1 =
        ScatterTrace.builder(line, line).mode(ScatterTrace.Mode.LINE).name("y = x").build();

    // Draw the actual data points
    ScatterTrace trace2 = ScatterTrace.builder(xData, yData).name("distributions").build();

    Layout layout =
        Layout.builder()
            .title(title)
            .xAxis(Axis.builder().title(xTitle).build())
            .yAxis(Axis.builder().title(yTitle).build())
            .height(700)
            .width(900)
            .build();
    return new Figure(layout, trace1, trace2);
  }

  /**
   * Returns a double array, whose values are quantiles from the given source, based on the given
   * size. The idea is to produce size elements that represent the quantiles of source array
   *
   * @param source The array to whose quantiles are calculated
   * @param size The size of the array to return
   */
  private static double[] interpolate(double[] source, int size) {
    double[] interpolatedData = new double[size];
    for (int i = 0; i < size; i++) {
      double value = ((i + .5) / (double) size) * 100;
      interpolatedData[i] = StatUtils.percentile(source, value);
    }
    return interpolatedData;
  }
}
