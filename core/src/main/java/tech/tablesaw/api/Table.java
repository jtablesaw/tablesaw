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

import static tech.tablesaw.aggregate.AggregateFunctions.count;
import static tech.tablesaw.aggregate.AggregateFunctions.max;
import static tech.tablesaw.aggregate.AggregateFunctions.mean;
import static tech.tablesaw.aggregate.AggregateFunctions.median;
import static tech.tablesaw.aggregate.AggregateFunctions.min;
import static tech.tablesaw.aggregate.AggregateFunctions.stdDev;
import static tech.tablesaw.aggregate.AggregateFunctions.sum;
import static tech.tablesaw.aggregate.AggregateFunctions.variance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomUtils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntIterable;
import it.unimi.dsi.fastutil.ints.IntIterator;
import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.aggregate.SummaryFunction;
import tech.tablesaw.columns.Column;
import tech.tablesaw.filtering.Filter;
import tech.tablesaw.io.DataFrameReader;
import tech.tablesaw.io.DataFrameWriter;
import tech.tablesaw.io.html.HtmlTableWriter;
import tech.tablesaw.join.DataFrameJoiner;
import tech.tablesaw.sorting.Sort;
import tech.tablesaw.sorting.Sort.Order;
import tech.tablesaw.store.StorageManager;
import tech.tablesaw.store.TableMetadata;
import tech.tablesaw.table.Projection;
import tech.tablesaw.table.Relation;
import tech.tablesaw.table.Rows;
import tech.tablesaw.table.ViewGroup;
import tech.tablesaw.util.BitmapBackedSelection;
import tech.tablesaw.util.IntComparatorChain;
import tech.tablesaw.util.ReversingIntComparator;
import tech.tablesaw.util.Selection;

