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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntIterable;
import it.unimi.dsi.fastutil.ints.IntIterator;
import org.apache.commons.lang3.RandomUtils;
import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.aggregate.Summarizer;
import tech.tablesaw.columns.Column;
import tech.tablesaw.filtering.Filter;
import tech.tablesaw.io.DataFrameReader;
import tech.tablesaw.io.DataFrameWriter;
import tech.tablesaw.io.html.HtmlTableWriter;
import tech.tablesaw.joining.DataFrameJoiner;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;
import tech.tablesaw.sorting.Sort;
import tech.tablesaw.sorting.comparators.IntComparatorChain;
import tech.tablesaw.sorting.comparators.ReversingIntComparator;
import tech.tablesaw.table.Projection;
import tech.tablesaw.table.Relation;
import tech.tablesaw.table.Rows;
import tech.tablesaw.table.StandardTableSliceGroup;
import tech.tablesaw.table.TableSliceGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A table of data, consisting of some number of columns, each of which has the same number of rows.
 * All the data in a column has the same type: integer, float, category, etc., but a table may contain an arbitrary
 * number of columns of any type.
 * <p>
 * Tables are the main data-type and primary focus of Airframe.
 */
public class Table extends Relation implements IntIterable {

    /**
     * The columns that hold the data in this table
     */
    private final List<Column> columnList = new ArrayList<>();
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
    protected Table(String name, Column... columns) {
        this(name);
        for (Column column : columns) {
            this.addColumn(column);
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
    public static Table create(String tableName, Column... columns) {
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

    /**
     * Returns an randomly generated array of ints of size N where Max is the largest possible value
     */
    private static int[] generateUniformBitmap(int N, int Max) {
        if (N > Max) {
            throw new IllegalArgumentException("Illegal arguments: N (" + N + ") greater than Max (" + Max + ")");
        }

        int[] ans = new int[N];
        if (N == Max) {
            for (int k = 0; k < N; ++k)
                ans[k] = k;
            return ans;
        }

        BitSet bs = new BitSet(Max);
        int cardinality = 0;
        while (cardinality < N) {
            int v = RandomUtils.nextInt(0, Max);
            if (!bs.get(v)) {
                bs.set(v);
                cardinality++;
            }
        }
        int pos = 0;
        for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
            ans[pos++] = i;
        }
        return ans;
    }

    public DataFrameWriter write() {
        return new DataFrameWriter(this);
    }

    /**
     * Adds the given column to this table
     */
    @Override
    public Table addColumn(Column... cols) {
        for (Column c : cols) {
            validateColumn(c);
            columnList.add(c);
        }
        return this;
    }

    /**
     * Throws an IllegalArgumentException if a column with the given name is already in the table
     */
    private void validateColumn(Column newColumn) {
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
    public Table addColumn(int index, Column column) {
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
    public Table replaceColumn(int colIndex, Column newColumn) {
        removeColumns(column(colIndex));
        addColumn(colIndex, newColumn);
        return this;
    }

    /**
     * Replaces an existing column (by name) in this table with the given new column
     *
     * @param columnName String name of the column to be replaced
     * @param newColumn  Column to be added
     */
    public Table replaceColumn(String columnName, Column newColumn) {
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
    public Column column(int columnIndex) {
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
    public List<Column> columns() {
        return columnList;
    }

    /**
     * Returns only the columns whose names are given in the input array
     */
    public List<Column> columns(String... columnNames) {
        List<Column> columns = new ArrayList<>();
        for (String columnName : columnNames) {
            columns.add(column(columnName));
        }
        return columns;
    }

    /**
     * Returns only the columns whose names are given in the input array
     */
    public List<CategoricalColumn> categoricalColumns(String... columnNames) {
        List<CategoricalColumn> columns = new ArrayList<>();
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
    public int columnIndex(Column column) {
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
        Column column = column(c);
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
        Column column = column(c);
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
        Column column = column(columnIndex(columnName));
        return column.getString(r);
    }

    /**
     * Returns a table with the same columns as this table
     */
    public Table fullCopy() {
        Table copy = new Table(name);
        for (Column column : columnList) {
            copy.addColumn(column.emptyCopy());
        }

        IntArrayList integers = new IntArrayList();
        for (int i = 0; i < rowCount(); i++)
            integers.add(i);
        Rows.copyRowsToTable(integers, this, copy);
        return copy;
    }

    /**
     * Returns a table with the same columns as this table, but no data
     */
    public Table emptyCopy() {
        Table copy = new Table(name);
        for (Column column : columnList) {
            copy.addColumn(column.emptyCopy());
        }
        return copy;
    }

    /**
     * Returns a table with the same columns as this table, but no data, initialized to the given row size
     */
    public Table emptyCopy(int rowSize) {
        Table copy = new Table(name);
        for (Column column : columnList) {
            copy.addColumn(column.emptyCopy(rowSize));
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

        int[] table1Records = generateUniformBitmap(table1Count, rowCount());
        for (int table1Record : table1Records) {
            table1Selection.add(table1Record);
        }
        table2Selection.andNot(table1Selection);
        tables[0] = selectWhere(table1Selection);
        tables[1] = selectWhere(table2Selection);
        return tables;
    }

    /**
     * Returns a table consisting of randomly selected records from this table. The sample size is based on the
     * given proportion
     *
     * @param proportion The proportion to go in the sample
     */
    public Table selectSample(double proportion) {
        Preconditions.checkArgument(proportion <= 1 && proportion >= 0,
                "The sample proportion must be between 0 and 1");

        int tableSize = (int) Math.round(rowCount() * proportion);
        return selectWhere(selectNRows(tableSize));
    }

    /**
     * Returns a table consisting of randomly selected records from this table
     *
     * @param nRows The number of rows to go in the sample
     */
    public Table selectSample(int nRows) {
        Preconditions.checkArgument(nRows > 0 && nRows < rowCount(),
                "The number of rows sampled must be greater than 0 and less than the number of rows in the table.");
        return selectWhere(selectNRows(nRows));
    }

    private Selection selectNRows(int tableCount) {
        Selection table1Selection = new BitmapBackedSelection();
        int[] selectedRecords = generateUniformBitmap(tableCount, rowCount());
        for (int selectedRecord : selectedRecords) {
            table1Selection.add(selectedRecord);
        }
        return table1Selection;
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
        nRows = Math.min(nRows, rowCount());
        Table newTable = emptyCopy(nRows);
        Rows.head(nRows, this, newTable);
        return newTable;
    }

    /**
     * Returns a new table containing the last {@code nrows} of data in this table
     */
    public Table last(int nRows) {
        nRows = Math.min(nRows, rowCount());
        Table newTable = emptyCopy(nRows);
        Rows.tail(nRows, this, newTable);
        return newTable;
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
            IntComparator comparator = getComparator(key);
            return sortOn(comparator);
        }
        IntComparatorChain chain = getChain(key);
        return sortOn(chain);
    }

    /**
     * Returns a comparator that can be used to sort the records in this table according to the given sort key
     */
    private IntComparator getComparator(Sort key) {
        Iterator<Map.Entry<String, Sort.Order>> entries = key.iterator();
        Map.Entry<String, Sort.Order> sort = entries.next();
        return rowComparator(sort.getKey(), sort.getValue());
    }

    /**
     * Returns a comparator chain for sorting according to the given key
     */
    private IntComparatorChain getChain(Sort key) {
        Iterator<Map.Entry<String, Sort.Order>> entries = key.iterator();
        Map.Entry<String, Sort.Order> sort = entries.next();

        IntComparator comparator = rowComparator(sort.getKey(), sort.getValue());

        IntComparatorChain chain = new IntComparatorChain(comparator);
        while (entries.hasNext()) {
            sort = entries.next();
            chain.addComparator(rowComparator(sort.getKey(), sort.getValue()));
        }
        return chain;
    }

    /**
     * Returns a copy of this table sorted using the given comparator
     */
    public Table sortOn(IntComparator rowComparator) {
        Table newTable = emptyCopy(rowCount());

        int[] newRows = rows();
        IntArrays.parallelQuickSort(newRows, rowComparator);

        Rows.copyRowsToTable(IntArrayList.wrap(newRows), this, newTable);
        return newTable;
    }

    /**
     * Returns an array of ints of the same number of rows as the table
     */
    @VisibleForTesting
    public int[] rows() {
        int[] rowIndexes = new int[rowCount()];
        for (int i = 0; i < rowCount(); i++) {
            rowIndexes[i] = i;
        }
        return rowIndexes;
    }

    /**
     * Returns a comparator for the column matching the specified name
     *
     * @param columnName The name of the column to sort
     * @param order      Specifies whether the sort should be in ascending or descending order
     */
    private IntComparator rowComparator(String columnName, Sort.Order order) {
        Column column = this.column(columnName);
        IntComparator rowComparator = column.rowComparator();

        if (order == Sort.Order.DESCEND) {
            return ReversingIntComparator.reverse(rowComparator);
        } else {
            return rowComparator;
        }
    }

    /**
     * Adds a single row to this table from sourceTable, copying every column in sourceTable
     */
    public void addRow(int rowIndex, Table sourceTable) {
        for (int i = 0; i < columnCount(); i++) {
            Column column = column(i);
            ColumnType type = column.type();
            switch (type) {
                case NUMBER:
                    NumberColumn numberColumn = (NumberColumn) column;
                    numberColumn.append(sourceTable.numberColumn(i).get(rowIndex));
                    break;
                case BOOLEAN:
                    BooleanColumn booleanColumn = (BooleanColumn) column;
                    booleanColumn.append(sourceTable.booleanColumn(i).get(rowIndex));
                    break;
                case LOCAL_DATE:
                    DateColumn localDateColumn = (DateColumn) column;
                    localDateColumn.appendInternal(sourceTable.dateColumn(i).getIntInternal(rowIndex));
                    break;
                case LOCAL_TIME:
                    TimeColumn timeColumn = (TimeColumn) column;
                    timeColumn.appendInternal(sourceTable.timeColumn(i).getIntInternal(rowIndex));
                    break;
                case LOCAL_DATE_TIME:
                    DateTimeColumn localDateTimeColumn = (DateTimeColumn) column;
                    localDateTimeColumn.appendInternal(sourceTable.dateTimeColumn(i).getLongInternal(rowIndex));
                    break;
                case STRING:
                    StringColumn stringColumn = (StringColumn) column;
                    stringColumn.append(sourceTable.stringColumn(i).get(rowIndex));
                    break;
                default:
                    throw new IllegalStateException("Unhandled column type updating columns");
            }
        }
    }

    public void addRow(Row row) {
        //TODO Implement
        for (Column column : columns()) {
            ColumnType type = column.type();
            switch (type) {
                case NUMBER:
                    NumberColumn numberColumn = (NumberColumn) column;
                    numberColumn.append(row.getDouble(column.name()));
                    break;
                case BOOLEAN:
                    BooleanColumn booleanColumn = (BooleanColumn) column;
                    booleanColumn.append(row.getBoolean(column.name()));
                    break;
                case LOCAL_DATE:
                    DateColumn localDateColumn = (DateColumn) column;
                    localDateColumn.appendInternal(row.getPackedDate(column.name()).getPackedValue());
                    break;
                case LOCAL_TIME:
                    TimeColumn timeColumn = (TimeColumn) column;
                    timeColumn.appendInternal(row.getPackedTime(column.name()).getPackedValue());
                    break;
                case LOCAL_DATE_TIME:
                    DateTimeColumn localDateTimeColumn = (DateTimeColumn) column;
                    localDateTimeColumn.appendInternal(row.getPackedDateTime(column.name()).getPackedValue());
                    break;
                case STRING:
                    StringColumn stringColumn = (StringColumn) column;
                    stringColumn.append(row.getString(column.name()));
                    break;
                default:
                    throw new IllegalStateException("Unhandled column type updating columns");
            }
        }
    }

    public Table selectRows(int... rowNumbers) {
        Preconditions.checkArgument(Ints.max(rowNumbers) <= rowCount());
        return selectWhere(Selection.with(rowNumbers));
    }

    public Table rejectRows(int... rowNumbers) {
        Preconditions.checkArgument(Ints.max(rowNumbers) <= rowCount());
        Selection selection = Selection.withRange(0, rowCount())
                .andNot(Selection.with(rowNumbers));
        return selectWhere(selection);
    }

    public Table selectRange(int rowStart, int rowEnd) {
        Preconditions.checkArgument(rowEnd <= rowCount());
        return selectWhere(Selection.withRange(rowStart, rowEnd));
    }

    public Table rejectRange(int rowStart, int rowEnd) {
        Preconditions.checkArgument(rowEnd <= rowCount());
        return selectWhere(Selection.withoutRange(0, rowCount(), rowStart, rowEnd));
    }

    public Table selectWhere(Selection selection) {
        Table newTable = this.emptyCopy(selection.size());
        Rows.copyRowsToTable(selection, this, newTable);
        return newTable;
    }

    public Table rejectWhere(Selection selection) {
        Selection opposite = new BitmapBackedSelection();
        opposite.addRange(0, rowCount());
        opposite.andNot(selection);
        Table newTable = this.emptyCopy(opposite.size());
        Rows.copyRowsToTable(opposite, this, newTable);
        return newTable;
    }

    public Table selectWhere(Filter filter) {
        return selectWhere(filter.apply(this));
    }

    public Table rejectWhere(Filter filter) {
        return rejectWhere(filter.apply(this));
    }

    /**
     * The first stage of a split-apply-combine operation
     */
    public TableSliceGroup splitOn(String... columns) {
        return splitOn(categoricalColumns(columns).toArray(new CategoricalColumn[columns.length]));
    }

    /**
     * The first stage of a split-apply-combine operation
     */
    public TableSliceGroup splitOn(CategoricalColumn... columns) {
        return StandardTableSliceGroup.create(this, columns);
    }

    public String printHtml() {
        return HtmlTableWriter.write(this);
    }

    public Table structure() {
        Table t = new Table("Structure of " + name());
        //NumberColumn index = DoubleColumn.create("Index", columnCount());
        NumberColumn index = DoubleColumn.indexColumn("Index", columnCount(), 0);
        StringColumn columnName = StringColumn.create("Column Name", columnCount());
        StringColumn columnType = StringColumn.create("Column Type", columnCount());
        t.addColumn(index);
        t.addColumn(columnName);
        t.addColumn(columnType);
        columnName.addAll(columnNames());
        for (int i = 0; i < columnCount(); i++) {
            Column column = columnList.get(i);
            columnType.append(column.type().name());
        }
        return t;
    }

    /**
     * Returns the unique records in this table
     * Note: Uses a lot of memory for a sort
     */
    public Table rejectDuplicateRows() {

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
    public Table rejectRowsWithMissingValues() {

        Table temp = emptyCopy();
        for (int row = 0; row < rowCount(); row++) {
            boolean add = true;
            for (int col = 0; col < columnCount(); col++) {
                Column c = column(col);
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

    public Projection project(String... columnName) {
        return new Projection(this, columnName);
    }

    /**
     * Removes the given columns
     */
    @Override
    public Table removeColumns(Column... columns) {
        columnList.removeAll(Arrays.asList(columns));
        return this;
    }

    /**
     * Removes the given columns with missing values
     */
    public Table removeColumnsWithMissingValues() {
        removeColumns(columnList.stream().filter(x -> x.countMissing() > 0).toArray(Column[]::new));
        return this;
    }

    /**
     * Removes all columns except for those given in the argument from this table
     */
    public Table retainColumns(Column... columns) {
        List<Column> retained = Arrays.asList(columns);
        columnList.clear();
        columnList.addAll(retained);
        return this;
    }

    /**
     * Removes all columns except for those given in the argument from this table
     */
    public Table retainColumns(String... columnNames) {
        List<Column> retained = columns(columnNames);
        columnList.clear();
        columnList.addAll(retained);
        return this;
    }

    public Table append(Table tableToAppend) {
        for (Column column : columnList) {
            Column columnToAppend = tableToAppend.column(column.name());
            column.append(columnToAppend);
        }
        return this;
    }

    public Summarizer summarize(String numericColumnName, AggregateFunction... functions) {
        return summarize(numberColumn(numericColumnName), functions);
    }

    public Summarizer summarize(String numericColumn1Name, String numericColumn2Name, AggregateFunction... functions) {
        return summarize(numberColumn(numericColumn1Name), numberColumn(numericColumn2Name), functions);
    }

    public Summarizer summarize(NumberColumn numberColumn, AggregateFunction... function) {
        return new Summarizer(this, numberColumn, function);
    }

    public Summarizer summarize(NumberColumn numberColumn1, NumberColumn numberColumn2,
                                AggregateFunction... function) {
        return new Summarizer(this, numberColumn1, numberColumn2, function);
    }

    public Summarizer summarize(NumberColumn column1, NumberColumn column2, NumberColumn column3,
                                AggregateFunction... function) {
        return new Summarizer(this, column1, column2, column3, function);
    }

    public Summarizer summarize(NumberColumn column1, NumberColumn column2, NumberColumn column3, NumberColumn column4,
                                AggregateFunction... function) {
        return new Summarizer(this, column1, column2, column3, column4, function);
    }

    /**
     * Returns a table containing two columns, the grouping column, and a column named "Count" that contains
     * the counts for each grouping column value
     * Todo: extend count by category to all categorical columns
     */
    public Table countBy(StringColumn groupingColumn) {
        return groupingColumn.countByCategory();
    }

    public DataFrameJoiner join(String columnName) {
        return new DataFrameJoiner(this, columnName);
    }

    @Override
    public IntIterator iterator() {

        return new IntIterator() {

            private int i = 0;

            @Override
            public int nextInt() {
                return i++;
            }

            @Override
            public int skip(int k) {
                return i + k;
            }

            @Override
            public boolean hasNext() {
                return i < rowCount();
            }
        };
    }

    /**
     * Applies the function in {@code doable} to every row in the table
     */
    public void doWithEachRow(Doable doable) {
        Row row = new Row(this);
        while (row.hasNext()) {
            doable.doWithRow(row.next());
        }
    }

    /**
     * Applies the function in {@code pairs} to each consecutive pairs of rows in the table
     */
    public void doWithRowPairs(Pairs pairs) {
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
    public void rollWithNrows(MultiRowDoable rowz, int n) {
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
                rowz.doWithNrows(rows);
            }
        }
    }

    /**
     * Applies the function in {@code pairs} to each consecutive pairs of rows in the table
     */
    public void stepWithNrows(MultiRowDoable rowz, int n) {
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
                rowz.doWithNrows(rows);
            }
        }
    }

    /**
     * Applies the function in {@code collectable} to every row in the table
     */
    public Column collectFromEachRow(Collectable collectable) {
        Row row = new Row(this);
        while (row.hasNext()) {
            collectable.collectFromRow(row.next());
        }
        return collectable.column();
    }

    interface MultiRowDoable {
        void doWithNrows(Row[] rows);
    }

    interface Pairs {
        void doWithPair(Row row1, Row row2);
    }

    /**
     * A function object that can be used to enumerate a table and perform operations on each row,
     * without explicit loops
     */
    static abstract class Doable {

        public abstract void doWithRow(Row row);
    }

    /**
     * A function object that can be used to enumerate a table and perform operations on each row,
     * without explicit loops. {@code Collectable} fills the given column with the results, so the column
     * must be of the correct type for whatever results are produced in the collectWithRow operation.
     */
    static abstract class Collectable {

        private final Column column;

        public Collectable(Column column) {
            this.column = column;
        }

        public Column column() {
            return column;
        }

        abstract void collectFromRow(Row row);
    }
}
