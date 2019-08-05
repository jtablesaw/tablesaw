package tech.tablesaw.aggregate;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.NumericColumn;

/** A partial implementation of aggregate functions to summarize over a numeric column */
public abstract class NumericAggregateFunction extends AggregateFunction<NumericColumn<?>, Double> {

  public NumericAggregateFunction(String name) {
    super(name);
  }

  @Override
  public boolean isCompatibleColumn(ColumnType type) {
    return type.equals(ColumnType.DOUBLE)
        || type.equals(ColumnType.FLOAT)
        || type.equals(ColumnType.INTEGER)
        || type.equals(ColumnType.SHORT)
        || type.equals(ColumnType.LONG);
  }

  @Override
  public ColumnType returnType() {
    return ColumnType.DOUBLE;
  }
}
