package tech.tablesaw.api;

import com.google.common.collect.ImmutableSortedMap;
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
    private final Map<String, Column> columnMap;

    Row(Table table) {
        this.table = table;
        rowNumber = -1;
        Map<String, Column> map = new HashMap<>();
        for (Column column : table.columns()) {
            map.put(column.name(), column);
        }
        this.columnMap = ImmutableSortedMap.copyOf(map);
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
        Column column = columnMap.get(columnName);
        IntColumn intColumn = (IntColumn) column;
        return intColumn.getInt(rowNumber);
    }

    public double getDouble(String columnName) {
        Column c = columnMap.get(columnName);
        DoubleColumn column = (DoubleColumn) c;
        return column.getDouble(rowNumber);
    }

    public String getString(String columnName) {
        Column c = columnMap.get(columnName);
        CategoryColumn column = (CategoryColumn) c;
        return column.get(rowNumber);
    }

    public LocalDate getPackedLocalDate(String columnName) {
        DateColumn c = (DateColumn) columnMap.get(columnName);
        return c.get(rowNumber);
    }

    public LocalDateTime getLocalDateTime(String columnName) {
        Column c = columnMap.get(columnName);
        DateTimeColumn column = (DateTimeColumn) c;
        return column.get(rowNumber);
    }

    public LocalTime getLocalTime(String columnName) {
        Column c = columnMap.get(columnName);
        TimeColumn column = (TimeColumn) c;
        return column.get(rowNumber);
    }

    public short getShort(String columnName) {
        Column c = columnMap.get(columnName);
        ShortColumn column = (ShortColumn) c;
        return column.get(rowNumber);
    }

    public boolean getBoolean(String columnName) {
        Column c = columnMap.get(columnName);
        BooleanColumn column = (BooleanColumn) c;
        return column.get(rowNumber);
    }

    public float getFloat(String columnName) {
        Column c = columnMap.get(columnName);
        FloatColumn column = (FloatColumn) c;
        return column.get(rowNumber);
    }

    public void at(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getRowNumber() {
        return rowNumber;
    }
}
