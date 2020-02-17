package tech.tablesaw.filtering;

import com.google.common.annotations.Beta;
import tech.tablesaw.api.NumericColumn;

@Beta
public interface NumberFilterSpec<T> extends FilterSpec<T> {

  T isEqualTo(double other);

  T isBetweenExclusive(double start, double end);

  T isBetweenInclusive(double start, double end);

  T isGreaterThan(double f);

  T isGreaterThanOrEqualTo(double f);

  T isLessThan(double f);

  T isLessThanOrEqualTo(double f);

  T isIn(Number... numbers);

  T isIn(double... doubles);

  T isNotIn(Number... numbers);

  T isNotIn(double... doubles);

  T isZero();

  T isPositive();

  T isNegative();

  T isNonNegative();

  T isCloseTo(Number target, Number margin);

  T isGreaterThan(NumericColumn<?> d);

  T isGreaterThanOrEqualTo(NumericColumn<?> d);

  T isEqualTo(NumericColumn<?> d);

  T isNotEqualTo(NumericColumn<?> d);

  T isLessThan(NumericColumn<?> d);

  T isLessThanOrEqualTo(NumericColumn<?> d);
}
