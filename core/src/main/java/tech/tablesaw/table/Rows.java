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
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.util.Selection;

import javax.annotation.concurrent.Immutable;

/**
 * A static utility class for row operations
 */
@Immutable
public class Rows {

    // Don't instantiate
    private Rows() {
    }

    public static void copyRowsToTable(IntArrayList rows, Table oldTable, Table newTable) {

        for (int columnIndex = 0; columnIndex < oldTable.columnCount(); columnIndex++) {
            ColumnType columnType = oldTable.column(columnIndex).type();
            switch (columnType) {
                case FLOAT:
                    copy(rows, (FloatColumn) oldTable.column(columnIndex), (FloatColumn) newTable.column(columnIndex));
                    break;
                case INTEGER:
                    copy(rows, (IntColumn) oldTable.column(columnIndex), (IntColumn) newTable.column(columnIndex));
                    break;
                case SHORT_INT:
                    copy(rows, (ShortColumn) oldTable.column(columnIndex), (ShortColumn) newTable.column(columnIndex));
                    break;
                case LONG_INT:
                    copy(rows, (LongColumn) oldTable.column(columnIndex), (LongColumn) newTable.column(columnIndex));
                    break;
                case CATEGORY:
                    copy(rows, (CategoryColumn) oldTable.column(columnIndex), (CategoryColumn) newTable.column
                            (columnIndex));
                    break;
                case BOOLEAN:
                    copy(rows, (BooleanColumn) oldTable.column(columnIndex), (BooleanColumn) newTable.column
                            (columnIndex));
                    break;
                case DOUBLE:
                    copy(rows, (DoubleColumn) oldTable.column(columnIndex), (DoubleColumn) newTable.column
                            (columnIndex));
                    break;
                case LOCAL_DATE:
                    copy(rows, (DateColumn) oldTable.column(columnIndex), (DateColumn) newTable.column(columnIndex));
                    break;
                case LOCAL_DATE_TIME:
                    copy(rows, (DateTimeColumn) oldTable.column(columnIndex), (DateTimeColumn) newTable.column
                            (columnIndex));
                    break;
                case LOCAL_TIME:
                    copy(rows, (TimeColumn) oldTable.column(columnIndex), (TimeColumn) newTable.column(columnIndex));
                    break;
                default:
                    throw new IllegalStateException("Unhandled column type in case statement");
            }
        }
    }

    public static void appendRowToTable(int row, Table oldTable, Table newTable) {

        IntArrayList rows = new IntArrayList();
        rows.add(row);
        copyRowsToTable(rows, oldTable, newTable);
    }

    public static boolean compareRows(int rowInOriginal, Table original, Table tempTable) {

        boolean result;
        for (int columnIndex = 0; columnIndex < original.columnCount(); columnIndex++) {
            ColumnType columnType = original.column(columnIndex).type();
            switch (columnType) {
                case FLOAT:
                    result = compare(rowInOriginal, (FloatColumn) tempTable.column(columnIndex), (FloatColumn)
                            original.column(columnIndex));
                    if (!result) return false;
                    break;
                case DOUBLE:
                    result = compare(rowInOriginal, (DoubleColumn) tempTable.column(columnIndex), (DoubleColumn)
                            original.column(columnIndex));
                    if (!result) return false;
                    break;
                case INTEGER:
                    result = compare(rowInOriginal, (IntColumn) tempTable.column(columnIndex), (IntColumn) original
                            .column(columnIndex));
                    if (!result) return false;
                    break;
                case SHORT_INT:
                    result = compare(rowInOriginal, (ShortColumn) tempTable.column(columnIndex), (ShortColumn)
                            original.column(columnIndex));
                    if (!result) return false;
                    break;
                case LONG_INT:
                    result = compare(rowInOriginal, (LongColumn) tempTable.column(columnIndex), (LongColumn) original
                            .column(columnIndex));
                    if (!result) return false;
                    break;
                case CATEGORY:
                    result = compare(rowInOriginal, (CategoryColumn) tempTable.column(columnIndex), (CategoryColumn)
                            original.column(columnIndex));
                    if (!result) return false;
                    break;
                case BOOLEAN:
                    result = compare(rowInOriginal, (BooleanColumn) tempTable.column(columnIndex), (BooleanColumn)
                            original.column(columnIndex));
                    if (!result) return false;
                    break;
                case LOCAL_DATE:
                    result = compare(rowInOriginal, (DateColumn) tempTable.column(columnIndex), (DateColumn) original
                            .column(columnIndex));
                    if (!result) return false;
                    break;
                case LOCAL_DATE_TIME:
                    result = compare(rowInOriginal, (DateTimeColumn) tempTable.column(columnIndex), (DateTimeColumn)
                            original.column(columnIndex));
                    if (!result) return false;
                    break;
                case LOCAL_TIME:
                    result = compare(rowInOriginal, (TimeColumn) tempTable.column(columnIndex), (TimeColumn) original
                            .column(columnIndex));
                    if (!result) return false;
                    break;
                default:
                    throw new RuntimeException("Unhandled column type in case statement");
            }
        }
        return true;
    }

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

