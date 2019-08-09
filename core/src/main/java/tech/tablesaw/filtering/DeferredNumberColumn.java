package tech.tablesaw.filtering;

import java.util.function.Function;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

public class DeferredNumberColumn extends DeferredColumn {

  public DeferredNumberColumn(String columnName) {
    super(columnName);
  }

  public Function<Table, Selection> isEqualTo(NumberColumn<?> other) {
    return table -> table.numberColumn(name()).isEqualTo(other);
  }

  public Function<Table, Selection> isEqualTo(double other) {
    return table -> table.numberColumn(name()).isEqualTo(other);
  }

  public Function<Table, Selection> isBetweenExclusive(double start, double end) {
    return table -> table.numberColumn(name()).isBetweenExclusive(start, end);
  }

  public Function<Table, Selection> isBetweenInclusive(double start, double end) {
    return table -> table.numberColumn(name()).isBetweenInclusive(start, end);
  }

  public Function<Table, Selection> isGreaterThan(double f) {
    return table -> table.numberColumn(name()).isGreaterThan(f);
  }

  public Function<Table, Selection> isGreaterThanOrEqualTo(double f) {
    return table -> table.numberColumn(name()).isGreaterThanOrEqualTo(f);
  }

  public Function<Table, Selection> isLessThan(double f) {
    return table -> table.numberColumn(name()).isLessThan(f);
  }

  public Function<Table, Selection> isLessThanOrEqualTo(double f) {
    return table -> table.numberColumn(name()).isLessThanOrEqualTo(f);
  }

  public Function<Table, Selection> isIn(Number... numbers) {
    return table -> table.numberColumn(name()).isIn(numbers);
  }

  public Function<Table, Selection> isIn(double... doubles) {
    return table -> table.numberColumn(name()).isIn(doubles);
  }

  public Function<Table, Selection> isNotIn(Number... numbers) {
    return table -> table.numberColumn(name()).isNotIn(numbers);
  }

  public Function<Table, Selection> isNotIn(double... doubles) {
    return table -> table.numberColumn(name()).isNotIn(doubles);
  }

  public Function<Table, Selection> isZero() {
    return table -> table.numberColumn(name()).isZero();
  }

  public Function<Table, Selection> isPositive() {
    return table -> table.numberColumn(name()).isPositive();
  }

  public Function<Table, Selection> isNegative() {
    return table -> table.numberColumn(name()).isNegative();
  }

  public Function<Table, Selection> isNonNegative() {
    return table -> table.numberColumn(name()).isNonNegative();
  }

  public Function<Table, Selection> isCloseTo(Number target, Number margin) {
    return table -> table.numberColumn(name()).isCloseTo(target, margin);
  }

  public Function<Table, Selection> isMissing() {
    return table -> table.numberColumn(name()).isMissing();
  }

  public Function<Table, Selection> isNotMissing() {
    return table -> table.numberColumn(name()).isNotMissing();
  }

  public Function<Table, Selection> isGreaterThan(NumericColumn<?> d) {
    return table -> table.numberColumn(name()).isGreaterThan(d);
  }

  public Function<Table, Selection> isGreaterThanOrEqualTo(NumericColumn<?> d) {
    return table -> table.numberColumn(name()).isGreaterThanOrEqualTo(d);
  }

  public Function<Table, Selection> isEqualTo(NumericColumn<?> d) {
    return table -> table.numberColumn(name()).isEqualTo(d);
  }

  public Function<Table, Selection> isNotEqualTo(NumericColumn<?> d) {
    return table -> table.numberColumn(name()).isNotEqualTo(d);
  }

  public Function<Table, Selection> isLessThan(NumericColumn<?> d) {
    return table -> table.numberColumn(name()).isLessThan(d);
  }

  public Function<Table, Selection> isLessThanOrEqualTo(NumericColumn<?> d) {
    return table -> table.numberColumn(name()).isLessThanOrEqualTo(d);
  }
}
