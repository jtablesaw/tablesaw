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

import static java.util.stream.Collectors.toList;
import static tech.tablesaw.aggregate.AggregateFunctions.count;
import static tech.tablesaw.aggregate.AggregateFunctions.countMissing;
import static tech.tablesaw.api.QuerySupport.not;
import static tech.tablesaw.selection.Selection.selectNRowsAtRandom;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.google.common.primitives.Ints;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import it.unimi.dsi.fastutil.ints.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.roaringbitmap.RoaringBitmap;
import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.aggregate.CrossTab;
import tech.tablesaw.aggregate.PivotTable;
import tech.tablesaw.aggregate.Summarizer;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.DataFrameReader;
import tech.tablesaw.io.DataFrameWriter;
import tech.tablesaw.io.DataReader;
import tech.tablesaw.io.DataWriter;
import tech.tablesaw.io.ReaderRegistry;
import tech.tablesaw.io.WriterRegistry;
import tech.tablesaw.joining.DataFrameJoiner;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;
import tech.tablesaw.sorting.Sort;
import tech.tablesaw.sorting.SortUtils;
import tech.tablesaw.sorting.comparators.IntComparatorChain;
import tech.tablesaw.table.*;

/**
 * A table of data, consisting of some number of columns, each of which has the same number of rows.
 * All the data in a column has the same type: integer, float, category, etc., but a table may
 * contain an arbitrary number of columns of any type.
 *
 * <p>Tables are the main data-type and primary focus of Tablesaw.
 */
public class Table extends Relation implements Iterable<Row> {

  public static final ReaderRegistry defaultReaderRegistry = new ReaderRegistry();
  public static final WriterRegistry defaultWriterRegistry = new WriterRegistry();

  static {
    autoRegisterReadersAndWriters();
  }

  /** The columns that hold the data in this table */
  private final List<Column<?>> columnList = new ArrayList<>();
  /** The name of the table */
  private String name;

  // standard column names for melt and cast operations
  public static final String MELT_VARIABLE_COLUMN_NAME = "variable";
  public static final String MELT_VALUE_COLUMN_NAME = "value";

  /** Returns a new table */
  private Table() {}

  /** Returns a new table initialized with the given name */
  private Table(String name) {
    this.name = name;
  }

  /**
   * Returns a new Table initialized with the given names and columns
   *
   * @param name The name of the table
   * @param columns One or more columns, all of which must have either the same length or size 0
   */
  protected Table(String name, Column<?>... columns) {
    this(name);
    for (final Column<?> column : columns) {
      this.addColumns(column);
    }
  }

  /**
   * Returns a new Table initialized with the given names and columns
   *
   * @param name The name of the table
   * @param columns One or more columns, all of which must have either the same length or size 0
   */
  protected Table(String name, Collection<Column<?>> columns) {
    this(name);
    for (final Column<?> column : columns) {
      this.addColumns(column);
    }
  }

  /** TODO: Add documentation */
  private static void autoRegisterReadersAndWriters() {
    try (ScanResult scanResult =
        new ClassGraph().enableAllInfo().whitelistPackages("tech.tablesaw.io").scan()) {
      List<String> classes = new ArrayList<>();
      classes.addAll(scanResult.getClassesImplementing(DataWriter.class.getName()).getNames());
      classes.addAll(scanResult.getClassesImplementing(DataReader.class.getName()).getNames());
      for (String clazz : classes) {
        try {
          Class.forName(clazz);
        } catch (ClassNotFoundException e) {
          throw new IllegalStateException(e);
        }
      }
    }
  }

  /** Returns a new, empty table (without rows or columns) */
  public static Table create() {
    return new Table();
  }

  /** Returns a new, empty table (without rows or columns) with the given name */
  public static Table create(String tableName) {
    return new Table(tableName);
  }

  /**
   * Returns a new table with the given columns
   *
   * @param columns one or more columns, all of the same @code{column.size()}
   */
  public static Table create(Column<?>... columns) {
    return new Table(null, columns);
  }

  /**
   * Returns a new table with the given columns
   *
   * @param columns one or more columns, all of the same @code{column.size()}
   */
  public static Table create(Collection<Column<?>> columns) {
    return new Table(null, columns);
  }

  /**
   * Returns a new table with the given columns
   *
   * @param columns one or more columns, all of the same @code{column.size()}
   */
  public static Table create(Stream<Column<?>> columns) {
    return new Table(null, columns.collect(Collectors.toList()));
  }

  /**
   * Returns a new table with the given columns and given name
   *
   * @param name the name for this table
   * @param columns one or more columns, all of the same @code{column.size()}
   */
  public static Table create(String name, Column<?>... columns) {
    return new Table(name, columns);
  }

  /**
   * Returns a new table with the given columns and given name
   *
   * @param name the name for this table
   * @param columns one or more columns, all of the same @code{column.size()}
   */
  public static Table create(String name, Collection<Column<?>> columns) {
    return new Table(name, columns);
  }

  /**
   * Returns a new table with the given columns and given name
   *
   * @param name the name for this table
   * @param columns one or more columns, all of the same @code{column.size()}
   */
  public static Table create(String name, Stream<Column<?>> columns) {
    return new Table(name, columns.collect(Collectors.toList()));
  }

  /**
   * Returns a sort Key that can be used for simple or chained comparator sorting
   *
   * <p>You can extend the sort key by using .next() to fill more columns to the sort order
   */
  private static Sort first(String columnName, Sort.Order order) {
    return Sort.on(columnName, order);
  }

  /**
   * Returns an object that can be used to sort this table in the order specified for by the given
   * column names
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

  /** Returns an object that can be used to read data from a file into a new Table */
  public static DataFrameReader read() {
    return new DataFrameReader(defaultReaderRegistry);
  }

  /**
   * Returns an object that an be used to write data from a Table into a file. If the file exists,
   * it is over-written
   */
  public DataFrameWriter write() {
    return new DataFrameWriter(defaultWriterRegistry, this);
  }

  /**
   * Adds the given column to this table. Column must either be empty or have size() == the
   * rowCount() of the table they're being added to. Column names in the table must remain unique.
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
   * For internal Tablesaw use only
   *
   * <p>Adds the given column to this table without performing duplicate-name or column size checks
   */
  public void internalAddWithoutValidation(final Column<?> c) {
    columnList.add(c);
  }

  /**
   * Throws an IllegalArgumentException if a column with the given name is already in the table, or
   * if the number of rows in the column does not match the number of rows in the table. Regarding
   * the latter rule, however, if the column is completely empty (size == 0), then it is filled with
   * missing values to match the rowSize() as a convenience.
   */
  private void validateColumn(final Column<?> newColumn) {
    Preconditions.checkNotNull(
        newColumn, "Attempted to add a null to the columns in table " + name);
    List<String> stringList = new ArrayList<>();
    for (String name : columnNames()) {
      stringList.add(name.toLowerCase());
    }
    if (stringList.contains(newColumn.name().toLowerCase())) {
      String message =
          String.format(
              "Cannot add column with duplicate name %s to table %s", newColumn.name(), name);
      throw new IllegalArgumentException(message);
    }

    checkColumnSize(newColumn);
  }

