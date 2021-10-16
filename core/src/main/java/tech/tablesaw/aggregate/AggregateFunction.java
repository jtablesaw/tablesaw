package tech.tablesaw.aggregate;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.Column;

/**
 * An abstract class that provides a partial implementation of aggregate functions to summarize over
 * a column
 */
public abstract class AggregateFunction<INCOL extends Column<?>, OUT> {

  private final String functionName;

  /** Constructs a function with the given name */
  public AggregateFunction(String functionName) {
    this.functionName = functionName;
  }

  /** Returns this function's name */
  public String functionName() {
    return functionName;
  }

  /** Apply this function to the column argument */
  public abstract OUT summarize(INCOL column);

  public String toString() {
    return functionName();
  }

  /** Returns true if the given {@link ColumnType} is compatible with this function */
  public abstract boolean isCompatibleColumn(ColumnType type);

  /** Returns the {@link ColumnType} to be used for the values returned by this function */
  public abstract ColumnType returnType();
}
