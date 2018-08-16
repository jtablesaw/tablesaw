package tech.tablesaw.api;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleOpenHashSet;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.ints.IntComparator;
import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.aggregate.NumericAggregateFunction;
import tech.tablesaw.columns.numbers.DoubleIterable;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.columns.numbers.NumberFillers;
import tech.tablesaw.columns.numbers.NumberFilters;
import tech.tablesaw.columns.numbers.NumberMapFunctions;
import tech.tablesaw.columns.numbers.NumberRollingColumn;
import tech.tablesaw.columns.numbers.Stats;
import tech.tablesaw.filtering.predicates.DoubleBiPredicate;
import tech.tablesaw.filtering.predicates.DoubleRangePredicate;
import tech.tablesaw.selection.Selection;

import java.text.NumberFormat;
import java.time.ZoneOffset;
import java.util.function.DoublePredicate;

import static tech.tablesaw.aggregate.AggregateFunctions.*;
import static tech.tablesaw.api.ColumnType.DOUBLE;
import static tech.tablesaw.columns.numbers.NumberPredicates.isMissing;
import static tech.tablesaw.columns.numbers.NumberPredicates.isNotMissing;

public interface NumberColumn extends NumberMapFunctions, DoubleIterable, NumberFilters, NumberFillers<NumberColumn>, CategoricalColumn<Double> {

    double MISSING_VALUE = (Double) DOUBLE.getMissingValue();

    static boolean valueIsMissing(double value) {
        return Double.isNaN(value);
    }

    @Override
    boolean isMissing(int rowNumber);

    void setPrintFormatter(NumberFormat format, String missingValueString);

    void setPrintFormatter(NumberColumnFormatter formatter);

    @Override
    int size();

    @Override
    Table summary();

    Stats stats();

    DoubleArrayList top(int n);

    DoubleArrayList bottom(int n);

    @Override
    NumberColumn unique();

    double firstElement();

    NumberColumn append(float f);

    NumberColumn append(double d);

    NumberColumn append(int i);

    @Override
    String getString(int row);

    @Override
    double getDouble(int row);

    @Override
    String getUnformattedString(int row);

    @Override
    NumberColumn emptyCopy();

    @Override
    NumberColumn emptyCopy(int rowSize);

    @Override
    NumberColumn copy();

    @Override
    void clear();

    @Override
    void sortAscending();

    @Override
    void sortDescending();

    @Override
    boolean isEmpty();

    @Override
    NumberColumn appendCell(String object);

    Integer roundInt(int i);

    long getLong(int i);

    default Double summarizeIf(Selection selection, NumericAggregateFunction function) {
        NumberColumn column = where(selection);
        return function.summarize(column);
    }

    @Override
    IntComparator rowComparator();

    NumberColumn set(int r, double value);

    NumberColumn set(Selection rowSelection, double newValue);

    double[] asDoubleArray();

    @Override
    NumberColumn where(Selection selection);

    Selection eval(DoublePredicate predicate);

    Selection eval(DoubleBiPredicate predicate, NumberColumn otherColumn);

    Selection eval(DoubleBiPredicate predicate, Number value);

    Selection eval(DoubleRangePredicate predicate, Number rangeStart, Number rangeEnd);

    @Override
    Selection isIn(Number... numbers);

    @Override
    Selection isNotIn(Number... numbers);

    DoubleSet asSet();

    boolean contains(double value);

    @Override
    int byteSize();

    @Override
    byte[] asBytes(int rowNumber);

    @Override
    int[] asIntArray();

    @Override
    DoubleList dataInternal();

    @Override
    NumberColumn appendMissing();

    default NumberRollingColumn rolling(final int windowSize) {
        return new NumberRollingColumn(this, windowSize);
    }    

    /**
     * Returns the count of missing values in this column
     */
    @Override
    default int countMissing() {
        int count = 0;
        for (int i = 0; i < size(); i++) {
            if (NumberColumn.valueIsMissing(getDouble(i))) {
                count++;
            }
        }
        return count;
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
     * Returns a DateTimeColumn where each value is the LocalDateTime represented by the values in this column
     *
     * The values in this column must be longs that represent the time in milliseconds from the epoch as in standard
     * Java date/time calculations
     * @param offset    The ZoneOffset to use in the calculation
     * @return          A column of LocalDateTime values
     */
    DateTimeColumn asDateTimes(ZoneOffset offset);

    /**
     * Returns the pearson's correlation between the receiver and the otherColumn
     **/
    default double pearsons(NumberColumn otherColumn) {

        double[] x = asDoubleArray();
        double[] y = otherColumn.asDoubleArray();

        return new PearsonsCorrelation().correlation(x, y);
    }

    /**
     * Returns the Spearman's Rank correlation between the receiver and the otherColumn
     * @param otherColumn  A NumberColumn with no missing values
     * @throws NotANumberException if either column contains any missing values
     *
     **/
    default double spearmans(NumberColumn otherColumn) {

        double[] x = asDoubleArray();
        double[] y = otherColumn.asDoubleArray();

        return new SpearmansCorrelation().correlation(x, y);
    }

    /**
     * Returns the Kendall's Tau Rank correlation between the receiver and the otherColumn
     **/
    default double kendalls(NumberColumn otherColumn) {

        double[] x = asDoubleArray();
        double[] y = otherColumn.asDoubleArray();

        return new KendallsCorrelation().correlation(x, y);
    }

    /**
     * Returns the number of unique values in this column, excluding missing values
     */
    @Override
    default int countUnique() {
        DoubleSet doubles = new DoubleOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!NumberColumn.valueIsMissing(getDouble(i))) {
                doubles.add(getDouble(i));
            }
        }
        return doubles.size();
    }

    default Selection isMissing() {
        return eval(isMissing);
    }

    default Selection isNotMissing() {
        return eval(isNotMissing);
    }

}
