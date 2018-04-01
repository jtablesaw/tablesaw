package tech.tablesaw.columns.times;

import tech.tablesaw.api.TimeColumn;

import java.time.LocalTime;

public class PackedTime {

    private int index = 0;
    private final TimeColumn timeColumn;

    public PackedTime(TimeColumn column) {
        this.timeColumn = column;
    }

    PackedTime next() {
        index++;
        return this;
    }

    public PackedTime get(int rowNumber) {
        index = rowNumber;
        return this;
    }

    public int getPackedValue() {
        return timeColumn.getPackedTime(index);
    }

    public LocalTime asLocalTime() {
        return PackedLocalTime.asLocalTime(timeColumn.getIntInternal(index));
    }
}
