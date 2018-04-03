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

import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.table.SelectionViewGroup;
import tech.tablesaw.table.StandardViewGroup;
import tech.tablesaw.table.ViewGroup;

import java.util.HashMap;
import java.util.Map;

public class SummaryFunction {

    private final Table original;
    private final String summarizedColumnName;
    private final AggregateFunction[] function;

    public SummaryFunction(Table original, String summarizedColumnName, AggregateFunction... function) {
        this.original = original;
        this.summarizedColumnName = summarizedColumnName;
        this.function = function;
    }

    public SummaryFunction(Table original, NumberColumn numberColumn, AggregateFunction... function) {
        this.original = original;
        this.summarizedColumnName = numberColumn.name();
        this.function = function;
    }

    private String summarizedColumnName() {
        return summarizedColumnName;
    }

    private Table original() {
        return original;
    }

    public Table by(String... columnNames) {
        ViewGroup group = StandardViewGroup.create(original(), columnNames);
        return group.aggregate(summarizedColumnName(), function);
    }

    public Table by(CategoricalColumn... columns) {
        ViewGroup group = StandardViewGroup.create(original(), columns);
        return group.aggregate(summarizedColumnName(), function);
    }

    public Table by(String groupNameTemplate, int step) {
        ViewGroup group = SelectionViewGroup.create(original(), groupNameTemplate, step);
        return group.aggregate(summarizedColumnName(), function);
    }

    /**
     * Returns the result of applying to the functions to all the values in the appropriate column
     */
    public Map<AggregateFunction, Double> getAll() {
        Map<AggregateFunction, Double> results = new HashMap<>();
        for (AggregateFunction fun : function) {
            results.put(fun, fun.agg(original.numberColumn(summarizedColumnName).asDoubleArray()));
        }
        return results;
    }
}
