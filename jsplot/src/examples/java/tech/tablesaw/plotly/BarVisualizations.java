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
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.api.HorizontalBarPlot;
import tech.tablesaw.plotly.api.ParetoPlot;
import tech.tablesaw.plotly.api.PiePlot;
import tech.tablesaw.plotly.api.VerticalBarPlot;

import static tech.tablesaw.aggregate.AggregateFunctions.*;

/**
 * Usage example using a Tornado data set
 */
public class BarVisualizations extends AbstractExample {

    public static void main(String[] args) throws Exception {

        Table football = Table.read().csv("../data/nfl_2013.csv");
        out(football.structure().printAll());
        football.setName("football");

        Table offence = football.summarize("ScoreOff", sum).by("TeamName");

        VerticalBarPlot.show(
                "Total points scored by team",
                offence,
                "teamName",
                "sum [ScoreOff]");

        PiePlot.show("Total Points Scored by scale", offence, "TeamName", "sum [ScoreOff]");

        Table fatalities2 = football.summarize("fatalities", sum).by("state");

        ParetoPlot.show(
                "Total Tornado Fatalities by State",
                fatalities2,
                "state",
                "sum [fatalities]");

        Table injuries1 = football.summarize("injuries", mean).by("scale");
        HorizontalBarPlot.show("Tornado Injuries by Scale", injuries1, "scale", "mean [injuries]");
        out(injuries1);
    }
}