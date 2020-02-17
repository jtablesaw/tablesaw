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
import tech.tablesaw.plotly.traces.HistogramTrace;

/** */
public class HistogramExample {

  public static void main(String[] args) throws Exception {
    Table baseball = Table.read().csv("../data/baseball.csv");

    Layout layout = Layout.builder().title("Distribution of team batting averages").build();
    HistogramTrace trace = HistogramTrace.builder(baseball.nCol("BA")).build();

    Plot.show(new Figure(layout, trace));
  }
}
