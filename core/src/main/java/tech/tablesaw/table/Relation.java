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

import static tech.tablesaw.joining.JoinType.FULL_OUTER;

import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparators;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.InstantColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.conversion.TableConverter;
import tech.tablesaw.conversion.smile.SmileConverter;
import tech.tablesaw.io.string.DataFramePrinter;

/**
 * A tabular data structure like a table in a relational database, but not formally implementing the
 * relational algebra
 */
public abstract class Relation implements Iterable<Row> {

  /**
   * Adds the given columns to this Relation and returns the same relation.
   *
   * @return This Relation
   */
  public abstract Relation addColumns(Column<?>... cols);

  /**
   * Sets the name of this Relation and returns the same relation.
   *
   * @return This Relation
   */
  public abstract Relation setName(String name);

  /**
   * Returns true if this relation has zero rows and false otherwise. Rows of missing values are
   * counted.
   */
  public boolean isEmpty() {
    return rowCount() == 0;
  }

  /** Returns a string that tells how many rows and columns this relation has */
  public String shape() {
    return name() + ": " + rowCount() + " rows X " + columnCount() + " cols";
  }

  /**
   * Removes the columns at the given 0-based indices from this Relation and returns the same
   * relation.
   *
   * @return This Relation
   */
  public Relation removeColumns(int... columnIndexes) {
    IntArrays.quickSort(columnIndexes, IntComparators.OPPOSITE_COMPARATOR);
    for (int i : columnIndexes) {
      removeColumns(column(i));
    }
    return this;
  }

  /**
   * Removes the given columns from this Relation and returns the same relation.
   *
   * @return This Relation
   */
  public abstract Relation removeColumns(Column<?>... columns);

  /**
   * Removes the columns with the given namesfrom this Relation and returns the same relation.
   *
   * @return This Relation
   */
  public Relation removeColumns(String... columnName) {
    Column<?>[] cols = new Column<?>[columnName.length];
    for (int i = 0; i < columnName.length; i++) {
      cols[i] = column(columnName[i]);
    }
    removeColumns(cols);
    return this;
  }

  /** Returns a list containing all the columns of the given type in this Relation */
  public List<Column<?>> columnsOfType(ColumnType type) {
    return columns().stream().filter(column -> column.type() == type).collect(Collectors.toList());
  }

  /** Returns a new table containing the first n rows in this Relation */
  public abstract Table first(int nRows);

  /** Returns the index of the column with the given columnName */
  public int columnIndex(String columnName) {
    for (int i = 0; i < columnCount(); i++) {
      if (columnNames().get(i).equalsIgnoreCase(columnName)) {
        return i;
      }
    }
    throw new IllegalArgumentException(
        String.format("Column %s is not present in table %s", columnName, name()));
  }

  /** Returns the column with the given columnName, ignoring case */
  public Column<?> column(String columnName) {
    for (Column<?> column : columns()) {
      String name = column.name().trim();
      if (name.equalsIgnoreCase(columnName)) {
        return column;
      }
    }
    throw new IllegalStateException(
        String.format("Column %s does not exist in table %s", columnName, name()));
  }

  /**
   * Returns the column at columnIndex (0-based)
   *
   * @param columnIndex an integer at least 0 and less than number of columns in the relation
   * @return the column at the given index
   */
  public abstract Column<?> column(int columnIndex);

  /** Returns the number of columns in the relation */
  public abstract int columnCount();

  /** Returns the number of rows in the relation */
  public abstract int rowCount();

  /** Returns a list of all the columns in the relation */
  public abstract List<Column<?>> columns();

  /** Returns the columns whose names are given in the input array */
  public List<Column<?>> columns(String... columnName) {
    List<Column<?>> cols = new ArrayList<>(columnName.length);
    for (String aColumnName : columnName) {
      cols.add(column(aColumnName));
    }
    return cols;
  }

  /** Returns the columns whose indices are given in the input array */
  public List<Column<?>> columns(int... columnIndices) {
    List<Column<?>> cols = new ArrayList<>(columnIndices.length);
    for (int i : columnIndices) {
      cols.add(column(i));
    }
    return cols;
  }

