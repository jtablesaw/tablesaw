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

    private int rowNumber;
    private final Table table;
    private final String[] columnNames;
    private final Map<String, DateColumn> dateColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, DoubleColumn> doubleColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, StringColumn> stringColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, BooleanColumn> booleanColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, DateTimeColumn> dateTimeColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, TimeColumn> timeColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private final Map<String, Column<?>> columnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public Row(Table table) {
        this.table = table;
        columnNames = table.columnNames().toArray(new String[0]);
        rowNumber = -1;
        for (Column<?> column : table.columns()) {
            if (column instanceof DoubleColumn) {
                doubleColumnMap.put(column.name(), (DoubleColumn) column);
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

            } else if (column instanceof TimeColumn) {
                timeColumnMap.put(column.name(), (TimeColumn) column);
            }
            columnMap.put(column.name(), column);
        }
    }

    @Override
    public boolean hasNext() {
        return rowNumber < table.rowCount() - 1;
    }

    /**
     * Returns a list containing the names of each column in the row
     */
    public List<String> columnNames() {
        return table.columnNames();
    }

    public int columnCount() {
        return table.columnCount();
    }

    @Override
    public Row next() {
        rowNumber++;
        return this;
    }

    public double getDouble(String columnName) {
        return doubleColumnMap.get(columnName).getDouble(rowNumber);
    }

    public double getDouble(int columnIndex) {
        return getDouble(columnNames[columnIndex]);
    }

    public int getInt(String columnName) {
        return (int) getDouble(columnName);
    }

    public int getInt(int columnIndex) {
        return (int) getDouble(columnIndex);
    }

    public String getString(String columnName) {
        return stringColumnMap.get(columnName).get(rowNumber);
    }

    public String getString(int columnIndex) {
        return getString(columnNames[columnIndex]);
    }

    public LocalDate getDate(String columnName) {
        return dateColumnMap.get(columnName).get(rowNumber);
    }

    public LocalDate getDate(int columnIndex) {
        return dateColumnMap.get(columnNames[columnIndex]).get(rowNumber);
    }

    public int getPackedDate(String columnName) {
        return dateColumnMap.get(columnName).getIntInternal(rowNumber);
    }

    public int getPackedDate(int columnIndex) {
        return dateColumnMap.get(columnNames[columnIndex]).getIntInternal(rowNumber);
    }

    public LocalTime getTime(String columnName) {
        return timeColumnMap.get(columnName).get(rowNumber);
    }

    public LocalTime getTime(int columnIndex) {
        return timeColumnMap.get(columnNames[columnIndex]).get(rowNumber);
    }

    public int getPackedTime(String columnName) {
        return timeColumnMap.get(columnName).getIntInternal(rowNumber);
    }

    public int getPackedTime(int columnIndex) {
        return timeColumnMap.get(columnNames[columnIndex]).getIntInternal(rowNumber);
    }

    public LocalDateTime getDateTime(String columnName) {
        return ((DateTimeColumn) columnMap.get(columnName)).get(rowNumber);
    }

    public LocalDateTime getDateTime(int columnIndex) {
        return getDateTime(columnNames[columnIndex]);
    }

    public long getPackedDateTime(String columnName) {
        return dateTimeColumnMap.get(columnName).getLongInternal(rowNumber);
    }

    public long getPackedDateTime(int columnIndex) {
        return dateTimeColumnMap.get(columnNames[columnIndex]).getLongInternal(rowNumber);
    }

    public boolean getBoolean(String columnName) {
        return booleanColumnMap.get(columnName).get(rowNumber);
    }

    public boolean getBoolean(int columnIndex) {
        return getBoolean(columnNames[columnIndex]);
    }

    public void at(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public Object getObject(String columnName) {
        return columnMap.get(columnName).get(rowNumber);
    }

    public Object getObject(int columnIndex) {
        return columnMap.get(columnNames[columnIndex]).get(rowNumber);
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
}
