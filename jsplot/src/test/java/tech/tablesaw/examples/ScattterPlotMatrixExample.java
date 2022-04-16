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
package tech.tablesaw.examples;

import java.util.ArrayList;
import java.util.List;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Grid;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;

/** Renders a scatter plot matrix for the first 6 numeric columns in the tornado dataset */
public class ScattterPlotMatrixExample {

  public static void main(String[] args) throws Exception {
    Table table = Table.read().csv("../data/tornadoes_1950-2014.csv").sampleN(500);

    List<NumericColumn<?>> columns = table.numericColumns().subList(0, 6);
    List<Trace> traceList = new ArrayList<>();
    int count = 1;
    for (int i = 0; i < columns.size(); i++) {
      for (NumericColumn<?> column : columns) {
        Trace t =
            ScatterTrace.builder(column.asDoubleArray(), columns.get(i).asDoubleArray())
                .xAxis("x" + count)
                .yAxis("y" + count)
                .name(columns.get(i).name() + " x " + column.name())
                .marker(Marker.builder().size(3).opacity(.5).build())
                .build();

        traceList.add(t);
        count++;
      }
    }
    Trace[] traces = traceList.toArray(new Trace[0]);

    Grid grid =
        Grid.builder()
            .columns(columns.size())
            .rows(columns.size())
            .pattern(Grid.Pattern.INDEPENDENT)
            .xSide(Grid.XSide.BOTTOM)
            .build();

    Layout layout =
        Layout.builder().title("Scatter Plot Matrix").width(1100).height(1100).grid(grid).build();

    Plot.show(new Figure(layout, traces));
  }
}
