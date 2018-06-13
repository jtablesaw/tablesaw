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

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.LayoutBuilder;
import tech.tablesaw.plotly.traces.PiePlot;

public class Pie extends JsPlot {

    public static void show(String title, Column categoryColumn, NumberColumn numberColumn) {
        String[] x = columnToStringArray(categoryColumn);
        double[] y = numberColumn.asDoubleArray();

        PiePlot trace = PiePlot.builder(x, y).build();
        Layout layout = new LayoutBuilder().title(title).build();
        Figure figure = new Figure(layout, trace);
        Plot.plot(figure, "target", outputFile());
    }

    /**
     * Display a pie chart with the given title, derived from the given table
     * @param title The main title for the plot
     * @param table Table must have its first column as the grouping column, and the second as the number column
     */
    public static void show(String title, Table table) {

        if (table.column(0) instanceof StringColumn) {
            show(title, table.stringColumn(0), table.numberColumn(1));
        }
        if (table.column(0) instanceof DoubleColumn) {
            show(title, table.numberColumn(0), table.nCol(1));
        }
    }
}