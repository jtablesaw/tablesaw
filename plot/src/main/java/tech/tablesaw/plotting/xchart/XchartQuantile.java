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
