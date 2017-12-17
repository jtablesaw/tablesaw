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

package tech.tablesaw.table;

import com.google.common.base.Preconditions;

import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A group of tables formed by performing splitting operations on an original table
 */
public class TableGroup implements Iterable<SubTable> {

    private static final String SPLIT_STRING = "~~~";
    private final Table original;

    private final List<SubTable> subTables;

    // the name(s) of the column(s) we're splitting the table on
    private String[] splitColumnNames;

    public TableGroup(Table original, String... splitColumnNames) {
        this.original = original.sortOn(splitColumnNames);
        this.subTables = splitOn(splitColumnNames);
        Preconditions.checkState(!subTables.isEmpty());
        this.splitColumnNames = splitColumnNames;
    }

    public TableGroup(Table original, Column... columns) {
        splitColumnNames = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            splitColumnNames[i] = columns[i].name();
        }
        this.original = original.sortOn(splitColumnNames);
        this.subTables = splitOn(splitColumnNames);
        Preconditions.checkState(!subTables.isEmpty());
    }

    /**
     * Splits the original table into sub-tables, grouping on the columns whose names are given in splitColumnNames
     */
    private List<SubTable> splitOn(String... columnNames) {

        int columnCount = columnNames.length;
        List<SubTable> tables = new ArrayList<>();

        int[] columnIndices = new int[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnIndices[i] = original.columnIndex(columnNames[i]);
        }

        Table empty = original.emptyCopy();

        SubTable newView = new SubTable(empty);
        String lastKey = "";
        newView.setName(lastKey);

        for (int row = 0; row < original.rowCount(); row++) {

            String newKey = "";
            List<String> values = new ArrayList<>();

            for (int col = 0; col < columnCount; col++) {
                if (col > 0)
                    newKey = newKey + SPLIT_STRING;

                String groupKey = original.get(row, columnIndices[col]);
                newKey = newKey + groupKey;
                values.add(groupKey);
            }

            if (!newKey.equals(lastKey)) {
                if (!newView.isEmpty()) {
                    tables.add(newView);
                }

                newView = new SubTable(empty);
                newView.setName(newKey);
                newView.setValues(values);
                lastKey = newKey;
            }
            newView.addRow(row, original);
        }

        if (!tables.contains(newView) && !newView.isEmpty()) {
            if (columnCount == 1) {
                tables.add(newView);
            } else {
                tables.add(newView);
            }
        }
        return tables;
    }

    public List<SubTable> getSubTables() {
        return subTables;
    }

    public int size() {
        return subTables.size();
    }

    public Table reduce(String numericColumnName, AggregateFunction function) {
        Preconditions.checkArgument(!subTables.isEmpty());
        Table t = Table.create(original.name() + " summary");
        CategoryColumn groupColumn = new CategoryColumn("Group", subTables.size());
        FloatColumn resultColumn = new FloatColumn(function.functionName(), subTables.size());
        t.addColumn(groupColumn);
        t.addColumn(resultColumn);

        for (SubTable subTable : subTables) {
            double result = subTable.agg(numericColumnName, function);
            groupColumn.append(subTable.name().replace(SPLIT_STRING, " * "));
            resultColumn.append((float) result);
        }
        return t;
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<SubTable> iterator() {
        return subTables.iterator();
    }
}
