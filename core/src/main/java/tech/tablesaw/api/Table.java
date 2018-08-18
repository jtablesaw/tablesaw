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

package tech.tablesaw.api;

import static tech.tablesaw.aggregate.AggregateFunctions.countMissing;
import static tech.tablesaw.selection.Selection.selectNRowsAtRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.aggregate.CrossTab;
import tech.tablesaw.aggregate.Summarizer;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.DataFrameReader;
import tech.tablesaw.io.DataFrameWriter;
import tech.tablesaw.io.html.HtmlTableWriter;
import tech.tablesaw.joining.DataFrameJoiner;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;
import tech.tablesaw.sorting.Sort;
import tech.tablesaw.sorting.SortUtils;
import tech.tablesaw.sorting.comparators.IntComparatorChain;
import tech.tablesaw.table.Relation;
import tech.tablesaw.table.Rows;
import tech.tablesaw.table.StandardTableSliceGroup;
import tech.tablesaw.table.TableSliceGroup;

/**
 * A table of data, consisting of some number of columns, each of which has the same number of rows.
 * All the data in a column has the same type: integer, float, category, etc., but a table may contain an arbitrary
 * number of columns of any type.
 * <p>
 * Tables are the main data-type and primary focus of Airframe.
 */
public class Table extends Relation implements Iterable<Row> {

    /**
     * The columns that hold the data in this table
     */
    private final List<Column<?>> columnList = new ArrayList<>();
    /**
     * The name of the table
     */
    private String name;

    /**
     * Returns a new table initialized with the given name
     */
    private Table(String name) {
        this.name = name;
    }

    /**
     * Returns a new Table initialized with the given names and columns
     *
     * @param name    The name of the table
     * @param columns One or more columns, all of which must have either the same length or size 0
     */
    protected Table(String name, Column<?>... columns) {
        this(name);
        for (final Column<?> column : columns) {
            this.addColumns(column);
        }
    }

    /**
     * Returns a new, empty table (without rows or columns) with the given name
     */
    public static Table create(String tableName) {
        return new Table(tableName);
    }

    /**
     * Returns a new table with the given columns and given name
     *
     * @param columns One or more columns, all of the same @code{column.size()}
     */
    public static Table create(final String tableName, final Column<?>... columns) {
        return new Table(tableName, columns);
    }

    /**
     * Returns a sort Key that can be used for simple or chained comparator sorting
     * <p>
     * You can extend the sort key by using .next() to fill more columns to the sort order
     */
    private static Sort first(String columnName, Sort.Order order) {
        return Sort.on(columnName, order);
    }

    /**
     * Returns an object that can be used to sort this table in the order specified for by the given column names
     */
    private static Sort getSort(String... columnNames) {
        Sort key = null;
        for (String s : columnNames) {
            if (key == null) {
                key = first(s, Sort.Order.DESCEND);
            } else {
                key.next(s, Sort.Order.DESCEND);
            }
        }
        return key;
    }

    public static DataFrameReader read() {
        return new DataFrameReader();
    }

    public DataFrameWriter write() {
        return new DataFrameWriter(this);
    }

    /**
     * Adds the given column to this table
     */
    @Override
    public Table addColumns(final Column<?>... cols) {
        for (final Column<?> c : cols) {
            validateColumn(c);
            columnList.add(c);
        }
        return this;
    }

