package tech.tablesaw.api;

import tech.tablesaw.columns.Column;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Row implements Iterator<Row> {

    private final Table table;
    private final String[] columnNames;
    private final Map<String, DateColumn> dateColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, DoubleColumn> doubleColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, IntColumn> intColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, LongColumn> longColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, ShortColumn> shortColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, FloatColumn> floatColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, Column<String>> stringColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, BooleanColumn> booleanColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, DateTimeColumn> dateTimeColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, TimeColumn> timeColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, Column<?>> columnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private int rowNumber;

    public Row(Table table) {
        this.table = table;
        columnNames = table.columnNames().toArray(new String[0]);
        rowNumber = -1;
        for (Column<?> column : table.columns()) {
            if (column instanceof DoubleColumn) {
                doubleColumnMap.put(column.name(), (DoubleColumn) column);
            }
            if (column instanceof IntColumn) {
                intColumnMap.put(column.name(), (IntColumn) column);
            }
            if (column instanceof ShortColumn) {
                shortColumnMap.put(column.name(), (ShortColumn) column);
            }
            if (column instanceof LongColumn) {
                longColumnMap.put(column.name(), (LongColumn) column);
            }
            if (column instanceof FloatColumn) {
                floatColumnMap.put(column.name(), (FloatColumn) column);
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
        return table.columnCount();
    }

    /**
     * Returns a list containing the names of each column in the row
     */
    public List<String> columnNames() {
        return table.columnNames();
    }

    public Boolean getBoolean(int columnIndex) {
        return getBoolean(columnNames[columnIndex]);
    }

    public Boolean getBoolean(String columnName) {
        return booleanColumnMap.get(columnName).get(rowNumber);
    }

    public LocalDate getDate(String columnName) {
        return dateColumnMap.get(columnName).get(rowNumber);
    }

    public LocalDate getDate(int columnIndex) {
        return dateColumnMap.get(columnNames[columnIndex]).get(rowNumber);
    }

    public LocalDateTime getDateTime(int columnIndex) {
        return getDateTime(columnNames[columnIndex]);
    }

    public LocalDateTime getDateTime(String columnName) {
        return ((DateTimeColumn) columnMap.get(columnName)).get(rowNumber);
    }

    public double getDouble(int columnIndex) {
        return getDouble(columnNames[columnIndex]);
    }

    public double getDouble(String columnName) {
        return doubleColumnMap.get(columnName).getDouble(rowNumber);
    }

    public float getFloat(int columnIndex) {
        return getFloat(columnNames[columnIndex]);
    }

    public float getFloat(String columnName) {
        return floatColumnMap.get(columnName).getFloat(rowNumber);
    }

    public int getInt(int columnIndex) {
        return getInt(columnNames[columnIndex]);
    }

    public int getInt(String columnName) {
        return intColumnMap.get(columnName).getInt(rowNumber);
    }

    public long getLong(int columnIndex) {
        return getLong(columnNames[columnIndex]);
    }

    public long getLong(String columnName) {
        return longColumnMap.get(columnName).getLong(rowNumber);
    }

    public Object getObject(String columnName) {
        return columnMap.get(columnName).get(rowNumber);
    }

    public Object getObject(int columnIndex) {
        return columnMap.get(columnNames[columnIndex]).get(rowNumber);
    }

    public int getPackedDate(String columnName) {
        return dateColumnMap.get(columnName).getIntInternal(rowNumber);
    }

    public int getPackedDate(int columnIndex) {
        return dateColumnMap.get(columnNames[columnIndex]).getIntInternal(rowNumber);
    }

    public long getPackedDateTime(String columnName) {
        return dateTimeColumnMap.get(columnName).getLongInternal(rowNumber);
    }

    public long getPackedDateTime(int columnIndex) {
        return dateTimeColumnMap.get(columnNames[columnIndex]).getLongInternal(rowNumber);
    }

    public int getPackedTime(String columnName) {
        return timeColumnMap.get(columnName).getIntInternal(rowNumber);
    }

    public int getPackedTime(int columnIndex) {
        return timeColumnMap.get(columnNames[columnIndex]).getIntInternal(rowNumber);
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
        return shortColumnMap.get(columnName).getShort(rowNumber);
    }

    public String getText(String columnName) {
        return stringColumnMap.get(columnName).get(rowNumber);
    }

    public String getText(int columnIndex) {
        return getString(columnNames[columnIndex]);
    }

    public LocalTime getTime(String columnName) {
        return timeColumnMap.get(columnName).get(rowNumber);
    }

    public LocalTime getTime(int columnIndex) {
        return timeColumnMap.get(columnNames[columnIndex]).get(rowNumber);
    }

    public String getString(String columnName) {
        return stringColumnMap.get(columnName).get(rowNumber);
    }

    @Override
    public boolean hasNext() {
        return rowNumber < table.rowCount() - 1;
    }

    @Override
    public Row next() {
        rowNumber++;
        return this;
    }

    public void setBoolean(int columnIndex, boolean value) {
        setBoolean(columnNames[columnIndex], value);
    }

    public void setBoolean(String columnName, boolean value) {
        booleanColumnMap.get(columnName).set(rowNumber, value);
    }

    public void setDate(int columnIndex, LocalDate value) {
        setDate(columnNames[columnIndex], value);
    }

    public void setDate(String columnName, LocalDate value) {
        dateColumnMap.get(columnName).set(rowNumber, value);
    }

    public void setDateTime(int columnIndex, LocalDateTime value) {
        setDateTime(columnNames[columnIndex], value);
    }

    public void setDateTime(String columnName, LocalDateTime value) {
        dateTimeColumnMap.get(columnName).set(rowNumber, value);
    }

    public void setDouble(int columnIndex, double value) {
        setDouble(columnNames[columnIndex], value);
    }

    public void setDouble(String columnName, double value) {
        doubleColumnMap.get(columnName).set(rowNumber, value);
    }

    public void setFloat(int columnIndex, float value) {
        setFloat(columnNames[columnIndex], value);
    }

    public void setFloat(String columnName, float value) {
        floatColumnMap.get(columnName).set(rowNumber, value);
    }

    public void setInt(int columnIndex, int value) {
        setInt(columnNames[columnIndex], value);
    }

    public void setInt(String columnName, int value) {
        intColumnMap.get(columnName).set(rowNumber, value);
    }

    public void setLong(int columnIndex, long value) {
        setLong(columnNames[columnIndex], value);
    }

    public void setLong(String columnName, long value) {
        longColumnMap.get(columnName).set(rowNumber, value);
    }

    public void setShort(int columnIndex, short value) {
        setShort(columnNames[columnIndex], value);
    }

    public void setShort(String columnName, short value) {
        shortColumnMap.get(columnName).set(rowNumber, value);
    }

    public void setString(int columnIndex, String value) {
        setString(columnNames[columnIndex], value);
    }

    public void setString(String columnName, String value) {
        stringColumnMap.get(columnName).set(rowNumber, value);
    }

    public void setText(int columnIndex, String value) {
        setString(columnNames[columnIndex], value);
    }

    public void setText(String columnName, String value) {
        stringColumnMap.get(columnName).set(rowNumber, value);
    }

    public void setTime(int columnIndex, LocalTime value) {
        setTime(columnNames[columnIndex], value);
    }

    @Override
    public String toString() {
        Table t = table.emptyCopy();
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
