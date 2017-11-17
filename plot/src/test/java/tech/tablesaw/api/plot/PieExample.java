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

package tech.tablesaw.api.plot;


import tech.tablesaw.api.Table;

import static tech.tablesaw.aggregate.AggregateFunctions.mean;
import static tech.tablesaw.aggregate.AggregateFunctions.sum;
import static tech.tablesaw.api.plot.Pie.show;

/**
 * Basic sample pie chart
 */
public class PieExample {

    public static void main(String[] args) throws Exception {
        Table table = Table.read().csv("../data/tornadoes_1950-2014.csv");

        Table t2 = table.countBy(table.categoryColumn("State"));
        show("tornadoes by state", t2.categoryColumn("Category"), t2.numericColumn("Count"));

        show("Sum of fatalities by State", table.summarize("fatalities", sum).by("State"));
        show("Average fatalities by scale", table.summarize("fatalities", mean).by("Scale"));
    }
}
