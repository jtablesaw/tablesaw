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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A group of tables formed by performing splitting operations on an original table
 */
public class ViewGroup implements Iterable<TableSlice> {

    static final String SPLIT_STRING = "~~~";
    private static final Splitter SPLITTER = Splitter.on(SPLIT_STRING);

    private final List<TableSlice> subTables = new ArrayList<>();

    private final String[] splitColumnNames;

    private Table sourceTable;

    protected ViewGroup(Table original) {
        sourceTable = original;
        splitColumnNames = new String[0];
    }

    protected ViewGroup(Table original, String[] splitColumnNames) {
        sourceTable = original;
        this.splitColumnNames = splitColumnNames;
    }

    String[] getSplitColumnNames() {
        return splitColumnNames;
    }

    int getByteSize(List<Column> columns) {
        int byteSize = 0;
        for (Column c : columns) {
            byteSize += c.byteSize();
        }
        return byteSize;
    }

    void addViewToSubTables(TableSlice view) {
        subTables.add(view);
    }

    public List<TableSlice> getSubTables() {
        return subTables;
    }

    public TableSlice get(int i) {
        return subTables.get(i);
    }

    @VisibleForTesting
    public Table getSourceTable() {
        return sourceTable;
    }

    public int size() {
        return subTables.size();
    }

    /**
     * For a subtable that is grouped by the values in more than one column, split the grouping column into separate
     * cols and return the revised view
     */
    private Table splitGroupingColumn(Table groupTable) {

        if (splitColumnNames.length > 0) {
            List<Column> newColumns = new ArrayList<>();
            List<Column> columns = sourceTable.columns(splitColumnNames);
            for (Column column : columns) {
                Column newColumn = column.emptyCopy();
                newColumns.add(newColumn);
            }
            // iterate through the rows in the table and split each of the grouping columns into multiple columns
            for (int row = 0; row < groupTable.rowCount(); row++) {
                List<String> strings = SPLITTER.splitToList(groupTable.stringColumn("Group").get(row));
                for (int col = 0; col < newColumns.size(); col++) {
                    newColumns.get(col).appendCell(strings.get(col));
                }
            }
            for (int col = 0; col < newColumns.size(); col++) {
                Column c = newColumns.get(col);
                groupTable.addColumn(col, c);
            }
            groupTable.removeColumns("Group");
        }
        return groupTable;
    }

    /**
     * Applies the given aggregation to the given column.
     * The apply and combine steps of a split-apply-combine.
     */
    public Table aggregate(String colName1, AggregateFunction... func1) {
        ArrayListMultimap<String, AggregateFunction> map = ArrayListMultimap.create();
        map.putAll(colName1, Lists.newArrayList(func1));
        return aggregate(map);
    }

    /**
     * Applies the given aggregations to the given columns.
     * The apply and combine steps of a split-apply-combine.
     *
     * @param functions map from column name to aggregation to apply on that function
     */
    public Table aggregate(ArrayListMultimap<String, AggregateFunction> functions) {
        Preconditions.checkArgument(!getSubTables().isEmpty());
        Table groupTable = Table.create(getSourceTable().name() + " summary");
        StringColumn groupColumn = StringColumn.create("Group", size());
        groupTable.addColumn(groupColumn);
        for (Map.Entry<String, Collection<AggregateFunction>> entry : functions.asMap().entrySet()) {
            String columnName = entry.getKey();
            int functionCount = 0;
            for (AggregateFunction function : entry.getValue()) {
                String colName = aggregateColumnName(columnName, function.functionName());
                NumberColumn resultColumn = NumberColumn.create(colName, getSubTables().size());
                for (TableSlice subTable : getSubTables()) {
                    double result = subTable.reduce(columnName, function);
                    if (functionCount == 0) {
                        groupColumn.append(subTable.name());
                    }
                    resultColumn.append(result);
                }
                groupTable.addColumn(resultColumn);
                functionCount++;
            }
        }
        return splitGroupingColumn(groupTable);
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<TableSlice> iterator() {
        return subTables.iterator();
    }

    private String aggregateColumnName(String columnName, String functionName) {
        return String.format("%s [%s]", functionName, columnName);
    }

    public List<Table> asTableList() {
        List<Table> tableList = new ArrayList<>();
        for (TableSlice view : this) {
            tableList.add(view.asTable());
        }
        return tableList;
    }

    void setSourceTable(Table sourceTable) {
        this.sourceTable = sourceTable;
    }
}
