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

import static tech.tablesaw.aggregate.AggregateFunctions.median;

import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

/** Usage example using a process data set */
public class OperationsExample extends AbstractExample {

  public static void main(String[] args) throws Exception {

    Table ops = Table.create("../data/operations.csv");

    out(ops.structure());
    out(ops);

    DateTimeColumn start = ops.dateColumn("Date").atTime(ops.timeColumn("Start"));
    DateTimeColumn end = ops.dateColumn("Date").atTime(ops.timeColumn("End"));

    for (Row row : ops) {
      if (row.getTime("End").isBefore(row.getTime("Start"))) {
        end.get(row.getRowNumber()).plusDays(1);
      }
    }

    for (Row row : ops) {
      if (row.getTime("End").isBefore(row.getTime("Start"))) {
        end.get(row.getRowNumber()).plusDays(1);
      }
    }

    // Calc duration
    LongColumn duration = start.differenceInSeconds(end);
    ops.addColumns(duration);
    duration.setName("Duration");

    out(ops);

    Table q2_429_assembly =
        ops.where(
            (ops.dateColumn("date")
                .isInQ2()
                .and((ops.stringColumn("SKU").startsWith("429")))
                .and((ops.stringColumn("Operation").isEqualTo("Assembly")))));

    Table durationByFacilityAndShift =
        q2_429_assembly.summarize("Duration", median).by("Facility", "Shift");

    out(durationByFacilityAndShift);

    durationByFacilityAndShift.write().csv("/tmp/durationByFacilityAndShift.csv");
  }
}
