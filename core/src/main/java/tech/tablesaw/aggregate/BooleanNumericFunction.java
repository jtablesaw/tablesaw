package tech.tablesaw.aggregate;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;

abstract class BooleanNumericFunction extends AggregateFunction<BooleanColumn, Double> {

  public BooleanNumericFunction(String functionName) {
    super(functionName);
  }

  @Override
  public abstract Double summarize(BooleanColumn column);

  @Override
  public boolean isCompatibleColumn(ColumnType type) {
    return type.equals(ColumnType.BOOLEAN);
  }

  @Override
  public ColumnType returnType() {
    return null;
  }
}
