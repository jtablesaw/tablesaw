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
import static tech.tablesaw.api.ColumnType.NUMBER;

import java.text.NumberFormat;
import java.util.function.DoublePredicate;

import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleIterable;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleOpenHashSet;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntSet;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.columns.numbers.NumberFillers;
import tech.tablesaw.columns.numbers.NumberFilters;
import tech.tablesaw.columns.numbers.NumberMapFunctions;
import tech.tablesaw.columns.numbers.Stats;
import tech.tablesaw.filtering.Filter;
import tech.tablesaw.filtering.predicates.DoubleBiPredicate;
import tech.tablesaw.filtering.predicates.DoubleRangePredicate;
import tech.tablesaw.selection.Selection;

public interface NumberColumn extends DoubleIterable, IntConvertibleColumn<Double>, NumberMapFunctions, NumberFilters, NumberFillers<NumberColumn> {
    double MISSING_VALUE = (Double) NUMBER.getMissingValue();

    static boolean valueIsMissing(final double value) {
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
    Column<Double> unique();

    double firstElement();

    void append(float f);

    void append(double d);

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
    NumberColumn lead(int n);

    @Override
    NumberColumn lag(int n);

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
    void appendCell(String object);

    Integer roundInt(int i);

    long getLong(int i);

    @Override
    IntComparator rowComparator();

    @Override
    Double get(int index);

    void set(int r, double value);

    void set(Selection rowSelection, double newValue);

    @Override
    double[] asDoubleArray();

    @Override
    void append(Column<Double> column);

    @Override
    DoubleIterator iterator();

    @Override
    NumberColumn where(Filter filter);

    @Override
    NumberColumn where(Selection selection);

    @Override
    Selection eval(DoublePredicate predicate);

    @Override
    Selection eval(DoubleBiPredicate predicate, NumberColumn otherColumn);

    @Override
    Selection eval(DoubleBiPredicate predicate, Number value);

    @Override
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
    IntSet asIntegerSet();

    @Override
    DoubleList dataInternal();

    /**
     * Returns the count of missing values in this column
     */
    @Override
    default int countMissing() {
        int count = 0;
        for (int i = 0; i < size(); i++) {
            if (NumberColumn.valueIsMissing(get(i))) {
                count++;
            }
        }
        return count;
    }

    // Reduce functions applied to the whole column
    @Override
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

    default double percentile(final double percentile) {
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
    default double pearsons(final NumberColumn otherColumn) {

        final double[] x = asDoubleArray();
        final double[] y = otherColumn.asDoubleArray();

        return new PearsonsCorrelation().correlation(x, y);
    }

    /**
     * Returns the Spearman's Rank correlation between the receiver and the otherColumn
     * @param otherColumn  A NumberColumn with no missing values
     * @throws NotANumberException if either column contains any missing values
     *
     **/
    default double spearmans(final NumberColumn otherColumn) {

        final double[] x = asDoubleArray();
        final double[] y = otherColumn.asDoubleArray();

        return new SpearmansCorrelation().correlation(x, y);
    }

    /**
     * Returns the Kendall's Tau Rank correlation between the receiver and the otherColumn
     **/
    default double kendalls(final NumberColumn otherColumn) {

        final double[] x = asDoubleArray();
        final double[] y = otherColumn.asDoubleArray();

        return new KendallsCorrelation().correlation(x, y);
    }

    /**
     * Returns the number of unique values in this column, excluding missing values
     */
    @Override
    default int countUnique() {
        final DoubleSet doubles = new DoubleOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!NumberColumn.valueIsMissing(get(i))) {
                doubles.add(get(i));
            }
        }
        return doubles.size();
    }
}
