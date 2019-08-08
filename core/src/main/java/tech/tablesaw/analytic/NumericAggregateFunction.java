package tech.tablesaw.analytic;

import tech.tablesaw.api.ColumnType;

public abstract class NumericAggregateFunction<T extends Number>
  implements AggregateFunction<T, Double> {

  @Override
  public ColumnType returnType(ColumnType inputColumnType) {
    return ColumnType.DOUBLE;
  }

  @Override
  public boolean isCompatibleColumn(ColumnType type) {
    return type.equals(ColumnType.DOUBLE)
      || type.equals(ColumnType.FLOAT)
      || type.equals(ColumnType.INTEGER)
      || type.equals(ColumnType.SHORT)
      || type.equals(ColumnType.LONG);
  }
}
