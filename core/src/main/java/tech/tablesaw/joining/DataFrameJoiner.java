package tech.tablesaw.joining;

import com.google.common.collect.Streams;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.index.CategoryIndex;
import tech.tablesaw.index.IntIndex;
import tech.tablesaw.index.LongIndex;
import tech.tablesaw.selection.Selection;

import java.util.concurrent.atomic.AtomicInteger;

public class DataFrameJoiner {

    private static final String TABLE_ALIAS = "T";

    private final Table table;
    private final Column column;
    private AtomicInteger joinTableId = new AtomicInteger(2);

    public DataFrameJoiner(Table table, String column) {
        this.table = table;
        this.column = table.column(column);
    }

    /**
     * Joins to the given tables assuming that they have a column of the name we're joining on
     *
     * @param tables The tables to join with
     */
    public Table inner(Table... tables) {
        return inner(false, tables);
    }

    /**
     * Joins to the given tables assuming that they have a column of the name we're joining on
     *
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed*
     * @param tables The tables to join with
     */
    public Table inner(boolean allowDuplicateColumnNames, Table... tables) {
        Table joined = table;
        for (Table table2 : tables) {
          joined = inner(table2, column.name(), allowDuplicateColumnNames);
        }
        return joined;
    }

    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2   The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     *                 rounding to integers.
     */
    public Table inner(Table table2, String col2Name) {
        return joinInternal(table2, col2Name, false, false);
    }

    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2   The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     *                 rounding to integers.
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed*
     */
    public Table inner(Table table2, String col2Name, boolean allowDuplicateColumnNames) {
        return joinInternal(table2, col2Name, false, allowDuplicateColumnNames);
    }

    private Table joinInternal(Table table2, String col2Name, boolean outer, boolean allowDuplicates) {

        if (allowDuplicates) {
            renameColumnsWithDuplicateNames(table2, col2Name);
        }

        Table result = emptyTableFromColumns(table, table2, col2Name);
        if (column instanceof DateColumn) {
            IntIndex index = new IntIndex(table2.dateColumn(col2Name));
            DateColumn col1 = (DateColumn) column;
            for (int i = 0; i < col1.size(); i++) {
                int value = col1.getIntInternal(i);
                Table table1Rows = table.where(Selection.with(i));
                Table table2Rows = table2.where(index.get(value));
                table2Rows.removeColumns(col2Name);
                if (outer && table2Rows.isEmpty()) {
                    withMissing(result, table1Rows, table2Rows);
                } else {
                    crossProduct(result, table1Rows, table2Rows);
                }
            }
        } else if (column instanceof DateTimeColumn) {
            LongIndex index = new LongIndex(table2.dateTimeColumn(col2Name));
            DateTimeColumn col1 = (DateTimeColumn) column;
            for (int i = 0; i < col1.size(); i++) {
                long value = col1.getLongInternal(i);
                Table table1Rows = table.where(Selection.with(i));
                Table table2Rows = table2.where(index.get(value));
                table2Rows.removeColumns(col2Name);
                if (outer && table2Rows.isEmpty()) {
                    withMissing(result, table1Rows, table2Rows);
                } else {
                    crossProduct(result, table1Rows, table2Rows);
                }
            }
        } else if (column instanceof TimeColumn) {
            IntIndex index = new IntIndex(table2.timeColumn(col2Name));
            TimeColumn col1 = (TimeColumn) column;
            for (int i = 0; i < col1.size(); i++) {
                int value = col1.getIntInternal(i);
                Table table1Rows = table.where(Selection.with(i));
                Table table2Rows = table2.where(index.get(value));
                table2Rows.removeColumns(col2Name);
                if (outer && table2Rows.isEmpty()) {
                    withMissing(result, table1Rows, table2Rows);
                } else {
                    crossProduct(result, table1Rows, table2Rows);
                }
            }
        } else if (column instanceof StringColumn) {
            CategoryIndex index = new CategoryIndex(table2.stringColumn(col2Name));
            StringColumn col1 = (StringColumn) column;
            for (int i = 0; i < col1.size(); i++) {
                String value = col1.get(i);
                Table table1Rows = table.where(Selection.with(i));
                Table table2Rows = table2.where(index.get(value));
                table2Rows.removeColumns(col2Name);
                if (outer && table2Rows.isEmpty()) {
                    withMissing(result, table1Rows, table2Rows);
                } else {
                    crossProduct(result, table1Rows, table2Rows);
                }
            }
        } else if (column instanceof DoubleColumn) {
            LongIndex index = new LongIndex(table2.numberColumn(col2Name));
            NumberColumn col1 = (NumberColumn) column;
            for (int i = 0; i < col1.size(); i++) {
                long value = col1.getLong(i);
                Table table1Rows = table.where(Selection.with(i));
                Table table2Rows = table2.where(index.get(value));
                table2Rows.removeColumns(col2Name);
                if (outer && table2Rows.isEmpty()) {
                    withMissing(result, table1Rows, table2Rows);
                } else {
                    crossProduct(result, table1Rows, table2Rows);
                }
            }
        } else {
            throw new IllegalArgumentException(
                    "Joining is supported on numeric, string, and date-like columns. Column "
                            + column.name() + " is of type " + column.type());
        }

        return result;
    }

