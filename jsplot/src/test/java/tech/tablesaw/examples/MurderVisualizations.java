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

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.columns.numbers.ShortColumnType;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.AreaPlot;
import tech.tablesaw.plotly.api.Histogram;
import tech.tablesaw.plotly.api.Histogram2D;
import tech.tablesaw.plotly.api.HorizontalBarPlot;
import tech.tablesaw.plotly.api.LinePlot;
import tech.tablesaw.plotly.api.ParetoPlot;
import tech.tablesaw.plotly.api.PiePlot;

import static tech.tablesaw.aggregate.AggregateFunctions.sum;
import static tech.tablesaw.api.ColumnType.DOUBLE;

/**
 * Usage example using a Tornado data set
 */
public class MurderVisualizations extends AbstractExample {

    public static void main(String[] args) throws Exception {

        Table murders = Table.read().csv("../data/UCR1965_2015.csv");
        out(murders.structure());
        murders.setName("murders");
        NumberColumn<?> cleared = murders.numberColumn("clr");
        NumberColumn<?> murdered = murders.numberColumn("mrd");
        murdered.setName("murdered");
        cleared.setName("cleared");
        DoubleColumn clearanceRate = cleared.divide(murdered);
        clearanceRate.setName("clearance rate");
        NumberColumn<?> unsolved = murdered.subtract(cleared);
        unsolved.setName("unsolved");
        murders.addColumns(clearanceRate, unsolved);

        Table totals = murders
                .summarize("murdered", "cleared", "unsolved", sum)
                .by("year");

        Column<?> cum_unsolved = totals.numberColumn("sum [unsolved]").cumSum();
        cum_unsolved.setName("cumulative unsolved");
        totals.addColumns(cum_unsolved);
        Plot.show(AreaPlot.create("Cumulative unsolved homicides", totals, "year", "cumulative unsolved"));

        NumberColumn<?> rate = totals.numberColumn("sum [cleared]").divide(totals.numberColumn("sum [murdered]"));
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


        Table details = Table.read().csv("../data/SHR76_16.csv");
        out(details.structure().printAll());
        out(details.shape());
        out(details);

        details.shortColumn("offage")
                .set(details.shortColumn("offage").isEqualTo(999), ShortColumnType.missingValueIndicator());

        details.shortColumn("vicage")
                .set(details.shortColumn("vicage").isEqualTo(999), ShortColumnType.missingValueIndicator());

        details.shortColumn("vicCount")
                .set(details.shortColumn("vicCount").isEqualTo(0)
                        .andNot(details.stringColumn("situation").containsString("multiple victims")), (short) 1);

        details.shortColumn("offCount")
                .set(
                      details.shortColumn("offCount").isEqualTo(0)
                        .andNot(details.stringColumn("situation").containsString("multiple offenders")), (short) 1);

        out(details);
        out(details.stringColumn("weapon").unique().print());

        StringColumn weaponCategory = details.stringColumn("Weapon").copy();
        weaponCategory.set(
                weaponCategory.containsString("gun")
                        .or(weaponCategory.containsString("Firearm")
                                .or(weaponCategory.containsString("Rifle"))), "Firearms");
        weaponCategory.setName("Weapon category");
        details.addColumns(weaponCategory);
        Table categoryCount = details.countBy(weaponCategory);
        out(categoryCount.printAll());

        Table xtab1 = details.xTabColumnPercents("VicSex", "Weapon category");
        xtab1.columnsOfType(DOUBLE).forEach(e -> ((DoubleColumn)e).setPrintFormatter(NumberColumnFormatter.percent(1)));
        out(xtab1.printAll());

        Plot.show(Histogram.create("victim age", details, "vicage"));
        Plot.show(Histogram.create("offender age", details, "offage"));
        Plot.show(Histogram2D.create("2D Histogram of offender age by victim age", details, "offage", "vicage"));

        Table weaponSummary = details.countBy(details.stringColumn("weapon"));
        Plot.show(HorizontalBarPlot.create("homicide counts by weapon used", weaponSummary, "Category", "count"));

        Table femaleVictims = details.where(
                details.stringColumn("vicSex").isEqualTo("Female")
                        .and(details.stringColumn("Weapon category").isNotEqualTo("Firearms"))
                            .and(details.stringColumn("Solved").isEqualTo("No")));
        femaleVictims.setName("Selected female victims");
        out(femaleVictims.shape());
        Table asphyx = femaleVictims.where(
                femaleVictims.stringColumn("Weapon category").containsString("Asphyx")
                    .and(femaleVictims.stringColumn("Relationship").isEqualTo("Relationship not determined")))

                .sortAscendingOn("ID");
        Table fla = asphyx.where(asphyx.stringColumn("Statename").isEqualTo("FLA"));
        fla = (Table) fla.removeColumns("State", "Source", "Solved", "StateName", "ActionType", "Homicide", "OffSex");
        fla = fla.retainColumns("CNTYFIPS", "Agency", "year", "month", "VicAge", "VicRace", "Situation", "Circumstance");
        out(fla.printAll());
        fla.write().csv("fla_asphyx.csv");
        out(asphyx.shape());


        Plot.show(Histogram.create("age", asphyx, "vicAge"));
        Table counts = asphyx.xTabCounts("year", "StateName");
        counts.columnsOfType(DOUBLE).stream().forEach(e -> ((DoubleColumn)e).setPrintFormatter(NumberColumnFormatter.ints()));
        counts.columnsOfType(DOUBLE).stream().forEach(e -> ((DoubleColumn)e)
                .set(((DoubleColumn) e).isEqualTo(0), DoubleColumnType.missingValueIndicator()));
        out(counts.printAll());
        out(femaleVictims.shape());
    }
}