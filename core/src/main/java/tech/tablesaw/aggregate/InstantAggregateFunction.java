package tech.tablesaw.aggregate;

import java.time.Instant;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.InstantColumn;

/** A partial implementation of aggregate functions to summarize over an instant column */
public abstract class InstantAggregateFunction extends AggregateFunction<InstantColumn, Instant> {

  /**
   * Constructs an InstantAggregateFunction with the given name. The name may be used to name a
   * column in the output when this function is used by {@link Summarizer}
   */
  public InstantAggregateFunction(String name) {
    super(name);
  }

  /** Returns an Instant that is the result of applying this function to the given column */
  public abstract Instant summarize(InstantColumn column);

  /** {@inheritDoc} */
  @Override
  public boolean isCompatibleColumn(ColumnType type) {
    return type.equals(ColumnType.INSTANT);
  }

  /** {@inheritDoc} */
  @Override
  public ColumnType returnType() {
    return ColumnType.INSTANT;
  }
}
