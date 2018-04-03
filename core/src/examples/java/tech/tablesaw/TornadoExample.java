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
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.selection.Selection;

import static tech.tablesaw.aggregate.AggregateFunctions.*;
import static tech.tablesaw.api.ColumnType.*;
import static tech.tablesaw.api.QueryHelper.*;

/**
 * Usage example using a Tornado dataset
 */
public class TornadoExample extends AbstractExample {

    // column types for the tornado table
    private static final ColumnType[] COLUMN_TYPES_OLD = {
            NUMBER,     // number by year
            NUMBER,     // year
            NUMBER,     // month
            NUMBER,     // day
            LOCAL_DATE,  // date
            LOCAL_TIME,  // time
            STRING,    // tz
            STRING,    // st
            STRING,    // state fips
            NUMBER,     // state torn number
            NUMBER,     // scale
            NUMBER,     // injuries
            NUMBER,     // fatalities
            NUMBER,       // loss
            NUMBER,   // crop loss
            NUMBER,   // St. Lat
            NUMBER,   // St. Lon
            NUMBER,   // End Lat
            NUMBER,   // End Lon
            NUMBER,   // length
            NUMBER,   // width
            NUMBER,   // NS
            NUMBER,   // SN
            NUMBER,   // SG
            STRING,  // Count FIPS 1-4
            STRING,
            STRING,
            STRING};

    public static void main(String[] args) throws Exception {

        Table tornadoes = Table.read().csv(CsvReadOptions
            .builder("../data/1950-2014_torn.csv")
            .columnTypes(COLUMN_TYPES_OLD));
        assert (tornadoes != null);

        out(tornadoes.structure());
        out();

        tornadoes.removeColumns("Number", "Year", "Month", "Day", "Zone", "State FIPS", "Loss", "Crop Loss",
            "End Lat", "End Lon", "NS", "SN", "SG", "FIPS 1", "FIPS 2", "FIPS 3", "FIPS 4");

        tornadoes.write().csv("../data/tornadoes_1950-2014.csv");

        //tornadoes = Table.createFromCsv(COLUMN_TYPES, "../data/tornadoes_1950-2014.csv");
        tornadoes = Table.read().csv("../data/tornadoes_1950-2014.csv");
        assert (tornadoes != null);

        out(tornadoes.structure());
        out(tornadoes.structure().selectWhere(stringColumn("Column Type").isEqualTo("NUMBER")));

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

        tornadoes = tornadoes.selectWhere(numberColumn("Start Lat").isGreaterThan(20f));

        out();
        out("Extact month from the date and make it a separate column");
        StringColumn month = tornadoes.dateColumn("Date").month();
        out(month.summary());

        out("Add the month column to the table");
        tornadoes.addColumn(2, month);
        out(tornadoes.columnNames());

        out();
        out("Filtering: Tornadoes where there were fatalities");
        Table fatal = tornadoes.selectWhere(numberColumn("Fatalities").isGreaterThan(0));
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

        // Average days between tornados in the summer

        // summer approximated as june, july, august
        Selection selection = tornadoes.dateColumn("Date").month().isIn("JUNE", "JULY", "AUGUST");
        Table summer = tornadoes.selectWhere(selection);
        summer = summer.sortAscendingOn("Date", "Time");
        summer.addColumn(summer.dateColumn("Date").lag(1));

        DateColumn date = summer.dateColumn("Date");
        DateColumn laggedDate = summer.dateColumn("Date lag(1)");

        NumberColumn delta = laggedDate.daysUntil(date);
        summer.addColumn(delta);

        Table summary = summer.summarize(delta.name(), mean).by(date.year());
        out(summary);

        out(summer.first(4));

        out();
        out("Writing the revised table to a new csv file");
        tornadoes.write().csv("../data/rev_tornadoes_1950-2014.csv");
    }
}