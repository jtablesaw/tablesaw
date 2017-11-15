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

import static tech.tablesaw.api.ColumnType.*;
import static tech.tablesaw.api.QueryHelper.column;

import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.plot.Scatter;
import tech.tablesaw.io.csv.CsvReadOptions;

/**
 * Usage example using a Tornado dataset
 */
public class TornadoExample {

    // column types for the tornado table
    private static final ColumnType[] COLUMN_TYPES_OLD = {
            INTEGER,     // number by year
            INTEGER,     // year
            INTEGER,     // month
            INTEGER,     // day
            LOCAL_DATE,  // date
            LOCAL_TIME,  // time
            CATEGORY,    // tz
            CATEGORY,    // st
            CATEGORY,    // state fips
            INTEGER,     // state torn number
            INTEGER,     // scale
            INTEGER,     // injuries
            INTEGER,     // fatalities
            FLOAT,       // loss
            FLOAT,   // crop loss
            FLOAT,   // St. Lat
            FLOAT,   // St. Lon
            FLOAT,   // End Lat
            FLOAT,   // End Lon
            FLOAT,   // length
            FLOAT,   // width
            FLOAT,   // NS
            FLOAT,   // SN
            FLOAT,   // SG
            CATEGORY,  // Count FIPS 1-4
            CATEGORY,
            CATEGORY,
            CATEGORY};

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
        out(tornadoes.structure().selectWhere(column("Column Type").isEqualTo("INTEGER")));

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

        tornadoes = tornadoes.selectWhere(column("Start Lat").isGreaterThan(20f));
        Scatter.show("US Tornadoes 1950-2014", tornadoes.nCol("Start Lon"), tornadoes.nCol("Start Lat"));

        out();
        out("Extact month from the date and make it a separate column");
        CategoryColumn month = tornadoes.dateColumn("Date").month();
        out(month.summary());

        out("Add the month column to the table");
        tornadoes.addColumn(2, month);
        out(tornadoes.columnNames());

        out();
        out("Filtering: Tornadoes where there were fatalities");
        Table fatal = tornadoes.selectWhere(column("Fatalities").isGreaterThan(0));
        out(fatal.shape());

        out();
        out(fatal.first(5));

        out();
        out("Total fatalities: " + fatal.shortColumn("Fatalities").sum());

        out();
        out("Sorting on Fatalities in descending order");
        fatal = fatal.sortDescendingOn("Fatalities");
        out(fatal.first(5));

        out("");
        out("Calculating basic descriptive statistics on Fatalities");
        out(fatal.shortColumn("Fatalities").summary());


        //TODO(lwhite): Provide a param for title of the new table (or auto-generate a better one).
        Table injuriesByScale = tornadoes.median("Injuries").by("Scale");
        Table fob = tornadoes.min("Injuries").by("Scale", "State");
        out(fob);
        injuriesByScale.setName("Median injuries by Tornado Scale");
        out(injuriesByScale);


        //TODO(lwhite): Provide a param for title of the new table (or auto-generate a better one).
        Table injuriesByScaleState = tornadoes.median("Injuries").by("Scale", "State");
        injuriesByScaleState.setName("Median injuries by Tornado Scale and State");
        out(injuriesByScaleState);


        out();
        out("Writing the revised table to a new csv file");
        tornadoes.write().csv("../data/rev_tornadoes_1950-2014.csv");

        out();
        out("Saving to Tablesaw format");
        String dbName = tornadoes.save("/tmp/tablesaw/testdata");

        // NOTE: dbName is equal to "/tmp/tablesaw/testdata/tornadoes.saw"

        out();
        out("Reading from Tablesaw format");
        tornadoes = Table.readTable(dbName);
    }

    private static void out(Object obj) {
        System.out.println(String.valueOf(obj));
    }

    private static void out() {
        System.out.println("");
    }
}