  /** Returns the index of the given column */
  public abstract int columnIndex(Column<?> col);

  /**
   * Returns the value at the given row and column indexes
   *
   * @param r the row index, 0 based
   * @param c the column index, 0 based
   */
  public Object get(int r, int c) {
    Column<?> column = column(c);
    return column.get(r);
  }

  /** Returns the name of this relation */
  public abstract String name();

  /** Clears all the dat in the relation, leaving the structure intact */
  public abstract void clear();

  /** Returns a list containing the names of all the columns in this relation */
  public abstract List<String> columnNames();

  /**
   * Returns an array of the column types of all columns in the relation, including duplicates as
   * appropriate, and maintaining order
   */
  public ColumnType[] typeArray() {
    ColumnType[] columnTypes = new ColumnType[columnCount()];
    for (int i = 0; i < columnCount(); i++) {
      columnTypes[i] = columns().get(i).type();
    }
    return columnTypes;
  }

  /**
   * Returns a List of the column types of all columns in the relation, including duplicates as
   * appropriate, and maintaining order
   */
  public List<ColumnType> types() {
    List<ColumnType> columnTypes = new ArrayList<>(columnCount());
    for (int i = 0; i < columnCount(); i++) {
      columnTypes.add(columns().get(i).type());
    }
    return columnTypes;
  }

  /** Returns an array of column widths for printing tables */
  public int[] colWidths() {
    int cols = columnCount();
    int[] widths = new int[cols];

    for (int i = 0; i < columnCount(); i++) {
      widths[i] = columns().get(i).columnWidth();
    }
    return widths;
  }

  /**
   * Returns a String containing a 'pretty-printed' representation of this table containing at most
   * 20 rows. The 20 rows are the first and last ten in this table.
   */
  @Override
  public String toString() {
    return print();
  }

  /** Returns a 'pretty-printed' string representation of this entire relation. */
  public String printAll() {
    return print(rowCount());
  }

