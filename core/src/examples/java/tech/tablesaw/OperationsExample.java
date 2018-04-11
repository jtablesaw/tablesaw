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

import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.DateTimeColumn;

import static tech.tablesaw.aggregate.AggregateFunctions.median;
import static tech.tablesaw.api.QueryHelper.*;

/**
 * Usage example using a process data set
 */
public class OperationsExample extends AbstractExample {

    public static void main(String[] args) throws Exception {

        Table ops = Table.create("../data/operations.csv");

        out(ops.structure());
        out(ops);

        DateTimeColumn start = ops.dateColumn("Date").atTime(ops.timeColumn("Start"));
        DateTimeColumn end = ops.dateColumn("Date").atTime(ops.timeColumn("End"));

        for (int row : ops) {
            if (ops.timeColumn("End").get(row).isBefore(ops.timeColumn("Start").get(row))) {
                end.get(row).plusDays(1);
            }
        }

        // Calc duration
        NumberColumn duration = start.differenceInSeconds(end);
        ops.addColumn(duration);
        duration.setName("Duration");

        out(ops);

        Table q2_429_assembly = ops.where(
                and
                        (dateColumn("date").isInQ2(),
                                (stringColumn("SKU").startsWith("429")),
                                (stringColumn("Operation").isEqualTo("Assembly"))));

        Table durationByFacilityAndShift = q2_429_assembly.summarize("Duration", median)
                .by("Facility", "Shift");

        out(durationByFacilityAndShift);

        durationByFacilityAndShift.write().csv("/tmp/durationByFacilityAndShift.csv");
    }
}
