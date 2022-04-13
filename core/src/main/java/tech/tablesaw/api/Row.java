package tech.tablesaw.api;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import tech.tablesaw.columns.Column;
import tech.tablesaw.table.TableSlice;

/**
 * Represents a row in a Relation (either a Table or TableSlice), allowing iteration over the
 * relation. During iteration, the Row slides over the table row-wise, exposing data as it advances,
 * acting as a cursor into the table. There is only one Row object for the entire table during
 * iteration.
 *
 * <p>Implementation Note: The row is always implemented over a TableSlice. If the constructor
 * argument is a table, it is wrapped by a slice over the whole table.
 */
public class Row implements Iterator<Row> {

  /**
   * Wrap Map of column name to Column map and provide helpful error messages to the user when a
   * column name cannot be found.
   */
  private class ColumnMap<T> {
    private final Map<String, T> columnMap = new HashMap<>();
    private final ColumnType columnType;

    public ColumnMap(ColumnType columnType) {
      this.columnType = columnType;
    }

    ColumnMap() {
      this.columnType = null;
    }

    T get(String columnName) {
      T column = columnMap.get(columnName.toLowerCase());
      if (column == null) {
        throwWrongTypeError(columnName);
        throwColumnNotPresentError(columnName);
      }
      return column;
    }

    void put(String columnName, T column) {
      columnMap.put(columnName.toLowerCase(), column);
    }

    /**
     * Will get thrown when column name is correct, but used the wrong method get/set is called.
     * E.G. the user called .getLong on an IntColumn.
     */
    private void throwWrongTypeError(String columnName) {
      for (int i = 0; i < columnNames.length; i++) {
        if (columnName.equals(columnNames[i])) {
          String actualType = tableSlice.getTable().columns().get(i).type().name();
          String proposedType = columnTypeName();
          throw new IllegalArgumentException(
              String.format(
                  "Column %s is of type %s and cannot be cast to %s. Use the method for %s.",
                  columnName, actualType, proposedType, actualType));
        }
      }
    }

    private void throwColumnNotPresentError(String columnName) {
      throw new IllegalStateException(
          String.format(
              "Column %s is not present in table %s", columnName, tableSlice.getTable().name()));
    }

    private String columnTypeName() {
      if (this.columnType != null) {
        return this.columnType.name();
      }
      return "Object";
    }
  }

  private final TableSlice tableSlice;
  private final String[] columnNames;
  private final ColumnMap<DateColumn> dateColumnMap = new ColumnMap<>();
  private final ColumnMap<DoubleColumn> doubleColumnMap = new ColumnMap<>(ColumnType.DOUBLE);
  private final ColumnMap<IntColumn> intColumnMap = new ColumnMap<>(ColumnType.INTEGER);
  private final ColumnMap<LongColumn> longColumnMap = new ColumnMap<>(ColumnType.LONG);
  private final ColumnMap<ShortColumn> shortColumnMap = new ColumnMap<>(ColumnType.SHORT);
  private final ColumnMap<FloatColumn> floatColumnMap = new ColumnMap<>(ColumnType.FLOAT);
  private final ColumnMap<Column<String>> stringColumnMap = new ColumnMap<>(ColumnType.STRING);
  private final ColumnMap<BooleanColumn> booleanColumnMap = new ColumnMap<>(ColumnType.BOOLEAN);
  private final ColumnMap<DateTimeColumn> dateTimeColumnMap =
      new ColumnMap<>(ColumnType.LOCAL_DATE_TIME);
  private final ColumnMap<InstantColumn> instantColumnMap = new ColumnMap<>(ColumnType.INSTANT);
  private final ColumnMap<TimeColumn> timeColumnMap = new ColumnMap<>(ColumnType.LOCAL_TIME);
  private final ColumnMap<Column<?>> columnMap = new ColumnMap<>();
  private final ColumnMap<NumericColumn<? extends Number>> numericColumnMap = new ColumnMap<>();
  private int rowNumber;

