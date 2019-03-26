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
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;

/**
 * An example of how to create a scatter with two Y axes.
 *
 * Some key points:
 *      The first trace gets the Y-Axis defined as yAxis by default if no value is set for the yAxis attribute.
 *      The second trace in the example has a yAxis attribute of Y2. This corresponds to the layout's attribute yAxis2.
 *      The numbers 2, 3, or 4 must match between the axis specified in the trace and the axis specified in the layout.
 *      You must also specify overlaying on the second axis if the values of the two traces overlap. Otherwise only one
 *      trace will render. Note that the value of overlaying in the axis for trace 2, references the axis used by trace 1.
 */
public class ScatterplotWithTwoYAxes {

    public static void main(String[] args) throws Exception {
        Table baseball = Table.read().csv("../data/baseball.csv");
        NumberColumn<?> x = baseball.nCol("BA");
        NumberColumn<?> y = baseball.nCol("W");
        NumberColumn<?> y2 = baseball.nCol("SLG");

        Layout layout = Layout.builder().title("Wins vs BA and SLG")
                .xAxis(Axis.builder().title("Batting Average").build())
                .yAxis(Axis.builder()
                        .title("Wins")
                        .build())

                .yAxis2(Axis.builder()
                        .title("SLG")
                        .side(Axis.Side.right)
                        .overlaying(ScatterTrace.YAxis.Y)
                        .build())
                .build();

        Trace trace = ScatterTrace.builder(x, y)
                .name("Batting avg.")
                .marker(Marker.builder().opacity(.7).color("#01FF70").build())
                .build();

        Trace trace2 = ScatterTrace.builder(x, y2)
                .yAxis(ScatterTrace.YAxis.Y2)
                .name("Slugging pct.")
                .marker(Marker.builder().opacity(.7).color("rgb(17, 157, 255)").build())
                .build();

        Figure figure = new Figure(layout, trace2, trace);
        Plot.show(figure);
    }
}