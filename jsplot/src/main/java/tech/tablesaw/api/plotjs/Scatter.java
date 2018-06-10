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
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.AxisBuilder;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.LayoutBuilder;
import tech.tablesaw.table.TableSliceGroup;

/**
 *
 */
public class Scatter extends JsPlot {

    public static void show(NumberColumn xCol, NumberColumn yCol) {
        double[] x = xCol.asDoubleArray();
        double[] y = yCol.asDoubleArray();
        show(x, y);
    }

    public static void show(double[] x, double[] y) {
        tech.tablesaw.plotly.traces.Scatter trace = tech.tablesaw.plotly.traces.Scatter.builder(x, y)
                .mode(tech.tablesaw.plotly.traces.Scatter.Mode.MARKERS)
                .build();

        Figure figure = new Figure(trace);
        Plot.plot(figure, "target", outputFile());
    }

    public static void show(double[] x, String xLabel, double[] y, String yLabel) {
        Axis xAxis = new AxisBuilder().title(xLabel).build();
        Axis yAxis = new AxisBuilder().title(yLabel).build();
        Layout layout = new LayoutBuilder().
                xAxis(xAxis)
                .yAxis(yAxis)
                .build();
        tech.tablesaw.plotly.traces.Scatter trace = tech.tablesaw.plotly.traces.Scatter.builder(x, y)
                .mode(tech.tablesaw.plotly.traces.Scatter.Mode.MARKERS)
                .build();

        Figure figure = new Figure(layout, trace);
        Plot.plot(figure, "target", outputFile());

    }

    public static void show(String title, NumberColumn x, NumberColumn y, TableSliceGroup groups) {
    }

    public static void show(String title, NumberColumn xCol, NumberColumn yCol) {
        double[] x = xCol.asDoubleArray();
        double[] y = yCol.asDoubleArray();

        Layout layout = new LayoutBuilder().title(title).build();

        tech.tablesaw.plotly.traces.Scatter trace = tech.tablesaw.plotly.traces.Scatter.builder(x, y)
                .mode(tech.tablesaw.plotly.traces.Scatter.Mode.MARKERS)
                .build();

        Figure figure = new Figure(layout, trace);
        Plot.plot(figure, "target", outputFile());
    }
}
