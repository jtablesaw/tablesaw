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
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.table.SelectionTableSliceGroup;
import tech.tablesaw.table.StandardTableSliceGroup;
import tech.tablesaw.table.TableSliceGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Summarizes the data in a table, by applying functions to a subset of its columns.
 *
 * How to use:
 *
 * 1. Create an instance providing a source table, the column or columns to summarize, and a function or functions to apply
 * 2. Applying the functions to the designated columns, possibly creating subgroup summaries using one of the by() methods
 */
public class Summarizer {

    private final Table original;
    private final List<String> summarizedColumns = new ArrayList<>();
    private final AggregateFunction[] functions;

    /**
     * Returns an object capable of summarizing the given numericColumn in the given sourceTable,
     * by applying the given functions
     */
    public Summarizer(Table sourceTable, NumberColumn numericColumn, AggregateFunction... functions) {
        this.original = sourceTable;
        summarizedColumns.add(numericColumn.name());
        this.functions = functions;
    }

    /**
     * Returns an object capable of summarizing the given numericColumn in the given sourceTable,
     * by applying the given functions
     */
    public Summarizer(Table sourceTable, NumberColumn numericColumn1, NumberColumn numericColumn2, AggregateFunction... functions) {
        this.original = sourceTable;
        summarizedColumns.add(numericColumn1.name());
        summarizedColumns.add(numericColumn2.name());
        this.functions = functions;
    }

    /**
     * Returns an object capable of summarizing the given numericColumn in the given sourceTable,
     * by applying the given functions
     */
    public Summarizer(Table sourceTable,
                      NumberColumn numericColumn1,
                      NumberColumn numericColumn2,
                      NumberColumn numericColumn3,
                      NumberColumn numericColumn4,
                      AggregateFunction... functions) {
        this.original = sourceTable;
        summarizedColumns.add(numericColumn1.name());
        summarizedColumns.add(numericColumn2.name());
        summarizedColumns.add(numericColumn3.name());
        summarizedColumns.add(numericColumn4.name());
        this.functions = functions;
    }

    /**
     * Returns an object capable of summarizing the given numericColumn in the given sourceTable,
     * by applying the given functions
     */
    public Summarizer(Table sourceTable,
                      NumberColumn numericColumn1,
                      NumberColumn numericColumn2,
                      NumberColumn numericColumn3,
                      AggregateFunction... functions) {
        this.original = sourceTable;
        summarizedColumns.add(numericColumn1.name());
        summarizedColumns.add(numericColumn2.name());
        summarizedColumns.add(numericColumn3.name());
        this.functions = functions;
    }

    public Table by(String... columnNames) {
        TableSliceGroup group = StandardTableSliceGroup.create(original, columnNames);
        return summarize(group);
    }

    public Table by(CategoricalColumn... columns) {
        TableSliceGroup group = StandardTableSliceGroup.create(original, columns);
        return summarize(group);
    }

    public Table by(String groupNameTemplate, int step) {
        TableSliceGroup group = SelectionTableSliceGroup.create(original, groupNameTemplate, step);
        return summarize(group);
    }

    /**
     * Returns the result of applying to the functions to all the values in the appropriate column
     */
    public Table apply() {
        Table table = TableSliceGroup.summaryTableName(original);
        for (String columnName : summarizedColumns) {
            for (AggregateFunction function : functions) {
                NumberColumn column = original.numberColumn(columnName);
                double result = function.summarize(column);
                Column newColumn = DoubleColumn.create(TableSliceGroup.aggregateColumnName(columnName, function.functionName()));
                ((DoubleColumn) newColumn).append(result);
                table.addColumn(newColumn);
            }
        }
        return table;
    }

    private Table summarize(TableSliceGroup group) {
        List<Table> results = new ArrayList<>();
        for (String name : summarizedColumns) {
            results.add(group.aggregate(name, functions));
        }
        return combineTables(results);
    }

    private Table combineTables(List<Table> tables) {
        if (tables.size() == 1) {
            return tables.get(0);
        }

        Table result = null;
        for (Table table : tables) {
            if (result == null) {
                result = table;
            } else {
                for (Column column : table.columns()) {
                    if (!result.columnNames().contains(column.name())) {
                        result.addColumn(column);
                    }
                }
            }
        }
        return result;
    }
}
