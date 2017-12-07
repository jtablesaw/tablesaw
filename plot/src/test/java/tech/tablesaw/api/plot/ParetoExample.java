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

import static tech.tablesaw.aggregate.AggregateFunctions.sum;
import static tech.tablesaw.api.QueryHelper.column;

import tech.tablesaw.api.Table;
import tech.tablesaw.api.plot.Pareto;

/**
 *
 */
public class ParetoExample {

    public static void main(String[] args) throws Exception {
        Table table = Table.read().csv("../data/tornadoes_1950-2014.csv");
        table = table.selectWhere(column("Fatalities").isGreaterThan(3));
        Pareto.show("Tornado Fatalities by State", table.summarize("fatalities", sum).by("State"));
    }
}