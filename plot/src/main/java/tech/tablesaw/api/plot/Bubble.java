/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.tablesaw.api.plot;

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.plotting.xchart.XchartBubble;

/**
 *
 * API to render a bubble plot.
 */
public final class Bubble {
    
    private static final String CHART_TITLE = "Bubbleplot";
    
    private Bubble() {}

    /**
     * Shows a bubble plot where the chart title will be "Bubbleplot" and titles 
     * for x and y axis will be taken from the columns.
     * 
     * @param x column for the x axis
     * @param y column for the y axis
     * @param data column for the bubbles
     */
    public static void show(NumericColumn x, NumericColumn y, NumericColumn data) {

        show(CHART_TITLE, x, y, data);
    }

    /**
     * Shows a bubble plot with the chart title. The titles 
     * for x and y axis will be taken from the columns.
     * 
     * @param chartTitle title for the chart
     * @param x column for the x axis
     * @param y column for the y axis
     * @param data column for the bubbles
     */
    public static void show(String chartTitle, NumericColumn x, NumericColumn y, NumericColumn data) {
        show(chartTitle, x, x.name(), y, y.name(), data);
    }
    
    /**
     * Shows a bubble plot with the chart title. The titles 
     * for x and y axis will be taken from the parameters.
     * 
     * @param chartTitle title for the chart
     * @param x column for the x axis
     * @param xAxis title for x axis
     * @param y column for the xy axis
     * @param yAxis title for y axis
     * @param data column for the bubbles
     */
    public static void show(String chartTitle, NumericColumn x, String xAxis, NumericColumn y, String yAxis, NumericColumn data) {
        show(chartTitle, x.asDoubleArray(), xAxis, y.asDoubleArray(), yAxis, data.asDoubleArray());
    }
    
    /**
     * Shows a bubble plot with the title "Bubbleplot" and data from the parameter.
     * 
     * @param x values for the x axis
     * @param y values for the y axis
     * @param data values for the bubbles 
     */
    public static void show(double[] x, double[] y, double[] data) {

        show(CHART_TITLE, x, y, data);
    }
    
    /**
     * Shows a bubble plot with the give title and data from the parameter.
     * 
     * @param chartTitle title for the chart
     * @param x values for the x axis
     * @param y values for the y axis
     * @param data values for the bubbles 
     */
    public static void show(String chartTitle, double[] x, double[] y, double[] data) {

        show(chartTitle, x, "", y, "", data);
    }

    /**
     * Shows a bubble plot with the give title and data from the parameter.
     * 
     * @param chartTitle title for the chart
     * @param x values for the x axis
     * @param xAxis
     * @param y values for the y axis
     * @param yAxis
     * @param data values for the bubbles
     */
    public static void show(String chartTitle, double[] x, String xAxis, double[] y, String yAxis, double[] data) {

        XchartBubble.show(chartTitle, x, xAxis, y, yAxis, data);
    }
}