  /** Constructs a Row object for the given table */
  public Row(Table table) {
    this(table, -1);
  }

  /** Constructs a Row object for the given TableSlice */
  public Row(TableSlice tableSlice) {
    this(tableSlice, -1);
  }

  /**
   * Constructs a Row object for the given Table, with the Row positioned at the given 0-based index
   */
  public Row(Table table, int rowNumber) {
    this(new TableSlice(table), rowNumber);
  }

  /**
   * Constructs a Row object for the given TableSlice, with the Row positioned at the given 0-based
   * index
   */
  public Row(TableSlice tableSlice, int rowNumber) {
    this.tableSlice = tableSlice;
    columnNames = tableSlice.columnNames().toArray(new String[0]);
    this.rowNumber = rowNumber;
    for (Column<?> column : tableSlice.getTable().columns()) {
      if (column instanceof DoubleColumn) {
        doubleColumnMap.put(column.name(), (DoubleColumn) column);
        numericColumnMap.put(column.name(), (DoubleColumn) column);
      }
      if (column instanceof IntColumn) {
        intColumnMap.put(column.name(), (IntColumn) column);
        numericColumnMap.put(column.name(), (IntColumn) column);
      }
      if (column instanceof ShortColumn) {
        shortColumnMap.put(column.name(), (ShortColumn) column);
        numericColumnMap.put(column.name(), (ShortColumn) column);
      }
      if (column instanceof LongColumn) {
        longColumnMap.put(column.name(), (LongColumn) column);
        numericColumnMap.put(column.name(), (LongColumn) column);
      }
      if (column instanceof FloatColumn) {
        floatColumnMap.put(column.name(), (FloatColumn) column);
        numericColumnMap.put(column.name(), (FloatColumn) column);
      }
      if (column instanceof BooleanColumn) {
        booleanColumnMap.put(column.name(), (BooleanColumn) column);
      }
      if (column instanceof StringColumn) {
        stringColumnMap.put(column.name(), (StringColumn) column);
      }
      if (column instanceof DateColumn) {
        dateColumnMap.put(column.name(), (DateColumn) column);

      } else if (column instanceof DateTimeColumn) {
        dateTimeColumnMap.put(column.name(), (DateTimeColumn) column);

      } else if (column instanceof InstantColumn) {
        instantColumnMap.put(column.name(), (InstantColumn) column);

      } else if (column instanceof TimeColumn) {
        timeColumnMap.put(column.name(), (TimeColumn) column);
      }
      columnMap.put(column.name(), column);
    }
  }

  public ColumnType type(int columnIndex) {
    return tableSlice.column(columnIndex).type();
  }

  /** Moves this Row to the given 0-based row index */
  public void at(int rowNumber) {
    this.rowNumber = rowNumber;
  }

  /** Returns the number of columns in this Row */
  public int columnCount() {
    return tableSlice.columnCount();
  }

  /** Returns a list containing the names of each column in the row */
  public List<String> columnNames() {
    return tableSlice.columnNames();
  }

  /** Returns a Boolean value from this Row at the given column index. */
  public Boolean getBoolean(int columnIndex) {
    return getBoolean(columnNames[columnIndex]);
  }

  /** Returns an element from a Boolean column in its internal byte form, avoiding boxing */
  public byte getBooleanAsByte(int columnIndex) {
    return getBooleanAsByte(columnNames[columnIndex]);
  }

  /** Returns an element from a Boolean column in its internal byte form, avoiding boxing */
  public byte getBooleanAsByte(String columnName) {
    return booleanColumnMap.get(columnName).getByte(getIndex(rowNumber));
  }

  /**
   * Returns a Boolean value from this Row at the column of the given name. An IllegalStateException
   * is thrown if the column is not present in the Row and an * IllegalArgumentException is thrown
   * if the column has a different type
   */
  public Boolean getBoolean(String columnName) {
    return booleanColumnMap.get(columnName).get(getIndex(rowNumber));
  }

