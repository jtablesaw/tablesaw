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

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.traces.HistogramTrace;
import tech.tablesaw.table.TableSliceGroup;

/**
 *
 */
public class HistogramOverlayExample {

    public static void main(String[] args) throws Exception {
        Table baseball = Table.read().csv("../data/baseball.csv");

        Layout layout = Layout.builder()
                .title("Distribution of team batting averages")
                .barMode(Layout.BarMode.OVERLAY)
                .showLegend(true)
                .build();

        TableSliceGroup groups = baseball.splitOn("league");
        Table t1 = groups.get(0).asTable();

        HistogramTrace trace1 = HistogramTrace
                .builder(t1.nCol("BA"))
                .name("American Leage")
                .opacity(.75)
                .nBinsX(24)
                .marker(Marker.builder().color("#FF4136").build())
                .build();

        Table t2 = groups.get(1).asTable();
        HistogramTrace trace2 = HistogramTrace
                .builder(t2.nCol("BA"))
                .name("National League")
                .opacity(.75)
                .nBinsX(24)
                .marker(Marker.builder().color("#7FDBFF").build())
                .build();

        Plot.show(new Figure(layout, trace1, trace2));
    }
}