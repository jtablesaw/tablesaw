package tech.tablesaw.api;

import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.dates.PackedDate;
import tech.tablesaw.columns.datetimes.PackedDateTime;
import tech.tablesaw.columns.times.PackedTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Row implements Iterator<Row> {

    private int rowNumber;
    private final Table table;
    private final Map<String, PackedDate> dateColumnMap = new HashMap<>();
    private final Map<String, NumberColumn> numberColumnMap = new HashMap<>();
    private final Map<String, StringColumn> categoryColumnMap = new HashMap<>();
    private final Map<String, BooleanColumn> booleanColumnMap = new HashMap<>();
    private final Map<String, PackedDateTime> dateTimeColumnMap = new HashMap<>();
    private final Map<String, PackedTime> timeColumnMap = new HashMap<>();

    public Row(Table table) {
        this.table = table;
        rowNumber = -1;
        for (Column column : table.columns()) {

            if (column instanceof DateColumn) {
                dateColumnMap.put(column.name(), new PackedDate((DateColumn) column));
            } else if (column instanceof DoubleColumn) {
                numberColumnMap.put(column.name(), (NumberColumn) column);
            } else if (column instanceof StringColumn) {
                categoryColumnMap.put(column.name(), (StringColumn) column);
            } else if (column instanceof BooleanColumn) {
                booleanColumnMap.put(column.name(), (BooleanColumn) column);
            } else if (column instanceof DateTimeColumn) {
                dateTimeColumnMap.put(column.name(), new PackedDateTime((DateTimeColumn) column));
            } else if (column instanceof TimeColumn) {
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

    public double getDouble(String columnName) {
        return numberColumnMap.get(columnName).get(rowNumber);
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

    public boolean getBoolean(String columnName) {
        return booleanColumnMap.get(columnName).get(rowNumber);
    }

    public void at(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getRowNumber() {
        return rowNumber;
    }
}
