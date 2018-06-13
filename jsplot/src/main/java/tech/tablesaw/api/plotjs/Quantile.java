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
public class Quantile extends JsPlot {

    public static void show(String chartTitle, NumberColumn yColumn) {
        double[] x = new double[yColumn.size()];

        for (int i = 0; i < x.length; i++) {
            x[i] = i / (float) x.length;
        }

        NumberColumn copy = yColumn.copy();
        copy.sortAscending();
        show(chartTitle, x, copy);
    }

    public static void show(String chartTitle, double[] x, NumberColumn yColumn) {

        double[] y = yColumn.asDoubleArray();
        Layout layout = new LayoutBuilder().title(chartTitle).build();

        tech.tablesaw.plotly.traces.Scatter trace = tech.tablesaw.plotly.traces.Scatter.builder(x, y)
                .mode(tech.tablesaw.plotly.traces.Scatter.Mode.MARKERS)
                .build();

        Figure figure = new Figure(layout, trace);
        Plot.plot(figure, "target", outputFile());
    }
}