/**
 * A table of data, consisting of some number of columns, each of which has the same number of rows.
 * All the data in a column has the same type: integer, float, category, etc., but a table may contain an arbitrary
 * number of columns of any type.
 * <p>
 * Tables are the main data-type and primary focus of Tablesaw.
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
     * Returns a new table initialized with data from the given TableMetadata object
     * <p>
     * The metadata is used by the storage module to save tables and read their data from disk
     */
    private Table(TableMetadata metadata) {
        this.name = metadata.getName();
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
     * Returns a new, empty table constructed according to the given metadata
     */
    public static Table create(TableMetadata metadata) {
        return new Table(metadata);
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
    @VisibleForTesting
    public static Sort getSort(String... columnNames) {
        Sort key = null;
        for (String s : columnNames) {
            if (key == null) {
                key = first(s, Order.DESCEND);
            } else {
                key.next(s, Order.DESCEND);
            }
        }
        return key;
    }

    /**
     * Creates an IntColumn containing the integers from startsWith to rowCount() and adds it to this table.
     * Can be used for maintaining/restoring a specific order on data without an existing order column, or for
     * generating scatter/line plots where the variation of points in some order is what you're trying to see.
     */
    public void addIndexColumn(String columnName, int startsWith) {

        IntColumn indexColumn = new IntColumn(columnName, rowCount());
        for (int i = 0; i < rowCount(); i++) {
            indexColumn.append(i + startsWith);
        }
        addColumn(indexColumn);
    }

    public static Table readTable(String tableNameAndPath) {
        Table t;
        try {
            t = StorageManager.readTable(tableNameAndPath);
        } catch (IOException e) {
            System.err.println("Unable to load table from Tablesaw table format");
            e.printStackTrace();
            return null;
        }
        return t;
    }

    public static DataFrameReader read() {
        return new DataFrameReader();
    }

    public DataFrameWriter write() {
      return new DataFrameWriter(this);
    }
 
    /**
     * Returns an randomly generated array of ints of size N where Max is the largest possible value
     */
    static int[] generateUniformBitmap(int N, int Max) {
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
     * Returns a table with the same columns as this table
     */
    public Table fullCopy() {
      Table copy = new Table(name);
      for (Column column : columnList) {
        copy.addColumn(column.emptyCopy());
      }

      IntArrayList integers = new IntArrayList();
      for(int i = 0; i < rowCount(); i++)
        integers.add(i);
      Rows.copyRowsToTable(integers,this,copy);
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
    public Table sample(double proportion) {

        int tableCount = (int) Math.round(rowCount() * proportion);

        Selection table1Selection = new BitmapBackedSelection();
        int[] selectedRecords = generateUniformBitmap(tableCount, rowCount());
        for (int selectedRecord : selectedRecords) {
            table1Selection.add(selectedRecord);
        }
        return selectWhere(table1Selection);
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
            Order order;
            if (names.contains(columnName.toUpperCase())) {
                // the column name has not been annotated with a prefix.
                order = Order.ASCEND;
            } else {

                // get the prefix which could be - or +
                String prefix = columnName.substring(0, 1);

                // remove - prefix so provided name matches actual column name
                columnName = columnName.substring(1, columnName.length());

                switch (prefix) {
                    case "+":
                        order = Order.ASCEND;
                        break;
                    case "-":
                        order = Order.DESCEND;
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
     * @param reverse    {@code true} if the column should be sorted in reverse
     */
    private IntComparator rowComparator(String columnName, Order order) {
        Column column = this.column(columnName);
        IntComparator rowComparator = column.rowComparator();

        if (order == Order.DESCEND) {
            return ReversingIntComparator.reverse(rowComparator);
        } else {
            return rowComparator;
        }
    }

    public Table selectWhere(Selection selection) {
        Table newTable = this.emptyCopy(selection.size());
        Rows.copyRowsToTable(selection, this, newTable);
        return newTable;
    }

    public BooleanColumn selectIntoColumn(String newColumnName, Selection selection) {
        return new BooleanColumn(newColumnName, selection, rowCount());
    }

    public Table selectWhere(Filter filter) {
        Selection map = filter.apply(this);
        Table newTable = this.emptyCopy(map.size());
        Rows.copyRowsToTable(map, this, newTable);
        return newTable;
    }

    public BooleanColumn selectIntoColumn(String newColumnName, Filter filter) {
        return new BooleanColumn(newColumnName, filter.apply(this), rowCount());
    }

    /**
     * The first stage of a split-apply-combine operation
     */
    public ViewGroup groupBy(String... columns) {
      return groupBy(columns(columns).toArray(new Column[columns.length]));
    }

    /**
     * The first stage of a split-apply-combine operation
     */
    public ViewGroup groupBy(Column... columns) {
      return new ViewGroup(this, columns);
    }

    /**
     * Synonymous with groupBy
     * The first stage of a split-apply-combine operation
     */
    public ViewGroup splitOn(String... columns) {
        return groupBy(columns);
    }

    /**
     * Synonymous with groupBy
     * The first stage of a split-apply-combine operation
     */
    public ViewGroup splitOn(Column... columns) {
        return groupBy(columns);
    }

    public String printHtml() {
        return HtmlTableWriter.write(this);
    }

    public Table structure() {
        Table t = new Table("Structure of " + name());
        IntColumn index = new IntColumn("Index", columnCount());
        CategoryColumn columnName = new CategoryColumn("Column Name", columnCount());
        CategoryColumn columnType = new CategoryColumn("Column Type", columnCount());
        t.addColumn(index);
        t.addColumn(columnName);
        t.addColumn(columnType);
        columnName.addAll(columnNames());
        for (int i = 0; i < columnCount(); i++) {
            Column column = columnList.get(i);
            index.append(i);
            columnType.append(column.type().name());
        }
        return t;
    }

    /**
     * Returns a table with the given rows selected
     * @param row the row to select
     * @return the table with the selected rows
     */
    public Table selectRow(int row) {
      return selectRows(row, row);
    }
    
    /**
     * Returns a table with the given rows selected
     * @param rows the rows to select
     * @return the table with the selected rows
     */
    public Table selectRows(Collection<Integer> rows) {
      Table newTable = emptyCopy();
      Rows.copyRowsToTable(new IntArrayList(rows), this, newTable);
      return newTable;

    }

    /**
     * Returns a table with the given rows selected
     * @param start the first row to select
     * @param end the last row to select
     * @return the table with the selected rows
     */
    public Table selectRows(int start, int end) {
      Table newTable = emptyCopy();
      IntArrayList rowsToKeep = new IntArrayList();
      for (int i = 0; i < rowCount(); i++) {
        if (i >= start && i <= end) {
          rowsToKeep.add(i);
        }
      }
      Rows.copyRowsToTable(rowsToKeep, this, newTable);
      return newTable;
    }

    /**
     * Returns a table with the given rows dropped
     * @param row the row to drop
     * @return the table with the dropped rows
     */
    public Table dropRow(int row) {
      return dropRows(row, row);
    }

    /**
     * Returns a table with the given rows dropped
     * @param rows the rows to drop
     * @return the table with the dropped rows
     */
    public Table dropRows(Collection<Integer> rows) {
      Table newTable = emptyCopy();
      IntArrayList rowsToKeep = new IntArrayList();
      for (int i = 0; i < rowCount(); i++) {
        rowsToKeep.add(i);
      }
      rowsToKeep.removeAll(new IntArrayList(rows));
      Rows.copyRowsToTable(rowsToKeep, this, newTable);
      return newTable;
    }

    /**
     * Returns a table with the given rows dropped
     * @param start the first row to drop
     * @param end the last row to drop
     * @return the table with the dropped rows
     */
    public Table dropRows(int start, int end) {
      Table newTable = emptyCopy();
      IntArrayList rowsToKeep = new IntArrayList();
      for (int i = 0; i < rowCount(); i++) {
        if (i < start || i > end) {
          rowsToKeep.add(i);
        }
      }
      Rows.copyRowsToTable(rowsToKeep, this, newTable);
      return newTable;
    }

    /**
     * Returns the unique records in this table
     * Note: Uses a lot of memory for a sort
     */
    public Table uniqueRecords() {

        Table sorted = this.sortOn(columnNames().toArray(new String[columns().size()]));
        Table temp = emptyCopy();

        for (int row = 0; row < rowCount(); row++) {
            if (temp.isEmpty() || !Rows.compareRows(row, sorted, temp)) {
                Rows.appendRowToTable(row, sorted, temp);
            }
        }
        return temp;
    }

    public Projection select(String... columnName) {
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
     * Removes the given column from this table and returns it
     *
     * @throws IllegalStateException if the given columnName does not match the name of a column in the table
     */
    public Column getAndRemoveColumn(String columnName) {
        Column c = column(columnName);
        removeColumns(c);
        return c;
    }

    /**
     * Removes the given column from this table and returns it
     *
     * @throws IndexOutOfBoundsException if the given columnIndex does not match any column in the table
     */
    public Column getAndRemoveColumn(int columnIndex) {
        Column c = column(columnIndex);
        removeColumns(c);
        return c;
    }

    /**
     * Removes the given columns from this table
     */
    public void retainColumns(Column... columns) {
        List<Column> retained = Arrays.asList(columns);
        columnList.retainAll(retained);
    }

    public void retainColumns(String... columnNames) {
        columnList.retainAll(columns(columnNames));
    }

    public SummaryFunction sum(String numericColumnName) {
        return new SummaryFunction(this, numericColumnName, sum);
    }
    public SummaryFunction mean(String numericColumnName) {
        return new SummaryFunction(this, numericColumnName, mean);
    }
    public SummaryFunction median(String numericColumnName) {
        return new SummaryFunction(this, numericColumnName, median);
    }
    public SummaryFunction variance(String numericColumnName) {
        return new SummaryFunction(this, numericColumnName, variance);
    }
    public SummaryFunction stdDev(String numericColumnName) {
        return new SummaryFunction(this, numericColumnName, stdDev);
    }

    public SummaryFunction count(String numericColumnName) {
        return new SummaryFunction(this, numericColumnName, count);
    }

    public SummaryFunction max(String numericColumnName) {
        return new SummaryFunction(this, numericColumnName, max);
    }

    public SummaryFunction min(String numericColumnName) {
      return new SummaryFunction(this, numericColumnName, min);
    }

    public void append(Table tableToAppend) {
        for (Column column : columnList) {
            Column columnToAppend = tableToAppend.column(column.name());
            column.append(columnToAppend);
        }
    }

    public String save(String folder) {
        String storageFolder = "";
        try {
            storageFolder = StorageManager.saveTable(folder, this);
        } catch (IOException e) {
            System.err.println("Unable to save table in Tablesaw format");
            e.printStackTrace();
        }
        return storageFolder;
    }

    /**
     * Returns the result of applying the given aggregate function to the specified column
     *
     * @param numericColumnName The name of a numeric (integer, float, etc.) column in this table
     * @param function          An aggregation function
     * @return the function result
     * @throws IllegalArgumentException if numericColumnName doesn't name a numeric column in this table
     */
    public double agg(String numericColumnName, AggregateFunction function) {
        Column column = column(numericColumnName);
        return function.agg(column.toDoubleArray());
    }

    public SummaryFunction summarize(String numericColumnName, AggregateFunction function) {
        return new SummaryFunction(this, numericColumnName, function);
    }

    public Table countBy(CategoryColumn column) {
        return column.countByCategory();
    }

    /**
     * Returns the first row for which the column {@code columnName} contains {@code value}, or
     * null if there are no matches
     * TODO(lwhite) This is a toy implementation badly in need of rewrite for performance.
     */
    public int getFirst(Column column, String value) {
        int row = -1;
        for (int r : this) {
            if (column.getString(r).equals(value)) {
                row = r;
                break;
            }
        }
        return row;
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

            @Override
            public Integer next() {
                return i++;
            }
        };
    }
}
