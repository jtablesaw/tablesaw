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
package tech.tablesaw.plotting.xchart;

import javax.swing.JFrame;
import org.knowm.xchart.BubbleChart;
import org.knowm.xchart.BubbleChartBuilder;
import org.knowm.xchart.SwingWrapper;

import static tech.tablesaw.plotting.xchart.XchartDefaults.*;

/**
 *
 * Renders bubble plots.
 */
public final class XchartBubble {
    
    private XchartBubble(){}
    
    public static JFrame show(String chartTitle, double[] xData, String xLabel, double[] yData, String yLabel, double [] bubbleData) {
        return show(chartTitle, xData, xLabel, yData, yLabel, bubbleData, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public static JFrame show(String chartTitle, double[] xData, String xLabel, double[] yData, String yLabel, double [] bubbleData, int width, int height) {
        BubbleChart chart = new BubbleChartBuilder()
                .width(width).height(height).title(chartTitle)
                .xAxisTitle(xLabel).yAxisTitle(yLabel).build();
        chart.getStyler().setTheme(new TablesawTheme());

        chart.addSeries(SERIES, xData, yData, bubbleData);
        
        return display(chart);
    }
    
    private static JFrame display(BubbleChart chart) {
        return new SwingWrapper<>(chart).displayChart(WINDOW_TITLE);
    }
}
