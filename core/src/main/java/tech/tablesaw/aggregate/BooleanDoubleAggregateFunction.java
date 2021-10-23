package tech.tablesaw.aggregate;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;

/**
 * Partial implementation of Aggregate function that returns a Double value when applied to a
 * BooleanColumn
 */
public abstract class BooleanDoubleAggregateFunction
    extends AggregateFunction<BooleanColumn, Double> {

  /**
   * Constructs a BooleanNumericFunction with the given name. The name may be used to name a column
   * in the output when this function is used by {@link Summarizer}
   */
  public BooleanDoubleAggregateFunction(String functionName) {
    super(functionName);
  }

  /** Returns a double that is the result of applying this function to the given column */
  @Override
  public abstract Double summarize(BooleanColumn column);

  /** {@inheritDoc} */
  @Override
  public boolean isCompatibleColumn(ColumnType type) {
    return type.equals(ColumnType.BOOLEAN);
  }

  /** {@inheritDoc} */
  @Override
  public ColumnType returnType() {
    return ColumnType.DOUBLE;
  }
}
