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
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.HistogramTrace;

import static tech.tablesaw.plotly.traces.HistogramTrace.HistFunc.*;

/** */
public class HistogramFuncExample {

  public static void main(String[] args) {

    Table test = Table.create(
            StringColumn.create("type").append("apples").append("apples").append("apples").append("oranges").append("bananas"),
            IntColumn.create("num").append(5).append(10).append(3).append(10).append(5));

    Layout layout1 = Layout.builder().title("Histogram COUNT Test (team batting averages)").build();
    HistogramTrace trace = HistogramTrace.
            builder(test.stringColumn("type"), test.intColumn("num"))
            .histFunc(COUNT)
            .build();

    Plot.show(new Figure(layout1, trace));

    Layout layout2 = Layout.builder().title("Hist SUM Test (team batting averages)").build();
    HistogramTrace trace2 = HistogramTrace.
            builder(test.stringColumn("type"), test.intColumn("num"))
            .histFunc(SUM)
            .build();

    Plot.show(new Figure(layout2, trace2));
  }
}
