package tech.tablesaw.joining;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.common.primitives.Ints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
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
import tech.tablesaw.selection.Selection;

public class DataFrameJoiner {

    private enum JoinType {
        INNER,
        LEFT_OUTER,
        FULL_OUTER;
    }

    private static final String TABLE_ALIAS = "T";

    private final Table table;
    private Column<?>[] joinColumns;
    private final String[] columnNames;
    private final List<Integer> joinColumnIndexes;
    private AtomicInteger joinTableId = new AtomicInteger(2);

    /**
     * Constructor.
     *
     * @param table The table to join on
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
        this.joinColumnIndexes = getJoinIndexes(table, columnNames);
    }

    private List<Integer> getJoinIndexes(Table table, String[] columnNames) {
        return Arrays.stream(columnNames).map(table::columnIndex).collect(Collectors.toList());
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
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column
     * have the same name if {@code true} the join will succeed and duplicate columns are renamed*
     * @param tables The tables to join with
     */
    public Table inner(boolean allowDuplicateColumnNames, Table... tables) {
        Table joined = table;

        for (Table currT : tables) {
            joined = joinInternal(joined, currT, JoinType.INNER, allowDuplicateColumnNames, columnNames);
        }
        return joined;
    }

    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2 The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     * rounding to integers.
     * @return The resulting table
     */
    public Table inner(Table table2, String col2Name) {
        return inner(table2, false, col2Name);
    }

    /**
     * Joins the joiner to the table2, using the given columns for the second table and returns the resulting table
     *
     * @param table2 The table to join with
     * @param col2Names The columns to join on. If a name refers to a double column, the join is performed after
     * rounding to integers.
     * @return The resulting table
     */
    public Table inner(Table table2, String[] col2Names) {
        return inner(table2, false, col2Names);
    }

    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2 The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     * rounding to integers.
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column
     * have the same name if {@code true} the join will succeed and duplicate columns are renamed*
     * @return The resulting table
     */
    public Table inner(Table table2, String col2Name, boolean allowDuplicateColumnNames) {
        return inner(table2, allowDuplicateColumnNames, col2Name);
    }

    /**
     * Joins the joiner to the table2, using the given columns for the second table and returns the resulting table
     *
     * @param table2 The table to join with
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column
     * have the same name if {@code true} the join will succeed and duplicate columns are renamed*
     * @param col2Names The columns to join on. If a name refers to a double column, the join is performed after
     * rounding to integers.
     * @return The resulting table
     */
    public Table inner(Table table2, boolean allowDuplicateColumnNames, String... col2Names) {
        Table joinedTable;
        joinedTable = joinInternal(table, table2, JoinType.INNER, allowDuplicateColumnNames, col2Names);
        return joinedTable;
    }

    /**
     * Joins the joiner to the table2, using the given columns for the second table and returns the resulting table
     *
     * @param table2 The table to join with
     * @param outer True if this join is actually an outer join, left or right or full, otherwise false.
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column
     * have the same name if {@code true} the join will succeed and duplicate columns are renamed*
     * @param col2Names The columns to join on. If a name refers to a double column, the join is performed after
     * rounding to integers.
     * @return The resulting table
     */
    @Deprecated
    public Table inner(Table table2, boolean outer, boolean allowDuplicateColumnNames, String... col2Names) {
        JoinType joinType = JoinType.INNER;
        if (outer) {
            joinType = JoinType.LEFT_OUTER;
        }

        Table joinedTable;
        joinedTable = joinInternal(table, table2, joinType, allowDuplicateColumnNames, col2Names);
        return joinedTable;
    }