  /**
   * Returns a 'pretty-printed' string representation of at most rowLimit rows from this relation.
   */
  public String print(int rowLimit) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataFramePrinter printer = new DataFramePrinter(rowLimit, baos);
    printer.print(this);
    return new String(baos.toByteArray());
  }

  /**
   * Returns a String containing a 'pretty-printed' representation of this table containing at most
   * 20 rows. The 20 rows are the first and last ten in this table.
   */
  public String print() {
    return print(20);
  }

  /**
   * Returns the structure of the this relation as a 3-column Table, consisting of Index (an
   * IntColumn), Column Name (a StringColumn), and Column Type (a StringColumn)
   */
  public Table structure() {
    Table t = Table.create("Structure of " + name());

    IntColumn index = IntColumn.indexColumn("Index", columnCount(), 0);
    StringColumn columnName = StringColumn.create("Column Name", columnCount());
    StringColumn columnType = StringColumn.create("Column Type", columnCount());
    t.addColumns(index);
    t.addColumns(columnName);
    t.addColumns(columnType);
    for (int i = 0; i < columnCount(); i++) {
      Column<?> column = this.columns().get(i);
      columnType.set(i, column.type().name());
      columnName.set(i, columnNames().get(i));
    }
    return t;
  }

  /** Returns a table containing summary statistics for the columns in this Relation */
  public Table summary() {
    Table summaryTable = Table.create(this.name());
    if (this.columnCount() == 0) {
      return summaryTable;
    }
    summaryTable.addColumns(StringColumn.create("Measure"));
    for (int i = 0; i < this.columnCount(); i++) {
      Table columnSummary = this.column(i).summary();
      columnSummary.column(1).setName(this.column(i).name());
      summaryTable =
          summaryTable
              .joinOn("Measure")
              .with(columnSummary)
              .rightJoinColumns(columnSummary.column(0).name())
              .type(FULL_OUTER)
              .join();
    }
    summaryTable.column(0).setName("Summary");
    return summaryTable;
  }

  /**
   * Returns the DoubleColumn at the given 0-based index if present. A ClassCastException is the
   * column is of a different type.
   */
  public BooleanColumn booleanColumn(int columnIndex) {
    return (BooleanColumn) column(columnIndex);
  }

  /**
   * Returns a BooleanColumn with the given name if it is present in this Relation. If the column is
   * of a different type, a ClassCastException is thrown
   */
  public BooleanColumn booleanColumn(String columnName) {
    return (BooleanColumn) column(columnName);
  }

  /**
   * Returns the NumberColumn at the given index. If the index points to a String or a boolean
   * column, a new NumberColumn is created and returned TODO(lwhite):Consider separating the indexed
   * access and the column type mods, which must be for ML functions (in smile or elsewhere)
   *
   * @param columnIndex The 0-based index of a column in the table
   * @return A number column
   * @throws ClassCastException if the cast to NumberColumn fails
   */
  public NumericColumn<?> numberColumn(int columnIndex) {
    Column<?> c = column(columnIndex);
    if (c.type() == ColumnType.STRING) {
      return ((StringColumn) c).asDoubleColumn();
    } else if (c.type() == ColumnType.BOOLEAN) {
      return ((BooleanColumn) c).asDoubleColumn();
    } else if (c.type() == ColumnType.LOCAL_DATE) {
      return ((DateColumn) c).asDoubleColumn();
    } else if (c.type() == ColumnType.LOCAL_DATE_TIME) {
      return ((DateTimeColumn) c).asDoubleColumn();
    } else if (c.type() == ColumnType.INSTANT) {
      return ((InstantColumn) c).asDoubleColumn();
    } else if (c.type() == ColumnType.LOCAL_TIME) {
      return ((TimeColumn) c).asDoubleColumn();
    }
    return (NumericColumn<?>) column(columnIndex);
  }

  /**
   * Returns a NumericColumn with the given name if it is present in this Relation. If the column is
   * not Numeric, a ClassCastException is thrown
   */
  public NumericColumn<?> numberColumn(String columnName) {
    return numberColumn(columnIndex(columnName));
  }

  /**
   * Returns a DoubleColumn with the given name if it is present in this Relation. If the column is
   * not of type DOUBLE, a ClassCastException is thrown
   */
  public DoubleColumn doubleColumn(String columnName) {
    return doubleColumn(columnIndex(columnName));
  }

  /**
   * Returns the DoubleColumn at the given 0-based index if present. A ClassCastException is the
   * column is of a different type.
   */
  public DoubleColumn doubleColumn(int columnIndex) {
    return (DoubleColumn) column(columnIndex);
  }

  /** Returns all the StringColumns in this Relation as an Array */
  public StringColumn[] stringColumns() {
    return columns().stream()
        .filter(e -> e.type() == ColumnType.STRING)
        .toArray(StringColumn[]::new);
  }

  /** Returns all the NumericColumns in this Relation as an Array */
  public NumericColumn<?>[] numberColumns() {
    return columns().stream()
        .filter(e -> e instanceof NumericColumn<?>)
        .toArray(NumericColumn[]::new);
  }

  /** Returns all the NumericColumns in the relation */
  public List<NumericColumn<?>> numericColumns() {
    return Arrays.asList(numberColumns());
  }

  /** Returns all the NumericColumns in the relation */
  public List<NumericColumn<?>> numericColumns(int... columnIndices) {
    List<NumericColumn<?>> cols = new ArrayList<>();
    for (int i : columnIndices) {
      cols.add(numberColumn(i));
    }
    return cols;
  }

  /** Returns all the NumericColumns in the relation */
  public List<NumericColumn<?>> numericColumns(String... columnNames) {
    List<NumericColumn<?>> cols = new ArrayList<>();
    for (String name : columnNames) {
      cols.add(numberColumn(name));
    }
    return cols;
  }

  /** Returns all BooleanColumns in this Relation as an Array */
  public BooleanColumn[] booleanColumns() {
    return columns().stream()
        .filter(e -> e.type() == ColumnType.BOOLEAN)
        .toArray(BooleanColumn[]::new);
  }

  /** Returns all DateColumns in this Relation as an Array */
  public DateColumn[] dateColumns() {
    return columns().stream()
        .filter(e -> e.type() == ColumnType.LOCAL_DATE)
        .toArray(DateColumn[]::new);
  }

  /** Returns all DateTimeColumns in this Relation as an Array */
  public DateTimeColumn[] dateTimeColumns() {
    return columns().stream()
        .filter(e -> e.type() == ColumnType.LOCAL_DATE_TIME)
        .toArray(DateTimeColumn[]::new);
  }

  /** Returns all InstantColumns in this Relation as an Array */
  public InstantColumn[] instantColumns() {
    return columns().stream()
        .filter(e -> e.type() == ColumnType.INSTANT)
        .toArray(InstantColumn[]::new);
  }

  /** Returns all TimeColumns in this Relation as an Array */
  public TimeColumn[] timeColumns() {
    return columns().stream()
        .filter(e -> e.type() == ColumnType.LOCAL_TIME)
        .toArray(TimeColumn[]::new);
  }

  /**
   * Returns a CategoricalColumn with the given name if it is present in this Relation. If the
   * column is not Categorical, a ClassCastException is thrown
   */
  public CategoricalColumn<?> categoricalColumn(String columnName) {
    return (CategoricalColumn<?>) column(columnName);
  }

  /**
   * Returns the CategoricalColumn at the given 0-based index if present. A ClassCastException is
   * thrown otherwise
   */
  public CategoricalColumn<?> categoricalColumn(int columnNumber) {
    return (CategoricalColumn<?>) column(columnNumber);
  }

  /** Returns the columns whose names are given in the input array */
  public List<CategoricalColumn<?>> categoricalColumns(String... columnName) {
    List<CategoricalColumn<?>> cols = new ArrayList<>(columnName.length);
    for (String aColumnName : columnName) {
      cols.add(categoricalColumn(aColumnName));
    }
    return cols;
  }

  /**
   * Returns the column with the given name cast to a NumberColumn. If the column is not Numeric, a
   * ClassCastException is thrown
   *
   * <p>Shorthand for numberColumn()
   */
  public NumericColumn<?> nCol(String columnName) {
    return numberColumn(columnName);
  }

  /**
   * Returns the column with the given name cast to a NumberColumn
   *
   * <p>Shorthand for numberColumn()
   */
  public NumericColumn<?> nCol(int columnIndex) {
    return numberColumn(columnIndex);
  }

  /**
   * Returns an IntColumn with the given name if it is present in this Relation. If the column has a
   * different type, a ClassCastException is thrown.
   */
  public IntColumn intColumn(String columnName) {
    return intColumn(columnIndex(columnName));
  }

  /**
   * Returns the IntColumn at the given 0-based index if present. A ClassCastException is the column
   * is of a different type.
   */
  public IntColumn intColumn(int columnIndex) {
    return (IntColumn) column(columnIndex);
  }

  /**
   * Returns a ShortColumn with the given name if it is present in this Relation. If the column has
   * a different type, a ClassCastException is thrown.
   */
  public ShortColumn shortColumn(String columnName) {
    return shortColumn(columnIndex(columnName));
  }

  /**
   * Returns the ShortColumn at the given 0-based index if present. A ClassCastException is the
   * column is of a different type.
   */
  public ShortColumn shortColumn(int columnIndex) {
    return (ShortColumn) column(columnIndex);
  }

  /**
   * Returns a LongColumn with the given name if it is present in this Relation. If the column has a
   * different type, a ClassCastException is thrown.
   */
  public LongColumn longColumn(String columnName) {
    return longColumn(columnIndex(columnName));
  }

  /**
   * Returns the LongColumn at the given 0-based index if present. A ClassCastException is the
   * column is of a different type.
   */
  public LongColumn longColumn(int columnIndex) {
    return (LongColumn) column(columnIndex);
  }

  /**
   * Returns a FloatColumn with the given name if it is present in this Relation. If the column has
   * a different type, a ClassCastException is thrown.
   */
  public FloatColumn floatColumn(String columnName) {
    return floatColumn(columnIndex(columnName));
  }

  /**
   * Returns the FloatColumn at the given 0-based index if present. A ClassCastException is the
   * column is of a different type.
   */
  public FloatColumn floatColumn(int columnIndex) {
    return (FloatColumn) column(columnIndex);
  }

  /**
   * Returns the DateColumn at the given 0-based index if present. A ClassCastException is the
   * column is of a different type.
   */
  public DateColumn dateColumn(int columnIndex) {
    return (DateColumn) column(columnIndex);
  }

  /**
   * Returns a DateColumn with the given name if it is present in this Relation. If the column has a
   * different type, a ClassCastException is thrown.
   */
  public DateColumn dateColumn(String columnName) {
    return (DateColumn) column(columnName);
  }

  /**
   * Returns a TimeColumn with the given name if it is present in this Relation. If the column has a
   * different type, a ClassCastException is thrown.
   */
  public TimeColumn timeColumn(String columnName) {
    return (TimeColumn) column(columnName);
  }

  /**
   * Returns the TimeColumn at the given 0-based index if present. A ClassCastException is the
   * column is of a different type.
   */
  public TimeColumn timeColumn(int columnIndex) {
    return (TimeColumn) column(columnIndex);
  }

  /**
   * Returns a StringColumn with the given name if it is present in this Relation. If the column has
   * a different type, a ClassCastException is thrown.
   */
  public StringColumn stringColumn(String columnName) {
    return (StringColumn) column(columnName);
  }

  /**
   * Returns the StringColumn at the given 0-based index if present. A ClassCastException is the
   * column is of a different type.
   */
  public StringColumn stringColumn(int columnIndex) {
    return (StringColumn) column(columnIndex);
  }

  /**
   * Returns the DateTimeColumn at the given 0-based index if present. A ClassCastException is the
   * column is of a different type.
   */
  public DateTimeColumn dateTimeColumn(int columnIndex) {
    return (DateTimeColumn) column(columnIndex);
  }

  /**
   * Returns a DateTimeColumn with the given name if it is present in this Relation. If the column
   * has a different type, a ClassCastException is thrown.
   */
  public DateTimeColumn dateTimeColumn(String columnName) {
    return (DateTimeColumn) column(columnName);
  }

  /**
   * Returns the InstantColumn at the given 0-based index if present. A ClassCastException is the
   * column is of a different type.
   */
  public InstantColumn instantColumn(int columnIndex) {
    return (InstantColumn) column(columnIndex);
  }

  /**
   * Returns an InstantColumn with the given name if it is present in this Relation. If the column
   * has a different type, a ClassCastException is thrown.
   */
  public InstantColumn instantColumn(String columnName) {
    return (InstantColumn) column(columnName);
  }

  /**
   * Returns an {@link tech.tablesaw.conversion.TableConverter} that can convert this Relation to a
   * two-dimensional matrix of primitive numeric values
   */
  public TableConverter as() {
    return new TableConverter(this);
  }

  /**
   * Returns an {@link tech.tablesaw.conversion.smile.SmileConverter} that can convert this table to
   * a format suitable for use with the Smile machine learning library.
   */
  public SmileConverter smile() {
    return new SmileConverter(this);
  }

  /**
   * Returns a string representation of the value at the given row and column indexes
   *
   * @param r the row index, 0 based
   * @param c the column index, 0 based
   */
  public String getUnformatted(int r, int c) {
    Column<?> column = column(c);
    return column.getUnformattedString(r);
  }

  /**
   * Returns a string representation of the value at the given row and column indexes
   *
   * @param r the row index, 0 based
   * @param columnName the name of the column to be returned
   *     <p>// TODO: performance would be enhanced if columns could be referenced via a hashTable
   */
  public String getString(int r, String columnName) {
    return getString(r, columnIndex(columnName));
  }

  /**
   * Returns a string representation of the value at the given row and column indexes
   *
   * @param r the row index, 0 based
   * @param columnIndex the index of the column to be returned
   *     <p>// TODO: performance would be enhanced if columns could be referenced via a hashTable
   */
  public String getString(int r, int columnIndex) {
    Column<?> column = column(columnIndex);
    return column.getString(r);
  }

  /** Returns true if the given column is in this Relation */
  public boolean containsColumn(Column<?> column) {
    return columns().contains(column);
  }

  /** Returns true if a column with the given name is in this Relation */
  public boolean containsColumn(String columnName) {
    String lowerCase = columnName.toLowerCase();
    return columnNames().stream().anyMatch(e -> e.toLowerCase().equals(lowerCase));
  }
}
