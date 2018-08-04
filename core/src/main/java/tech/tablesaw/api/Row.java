package tech.tablesaw.api;

import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.dates.PackedDate;
import tech.tablesaw.columns.datetimes.PackedDateTime;
import tech.tablesaw.columns.times.PackedTime;

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
    private final Map<String, PackedDate> dateColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, DoubleColumn> doubleColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, StringColumn> stringColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, BooleanColumn> booleanColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, PackedDateTime> dateTimeColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, PackedTime> timeColumnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private final Map<String, Column> columnMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public Row(Table table) {
        this.table = table;
        columnNames = table.columnNames().toArray(new String[0]);
        rowNumber = -1;
        for (Column column : table.columns()) {
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
                dateColumnMap.put(column.name(), new PackedDate((DateColumn) column));

            } else if (column instanceof DateTimeColumn) {
                dateTimeColumnMap.put(column.name(), new PackedDateTime((DateTimeColumn) column));

            } else if (column instanceof TimeColumn) {
                timeColumnMap.put(column.name(), new PackedTime((TimeColumn) column));
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
        return doubleColumnMap.get(columnName).get(rowNumber);
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
        return getPackedDate(columnName).asLocalDate();
    }

    public LocalDate getDate(int columnIndex) {
        return getPackedDate(columnNames[columnIndex]).asLocalDate();
    }

    public PackedDate getPackedDate(String columnName) {
        return dateColumnMap.get(columnName).get(rowNumber);
    }

    public PackedDate getPackedDate(int columnIndex) {
        return dateColumnMap.get(columnNames[columnIndex]).get(rowNumber);
    }

    public LocalTime getTime(String columnName) {
        return getPackedTime(columnName).asLocalTime();
    }

    public LocalTime getTime(int columnIndex) {
        return getPackedTime(columnNames[columnIndex]).asLocalTime();
    }

    public PackedTime getPackedTime(String columnName) {
        return timeColumnMap.get(columnName).get(rowNumber);
    }

    public PackedTime getPackedTime(int columnIndex) {
        return timeColumnMap.get(columnNames[columnIndex]).get(rowNumber);
    }

    public LocalDateTime getDateTime(String columnName) {
        return ((DateTimeColumn) columnMap.get(columnName)).get(rowNumber);
    }

    public LocalDateTime getDateTime(int columnIndex) {
        return getDateTime(columnNames[columnIndex]);
    }

    public PackedDateTime getPackedDateTime(String columnName) {
        return dateTimeColumnMap.get(columnName).get(rowNumber);
    }

    public PackedDateTime getPackedDateTime(int columnIndex) {
        return dateTimeColumnMap.get(columnNames[columnIndex]).get(rowNumber);
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
        return columnMap.get(columnName).getObject(rowNumber);
    }

    public Object getObject(int columnIndex) {
        return columnMap.get(columnNames[columnIndex]).getObject(rowNumber);
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
