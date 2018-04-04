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

package tech.tablesaw;

import tech.tablesaw.api.*;
import tech.tablesaw.filtering.Filter;

import static tech.tablesaw.aggregate.AggregateFunctions.*;
import static tech.tablesaw.table.Relation.anyOf;
import static tech.tablesaw.table.Relation.both;

/**
 * Usage example using a Tornado data set
 */
public class TornadoExample extends AbstractExample {

    public static void main(String[] args) throws Exception {

        Table tornadoes = Table.read().csv("../data/tornadoes_1950-2014.csv");
        assert (tornadoes != null);

        out(tornadoes.structure());
        out(tornadoes.structure().selectWhere(tornadoes.stringColumn("Column Type").isEqualTo("NUMBER")));

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

        tornadoes = tornadoes.selectWhere(tornadoes.numberColumn("Start Lat").isGreaterThan(20f));

        out();
        out("Extact month from the date and make it a separate column");
        StringColumn month = tornadoes.dateColumn("Date").month();
        out(month.summary());

        out("Add the month column to the table");
        tornadoes.addColumn(2, month);
        out(tornadoes.columnNames());

        out();
        out("Filtering: Tornadoes where there were fatalities");
        Table fatal = tornadoes.selectWhere(tornadoes.numberColumn("Fatalities").isGreaterThan(0));
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

        //TODO(lwhite): Provide a param for title of the new table (or auto-generate a better one).
        Table injuriesByScaleState = tornadoes.summarize("Injuries", median).by("Scale", "State");
        injuriesByScaleState.setName("Median injuries by Tornado Scale and State");
        out(injuriesByScaleState);

        // Average days between tornadoes in the summer

        // alternate, somewhat more precise approach
        DateColumn date = tornadoes.dateColumn("Date");

        Filter summerFilter =
                anyOf(
                        date.month().isIn("JULY", "AUGUST"),
                        both(date.month().isEqualTo("JUNE"),
                                date.dayOfMonth().isGreaterThanOrEqualTo(21)),
                        both(date.month().isEqualTo("SEPTEMBER"),
                            date.dayOfMonth().isLessThanOrEqualTo(22)));

        //Table summer = tornadoes.selectWhere(selection);
        Table summer = tornadoes.selectWhere(summerFilter);
        summer = summer.sortAscendingOn("Date", "Time");
        summer.addColumn(summer.dateColumn("Date").lag(1));

        // calculate the difference between a date and the prior date using the lagged column
        DateColumn summerDate = summer.dateColumn("Date");
        DateColumn laggedDate = summer.dateColumn("Date lag(1)");
        NumberColumn delta = laggedDate.daysUntil(summerDate);  // the lagged date is earlier
        summer.addColumn(delta);

        out(summer.first(4));

        // now we can summarize by year so we don't inadvertently include differences between multiple years
        Table summary = summer.summarize(delta, mean, count).by(summerDate.year());
        out(summary);

        // taking the mean of the annual means gives us an approximate answer
        // we could also use the count value calculated above to get a weighted average
        out(summary.nCol(1).mean());

        out();

        out("Writing the revised table to a new csv file");
        tornadoes.write().csv("../data/rev_tornadoes_1950-2014.csv");
    }
}