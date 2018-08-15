package tech.tablesaw.aggregate;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.Column;

/**
 * A partial implementation of aggregate functions to summarize over a boolean column
 */
public abstract class BooleanAggregateFunction<C extends Column<?>> extends AggregateFunction<Boolean, C> {

    public BooleanAggregateFunction(String name) {
        super(name);
    }

    @Override
    public boolean isCompatableColumn(ColumnType type) {
        return false;
    }

    @Override
    public ColumnType returnType() {
        return null;
    }
}