    private Table joinInternal(Table table1, Table table2, JoinType joinType, boolean allowDuplicates,
        String... col2Names) {
        List<Integer> col2Indexes = getJoinIndexes(table2, col2Names);
        Set<Integer> col2IndexSet = ImmutableSet.copyOf(col2Indexes);
        if (allowDuplicates) {
            renameColumnsWithDuplicateNames(table1, table2, col2Names);
        }
        Table result = emptyTableFromColumns(table1, table2, col2Names);


        // Build a reverse index for every join column in both tables.
        List<Index> table1Indexes = joinColumnIndexes.stream().map(index -> indexFor(table1, index))
            .collect(Collectors.toList());
        List<Index> table2Indexes = col2Indexes.stream().map(index -> indexFor(table2, index))
            .collect(Collectors.toList());

        Selection table1DoneSelection = Selection.with();
        Selection table2DoneSelection = Selection.with();
        for (Row row : table1) {
            int ri = row.getRowNumber();
            if (table1DoneSelection.contains(ri)) {
                // Already processed a selection of table1 that contained this row.
                continue;
            }

            Selection table1Rows = createMultiColSelection(table1, ri, table1Indexes, table1.rowCount());
            Selection table2Rows = createMultiColSelection(table1, ri, table2Indexes, table2.rowCount());

            if (joinType != JoinType.INNER && table2Rows.isEmpty()) {
                withMissingLeftJoin(result, table1.where(table1Rows));
            } else {
                crossProduct(result, table1, table2, table1Rows, table2Rows, col2IndexSet);
            }

            table1DoneSelection = table1DoneSelection.or(table1Rows);
            if (joinType == JoinType.FULL_OUTER) {
                // Update done rows in table2 for full Outer.
                table2DoneSelection = table2DoneSelection.or(table2Rows);
            } else if (table1DoneSelection.size() == table1.rowCount()) {
                // Processed all the rows in table1 exit early.
                return result;
            }
        }

        // Add all rows from table2 that were not copied over.
        Preconditions.checkState(joinType == JoinType.FULL_OUTER,
            "if got this far. Should be full outer");
        Selection table2OnlySelection = table2DoneSelection.flip(0, table2.rowCount());
        Table table2OnlyRows = table2.where(table2OnlySelection);
        List<Column<?>> joinColumns = table2OnlyRows.columns(col2Names);
        table2OnlyRows.removeColumns(col2Names);
        withMissingRightJoin(result, joinColumns, table2OnlyRows);
        return result;
    }

    private Index indexFor(Table table, int colIndex) {
        ColumnType type = table.column(colIndex).type();
        if (type instanceof DateColumnType) {
            return new IntIndex(table.dateColumn(colIndex));
        }
        if (type instanceof DateTimeColumnType) {
            return new LongIndex(table.dateTimeColumn(colIndex));
        }
        if (type instanceof InstantColumnType) {
            return new LongIndex(table.instantColumn(colIndex));
        }
        if (type instanceof TimeColumnType) {
            return new IntIndex(table.timeColumn(colIndex));
        }
        if (type instanceof StringColumnType || type instanceof TextColumnType) {
            return new StringIndex(table.stringColumn(colIndex));
        }
        if (type instanceof IntColumnType) {
            return new IntIndex(table.intColumn(colIndex));
        }
        if (type instanceof LongColumnType) {
            return new LongIndex(table.longColumn(colIndex));
        }
        if (type instanceof ShortColumnType) {
            return new ShortIndex(table.shortColumn(colIndex));
        }
        if (type instanceof BooleanColumnType) {
            return new ByteIndex(table.booleanColumn(colIndex));
        }
        if (type instanceof DoubleColumnType) {
            return new DoubleIndex(table.doubleColumn(colIndex));
        }
        if (type instanceof FloatColumnType) {
            return new FloatIndex(table.floatColumn(colIndex));
        }
        throw new IllegalArgumentException(
            "Joining attempted on unsupported column type " + type);
    }

