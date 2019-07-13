package tech.tablesaw.joining;

import com.google.common.collect.Streams;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.InstantColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.booleans.BooleanColumnType;
import tech.tablesaw.columns.dates.DateColumnType;
import tech.tablesaw.columns.datetimes.DateTimeColumnType;
import tech.tablesaw.columns.instant.InstantColumnType;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.FloatColumnType;
import tech.tablesaw.columns.numbers.IntColumnType;
import tech.tablesaw.columns.numbers.LongColumnType;
import tech.tablesaw.columns.numbers.ShortColumnType;
import tech.tablesaw.columns.strings.StringColumnType;
import tech.tablesaw.columns.strings.TextColumnType;
import tech.tablesaw.columns.times.TimeColumnType;
import tech.tablesaw.index.ByteIndex;
import tech.tablesaw.index.DoubleIndex;
import tech.tablesaw.index.FloatIndex;
import tech.tablesaw.index.Index;
import tech.tablesaw.index.IntIndex;
import tech.tablesaw.index.LongIndex;
import tech.tablesaw.index.ShortIndex;
import tech.tablesaw.index.StringIndex;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DataFrameJoiner {

    private static final String TABLE_ALIAS = "T";

    private final Table table;
    private Column<?>[] joinColumns;
    private final String[] columnNames;
    private AtomicInteger joinTableId = new AtomicInteger(2);

    /**
     * Constructor.
     * @param table       The table to join on
     * @param columnNames The column names to join on
     */
    public DataFrameJoiner(Table table, String... columnNames) {
        this.table = table;
        joinColumns = new Column<?>[columnNames.length];
        this.columnNames = columnNames;
        for (int i = 0; i < this.columnNames.length; i++) {
            String colName = this.columnNames[i];
            this.joinColumns[i] = table.column(colName);
        }
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

        for (Table currT : tables) {
            joined = joinInternal(joined, currT, false, allowDuplicateColumnNames, columnNames);
        }
        return joined;
    }

    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2   The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     *                 rounding to integers.
     * @return The resulting table
     */
     public Table inner(Table table2, String col2Name) {
         return inner(table2, false, col2Name);
     }

    /**
     * Joins the joiner to the table2, using the given columns for the second table and returns the resulting table
     *
     * @param table2    The table to join with
     * @param col2Names The columns to join on. If a name refers to a double column, the join is performed after
     *                  rounding to integers.
     * @return The resulting table
     */
    public Table inner(Table table2, String[] col2Names) {
        return inner(table2, false, col2Names);
    }

    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2   The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     *                 rounding to integers.
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed*
     * @return The resulting table
     */
    public Table inner(Table table2, String col2Name, boolean allowDuplicateColumnNames) {
        return inner(table2, allowDuplicateColumnNames, col2Name);
    }

    /**
     * Joins the joiner to the table2, using the given columns for the second table and returns the resulting table
     *
     * @param table2    The table to join with
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed*
     * @param col2Names The columns to join on. If a name refers to a double column, the join is performed after
     *                  rounding to integers.
     * @return The resulting table
     */
    public Table inner(Table table2, boolean allowDuplicateColumnNames, String... col2Names) {
        return inner(table2, false, allowDuplicateColumnNames, col2Names);
    }

    /**
     * Joins the joiner to the table2, using the given columns for the second table and returns the resulting table
     *
     * @param table2    The table to join with
     * @param outer     True if this join is actually an outer join, left or right or full, otherwise false.
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed*
     * @param col2Names The columns to join on. If a name refers to a double column, the join is performed after
     *                  rounding to integers.
     * @return The resulting table
     */
    public Table inner(Table table2, boolean outer, boolean allowDuplicateColumnNames, String... col2Names) {
        Table joinedTable;
        joinedTable = joinInternal(table, table2, outer, allowDuplicateColumnNames, col2Names);
        return joinedTable;
    }

    /**
     * Joins the joiner to the {@code table2}, using the given columns for the second table and returns the resulting table
     *
     * @param table1    The base table to join on
     * @param table2    The table to join with
     * @param outer     True if this join is actually an outer join, left or right or full, otherwise false.
     * @param allowDuplicates    if {@code false} the join will fail if any columns other than the join column have the same name
     *                           if {@code true} the join will succeed and duplicate columns are renamed*
     * @param col2Names The columns to join on. If a col2Name refers to a double column, the join is performed after
     *                  rounding to integers.
     * @return The resulting table
     */
    private Table joinInternal(Table table1, Table table2, boolean outer, boolean allowDuplicates, String... col2Names) {

        if (allowDuplicates) {
            renameColumnsWithDuplicateNames(table1, table2, col2Names);
        }
        Table result = emptyTableFromColumns(table1, table2, col2Names);
        Map<Column<?>, Index> columnIndexMap = new HashMap<>();

        for (int i = 0; i < joinColumns.length; i++) {
            Column<?> col = joinColumns[i];
            String col2Name = col2Names[i];
            columnIndexMap.put(col, indexFor(table2, col2Name, col));
        }

        for (Row row : table1) {
            int ri = row.getRowNumber();
            Table table1Rows = table1.where(Selection.with(ri));
            Selection rowBitMapMultiCol = null;
            for (int i = 0; i < joinColumns.length; i++) {

                // Need to use the column from table1 that is the same column originally
                // defined for this DataFrameJoiner. Column names must be unique within the
                // same table, so use the original column's name to get the corresponding
                // column out of the table1 input Table.
                Column<?> column = joinColumns[i];
                Column<?> table1Column = table1.column(column.name());

                ColumnType type = table1Column.type();
                // relies on both arrays, columns, and col2Names,
                // having corresponding values at same index
                Selection rowBitMapOneCol = null;
                if (type instanceof DateColumnType) {
                    IntIndex index = (IntIndex) columnIndexMap.get(column);
                    DateColumn col1 = (DateColumn) table1Column;
                    int value = col1.getIntInternal(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof TimeColumnType) {
                    IntIndex index = (IntIndex) columnIndexMap.get(column);
                    TimeColumn col1 = (TimeColumn) table1Column;
                    int value = col1.getIntInternal(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof DateTimeColumnType) {
                    LongIndex index = (LongIndex) columnIndexMap.get(column);
                    DateTimeColumn col1 = (DateTimeColumn) table1Column;
                    long value = col1.getLongInternal(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof InstantColumnType) {
                    LongIndex index = (LongIndex) columnIndexMap.get(column);
                    InstantColumn col1 = (InstantColumn) table1Column;
                    long value = col1.getLongInternal(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof StringColumnType || type instanceof TextColumnType) {
                    StringIndex index = (StringIndex) columnIndexMap.get(column);
                    StringColumn col1 = (StringColumn) table1Column;
                    String value = col1.get(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof IntColumnType) {
                    IntIndex index = (IntIndex) columnIndexMap.get(column);
                    IntColumn col1 = (IntColumn) table1Column;
                    int value = col1.getInt(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof LongColumnType) {
                    LongIndex index = (LongIndex) columnIndexMap.get(column);
                    LongColumn col1 = (LongColumn) table1Column;
                    long value = col1.getLong(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof ShortColumnType) {
                    ShortIndex index = (ShortIndex) columnIndexMap.get(column);
                    ShortColumn col1 = (ShortColumn) table1Column;
                    short value = col1.getShort(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof BooleanColumnType) {
                    ByteIndex index = (ByteIndex) columnIndexMap.get(column);
                    BooleanColumn col1 = (BooleanColumn) table1Column;
                    byte value = col1.getByte(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof DoubleColumnType) {
                    DoubleIndex index = (DoubleIndex) columnIndexMap.get(column);
                    DoubleColumn col1 = (DoubleColumn) table1Column;
                    double value = col1.getDouble(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof FloatColumnType) {
                    FloatIndex index = (FloatIndex) columnIndexMap.get(column);
                    FloatColumn col1 = (FloatColumn) table1Column;
                    float value = col1.getFloat(ri);
                    rowBitMapOneCol = index.get(value);
                } else {
                    throw new IllegalArgumentException(
                            "Joining is supported on numeric, string, and date-like columns. Column "
                                    + table1Column.name() + " is of type " + table1Column.type());
                }
                // combine Selection's into one big AND Selection
                if (rowBitMapOneCol != null) {
                    rowBitMapMultiCol =
                            rowBitMapMultiCol != null
                            ? rowBitMapMultiCol.and(rowBitMapOneCol)
                            : rowBitMapOneCol;
                }
            }

            Table table2Rows = table2.where(rowBitMapMultiCol);
            table2Rows.removeColumns(col2Names);
            if (outer && table2Rows.isEmpty()) {
                withMissingLeftJoin(result, table1Rows);
            } else {
                crossProduct(result, table1Rows, table2Rows);
            }
        }
        return result;
    }

    private Index indexFor(Table table2, String col2Name, Column<?> col) {
        ColumnType type = col.type();
        if (type instanceof DateColumnType) {
            return new IntIndex(table2.dateColumn(col2Name));
        }
        if (type instanceof DateTimeColumnType) {
            return new LongIndex(table2.dateTimeColumn(col2Name));
        }
        if (type instanceof InstantColumnType) {
            return new LongIndex(table2.instantColumn(col2Name));
        }
        if (type instanceof TimeColumnType) {
            return new IntIndex(table2.timeColumn(col2Name));
        }
        if (type instanceof StringColumnType || type instanceof TextColumnType) {
            return new StringIndex(table2.stringColumn(col2Name));
        }
        if (type instanceof IntColumnType) {
            return new IntIndex(table2.intColumn(col2Name));
        }
        if (type instanceof LongColumnType) {
            return new LongIndex(table2.longColumn(col2Name));
        }
        if (type instanceof ShortColumnType) {
            return new ShortIndex(table2.shortColumn(col2Name));
        }
        if (type instanceof BooleanColumnType) {
            return new ByteIndex(table2.booleanColumn(col2Name));
        }
        if (type instanceof DoubleColumnType) {
            return new DoubleIndex(table2.doubleColumn(col2Name));
        }
        if (type instanceof FloatColumnType) {
            return new FloatIndex(table2.floatColumn(col2Name));
        }
        throw new IllegalArgumentException(
                    "Joining attempted on unsupported column type " + col.type());
    }

    private void renameColumnsWithDuplicateNames(Table table1, Table table2, String... col2Names) {
        String table2Alias = TABLE_ALIAS + joinTableId.getAndIncrement();
        List<String> list = Arrays.asList(col2Names);
        for (Column<?> table2Column : table2.columns()) {
            String columnName = table2Column.name();
            if (table1.columnNames().stream().anyMatch(columnName::equalsIgnoreCase)
                    && !(list.stream().anyMatch(columnName::equalsIgnoreCase))) {
                table2Column.setName(newName(table2Alias, columnName));
            }
        }
    }

    private String newName(String table2Alias, String columnName) {
        return table2Alias + "." + columnName;
    }

    /**
     * Full outer join to the given tables assuming that they have a column of the name we're joining on
     *
     * @param tables The tables to join with
     * @return The resulting table
     */
    public Table fullOuter(Table... tables) {
        return fullOuter(false, tables);
    }

    /**
     * Full outer join to the given tables assuming that they have a column of the name we're joining on
     *
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed*
     * @param tables The tables to join with
     * @return The resulting table
     */
    public Table fullOuter(boolean allowDuplicateColumnNames, Table... tables) {
       Table joined = table;

        for (Table currT : tables) {
            joined = fullOuter(joined, currT, allowDuplicateColumnNames, columnNames);
        }
        return joined;
    }

    /**
     * Full outer join the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2   The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     *                 rounding to integers.
     * @return The resulting table
     */
    public Table fullOuter(Table table2, String col2Name) {
        return fullOuter(table, table2, false, col2Name);
    }

    /**
     * Full outer join table1 to table2, using the given columns for the second table and returns the resulting table
     *
     * @param table1    The base table to join on
     * @param table2    The table to join with
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed*
     * @param col2Names The columns to join on. If a name refers to a double column, the join is performed after
     *                  rounding to integers.
     * @return The resulting table
     */
    public Table fullOuter(Table table1, Table table2, boolean allowDuplicateColumnNames, String... col2Names) {
        Table result = joinInternal(table1, table2, true, allowDuplicateColumnNames, col2Names);

        Selection selection = new BitmapBackedSelection();
        for (Row row : table2) {
            int ri = row.getRowNumber();
            Selection rowBitMapMultiCol = null;

            for (int i = 0; i < joinColumns.length; i++) {

                // Need to use the column from table1 that is the same column originally
                // defined for this DataFrameJoiner. Column names must be unique within the
                // same table, so use the original column's name to get the corresponding
                // column out of the table1 input Table.
                Column<?> column = joinColumns[i];
                Column<?> table1Column = table1.column(column.name());

                ColumnType type = table1Column.type();
                // relies on both arrays, columns, and col2Names,
                // having corresponding values at same index
                String col2Name = col2Names[i];
                Selection rowBitMapOneCol = null;

                if (type instanceof DateColumnType) {
                    IntIndex index = new IntIndex(result.dateColumn(col2Name));
                    DateColumn col2 = (DateColumn) table2.column(col2Name);
                    int value = col2.getIntInternal(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof DateTimeColumnType) {
                    LongIndex index = new LongIndex(result.dateTimeColumn(col2Name));
                    DateTimeColumn col2 = (DateTimeColumn) table2.column(col2Name);
                    long value = col2.getLongInternal(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof InstantColumnType) {
                    LongIndex index = new LongIndex(result.instantColumn(col2Name));
                    InstantColumn col2 = (InstantColumn) table2.column(col2Name);
                    long value = col2.getLongInternal(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof TimeColumnType) {
                    IntIndex index = new IntIndex(result.timeColumn(col2Name));
                    TimeColumn col2 = (TimeColumn) table2.column(col2Name);
                    int value = col2.getIntInternal(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof StringColumnType || type instanceof TextColumnType) {
                    StringIndex index = new StringIndex(result.stringColumn(col2Name));
                    StringColumn col2 = (StringColumn) table2.column(col2Name);
                    String value = col2.get(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof IntColumnType) {
                    IntIndex index = new IntIndex(result.intColumn(col2Name));
                    IntColumn col2 = (IntColumn) table2.column(col2Name);
                    int value = col2.getInt(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof LongColumnType) {
                    LongIndex index = new LongIndex(result.longColumn(col2Name));
                    LongColumn col2 = (LongColumn) table2.column(col2Name);
                    long value = col2.getLong(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof ShortColumnType) {
                    ShortIndex index = new ShortIndex(result.shortColumn(col2Name));
                    ShortColumn col2 = (ShortColumn) table2.column(col2Name);
                    short value = col2.getShort(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof BooleanColumnType) {
                    ByteIndex index = new ByteIndex(result.booleanColumn(col2Name));
                    BooleanColumn col2 = (BooleanColumn) table2.column(col2Name);
                    byte value = col2.getByte(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof DoubleColumnType) {
                    DoubleIndex index = new DoubleIndex(result.doubleColumn(col2Name));
                    DoubleColumn col2 = (DoubleColumn) table2.column(col2Name);
                    double value = col2.getDouble(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof FloatColumnType) {
                    FloatIndex index = new FloatIndex(result.floatColumn(col2Name));
                    FloatColumn col2 = (FloatColumn) table2.column(col2Name);
                    float value = col2.getFloat(ri);
                    rowBitMapOneCol = index.get(value);
                } else {
                    throw new IllegalArgumentException(
                            "Joining is supported on numeric, string, and date-like columns. Column "
                                    + table1Column.name() + " is of type " + table1Column.type());
                }
                // combine Selections into one big AND Selection
                if (rowBitMapOneCol != null) {
                    rowBitMapMultiCol = rowBitMapMultiCol != null
                            ? rowBitMapMultiCol.and(rowBitMapOneCol)
                            : rowBitMapOneCol;
                }
            }
            if (rowBitMapMultiCol.isEmpty()) {
                selection.add(ri);
            }
        }
        Table table2OnlyRows = table2.where(selection);
        List<Column<?>> joinColumns = table2OnlyRows.columns(col2Names);
        table2OnlyRows.removeColumns(col2Names);
        withMissingRightJoin(result, joinColumns, table2OnlyRows);
        return result;
    }

    /**
     * Joins to the given tables assuming that they have a column of the name we're joining on
     *
     * @param tables The tables to join with
     * @return The resulting table
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
     * @return The resulting table
     */
    public Table leftOuter(boolean allowDuplicateColumnNames, Table... tables) {
        Table joined = table;
        for (Table table2 : tables) {
            joined = leftOuter(table2, allowDuplicateColumnNames, columnNames);
        }
        return joined;
    }

    /**
     * Joins the joiner to the table2, using the given columns for the second table and returns the resulting table
     *
     * @param table2    The table to join with
     * @param col2Names The columns to join on. If a name refers to a double column, the join is performed after
     *                  rounding to integers.
     * @return The resulting table
     */
    public Table leftOuter(Table table2, String[] col2Names) {
        return leftOuter(table2, false, col2Names);
    }

    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2   The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     *                 rounding to integers.
     * @return The resulting table
     */
    public Table leftOuter(Table table2, String col2Name) {
        return leftOuter(table2, false, col2Name);
    }

    /**
     * Joins the joiner to the table2, using the given columns for the second table and returns the resulting table
     *
     * @param table2    The table to join with
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed
     * @param col2Names The columns to join on. If a name refers to a double column, the join is performed after
     *                  rounding to integers.
     * @return The resulting table
     */
    public Table leftOuter(Table table2, boolean allowDuplicateColumnNames, String... col2Names) {
        return joinInternal(table, table2, true, allowDuplicateColumnNames, col2Names);
    }

    /**
     * Joins to the given tables assuming that they have a column of the name we're joining on
     *
     * @param tables The tables to join with
     * @return The resulting table
     */
    public Table rightOuter(Table... tables) {
        return rightOuter(false, tables);
    }

    /**
     * Joins to the given tables assuming that they have a column of the name we're joining on
     *
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed
     * @param tables The tables to join with
     * @return The resulting table
     */
    public Table rightOuter(boolean allowDuplicateColumnNames, Table... tables) {
        Table joined = table;
        for (Table table2 : tables) {
            joined = rightOuter(table2, allowDuplicateColumnNames, columnNames);
        }
        return joined;
    }

    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2   The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     *                 rounding to integers.
     * @return The resulting table
     */
    public Table rightOuter(Table table2, String col2Name) {
        return rightOuter(table2, false, col2Name);
    }

    /**
     * Joins the joiner to the table2, using the given columns for the second table and returns the resulting table
     *
     * @param table2    The table to join with
     * @param col2Names The columns to join on. If a name refers to a double column, the join is performed after
     *                  rounding to integers.
     * @return The resulting table
     */
    public Table rightOuter(Table table2, String[] col2Names) {
        return rightOuter(table2, false, col2Names);
    }

    /**
     * Joins the joiner to the table2, using the given columns for the second table and returns the resulting table
     *
     * @param table2   The table to join with
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed
     * @param col2Names The columns to join on. If a name refers to a double column, the join is performed after
     *                 rounding to integers.
     * @return The resulting table
     */

    public Table rightOuter(Table table2, boolean allowDuplicateColumnNames, String... col2Names) {
        Table leftOuter = table2.joinOn(col2Names).leftOuter(table, allowDuplicateColumnNames, columnNames);

        // reverse the columns
        Table result = Table.create(leftOuter.name());
        // loop on table that was originally first (left) and add the left-joined matching columns by name
        for (String name : table.columnNames()) {
            try {
                result.addColumns(leftOuter.column(name));
            } catch (IllegalStateException e) {
                // Can ignore this exception as it is anticipated.
                // NOTE: DataFrameJoiner.rightOuter(): skipping left table's column,'"
                //     +name+"', in favor of right table's matching column that was kept in join operation.");
            }
        }
        for (String name : table2.columnNames()) {
            if (!result.columnNames().stream().anyMatch(name::equalsIgnoreCase)) {
                result.addColumns(leftOuter.column(name));
            }
        }
        return result;
    }

    private Table emptyTableFromColumns(Table table1, Table table2, String... col2Names) {
        Column<?>[] cols = Streams.concat(
                table1.columns().stream(),
                table2.columns().stream().filter(c -> !Arrays.asList(col2Names).stream().anyMatch(c.name()::equalsIgnoreCase))
                ).map(Column::emptyCopy).toArray(Column[]::new);
        return Table.create(table1.name(), cols);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void crossProduct(Table destination, Table table1, Table table2) {
        for (int c = 0; c < table1.columnCount() + table2.columnCount(); c++) {
            for (int r1 = 0; r1 < table1.rowCount(); r1++) {
                for (int r2 = 0; r2 < table2.rowCount(); r2++) {
                    if (c < table1.columnCount()) {
                        Column t1Col = table1.column(c);
                        destination.column(c).append(t1Col, r1);
                    } else {
                        Column t2Col = table2.column(c - table1.columnCount());
                        destination.column(c).append(t2Col, r2);
                    }
                }
            }
        }
    }

    /**
     * Adds rows to destination for each row in table1, with the columns from table2 added as missing values in each
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void withMissingLeftJoin(Table destination, Table table1) {
        for (int c = 0; c < destination.columnCount(); c++) {
            if (c < table1.columnCount()) {
                Column t1Col = table1.column(c);
                destination.column(c).append(t1Col);
            } else {
                for (int r1 = 0; r1 < table1.rowCount(); r1++) {
                    destination.column(c).appendMissing();
                }
            }
        }
    }

    /**
     * Adds rows to destination for each row in the joinColumn and table2
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void withMissingRightJoin(Table destination, List<Column<?>> joinColumns, Table table2) {
        int t2StartCol = destination.columnCount() - table2.columnCount();
        for (int c = 0; c < destination.columnCount(); c++) {
            boolean addedJoinColumns = false;
            for (Column joinColumn : joinColumns) {
                if (destination.column(c).name().equalsIgnoreCase(joinColumn.name())) {
                    destination.column(c).append(joinColumn);
                    addedJoinColumns = true;
                }
            }
            if (!addedJoinColumns) {
                if (c < t2StartCol) {
                    for (int r2 = 0; r2 < table2.rowCount(); r2++) {
                        destination.column(c).appendMissing();
                    }
                } else {
                    Column t2Col = table2.column(c - t2StartCol);
                    destination.column(c).append(t2Col);
                }
            }
        }
    }

}
