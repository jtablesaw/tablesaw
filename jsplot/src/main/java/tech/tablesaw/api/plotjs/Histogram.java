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
 *
 */
public class Histogram extends JsPlot {

    public static void show(NumberColumn xCol) {
        double[] x = xCol.asDoubleArray();
        tech.tablesaw.plotly.traces.Histogram trace = tech.tablesaw.plotly.traces.Histogram.builder(x).build();

        Figure figure = new Figure(trace);

        Plot.plot(figure, "target", outputFile());
    }

    public static void show(double[] x) {

        tech.tablesaw.plotly.traces.Histogram trace = tech.tablesaw.plotly.traces.Histogram.builder(x).build();

        Figure figure = new Figure(trace);

        Plot.plot(figure, "target", outputFile());
    }

    public static void show(String title, NumberColumn xCol) {
        Layout layout = new LayoutBuilder().title(title).build();

        double[] x = xCol.asDoubleArray();
        tech.tablesaw.plotly.traces.Histogram trace = tech.tablesaw.plotly.traces.Histogram.builder(x).build();

        Figure figure = new Figure(layout, trace);

        Plot.plot(figure, "target", outputFile());
    }
}
