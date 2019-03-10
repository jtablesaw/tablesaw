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
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.numbers.IntColumnType;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.HorizontalBarPlot;
import tech.tablesaw.plotly.api.ParetoPlot;
import tech.tablesaw.plotly.api.PiePlot;

import static tech.tablesaw.aggregate.AggregateFunctions.mean;
import static tech.tablesaw.aggregate.AggregateFunctions.sum;

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

        IntColumn scale = tornadoes.intColumn("scale");
        scale.set(scale.isEqualTo(-9), IntColumnType.missingValueIndicator());

        Table fatalities1 = tornadoes.summarize("fatalities", sum).by("scale");

        Plot.show(HorizontalBarPlot.create(
                "Total fatalities by scale",
                fatalities1,
                "scale",
                "sum [fatalities]"));

        Plot.show(PiePlot.create("Total fatalities by scale", fatalities1, "scale", "sum [fatalities]"));

        Table fatalities2 = tornadoes.summarize("fatalities", sum).by("state");

        Plot.show(
                ParetoPlot.createVertical(
                        "Total Tornado Fatalities by State",
                        fatalities2,
                        "state",
                        "sum [fatalities]"));

        Table injuries1 = tornadoes.summarize("injuries", mean).by("scale");
        Plot.show(HorizontalBarPlot.create("Tornado Injuries by Scale", injuries1, "scale", "mean [injuries]"));
        out(injuries1);
    }
}