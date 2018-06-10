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

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.LayoutBuilder;
import tech.tablesaw.plotly.traces.BoxPlot;
import tech.tablesaw.table.TableSliceGroup;

/**
 *
 */
public class Box extends JsPlot {

    public static void show(String title, TableSliceGroup groups, int columnIndex) {


    }

    public static void show(String title, Table table, String summaryColumnName, String groupingColumnName) {

        Layout layout = new LayoutBuilder().title(title).build();
        double[] y = table.nCol(summaryColumnName).asDoubleArray();

        Column groupingColumn = table.column(groupingColumnName);
        String[] x = columnToStringArray(groupingColumn);

        BoxPlot trace = BoxPlot.builder(x, y).build();

        Figure figure = new Figure(layout, trace);
        Plot.plot(figure, "target", outputFile());
    }
}