    private void renameColumnsWithDuplicateNames(Table table2, String col2Name) {
        String table2Alias = TABLE_ALIAS + joinTableId.getAndIncrement();

        for (Column table2Column : table2.columns()) {
            String columnName = table2Column.name();
            if (table.columnNames().contains(columnName)
                    && !columnName.equalsIgnoreCase(col2Name)) {
                table2Column.setName(newName(table2Alias, columnName));
            }
        }
    }

    private String newName(String table2Alias, String columnName) {
        return table2Alias + "." + columnName;
    }

    /**
     * Joins to the given tables assuming that they have a column of the name we're joining on
     *
     * @param tables The tables to join with
     */
    public Table leftOuter(Table... tables) {
        return leftOuter(false, tables);
    }

    /**
     * Joins to the given tables assuming that they have a column of the name we're joining on
     *
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed*
     * @param tables The tables to join with
     */
    public Table leftOuter(boolean allowDuplicateColumnNames, Table... tables) {
        Table joined = table;
        for (Table table2 : tables) {
          joined = leftOuter(table2, column.name(), allowDuplicateColumnNames);
        }
        return joined;
    }

    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2   The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     *                 rounding to integers.
     */
    public Table leftOuter(Table table2, String col2Name) {
        return joinInternal(table2, col2Name, true, false);
    }

    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2   The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     *                 rounding to integers.
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed
     */
    public Table leftOuter(Table table2, String col2Name, boolean allowDuplicateColumnNames) {
        return joinInternal(table2, col2Name, true, allowDuplicateColumnNames);
    }

    /**
     * Joins to the given tables assuming that they have a column of the name we're joining on
     *
     * @param tables The tables to join with
     */
    public Table rightOuter(Table... tables) {
        return rightOuter(false, tables);
    }

    /**
     * Joins to the given tables assuming that they have a column of the name we're joining on
     *
     * @param tables The tables to join with
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed
     */
    public Table rightOuter(boolean allowDuplicateColumnNames, Table... tables) {
        Table joined = table;
        for (Table table2 : tables) {
          joined = rightOuter(table2, column.name(), allowDuplicateColumnNames);
        }
        return joined;
    }

    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2   The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     *                 rounding to integers.
     */
    public Table rightOuter(Table table2, String col2Name) {
        return rightOuter(table2, col2Name, false);
    }

    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2   The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     *                 rounding to integers.
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed
     */

    public Table rightOuter(Table table2, String col2Name, boolean allowDuplicateColumnNames) {
        Table leftOuter = table2.join(col2Name).leftOuter(table, column.name(), allowDuplicateColumnNames);

        // reverse the columns
        Table result = Table.create(leftOuter.name());
        for (String name : table.columnNames()) {
            result.addColumns(leftOuter.column(name));
        }
        for (String name : table2.columnNames()) {
            if (! result.columnNames().contains(name)) {
                result.addColumns(leftOuter.column(name));
            }
        }
        return result;
    }

    private Table emptyTableFromColumns(Table table1, Table table2, String col2Name) {
        Column[] cols = Streams.concat(
                table1.columns().stream(),
                table2.columns().stream().filter(c -> !c.name().equalsIgnoreCase(col2Name))
        ).map(col -> col.emptyCopy(col.size())).toArray(Column[]::new);
        return Table.create(table1.name(), cols);
    }

    private void crossProduct(Table destination, Table table1, Table table2) {
        for (int c = 0; c < table1.columnCount() + table2.columnCount(); c++) {
            for (int r1 = 0; r1 < table1.rowCount(); r1++) {
                for (int r2 = 0; r2 < table2.rowCount(); r2++) {
                    if (c < table1.columnCount()) {
                        destination.column(c).appendCell(table1.getUnformatted(r1, c));
                    } else {
                        destination.column(c).appendCell(table2.getUnformatted(r2, c - table1.columnCount()));
                    }
                }
            }
        }
    }

    /**
     * Adds rows to destination for each row in table1, with the columns from table2 added as missing values in each
     */
    private void withMissing(Table destination, Table table1, Table table2) {
        for (int c = 0; c < table1.columnCount() + table2.columnCount(); c++) {
            for (int r1 = 0; r1 < table1.rowCount(); r1++) {
                if (c < table1.columnCount()) {
                    destination.column(c).appendCell(table1.getUnformatted(r1, c));
                } else {
                    destination.column(c).appendMissing();
                }
            }
        }
    }
}
