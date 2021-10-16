package tech.tablesaw.aggregate;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.NumericColumn;

/** A partial implementation of aggregate functions to summarize over a numeric column */
public abstract class NumericAggregateFunction extends AggregateFunction<NumericColumn<?>, Double> {

  /**
   * Constructs a NumericAggregateFunction with the given name. The name is used as a column name in
   * the output
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
