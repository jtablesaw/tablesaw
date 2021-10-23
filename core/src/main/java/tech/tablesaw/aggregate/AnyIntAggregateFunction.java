package tech.tablesaw.aggregate;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.Column;

/**
 * Partial implementation of AggregateFunction that returns an integer when applied to a column of
 * any type
 */
public abstract class AnyIntAggregateFunction extends AggregateFunction<Column<?>, Integer> {

  /**
   * Constructs a CountFunction with the given name. The name is used to name an output column when
   * this function is used by {@link Summarizer}
   */
  public AnyIntAggregateFunction(String functionName) {
    super(functionName);
  }

  /** Returns an Integer when this function is applied to the given column */
  @Override
  public abstract Integer summarize(Column<?> column);

  /** {@inheritDoc} */
  @Override
  public boolean isCompatibleColumn(ColumnType type) {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public ColumnType returnType() {
    return ColumnType.DOUBLE;
  }
}