    private static void copy(IntArrayList rows, FloatColumn oldColumn, FloatColumn newColumn) {
        for (int index : rows) {
            newColumn.append(oldColumn.get(index));
        }
    }

    private static void copy(IntArrayList rows, DoubleColumn oldColumn, DoubleColumn newColumn) {
        for (int index : rows) {
            newColumn.append(oldColumn.get(index));
        }
    }

    private static boolean compare(int row, FloatColumn tempTable, FloatColumn original) {
        return original.get(row) == tempTable.get(tempTable.size() - 1);
    }

    private static boolean compare(int row, DoubleColumn tempTable, DoubleColumn original) {
        return original.get(row) == tempTable.get(tempTable.size() - 1);
    }

    private static void copy(IntArrayList rows, CategoryColumn oldColumn, CategoryColumn newColumn) {
        newColumn.initializeWith(oldColumn.getValues(rows), oldColumn.dictionaryMap());
    }

    private static boolean compare(int row, CategoryColumn tempTable, CategoryColumn original) {
        String t = tempTable.get(tempTable.size() - 1);
        String o = original.get(row);
        return o.equals(t);
    }

    private static void copy(IntArrayList rows, BooleanColumn oldColumn, BooleanColumn newColumn) {
        for (int index : rows) {
            newColumn.append(oldColumn.get(index));
        }
    }

    private static boolean compare(int row, BooleanColumn tempTable, BooleanColumn original) {
        return original.get(row) == tempTable.get(tempTable.size() - 1);
    }

    private static void copy(IntArrayList rows, IntColumn oldColumn, IntColumn newColumn) {
        for (int index : rows) {
            newColumn.append(oldColumn.get(index));
        }
    }

    private static boolean compare(int row, IntColumn tempTable, IntColumn original) {
        return original.get(row) == tempTable.get(tempTable.size() - 1);
    }

    private static void copy(IntArrayList rows, ShortColumn oldColumn, ShortColumn newColumn) {
        for (int index : rows) {
            newColumn.append(oldColumn.get(index));
        }
    }

    private static boolean compare(int row, ShortColumn tempTable, ShortColumn original) {
        short t = tempTable.get(tempTable.size() - 1);
        short o = original.get(row);
        return o == t;
    }

    private static void copy(IntArrayList rows, LongColumn oldColumn, LongColumn newColumn) {
        for (int index : rows) {
            newColumn.append(oldColumn.get(index));
        }
    }

    private static boolean compare(int row, LongColumn tempTable, LongColumn original) {
        return original.get(row) == tempTable.get(tempTable.size() - 1);
    }

    private static void copy(IntArrayList rows, DateTimeColumn oldColumn, DateTimeColumn newColumn) {
        for (int index : rows) {
            newColumn.appendInternal(oldColumn.getLongInternal(index));
        }
    }

    private static boolean compare(int row, DateTimeColumn tempTable, DateTimeColumn original) {
        return original.getLongInternal(row) == tempTable.getLongInternal(tempTable.size() - 1);
    }

    private static void copy(IntArrayList rows, DateColumn oldColumn, DateColumn newColumn) {
        for (int index : rows) {
            newColumn.appendInternal(oldColumn.getIntInternal(index));
        }
    }

    private static boolean compare(int row, DateColumn tempTable, DateColumn original) {
        return original.getIntInternal(row) == tempTable.getIntInternal(tempTable.size() - 1);
    }

    private static void copy(IntArrayList rows, TimeColumn oldColumn, TimeColumn newColumn) {
        for (int index : rows) {
            newColumn.appendInternal(oldColumn.getIntInternal(index));
        }
    }

    private static boolean compare(int row, TimeColumn tempTable, TimeColumn original) {
        return original.getIntInternal(row) == tempTable.getIntInternal(tempTable.size() - 1);
    }
}
