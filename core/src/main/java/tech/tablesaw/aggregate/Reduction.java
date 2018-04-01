package tech.tablesaw.aggregate;

/**
 * A partial implementation of aggregate functions to summarize over a numeric column
 */
public abstract class Reduction implements AggregateFunction {

    private final String name;

    public Reduction(String name) {
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
