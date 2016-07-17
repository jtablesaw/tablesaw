package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.NumericColumn;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;

/**
 *
 */
public class Scatter {

  public static void show(String chartTitle, NumericColumn xColumn, NumericColumn yColumn) {
    double[] xData = xColumn.toDoubleArray();
    double[] yData = yColumn.toDoubleArray();

    // Create Chart
    XYChart chart = new XYChart(600, 400);
    chart.setTitle(chartTitle);
    chart.setXAxisTitle(xColumn.name());
    chart.setYAxisTitle(yColumn.name());
    chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
    chart.getStyler().setChartTitleVisible(true);
    chart.getStyler().setMarkerSize(1);
    chart.getStyler().setLegendVisible(false);
    chart.getStyler().setPlotBorderColor(Color.LIGHT_GRAY);
    chart.getStyler().setChartBackgroundColor(Color.WHITE);
    XYSeries series = chart.addSeries(yColumn.name() + " by " + xColumn.name(), xData, yData);
    series.setMarker(SeriesMarkers.CIRCLE);
    new SwingWrapper<>(chart).displayChart();

  }
}
