package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.api.plot.swing.TablesawTheme;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

/**
 * Render scatter plots using Swing
 */
public class Scatter {

  public static void show(String chartTitle, NumericColumn xColumn, NumericColumn yColumn) {
    show(chartTitle, xColumn, yColumn, 600, 400);
  }

  public static void show(String chartTitle, NumericColumn xColumn, NumericColumn yColumn, int width, int height) {
    double[] xData = xColumn.toDoubleArray();
    double[] yData = yColumn.toDoubleArray();

    // Create Chart
    XYChart chart = new XYChart(width, height);
    chart.setTitle(chartTitle);
    chart.setXAxisTitle(xColumn.name());
    chart.setYAxisTitle(yColumn.name());
    chart.getStyler().setTheme(new TablesawTheme());
    chart.getStyler().setMarkerSize(2);
    chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);

    XYSeries series = chart.addSeries(yColumn.name() + " by " + xColumn.name(), xData, yData);
    series.setMarker(SeriesMarkers.CIRCLE);
    new SwingWrapper<>(chart).displayChart();

  }
}
