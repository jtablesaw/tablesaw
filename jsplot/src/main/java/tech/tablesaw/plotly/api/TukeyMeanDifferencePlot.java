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

/**
 * A Tukey Mean-Difference Plot (AKA a Bland-Altman plot) is a kind of scatter plot used frequently
 * in medicine, biology and other fields, is used to visualize the differences between two
 * quantitative measurements. In particular, it is often used to evaluate whether two tests produce
 * 'the same' result.
 *
 * <p>For two numeric arrays, a and b, the plot shows the mean for each pair of observations (a + b)
 * / 2, as well as the differences between them: a - b.
 *
 * <p>For more information: https://en.wikipedia.org/wiki/Bland%E2%80%93Altman_plot
 */
public class TukeyMeanDifferencePlot {

  /**
   * Returns a figure containing a Tukey Mean-Difference Plot describing the differences between the
   * data in two columns of interest
   *
   * @param title A title for the plot
   * @param measure The measure being compared on the plot (e.g "inches" or "height in inches"
   * @param table The table containing the columns of interest
   * @param columnName1 The name of the first numeric column containing the data to plot
   * @param columnName2 The name of the second numeric column containing the data to plot
   * @return A quantile plot
   */
  public static Figure create(
      String title, String measure, Table table, String columnName1, String columnName2) {

    NumericColumn<?> xCol = table.nCol(columnName1);
    NumericColumn<?> yCol = table.nCol(columnName2);

    return create(title, measure, xCol.asDoubleArray(), yCol.asDoubleArray());
  }

  /**
   * Returns a figure containing a QQ Plot describing the differences between the distribution of
   * values in the columns of interest
   *
   * @param title A title for the plot
   * @param measure The measure being compared on the plot (e.g "inches" or "height in inches"
   * @param xData The data to plot on the x Axis
   * @param yData The data to plot on the y Axis
   * @return A quantile plot
   */
  public static Figure create(String title, String measure, double[] xData, double[] yData) {

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

    double[] averagePoints = new double[xData.length];
    double[] differencePoints = new double[xData.length];
    for (int i = 0; i < xData.length; i++) {
      averagePoints[i] = (xData[i] + yData[i]) / 2.0;
      differencePoints[i] = (xData[i] - yData[i]);
    }

    double xMin = StatUtils.min(xData);
    double xMax = StatUtils.max(xData);
    double[] zeroLineX = {xMin, xMax};
    double[] zeroLineY = {0, 0};

    // Draw the line indicating equal distributions (this is zero in this plot)
    ScatterTrace trace1 =
        ScatterTrace.builder(zeroLineX, zeroLineY)
            .mode(ScatterTrace.Mode.LINE)
            .name("y = x")
            .build();

    // Draw the actual data points
    ScatterTrace trace2 =
        ScatterTrace.builder(averagePoints, differencePoints).name("mean x difference").build();

    Layout layout =
        Layout.builder()
            .title(title)
            .xAxis(Axis.builder().title("mean (" + measure + ")").build())
            .yAxis(Axis.builder().title("difference (" + measure + ")").build())
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
