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

package tech.tablesaw.api.plot;

import org.junit.Test;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.plotjs.ScatterPlot;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;

import java.nio.file.Paths;

/**
 *
 */
public class ScatterplotExample {

    @Test
    public void testPlot() throws Exception {
        Table baseball = Table.read().csv("../data/baseball.csv");
        NumberColumn x = baseball.nCol("BA");
        NumberColumn y = baseball.nCol("W");
        //ScatterPlot.show(x, y);
        Layout layout = Layout.builder().title("Wins vs BA").build();
        ScatterPlot.show(layout, x, y);

/*
        Scatter.show("Regular season wins by year",
                baseball.numberColumn("W"),
                baseball.numberColumn("Year"),
                baseball.splitOn(baseball.categoricalColumn("Playoffs")));
*/

    }

    @Test
    public void testPlot1() throws Exception {
        Table baseball = Table.read().csv("../data/baseball.csv");
        NumberColumn x = baseball.nCol("BA");
        NumberColumn y = baseball.nCol("W");

        Layout layout = Layout.builder().title("Wins vs BA").build();
        ScatterTrace trace = ScatterTrace.builder(x, y)
                .mode(ScatterTrace.Mode.MARKERS)
                .build();
        Plot.show(new Figure(layout, trace),
                "target",
                Paths.get("testoutput","output.html").toFile());
    }
}