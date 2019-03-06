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

import static tech.tablesaw.aggregate.AggregateFunctions.countNonMissing;
import static tech.tablesaw.aggregate.AggregateFunctions.mean;
import static tech.tablesaw.aggregate.AggregateFunctions.median;
import static tech.tablesaw.aggregate.AggregateFunctions.min;
import static tech.tablesaw.aggregate.AggregateFunctions.sum;

import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

/**
 * Usage example using a Tornado data set
 */
public class TornadoExample extends AbstractExample {

    public static void main(String[] args) throws Exception {

        Table tornadoes = Table.read().csv("../data/tornadoes_1950-2014.csv");
        assert (tornadoes != null);

        Table structure = tornadoes.structure();
        out(structure);
        out(structure.where(structure.stringColumn("Column Type").isEqualTo("DOUBLE")));

        tornadoes.setName("tornadoes");

        out();
        out("Column names");
        out(tornadoes.columnNames());

        out();
        out("Remove the 'State No' column");
        tornadoes.removeColumns("State No");
        out(tornadoes.columnNames());

        out();
        out("print the table's shape:");
        out(tornadoes.shape());

        out();
        out("Use first(3) to view the first 3 rows:");
        out(tornadoes.first(3));

        tornadoes = tornadoes.where(tornadoes.numberColumn("Start Lat").isGreaterThan(20f));

        out();
        out("Extract month from the date and make it a separate column");
        StringColumn month = tornadoes.dateColumn("Date").month();
        out(month.summary());

        out("Add the month column to the table");
        tornadoes.insertColumn(2, month);
        out(tornadoes.columnNames());

        out();
        out("Filtering: Tornadoes where there were fatalities");
        Table fatal = tornadoes.where(tornadoes.numberColumn("Fatalities").isGreaterThan(0));
        out(fatal.shape());

        out();
        out(fatal.first(5));

        out();
        out("Total fatalities: " + fatal.numberColumn("Fatalities").sum());

        out();
        out("Sorting on Fatalities in descending order");
        fatal = fatal.sortDescendingOn("Fatalities");
        out(fatal.first(5));

        out("");
        out("Calculating basic descriptive statistics on Fatalities");
        out(fatal.numberColumn("Fatalities").summary());

        //TODO(lwhite): Provide a param for title of the new table (or auto-generate a better one).
        Table injuriesByScale = tornadoes.summarize("Injuries", median).by("Scale");
        Table fob = tornadoes.summarize("Injuries", min).by("Scale", "State");
        out(fob);
        injuriesByScale.setName("Median injuries by Tornado Scale");
        out(injuriesByScale);

        injuriesByScale = tornadoes.summarize("Injuries", mean).by("Scale");
        injuriesByScale.setName("Average injuries by Tornado Scale");
        out(injuriesByScale);

        //TODO(lwhite): Provide a param for title of the new table (or auto-generate a better one).
        Table injuriesByScaleState = tornadoes.summarize("Injuries", median).by("Scale", "State");
        injuriesByScaleState.setName("Median injuries by Tornado Scale and State");
        out(injuriesByScaleState);

        Table injuriesByScaleState2 = tornadoes.summarize("Injuries", sum).by("State", "Scale");
        injuriesByScaleState2.setName("Total injuries by Tornado Scale and State");
        out(injuriesByScaleState2);

        // Average days between tornadoes in the summer

        // alternate, somewhat more precise approach
        DateColumn date = tornadoes.dateColumn("Date");

        Selection summerFilter =
                  date.month().isIn("JULY", "AUGUST")
                        .or(date.month().isEqualTo("JUNE")
                            .and(date.dayOfMonth().isGreaterThanOrEqualTo(21)))
                        .or(date.month().isEqualTo("SEPTEMBER")
                            .and(date.dayOfMonth().isLessThanOrEqualTo(22)));

        //Table summer = tornadoes.select(selection);
        Table summer = tornadoes.where(summerFilter);
        summer = summer.sortAscendingOn("Date", "Time");
        summer.addColumns(summer.dateColumn("Date").lag(1));

        // calculate the difference between a date and the prior date using the lagged column
        DateColumn summerDate = summer.dateColumn("Date");
        DateColumn laggedDate = summer.dateColumn("Date lag(1)");
        IntColumn delta = laggedDate.daysUntil(summerDate);  // the lagged date is earlier
        summer.addColumns(delta);

        out(summer.first(4));

        // now we can summarize by year so we don't inadvertently include differences between multiple years
        Table summary = summer.summarize(delta, mean, countNonMissing).by(summerDate.year());
        out(summary);

        // taking the mean of the annual means gives us an approximate answer
        // we could also use the count value calculated above to get a weighted average
        out(summary.nCol(1).mean());

        out();

        out("Writing the revised table to a new csv file");
        tornadoes.write().csv("../data/rev_tornadoes_1950-2014.csv");
    }
}