  /**
   * Returns a LocalDate value from this Row at the column of the given name. An
   * IllegalStateException is thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if the column has a different type
   */
  public LocalDate getDate(String columnName) {
    return dateColumnMap.get(columnName).get(getIndex(rowNumber));
  }

  /**
   * Returns a LocalDate value from this Row at the column with the given index. An
   * IllegalStateException is thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if the column has a different type
   */
  public LocalDate getDate(int columnIndex) {
    return dateColumnMap.get(columnNames[columnIndex]).get(getIndex(rowNumber));
  }

  /**
   * Returns a LocalDateTime from this Row at the column with the given index. An
   * IllegalStateException is thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type
   */
  public LocalDateTime getDateTime(int columnIndex) {
    return getDateTime(columnNames[columnIndex]);
  }

  /**
   * Returns a LocalDateTime value from this Row at the column of the given name. An
   * IllegalStateException is thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type
   */
  public LocalDateTime getDateTime(String columnName) {
    return ((DateTimeColumn) columnMap.get(columnName)).get(getIndex(rowNumber));
  }

  /**
   * Returns an Instant from this Row at the column with the given index. An IllegalStateException
   * is thrown if the column is not present in the Row and an IllegalArgumentException is thrown if
   * it has a different type
   */
  public Instant getInstant(int columnIndex) {
    return getInstant(columnNames[columnIndex]);
  }

  /**
   * Returns an Instant value from this Row at the column of the given name. An
   * IllegalStateException is thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type
   */
  public Instant getInstant(String columnName) {
    return ((InstantColumn) columnMap.get(columnName)).get(getIndex(rowNumber));
  }

  /**
   * Returns a double from this Row at the column with the given index. An IllegalStateException is
   * thrown if the column is not present in the Row and an IllegalArgumentException is thrown if it
   * has a different type
   */
  public double getDouble(int columnIndex) {
    return getDouble(columnNames[columnIndex]);
  }

  /**
   * Returns a double from this Row at the column of the given name. An IllegalStateException is
   * thrown if the column is not present in the Row and an IllegalArgumentException is thrown if it
   * has a different type
   */
  public double getDouble(String columnName) {
    return doubleColumnMap.get(columnName).getDouble(getIndex(rowNumber));
  }

  /**
   * Returns a float from this Row at the column with the given index. An IllegalStateException is
   * thrown if the column is not present in the Row and an IllegalArgumentException is thrown if it
   * has a different type
   */
  public float getFloat(int columnIndex) {
    return getFloat(columnNames[columnIndex]);
  }

  /**
   * Returns a float from this Row at the column of the given name. An IllegalStateException is
   * thrown if the column is not present in the Row and an IllegalArgumentException is thrown if it
   * has a different type
   */
  public float getFloat(String columnName) {
    return floatColumnMap.get(columnName).getFloat(getIndex(rowNumber));
  }

  /**
   * Returns an int from this Row at the column with the given index. An IllegalStateException is
   * thrown if the column is not present in the Row and an IllegalArgumentException is thrown if it
   * has a different type
   */
  public int getInt(int columnIndex) {
    return getInt(columnNames[columnIndex]);
  }

  /**
   * Returns an int from this Row at the column of the given name. An IllegalStateException is
   * thrown if the column is not present in the Row and an IllegalArgumentException is thrown if it
   * has a different type
   */
  public int getInt(String columnName) {
    return intColumnMap.get(columnName).getInt(getIndex(rowNumber));
  }

  /**
   * Returns a long from this Row at the column with the given index. An IllegalStateException is
   * thrown if the column is not present in the Row and an IllegalArgumentException is thrown if it
   * has a different type
   */
  public long getLong(int columnIndex) {
    return getLong(columnNames[columnIndex]);
  }

