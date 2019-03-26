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

import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;

/**
 *
 */
public class ScatterplotExample2 {

    public static void main(String[] args) throws Exception {
        Table tornadoes = Table.read().csv("../data/tornadoes_1950-2014.csv");
        tornadoes = tornadoes.where(tornadoes.nCol("Start lat").isGreaterThan(20));
        NumberColumn<?> x = tornadoes.nCol("Start lon");
        NumberColumn<?> y = tornadoes.nCol("Start lat");
        Layout layout = Layout.builder()
                .title("tornado start points")
                .height(600)
                .width(800)
                .build();
        Trace trace = ScatterTrace.builder(x, y)
                .marker(Marker.builder().size(1).build())
                .build();
        Plot.show(new Figure(layout, trace));

    }
}