    private Selection selectionForColumn(
        Column<?> valueColumn,
        int rowIndex,
        Index rawIndex) {

        ColumnType type = valueColumn.type();
        // relies on both arrays, columns, and col2Names,
        // having corresponding values at same index
        Selection selection = Selection.with();
        if (type instanceof DateColumnType) {
            IntIndex index = (IntIndex) rawIndex;
            DateColumn typedValueColumn = (DateColumn) valueColumn;
            int value = typedValueColumn.getIntInternal(rowIndex);
            selection = index.get(value);
        } else if (type instanceof TimeColumnType) {
            IntIndex index = (IntIndex) rawIndex;
            TimeColumn typedValueColumn = (TimeColumn) valueColumn;
            int value = typedValueColumn.getIntInternal(rowIndex);
            selection = index.get(value);
        } else if (type instanceof DateTimeColumnType) {
            LongIndex index = (LongIndex) rawIndex;
            DateTimeColumn typedValueColumn = (DateTimeColumn) valueColumn;
            long value = typedValueColumn.getLongInternal(rowIndex);
            selection = index.get(value);
        } else if (type instanceof InstantColumnType) {
            LongIndex index = (LongIndex) rawIndex;
            InstantColumn typedValueColumn = (InstantColumn) valueColumn;
            long value = typedValueColumn.getLongInternal(rowIndex);
            selection = index.get(value);
        } else if (type instanceof StringColumnType || type instanceof TextColumnType) {
            StringIndex index = (StringIndex) rawIndex;
            StringColumn typedValueColumn = (StringColumn) valueColumn;
            String value = typedValueColumn.get(rowIndex);
            selection = index.get(value);
        } else if (type instanceof IntColumnType) {
            IntIndex index = (IntIndex) rawIndex;
            IntColumn typedValueColumn = (IntColumn) valueColumn;
            int value = typedValueColumn.getInt(rowIndex);
            selection = index.get(value);
        } else if (type instanceof LongColumnType) {
            LongIndex index = (LongIndex) rawIndex;
            LongColumn typedValueColumn = (LongColumn) valueColumn;
            long value = typedValueColumn.getLong(rowIndex);
            selection = index.get(value);
        } else if (type instanceof ShortColumnType) {
            ShortIndex index = (ShortIndex) rawIndex;
            ShortColumn typedValueColumn = (ShortColumn) valueColumn;
            short value = typedValueColumn.getShort(rowIndex);
            selection = index.get(value);
        } else if (type instanceof BooleanColumnType) {
            ByteIndex index = (ByteIndex) rawIndex;
            BooleanColumn typedValueColumn = (BooleanColumn) valueColumn;
            byte value = typedValueColumn.getByte(rowIndex);
            selection = index.get(value);
        } else if (type instanceof DoubleColumnType) {
            DoubleIndex index = (DoubleIndex) rawIndex;
            DoubleColumn typedValueColumn = (DoubleColumn) valueColumn;
            double value = typedValueColumn.getDouble(rowIndex);
            selection = index.get(value);
        } else if (type instanceof FloatColumnType) {
            FloatIndex index = (FloatIndex) rawIndex;
            FloatColumn typedValueColumn = (FloatColumn) valueColumn;
            float value = typedValueColumn.getFloat(rowIndex);
            selection = index.get(value);
        } else {
            throw new IllegalArgumentException(
                "Joining is supported on numeric, string, and date-like columns. Column "
                    + valueColumn.name() + " is of type " + valueColumn.type());
        }
        return selection;
    }

