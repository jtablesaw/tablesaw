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

import com.google.common.collect.ArrayListMultimap;
import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.ColumnType;
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
    private final AggregateFunction[] reductions;

    /**
     * Returns an object capable of summarizing the given column in the given sourceTable,
     * by applying the given functions
     */
    public Summarizer(Table sourceTable, Column column, AggregateFunction... functions) {
        this.original = sourceTable;
        summarizedColumns.add(column.name());
        this.reductions = functions;
    }

    /**
     * Returns an object capable of summarizing the given columns in the given sourceTable,
     * by applying the given functions
     */
    public Summarizer(Table sourceTable, Column column1, Column column2, AggregateFunction... functions) {
        this.original = sourceTable;
        summarizedColumns.add(column1.name());
        summarizedColumns.add(column2.name());
        this.reductions = functions;
    }

    /**
     * Returns an object capable of summarizing the given columns in the given sourceTable,
     * by applying the given functions
     */
    public Summarizer(Table sourceTable,
                      Column column1,
                      Column column2,
                      Column column3,
                      Column column4,
                      AggregateFunction... functions) {
        this.original = sourceTable;
        summarizedColumns.add(column1.name());
        summarizedColumns.add(column2.name());
        summarizedColumns.add(column3.name());
        summarizedColumns.add(column4.name());
        this.reductions = functions;
    }

    /**
     * Returns an object capable of summarizing the given column2 in the given sourceTable,
     * by applying the given functions
     */
    public Summarizer(Table sourceTable, Column column1, Column column2, Column column3, AggregateFunction... functions) {
        this.original = sourceTable;
        summarizedColumns.add(column1.name());
        summarizedColumns.add(column2.name());
        summarizedColumns.add(column3.name());
        this.reductions = functions;
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
            for (AggregateFunction function : reductions) {
                NumberColumn column = original.numberColumn(columnName);
                double result = function.summarize(column);
                Column newColumn = DoubleColumn.create(TableSliceGroup.aggregateColumnName(columnName, function.functionName()));
                ((DoubleColumn) newColumn).append(result);
                table.addColumn(newColumn);
            }
        }
        return table;
    }

    /**
     * Associates the columns to be summarized with the functions that match their type. All valid combinations are used
     * @param group A table slice group
     * @return      A table containing a row of summarized data for each group in the table slice group
     */
    private Table summarize(TableSliceGroup group) {
        List<Table> results = new ArrayList<>();

        ArrayListMultimap<String, AggregateFunction> reductionMultimap = ArrayListMultimap.create();
        for (String name: summarizedColumns) {
            Column column = original.column(name);
            ColumnType type = column.type();
            for (AggregateFunction reduction : reductions) {
                if (reduction.isCompatibleWith(type)) {
                    reductionMultimap.put(name, reduction);
                }
            }
        }

        for (String name : reductionMultimap.keys()) {
            List<AggregateFunction> reductions = reductionMultimap.get(name);
            results.add(group.aggregate(name, reductions.toArray(new AggregateFunction[reductions.size()])));
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