  /**
   * Returns a long from this Row at the column of the given name. An IllegalStateException is
   * thrown if the column is not present in the Row and an IllegalArgumentException is thrown if it
   * has a different type
   */
  public long getLong(String columnName) {
    return longColumnMap.get(columnName).getLong(getIndex(rowNumber));
  }

  /**
   * Returns an Object representing the value from this Row at the column of the given name. An
   * IllegalStateException is thrown if the column is not present in the Row.
   */
  public Object getObject(String columnName) {
    return columnMap.get(columnName).get(getIndex(rowNumber));
  }

  /**
   * Returns an Object representing the LocalTime from this Row at the column with the given index.
   * An IllegalStateException is thrown if the column is not present in the Row
   */
  public Object getObject(int columnIndex) {
    return columnMap.get(columnNames[columnIndex]).get(getIndex(rowNumber));
  }

  /**
   * Returns an int representing the LocalDate from this Row at the column of the given name. An
   * IllegalStateException is thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type
   */
  public int getPackedDate(String columnName) {
    return dateColumnMap.get(columnName).getIntInternal(getIndex(rowNumber));
  }

  /**
   * Returns an int representing the LocalTime from this Row at the column with the given index. An
   * IllegalStateException is thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type type
   */
  public int getPackedDate(int columnIndex) {
    return dateColumnMap.get(columnNames[columnIndex]).getIntInternal(getIndex(rowNumber));
  }

  /**
   * Returns an long representing the LocalTime from this Row at the column with the given index. An
   * IllegalStateException is thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type type
   */
  public long getPackedInstant(int columnIndex) {
    return instantColumnMap.get(columnNames[columnIndex]).getLongInternal(getIndex(rowNumber));
  }

  /**
   * Returns a long representing the Instant from this Row at the column of the given name. An
   * IllegalStateException is thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type
   */
  public long getPackedInstant(String columnName) {
    return instantColumnMap.get(columnName).getLongInternal(getIndex(rowNumber));
  }

  /**
   * Returns a long representing the LocalDateTime from this Row at the column of the given name. An
   * IllegalStateException is thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type
   */
  public long getPackedDateTime(String columnName) {
    return dateTimeColumnMap.get(columnName).getLongInternal(getIndex(rowNumber));
  }

  /**
   * Returns an long representing the LocalTime from this Row at the column with the given index. An
   * IllegalStateException is thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type type
   */
  public long getPackedDateTime(int columnIndex) {
    return dateTimeColumnMap.get(columnNames[columnIndex]).getLongInternal(getIndex(rowNumber));
  }

  /**
   * Returns an int representing the LocalTime from this Row at the column of the given name. An
   * IllegalStateException is thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type
   */
  public int getPackedTime(String columnName) {
    return timeColumnMap.get(columnName).getIntInternal(getIndex(rowNumber));
  }

  /**
   * Returns an int representing the LocalTime from this Row at the column with the given index. An
   * IllegalStateException is thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type type
   */
  public int getPackedTime(int columnIndex) {
    return timeColumnMap.get(columnNames[columnIndex]).getIntInternal(getIndex(rowNumber));
  }

  /**
   * Returns a short value from this Row at the column with the given index. An
   * IllegalStateException is thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type type
   */
  public short getShort(int columnIndex) {
    return getShort(columnNames[columnIndex]);
  }

  /** Returns the zero-based index of the current position of this Row */
  public int getRowNumber() {
    return rowNumber;
  }

  /**
   * Returns a String value from this Row at the column with the given index. An
   * IllegalStateException is thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type type
   */
  public String getString(int columnIndex) {
    return getString(columnNames[columnIndex]);
  }

  /**
   * Returns a short from this Row at the column of the given name. An IllegalStateException is
   * thrown if the column is not present in the Row and an IllegalArgumentException is thrown if it
   * has a different type
   */
  public short getShort(String columnName) {
    return shortColumnMap.get(columnName).getShort(getIndex(rowNumber));
  }

