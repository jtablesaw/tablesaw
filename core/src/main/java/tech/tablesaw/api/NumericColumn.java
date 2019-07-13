package tech.tablesaw.api;

import static tech.tablesaw.aggregate.AggregateFunctions.geometricMean;
import static tech.tablesaw.aggregate.AggregateFunctions.kurtosis;
import static tech.tablesaw.aggregate.AggregateFunctions.max;
import static tech.tablesaw.aggregate.AggregateFunctions.mean;
import static tech.tablesaw.aggregate.AggregateFunctions.median;
import static tech.tablesaw.aggregate.AggregateFunctions.min;
import static tech.tablesaw.aggregate.AggregateFunctions.populationVariance;
import static tech.tablesaw.aggregate.AggregateFunctions.product;
import static tech.tablesaw.aggregate.AggregateFunctions.quadraticMean;
import static tech.tablesaw.aggregate.AggregateFunctions.quartile1;
import static tech.tablesaw.aggregate.AggregateFunctions.quartile3;
import static tech.tablesaw.aggregate.AggregateFunctions.range;
import static tech.tablesaw.aggregate.AggregateFunctions.skewness;
import static tech.tablesaw.aggregate.AggregateFunctions.stdDev;
import static tech.tablesaw.aggregate.AggregateFunctions.sum;
import static tech.tablesaw.aggregate.AggregateFunctions.sumOfLogs;
import static tech.tablesaw.aggregate.AggregateFunctions.sumOfSquares;
import static tech.tablesaw.aggregate.AggregateFunctions.variance;
import static tech.tablesaw.columns.numbers.NumberPredicates.isMissing;
import static tech.tablesaw.columns.numbers.NumberPredicates.isNotMissing;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;

import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleRBTreeSet;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.aggregate.NumericAggregateFunction;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.NumberFilters;
import tech.tablesaw.columns.numbers.NumberMapFunctions;
import tech.tablesaw.columns.numbers.NumberRollingColumn;
import tech.tablesaw.columns.numbers.Stats;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

public interface NumericColumn<T> extends Column<T>, NumberMapFunctions, NumberFilters {

    @Override
    default boolean isEmpty() {
        return size() == 0;
    }

    @Override
    default double[] asDoubleArray() {
        final double[] output = new double[size()];
        for (int i = 0; i < size(); i++) {
            output[i] = getDouble(i);
        }
        return output;
    }

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

    @Override
    default Selection isIn(final Number... numbers) {
        return isIn(Arrays.stream(numbers).mapToDouble(Number::doubleValue).toArray());
    }

    @Override
    default Selection isIn(final double... doubles) {
        final Selection results = new BitmapBackedSelection();
        final DoubleRBTreeSet doubleSet = new DoubleRBTreeSet(doubles);
        for (int i = 0; i < size(); i++) {
            if (doubleSet.contains(getDouble(i))) {
                results.add(i);
            }
        }
        return results;
    }

    @Override
    default Selection isNotIn(final Number... numbers) {
        final Selection results = new BitmapBackedSelection();
        results.addRange(0, size());
        results.andNot(isIn(numbers));
        return results;
    }

    @Override
    default Selection isNotIn(final double... doubles) {
        final Selection results = new BitmapBackedSelection();
        results.addRange(0, size());
        results.andNot(isIn(doubles));
        return results;
    }

    @Override
    default Selection isMissing() {
        return eval(isMissing);
    }

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
     * @param max  the maximum number of rows to count
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
     * Returns the maximum row according to the provided Comparator
     *
     * @param comp
     * @return the maximum row
     */
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

    /**
     * Returns the minimum row according to the provided Comparator
     *
     * @param comp
     * @return the minimum row
     */
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
     * Reduction with binary operator and initial value
     *
     * @param initial initial value
     * @param op      the operator
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
     * @param fun  function to map
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

    @Override
    default NumericColumn<T> where(final Selection selection) {
        return (NumericColumn<T>) subset(selection.toArray());
    }

    /**
     * Summarizes the data in this column for all rows where the current value matches the selection criteria
     * <p>
     * Example:
     * myColumn.summarize(myColumn.isLessThan(100), AggregateFunctions.count);
     */
    default Double summarize(Selection selection, NumericAggregateFunction function) {
        NumericColumn<T> column = where(selection);
        return function.summarize(column);
    }

    // Reduce functions applied to the whole column
    default double sum() {
        return sum.summarize(this);
    }

    default double product() {
        return product.summarize(this);
    }