  /**
   * Throws an IllegalArgumentException if the column size doesn't match the rowCount() for the
   * table. Columns that are completely empty, however, are initialized to match rowcount by filling
   * with missing values
   */
  private void checkColumnSize(Column<?> newColumn) {
    if (columnCount() != 0) {
      if (!isEmpty()) {
        if (newColumn.isEmpty()) {
          while (newColumn.size() < rowCount()) {
            newColumn.appendMissing();
          }
        }
      }
      Preconditions.checkArgument(
          newColumn.size() == rowCount(),
          "Column "
              + newColumn.name()
              + " does not have the same number of rows as the other columns in the table.");
    }
  }

  /**
   * Adds the given column to this table at the given position in the column list. Columns must
   * either be empty or have size() == the rowCount() of the table they're being added to. Column
   * names in the table must remain unique.
   *
   * @param index Zero-based index into the column list
   * @param column Column to be added
   */
  public Table insertColumn(int index, Column<?> column) {
    validateColumn(column);
    columnList.add(index, column);
    return this;
  }

  /**
   * Return a new table (shallow copy) that contains all the columns in this table, in the order
   * given in the argument. Throw an IllegalArgument exception if the number of names given does not
   * match the number of columns in this table. NOTE: This does not make a copy of the columns, so
   * they are shared between the two tables.
   *
   * @param columnNames a column name or array of names
   */
  public Table reorderColumns(String... columnNames) {
    Preconditions.checkArgument(columnNames.length == columnCount());
    Table table = Table.create(name);
    for (String name : columnNames) {
      table.addColumns(column(name));
    }
    return table;
  }

  /**
   * Replaces an existing column (by index) in this table with the given new column
   *
   * @param colIndex Zero-based index of the column to be replaced
   * @param newColumn Column to be added
   */
  public Table replaceColumn(final int colIndex, final Column<?> newColumn) {
    removeColumns(column(colIndex));
    return insertColumn(colIndex, newColumn);
  }

  /**
   * Replaces an existing column (by name) in this table with the given new column
   *
   * @param columnName String name of the column to be replaced
   * @param newColumn Column to be added
   */
  public Table replaceColumn(final String columnName, final Column<?> newColumn) {
    int colIndex = columnIndex(columnName);
    return replaceColumn(colIndex, newColumn);
  }

  /**
   * Replaces an existing column having the same name of the given column with the given column
   *
   * @param newColumn Column to be added
   */
  public Table replaceColumn(Column<?> newColumn) {
    return replaceColumn(newColumn.name(), newColumn);
  }

  /** Sets the name of the table */
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

  /** Returns the number of columns in the table */
  @Override
  public int columnCount() {
    return columnList.size();
  }

  /** Returns the number of rows in the table */
  @Override
  public int rowCount() {
    int result = 0;
    if (!columnList.isEmpty()) {
      // all the columns have the same number of elements, so we can check any of them
      result = columnList.get(0).size();
    }
    return result;
  }

  /** Returns the list of columns */
  @Override
  public List<Column<?>> columns() {
    return columnList;
  }

  /** Returns the columns in this table as an array */
  public Column<?>[] columnArray() {
    return columnList.toArray(new Column<?>[columnCount()]);
  }

  /** Returns only the columns whose names are given in the input array */
  @Override
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
  @Override
  public int columnIndex(String columnName) {
    int columnIndex = -1;
    for (int i = 0; i < columnList.size(); i++) {
      if (columnList.get(i).name().equalsIgnoreCase(columnName)) {
        columnIndex = i;
        break;
      }
    }
    if (columnIndex == -1) {
      throw new IllegalArgumentException(
          String.format("Column %s is not present in table %s", columnName, name));
    }
    return columnIndex;
  }

  /**
   * Returns the index of the given column (its position in the list of columns)
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

  /** Returns the name of the table */
  @Override
  public String name() {
    return name;
  }

  /** Returns a List of the names of all the columns in this table */
  public List<String> columnNames() {
    return columnList.stream().map(Column::name).collect(toList());
  }

  /** Returns a table with the same columns and data as this table */
  public Table copy() {
    return inRange(0, this.rowCount());
  }

  /** Returns a table with the same columns as this table, but no data */
  public Table emptyCopy() {
    Table copy = new Table(name);
    for (Column<?> column : columnList) {
      copy.addColumns(column.emptyCopy());
    }
    return copy;
  }

  /**
   * Returns a table with the same columns as this table, but no data, initialized to the given row
   * size
   */
  public Table emptyCopy(int rowSize) {
    Table copy = new Table(name);
    for (Column<?> column : columnList) {
      copy.addColumns(column.emptyCopy(rowSize));
    }
    return copy;
  }

