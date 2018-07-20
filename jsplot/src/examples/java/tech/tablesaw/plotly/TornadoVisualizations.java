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

import tech.tablesaw.AbstractExample;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.api.BarPlot;
import tech.tablesaw.plotly.api.BoxPlot;
import tech.tablesaw.plotly.api.Histogram;
import tech.tablesaw.plotly.api.ParetoPlot;
import tech.tablesaw.plotly.api.PiePlot;

import static tech.tablesaw.aggregate.AggregateFunctions.*;

/**
 * Usage example using a Tornado data set
 */
public class TornadoVisualizations extends AbstractExample {

    public static void main(String[] args) throws Exception {

        Table tornadoes = Table.read().csv("../data/tornadoes_1950-2014.csv");
        out(tornadoes.structure());
        tornadoes.setName("tornadoes");

        // filter out a bad data point
        tornadoes = tornadoes.where(tornadoes.numberColumn("Start Lat").isGreaterThan(20f));

        NumberColumn scale = tornadoes.numberColumn("scale");
        scale.set(scale.isEqualTo(-9), DoubleColumn.MISSING_VALUE);

        Table fatalities1 = tornadoes.summarize("fatalities", sum).by("scale");

        BarPlot.showHorizontal(
                "Total fatalities by scale",
                fatalities1,
                "scale",
                "sum [fatalities]");

        PiePlot.show("Total fatalities by scale", fatalities1, "scale", "sum [fatalities]");

        Table fatalities2 = tornadoes.summarize("fatalities", sum).by("state");

        ParetoPlot.showVertical(
                "Total Tornado Fatalities by State",
                fatalities2,
                "state",
                "sum [fatalities]");

        Table injuries1 = tornadoes.summarize("injuries", mean).by("scale");
        BarPlot.showHorizontal("Tornado Injuries by Scale", injuries1, "scale", "mean [injuries]");
        out(injuries1);

        // distributions
        Table level5 = tornadoes.where(scale.isEqualTo(5));

        out(tornadoes.numberColumn("injuries").print());
        Histogram.show("Distribution of injuries for scale = 5", level5, "injuries");

        BoxPlot.show("Average number of tornado injuries by scale", tornadoes,"scale", "injuries");

        Table injuriesByScaleState2 = tornadoes.summarize("Injuries", sum).by("State", "Scale");
        injuriesByScaleState2.setName("Total injuries by Tornado Scale and State");
        out(injuriesByScaleState2);

    }
}