    /**
     * Throws an IllegalArgumentException if a column with the given name is already in the table
     */
    private void validateColumn(final Column<?> newColumn) {
        Preconditions.checkNotNull(newColumn, "Attempted to add a null to the columns in table " + name);
        List<String> stringList = new ArrayList<>();
        for (String name : columnNames()) {
            stringList.add(name.toLowerCase());
        }
        if (stringList.contains(newColumn.name().toLowerCase())) {
            String message = String.format("Cannot add column with duplicate name %s to table %s", newColumn, name);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Adds the given column to this table at the given position in the column list
     *
     * @param index  Zero-based index into the column list
     * @param column Column to be added
     */
    public Table insertColumn(int index, Column<?> column) {
        validateColumn(column);
        columnList.add(index, column);
        return this;
    }

    /**
     * Replaces an existing column (by index) in this table with the given new column
     *
     * @param colIndex  Zero-based index of the column to be replaced
     * @param newColumn Column to be added
     */
    public Table replaceColumn(final int colIndex, final Column<?> newColumn) {
        removeColumns(column(colIndex));
        insertColumn(colIndex, newColumn);
        return this;
    }

    /**
     * Replaces an existing column (by name) in this table with the given new column
     *
     * @param columnName String name of the column to be replaced
     * @param newColumn  Column to be added
     */
    public Table replaceColumn(final String columnName, final Column<?> newColumn) {
        int colIndex = columnIndex(columnName);
        replaceColumn(colIndex, newColumn);
        return this;
    }

    /**
     * Sets the name of the table
     */
    @Override
    public Table setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Returns the column at the given index in the column list
     *
     * @param columnIndex an integer at least 0 and less than number of columns in the table
     */
    @Override
    public Column<?> column(int columnIndex) {
        return columnList.get(columnIndex);
    }

    /**
     * Returns the number of columns in the table
     */
    @Override
    public int columnCount() {
        return columnList.size();
    }

    /**
     * Returns the number of rows in the table
     */
    @Override
    public int rowCount() {
        int result = 0;
        if (!columnList.isEmpty()) {
            // all the columns have the same number of elements, so we can check any of them
            result = columnList.get(0).size();
        }
        return result;
    }

    /**
     * Returns the list of columns
     */
    @Override
    public List<Column<?>> columns() {
        return columnList;
    }

    public Column<?>[] columnArray() {
        return columnList.toArray(new Column<?>[columnCount()]);
    }

    /**
     * Returns only the columns whose names are given in the input array
     */
    public List<CategoricalColumn<?>> categoricalColumns(String... columnNames) {
        List<CategoricalColumn<?>> columns = new ArrayList<>();
        for (String columnName : columnNames) {
            columns.add(categoricalColumn(columnName));
        }
        return columns;
    }

    /**
     * Returns the index of the column with the given name
     *
     * @throws IllegalArgumentException if the input string is not the name of any column in the table
     */
    public int columnIndex(String columnName) {
        int columnIndex = -1;
        for (int i = 0; i < columnList.size(); i++) {
            if (columnList.get(i).name().equalsIgnoreCase(columnName)) {
                columnIndex = i;
                break;
            }
        }
        if (columnIndex == -1) {
            throw new IllegalArgumentException(String.format("Column %s is not present in table %s", columnName, name));
        }
        return columnIndex;
    }

    /**
     * Returns the index of the given column (its position in the list of columns)
     * <p>
     *
     * @throws IllegalArgumentException if the column is not present in this table
     */
    public int columnIndex(Column<?> column) {
        int columnIndex = -1;
        for (int i = 0; i < columnList.size(); i++) {
            if (columnList.get(i).equals(column)) {
                columnIndex = i;
                break;
            }
        }
        if (columnIndex == -1) {
            throw new IllegalArgumentException(
                    String.format("Column %s is not present in table %s", column.name(), name));
        }
        return columnIndex;
    }

    /**
     * Returns the name of the table
     */
    @Override
    public String name() {
        return name;
    }

    /**
     * Returns a List of the names of all the columns in this table
     */
    public List<String> columnNames() {
        List<String> names = new ArrayList<>(columnList.size());
        names.addAll(columnList.stream().map(Column::name).collect(Collectors.toList()));
        return names;
    }

    /**
     * Returns a string representation of the value at the given row and column indexes
     *
     * @param r the row index, 0 based
     * @param c the column index, 0 based
     */
    @Override
    public String get(int r, int c) {
        Column<?> column = column(c);
        return column.getString(r);
    }

    /**
     * Returns a string representation of the value at the given row and column indexes
     *
     * @param r the row index, 0 based
     * @param c the column index, 0 based
     */
    @Override
    public String getUnformatted(int r, int c) {
        Column<?> column = column(c);
        return column.getUnformattedString(r);
    }

    /**
     * Returns a string representation of the value at the given row and column indexes
     *
     * @param r          the row index, 0 based
     * @param columnName the name of the column to be returned
     *                   <p>
     *                   // TODO: performance would be enhanced if columns could be referenced via a hashTable
     */
    public String get(int r, String columnName) {
        Column<?> column = column(columnIndex(columnName));
        return column.getString(r);
    }

    /**
     * Returns a table with the same columns as this table
     */
    public Table copy() {
        Table copy = new Table(name);
        for (Column<?> column : columnList) {
            copy.addColumns(column.emptyCopy());
        }

        IntArrayList integers = new IntArrayList();
        for (int i = 0; i < rowCount(); i++) {
            integers.add(i);
        }
        Rows.copyRowsToTable(integers, this, copy);
        return copy;
    }

    /**
     * Returns a table with the same columns as this table, but no data
     */
    public Table emptyCopy() {
        Table copy = new Table(name);
        for (Column<?> column : columnList) {
            copy.addColumns(column.emptyCopy());
        }
        return copy;
    }

    /**
     * Returns a table with the same columns as this table, but no data, initialized to the given row size
     */
    public Table emptyCopy(int rowSize) {
        Table copy = new Table(name);
        for (Column<?> column : columnList) {
            copy.addColumns(column.emptyCopy(rowSize));
        }
        return copy;
    }

    /**
     * Splits the table into two, randomly assigning records to each according to the proportion given in
     * trainingProportion
     *
     * @param table1Proportion The proportion to go in the first table
     * @return An array two tables, with the first table having the proportion specified in the method parameter,
     * and the second table having the balance of the rows
     */
    public Table[] sampleSplit(double table1Proportion) {
        Table[] tables = new Table[2];
        int table1Count = (int) Math.round(rowCount() * table1Proportion);

        Selection table2Selection = new BitmapBackedSelection();
        for (int i = 0; i < rowCount(); i++) {
            table2Selection.add(i);
        }
        Selection table1Selection = new BitmapBackedSelection();

        Selection table1Records = Selection.selectNRowsAtRandom(table1Count, rowCount());
        for (int table1Record : table1Records) {
            table1Selection.add(table1Record);
        }
        table2Selection.andNot(table1Selection);
        tables[0] = where(table1Selection);
        tables[1] = where(table2Selection);
        return tables;
    }

    /**
     * Returns a table consisting of randomly selected records from this table. The sample size is based on the
     * given proportion
     *
     * @param proportion The proportion to go in the sample
     */
    public Table sampleX(double proportion) {
        Preconditions.checkArgument(proportion <= 1 && proportion >= 0,
                "The sample proportion must be between 0 and 1");

        int tableSize = (int) Math.round(rowCount() * proportion);
        return where(selectNRowsAtRandom(tableSize, rowCount()));
    }

    /**
     * Returns a table consisting of randomly selected records from this table
     *
     * @param nRows The number of rows to go in the sample
     */
    public Table sampleN(int nRows) {
        Preconditions.checkArgument(nRows > 0 && nRows < rowCount(),
                "The number of rows sampled must be greater than 0 and less than the number of rows in the table.");
        return where(selectNRowsAtRandom(nRows, rowCount()));
    }

    /**
     * Clears all the data from this table
     */
    @Override
    public void clear() {
        columnList.forEach(Column::clear);
    }

    /**
     * Returns a new table containing the first {@code nrows} of data in this table
     */
    public Table first(int nRows) {
        int newRowCount = Math.min(nRows, rowCount());
        return inRange(0, newRowCount);
    }

    /**
     * Returns a new table containing the last {@code nrows} of data in this table
     */
    public Table last(int nRows) {
        int newRowCount = Math.min(nRows, rowCount());
        return inRange(rowCount() - newRowCount, rowCount());
    }

    /**
     * Sorts this table into a new table on the columns indexed in ascending order
     * <p>
     * TODO(lwhite): Rework this so passing an negative number does a descending sort
     */
    public Table sortOn(int... columnIndexes) {
        List<String> names = new ArrayList<>();
        for (int i : columnIndexes) {
            names.add(columnList.get(i).name());
        }
        return sortOn(names.toArray(new String[names.size()]));
    }

    /**
     * Returns a copy of this table sorted on the given column names, applied in order,
     * <p>
     * if column name starts with - then sort that column descending otherwise sort ascending
     */
    public Table sortOn(String... columnNames) {

        Sort key = null;
        List<String> names = new ArrayList<>();
        for (String name : columnNames()) {
            names.add(name.toUpperCase());
        }

        for (String columnName : columnNames) {
            Sort.Order order;
            if (names.contains(columnName.toUpperCase())) {
                // the column name has not been annotated with a prefix.
                order = Sort.Order.ASCEND;
            } else {

                // get the prefix which could be - or +
                String prefix = columnName.substring(0, 1);

                // remove - prefix so provided name matches actual column name
                columnName = columnName.substring(1, columnName.length());

                switch (prefix) {
                    case "+":
                        order = Sort.Order.ASCEND;
                        break;
                    case "-":
                        order = Sort.Order.DESCEND;
                        break;
                    default:
                        throw new IllegalStateException("Column prefix: " + prefix + " is unknown.");
                }
            }

            if (key == null) { // key will be null the first time through
                key = first(columnName, order);
            } else {
                key.next(columnName, order);
            }
        }
        return sortOn(key);
    }

    /**
     * Returns a copy of this table sorted in the order of the given column names, in ascending order
     */
    public Table sortAscendingOn(String... columnNames) {
        return this.sortOn(columnNames);
    }

    /**
     * Returns a copy of this table sorted on the given column names, applied in order, descending
     * TODO: Provide equivalent methods naming columns by index
     */
    public Table sortDescendingOn(String... columnNames) {
        Sort key = getSort(columnNames);
        return sortOn(key);
    }

    /**
     */
    public Table sortOn(Sort key) {
        Preconditions.checkArgument(!key.isEmpty());
        if (key.size() == 1) {
            IntComparator comparator = SortUtils.getComparator(this, key);
            return sortOn(comparator);
        }
        IntComparatorChain chain = SortUtils.getChain(this, key);
        return sortOn(chain);
    }

    /**
     * Returns a copy of this table sorted using the given comparator
     */
    private Table sortOn(IntComparator rowComparator) {
        Table newTable = emptyCopy(rowCount());

        int[] newRows = rows();
        IntArrays.parallelQuickSort(newRows, rowComparator);

        Rows.copyRowsToTable(IntArrayList.wrap(newRows), this, newTable);
        return newTable;
    }

    /**
     * Returns a copy of this table sorted using the given comparator
     */
    public Table sortOn(Comparator<Row> rowComparator) {
        Row row1 = new Row(this);
        Row row2 = new Row(this);
        return sortOn(new IntComparator() {
            @Override
            public int compare(int k1, int k2) {
                row1.at(k1);
                row2.at(k2);
                return rowComparator.compare(row1, row2);
            }
        });
    }

    /**
     * Returns an array of ints of the same number of rows as the table
     */
    private int[] rows() {
        int[] rowIndexes = new int[rowCount()];
        for (int i = 0; i < rowCount(); i++) {
            rowIndexes[i] = i;
        }
        return rowIndexes;
    }

    /**
     * Adds a single row to this table from sourceTable, copying every column in sourceTable
     *
     * @param rowIndex      The row in sourceTable to add to this table
     * @param sourceTable   A table with the same column structure as this table
     */
    public void addRow(int rowIndex, Table sourceTable) {
        for (int i = 0; i < columnCount(); i++) {
            column(i).appendObj(sourceTable.column(i).get(rowIndex));
        }
    }
    
    public void addRow(Row row) {
        for (int i = 0; i < row.columnCount(); i++) {
            column(i).appendObj(row.getObject(i));
        }
    }

    public Table rows(int... rowNumbers) {
        Preconditions.checkArgument(Ints.max(rowNumbers) <= rowCount());
        return where(Selection.with(rowNumbers));
    }

    public Table dropRows(int... rowNumbers) {
        Preconditions.checkArgument(Ints.max(rowNumbers) <= rowCount());
        Selection selection = Selection.withRange(0, rowCount())
                .andNot(Selection.with(rowNumbers));
        return where(selection);
    }

    public Table inRange(int rowStart, int rowEnd) {
        Preconditions.checkArgument(rowEnd <= rowCount());
        return where(Selection.withRange(rowStart, rowEnd));
    }

    public Table dropRange(int rowStart, int rowEnd) {
        Preconditions.checkArgument(rowEnd <= rowCount());
        return where(Selection.withoutRange(0, rowCount(), rowStart, rowEnd));
    }

    public Table where(Selection selection) {
        Table newTable = this.emptyCopy(selection.size());
        Rows.copyRowsToTable(selection, this, newTable);
        return newTable;
    }

    public Table dropWhere(Selection selection) {
        Selection opposite = new BitmapBackedSelection();
        opposite.addRange(0, rowCount());
        opposite.andNot(selection);
        Table newTable = this.emptyCopy(opposite.size());
        Rows.copyRowsToTable(opposite, this, newTable);
        return newTable;
    }

    /**
     * Returns a non-overlapping and exhaustive collection of "slices" over this table.
     * Each slice is like a virtual table containing a subset of the records in this table
     *
     * This method is intended for advanced or unusual operations on the subtables.
     * If you want to calculate summary statistics for each subtable, the summarize methods (e.g)
     *
     * table.summarize(myColumn, mean, median).by(columns)
     *
     * are preferred
     */
    public TableSliceGroup splitOn(String... columns) {
        return splitOn(categoricalColumns(columns).toArray(new CategoricalColumn<?>[columns.length]));
    }

    /**
     * Returns a non-overlapping and exhaustive collection of "slices" over this table.
     * Each slice is like a virtual table containing a subset of the records in this table
     *
     * This method is intended for advanced or unusual operations on the subtables.
     * If you want to calculate summary statistics for each subtable, the summarize methods (e.g)
     *
     * table.summarize(myColumn, mean, median).by(columns)
     *
     * are preferred
     */
    public TableSliceGroup splitOn(CategoricalColumn<?>... columns) {
        return StandardTableSliceGroup.create(this, columns);
    }

    public String printHtml() {
        return HtmlTableWriter.write(this);
    }

    public Table structure() {
        Table t = new Table("Structure of " + name());
        //NumberColumn index = NumberColumn.create("Index", columnCount());
        IntColumn index = IntColumn.indexColumn("Index", columnCount(), 0);
        StringColumn columnName = StringColumn.create("Column Name", columnCount());
        StringColumn columnType = StringColumn.create("Column Type", columnCount());
        t.addColumns(index);
        t.addColumns(columnName);
        t.addColumns(columnType);
        columnName.addAll(columnNames());
        for (int i = 0; i < columnCount(); i++) {
            Column<?> column = columnList.get(i);
            columnType.append(column.type().name());
        }
        return t;
    }

    /**
     * Returns the unique records in this table
     * Note: Uses a lot of memory for a sort
     */
    public Table dropDuplicateRows() {

        Table sorted = this.sortOn(columnNames().toArray(new String[columns().size()]));
        Table temp = emptyCopy();

        for (int row = 0; row < rowCount(); row++) {
            if (temp.isEmpty() || !Rows.compareRows(row, sorted, temp)) {
                Rows.appendRowToTable(row, sorted, temp);
            }
        }
        return temp;
    }



    /**
     * Returns only those records in this table that have no columns with missing values
     */
    public Table dropRowsWithMissingValues() {

        Table temp = emptyCopy();
        for (int row = 0; row < rowCount(); row++) {
            boolean add = true;
            for (int col = 0; col < columnCount(); col++) {
                Column<?> c = column(col);
                if (c.isMissing(row)) {
                    add = false;
                    break;
                }
            }
            if (add) {
                Rows.appendRowToTable(row, this, temp);
            }
        }
        return temp;
    }

    public Table select(Column<?>... columns) {
        return new Table(this.name, columns);
    }

    public Table select(String... columnNames) {
        return Table.create(this.name, columns(columnNames).toArray(new Column<?>[0]));
    }

    /**
     * Removes the given columns
     */
    @Override
    public Table removeColumns(Column<?>... columns) {
        columnList.removeAll(Arrays.asList(columns));
        return this;
    }

    /**
     * Removes the given columns with missing values
     */
    public Table removeColumnsWithMissingValues() {
        removeColumns(columnList.stream().filter(x -> x.countMissing() > 0).toArray(Column<?>[]::new));
        return this;
    }

    /**
     * Removes all columns except for those given in the argument from this table
     */
    public Table retainColumns(Column<?>... columns) {
        List<Column<?>> retained = Arrays.asList(columns);
        columnList.clear();
        columnList.addAll(retained);
        return this;
    }

    /**
     * Removes all columns except for those given in the argument from this table
     */
    public Table retainColumns(String... columnNames) {
        List<Column<?>> retained = columns(columnNames);
        columnList.clear();
        columnList.addAll(retained);
        return this;
    }

    public Table append(Table tableToAppend) {
        for (final Column<?> column : columnList) {
            final Column<?> columnToAppend = tableToAppend.column(column.name());
            for (int i = 0; i < columnToAppend.size(); i++) {
                column.appendObj(columnToAppend.get(i));
            }
        }
        return this;
    }

    /**
     * Add all the columns of tableToConcatenate to this table
     * Note: The columns in the result must have unique names, when compared case insensitive
     * Note: Both tables must have the same number of rows
     * @param tableToConcatenate    The table containing the columns to be added
     * @return                      This table
     */
    public Table concat(Table tableToConcatenate) {
        Preconditions.checkArgument(tableToConcatenate.rowCount() == this.rowCount(),
                "Both tables must have the same number of rows to concatenate them.");
        for (Column<?> column : tableToConcatenate.columns()) {
            this.addColumns(column);
        }
        return this;
    }

    public Summarizer summarize(String columName, AggregateFunction<?, ?>... functions) {
        return summarize(column(columName), functions);
    }

    public Summarizer summarize(List<String> columnNames, AggregateFunction<?, ?>... functions) {
        return new Summarizer(this, columnNames, functions);
    }

    public Summarizer summarize(String numericColumn1Name, String numericColumn2Name, AggregateFunction<?, ?>... functions) {
        return summarize(column(numericColumn1Name), column(numericColumn2Name), functions);
    }

    public Summarizer summarize(String col1Name, String col2Name, String col3Name, AggregateFunction<?, ?>... functions) {
        return summarize(column(col1Name), column(col2Name), column(col3Name), functions);
    }

    public Summarizer summarize(String col1Name, String col2Name, String col3Name, String col4Name, AggregateFunction<?, ?>... functions) {
        return summarize(column(col1Name), column(col2Name), column(col3Name), column(col4Name), functions);
    }

    public Summarizer summarize(Column<?> numberColumn, AggregateFunction<?, ?>... function) {
        return new Summarizer(this, numberColumn, function);
    }

    public Summarizer summarize(Column<?> column1, Column<?> column2,
                                AggregateFunction<?, ?>... function) {
        return new Summarizer(this, column1, column2, function);
    }

    public Summarizer summarize(Column<?> column1, Column<?> column2, Column<?> column3,
                                AggregateFunction<?, ?>... function) {
        return new Summarizer(this, column1, column2, column3, function);
    }

    public Summarizer summarize(Column<?> column1, Column<?> column2, Column<?> column3, Column<?> column4,
                                AggregateFunction<?, ?>... function) {
        return new Summarizer(this, column1, column2, column3, column4, function);
    }

    /**
     * Returns a table with n by m + 1 cells. The first column contains labels, the other cells contains the counts for every unique
     * combination of values from the two specified columns in this table
     */
    public Table xTabCounts(String column1Name, String column2Name) {
        return CrossTab.counts(this, categoricalColumn(column1Name), categoricalColumn(column2Name));
    }

    public Table xTabRowPercents(String column1Name, String column2Name) {
        return CrossTab.rowPercents(this, column1Name, column2Name);
    }

    public Table xTabColumnPercents(String column1Name, String column2Name) {
        return CrossTab.columnPercents(this, column1Name, column2Name);
    }

    /**
     * Returns a table with n by m + 1 cells. The first column contains labels, the other cells contains the proportion
     * for a unique combination of values from the two specified columns in this table
     */
    public Table xTabTablePercents(String column1Name, String column2Name) {
        return CrossTab.tablePercents(this, column1Name, column2Name);
    }

    /**
     * Returns a table with two columns, the first contains a value each unique value in the argument,
     * and the second contains the proportion of observations having that value
     */
    public Table xTabPercents(String column1Name) {
        return CrossTab.percents(this, column1Name);
    }

    /**
     * Returns a table with two columns, the first contains a value each unique value in the argument,
     * and the second contains the number of observations of each value
     */
    public Table xTabCounts(String column1Name) {
        return CrossTab.counts(this, column1Name);
    }

    /**
     * Returns a table containing two columns, the grouping column, and a column named "Count" that contains
     * the counts for each grouping column value
     */
    public Table countBy(CategoricalColumn<?> groupingColumn) {
        return groupingColumn.countByCategory();
    }

    public DataFrameJoiner join(String columnName) {
        return new DataFrameJoiner(this, columnName);
    }

    public Table missingValueCounts() {
        return summarize(columnNames(), countMissing).apply();
    }

    @Override
    public Iterator<Row> iterator() {

        return new Iterator<Row>() {

            final private Row row = new Row(Table.this);

            @Override
            public Row next() {
                return row.next();
            }

            @Override
            public boolean hasNext() {
                return row.hasNext();
            }
        };
    }

    /**
     * Applies the operation in {@code doable} to every row in the table
     */
    public void doWithRows(Consumer<Row> doable) {
        Row row = new Row(this);
        while (row.hasNext()) {
            doable.accept(row.next());
        }
    }

    /**
     * Applies the predicate to each row, and return true if any row returns true
     */
    public boolean detect(Predicate<Row> predicate) {
        Row row = new Row(this);
        while (row.hasNext()) {
            if (predicate.test(row.next())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Applies the operation in {@code doable} to every row in the table
     */
    public void stepWithRows(Consumer<Row[]> rowConsumer, int n) {
        if (!isEmpty()) {
            Row[] rows = new Row[n];
            for (int i = 0; i < n; i++) {
                rows[i] = new Row(this);
            }

            int max = rowCount() - n;
            for (int i = 0; i <= max; i++) {
                for (int r = 0; r < n; r++) {
                    rows[r].at(i + r);
                }
                rowConsumer.accept(rows);
            }
        }
    }

    /**
     * Applies the function in {@code pairs} to each consecutive pairs of rows in the table
     */
    public void doWithRows(Pairs pairs) {
        Row row1 = new Row(this);
        Row row2 = new Row(this);
        if (!isEmpty()) {
            int max = rowCount();
            for (int i = 1; i < max; i++) {
                row1.at(i - 1);
                row2.at(i);
                pairs.doWithPair(row1, row2);
            }
        }
    }

    /**
     * Applies the function in {@code pairs} to each consecutive pairs of rows in the table
     */
    public void doWithRowPairs(Consumer<RowPair> pairConsumer) {
        Row row1 = new Row(this);
        Row row2 = new Row(this);
        RowPair pair = new RowPair(row1, row2);
        if (!isEmpty()) {
            int max = rowCount();
            for (int i = 1; i < max; i++) {
                row1.at(i - 1);
                row2.at(i);
                pairConsumer.accept(pair);
            }
        }
    }

    /**
     * Applies the function in {@code pairs} to each group of contiguous rows of size n in the table
     * This can be used, for example, to calculate a running average of in rows
     */
    public void rollWithRows(Consumer<Row[]> rowConsumer, int n) {
        if (!isEmpty()) {
            Row[] rows = new Row[n];
            for (int i = 0; i < n; i++) {
                rows[i] = new Row(this);
            }

            int max = rowCount() - (n - 2);
            for (int i = 1; i < max; i++) {
                for (int r = 0; r < n; r++) {
                    rows[r].at(i + r - 1);
                }
                rowConsumer.accept(rows);
            }
        }
    }

    public static class RowPair {
        private final Row first;
        private final Row second;

        public RowPair(Row first, Row second) {
            this.first = first;
            this.second = second;
        }

        public Row getFirst() {
            return first;
        }

        public Row getSecond() {
            return second;
        }
    }

    interface Pairs {

        void doWithPair(Row row1, Row row2);

        /**
         * Returns an object containing the results of applying doWithPair() to the rows in a table.
         *
         * The default implementation throws an exception, to be used if the operation produces only side effects
         */
        default Object getResult() {
            throw new UnsupportedOperationException("This Pairs function returns no results");
        }
    }
}