  /**
   * Copies the rows specified by Selection into newTable
   *
   * @param rows A Selection defining the rows to copy
   * @param newTable The table to copy the rows into
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void copyRowsToTable(Selection rows, Table newTable) {
    for (int columnIndex = 0; columnIndex < this.columnCount(); columnIndex++) {
      Column oldColumn = this.column(columnIndex);
      int r = 0;
      for (int i : rows) {
        newTable.column(columnIndex).set(r, oldColumn, i);
        r++;
      }
    }
  }

  /**
   * Copies the rows indicated by the row index values in the given array from oldTable to newTable
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void copyRowsToTable(int[] rows, Table newTable) {
    for (int columnIndex = 0; columnIndex < columnCount(); columnIndex++) {
      Column oldColumn = column(columnIndex);
      int r = 0;
      for (int i : rows) {
        newTable.column(columnIndex).set(r, oldColumn, i);
        r++;
      }
    }
  }

  /**
   * Returns {@code true} if the row @rowNumber in table1 holds the same data as the row at
   * rowNumber in table2
   */
  public static boolean compareRows(int rowNumber, Table table1, Table table2) {
    int columnCount = table1.columnCount();
    boolean result;
    for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
      ColumnType columnType = table1.column(columnIndex).type();
      result =
          columnType.compare(rowNumber, table2.column(columnIndex), table1.column(columnIndex));
      if (!result) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns true if every value in row1 is equal to the same value in row2, where row1 and row2 are
   * both rows from this table
   */
  private boolean duplicateRows(Row row1, Row row2) {
    if (row1.columnCount() != row2.columnCount()) {
      return false;
    }
    boolean result;
    for (int columnIndex = 0; columnIndex < row1.columnCount(); columnIndex++) {
      Column<?> c = column(columnIndex);
      result = c.equals(row1.getRowNumber(), row2.getRowNumber());
      if (!result) {
        return false;
      }
    }
    return true;
  }

  public Table[] sampleSplit(double table1Proportion) {
    Table[] tables = new Table[2];
    int table1Count = (int) Math.round(rowCount() * table1Proportion);

    Selection table2Selection = new BitmapBackedSelection();
    for (int i = 0; i < rowCount(); i++) {
      table2Selection.add(i);
    }
    Selection table1Selection = new BitmapBackedSelection();

    Selection table1Records = selectNRowsAtRandom(table1Count, rowCount());
    for (int table1Record : table1Records) {
      table1Selection.add(table1Record);
    }
    table2Selection.andNot(table1Selection);
    tables[0] = where(table1Selection);
    tables[1] = where(table2Selection);
    return tables;
  }

  /**
   * Splits the table into two stratified samples, this uses the specified column to divide the
   * table into groups, randomly assigning records to each according to the proportion given in
   * trainingProportion.
   *
   * @param column the column to be used for the stratified sampling
   * @param table1Proportion The proportion to go in the first table
   * @return An array two tables, with the first table having the proportion specified in the method
   *     parameter, and the second table having the balance of the rows
   */
  public Table[] stratifiedSampleSplit(CategoricalColumn<?> column, double table1Proportion) {
    Preconditions.checkArgument(
        containsColumn(column),
        "The categorical column must be part of the table, you can create a string column and add it to this table before sampling.");
    final Table first = emptyCopy();
    final Table second = emptyCopy();

    splitOn(column)
        .asTableList()
        .forEach(
            tab -> {
              Table[] splits = tab.sampleSplit(table1Proportion);
              first.append(splits[0]);
              second.append(splits[1]);
            });

    return new Table[] {first, second};
  }

  /**
   * Returns a table consisting of randomly selected records from this table. The sample size is
   * based on the given proportion
   *
   * @param proportion The proportion to go in the sample
   */
  public Table sampleX(double proportion) {
    Preconditions.checkArgument(
        proportion <= 1 && proportion >= 0, "The sample proportion must be between 0 and 1");

    int tableSize = (int) Math.round(rowCount() * proportion);
    return where(selectNRowsAtRandom(tableSize, rowCount()));
  }

  /**
   * Returns a table consisting of randomly selected records from this table
   *
   * @param nRows The number of rows to go in the sample
   */
  public Table sampleN(int nRows) {
    Preconditions.checkArgument(
        nRows > 0 && nRows < rowCount(),
        "The number of rows sampled must be greater than 0 and less than the number of rows in the table.");
    return where(selectNRowsAtRandom(nRows, rowCount()));
  }

  /** Clears all the data from this table */
  @Override
  public void clear() {
    columnList.forEach(Column::clear);
  }

  /** Returns a new table containing the first {@code nrows} of data in this table */
  public Table first(int nRows) {
    int newRowCount = Math.min(nRows, rowCount());
    return inRange(0, newRowCount);
  }

  /** Returns a new table containing the last {@code nrows} of data in this table */
  public Table last(int nRows) {
    int newRowCount = Math.min(nRows, rowCount());
    return inRange(rowCount() - newRowCount, rowCount());
  }

  /**
   * Sorts this table into a new table on the columns indexed
   *
   * <p>if index is negative then sort that column in descending order otherwise sort ascending
   */
  public Table sortOn(int... columnIndexes) {
    List<String> names = new ArrayList<>();
    for (int i : columnIndexes) {
      if (i >= 0) {
        names.add(columnList.get(i).name());
      } else {
        names.add("-" + columnList.get(-i).name());
      }
    }
    return sortOn(names.toArray(new String[names.size()]));
  }

  /**
   * Returns a copy of this table sorted on the given column names, applied in order,
   *
   * <p>if column name starts with - then sort that column descending otherwise sort ascending
   */
  public Table sortOn(String... columnNames) {
    return this.sortOn(Sort.create(this, columnNames));
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
   * Returns a copy of this table sorted using the given sort key.
   *
   * @param key to sort on.
   * @return a sorted copy of this table.
   */
  public Table sortOn(Sort key) {
    Preconditions.checkArgument(!key.isEmpty());
    if (key.size() == 1) {
      IntComparator comparator = SortUtils.getComparator(this, key);
      return parallelSortOn(comparator);
    }
    IntComparatorChain chain = SortUtils.getChain(this, key);
    return parallelSortOn(chain);
  }

  /**
   * Returns a copy of this table sorted using the given comparator. This method sorts in a single
   * thread, as is required for using Comparator<Row>
   */
  private Table sortOn(IntComparator rowComparator) {
    Table newTable = emptyCopy(rowCount());

    int[] newRows = rows();
    IntArrays.mergeSort(newRows, rowComparator);

    copyRowsToTable(newRows, newTable);
    return newTable;
  }

  /** Returns a copy of this table sorted in parallel using the given comparator */
  private Table parallelSortOn(IntComparator rowComparator) {
    Table newTable = emptyCopy(rowCount());

    int[] newRows = rows();
    IntArrays.parallelQuickSort(newRows, rowComparator);

    copyRowsToTable(newRows, newTable);
    return newTable;
  }

  /** Returns a copy of this table sorted using the given comparator */
  public Table sortOn(Comparator<Row> rowComparator) {
    Row row1 = new Row(this);
    Row row2 = new Row(this);
    return sortOn( // Note: Never user parallel sort here as Row isn't remotely thread-safe
        (IntComparator)
            (k1, k2) -> {
              row1.at(k1);
              row2.at(k2);
              return rowComparator.compare(row1, row2);
            });
  }

  /** Returns an array of ints of the same number of rows as the table */
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
   * @param rowIndex The row in sourceTable to add to this table
   * @param sourceTable A table with the same column structure as this table
   */
  public void addRow(int rowIndex, Table sourceTable) {
    for (int i = 0; i < columnCount(); i++) {
      column(i).appendObj(sourceTable.column(i).get(rowIndex));
    }
  }

  /** Returns a new Row object with its position set to the given zero-based row index. */
  public Row row(int rowIndex) {
    Row row = new Row(Table.this);
    row.at(rowIndex);
    return row;
  }

  /** Returns a table containing the rows contained in the given array of row indices */
  public Table rows(int... rowNumbers) {
    Preconditions.checkArgument(Ints.max(rowNumbers) <= rowCount());
    return where(Selection.with(rowNumbers));
  }

  /** Returns a table EXCLUDING the rows contained in the given array of row indices */
  public Table dropRows(int... rowNumbers) {
    Preconditions.checkArgument(Ints.max(rowNumbers) <= rowCount());
    Selection selection = Selection.withRange(0, rowCount()).andNot(Selection.with(rowNumbers));
    return where(selection);
  }

  /**
   * Returns a new table containing the first rowCount rows if rowCount positive. Returns the last
   * rowCount rows if rowCount negative.
   */
  public Table inRange(int rowCount) {
    Preconditions.checkArgument(rowCount <= rowCount());
    int rowStart = rowCount >= 0 ? 0 : rowCount() + rowCount;
    int rowEnd = rowCount >= 0 ? rowCount : rowCount();
    return where(Selection.withRange(rowStart, rowEnd));
  }

  /**
   * Returns a new table containing the rows contained in the range from rowStart inclusive to
   * rowEnd exclusive
   */
  public Table inRange(int rowStart, int rowEnd) {
    Preconditions.checkArgument(rowEnd <= rowCount());
    return where(Selection.withRange(rowStart, rowEnd));
  }

  /**
   * Returns a new table EXCLUDING the first rowCount rows if rowCount positive. Drops the last
   * rowCount rows if rowCount negative.
   */
  public Table dropRange(int rowCount) {
    Preconditions.checkArgument(rowCount <= rowCount());
    int rowStart = rowCount >= 0 ? rowCount : 0;
    int rowEnd = rowCount >= 0 ? rowCount() : rowCount() + rowCount;
    return where(Selection.withRange(rowStart, rowEnd));
  }

  /**
   * Returns a table EXCLUDING the rows contained in the range from rowStart inclusive to rowEnd
   * exclusive
   */
  public Table dropRange(int rowStart, int rowEnd) {
    Preconditions.checkArgument(rowEnd <= rowCount());
    return where(Selection.withoutRange(0, rowCount(), rowStart, rowEnd));
  }

  /** Returns a table containing the rows contained in the given Selection */
  public Table where(Selection selection) {
    Table newTable = this.emptyCopy(selection.size());
    copyRowsToTable(selection, newTable);
    return newTable;
  }

  /** Returns a new Table made by applying the given function to this table */
  public Table where(Function<Table, Selection> selection) {
    return where(selection.apply(this));
  }

  /**
   * Returns a new Table made by EXCLUDING any rows returned when the given function is applied to
   * this table
   */
  public Table dropWhere(Function<Table, Selection> selection) {
    return where(not(selection));
  }

  /** Returns a table EXCLUDING the rows contained in the given Selection */
  public Table dropWhere(Selection selection) {
    Selection opposite = new BitmapBackedSelection();
    opposite.addRange(0, rowCount());
    opposite.andNot(selection);
    Table newTable = this.emptyCopy(opposite.size());
    copyRowsToTable(opposite, newTable);
    return newTable;
  }

  /**
   * Returns a pivot on this table, where: The first column contains unique values from the index
   * column1 There are n additional columns, one for each unique value in column2 The values in each
   * of the cells in these new columns are the result of applying the given AggregateFunction to the
   * data in column3, grouped by the values of column1 and column2
   */
  public Table pivot(
      CategoricalColumn<?> column1,
      CategoricalColumn<?> column2,
      NumericColumn<?> column3,
      AggregateFunction<?, ?> aggregateFunction) {
    return PivotTable.pivot(this, column1, column2, column3, aggregateFunction);
  }

  /**
   * Returns a pivot on this table, where: The first column contains unique values from the index
   * column1 There are n additional columns, one for each unique value in column2 The values in each
   * of the cells in these new columns are the result of applying the given AggregateFunction to the
   * data in column3, grouped by the values of column1 and column2
   */
  public Table pivot(
      String column1Name,
      String column2Name,
      String column3Name,
      AggregateFunction<?, ?> aggregateFunction) {
    return pivot(
        categoricalColumn(column1Name),
        categoricalColumn(column2Name),
        numberColumn(column3Name),
        aggregateFunction);
  }

  /**
   * Returns a non-overlapping and exhaustive collection of "slices" over this table. Each slice is
   * like a virtual table containing a subset of the records in this table
   *
   * <p>This method is intended for advanced or unusual operations on the subtables. If you want to
   * calculate summary statistics for each subtable, the summarize methods (e.g)
   *
   * <p>table.summarize(myColumn, mean, median).by(columns)
   *
   * <p>are preferred
   */
  public TableSliceGroup splitOn(String... columns) {
    return splitOn(categoricalColumns(columns).toArray(new CategoricalColumn<?>[columns.length]));
  }

  /**
   * Returns a non-overlapping and exhaustive collection of "slices" over this table. Each slice is
   * like a virtual table containing a subset of the records in this table
   *
   * <p>This method is intended for advanced or unusual operations on the subtables. If you want to
   * calculate summary statistics for each subtable, the summarize methods (e.g)
   *
   * <p>table.summarize(myColumn, mean, median).by(columns)
   *
   * <p>are preferred
   */
  public TableSliceGroup splitOn(CategoricalColumn<?>... columns) {
    return StandardTableSliceGroup.create(this, columns);
  }

  /**
   * Returns the unique records in this table, such that any record that appears more than once in
   * this table, appears only once in the returned table.
   */
  public Table dropDuplicateRows() {

    Table temp = emptyCopy();
    Int2ObjectMap<IntArrayList> uniqueHashes = new Int2ObjectOpenHashMap<>();
    // ListMultimap<Integer, Integer> uniqueHashes = ArrayListMultimap.create();
    for (Row row : this) {
      if (!isDuplicate(row, uniqueHashes)) {
        temp.append(row);
      }
    }
    return temp;
  }

  /**
   * Returns true if all the values in row are identical to those in another row previously seen and
   * recorded in the list.
   *
   * @param row the row to evaluate
   * @param uniqueHashes a map of row hashes to the id of an exemplar row that produces that hash.
   *     If two different rows produce the same hash, then the row number for each is placed in the
   *     list, so that there are exemplars for both
   * @return true if the row's values exactly match a row that was previously seen
   */
  private boolean isDuplicate(Row row, Int2ObjectMap<IntArrayList> uniqueHashes) {
    int hash = row.rowHash();
    if (!uniqueHashes.containsKey(hash)) {
      IntArrayList rowNumbers = new IntArrayList();
      rowNumbers.add(row.getRowNumber());
      uniqueHashes.put(hash, rowNumbers);
      return false;
    }

    // the hashmap contains the hash, make sure the actual row values match
    IntArrayList matchingKeys = uniqueHashes.get(hash);

    for (int key : matchingKeys) {
      Row oldRow = this.row(key);
      if (duplicateRows(row, oldRow)) {
        return true;
      } else {
        uniqueHashes.get(hash).add(row.getRowNumber());
        return false;
      }
    }
    return true;
  }

  /** Returns only those records in this table that have no columns with missing values */
  public Table dropRowsWithMissingValues() {

    Selection missing = new BitmapBackedSelection();

    for (int row = 0; row < rowCount(); row++) {
      for (int col = 0; col < columnCount(); col++) {
        Column<?> c = column(col);
        if (c.isMissing(row)) {
          missing.add(row);
          break;
        }
      }
    }
    Selection notMissing = Selection.withRange(0, rowCount());
    notMissing.andNot(missing);
    Table temp = emptyCopy(notMissing.size());
    copyRowsToTable(notMissing, temp);
    return temp;
  }

  /**
   * Returns a new table containing copies of the selected columns from this table
   *
   * @param columns The columns to copy into the new table
   * @see #retainColumns(Column[])
   */
  public Table selectColumns(Column<?>... columns) {
    Table t = Table.create(this.name);
    for (Column<?> c : columns) {
      t.addColumns(c.copy());
    }
    return t;
  }

  /**
   * Returns a new table containing copies of the selected columns from this table
   *
   * @param columnNames The names of the columns to include
   * @see #retainColumns(String[])
   */
  public Table selectColumns(String... columnNames) {
    Table t = Table.create(this.name);
    for (String s : columnNames) {
      t.addColumns(column(s).copy());
    }
    return t;
  }

  /**
   * Returns a new table containing copies of all the columns from this table, except those at the
   * given indexes
   *
   * @param columnIndexes The indexes of the columns to exclude
   * @see #removeColumns(int[])
   */
  public Table rejectColumns(int... columnIndexes) {
    Table t = Table.create(this.name);
    RoaringBitmap bm = new RoaringBitmap();
    bm.add((long) 0, columnCount());
    RoaringBitmap excluded = new RoaringBitmap();
    excluded.add(columnIndexes);
    bm.andNot(excluded);
    for (int i : bm) {
      t.addColumns(column(i).copy());
    }
    return t;
  }

  /**
   * Returns a new table containing copies of all the columns from this table, except those named in
   * the argument
   *
   * @param columnNames The names of the columns to exclude
   * @see #removeColumns(int[])
   */
  public Table rejectColumns(String... columnNames) {
    IntArrayList indices = new IntArrayList();
    for (String s : columnNames) {
      indices.add(columnIndex(s));
    }
    return rejectColumns(indices.toIntArray());
  }

  /**
   * Returns a new table containing copies of all the columns from this table, except those named in
   * the argument
   *
   * @param columns The names of the columns to exclude
   * @see #removeColumns(int[])
   */
  public Table rejectColumns(Column<?>... columns) {
    IntArrayList indices = new IntArrayList();
    for (Column<?> c : columns) {
      indices.add(columnIndex(c));
    }
    return rejectColumns(indices.toIntArray());
  }

  /**
   * Returns a new table containing copies of the columns at the given indexes
   *
   * @param columnIndexes The indexes of the columns to include
   * @see #retainColumns(int[])
   */
  public Table selectColumns(int... columnIndexes) {
    Table t = Table.create(this.name);
    RoaringBitmap bm = new RoaringBitmap();
    bm.add(columnIndexes);
    for (int i : bm) {
      t.addColumns(column(i).copy());
    }
    return t;
  }

  /** Removes the given columns from this table and returns this table */
  @Override
  public Table removeColumns(Column<?>... columns) {
    columnList.removeAll(Arrays.asList(columns));
    return this;
  }

  /** Removes all columns with missing values from this table, and returns this table. */
  public Table removeColumnsWithMissingValues() {
    removeColumns(columnList.stream().filter(x -> x.countMissing() > 0).toArray(Column<?>[]::new));
    return this;
  }

  /**
   * Removes all columns except for those given in the argument from this table and returns this
   * table
   */
  public Table retainColumns(Column<?>... columns) {
    List<Column<?>> retained = Arrays.asList(columns);
    columnList.clear();
    columnList.addAll(retained);
    return this;
  }

  /**
   * Removes all columns except for those given in the argument from this table and returns this
   * table
   */
  public Table retainColumns(int... columnIndexes) {
    List<Column<?>> retained = columns(columnIndexes);
    columnList.clear();
    columnList.addAll(retained);
    return this;
  }

  /**
   * Removes all columns except for those given in the argument from this table and returns this
   * table
   */
  public Table retainColumns(String... columnNames) {
    List<Column<?>> retained = columns(columnNames);
    columnList.clear();
    columnList.addAll(retained);
    return this;
  }

  /** Returns this table after adding the data from the argument */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public Table append(Relation tableToAppend) {
    for (final Column column : columnList) {
      final Column columnToAppend = tableToAppend.column(column.name());
      column.append(columnToAppend);
    }
    return this;
  }

  /**
   * Appends the given row to this table and returns the table.
   *
   * <p>Note: The table is modified in-place TODO: Performance
   */
  public Table append(Row row) {
    for (int i = 0; i < row.columnCount(); i++) {
      column(i).appendObj(row.getObject(i));
    }
    return this;
  }

  /** Removes the columns with the given names from this table and returns this table */
  @Override
  public Table removeColumns(String... columns) {
    return (Table) super.removeColumns(columns);
  }

  /** Removes the columns at the given indices from this table and returns this table */
  @Override
  public Table removeColumns(int... columnIndexes) {
    return (Table) super.removeColumns(columnIndexes);
  }

  /**
   * Appends an empty row and returns a Row object indexed to the newly added row so values can be
   * set.
   *
   * <p>Intended usage:
   *
   * <p>for (int i = 0; ...) { Row row = table.appendRow(); row.setString("name", "Bob");
   * row.setFloat("IQ", 123.4f); ...etc. }
   */
  public Row appendRow() {
    for (final Column<?> column : columnList) {
      column.appendMissing();
    }
    return row(rowCount() - 1);
  }

  /**
   * Add all the columns of tableToConcatenate to this table Note: The columns in the result must
   * have unique names, when compared case insensitive Note: Both tables must have the same number
   * of rows
   *
   * @param tableToConcatenate The table containing the columns to be added
   * @return This table
   */
  public Table concat(Table tableToConcatenate) {
    Preconditions.checkArgument(
        tableToConcatenate.rowCount() == this.rowCount(),
        "Both tables must have the same number of rows to concatenate them.");
    for (Column<?> column : tableToConcatenate.columns()) {
      this.addColumns(column);
    }
    return this;
  }

  /**
   * Returns a {@link Summarizer} that can be used to summarize the column with the given name(s)
   * using the given functions. This object implements reduce/aggregation operations on a table.
   *
   * <p>Summarizer can return the results as a table using the Summarizer:apply() method. Summarizer
   * can compute sub-totals using the Summarizer:by() method.
   */
  public Summarizer summarize(String columName, AggregateFunction<?, ?>... functions) {
    return summarize(column(columName), functions);
  }

  /**
   * Returns a {@link Summarizer} that can be used to summarize the column with the given name(s)
   * using the given functions. This object implements reduce/aggregation operations on a table.
   *
   * <p>Summarizer can return the results as a table using the Summarizer:apply() method. Summarizer
   * can compute sub-totals using the Summarizer:by() method.
   */
  public Summarizer summarize(List<String> columnNames, AggregateFunction<?, ?>... functions) {
    return new Summarizer(this, columnNames, functions);
  }

  /**
   * Returns a {@link Summarizer} that can be used to summarize the column with the given name(s)
   * using the given functions. This object implements reduce/aggregation operations on a table.
   *
   * <p>Summarizer can return the results as a table using the Summarizer:apply() method. Summarizer
   * can compute sub-totals using the Summarizer:by() method.
   */
  public Summarizer summarize(
      String numericColumn1Name, String numericColumn2Name, AggregateFunction<?, ?>... functions) {
    return summarize(column(numericColumn1Name), column(numericColumn2Name), functions);
  }

  /**
   * Returns a {@link Summarizer} that can be used to summarize the column with the given name(s)
   * using the given functions. This object implements reduce/aggregation operations on a table.
   *
   * <p>Summarizer can return the results as a table using the Summarizer:apply() method. Summarizer
   * can compute sub-totals using the Summarizer:by() method.
   */
  public Summarizer summarize(
      String col1Name, String col2Name, String col3Name, AggregateFunction<?, ?>... functions) {
    return summarize(column(col1Name), column(col2Name), column(col3Name), functions);
  }

  /**
   * Returns a {@link Summarizer} that can be used to summarize the column with the given name(s)
   * using the given functions. This object implements reduce/aggregation operations on a table.
   *
   * <p>Summarizer can return the results as a table using the Summarizer:apply() method. Summarizer
   * can compute sub-totals using the Summarizer:by() method.
   */
  public Summarizer summarize(
      String col1Name,
      String col2Name,
      String col3Name,
      String col4Name,
      AggregateFunction<?, ?>... functions) {
    return summarize(
        column(col1Name), column(col2Name), column(col3Name), column(col4Name), functions);
  }

  /**
   * Returns a {@link Summarizer} that can be used to summarize the column with the given name(s)
   * using the given functions. This object implements reduce/aggregation operations on a table.
   *
   * <p>Summarizer can return the results as a table using the Summarizer:apply() method. Summarizer
   * can compute sub-totals using the Summarizer:by() method.
   */
  public Summarizer summarize(Column<?> numberColumn, AggregateFunction<?, ?>... function) {
    return new Summarizer(this, numberColumn, function);
  }

  /**
   * Returns a {@link Summarizer} that can be used to summarize the column with the given name(s)
   * using the given functions. This object implements reduce/aggregation operations on a table.
   *
   * <p>Summarizer can return the results as a table using the Summarizer:apply() method. Summarizer
   * can compute sub-totals using the Summarizer:by() method.
   */
  public Summarizer summarize(
      Column<?> column1, Column<?> column2, AggregateFunction<?, ?>... function) {
    return new Summarizer(this, column1, column2, function);
  }

  /**
   * Returns a {@link Summarizer} that can be used to summarize the column with the given name(s)
   * using the given functions. This object implements reduce/aggregation operations on a table.
   *
   * <p>Summarizer can return the results as a table using the Summarizer:apply() method. Summarizer
   * can compute sub-totals using the Summarizer:by() method.
   */
  public Summarizer summarize(
      Column<?> column1,
      Column<?> column2,
      Column<?> column3,
      AggregateFunction<?, ?>... function) {
    return new Summarizer(this, column1, column2, column3, function);
  }

  /**
   * Returns a {@link Summarizer} that can be used to summarize the column with the given name(s)
   * using the given functions. This object implements reduce/aggregation operations on a table.
   *
   * <p>Summarizer can return the results as a table using the Summarizer:apply() method. Summarizer
   * can compute sub-totals using the Summarizer:by() method.
   */
  public Summarizer summarize(
      Column<?> column1,
      Column<?> column2,
      Column<?> column3,
      Column<?> column4,
      AggregateFunction<?, ?>... function) {
    return new Summarizer(this, column1, column2, column3, column4, function);
  }

  /**
   * Returns a table with n by m + 1 cells. The first column contains labels, the other cells
   * contains the counts for every unique combination of values from the two specified columns in
   * this table.
   */
  public Table xTabCounts(String column1Name, String column2Name) {
    return CrossTab.counts(this, categoricalColumn(column1Name), categoricalColumn(column2Name));
  }

  /**
   * Returns a table with n by m + 1 cells. The first column contains labels, the other cells
   * contains the row percents for every unique combination of values from the two specified columns
   * in this table. Row percents total to 100% in every row.
   */
  public Table xTabRowPercents(String column1Name, String column2Name) {
    return CrossTab.rowPercents(this, column1Name, column2Name);
  }

  /**
   * Returns a table with n by m + 1 cells. The first column contains labels, the other cells
   * contains the column percents for every unique combination of values from the two specified
   * columns in this table. Column percents total to 100% in every column.
   */
  public Table xTabColumnPercents(String column1Name, String column2Name) {
    return CrossTab.columnPercents(this, column1Name, column2Name);
  }

  /**
   * Returns a table with n by m + 1 cells. The first column contains labels, the other cells
   * contains the proportion for a unique combination of values from the two specified columns in
   * this table
   */
  public Table xTabTablePercents(String column1Name, String column2Name) {
    return CrossTab.tablePercents(this, column1Name, column2Name);
  }

  /**
   * TODO: Rename the method to xTabProportions, deprecating this version Returns a table with two
   * columns, the first contains a value each unique value in the argument, and the second contains
   * the proportion of observations having that value
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
   * Returns a table containing two columns, the grouping column, and a column named "Count" that
   * contains the counts for each grouping column value
   */
  public Table countBy(CategoricalColumn<?>... groupingColumns) {
    String[] names = new String[groupingColumns.length];
    for (int i = 0; i < groupingColumns.length; i++) {
      names[i] = groupingColumns[i].name();
    }
    return countBy(names);
  }

  /**
   * Returns a table containing a column for each grouping column, and a column named "Count" that
   * contains the counts for each combination of grouping column values
   *
   * @param categoricalColumnNames The name(s) of one or more CategoricalColumns in this table
   * @return A table containing counts of rows grouped by the categorical columns
   * @throws ClassCastException if the categoricalColumnName parameter is the name of a column that
   *     does not * implement categorical
   */
  public Table countBy(String... categoricalColumnNames) {
    Table t = summarize(column(0).name(), count).by(categoricalColumnNames);
    t.column(t.columnCount() - 1).setName("Count");
    t.replaceColumn("Count", (t.doubleColumn("Count").asIntColumn()));
    return t;
  }

  /**
   * Returns a new DataFrameJoiner initialized with multiple {@code columnNames}
   *
   * @param columnNames Name of the columns to join on.
   * @return The new DataFrameJoiner
   */
  public DataFrameJoiner joinOn(String... columnNames) {
    return new DataFrameJoiner(this, columnNames);
  }

  /** Returns a table containing the number of missing values in each column in this table */
  public Table missingValueCounts() {
    return summarize(columnNames(), countMissing).apply();
  }

  @Override
  public Iterator<Row> iterator() {

    return new Iterator<Row>() {

      private final Row row = new Row(Table.this);

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
   * Iterates over rolling sets of rows. I.e. 0 to n-1, 1 to n, 2 to n+1, etc.
   *
   * @param n the number of rows to return for each iteration
   */
  public Iterator<Row[]> rollingIterator(int n) {

    return new Iterator<Row[]>() {

      private int currRow = 0;

      @Override
      public Row[] next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        Row[] rows = new Row[n];
        for (int i = 0; i < n; i++) {
          rows[i] = new Row(Table.this, currRow + i);
        }
        currRow++;
        return rows;
      }

      @Override
      public boolean hasNext() {
        return currRow + n <= rowCount();
      }
    };
  }

  /**
   * Streams over stepped sets of rows. I.e. 0 to n-1, n to 2n-1, 2n to 3n-1, etc. Only returns full
   * sets of rows.
   *
   * @param n the number of rows to return for each iteration
   */
  public Iterator<Row[]> steppingIterator(int n) {

    return new Iterator<Row[]>() {

      private int currRow = 0;

      @Override
      public Row[] next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        Row[] rows = new Row[n];
        for (int i = 0; i < n; i++) {
          rows[i] = new Row(Table.this, currRow + i);
        }
        currRow += n;
        return rows;
      }

      @Override
      public boolean hasNext() {
        return currRow + n <= rowCount();
      }
    };
  }

  /** Returns the rows in this table as a Stream */
  public Stream<Row> stream() {
    return Streams.stream(iterator());
  }

  /**
   * Streams over stepped sets of rows. I.e. 0 to n-1, n to 2n-1, 2n to 3n-1, etc. Only returns full
   * sets of rows.
   *
   * @param n the number of rows to return for each iteration
   */
  public Stream<Row[]> steppingStream(int n) {
    return Streams.stream(steppingIterator(n));
  }

  /**
   * Streams over rolling sets of rows. I.e. 0 to n-1, 1 to n, 2 to n+1, etc.
   *
   * @param n the number of rows to return for each iteration
   */
  public Stream<Row[]> rollingStream(int n) {
    return Streams.stream(rollingIterator(n));
  }

  /**
   * Transposes data in the table, switching rows for columns. For example, a table like this.<br>
   * value1 | value2 |<br>
   * -------------------------------<br>
   * 1 | 2 |<br>
   * 1.1 | 2.1 |<br>
   * 1.2 | 2.2 |<br>
   *
   * <p>Is transposed into the following<br>
   * 0 | 1 | 2 |<br>
   * -------------------------------------<br>
   * 1 | 1.1 | 1.2 |<br>
   * 2 | 2.1 | 2.2 |<br>
   *
   * @see Table#transpose(boolean,boolean)
   * @return transposed table
   */
  public Table transpose() {
    return transpose(false, false);
  }

  /**
   * Transposes data in the table, switching rows for columns. For example, a table like this.<br>
   * label | value1 | value2 |<br>
   * -------------------------------<br>
   * row1 | 1 | 2 |<br>
   * row2 | 1.1 | 2.1 |<br>
   * row3 | 1.2 | 2.2 |<br>
   *
   * <p>Is transposed into the following<br>
   * label | row1 | row2 | row3 |<br>
   * -------------------------------------<br>
   * value1 | 1 | 1.1 | 1.2 |<br>
   * value2 | 2 | 2.1 | 2.2 |<br>
   *
   * @param includeColumnHeadingsAsFirstColumn Toggle whether to include the column headings as
   *     first column in result
   * @param useFirstColumnForHeadings Use the first column as the column headings in the result.
   *     Useful if the data set already has a first column which contains a set of labels
   * @return The transposed table
   */
  public Table transpose(
      boolean includeColumnHeadingsAsFirstColumn, boolean useFirstColumnForHeadings) {
    if (this.columnCount() == 0) {
      return this;
    }

    // Validate first
    int columnOffset = useFirstColumnForHeadings ? 1 : 0;
    ColumnType resultColumnType = validateTableHasSingleColumnType(columnOffset);

    Table transposed = Table.create(this.name);
    if (includeColumnHeadingsAsFirstColumn) {
      String columnName = useFirstColumnForHeadings ? this.column(0).name() : "0";
      StringColumn labelColumn = StringColumn.create(columnName);
      for (int i = columnOffset; i < this.columnCount(); i++) {
        Column<?> columnToTranspose = this.column(i);
        labelColumn.append(columnToTranspose.name());
      }
      transposed.addColumns(labelColumn);
    }

    if (useFirstColumnForHeadings) {
      transpose(transposed, resultColumnType, row -> String.valueOf(this.get(row, 0)), 1);
    } else {
      // default column labelling
      return transpose(
          transposed, resultColumnType, row -> String.valueOf(transposed.columnCount()), 0);
    }
    return transposed;
  }

  private ColumnType validateTableHasSingleColumnType(int startingColumn) {
    // If all columns are of the same type
    ColumnType[] columnTypes = this.typeArray();
    long distinctColumnTypesCount =
        Arrays.stream(columnTypes).skip(startingColumn).distinct().count();
    if (distinctColumnTypesCount > 1) {
      throw new IllegalArgumentException(
          "This operation currently only supports tables where value columns are of the same type");
    }
    return columnTypes[startingColumn];
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private Table transpose(
      Table transposed,
      ColumnType resultColumnType,
      IntFunction<String> columnNameExtractor,
      int startingColumn) {

    for (int row = 0; row < this.rowCount(); row++) {
      String columnName = columnNameExtractor.apply(row);
      Column column = resultColumnType.create(columnName);

      for (int col = startingColumn; col < this.columnCount(); col++) {
        column.append(this.column(col), row);
      }
      transposed.addColumns(column);
    }
    return transposed;
  }

  /**
   * Melt implements the 'tidy' melt operation as described in these papers by Hadley Wickham.
   *
   * <p>Tidy concepts: see https://www.jstatsoft.org/article/view/v059i10
   *
   * <p>Cast function details: see https://www.jstatsoft.org/article/view/v021i12
   *
   * <p>In short, melt turns columns into rows, but in a particular way. Used with the cast method,
   * it can help make data tidy. In a tidy dataset, every variable is a column and every observation
   * a row.
   *
   * <p>This method returns a table that contains all the data in this table, but organized such
   * that there is a set of identifier variables (columns) and a single measured variable (column).
   * For example, given a table with columns:
   *
   * <p>patient_id, gender, age, weight, temperature,
   *
   * <p>it returns a table with the columns:
   *
   * <p>patient_id, variable, value
   *
   * <p>In the new format, the strings age, weight, and temperature have become cells in the
   * measurement table, such that a single row in the source table might look like this in the
   * result table:
   *
   * <p>1234, gender, male 1234, age, 42 1234, weight, 186 1234, temperature, 97.4
   *
   * <p>This kind of structure often makes for a good intermediate format for performing subsequent
   * transformations. It is especially useful when combined with the {@link #cast()} operation
   *
   * @param idVariables A list of column names intended to be used as identifiers. In he example,
   *     only patient_id would be an identifier
   * @param measuredVariables A list of columns intended to be used as measured variables. All
   *     columns must have the same type
   * @param dropMissing drop any row where the value is missing
   */
  public Table melt(
      List<String> idVariables, List<NumericColumn<?>> measuredVariables, Boolean dropMissing) {

    Table result = Table.create(name);
    for (String idColName : idVariables) {
      result.addColumns(column(idColName).type().create(idColName));
    }
    result.addColumns(
        StringColumn.create(MELT_VARIABLE_COLUMN_NAME),
        DoubleColumn.create(MELT_VALUE_COLUMN_NAME));

    List<String> measureColumnNames =
        measuredVariables.stream().map(Column::name).collect(Collectors.toList());

    TableSliceGroup slices = splitOn(idVariables.toArray(new String[0]));
    for (TableSlice slice : slices) {
      for (Row row : slice) {
        for (String colName : measureColumnNames) {
          if (!dropMissing || !row.isMissing(colName)) {
            writeIdVariables(idVariables, result, row);
            result.stringColumn(MELT_VARIABLE_COLUMN_NAME).append(colName);
            double value = row.getNumber(colName);
            result.doubleColumn(MELT_VALUE_COLUMN_NAME).append(value);
          }
        }
      }
    }

    return result;
  }

  /** Writes one row of id variables into the result table */
  private void writeIdVariables(List<String> idVariables, Table result, Row row) {
    for (String id : idVariables) {
      Column<?> resultColumn = result.column(id);
      final ColumnType columnType = resultColumn.type();
      if (columnType.equals(ColumnType.STRING)) {
        StringColumn sc = (StringColumn) resultColumn;
        sc.append(row.getString(resultColumn.name()));
      } else if (columnType.equals(ColumnType.INTEGER)) {
        IntColumn ic = (IntColumn) resultColumn;
        ic.append(row.getInt(resultColumn.name()));
      } else if (columnType.equals(ColumnType.LONG)) {
        LongColumn ic = (LongColumn) resultColumn;
        ic.append(row.getLong(resultColumn.name()));
      } else if (columnType.equals(ColumnType.SHORT)) {
        ShortColumn ic = (ShortColumn) resultColumn;
        ic.append(row.getShort(resultColumn.name()));
      } else if (columnType.equals(ColumnType.LOCAL_DATE)) {
        DateColumn ic = (DateColumn) resultColumn;
        ic.appendInternal(row.getPackedDate(resultColumn.name()));
      } else if (columnType.equals(ColumnType.LOCAL_DATE_TIME)) {
        DateTimeColumn ic = (DateTimeColumn) resultColumn;
        ic.appendInternal(row.getPackedDateTime(resultColumn.name()));
      } else if (columnType.equals(ColumnType.LOCAL_TIME)) {
        TimeColumn ic = (TimeColumn) resultColumn;
        ic.appendInternal(row.getPackedTime(resultColumn.name()));
      } else if (columnType.equals(ColumnType.INSTANT)) {
        InstantColumn ic = (InstantColumn) resultColumn;
        ic.appendInternal(row.getPackedInstant(resultColumn.name()));
      } else if (columnType.equals(ColumnType.BOOLEAN)) {
        BooleanColumn ic = (BooleanColumn) resultColumn;
        ic.append(row.getBooleanAsByte(resultColumn.name()));
      } else if (columnType.equals(ColumnType.DOUBLE)) {
        DoubleColumn ic = (DoubleColumn) resultColumn;
        ic.append(row.getDouble(resultColumn.name()));
      } else if (columnType.equals(ColumnType.FLOAT)) {
        FloatColumn ic = (FloatColumn) resultColumn;
        ic.append(row.getFloat(resultColumn.name()));
      } else {
        throw new IllegalArgumentException("melt() does not support column type " + columnType);
      }
    }
  }

  /**
   * Cast implements the 'tidy' cast operation as described in these papers by Hadley Wickham:
   *
   * <p>Cast takes a table in 'molten' format, such as is produced by the {@link #melt(List, List,
   * Boolean)} t} method, and returns a version in standard tidy format.
   *
   * <p>The molten table should have a StringColumn called "variable" and a column called "value"
   * Every unique variable name will become a column in the output table.
   *
   * <p>All other columns in this table are considered identifier variable. Each combination of
   * identifier variables specifies an observation, so there will be one row for each, with the
   * other variables added.
   *
   * <p>Variable columns are returned in an arbitrary order. Use {@link #reorderColumns(String...)}
   * if column order is important.
   *
   * <p>Tidy concepts: see https://www.jstatsoft.org/article/view/v059i10
   *
   * <p>Cast function details: see https://www.jstatsoft.org/article/view/v021i12
   */
  public Table cast() {
    StringColumn variableNames = stringColumn(MELT_VARIABLE_COLUMN_NAME);
    List<Column<?>> idColumns =
        columnList.stream()
            .filter(
                column ->
                    !column.name().equals(MELT_VARIABLE_COLUMN_NAME)
                        && !column.name().equals(MELT_VALUE_COLUMN_NAME))
            .collect(toList());
    Table result = Table.create(name);
    for (Column<?> idColumn : idColumns) {
      result.addColumns(idColumn.type().create(idColumn.name()));
    }
    StringColumn uniqueVariableNames = variableNames.unique();
    for (String varName : uniqueVariableNames) {
      result.addColumns(DoubleColumn.create(varName));
    }
    TableSliceGroup slices = splitOn(idColumns.stream().map(Column::name).toArray(String[]::new));
    for (TableSlice slice : slices) {
      Table sliceTable = slice.asTable();
      for (Column<?> idColumn : idColumns) {
        final ColumnType columnType = idColumn.type();
        if (columnType.equals(ColumnType.STRING)) {
          StringColumn source = (StringColumn) sliceTable.column(idColumn.name());
          StringColumn dest = (StringColumn) result.column(idColumn.name());
          dest.append(source.get(0));
        } else if (columnType.equals(ColumnType.INTEGER)) {
          IntColumn source = (IntColumn) sliceTable.column(idColumn.name());
          IntColumn dest = (IntColumn) result.column(idColumn.name());
          dest.append(source.get(0));
        } else if (columnType.equals(ColumnType.LONG)) {
          LongColumn source = (LongColumn) sliceTable.column(idColumn.name());
          LongColumn dest = (LongColumn) result.column(idColumn.name());
          dest.append(source.get(0));
        } else if (columnType.equals(ColumnType.SHORT)) {
          ShortColumn source = (ShortColumn) sliceTable.column(idColumn.name());
          ShortColumn dest = (ShortColumn) result.column(idColumn.name());
          dest.append(source.get(0));
        } else if (columnType.equals(ColumnType.BOOLEAN)) {
          BooleanColumn source = (BooleanColumn) sliceTable.column(idColumn.name());
          BooleanColumn dest = (BooleanColumn) result.column(idColumn.name());
          dest.append(source.get(0));
        } else if (columnType.equals(ColumnType.LOCAL_DATE)) {
          DateColumn source = (DateColumn) sliceTable.column(idColumn.name());
          DateColumn dest = (DateColumn) result.column(idColumn.name());
          dest.append(source.get(0));
        } else if (columnType.equals(ColumnType.LOCAL_DATE_TIME)) {
          DateTimeColumn source = (DateTimeColumn) sliceTable.column(idColumn.name());
          DateTimeColumn dest = (DateTimeColumn) result.column(idColumn.name());
          dest.append(source.get(0));
        } else if (columnType.equals(ColumnType.INSTANT)) {
          InstantColumn source = (InstantColumn) sliceTable.column(idColumn.name());
          InstantColumn dest = (InstantColumn) result.column(idColumn.name());
          dest.append(source.get(0));
        } else if (columnType.equals(ColumnType.LOCAL_TIME)) {
          TimeColumn source = (TimeColumn) sliceTable.column(idColumn.name());
          TimeColumn dest = (TimeColumn) result.column(idColumn.name());
          dest.append(source.get(0));
        }
      }
      for (String varName : uniqueVariableNames) {
        DoubleColumn dest = (DoubleColumn) result.column(varName);
        Table sliceRow =
            sliceTable.where(sliceTable.stringColumn(MELT_VARIABLE_COLUMN_NAME).isEqualTo(varName));
        if (!sliceRow.isEmpty()) {
          dest.append(sliceRow.doubleColumn(MELT_VALUE_COLUMN_NAME).get(0));
        } else {
          dest.appendMissing();
        }
      }
    }
    return result;
  }
}
