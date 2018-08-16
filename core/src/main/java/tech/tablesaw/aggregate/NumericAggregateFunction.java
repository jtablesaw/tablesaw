package tech.tablesaw.aggregate;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.NumberColumn;

/**
 * A partial implementation of aggregate functions to summarize over a numeric column
 */
public abstract class NumericAggregateFunction extends AggregateFunction<NumberColumn, Double> {

    public NumericAggregateFunction(String name) {
        super(name);
    }

    @Override
    public boolean isCompatableColumn(ColumnType type) {
        return type.equals(ColumnType.DOUBLE);
    }

    @Override
    public ColumnType returnType() {
        return ColumnType.DOUBLE;
    }
}
