package tech.tablesaw.filtering;

import com.google.common.annotations.Beta;
import java.util.Collection;
import java.util.function.Function;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

@Beta
public class DeferredNumberColumn extends DeferredColumn
    implements NumberFilterSpec<Function<Table, Selection>> {

  public DeferredNumberColumn(String columnName) {
    super(columnName);
  }

  @Override
  public Function<Table, Selection> isEqualTo(double other) {
    return table -> table.numberColumn(name()).isEqualTo(other);
  }

  @Override
  public Function<Table, Selection> isBetweenExclusive(double start, double end) {
    return table -> table.numberColumn(name()).isBetweenExclusive(start, end);
  }

  @Override
  public Function<Table, Selection> isBetweenInclusive(double start, double end) {
    return table -> table.numberColumn(name()).isBetweenInclusive(start, end);
  }

  @Override
  public Function<Table, Selection> isGreaterThan(double f) {
    return table -> table.numberColumn(name()).isGreaterThan(f);
  }

  @Override
  public Function<Table, Selection> isGreaterThanOrEqualTo(double f) {
    return table -> table.numberColumn(name()).isGreaterThanOrEqualTo(f);
  }

  @Override
  public Function<Table, Selection> isLessThan(double f) {
    return table -> table.numberColumn(name()).isLessThan(f);
  }

  @Override
  public Function<Table, Selection> isLessThanOrEqualTo(double f) {
    return table -> table.numberColumn(name()).isLessThanOrEqualTo(f);
  }

  @Override
  public Function<Table, Selection> isIn(Collection<Number> numbers) {
    return table -> table.numberColumn(name()).isIn(numbers);
  }

  @Override
  public Function<Table, Selection> isNotIn(Collection<Number> numbers) {
    return table -> table.numberColumn(name()).isNotIn(numbers);
  }

  @Override
  public Function<Table, Selection> isZero() {
    return table -> table.numberColumn(name()).isZero();
  }

  @Override
  public Function<Table, Selection> isPositive() {
    return table -> table.numberColumn(name()).isPositive();
  }

  @Override
  public Function<Table, Selection> isNegative() {
    return table -> table.numberColumn(name()).isNegative();
  }

  @Override
  public Function<Table, Selection> isNonNegative() {
    return table -> table.numberColumn(name()).isNonNegative();
  }

  @Override
  public Function<Table, Selection> isCloseTo(Number target, Number margin) {
    return table -> table.numberColumn(name()).isCloseTo(target, margin);
  }

  @Override
  public Function<Table, Selection> isGreaterThan(NumericColumn<?> d) {
    return table -> table.numberColumn(name()).isGreaterThan(d);
  }

  @Override
  public Function<Table, Selection> isGreaterThanOrEqualTo(NumericColumn<?> d) {
    return table -> table.numberColumn(name()).isGreaterThanOrEqualTo(d);
  }

  @Override
  public Function<Table, Selection> isEqualTo(NumericColumn<?> d) {
    return table -> table.numberColumn(name()).isEqualTo(d);
  }

  @Override
  public Function<Table, Selection> isNotEqualTo(NumericColumn<?> d) {
    return table -> table.numberColumn(name()).isNotEqualTo(d);
  }

  @Override
  public Function<Table, Selection> isLessThan(NumericColumn<?> d) {
    return table -> table.numberColumn(name()).isLessThan(d);
  }

  @Override
  public Function<Table, Selection> isLessThanOrEqualTo(NumericColumn<?> d) {
    return table -> table.numberColumn(name()).isLessThanOrEqualTo(d);
  }
}
