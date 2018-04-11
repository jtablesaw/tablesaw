package tech.tablesaw.aggregate;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.Column;

/**
 * A partial implementation of aggregate functions to summarize over a dateTime column
 */
public abstract class DateTimeReduction extends Reduction{

    public DateTimeReduction(String name) {
        super(name);
    }

    abstract public double summarize(Column column);

    public boolean isCompatibleWith(ColumnType type) {
        return type.equals(ColumnType.LOCAL_DATE_TIME);
    }
}
