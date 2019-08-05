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

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

/** Some example code using the API */
public class BusStopExample extends AbstractExample {

  public static void main(String[] args) throws Exception {

    out("");
    out("Some Examples: ");

    // Read the CSV file
    ColumnType[] types = {
      ColumnType.DOUBLE, ColumnType.STRING, ColumnType.STRING, ColumnType.DOUBLE, ColumnType.DOUBLE
    };
    Table table =
        Table.read().csv(CsvReadOptions.builder("../data/bus_stop_test.csv").columnTypes(types));

    // Look at the column names
    out(table.columnNames());

    // Peak at the data
    out(table.first(5));

    // Remove the description column
    table.removeColumns("stop_desc");

    // Check the column names to see that it's gone
    out(table.columnNames());

    // Take a look at some data
    out("In 'examples. Printing first(5)");
    out(table.first(5));

    // Lets take a look at the latitude and longitude columns
    // out(table.realColumn("stop_lat").rowSummary().out());
    out(table.numberColumn("stop_lat").summary());

    // Now lets fill a column based on data in the existing columns

    // Apply the map function and fill the resulting column to the original table

    // Lets filtering out some of the rows. We're only interested in records with IDs between
    // 524-624

    Table filtered = table.where(table.numberColumn("stop_id").isBetweenInclusive(524, 624));
    out(filtered.first(5));

    // Write out the new CSV file
    filtered.write().csv("../data/filtered_bus_stops.csv");
  }
}
