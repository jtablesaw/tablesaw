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

import tech.tablesaw.examples.AbstractExample;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.plotly.api.HorizontalBarPlot;
import tech.tablesaw.plotly.api.ParetoPlot;
import tech.tablesaw.plotly.api.PiePlot;
import tech.tablesaw.plotly.api.VerticalBarPlot;

import static tech.tablesaw.aggregate.AggregateFunctions.mean;

/**
 * Usage example using a Tornado data set
 */
public class BarVisualizations extends AbstractExample {

    public static void main(String[] args) throws Exception {

        Table murders = Table.read().csv("../data/SHR76_16.csv");
        out(murders.structure().printAll());
        murders.setName("murders");
        NumberColumn victimAge = murders.numberColumn("vicAge");
        victimAge.set(victimAge.isEqualTo(999), DoubleColumnType.missingValueIndicator());

        Table count = murders.countBy(murders.stringColumn("state"));
        out(count.structure());
        Plot.show(VerticalBarPlot.create(
                "Total murders by state",
                count,
                "category",
                "count"));

        Table count2 = murders.countBy(murders.stringColumn("VicSex"));

        Plot.show(PiePlot.create("Total murders by victim gender", count2, "category", "count"));

        Plot.show(ParetoPlot.create(
                "Total murders by state",
                count,
                "category",
                "count"));

        Table ages = murders.summarize("vicAge", mean).by("relationship");
        Plot.show(HorizontalBarPlot.create("Victim Age by relationship to offender", ages, "relationship", "mean [vicAge]"));
        out(ages);
    }
}