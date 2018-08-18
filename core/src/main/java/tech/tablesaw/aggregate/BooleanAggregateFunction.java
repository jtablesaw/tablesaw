package tech.tablesaw.aggregate;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;

/**
 * A partial implementation of aggregate functions to summarize over a boolean column
 */
public abstract class BooleanAggregateFunction extends AggregateFunction<BooleanColumn, Boolean> {

    public BooleanAggregateFunction(String name) {
        super(name);
    }

    abstract public Boolean summarize(BooleanColumn column);

    @Override
    public boolean isCompatibleColumn(ColumnType type) {
        return type == ColumnType.BOOLEAN;
    }

    @Override
    public ColumnType returnType() {
        return ColumnType.BOOLEAN;
    }
}
