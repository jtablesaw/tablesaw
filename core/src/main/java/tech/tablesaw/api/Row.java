package tech.tablesaw.api;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import tech.tablesaw.columns.Column;
import tech.tablesaw.table.TableSlice;

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

  public Row(Table table) {
    this(table, -1);
  }

  public Row(TableSlice tableSlice) {
    this(tableSlice, -1);
  }

  public Row(Table table, int rowNumber) {
    this(new TableSlice(table), rowNumber);
  }

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
      if (column instanceof TextColumn) {
        stringColumnMap.put(column.name(), (TextColumn) column);
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

  public void at(int rowNumber) {
    this.rowNumber = rowNumber;
  }

  public int columnCount() {
    return tableSlice.columnCount();
  }

  /** Returns a list containing the names of each column in the row */
  public List<String> columnNames() {
    return tableSlice.columnNames();
  }

  public Boolean getBoolean(int columnIndex) {
    return getBoolean(columnNames[columnIndex]);
  }

  public Boolean getBoolean(String columnName) {
    return booleanColumnMap.get(columnName).get(getIndex(rowNumber));
  }

  public LocalDate getDate(String columnName) {
    return dateColumnMap.get(columnName).get(getIndex(rowNumber));
  }

  public LocalDate getDate(int columnIndex) {
    return dateColumnMap.get(columnNames[columnIndex]).get(getIndex(rowNumber));
  }

  public LocalDateTime getDateTime(int columnIndex) {
    return getDateTime(columnNames[columnIndex]);
  }

  public LocalDateTime getDateTime(String columnName) {
    return ((DateTimeColumn) columnMap.get(columnName)).get(getIndex(rowNumber));
  }

  public Instant getInstant(int columnIndex) {
    return getInstant(columnNames[columnIndex]);
  }

  public Instant getInstant(String columnName) {
    return ((InstantColumn) columnMap.get(columnName)).get(getIndex(rowNumber));
  }

  public double getDouble(int columnIndex) {
    return getDouble(columnNames[columnIndex]);
  }

  public double getDouble(String columnName) {
    return doubleColumnMap.get(columnName).getDouble(getIndex(rowNumber));
  }

  public float getFloat(int columnIndex) {
    return getFloat(columnNames[columnIndex]);
  }

  public float getFloat(String columnName) {
    return floatColumnMap.get(columnName).getFloat(getIndex(rowNumber));
  }

  public int getInt(int columnIndex) {
    return getInt(columnNames[columnIndex]);
  }

  public int getInt(String columnName) {
    return intColumnMap.get(columnName).getInt(getIndex(rowNumber));
  }

  public long getLong(int columnIndex) {
    return getLong(columnNames[columnIndex]);
  }

  public long getLong(String columnName) {
    return longColumnMap.get(columnName).getLong(getIndex(rowNumber));
  }

  public Object getObject(String columnName) {
    return columnMap.get(columnName).get(getIndex(rowNumber));
  }

  public Object getObject(int columnIndex) {
    return columnMap.get(columnNames[columnIndex]).get(getIndex(rowNumber));
  }

  public int getPackedDate(String columnName) {
    return dateColumnMap.get(columnName).getIntInternal(getIndex(rowNumber));
  }

  public int getPackedDate(int columnIndex) {
    return dateColumnMap.get(columnNames[columnIndex]).getIntInternal(getIndex(rowNumber));
  }

  public long getPackedDateTime(String columnName) {
    return dateTimeColumnMap.get(columnName).getLongInternal(getIndex(rowNumber));
  }

  public long getPackedDateTime(int columnIndex) {
    return dateTimeColumnMap.get(columnNames[columnIndex]).getLongInternal(getIndex(rowNumber));
  }

  public int getPackedTime(String columnName) {
    return timeColumnMap.get(columnName).getIntInternal(getIndex(rowNumber));
  }

  public int getPackedTime(int columnIndex) {
    return timeColumnMap.get(columnNames[columnIndex]).getIntInternal(getIndex(rowNumber));
  }

  public short getShort(int columnIndex) {
    return getShort(columnNames[columnIndex]);
  }

  public int getRowNumber() {
    return rowNumber;
  }

  public String getString(int columnIndex) {
    return getString(columnNames[columnIndex]);
  }

  public short getShort(String columnName) {
    return shortColumnMap.get(columnName).getShort(getIndex(rowNumber));
  }

  public String getText(String columnName) {
    return stringColumnMap.get(columnName).get(getIndex(rowNumber));
  }

  public String getText(int columnIndex) {
    return getString(columnNames[columnIndex]);
  }

  public LocalTime getTime(String columnName) {
    return timeColumnMap.get(columnName).get(getIndex(rowNumber));
  }

  public LocalTime getTime(int columnIndex) {
    return timeColumnMap.get(columnNames[columnIndex]).get(getIndex(rowNumber));
  }

  public String getString(String columnName) {
    return stringColumnMap.get(columnName).get(getIndex(rowNumber));
  }

  public boolean isMissing(String columnName) {
    Column<?> x = columnMap.get(columnName);
    int i = getIndex(rowNumber);
    return x.isMissing(i);
  }

  @Override
  public boolean hasNext() {
    return rowNumber < this.tableSlice.rowCount() - 1;
  }

  @Override
  public Row next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    rowNumber++;
    return this;
  }

  public void setMissing(int columnIndex) {
    setMissing(columnNames[columnIndex]);
  }

  public void setMissing(String columnName) {
    columnMap.get(columnName).setMissing(getIndex(rowNumber));
  }

  public void setBoolean(int columnIndex, boolean value) {
    setBoolean(columnNames[columnIndex], value);
  }

  public void setBoolean(String columnName, boolean value) {
    booleanColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  public void setDate(int columnIndex, LocalDate value) {
    setDate(columnNames[columnIndex], value);
  }

  public void setDate(String columnName, LocalDate value) {
    dateColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  public void setDateTime(int columnIndex, LocalDateTime value) {
    setDateTime(columnNames[columnIndex], value);
  }

  public void setDateTime(String columnName, LocalDateTime value) {
    dateTimeColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  public void setInstant(int columnIndex, Instant value) {
    setInstant(columnNames[columnIndex], value);
  }

  public void setInstant(String columnName, Instant value) {
    instantColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  public void setDouble(int columnIndex, double value) {
    setDouble(columnNames[columnIndex], value);
  }

  public void setDouble(String columnName, double value) {
    doubleColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  public void setFloat(int columnIndex, float value) {
    setFloat(columnNames[columnIndex], value);
  }

  public void setFloat(String columnName, float value) {
    floatColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  public void setInt(int columnIndex, int value) {
    setInt(columnNames[columnIndex], value);
  }

  public void setInt(String columnName, int value) {
    intColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  public void setLong(int columnIndex, long value) {
    setLong(columnNames[columnIndex], value);
  }

  public void setLong(String columnName, long value) {
    longColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  public void setShort(int columnIndex, short value) {
    setShort(columnNames[columnIndex], value);
  }

  public void setShort(String columnName, short value) {
    shortColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  public void setString(int columnIndex, String value) {
    setString(columnNames[columnIndex], value);
  }

  public void setString(String columnName, String value) {
    stringColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

  public void setText(int columnIndex, String value) {
    setString(columnNames[columnIndex], value);
  }

  public void setText(String columnName, String value) {
    stringColumnMap.get(columnName).set(getIndex(rowNumber), value);
  }

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

  public double getNumber(String columnName) {
    return numericColumnMap.get(columnName).getDouble(rowNumber);
  }

  public ColumnType getColumnType(String columnName) {
    return columnMap.get(columnName).type();
  }

  @Override
  public String toString() {
    Table t = tableSlice.getTable().emptyCopy();
    if (getRowNumber() == -1) {
      return "";
    }
    t.addRow(this);
    return t.print();
  }

  public void setTime(String columnName, LocalTime value) {
    timeColumnMap.get(columnName).set(rowNumber, value);
  }
}
