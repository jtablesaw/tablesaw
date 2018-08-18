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

import it.unimi.dsi.fastutil.ints.IntArrayList;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;
import tech.tablesaw.util.ColumnUtil;

import javax.annotation.concurrent.Immutable;

/**
 * A static utility class for row operations
 */
@Immutable
public class Rows {

    // Don't instantiate
    private Rows() {}

    public static void copyRowsToTable(IntArrayList rows, Table oldTable, Table newTable) {
        for (int i = 0; i < rows.size(); i++) {
            int rowIndex = rows.getInt(i);
            int columnCount = oldTable.columnCount();
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                ColumnUtil.append(oldTable.column(columnIndex), newTable.column(columnIndex), rowIndex);
            }
        }
    }

    public static void appendRowToTable(int row, Table oldTable, Table newTable) {
        IntArrayList rows = new IntArrayList();
        rows.add(row);
        copyRowsToTable(rows, oldTable, newTable);
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

    /**
     * Copies the rows in oldTable to newTable if they are included in the given selection
     *
     * TODO(lwhite): Possible performance enhancement: Consider implementing this method directly so we don't need array list
     */
    public static void copyRowsToTable(Selection rows, Table oldTable, Table newTable) {
        int[] r = rows.toArray();
        IntArrayList rowArray = new IntArrayList(r);
        copyRowsToTable(rowArray, oldTable, newTable);
    }

    public static void head(int rowCount, Table oldTable, Table newTable) {
        IntArrayList rows = new IntArrayList(rowCount);
        for (int i = 0; i < rowCount; i++) {
            rows.add(i);
        }
        copyRowsToTable(rows, oldTable, newTable);
    }

    public static void tail(int rowsToInclude, Table oldTable, Table newTable) {
        int oldTableSize = oldTable.rowCount();
        int start = oldTableSize - rowsToInclude;
        IntArrayList rows = new IntArrayList(rowsToInclude);
        for (int i = start; i < oldTableSize; i++) {
            rows.add(i);
        }
        copyRowsToTable(rows, oldTable, newTable);
    }
}
