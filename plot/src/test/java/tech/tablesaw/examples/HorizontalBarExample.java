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

import static tech.tablesaw.aggregate.AggregateFunctions.count;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.BarTrace;

/** */
public class HorizontalBarExample {

  public static void main(String[] args) throws Exception {
    Table table = Table.read().csv("../data/tornadoes_1950-2014.csv");
    Table s = table.summarize("fatalities", count).by("State");

    BarTrace trace =
        BarTrace.builder(s.categoricalColumn(0), s.numberColumn(1))
            .orientation(BarTrace.Orientation.HORIZONTAL)
            .build();

    Layout layout = Layout.builder().title("Tornadoes by state").height(600).width(800).build();
    Plot.show(new Figure(layout, trace));
  }
}
