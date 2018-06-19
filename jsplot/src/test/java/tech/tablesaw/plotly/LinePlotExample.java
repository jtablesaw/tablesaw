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

import org.junit.Test;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Line;
import tech.tablesaw.plotly.traces.ScatterTrace;

/**
 *
 */
public class LinePlotExample {

    @Test
    public void test1() throws Exception {
        Table baseball = Table.read().csv("../data/boston-robberies.csv");
        NumberColumn x = baseball.nCol("Record");
        NumberColumn y = baseball.nCol("Robberies");

        Layout layout = Layout.builder()
                .title("Monthly Boston Armed Robberies Jan. 1966 - Oct. 1975")
                .build();
        ScatterTrace trace = ScatterTrace.builder(x, y)
                .mode(ScatterTrace.Mode.LINE)
                .build();
        Plot.show(new Figure(layout, trace));

    }

    @Test
    public void test2() throws Exception {
        Table baseball = Table.read().csv("../data/boston-robberies.csv");
        NumberColumn x = baseball.nCol("Record");
        NumberColumn y = baseball.nCol("Robberies");

        Layout layout = Layout.builder()
                .title("Monthly Boston Armed Robberies Jan. 1966 - Oct. 1975")
                .build();

        ScatterTrace trace = ScatterTrace.builder(x, y)
                .mode(ScatterTrace.Mode.LINE)
                .line(Line.builder()
                        .shape(Line.Shape.SPLINE)
                        .smoothing(1.2f)
                        .build())
                .build();

        Plot.show(new Figure(layout, trace));

    }
}