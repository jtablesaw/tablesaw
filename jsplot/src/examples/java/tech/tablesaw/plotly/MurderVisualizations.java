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
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.plotly.api.AreaPlot;
import tech.tablesaw.plotly.api.LinePlot;
import tech.tablesaw.plotly.api.ParetoPlot;
import tech.tablesaw.plotly.api.PiePlot;

import static tech.tablesaw.aggregate.AggregateFunctions.sum;

/**
 * Usage example using a Tornado data set
 */
public class MurderVisualizations extends AbstractExample {

    public static void main(String[] args) throws Exception {

        Table murders = Table.read().csv("../data/UCR1965_2015.csv");
        out(murders.structure());
        murders.setName("murders");
        NumberColumn cleared = murders.numberColumn("clr");
        NumberColumn murdered = murders.numberColumn("mrd");
        murdered.setName("murdered");
        cleared.setName("cleared");
        NumberColumn clearanceRate = cleared.divide(murdered);
        clearanceRate.setName("clearance rate");
        NumberColumn unsolved = murdered.subtract(cleared);
        unsolved.setName("unsolved");
        murders.addColumns(clearanceRate, unsolved);

        Table totals = murders
                .summarize("murdered", "cleared", "unsolved", sum)
                .by("year");

        Column cum_unsolved = totals.numberColumn("sum [unsolved]").cumSum();
        cum_unsolved.setName("cumulative unsolved");
        totals.addColumns(cum_unsolved);
        Plot.show(AreaPlot.create("Cumulative unsolved homicides", totals, "year", "cumulative unsolved"));

        NumberColumn rate = totals.numberColumn("sum [cleared]").divide(totals.numberColumn("sum [murdered]"));
        rate.setName("clearance rate");
        totals.addColumns(rate);

        Plot.show(LinePlot.create(
                "clearance rate by year",
                totals,
                "year",
                "clearance rate"));

        StringColumn state = murders.stringColumn("state");
        state.set(state.isEqualTo("Rhodes Island"), "Rhode Island");

        Table annualMurders = murders.summarize("murdered", sum).by("year");

        Plot.show(LinePlot.create(
                "Total murders by year",
                annualMurders,
                "year",
                "sum [murdered]"));

        Table RI = murders.where(murders.stringColumn("State").isEqualTo("Rhode Island"));
        Table RI_total = RI.summarize("murdered", sum).by("county");
        Plot.show(PiePlot.create("RI murders by county", RI_total, "County", "sum [murdered]"));

        Table murders2 = murders.summarize("murdered", sum).by("state");

        Plot.show(
                ParetoPlot.createVertical(
                        "Total Murders by State",
                        murders2,
                        "state",
                        "sum [murdered]"));

    }
}