package tech.tablesaw.aggregate;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;

import java.time.LocalDate;

/**
 * A partial implementation of aggregate functions to summarize over a date column
 */
public abstract class DateAggregateFunction extends AggregateFunction<DateColumn, LocalDate> {

    public DateAggregateFunction(String name) {
        super(name);
    }

    abstract public LocalDate summarize(DateColumn column);

    @Override
    public boolean isCompatibleColumn(ColumnType type) {
        return type.equals(ColumnType.LOCAL_DATE);
    }

    @Override
    public ColumnType returnType() {
        return ColumnType.LOCAL_DATE;
    }
}
