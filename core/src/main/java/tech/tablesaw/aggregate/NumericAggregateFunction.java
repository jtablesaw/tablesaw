package tech.tablesaw.aggregate;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.NumericColumn;

/** A partial implementation of aggregate functions to summarize over a numeric column */
public abstract class NumericAggregateFunction extends AggregateFunction<NumericColumn<?>, Double> {

  /**
   * Constructs a NumericAggregateFunction with the given name. The name may be used to name a
   * column in the output when this function is used by {@link Summarizer}
   */
  public NumericAggregateFunction(String name) {
    super(name);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isCompatibleColumn(ColumnType type) {
    return type.equals(ColumnType.DOUBLE)
        || type.equals(ColumnType.FLOAT)
        || type.equals(ColumnType.INTEGER)
        || type.equals(ColumnType.SHORT)
        || type.equals(ColumnType.LONG);
  }

  /** {@inheritDoc} */
  @Override
  public ColumnType returnType() {
    return ColumnType.DOUBLE;
  }
}