  /**
   * Returns a LocalTime value from this Row at the column of the given name. An
   * IllegalStateException is thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type type
   */
  public LocalTime getTime(String columnName) {
    return timeColumnMap.get(columnName).get(getIndex(rowNumber));
  }

  /**
   * Returns a LocalTime value from this Row at the column with the given index. An
   * IllegalStateException is thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type type
   */
  public LocalTime getTime(int columnIndex) {
    return timeColumnMap.get(columnNames[columnIndex]).get(getIndex(rowNumber));
  }

  /**
   * Returns a String from this Row at the column of the given name. An IllegalStateException is
   * thrown if the column is not present in the Row and an IllegalArgumentException is thrown if it
   * has a different type
   */
  public String getString(String columnName) {
    return stringColumnMap.get(columnName).get(getIndex(rowNumber));
  }

  /** Returns true if the value at columnName is missing, and false otherwise */
  public boolean isMissing(String columnName) {
    Column<?> x = columnMap.get(columnName);
    int i = getIndex(rowNumber);
    return x.isMissing(i);
  }

  /** Returns true if there's at least one more row beyond the current one. */
  @Override
  public boolean hasNext() {
    return rowNumber < this.tableSlice.rowCount() - 1;
  }

  /** Increments the row pointer, making the next row's data accessible */
  @Override
  public Row next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    rowNumber++;
    return this;
  }

  /**
   * Sets the value of the given column at this Row to the appropriate missing-value indicator for
   * the column type.
   */
  public void setMissing(int columnIndex) {
    setMissing(columnNames[columnIndex]);
  }

  /**
   * Sets the value of the given column at this Row to the appropriate missing-value indicator for
   * the column type.
   */
  public void setMissing(String columnName) {
    columnMap.get(columnName).setMissing(getIndex(rowNumber));
  }

  /**
   * Sets the value of the column at the given index and this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setBoolean(int columnIndex, boolean value) {
    setBoolean(columnNames[columnIndex], value);
  }

  /**
   * Sets the value of the column with the given name at this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setBoolean(String columnName, boolean value) {
    booleanColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  /**
   * Sets the value of the column at the given index and this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setDate(int columnIndex, LocalDate value) {
    setDate(columnNames[columnIndex], value);
  }

  public void setPackedDate(int columnIndex, int value) {
    setPackedDate(columnNames[columnIndex], value);
  }

  /**
   * Sets the value of the column with the given name at this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setDate(String columnName, LocalDate value) {
    dateColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  public void setPackedDate(String columnName, int value) {
    dateColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  public void setPackedTime(int columnIndex, int value) {
    setPackedTime(columnNames[columnIndex], value);
  }

  public void setPackedDateTime(int columnIndex, long value) {
    setPackedDateTime(columnNames[columnIndex], value);
  }

  public void setPackedInstant(int columnIndex, long value) {
    setPackedInstant(columnNames[columnIndex], value);
  }

  public void setPackedTime(String columnName, int value) {
    timeColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  public void setPackedDateTime(String columnName, long value) {
    dateTimeColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  public void setPackedInstant(String columnName, long value) {
    instantColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  public void setBooleanAsByte(String columnName, byte value) {
    booleanColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  public void setBooleanAsByte(int columnIndex, byte value) {
    setBooleanAsByte(columnNames[columnIndex], value);
  }

  /**
   * Sets the value of the column at the given index and this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setDateTime(int columnIndex, LocalDateTime value) {
    setDateTime(columnNames[columnIndex], value);
  }

  /**
   * Sets the value of the column with the given name at this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setDateTime(String columnName, LocalDateTime value) {
    dateTimeColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  /**
   * Sets the value of the column at the given index and this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setInstant(int columnIndex, Instant value) {
    setInstant(columnNames[columnIndex], value);
  }

  /**
   * Sets the value of the column with the given name at this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setInstant(String columnName, Instant value) {
    instantColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  /**
   * Sets the value of the column at the given index and this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setDouble(int columnIndex, double value) {
    setDouble(columnNames[columnIndex], value);
  }

  /**
   * Sets the value of the column with the given name at this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setDouble(String columnName, double value) {
    doubleColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  /**
   * Sets the value of the column at the given index and this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setFloat(int columnIndex, float value) {
    setFloat(columnNames[columnIndex], value);
  }

  /**
   * Sets the value of the column with the given name at this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setFloat(String columnName, float value) {
    floatColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  /**
   * Sets the value of the column at the given index and this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setInt(int columnIndex, int value) {
    setInt(columnNames[columnIndex], value);
  }

  /**
   * Sets the value of the column with the given name at this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setInt(String columnName, int value) {
    intColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  /**
   * Sets the value of the column at the given index and this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setLong(int columnIndex, long value) {
    setLong(columnNames[columnIndex], value);
  }

  /**
   * Sets the value of the column with the given name at this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setLong(String columnName, long value) {
    longColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  /**
   * Sets the value of the column at the given index and this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setShort(int columnIndex, short value) {
    setShort(columnNames[columnIndex], value);
  }

  /**
   * Sets the value of the column with the given name at this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setShort(String columnName, short value) {
    shortColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  /**
   * Sets the value of the column at the given index and this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setString(int columnIndex, String value) {
    setString(columnNames[columnIndex], value);
  }

  /**
   * Sets the value of the column with the given name at this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setString(String columnName, String value) {
    stringColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  /**
   * Sets the value of the column at the given index and this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setTime(int columnIndex, LocalTime value) {
    setTime(columnNames[columnIndex], value);
  }

  /**
   * Returns the row number for this row, relative to the backing column
   *
   * @param rowNumber the rowNumber in the TableSlice backing this (row)
   * @return the matching row number in the underlying column.
   */
  private int getIndex(int rowNumber) {
    return tableSlice.mappedRowNumber(rowNumber);
  }

  /**
   * Returns the row number in the table backing the slice behind this row. This value may differ
   * from the rowNumber() if the slice covers less than the entire table
   */
  public int getBackingRowNumber() {
    return getIndex(getRowNumber());
  }

  /**
   * Returns a double representing the value held in the column with the given name at this row, for
   * any numeric column type
   */
  public double getNumber(String columnName) {
    return numericColumnMap.get(columnName).getDouble(getIndex(rowNumber));
  }

  /** Returns the type of the named column */
  public ColumnType getColumnType(String columnName) {
    return columnMap.get(columnName).type();
  }

  public ColumnType getColumnType(int columnIndex) {
    return tableSlice.column(columnIndex).type();
  }

  public Column<?> column(int columnIndex) {
    return tableSlice.column(columnIndex);
  }

  /** Returns a hash computed on the values in the backing table at this row */
  public int rowHash() {
    int[] values = new int[columnCount()];
    for (int i = 0; i < columnCount(); i++) {
      Column<?> column = tableSlice.column(i);
      values[i] = column.valueHash(rowNumber);
    }
    int result = 1;
    for (int hash : values) {
      result = 31 * result + hash;
    }
    return result;
  }

  @Override
  public String toString() {
    Table t = tableSlice.getTable().emptyCopy();
    if (getRowNumber() == -1) {
      return "";
    }
    t.append(this);
    return t.print();
  }

  /**
   * Sets the value of the column with the given name at this Row to the given value. An
   * IllegalStateException is * thrown if the column is not present in the Row and an
   * IllegalArgumentException is thrown if it has a different type to that named in the method
   * signature
   */
  public void setTime(String columnName, LocalTime value) {
    timeColumnMap.get(columnName).set(rowNumber, value);
  }
}
