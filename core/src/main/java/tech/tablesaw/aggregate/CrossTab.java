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

import com.google.common.collect.TreeBasedTable;
import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

/**
 * Utilities for creating frequency and proportion cross tabs
 */
public final class CrossTab {

    private static final String LABEL_COLUMN_NAME = "[labels]";

    /**
     * Returns a table containing two-dimensional cross-tabulated counts for each combination of values in
     * {@code column1} and {@code column2}
     * <p>
     *
     * @param table   The table we're deriving the counts from
     * @param column1 A column in {@code table}
     * @param column2 Another column in {@code table}
     * @return A table containing the cross-tabs
     */
    public static Table counts(Table table, CategoricalColumn column1, CategoricalColumn column2) {

        Table t = Table.create("Crosstab Counts: " + column1.name() + " x " + column2.name());
        t.addColumn(StringColumn.create(LABEL_COLUMN_NAME));

        Table temp = table.sortOn(column1.name(), column2.name());

        int colIndex1 = table.columnIndex(column1.name());
        int colIndex2 = table.columnIndex(column2.name());

        com.google.common.collect.Table<String, String, Integer> gTable = TreeBasedTable.create();
        String a;
        String b;

        for (int row : temp) {
            a = temp.column(colIndex1).getString(row);
            b = temp.column(colIndex2).getString(row);
            Integer cellValue = gTable.get(a, b);
            Integer value;
            if (cellValue != null) {
                value = cellValue + 1;
            } else {
                value = 1;
            }
            gTable.put(a, b, value);
        }

        for (String colName : gTable.columnKeySet()) {
            t.addColumn(NumberColumn.create(colName));
        }

        t.addColumn(NumberColumn.create("total"));

        int[] columnTotals = new int[t.columnCount()];

        for (String rowKey : gTable.rowKeySet()) {
            t.column(0).appendCell(rowKey);

            int rowSum = 0;

            for (String colKey : gTable.columnKeySet()) {
                Integer cellValue = gTable.get(rowKey, colKey);
                if (cellValue != null) {
                    int colIdx = t.columnIndex(colKey);
                    t.numberColumn(colIdx).append(cellValue);
                    rowSum += cellValue;
                    columnTotals[colIdx] = columnTotals[colIdx] + cellValue;

                } else {
                    t.numberColumn(colKey).append(0);
                }
            }
            t.numberColumn(t.columnCount() - 1).append(rowSum);
        }
        t.column(0).appendCell("Total");
        int grandTotal = 0;
        for (int i = 1; i < t.columnCount() - 1; i++) {
            t.numberColumn(i).append(columnTotals[i]);
            grandTotal = grandTotal + columnTotals[i];
        }
        t.numberColumn(t.columnCount() - 1).append(grandTotal);
        return t;
    }


    public static Table counts(Table table, String column1) {
        return table.countBy(table.stringColumn(column1));
    }

    public static Table percents(Table table, String column1) {
        Table countTable = counts(table, column1);
        Table percentTable = Table.create(countTable.name());
        percentTable.addColumn(countTable.column(0).copy());

        NumberColumn countsColumn = countTable.numberColumn("Count");
        NumberColumn pctsColumn = NumberColumn.create("Percents");
        double sum = countsColumn.sum();
        for (int i = 0; i < countsColumn.size(); i++) {
            pctsColumn.append(countsColumn.get(i) / sum);
        }
        percentTable.addColumn(pctsColumn);
        return percentTable;
    }

    public static Table rowPercents(Table xTabCounts) {

        Table pctTable = Table.create("Crosstab Row Proportions: ");
        StringColumn labels = StringColumn.create(LABEL_COLUMN_NAME);

        pctTable.addColumn(labels);

        for (int i = 0; i < xTabCounts.rowCount(); i++) {
            labels.append(xTabCounts.column(0).getString(i));
        }

        for (int i = 1; i < xTabCounts.columnCount(); i++) {
            Column column = xTabCounts.column(i);
            pctTable.addColumn(NumberColumn.create(column.name()));
        }

        for (int i = 0; i < xTabCounts.rowCount(); i++) {
            float rowTotal = (float) xTabCounts.numberColumn(xTabCounts.columnCount() - 1).get(i);

            for (int c = 1; c < xTabCounts.columnCount(); c++) {
                if (rowTotal == 0) {
                    pctTable.numberColumn(c).append(Float.NaN);
                } else {
                    pctTable.numberColumn(c).append((float) xTabCounts.numberColumn(c).get(i) / rowTotal);
                }
            }
        }
        return pctTable;
    }

