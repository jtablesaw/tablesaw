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

import com.google.common.collect.Lists;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.filtering.Filter;
import tech.tablesaw.selection.Selection;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A table projection, i.e. the subset of columns in a table that should be returned in a query
 */
public class Projection extends Relation {

    private final Table table;
    private final Column[] columns;

    public Projection(Table table, String[] columnNames) {
        this.table = table;
        columns = new Column[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            String name = columnNames[i];
            columns[i] = table.column(name);
        }
    }

    public Table where(Filter filter) {
        Table projectedTable = Table.create(table.name(), columns);
        Table newTable = projectedTable.emptyCopy();
        Selection map = filter.apply(table);
        Rows.copyRowsToTable(map, projectedTable, newTable);
        return newTable;
    }

    @Override
    public Relation addColumn(Column... cols) {
        throw new UnsupportedOperationException("Projections do not support updates.");
    }

    @Override
    public Relation setName(String name) {
        throw new UnsupportedOperationException("Projections do not support updates.");
    }

    @Override
    public Relation removeColumns(Column... columns) {
        throw new UnsupportedOperationException("Projections do not support updates.");
    }

    @Override
    public Table first(int nRows) {
        return table.fullCopy().retainColumns(columns).first(nRows);
    }

    @Override
    public Column column(int columnIndex) {
        return columns[columnIndex];
    }

    @Override
    public int columnCount() {
        return columns.length;
    }

    @Override
    public int rowCount() {
        return table.rowCount();
    }

    @Override
    public List<Column> columns() {
        return Lists.newArrayList(columns);
    }

    @Override
    public int columnIndex(Column col) {
        return columns().indexOf(col);
    }

    @Override
    public String get(int r, int c) {
        return table.get(r, columns[c].name());
    }

    @Override
    public String name() {
        return table.name();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Projections do not support updates.");
    }

    @Override
    public List<String> columnNames() {
        return columns().stream().map(Column::name).collect(Collectors.toList());
    }
}
