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

package tech.tablesaw.plotly;

import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;

/**
 *
 */
public class ScatterplotExample {

    public static void main(String[] args) throws Exception {
        Table baseball = Table.read().csv("../data/baseball.csv");
        NumberColumn<?> x = baseball.nCol("BA");
        NumberColumn<?> y = baseball.nCol("W");
        Layout layout = Layout.builder().title("Wins vs BA")
                .xAxis(Axis.builder().title("Batting Average").build())
                .yAxis(Axis.builder().title("Wins").build())
                .build();
        Trace trace = ScatterTrace.builder(x, y).build();
        Plot.show(new Figure(layout, trace));
    }
}