    default double mean() {
        return mean.summarize(this);
    }

    default double median() {
        return median.summarize(this);
    }

    default double quartile1() {
        return quartile1.summarize(this);
    }

    default double quartile3() {
        return quartile3.summarize(this);
    }

    default double percentile(double percentile) {
        return AggregateFunctions.percentile(this, percentile);
    }

    default double range() {
        return range.summarize(this);
    }

    default double max() {
        return max.summarize(this);
    }

    default double min() {
        return min.summarize(this);
    }

    default double variance() {
        return variance.summarize(this);
    }

    default double populationVariance() {
        return populationVariance.summarize(this);
    }

    default double standardDeviation() {
        return stdDev.summarize(this);
    }

    default double sumOfLogs() {
        return sumOfLogs.summarize(this);
    }

    default double sumOfSquares() {
        return sumOfSquares.summarize(this);
    }

    default double geometricMean() {
        return geometricMean.summarize(this);
    }

    /**
     * Returns the quadraticMean, aka the root-mean-square, for all values in this column
     */
    default double quadraticMean() {
        return quadraticMean.summarize(this);
    }

    default double kurtosis() {
        return kurtosis.summarize(this);
    }

    default double skewness() {
        return skewness.summarize(this);
    }

    /**
     * Returns the pearson's correlation between the receiver and the otherColumn
     **/
    default double pearsons(NumericColumn<?> otherColumn) {
        double[] x = asDoubleArray();
        double[] y = otherColumn.asDoubleArray();
        return new PearsonsCorrelation().correlation(x, y);
    }

    /**
     * Returns the Spearman's Rank correlation between the receiver and the otherColumn
     *
     * @param otherColumn A NumberColumn with no missing values
     * @throws NotANumberException if either column contains any missing values
     **/
    default double spearmans(NumericColumn<?> otherColumn) {
        double[] x = asDoubleArray();
        double[] y = otherColumn.asDoubleArray();
        return new SpearmansCorrelation().correlation(x, y);
    }

    /**
     * Returns the Kendall's Tau Rank correlation between the receiver and the otherColumn
     **/
    default double kendalls(NumericColumn<?> otherColumn) {
        double[] x = asDoubleArray();
        double[] y = otherColumn.asDoubleArray();
        return new KendallsCorrelation().correlation(x, y);
    }

    default Table summary() {
        return stats().asTable();
    }

    default Stats stats() {
        return Stats.create(this);
    }
    
    default NumberRollingColumn rolling(final int windowSize) {
        return new NumberRollingColumn(this, windowSize);
    }

    default DoubleColumn pctChange(int periods) {
        return (DoubleColumn) rolling(periods + 1).calc(AggregateFunctions.pctChange)
            .setName(name() + " " + periods + "-period " + AggregateFunctions.pctChange.functionName());
    }

    @Override
    default NumericColumn<T> lead(final int n) {
        final NumericColumn<T> numberColumn = lag(-n);
        numberColumn.setName(name() + " lead(" + n + ")");
        return numberColumn;
    }

    NumericColumn<T> lag(final int n);

    double getDouble(int index);

    /**
     * Returns a new LongColumn containing a value for each value in this column
     *
     * The exact behavior when overridden depends on the type of the receiver (LongColumn, FloatColumn, etc.)
     *
     * In this version, the result is a copy of the original
     */
    default LongColumn asLongColumn() {
        return (LongColumn) this.copy();
    }

    /**
     * Returns a new IntColumn containing  a value for each value in this column
     *
     * The exact behavior when overridden depends on the type of the receiver (LongColumn, FloatColumn, etc.)
     *
     * In this version, the result is a copy of the original
     */
    default IntColumn asIntColumn() {
        return (IntColumn) this.copy();
    }

    /**
     * Returns a new FloatColumn containing a value for each value in this column
     *
     * The exact behavior when overridden depends on the type of the receiver (LongColumn, FloatColumn, etc.)
     *
     * In this version, the result is a copy of the original
     */
    default FloatColumn asFloatColumn() {
        return (FloatColumn) this.copy();
    }

    /**
     * Returns a new DoubleColumn containing a value for each value in this column
     *
     * The exact behavior when overridden depends on the type of the receiver (LongColumn, FloatColumn, etc.)
     *
     * In this version, the result is a copy of the original
     */
    default DoubleColumn asDoubleColumn() {
        return (DoubleColumn) this.copy();
    }

    default ShortColumn asShortColumn() {
        return (ShortColumn) this.copy();
    }

    StringColumn asStringColumn();
}
