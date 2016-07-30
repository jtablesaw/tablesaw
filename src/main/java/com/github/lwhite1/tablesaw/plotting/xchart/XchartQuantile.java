package com.github.lwhite1.tablesaw.plotting.xchart;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

/**
 *
 */
public class XchartQuantile {

  private static final String WINDOW_TITLE = "Tablesaw";

  public static void show(String chartTitle, NumericColumn yColumn) {
    double[] x = new double[yColumn.size()];

    for (int i = 0; i < x.length; i++) {
      x[i] = i / (float) x.length;
    }

    NumericColumn copy = (NumericColumn) yColumn.copy();
    copy.sortAscending();
    show(chartTitle, x, copy, 600, 400);
  }

  public static void show(String chartTitle, double[] xData, NumericColumn yColumn, int width, int height) {
    double[] yData = yColumn.toDoubleArray();

    // Create Chart
    XYChart chart = new XYChart(width, height);
    chart.setTitle(chartTitle);
    chart.setYAxisTitle(yColumn.name());
    chart.getStyler().setTheme(new TablesawTheme());
    chart.getStyler().setMarkerSize(2);
    chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);

    XYSeries series = chart.addSeries("Ranked: " + yColumn.name(), xData, yData);
    series.setMarker(SeriesMarkers.CIRCLE);
    new SwingWrapper<>(chart).displayChart(WINDOW_TITLE);
  }
}
