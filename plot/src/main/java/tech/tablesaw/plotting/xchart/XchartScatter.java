/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.plotting.xchart;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.table.TemporaryView;
import tech.tablesaw.table.ViewGroup;
import tech.tablesaw.util.DoubleArrays;

import java.util.Arrays;

/**
 * Render scatter plots using Swing
 */
public class XchartScatter {

    private static final int DEFAULT_WIDTH = 600;
    private static final int DEFAULT_HEIGHT = 400;

    private static final String WINDOW_TITLE = "Tablesaw";

    public static void show(String chartTitle, NumericColumn yColumn) {
        double[] x = DoubleArrays.toN(yColumn.size());
        show(chartTitle, x, yColumn, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public static void show(String chartTitle, NumericColumn xColumn, NumericColumn yColumn, int markerSize) {
        show(chartTitle, xColumn, yColumn, DEFAULT_WIDTH, DEFAULT_HEIGHT, markerSize);
    }

    public static void show(String chartTitle, NumericColumn xColumn, NumericColumn yColumn) {
        int markerSize = 3;
        show(chartTitle, xColumn, yColumn, DEFAULT_WIDTH, DEFAULT_HEIGHT, markerSize);
    }

    public static void show(String chartTitle, NumericColumn xColumn, NumericColumn yColumn, ViewGroup group) {
        XYChart chart = new XYChart(DEFAULT_WIDTH, DEFAULT_HEIGHT);
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
        XYChart chart = new XYChart(DEFAULT_WIDTH, DEFAULT_HEIGHT);
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
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);

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
        new SwingWrapper<>(chart)
                .displayChart(WINDOW_TITLE);
    }

    public static void show(String chartTitle, double[] xData, String xLabel, double[] yData, String yLabel, int
            width, int height) {

        // Create Chart
        XYChart chart = new XYChart(width, height);
        chart.setTitle(chartTitle);
        chart.setXAxisTitle(xLabel);
        chart.setYAxisTitle(yLabel);
        chart.getStyler().setTheme(new TablesawTheme());
        chart.getStyler().setMarkerSize(5);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);

        XYSeries series = chart.addSeries("series 1", xData, yData);
        series.setMarker(SeriesMarkers.CIRCLE);
        new SwingWrapper<>(chart)
                .displayChart(WINDOW_TITLE);
    }

    public static void show(String chartTitle, String xTitle, double[] x, String yTitle, double[] y) {

        XYChart chart = new XYChart(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        chart.setTitle(chartTitle);
        chart.setXAxisTitle(xTitle);
        chart.setYAxisTitle(yTitle);
        chart.getStyler().setTheme(new TablesawTheme());
        chart.getStyler().setMarkerSize(4);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);

        XYSeries series = chart.addSeries(chartTitle, x, y);
        series.setMarker(SeriesMarkers.CIRCLE);
        new SwingWrapper<>(chart)
                .displayChart(WINDOW_TITLE);

    }
}
