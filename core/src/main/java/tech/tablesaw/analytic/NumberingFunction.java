package tech.tablesaw.analytic;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.columns.Column;

/**
 * Analytic numbering functions give every value in the window a number based on some ordering rules.
 * // TODO make sure partition is ordered.
 * They require the partition to be ordered.
 */
public abstract class NumberingFunction
  implements MappingFunction<Column<? extends Comparable<?>>, NumericColumn<Integer>> {

  @Override
  public abstract NumericColumn<Integer> apply(Column<? extends Comparable<?>> inputWindow);

  @Override
  public ColumnType returnType(ColumnType inputColumnType) {
    return ColumnType.INTEGER;
  }

  public @Override String toString() {
    return functionName();
  }
}
