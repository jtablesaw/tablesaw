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

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.filtering.Filter;
import tech.tablesaw.util.Selection;

/**
 * A table projection, i.e. the subset of columns in a table that should be returned in a query
 */
public class Projection {

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
}
