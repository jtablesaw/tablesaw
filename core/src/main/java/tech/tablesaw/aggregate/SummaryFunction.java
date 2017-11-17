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

package tech.tablesaw.aggregate;

import tech.tablesaw.api.Table;
import tech.tablesaw.table.ViewGroup;

public class SummaryFunction {

    private final Table original;
    private final String summarizedColumnName;
    private final AggregateFunction function;

    public SummaryFunction(Table original, String summarizedColumnName, AggregateFunction function) {
        this.original = original;
        this.summarizedColumnName = summarizedColumnName;
        this.function = function;
    }

    public String summarizedColumnName() {
        return summarizedColumnName;
    }

    Table original() {
        return original;
    }

    public NumericSummaryTable by(String... columnNames) {
        ViewGroup group = ViewGroup.create(original(), columnNames);
        return group.agg(summarizedColumnName(), function);
    }

    /**
     * Returns the result of applying to the function to all the values in the appropriate column
     */
    public double get() {
        return original.agg(summarizedColumnName, function);
    }

}
