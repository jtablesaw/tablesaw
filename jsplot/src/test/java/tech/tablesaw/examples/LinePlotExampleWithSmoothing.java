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

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Line;
import tech.tablesaw.plotly.traces.ScatterTrace;

/** */
public class LinePlotExampleWithSmoothing {

  public static void main(String[] args) throws Exception {
    Table robberies = Table.read().csv("../data/boston-robberies.csv");
    NumericColumn<?> x = robberies.nCol("Record");
    NumericColumn<?> y = robberies.nCol("Robberies");

    Layout layout =
        Layout.builder().title("Monthly Boston Armed Robberies Jan. 1966 - Oct. 1975").build();

    ScatterTrace trace =
        ScatterTrace.builder(x, y)
            .mode(ScatterTrace.Mode.LINE)
            .line(Line.builder().shape(Line.Shape.SPLINE).smoothing(1.2).build())
            .build();

    Plot.show(new Figure(layout, trace));
  }
}
