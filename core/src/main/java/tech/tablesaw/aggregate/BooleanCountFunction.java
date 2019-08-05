package tech.tablesaw.aggregate;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;

abstract class BooleanCountFunction extends AggregateFunction<BooleanColumn, Integer> {

  public BooleanCountFunction(String functionName) {
    super(functionName);
  }

  @Override
  public abstract Integer summarize(BooleanColumn column);

  @Override
  public boolean isCompatibleColumn(ColumnType type) {
    return type.equals(ColumnType.BOOLEAN);
  }

  @Override
  public ColumnType returnType() {
    return ColumnType.DOUBLE;
  }
}