    public static Table tablePercents(Table xTabCounts) {

        Table pctTable = Table.create("Crosstab Table Proportions: ");
        StringColumn labels = StringColumn.create(LABEL_COLUMN_NAME);

        pctTable.addColumn(labels);

        double grandTotal = xTabCounts.numberColumn(xTabCounts.columnCount() - 1).get(xTabCounts.rowCount() - 1);

        for (int i = 0; i < xTabCounts.rowCount(); i++) {
            labels.append(xTabCounts.column(0).getString(i));
        }

        for (int i = 1; i < xTabCounts.columnCount(); i++) {
            Column column = xTabCounts.column(i);
            pctTable.addColumn(NumberColumn.create(column.name()));
        }

        for (int i = 0; i < xTabCounts.rowCount(); i++) {
            for (int c = 1; c < xTabCounts.columnCount(); c++) {
                if (grandTotal == 0) {
                    pctTable.numberColumn(c).append(Float.NaN);
                } else {
                    pctTable.numberColumn(c).append((float) xTabCounts.numberColumn(c).get(i) / grandTotal);
                }
            }
        }
        return pctTable;
    }

    public static Table columnPercents(Table xTabCounts) {

        Table pctTable = Table.create("Crosstab Column Proportions: ");
        StringColumn labels = StringColumn.create(LABEL_COLUMN_NAME);

        pctTable.addColumn(labels);

        // setup the labels
        for (int i = 0; i < xTabCounts.rowCount(); i++) {
            labels.append(xTabCounts.column(0).getString(i));
        }

        // create the new cols
        for (int i = 1; i < xTabCounts.columnCount(); i++) {
            Column column = xTabCounts.column(i);
            pctTable.addColumn(NumberColumn.create(column.name()));
        }

        // get the column totals
        double[] columnTotals = new double[xTabCounts.columnCount() - 1];
        int totalRow = xTabCounts.rowCount() - 1;
        for (int i = 1; i < xTabCounts.columnCount(); i++) {
            columnTotals[i - 1] = xTabCounts.numberColumn(i).get(totalRow);
        }

        // calculate the column pcts and update the new table
        for (int i = 0; i < xTabCounts.rowCount(); i++) {
            for (int c = 1; c < xTabCounts.columnCount(); c++) {
                if (columnTotals[c - 1] == 0) {
                    pctTable.numberColumn(c).append(Float.NaN);
                } else {
                    pctTable.numberColumn(c).append((float) xTabCounts.numberColumn(c).get(i) / columnTotals[c - 1]);
                }
            }
        }
        return pctTable;
    }

    /**
     * Returns a table containing the column percents made from a source table, after first calculating the counts
     * cross-tabulated from the given columns
     */
    public static Table columnPercents(Table table, CategoricalColumn column1, CategoricalColumn column2) {
        Table xTabs = counts(table, column1, column2);
        return columnPercents(xTabs);
    }

    /**
     * Returns a table containing the row percents made from a source table, after first calculating the counts
     * cross-tabulated from the given columns
     */
    public static Table rowPercents(Table table, CategoricalColumn column1, CategoricalColumn column2) {
        Table xTabs = counts(table, column1, column2);
        return rowPercents(xTabs);
    }

    /**
     * Returns a table containing the table percents made from a source table, after first calculating the counts
     * cross-tabulated from the given columns
     */
    public static Table tablePercents(Table table, CategoricalColumn column1, CategoricalColumn column2) {
        Table xTabs = counts(table, column1, column2);
        return tablePercents(xTabs);
    }
}