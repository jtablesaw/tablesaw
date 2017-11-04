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

import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.reducing.NumericReduceFunction;
import tech.tablesaw.reducing.NumericSummaryTable;
import tech.tablesaw.util.BitmapBackedSelection;
import tech.tablesaw.util.Selection;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A group of tables formed by performing splitting operations on an original table
 */
public class ViewGroup implements Iterable<TemporaryView> {

    private static final String SPLIT_STRING = "~~~";
    private static final Splitter SPLITTER = Splitter.on(SPLIT_STRING);


    private final Table sortedOriginal;

    private List<TemporaryView> subTables = new ArrayList<>();

    // the name(s) of the column(s) we're splitting the table on
    private String[] splitColumnNames;

    public ViewGroup(Table original, Column... columns) {
        splitColumnNames = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            splitColumnNames[i] = columns[i].name();
        }
        this.sortedOriginal = original.sortOn(splitColumnNames);
        splitOn(splitColumnNames);
    }

    public static ViewGroup create(Table original, String... columnsNames) {
        List<Column> columns = original.columns(columnsNames);
        return new ViewGroup(original, columns.toArray(new Column[columns.size()]));
    }

    /**
     * Splits the sortedOriginal table into sub-tables, grouping on the columns whose names are given in
     * splitColumnNames
     */
    private void splitOn(String... columnNames) {

        List<Column> columns = sortedOriginal.columns(columnNames);
        int byteSize = getByteSize(columns);

        byte[] currentKey = null;
        String currentStringKey = null;
        TemporaryView view;

        Selection selection = new BitmapBackedSelection();

        for (int row = 0; row < sortedOriginal.rowCount(); row++) {

            ByteBuffer byteBuffer = ByteBuffer.allocate(byteSize);
            String newStringKey = "";

            for (int col = 0; col < columnNames.length; col++) {
                if (col > 0) {
                    newStringKey = newStringKey + SPLIT_STRING;
                }

                Column c = sortedOriginal.column(columnNames[col]);
                String groupKey = sortedOriginal.get(row, sortedOriginal.columnIndex(c));
                newStringKey = newStringKey + groupKey;
                byteBuffer.put(c.asBytes(row));
            }
            byte[] newKey = byteBuffer.array();
            if (row == 0) {
                currentKey = newKey;
                currentStringKey = newStringKey;
            }
            if (!Arrays.equals(newKey, currentKey)) {
                currentKey = newKey;
                view = new TemporaryView(sortedOriginal, selection);
                view.setName(currentStringKey);
                currentStringKey = newStringKey;
                addViewToSubTables(view);
                selection = new BitmapBackedSelection();
                selection.add(row);
            } else {
                selection.add(row);
            }
        }
        if (!selection.isEmpty()) {
            view = new TemporaryView(sortedOriginal, selection);
            view.setName(currentStringKey);
            addViewToSubTables(view);
        }
    }

    private int getByteSize(List<Column> columns) {
        int byteSize = 0;
        {
            for (Column c : columns) {
                byteSize += c.byteSize();
            }
        }
        return byteSize;
    }

    private void addViewToSubTables(TemporaryView view) {
        subTables.add(view);
    }

    public List<TemporaryView> getSubTables() {
        return subTables;
    }

    public TemporaryView get(int i) {
        return subTables.get(i);
    }

    @VisibleForTesting
    public Table getSortedOriginal() {
        return sortedOriginal;
    }

    public int size() {
        return subTables.size();
    }


    /**
     * For a subtable that is grouped by the values in more than one column, split the grouping column into separate
     * cols and return the revised view
     */
    private NumericSummaryTable splitGroupingColumn(NumericSummaryTable groupTable) {

        List<Column> newColumns = new ArrayList<>();

        List<Column> columns = sortedOriginal.columns(splitColumnNames);
        for (Column column : columns) {
            Column newColumn = column.emptyCopy();
            newColumns.add(newColumn);
        }
        // iterate through the rows in the table and split each of the grouping columns into multiple columns
        for (int row = 0; row < groupTable.rowCount(); row++) {
            List<String> strings = SPLITTER.splitToList(groupTable.categoryColumn("Group").get(row));
            for (int col = 0; col < newColumns.size(); col++) {
                newColumns.get(col).appendCell(strings.get(col));
            }
        }
        for (int col = 0; col < newColumns.size(); col++) {
            Column c = newColumns.get(col);
            groupTable.addColumn(col, c);
        }
        groupTable.removeColumns("Group");
        return groupTable;
    }


    public NumericSummaryTable reduce(String numericColumnName, NumericReduceFunction function) {
        Preconditions.checkArgument(!subTables.isEmpty());
        NumericSummaryTable groupTable = NumericSummaryTable.create(sortedOriginal.name() + " summary");
        CategoryColumn groupColumn = new CategoryColumn("Group", subTables.size());
        FloatColumn resultColumn = new FloatColumn(reduceColumnName(numericColumnName, function.functionName()),
                subTables.size());
        groupTable.addColumn(groupColumn);
        groupTable.addColumn(resultColumn);

        for (TemporaryView subTable : subTables) {
            double result = subTable.reduce(numericColumnName, function);
            groupColumn.add(subTable.name());
            resultColumn.append((float) result);
        }
        return splitGroupingColumn(groupTable);
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<TemporaryView> iterator() {
        return subTables.iterator();
    }

    private String reduceColumnName(String columnName, String functionName) {
        return String.format("%s [%s]", functionName, columnName);
    }
}
