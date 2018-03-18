package tech.tablesaw.api;

import tech.tablesaw.api.DateColumn.PackedDate;
import tech.tablesaw.api.DateTimeColumn.PackedDateTime;
import tech.tablesaw.api.TimeColumn.PackedTime;
import tech.tablesaw.columns.Column;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class Row implements Iterator<Row> {

    private int rowNumber;
    private final Table table;
    private final Map<String, PackedDate> dateColumnMap = new HashMap<>();
    private final Map<String, DoubleColumn> doubleColumnMap = new HashMap<>();
    private final Map<String, ShortColumn> shortColumnMap = new HashMap<>();
    private final Map<String, IntColumn> intColumnMap = new HashMap<>();
    private final Map<String, CategoryColumn> categoryColumnMap = new HashMap<>();
    private final Map<String, FloatColumn> floatColumnMap = new HashMap<>();
    private final Map<String, BooleanColumn> booleanColumnMap = new HashMap<>();
    private final Map<String, PackedDateTime> dateTimeColumnMap = new HashMap<>();
    private final Map<String, PackedTime> timeColumnMap = new HashMap<>();

    Row(Table table) {
        this.table = table;
        rowNumber = -1;
        Map<String, Column> map = new HashMap<>();
        for (Column column : table.columns()) {

            if (column instanceof DateColumn) {
                dateColumnMap.put(column.name(), new PackedDate((DateColumn) column));
            }
            else if (column instanceof DoubleColumn) {
                doubleColumnMap.put(column.name(), (DoubleColumn) column);
            }
            else if (column instanceof ShortColumn) {
                shortColumnMap.put(column.name(), (ShortColumn) column);
            }
            else if (column instanceof IntColumn) {
                intColumnMap.put(column.name(), (IntColumn) column);
            }
            else if (column instanceof CategoryColumn) {
                categoryColumnMap.put(column.name(), (CategoryColumn) column);
            }
            else if (column instanceof FloatColumn) {
                floatColumnMap.put(column.name(), (FloatColumn) column);
            }
            else if (column instanceof BooleanColumn) {
                booleanColumnMap.put(column.name(), (BooleanColumn) column);
            }
            else if (column instanceof DateTimeColumn) {
                dateTimeColumnMap.put(column.name(), new PackedDateTime((DateTimeColumn) column));
            }
            else if (column instanceof TimeColumn) {
                timeColumnMap.put(column.name(), new PackedTime((TimeColumn) column));
            } else {
                throw new RuntimeException("Unsupported Column type in column " + column);
            }
        }
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

    public int getInt(String columnName) {
        return intColumnMap.get(columnName).getInt(rowNumber);
    }

    public double getDouble(String columnName) {
        return doubleColumnMap.get(columnName).getDouble(rowNumber);
    }

    public String getString(String columnName) {
        return categoryColumnMap.get(columnName).get(rowNumber);
    }

    public LocalDate getLocalDate(String columnName) {
        return getPackedDate(columnName).asLocalDate();
    }

    public PackedDate getPackedDate(String columnName) {
        return dateColumnMap.get(columnName).get(rowNumber);
    }

    public LocalTime getLocalTime(String columnName) {
        return getPackedTime(columnName).asLocalTime();
    }

    public LocalDateTime getLocalDateTime(String columnName) {
        return getPackedDateTime(columnName).asLocalDateTime();
    }

    public PackedTime getPackedTime(String columnName) {
        return timeColumnMap.get(columnName).get(rowNumber);
    }

    public PackedDateTime getPackedDateTime(String columnName) {
        return dateTimeColumnMap.get(columnName).get(rowNumber);
    }

    public short getShort(String columnName) {
        return shortColumnMap.get(columnName).get(rowNumber);
    }

    public boolean getBoolean(String columnName) {
        return booleanColumnMap.get(columnName).get(rowNumber);
    }

    public float getFloat(String columnName) {
        return floatColumnMap.get(columnName).get(rowNumber);
    }

    public void at(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getRowNumber() {
        return rowNumber;
    }
}
