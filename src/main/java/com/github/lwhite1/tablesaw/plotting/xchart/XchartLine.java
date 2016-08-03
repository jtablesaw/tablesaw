package com.github.lwhite1.tablesaw.plotting.xchart;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import com.github.lwhite1.tablesaw.table.TemporaryView;
import com.github.lwhite1.tablesaw.table.ViewGroup;
import com.github.lwhite1.tablesaw.util.DoubleArrays;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.Arrays;

/**
 *
 */
public class XchartLine {

  private static final String WINDOW_TITLE = "Tablesaw";

  public static void show(String chartTitle, NumericColumn yColumn) {
    double[] x = DoubleArrays.toN(yColumn.size());
    show(chartTitle, x, yColumn, 600, 400);
  }

  public static void show(String chartTitle, NumericColumn xColumn, NumericColumn yColumn, int markerSize) {
    show(chartTitle, xColumn, yColumn, 600, 400, markerSize);
  }

  public static void show(String chartTitle, NumericColumn xColumn, NumericColumn yColumn) {
    int markerSize = 2;
    show(chartTitle, xColumn, yColumn, 600, 400, markerSize);
  }

  public static void show(String chartTitle, NumericColumn xColumn, NumericColumn yColumn, ViewGroup group) {
    XYChart chart = new XYChart(600, 400);
    chart.setTitle(chartTitle);
    chart.setXAxisTitle(xColumn.name());
    chart.setYAxisTitle(yColumn.name());
    chart.getStyler().setTheme(new TablesawTheme());

    chart.getStyler().setMarkerSize(5);

    chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);

    for (TemporaryView view : group) {
      double[] xData = view.numericColumn(xColumn.name()).toDoubleArray();
      double[] yData = view.numericColumn(yColumn.name()).toDoubleArray();
      chart.addSeries(view.name(), Arrays.copyOf(xData, xData.length), Arrays.copyOf(yData, yData.length));
    }
    new SwingWrapper<>(chart).displayChart(WINDOW_TITLE);
  }

  public static void show(String chartTitle,
                          NumericColumn xColumn,
                          NumericColumn yColumn,
                          ViewGroup group,
                          int markerSize) {
    XYChart chart = new XYChart(600, 400);
    chart.setTitle(chartTitle);
    chart.setXAxisTitle(xColumn.name());
    chart.setYAxisTitle(yColumn.name());
    chart.getStyler().setTheme(new TablesawTheme());

    chart.getStyler().setMarkerSize(markerSize);

    chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);

    for (TemporaryView view : group) {
      double[] xData = view.numericColumn(xColumn.name()).toDoubleArray();
      double[] yData = view.numericColumn(yColumn.name()).toDoubleArray();
      chart.addSeries(view.name(), Arrays.copyOf(xData, xData.length), Arrays.copyOf(yData, yData.length));
    }
    new SwingWrapper<>(chart).displayChart(WINDOW_TITLE);
  }

  public static void show(String chartTitle,
                          NumericColumn xColumn,
                          NumericColumn yColumn,
                          int width,
                          int height,
                          int markerSize) {
    double[] xData = xColumn.toDoubleArray();
    double[] yData = yColumn.toDoubleArray();

    // Create Chart
    XYChart chart = new XYChart(width, height);
    chart.setTitle(chartTitle);
    chart.setXAxisTitle(xColumn.name());
    chart.setYAxisTitle(yColumn.name());
    chart.getStyler().setTheme(new TablesawTheme());
    chart.getStyler().setMarkerSize(markerSize);
  //  chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);

    XYSeries series = chart.addSeries(yColumn.name() + " by " + xColumn.name(), xData, yData);
    series.setMarker(SeriesMarkers.CIRCLE);
    new SwingWrapper<>(chart).displayChart(WINDOW_TITLE);
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
