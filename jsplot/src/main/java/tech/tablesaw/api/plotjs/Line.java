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

package tech.tablesaw.api.plotjs;

import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.LayoutBuilder;

/**
 * Displays a line chart
 */
public class Line extends JsPlot {

    public static void show(String chartTitle, NumberColumn xCol, NumberColumn yCol) {
        double[] x = xCol.asDoubleArray();
        double[] y = yCol.asDoubleArray();

        Layout layout = new LayoutBuilder().title(chartTitle).build();

        tech.tablesaw.plotly.traces.Scatter trace = tech.tablesaw.plotly.traces.Scatter.builder(x, y)
                .mode(tech.tablesaw.plotly.traces.Scatter.Mode.LINE)
                .build();

        Figure figure = new Figure(layout, trace);
        Plot.plot(figure, "target", outputFile());
    }

    /**
     * Displays a line chart with multiple series
     *
     * @param chartTitle    The main title
     * @param x             The column supplying the x values
     * @param y             The column supplying the y values
     */
    public static void show(String chartTitle, NumberColumn x, NumberColumn... y) {
    }
}