    private Selection createMultiColSelection(Table table1, int ri, List<Index> indexes, int size) {
        Selection multiColSelection = Selection.withRange(0, size);
        for (int i = 0; i < joinColumns.length; i++) {
            // Need to use the column from table1 that is the same column originally
            // defined for this DataFrameJoiner. Column names must be unique within the
            // same table, so use the original column's name to get the corresponding
            // column out of the table1 input Table.
            Column<?> col = table1.column(joinColumns[i].name());
            Selection oneColSelection = selectionForColumn(col, ri, indexes.get(i));
            multiColSelection = multiColSelection.and(oneColSelection);
        }
        return multiColSelection;
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
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column
     * have the same name if {@code true} the join will succeed and duplicate columns are renamed*
     * @param tables The tables to join with
     * @return The resulting table
     */
    public Table fullOuter(boolean allowDuplicateColumnNames, Table... tables) {
        Table joined = table;

        for (Table currT : tables) {
            joined = joinInternal(joined, currT, JoinType.FULL_OUTER, allowDuplicateColumnNames, columnNames);
        }
        return joined;
    }

    /**
     * Full outer join the joiner to the table2, using the given column for the second table and returns the resulting
     * table
     *
     * @param table2 The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     * rounding to integers.
     * @return The resulting table
     */
    public Table fullOuter(Table table2, String col2Name) {
        return joinInternal(table, table2, JoinType.FULL_OUTER, false, col2Name);
    }

    /*
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
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column
     * have the same name if {@code true} the join will succeed and duplicate columns are renamed*
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
     * @param table2 The table to join with
     * @param col2Names The columns to join on. If a name refers to a double column, the join is performed after
     * rounding to integers.
     * @return The resulting table
     */
    public Table leftOuter(Table table2, String[] col2Names) {
        return leftOuter(table2, false, col2Names);
    }

    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2 The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     * rounding to integers.
     * @return The resulting table
     */
    public Table leftOuter(Table table2, String col2Name) {
        return leftOuter(table2, false, col2Name);
    }

    /**
     * Joins the joiner to the table2, using the given columns for the second table and returns the resulting table
     *
     * @param table2 The table to join with
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column
     * have the same name if {@code true} the join will succeed and duplicate columns are renamed
     * @param col2Names The columns to join on. If a name refers to a double column, the join is performed after
     * rounding to integers.
     * @return The resulting table
     */
    public Table leftOuter(Table table2, boolean allowDuplicateColumnNames, String... col2Names) {
        return joinInternal(table, table2, JoinType.LEFT_OUTER, allowDuplicateColumnNames, col2Names);
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
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column
     * have the same name if {@code true} the join will succeed and duplicate columns are renamed
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
     * @param table2 The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     * rounding to integers.
     * @return The resulting table
     */
    public Table rightOuter(Table table2, String col2Name) {
        return rightOuter(table2, false, col2Name);
    }

    /**
     * Joins the joiner to the table2, using the given columns for the second table and returns the resulting table
     *
     * @param table2 The table to join with
     * @param col2Names The columns to join on. If a name refers to a double column, the join is performed after
     * rounding to integers.
     * @return The resulting table
     */
    public Table rightOuter(Table table2, String[] col2Names) {
        return rightOuter(table2, false, col2Names);
    }

    /**
     * Joins the joiner to the table2, using the given columns for the second table and returns the resulting table
     *
     * @param table2 The table to join with
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column
     * have the same name if {@code true} the join will succeed and duplicate columns are renamed
     * @param col2Names The columns to join on. If a name refers to a double column, the join is performed after
     * rounding to integers.
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
    private void crossProduct(Table destination, Table table1, Table table2,
        Selection table1Rows, Selection table2Rows, Set<Integer> col2Indexes) {
        // Skip adding the join columns a second time.
        int skippedJoinColumns = 0;
        for (int c = 0; c < table1.columnCount() + table2.columnCount(); c++) {
            int table2Index = c - table1.columnCount();
            if (c >= table1.columnCount() && col2Indexes.contains(table2Index)) {
                skippedJoinColumns++;
                continue;
            }
            for (int r1 : table1Rows) {
                for (int r2 : table2Rows) {
                    if (c < table1.columnCount()) {
                        Column t1Col = table1.column(c);
                        destination.column(c).append(t1Col, r1);
                    } else {
                        Column t2Col = table2.column(table2Index);
                        destination.column(c - skippedJoinColumns).append(t2Col, r2);
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
