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

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.aggregate.CrossTab;
import tech.tablesaw.util.Selection;

import static tech.tablesaw.aggregate.AggregateFunctions.range;
import static tech.tablesaw.api.QueryHelper.*;

/**
 *         Example code for:
 *         Learning Data Science with Java and Tablesaw
 *
 */
public class Example1 {

    public static void main(String[] args) throws Exception {

        // create our table from a flat file:
        Table table1 = Table.read().csv("data/BushApproval.csv");

        // return the name of the table
        out("Table name: " + table1.name());

        // return the table's shape
        out(table1.shape());

        // display the table structure:
        out(table1.structure().print());

        // We can peak at a few rows:
        out("First three rows:");
        out(table1.first(3).print());

        // List the column names
        out("Column names: " + table1.columnNames());

        // Get the approval column.
        ShortColumn approval = table1.shortColumn("approval");

        // Column Operation Examples

        // Operations like count(), and min() produce a single value for a column of data.
        out("Minimum approval rating: " + approval.min());

        // Other operations return a new column.
        // Method dayOfYear() applied to a DateColumn returns a ShortColumn containing the day of the year from 1 to 366

        DateColumn date = table1.dateColumn("date");
        ShortColumn dayOfYear = date.dayOfYear();

        out(dayOfYear.summary().print());

        // Show the first 10 elements of the column
        out(dayOfYear.first(10).print());

        // As a rule, column-returning methods come in two flavors: Some take a scalar value as an input.
        // This adds four days to every element.

        out(date.plusDays(4));

        // Others take a column as an argument. They process the two columns in order, computing a new value for each
        // row and returning it as a column

        // Boolean results
        // Boolean operations like isMonday() return a Selection object. Selections can be used to filter tables
        Selection selection = date.isMonday();

        // To get a boolean column if you want it. You simply pass the Selection and the original column length to a BooleanColumn constructor, along with a name for the new column.
        BooleanColumn monday = new BooleanColumn("monday?", selection, date.size());
        out(monday.summary().print());

        //Querying
        //NOTE: we need a static import of QueryHelper for this section. See the imports above

        Table highRatings = table1.selectWhere(column("approval").isGreaterThan(80));
        highRatings.setName("Approval ratings over 80%");
        out(highRatings.print());

        Table Q3 = table1.selectWhere(date.isInQ3());
        Q3.setName("3rd Quarter ratings");
        out(Q3.print());

        // Sorting
        // Sort on column names in ascending order
        highRatings = highRatings.sortOn("who", "approval");
        out(highRatings.first(10).print());

        // Sort on column names in descending order
        highRatings = highRatings.sortDescendingOn("who", "approval");
        out(highRatings.first(10).print());

        // To sort in mixed order by column names, you can prepend a minus sign “-“
        // to a column name to indicate a descending sort on that column
        highRatings = highRatings.sortOn("who", "-approval");
        out(highRatings.first(10).print());


        // Summarizing
        Table summary = table1.summarize("approval", range).by("who");
        out(summary.print());

        CategoryColumn month = date.month();
        table1.addColumn(month);

        CategoryColumn who = table1.categoryColumn("who");

        Table xtab = CrossTab.xTabCount(table1, month, who);
        out(xtab.print());

        out(CrossTab.tablePercents(xtab).print());
    }

    private static void out(Object str) {
        System.out.println(String.valueOf(str));
        System.out.println("");
    }
}
