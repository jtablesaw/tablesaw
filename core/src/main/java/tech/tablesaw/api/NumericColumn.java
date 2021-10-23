package tech.tablesaw.api;

import static tech.tablesaw.aggregate.AggregateFunctions.*;
import static tech.tablesaw.columns.numbers.NumberPredicates.isMissing;
import static tech.tablesaw.columns.numbers.NumberPredicates.isNotMissing;

import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.aggregate.NumericAggregateFunction;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.*;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/**
 * A Column of numeric values
 *
 * @param <T>
 */
public interface NumericColumn<T extends Number>
    extends Column<T>, NumberMapFunctions, NumberFilters {

  /** {@inheritDoc} */
  @Override
  default boolean isEmpty() {
    return size() == 0;
  }

  /** {@inheritDoc} */
  @Override
  default double[] asDoubleArray() {
    final double[] output = new double[size()];
    for (int i = 0; i < size(); i++) {
      output[i] = getDouble(i);
    }
    return output;
  }

  /** {@inheritDoc} */
  @Override
  default Selection eval(final DoublePredicate predicate) {
    final Selection bitmap = new BitmapBackedSelection();
    for (int idx = 0; idx < size(); idx++) {
      final double next = getDouble(idx);
      if (predicate.test(next)) {
        bitmap.add(idx);
      }
    }
    return bitmap;
  }

  /** {@inheritDoc} */
  @Override
  default Selection eval(final BiPredicate<Number, Number> predicate, final Number number) {
    final double value = number.doubleValue();
    final Selection bitmap = new BitmapBackedSelection();
    for (int idx = 0; idx < size(); idx++) {
      final double next = getDouble(idx);
      if (predicate.test(next, value)) {
        bitmap.add(idx);
      }
    }
    return bitmap;
  }

  /** {@inheritDoc} */
  @Override
  default Selection isIn(Collection<Number> numbers) {
    final Selection results = new BitmapBackedSelection();
    for (int i = 0; i < size(); i++) {
      if (numbers.contains(getDouble(i))) {
        results.add(i);
      }
    }
    return results;
  }

  /** {@inheritDoc} */
  @Override
  default Selection isNotIn(Collection<Number> numbers) {
    final Selection results = new BitmapBackedSelection();
    results.addRange(0, size());
    results.andNot(isIn(numbers));
    return results;
  }

  /** {@inheritDoc} */
  @Override
  default Selection isMissing() {
    return eval(isMissing);
  }

  /** {@inheritDoc} */
  @Override
  default Selection isNotMissing() {
    return eval(isNotMissing);
  }

  /**
   * Counts the number of rows satisfying predicate
   *
   * @param test the predicate
   * @return the number of rows satisfying the predicate
   */
  default int count(DoublePredicate test) {
    return count(test, size());
  }

  /**
   * Counts the number of rows satisfying predicate, but only upto the max value
   *
   * @param test the predicate
   * @param max the maximum number of rows to count
   * @return the number of rows satisfying the predicate
   */
  default int count(DoublePredicate test, int max) {
    int count = 0;
    for (int i = 0; i < size(); i++) {
      if (test.test(getDouble(i))) {
        count++;
        if (count >= max) {
          return count;
        }
      }
    }
    return count;
  }

  /**
   * Returns true if all rows satisfy the predicate, false otherwise
   *
   * @param test the predicate
   * @return true if all rows satisfy the predicate, false otherwise
   */
  default boolean allMatch(DoublePredicate test) {
    return count(test.negate(), 1) == 0;
  }

  /**
   * Returns true if any row satisfies the predicate, false otherwise
   *
   * @param test the predicate
   * @return true if any rows satisfies the predicate, false otherwise
   */
  default boolean anyMatch(DoublePredicate test) {
    return count(test, 1) > 0;
  }

  /**
   * Returns true if no row satisfies the predicate, false otherwise
   *
   * @param test the predicate
   * @return true if no row satisfies the predicate, false otherwise
   */
  default boolean noneMatch(DoublePredicate test) {
    return count(test, 1) == 0;
  }

  /** Returns the maximum row according to the provided Comparator */
  default Optional<Double> max(DoubleComparator comp) {
    boolean first = true;
    double d1 = 0.0;
    for (int i = 0; i < size(); i++) {
      double d2 = getDouble(i);
      if (first) {
        d1 = d2;
        first = false;
      } else if (comp.compare(d1, d2) < 0) {
        d1 = d2;
      }
    }
    return (first ? Optional.<Double>empty() : Optional.<Double>of(d1));
  }

  /** Returns the minimum row according to the provided Comparator */
  default Optional<Double> min(DoubleComparator comp) {
    boolean first = true;
    double d1 = 0.0;
    for (int i = 0; i < size(); i++) {
      double d2 = getDouble(i);
      if (first) {
        d1 = d2;
        first = false;
      } else if (comp.compare(d1, d2) > 0) {
        d1 = d2;
      }
    }
    return (first ? Optional.<Double>empty() : Optional.<Double>of(d1));
  }

  /**
   * Sets the print formatter to a new {@link tech.tablesaw.columns.ColumnFormatter} constructed
   * from the given number format and missing value indicator TODO: make these return the column?
   */
  void setPrintFormatter(final NumberFormat format, final String missingValueIndicator);

  /** Sets the print formatter to the argument */
  void setPrintFormatter(final NumberColumnFormatter formatter);

  /**
   * Reduction with binary operator and initial value
   *
   * @param initial initial value
   * @param op the operator
   * @return the result of reducing initial value and all rows with operator
   */
  default double reduce(double initial, DoubleBinaryOperator op) {
    double acc = initial;
    for (int i = 0; i < size(); i++) {
      acc = op.applyAsDouble(acc, getDouble(i));
    }
    return acc;
  }

  /**
   * Reduction with binary operator
   *
   * @param op the operator
   * @return Optional with the result of reducing all rows with operator
   */
  default Optional<Double> reduce(DoubleBinaryOperator op) {
    boolean first = true;
    double acc = 0.0;
    for (int i = 0; i < size(); i++) {
      double d = getDouble(i);
      if (first) {
        acc = d;
        first = false;
      } else {
        acc = op.applyAsDouble(acc, d);
      }
    }
    return (first ? Optional.<Double>empty() : Optional.<Double>of(acc));
  }

  /**
   * Maps the function across all rows, appending the results to the provided Column
   *
   * @param fun function to map
   * @param into Column to which results are appended
   * @return the provided Column, to which results are appended
   */
  default <R extends Column<RT>, RT> R mapInto(DoubleFunction<? extends RT> fun, R into) {
    for (int i = 0; i < size(); i++) {
      try {
        into.append(fun.apply(getDouble(i)));
      } catch (Exception e) {
        into.appendMissing();
      }
    }
    return into;
  }

  /** Returns the subset of data in this column included in the given {@link Selection} */
  @Override
  default NumericColumn<T> where(final Selection selection) {
    return (NumericColumn<T>) subset(selection.toArray());
  }

  /**
   * Returns a {@link NumberInterpolator} object that can be used to interpolate values for elements
   * missing in the column
   */
  @Override
  default NumberInterpolator<T> interpolate() {
    return new NumberInterpolator<>(this);
  }

  /**
   * Summarizes the data in this column for all rows where the current value matches the selection
   * criteria
   *
   * <p>Example: myColumn.summarize(myColumn.isLessThan(100), AggregateFunctions.count);
   */
  default Double summarize(Selection selection, NumericAggregateFunction function) {
    NumericColumn<T> column = where(selection);
    return function.summarize(column);
  }

  /** Returns the sum of the values in this column */
  default double sum() {
    return sum.summarize(this);
  }

  /** Returns the product of values in this column */
  default double product() {
    return product.summarize(this);
  }

  /** Returns the mean of the data in this column */
  default double mean() {
    return mean.summarize(this);
  }

  /** Returns the median or 50th percentile of the data in this column */
  default double median() {
    return median.summarize(this);
  }

  /** Returns the 1st quartile of the data in this column */
  default double quartile1() {
    return quartile1.summarize(this);
  }

  /** Returns the 3rd quartile of the data in this column */
  default double quartile3() {
    return quartile3.summarize(this);
  }

  /** Returns the given percentile of the data in this column */
  default double percentile(double percentile) {
    return AggregateFunctions.percentile(this, percentile);
  }

  /** Returns the range of the data in this column */
  default double range() {
    return range.summarize(this);
  }

  /** Returns the largest value in this column */
  default double max() {
    return max.summarize(this);
  }

  /** Returns the smallest value in this column */
  default double min() {
    return min.summarize(this);
  }

  /** Returns the sample variance of the data in this column */
  default double variance() {
    return variance.summarize(this);
  }

  /** Returns the population variance of the data in this column */
  default double populationVariance() {
    return populationVariance.summarize(this);
  }

  /** Returns the standard deviation of the data in this column */
  default double standardDeviation() {
    return stdDev.summarize(this);
  }

  /** Returns the sum of logs of the data in this column */
  default double sumOfLogs() {
    return sumOfLogs.summarize(this);
  }

  /** Returns the sum of squares of the data in this column */
  default double sumOfSquares() {
    return sumOfSquares.summarize(this);
  }

  /** Returns the geometric mean of the data in this column */
  default double geometricMean() {
    return geometricMean.summarize(this);
  }

  /** Returns the quadraticMean, aka the root-mean-square, for all values in this column */
  default double quadraticMean() {
    return quadraticMean.summarize(this);
  }

  /** Returns the kurtosis of the data in this column */
  default double kurtosis() {
    return kurtosis.summarize(this);
  }

  /** Returns the skewness of the data in this column */
  default double skewness() {
    return skewness.summarize(this);
  }

  /** Returns the pearson's correlation between the receiver and the otherColumn */
  default double pearsons(NumericColumn<?> otherColumn) {
    double[] x = asDoubleArray();
    double[] y = otherColumn.asDoubleArray();
    return new PearsonsCorrelation().correlation(x, y);
  }

  /** Returns the auto-correlation (correlation between each element and the next) */
  default double autoCorrelation() {
    int defaultLag = 1;
    return autoCorrelation(defaultLag);
  }

  /**
   * Returns the auto-correlation between elements separated by {@code lag}. If lag is 2, the
   * correlation is computed between pairs of elements 0 and 2, 1 and 3; 2 and 4, etc.
   */
  default double autoCorrelation(int lag) {
    int slice = this.size() - lag;
    if (slice <= 1) {
      return Double.NaN;
    }
    NumericColumn<?> x = (NumericColumn<?>) this.first(slice);
    NumericColumn<?> y = (NumericColumn<?>) this.last(slice);
    return new PearsonsCorrelation().correlation(x.asDoubleArray(), y.asDoubleArray());
  }

  /**
   * Returns the Spearman's Rank correlation between the receiver and the otherColumn
   *
   * @param otherColumn A NumberColumn with no missing values
   * @throws NotANumberException if either column contains any missing values
   */
  default double spearmans(NumericColumn<?> otherColumn) {
    double[] x = asDoubleArray();
    double[] y = otherColumn.asDoubleArray();
    return new SpearmansCorrelation().correlation(x, y);
  }

  /** Returns the Kendall's Tau Rank correlation between the receiver and the otherColumn */
  default double kendalls(NumericColumn<?> otherColumn) {
    double[] x = asDoubleArray();
    double[] y = otherColumn.asDoubleArray();
    return new KendallsCorrelation().correlation(x, y);
  }

  /** Returns a table of common statistical values that together describe the data in this column */
  default Table summary() {
    return stats().asTable();
  }

  /**
   * Returns a {@link Stats} object that collects common statistical measures of the data in this
   * column
   */
  default Stats stats() {
    return Stats.create(this);
  }

  /** {@inheritDoc} */
  @Override
  default NumberRollingColumn rolling(final int windowSize) {
    return new NumberRollingColumn(this, windowSize);
  }

  /**
   * Returns a column containing the percentage change between values that are {@code periods} apart
   */
  default DoubleColumn pctChange(int periods) {
    return (DoubleColumn)
        rolling(periods + 1)
            .calc(AggregateFunctions.pctChange)
            .setName(
                name() + " " + periods + "-period " + AggregateFunctions.pctChange.functionName());
  }

  /** {@inheritDoc} */
  @Override
  default NumericColumn<T> lead(final int n) {
    final NumericColumn<T> numberColumn = lag(-n);
    numberColumn.setName(name() + " lead(" + n + ")");
    return numberColumn;
  }

  /** {@inheritDoc} */
  @Override
  NumericColumn<T> lag(final int n);

  /** Returns a double representation of the number at {@code index} */
  double getDouble(int index);

  /**
   * Returns a new LongColumn containing a value for each value in this column
   *
   * <p>The exact behavior when overridden depends on the type of the receiver (LongColumn,
   * FloatColumn, etc.)
   *
   * <p>In this version, the result is a copy of the original
   */
  default LongColumn asLongColumn() {
    return (LongColumn) this.copy();
  }

  /**
   * Returns a new IntColumn containing a value for each value in this column
   *
   * <p>The exact behavior when overridden depends on the type of the receiver (LongColumn,
   * FloatColumn, etc.)
   *
   * <p>In this version, the result is a copy of the original
   */
  default IntColumn asIntColumn() {
    return (IntColumn) this.copy();
  }

  /**
   * Returns a new FloatColumn containing a value for each value in this column
   *
   * <p>The exact behavior when overridden depends on the type of the receiver (LongColumn,
   * FloatColumn, etc.)
   *
   * <p>In this version, the result is a copy of the original
   */
  default FloatColumn asFloatColumn() {
    return (FloatColumn) this.copy();
  }

  /**
   * Returns a new DoubleColumn containing a value for each value in this column
   *
   * <p>The exact behavior when overridden depends on the type of the receiver (LongColumn,
   * FloatColumn, etc.)
   *
   * <p>In this version, the result is a copy of the original
   */
  default DoubleColumn asDoubleColumn() {
    return (DoubleColumn) this.copy();
  }

  /**
   * Returns a new ShortColumn containing a value for each value in this column
   *
   * <p>The exact behavior when overridden depends on the type of the receiver (LongColumn,
   * FloatColumn, etc.)
   *
   * <p>In this version, the result is a copy of the original
   */
  default ShortColumn asShortColumn() {
    return (ShortColumn) this.copy();
  }

  /** {@inheritDoc} */
  @Override
  NumericColumn<T> copy();

  /** {@inheritDoc} */
  @Override
  StringColumn asStringColumn();
}
