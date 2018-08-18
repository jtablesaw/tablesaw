package tech.tablesaw.aggregate;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateTimeColumn;

import java.time.LocalDateTime;

/**
 * A partial implementation of aggregate functions to summarize over a dateTime column
 */
public abstract class DateTimeAggregateFunction extends AggregateFunction<DateTimeColumn, LocalDateTime> {

    public DateTimeAggregateFunction(String name) {
        super(name);
    }

    abstract public LocalDateTime summarize(DateTimeColumn column);

    @Override
    public boolean isCompatibleColumn(ColumnType type) {
        return type.equals(ColumnType.LOCAL_DATE_TIME);
    }

    @Override
    public ColumnType returnType() {
        return ColumnType.LOCAL_DATE_TIME;
    }
}
