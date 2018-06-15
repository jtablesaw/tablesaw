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
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;
import tech.tablesaw.table.TableSliceGroup;

/**
 *
 */
public class ScatterPlot extends JsPlot {

    public static void show(NumberColumn xCol, NumberColumn yCol) {
        Axis xAxis = Axis.builder().title(xCol.name()).build();
        Axis yAxis = Axis.builder().title(yCol.name()).build();

        Layout layout = Layout.builder()
                .xAxis(xAxis)
                .yAxis(yAxis)
                .build();
        show(layout, xCol, yCol);
    }

    /**
     * Display scatter plot with one or more series using the given layout
     */
    public static void show(Layout layout, NumberColumn xCol, NumberColumn ... yCols) {
        Figure figure = figure(layout, xCol, yCols);
        Plot.show(figure, "target", outputFile());
    }

    /**
     * Display scatter plot with one or more series using the given layout
     */
    public static Figure figure(Layout layout, NumberColumn xCol, NumberColumn ... yCols) {
        double[] x = xCol.asDoubleArray();
        Trace[] traces = new Trace[yCols.length];
        for (int i = 0; i < yCols.length; i++) {
            NumberColumn column = yCols[i];
            double[] y = column.asDoubleArray();
            ScatterTrace trace = ScatterTrace.builder(x, y)
                    .mode(ScatterTrace.Mode.MARKERS)
                    .name(column.name())
                    .build();
            traces[i] = trace;
        }

        return new Figure(layout, traces);
    }

    public static void show(String title, NumberColumn x, NumberColumn y, TableSliceGroup groups) {}
}
