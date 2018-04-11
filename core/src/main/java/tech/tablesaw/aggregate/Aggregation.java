package tech.tablesaw.aggregate;

/**
 * A partial implementation of aggregate functions to summarize over a numeric column
 */
public abstract class Aggregation implements AggregateFunction {

    private final String name;

    public Aggregation(String name) {
        this.name = name;
    }

    @Override
    public String functionName() {
        return name;
    }

    @Override
    public String toString() {
        return functionName();
    }
}
