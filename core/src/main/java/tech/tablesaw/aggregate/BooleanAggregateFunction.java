package tech.tablesaw.aggregate;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.Column;

/**
 * A partial implementation of aggregate functions to summarize over a boolean column
 */
public abstract class BooleanAggregateFunction<Boolean> extends AggregateFunction {

    public BooleanAggregateFunction(String name) {
        super(name);
    }

    abstract public Boolean summarize(Column column);

    @Override
    public boolean isCompatableColumn(ColumnType type) {
        return false;
    }

    @Override
    public ColumnType returnType() {
        return null;
    }
}
