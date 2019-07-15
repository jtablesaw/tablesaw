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

import java.util.concurrent.atomic.AtomicInteger;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.Trace;

import tech.tablesaw.plotly.components.Grid;
import tech.tablesaw.plotly.traces.HistogramTrace;

/**
 * Basic sample vertical bar chart
 */
public class SubplotExample {

    public static void main(String[] args) throws Exception {
        Table table = Table.read().csv("../data/tornadoes_1950-2014.csv");
        AtomicInteger order = new AtomicInteger(0);
        Trace[] traces = table.numericColumns().stream().map(f -> {
            int i = order.incrementAndGet();
            return HistogramTrace.builder(f.asDoubleArray()).name(f.name()).xAxis("x"+i).yAxis("y"+i).build();
        }).toArray(Trace[]::new);

        int columns = 2;
        Grid grid = Grid.builder().columns(columns).rows(traces.length / columns).pattern(Grid.Pattern.INDEPENDENT).build();
        Layout layout = Layout.builder()
                .title("Subplot")
                .width(1000)
                .height(700)
                .grid(grid)
                .build();

        Plot.show(new Figure(layout, traces));
    }
}
