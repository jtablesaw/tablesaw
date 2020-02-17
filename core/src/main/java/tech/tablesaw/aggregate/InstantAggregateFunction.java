package tech.tablesaw.aggregate;

import java.time.Instant;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.InstantColumn;

/** A partial implementation of aggregate functions to summarize over an instant column */
public abstract class InstantAggregateFunction extends AggregateFunction<InstantColumn, Instant> {

  public InstantAggregateFunction(String name) {
    super(name);
  }

  public abstract Instant summarize(InstantColumn column);

  @Override
  public boolean isCompatibleColumn(ColumnType type) {
    return type.equals(ColumnType.INSTANT);
  }

  @Override
  public ColumnType returnType() {
    return ColumnType.INSTANT;
  }
}
