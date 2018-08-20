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

import javax.annotation.concurrent.Immutable;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/**
 * A static utility class for row operations
 */
@Immutable
public final class Rows {

    // Don't instantiate
    private Rows() {}

    /**
     * N.B. this method does not preserve ordering. Use the version taking int[] if row ordering matters 
     */
    public static void copyRowsToTable(Selection rows, Table oldTable, Table newTable) {
        copyRowsToTable(rows.toArray(), oldTable, newTable);
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    public static void copyRowsToTable(int[] rows, Table oldTable, Table newTable) {
        for (int columnIndex = 0; columnIndex < oldTable.columnCount(); columnIndex++) {
            Column oldColumn = oldTable.column(columnIndex);
            newTable.column(columnIndex).append(oldColumn.subset(rows));
        }
    }

    public static void appendRowToTable(int row, Table oldTable, Table newTable) {
        copyRowsToTable(new int[] {row}, oldTable, newTable);
    }

    public static boolean compareRows(int rowInOriginal, Table original, Table tempTable) {
        int columnCount = original.columnCount();
        boolean result;
        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            ColumnType columnType = original.column(columnIndex).type();
            result = columnType.compare(rowInOriginal, tempTable.column(columnIndex), original.column(columnIndex));
            if (!result) {
                return false;
            }
        }
        return true;
    }

    public static void head(int rowCount, Table oldTable, Table newTable) {
        Selection rows = new BitmapBackedSelection(rowCount);
        for (int i = 0; i < rowCount; i++) {
            rows.add(i);
        }
        copyRowsToTable(rows, oldTable, newTable);
    }

    public static void tail(int rowsToInclude, Table oldTable, Table newTable) {
        int oldTableSize = oldTable.rowCount();
        int start = oldTableSize - rowsToInclude;
        Selection rows = new BitmapBackedSelection(rowsToInclude);
        for (int i = start; i < oldTableSize; i++) {
            rows.add(i);
        }
        copyRowsToTable(rows, oldTable, newTable);
    }
}
