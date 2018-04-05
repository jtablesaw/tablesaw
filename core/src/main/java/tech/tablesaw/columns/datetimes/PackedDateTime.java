package tech.tablesaw.columns.datetimes;

import tech.tablesaw.api.DateTimeColumn;

import java.time.LocalDateTime;

public class PackedDateTime {

    private int index = 0;
    private final DateTimeColumn dateTimeColumn;

    public PackedDateTime(DateTimeColumn column) {
        this.dateTimeColumn = column;
    }

    PackedDateTime next() {
        index++;
        return this;
    }

    public PackedDateTime get(int rowNumber) {
        index = rowNumber;
        return this;
    }

    public long getPackedValue() {
        return dateTimeColumn.getLongInternal(index);
    }

    public LocalDateTime asLocalDateTime() {
        return PackedLocalDateTime.asLocalDateTime(dateTimeColumn.getLongInternal(index));
    }
}
