package tech.tablesaw.aggregate;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.Column;

/**
 * A partial implementation of aggregate functions to summarize over a numeric column
 */
public abstract class AggregateFunction {

    private final String name;

    public AggregateFunction(String name) {
        this.name = name;
    }

    abstract public double summarize(Column<?> column);

    public String functionName() {
        return name;
    }

    public String toString() {
        return functionName();
    }

    abstract public boolean isCompatibleWith(ColumnType type);
}
