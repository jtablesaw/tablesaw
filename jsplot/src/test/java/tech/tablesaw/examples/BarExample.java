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

import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.numbers.IntColumnType;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.HorizontalBarPlot;
import tech.tablesaw.plotly.api.VerticalBarPlot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.traces.BarTrace;
import tech.tablesaw.plotly.traces.Trace;

import static tech.tablesaw.aggregate.AggregateFunctions.sum;

/**
 * Basic sample vertical bar chart
 */
public class BarExample {

    public static void main(String[] args) throws Exception {
        Table table = Table.read().csv("../data/tornadoes_1950-2014.csv");
        NumberColumn<?> logNInjuries = table.numberColumn("injuries").add(1).logN();
        logNInjuries.setName("log injuries");
        table.addColumns(logNInjuries);
        IntColumn scale = table.intColumn("scale");
        scale.set(scale.isLessThan(0), IntColumnType.missingValueIndicator());

        Table summaryTable = table.summarize("fatalities", "log injuries", sum).by("Scale");

        Plot.show(HorizontalBarPlot.create("Tornado Impact", summaryTable, "scale", Layout.BarMode.STACK,"Sum [Fatalities]", "Sum [log injuries]"));

        Plot.show(
                VerticalBarPlot.create("Tornado Impact", summaryTable, "scale", Layout.BarMode.GROUP,"Sum [Fatalities]", "Sum [log injuries]"));


        Layout layout = Layout.builder()
                .title("Tornado Impact")
                .barMode(Layout.BarMode.GROUP)
                .showLegend(true)
                .build();

        String[] numberColNames = {"Sum [Fatalities]", "Sum [log injuries]"};
        String[] colors = {"#85144b", "#FF4136"};

        Trace[] traces = new Trace[2];
        for (int i = 0; i < 2; i++) {
            String name = numberColNames[i];
            BarTrace trace = BarTrace.builder(
                    summaryTable.categoricalColumn("scale"),
                    summaryTable.numberColumn(name))
                    .orientation(BarTrace.Orientation.VERTICAL)
                    .marker(Marker.builder().color(colors[i]).build())
                    .showLegend(true)
                    .name(name)
                    .build();
            traces[i] = trace;
        }
        Plot.show(new Figure(layout, traces));
    }
}
