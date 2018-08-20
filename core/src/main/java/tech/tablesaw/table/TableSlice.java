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

import it.unimi.dsi.fastutil.ints.IntIterable;
import it.unimi.dsi.fastutil.ints.IntIterator;
import tech.tablesaw.aggregate.NumericAggregateFunction;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.util.ArrayList;
import java.util.List;

/**
 * A TableSlice is a facade around a Relation that acts as a filter.
 * Requests for data are forwarded to the underlying table.
 * <p>
 * A TableSlice is only good until the structure of the underlying table changes.
 */
public class TableSlice extends Relation implements IntIterable {

    private final Selection selection;
    private String name;
    private final Table table;

    /**
     * Returns a new View constructed from the given table, containing only the rows represented by the bitmap
     */
    public TableSlice(Table table, Selection rowSelection) {
        this.name = table.name();
        this.selection = rowSelection;
        this.table = table;
    }

    @Override
    public Column<?> column(int columnIndex) {
        return table.column(columnIndex).where(selection);
    }

    @Override
    public Column<?> column(String columnName) {
        return table.column(columnName).where(selection);
    }

    /**
     * Returns the entire column of the source table, unfiltered
     */
    private Column<?> entireColumn(int columnIndex) {
        return table.column(columnIndex);
    }

    @Override
    public int columnCount() {
        return table.columnCount();
    }

    @Override
    public int rowCount() {
        return selection.size();
    }

    @Override
    public List<Column<?>> columns() {
        List<Column<?>> columns = new ArrayList<>();
        for (int i = 0; i < columnCount(); i++) {
            columns.add(entireColumn(i));
        }
        return columns;
    }

    @Override
    public int columnIndex(Column<?> column) {
        return table.columnIndex(column);
    }

    @Override
    public Object get(int r, int c) {
        return table.get(selection.get(r), c);
    }

    @Override
    public String name() {
        return name;
    }

    /**
     * Clears all rows from this View, leaving the structure in place
     */
    @Override
    public void clear() {
        selection.clear();
    }

    @Override
    public List<String> columnNames() {
        return table.columnNames();
    }

    @Override
    public TableSlice addColumns(Column<?>... column) {
        throw new UnsupportedOperationException("Class TableSlice does not support the addColumns operation");
    }

    @Override
    public TableSlice removeColumns(Column<?>... columns) {
        throw new UnsupportedOperationException("Class TableSlice does not support the removeColumns operation");
    }

    @Override
    public Table first(int nRows) {
        Selection newMap = new BitmapBackedSelection();
        int count = 0;
        IntIterator it = intIterator();
        while (it.hasNext() && count < nRows) {
            int row = it.nextInt();
            newMap.add(row);
            count++;
        }
        return table.where(newMap);
    }

    @Override
    public TableSlice setName(String name) {
        this.name = name;
        return this;
    }

    public Table asTable() {
        Table table = Table.create(this.name());
        for (Column<?> column : columns()) {
            table.addColumns(column.where(selection));
        }
        return table;
    }

    private IntIterator intIterator() {
        return selection.iterator();
    }

    /**
     * Returns the result of applying the given function to the specified column
     *
     * @param numberColumnName The name of a numeric column in this table
     * @param function         A numeric reduce function
     * @return the function result
     * @throws IllegalArgumentException if numberColumnName doesn't name a numeric column in this table
     */
    public double reduce(String numberColumnName, NumericAggregateFunction function) {
        NumberColumn<?> column = table.numberColumn(numberColumnName);
        return function.summarize(column.where(selection));
    }

    /**
     * Returns a 0 based int iterator for use with, for example, get(). When it returns 0 for the first row,
     * get will transform that to the 0th row in the selection, which may not be the 0th row in the underlying
     * table.
     */
    @Override
    public IntIterator iterator() {

        return new IntIterator() {

            private int i = 0;

            @Override
            public int nextInt() {
                return i++;
            }

            @Override
            public int skip(int k) {
                return i + k;
            }

            @Override
            public boolean hasNext() {
                return i < rowCount();
            }
        };
    }
}