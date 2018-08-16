package tech.tablesaw.api;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleOpenHashSet;
import it.unimi.dsi.fastutil.doubles.DoubleRBTreeSet;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.aggregate.NumericAggregateFunction;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.DoubleDataWrapper;
import tech.tablesaw.columns.numbers.IntDataWrapper;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.columns.numbers.NumberFillers;
import tech.tablesaw.columns.numbers.NumberFilters;
import tech.tablesaw.columns.numbers.NumberIterable;
import tech.tablesaw.columns.numbers.NumberIterator;
import tech.tablesaw.columns.numbers.NumberMapFunctions;
import tech.tablesaw.columns.numbers.NumericDataWrapper;
import tech.tablesaw.columns.numbers.Stats;
import tech.tablesaw.filtering.predicates.DoubleBiPredicate;
import tech.tablesaw.filtering.predicates.DoubleRangePredicate;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.ToDoubleFunction;

import static tech.tablesaw.aggregate.AggregateFunctions.*;
import static tech.tablesaw.api.ColumnType.DOUBLE;
import static tech.tablesaw.columns.numbers.NumberPredicates.*;


public class NumberColumn extends AbstractColumn<Double> implements NumberMapFunctions, NumberFilters, NumberFillers<NumberColumn>, CategoricalColumn<Double> {

    public static double MISSING_VALUE = (Double) DOUBLE.getMissingValue();

    private NumericDataWrapper data;

    private NumberColumnFormatter printFormatter = new NumberColumnFormatter();

    private Locale locale;

    public static boolean valueIsMissing(double value) {
        return Double.isNaN(value);
    }

    public static boolean valueIsMissing(float value) {
        return Float.isNaN(value);
    }

    private final IntComparator comparator = new IntComparator() {

        @Override
        public int compare(final int r1, final int r2) {
            final double f1 = getDouble(r1);
            final double f2 = getDouble(r2);
            return Double.compare(f1, f2);
        }
    };

    /**
     * Returns a new numeric column initialized with the given name and size. The values in the column are
     * integers beginning at startsWith and continuing through size (exclusive), monotonically increasing by 1
     * TODO consider a generic fill function including steps or random samples from various distributions
     */
    public static NumberColumn indexColumn(final String columnName, final int size, final int startsWith) {
        final NumberColumn indexColumn = NumberColumn.createWithIntegers(columnName, size);
        for (int i = 0; i < size; i++) {
            indexColumn.append(i + startsWith);
        }
        indexColumn.setPrintFormatter(NumberColumnFormatter.ints());
        return indexColumn;
    }

    public static NumberColumn create(final String name, final double[] arr) {
        return new NumberColumn(name, new DoubleArrayList(arr));
    }

    public static NumberColumn create(final String name, final NumericDataWrapper data) {
        return new NumberColumn(name, data);
    }

    public static NumberColumn create(final String name) {
        return new NumberColumn(name, new DoubleArrayList());
    }

    public static NumberColumn create(final String name, final float[] arr) {
        final double[] doubles = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            doubles[i] = arr[i];
        }
        return new NumberColumn(name, new DoubleArrayList(doubles));
    }

    public static NumberColumn create(final String name, final int[] arr) {
        final double[] doubles = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            doubles[i] = arr[i];
        }
        return new NumberColumn(name, new DoubleArrayList(doubles));
    }

    public static NumberColumn create(final String name, final long[] arr) {
        final double[] doubles = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            doubles[i] = arr[i];
        }
        return new NumberColumn(name, new DoubleArrayList(doubles));
    }

    public static NumberColumn create(final String name, final List<Number> numberList) {
        final double[] doubles = new double[numberList.size()];
        for (int i = 0; i < numberList.size(); i++) {
            doubles[i] = numberList.get(i).doubleValue();
        }
        return new NumberColumn(name, new DoubleArrayList(doubles));
    }

    public static NumberColumn create(final String name, final Number[] numbers) {
        final double[] doubles = new double[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            doubles[i] = numbers[i].doubleValue();
        }
        return new NumberColumn(name, new DoubleArrayList(doubles));
    }

    public static NumberColumn createWithIntegers(String name) {
        return new NumberColumn(name, new IntArrayList(DEFAULT_ARRAY_SIZE));
    }

    public static NumberColumn createWithIntegers(String name, int size) {
        return new NumberColumn(name, new IntArrayList(size));
    }

    public static NumberColumn create(final String name, final int initialSize) {
        return new NumberColumn(name, new DoubleArrayList(initialSize));
    }

    private NumberColumn(final String name, final DoubleArrayList data) {
        super(DOUBLE, name);
        setDataWrapper(new DoubleDataWrapper(data));
    }

    private void setDataWrapper(NumericDataWrapper wrapper) {
        if (wrapper instanceof IntDataWrapper) {
            printFormatter = NumberColumnFormatter.ints();
        }
        this.data = wrapper;
    }

    private NumberColumn(final String name, final NumericDataWrapper data) {
        super(DOUBLE, name);
        setDataWrapper(data);
    }

    @Override
    public boolean isMissing(final int rowNumber) {
        return NumberColumn.valueIsMissing(getDouble(rowNumber));
    }

    public void setPrintFormatter(final NumberFormat format, final String missingValueString) {
        this.printFormatter = new NumberColumnFormatter(format, missingValueString);
    }


    public void setPrintFormatter(final NumberColumnFormatter formatter) {
        this.printFormatter = formatter;
    }

    @Override
    public int size() {
        return data.size();
    }

    public Table summary() {
        return stats().asTable();
    }

    public Stats stats() {
        return Stats.create(this);
    }

    /**
     * Returns the largest ("top") n values in the column
     * TODO(lwhite): Consider whether this should exclude missing
     *
     * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
     *          number of observations in the column
     * @return A list, possibly empty, of the largest observations
     */
    public NumberColumn top(final int n) {
//            final int[] values = intData.toIntArray();
//            IntArrays.parallelQuickSort(values, descendingIntComparator);
//            for (int i = 0; i < n && i < values.length; i++) {
//                top.add(values[i]);
//            }
        return NumberColumn.create(name() + "[Top " + n  + "]", data.top(n));

    }

    /**
     * Returns the smallest ("bottom") n values in the column
     * TODO(lwhite): Consider whether this should exclude missing
     *
     * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
     *          number of observations in the column
     * @return A list, possibly empty, of the smallest n observations
     */
    public NumberColumn bottom(final int n) {
        return NumberColumn.create(name() + "[Bottoms " + n  + "]", data.bottom(n));
    }

    /**
     *
     */
    @Override
    public NumberColumn unique() {
        final DoubleSet doubles = new DoubleOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissing(i)) {
                doubles.add(getDouble(i));
            }
        }
        final NumberColumn column = NumberColumn.create(name() + " Unique values", doubles.size());
        doubles.forEach((DoubleConsumer) column::append);
        return column;
    }

    public double firstElement() {
        if (size() > 0) {
            return getDouble(0);
        }
        return MISSING_VALUE;
    }

    /**
     * Adds the given float to this column
     */
    public NumberColumn append(final float f) {
        data.append(f);
        return this;
    }


    /**
     * Adds the given double to this column
     */
    public NumberColumn append(double d) {
        data.append(d);
        return this;
    }

    public NumberColumn append(int i) {
        data.append(i);
        return this;
    }

    @Override
    public NumberColumn append(Double val) {
        this.append(val.doubleValue());
        return this;
    }

    public NumberColumn append(Integer val) {
        this.append(val.doubleValue());
        return this;
    }

    private NumberColumn(final String name, IntArrayList data) {
        super(DOUBLE, name);
        this.printFormatter = NumberColumnFormatter.ints();
        setDataWrapper(new IntDataWrapper(data));
    }

    @Override
    public String getString(final int row) {
        final double value = getDouble(row);
        if (NumberColumn.valueIsMissing(value)) {
            return "";
        }
        return String.valueOf(printFormatter.format(value));
    }

    @Override
    public double getDouble(final int row) {
        return data.getDouble(row);
    }

    @Override
    public String getUnformattedString(final int row) {
        return String.valueOf(getDouble(row));
    }

    @Override
    public NumberColumn emptyCopy() {
        return emptyCopy(DEFAULT_ARRAY_SIZE);
    }

    @Override
    public NumberColumn emptyCopy(final int rowSize) {
        final NumberColumn column = NumberColumn.create(name(), rowSize);
        column.setPrintFormatter(printFormatter);
        column.locale = locale;
        return column;
    }

    @Override
    public NumberColumn copy() {
        return NumberColumn.create(name(), data.copy());
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public void sortAscending() {
        data.sortAscending();
    }

    @Override
    public void sortDescending() {
        data.sortDescending();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public NumberColumn appendCell(final String object) {
        try {
            append(DoubleColumnType.DEFAULT_PARSER.parseDouble(object));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException(name() + ": " + e.getMessage());
        }
        return this;
    }

    @Override
    public NumberColumn appendCell(final String object, StringParser parser) {
        try {
            append(parser.parseDouble(object));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException(name() + ": " + e.getMessage());
        }
        return this;
    }

    /**
     * Returns the rounded value as an int
     *
     * @throws ClassCastException if the returned value will not fit in an int
     */
    public Integer roundInt(final int i) {
        final double value = getDouble(i);
        if (NumberColumn.valueIsMissing(value)) {
            return null;
        }
        return (int) Math.round(getDouble(i));
    }

    /**
     * Returns the value of the ith element rounded to the nearest long
     *
     * @param i the index in the column
     * @return the value at i, rounded to the nearest integer
     */
    public long getLong(final int i) {
        final double value = getDouble(i);
        return NumberColumn.valueIsMissing(value) ? DateTimeColumn.MISSING_VALUE : Math.round(value);
    }


    public Double summarizeIf(Selection selection, NumericAggregateFunction function) {
        NumberColumn column = where(selection);
        return function.summarize(column);
    }

    /**
     * Compares the given ints, which refer to the indexes of the doubles in this column, according to the values of the
     * doubles themselves
     */
    @Override
    public IntComparator rowComparator() {
        return comparator;
    }

    public NumberColumn set(final int r, final double value) {
        data.set(r, value);
        return this;
    }


    /**
     * Conditionally update this column, replacing current values with newValue for all rows where the current value
     * matches the selection criteria
     * <p>
     * Example:
     * myColumn.set(4.0, myColumn.valueIsMissing()); // no more missing values
     */
    public NumberColumn set(final Selection rowSelection, final double newValue) {
        for (final int row : rowSelection) {
            set(row, newValue);
        }
        return this;
    }

    @Override
    public double[] asDoubleArray() {
        final double[] output = new double[size()];
        for (int i = 0; i < size(); i++) {
            output[i] = getDouble(i);
        }
        return output;
    }

    @Override
    public NumberColumn where(final Selection selection) {
        return (NumberColumn) subset(selection);
    }

    @Override
    public Selection eval(final DoublePredicate predicate) {
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
    public Selection eval(final DoubleBiPredicate predicate, final NumberColumn otherColumn) {
        final Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            if (predicate.test(getDouble(idx), otherColumn.getDouble(idx))) {
                selection.add(idx);
            }
        }
        return selection;
    }

    @Override
    public Selection eval(final DoubleBiPredicate predicate, final Number number) {
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
    public Selection eval(final BiPredicate<Number, Number> predicate, final Number number) {
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
    public Selection eval(final DoubleRangePredicate predicate, final Number rangeStart, final Number rangeEnd) {
        final double start = rangeStart.doubleValue();
        final double end = rangeEnd.doubleValue();
        final Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            final double next = getDouble(idx);
            if (predicate.test(next, start, end)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }

    @Override
    public Selection isIn(final Number... numbers) {
        return isIn(Arrays.stream(numbers).mapToDouble(Number::doubleValue).toArray());
    }

    @Override
    public Selection isIn(final double... doubles) {
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
    public Selection isNotIn(final Number... numbers) {
        final Selection results = new BitmapBackedSelection();
        results.addRange(0, size());
        results.andNot(isIn(numbers));
        return results;
    }

    @Override
    public Selection isNotIn(final double... doubles) {
        final Selection results = new BitmapBackedSelection();
        results.addRange(0, size());
        results.andNot(isIn(doubles));
        return results;
    }

    public boolean contains(final double value) {
        return data.contains(value);
    }

    public boolean contains(final int value) {
        return data.contains(value);
    }

    @Override
    public int byteSize() {
        return type().byteSize();
    }

    /**
     * Returns the contents of the cell at rowNumber as a byte[]
     */
    @Override
    public byte[] asBytes(final int rowNumber) {
        return ByteBuffer.allocate(byteSize()).putDouble(getDouble(rowNumber)).array();
    }

    @Override
    public int[] asIntArray() {  // TODO: Need to figure out how to handle NaN -> Maybe just use a list with nulls?
        final int[] result = new int[size()];
        for (int i = 0; i < size(); i++) {
            result[i] = roundInt(i);
        }
        return result;
    }

    @Override
    public DoubleList dataInternal() {
        return data.copy().dataInternal();
    }

    @Override
    public NumberColumn appendMissing() {
        append(MISSING_VALUE);
        return this;
    }

    /**
     * Returns the count of missing values in this column
     */
    @Override
    public int countMissing() {
        int count = 0;
        for (int i = 0; i < size(); i++) {
            if (NumberColumn.valueIsMissing(getDouble(i))) {
                count++;
            }
        }
        return count;
    }

    // Reduce functions applied to the whole column
    public double sum() {
        return sum.summarize(this);
    }

    public double product() {
        return product.summarize(this);
    }

    public double mean() {
        return mean.summarize(this);
    }

    public double median() {
        return median.summarize(this);
    }

    public double quartile1() {
        return quartile1.summarize(this);
    }

    public double quartile3() {
        return quartile3.summarize(this);
    }

    public double percentile(double percentile) {
        return AggregateFunctions.percentile(this, percentile);
    }

    public double range() {
        return range.summarize(this);
    }

    public double max() {
        return max.summarize(this);
    }

    public double min() {
        return min.summarize(this);
    }

    public double variance() {
        return variance.summarize(this);
    }

    public double populationVariance() {
        return populationVariance.summarize(this);
    }

    public double standardDeviation() {
        return stdDev.summarize(this);
    }

    public double sumOfLogs() {
        return sumOfLogs.summarize(this);
    }

    public double sumOfSquares() {
        return sumOfSquares.summarize(this);
    }

    public double geometricMean() {
        return geometricMean.summarize(this);
    }

    /**
     * Returns the quadraticMean, aka the root-mean-square, for all values in this column
     */
    public double quadraticMean() {
        return quadraticMean.summarize(this);
    }

    public double kurtosis() {
        return kurtosis.summarize(this);
    }

    public double skewness() {
        return skewness.summarize(this);
    }

    /**
     * Returns a DateTimeColumn where each value is the LocalDateTime represented by the values in this column
     * <p>
     * The values in this column must be longs that represent the time in milliseconds from the epoch as in standard
     * Java date/time calculations
     *
     * @param offset The ZoneOffset to use in the calculation
     * @return A column of LocalDateTime values
     */
    public DateTimeColumn asDateTimes(ZoneOffset offset) {
        DateTimeColumn column = DateTimeColumn.create(name() + ": date time");
        NumberIterator it = numberIterator();
        while (it.hasNext()) {
            double d = it.next();
            LocalDateTime dateTime =
                    Instant.ofEpochMilli((long) d).atZone(offset).toLocalDateTime();
            column.append(dateTime);
        }
        return column;
    }

    /**
     * Returns the pearson's correlation between the receiver and the otherColumn
     **/
    public double pearsons(NumberColumn otherColumn) {

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
    public double spearmans(NumberColumn otherColumn) {

        double[] x = asDoubleArray();
        double[] y = otherColumn.asDoubleArray();

        return new SpearmansCorrelation().correlation(x, y);
    }

    /**
     * Returns the Kendall's Tau Rank correlation between the receiver and the otherColumn
     **/
    public double kendalls(NumberColumn otherColumn) {

        double[] x = asDoubleArray();
        double[] y = otherColumn.asDoubleArray();

        return new KendallsCorrelation().correlation(x, y);
    }

    /**
     * Returns the number of unique values in this column, excluding missing values
     */
    @Override
    public int countUnique() {
        DoubleSet doubles = new DoubleOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!NumberColumn.valueIsMissing(getDouble(i))) {
                doubles.add(getDouble(i));
            }
        }
        return doubles.size();
    }

    public Selection isMissing() {
        return eval(isMissing);
    }

    public Selection isNotMissing() {
        return eval(isNotMissing);
    }

    @Override
    public NumberColumn appendObj(Object obj) {
        if (!(obj instanceof Double)) {
            throw new IllegalArgumentException();
        }
        return append((double) obj);
    }

    /**
     * Counts the number of rows satisfying predicate, but only upto the max value
     *
     * @param test the predicate
     * @param max  the maximum number of rows to count
     * @return the number of rows satisfying the predicate
     */
    public int count(DoublePredicate test, int max) {
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
     * Returns a new NumberColumn with only those rows satisfying the predicate
     *
     * @param test the predicate
     * @return a new NumberColumn with only those rows satisfying the predicate
     */
    public NumberColumn filter(DoublePredicate test) {
        NumberColumn result = NumberColumn.create(name());
        for (int i = 0; i < size(); i++) {
            double d = getDouble(i);
            if (test.test(d)) {
                result.append(d);
            }
        }
        return result;
    }

    /**
     * Maps the function across all rows, appending the results to the provided Column
     *
     * @param fun  function to map
     * @param into Column to which results are appended
     * @return the provided Column, to which results are appended
     */
    public <R> Column<R> mapInto(DoubleFunction<? extends R> fun, Column<R> into) {
        for (int i = 0; i < size(); i++) {
            try {
                into.append(fun.apply(getDouble(i)));
            } catch (Exception e) {
                into.appendMissing();
            }
        }
        return into;
    }

    /**
     * Maps the function across all rows, appending the results to a new NumberColumn
     *
     * @param fun function to map
     * @return the NumberColumn with the results
     */
    public NumberColumn map(ToDoubleFunction<Double> fun) {
        NumberColumn result = NumberColumn.create(name());
        for (double t : this) {
            try {
                result.append(fun.applyAsDouble(t));
            } catch (Exception e) {
                result.appendMissing();
            }
        }
        return result;
    }

    /**
     * Returns the maximum row according to the provided Comparator
     *
     * @param comp
     * @return the maximum row
     */
    public Optional<Double> max(DoubleComparator comp) {
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
    public Optional<Double> min(DoubleComparator comp) {
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
    public double reduce(double initial, DoubleBinaryOperator op) {
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
    public Optional<Double> reduce(DoubleBinaryOperator op) {
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

    @Override
    public NumberColumn lead(final int n) {
        final NumberColumn numberColumn = lag(-n);
        numberColumn.setName(name() + " lead(" + n + ")");
        return numberColumn;
    }

    @Override
    public NumberColumn lag(final int n) {
        return NumberColumn.create(name() + " lag(" + n + ")", data.lag(n));
    }

    @Override
    public NumberColumn removeMissing() {
        return new NumberColumn(name(), data.removeMissing());
    }

    @Override
    public NumberIterator numberIterator() {
        return data.numberIterator();
    }

    @Override
    public Iterator<Double> iterator() {
        return data.iterator();
    }

    public IntSet asIntegerSet() {
        final IntSet ints = new IntOpenHashSet();
        NumberIterator it = numberIterator();
        while (it.hasNext()) {
            double d = it.next();
            if (!NumberColumn.valueIsMissing(d)) {
                ints.add((int) Math.round(d));
            }
        }
        return ints;
    }

    @Override
    public Object[] asObjectArray() {
        final Double[] output = new Double[size()];
        for (int i = 0; i < size(); i++) {
            output[i] = getDouble(i);
        }
        return output;
    }

    @Override
    public int compare(Double o1, Double o2) {
        return Double.compare(o1, o2);
    }


    /**
     * Counts the number of rows satisfying predicate
     *
     * @param test the predicate
     * @return the number of rows satisfying the predicate
     */
    public int count(DoublePredicate test) {
        return count(test, size());
    }

    /**
     * Returns true if all rows satisfy the predicate, false otherwise
     *
     * @param test the predicate
     * @return true if all rows satisfy the predicate, false otherwise
     */
    public boolean allMatch(DoublePredicate test) {
        return count(test.negate(), 1) == 0;
    }

    /**
     * Returns true if any row satisfies the predicate, false otherwise
     *
     * @param test the predicate
     * @return true if any rows satisfies the predicate, false otherwise
     */
    public boolean anyMatch(DoublePredicate test) {
        return count(test, 1) > 0;
    }

    /**
     * Returns true if no row satisfies the predicate, false otherwise
     *
     * @param test the predicate
     * @return true if no row satisfies the predicate, false otherwise
     */
    public boolean noneMatch(DoublePredicate test) {
        return count(test, 1) == 0;
    }


    @Override
    public Double get(final int index) {
        return getDouble(index);
    }

    @Override
    public NumberColumn set(int i, Double val) {
        return set(i, (double) val);
    }

    @Override
    public NumberColumn append(final Column<Double> column) {
        Preconditions.checkArgument(column.type() == this.type());
        final NumberColumn numberColumn = (NumberColumn) column;
        for (int i = 0; i < numberColumn.size(); i++) {
            append(numberColumn.getDouble(i));
        }
        return this;
    }

    // fillWith methods

    @Override
    public NumberColumn fillWith(final NumberIterator iterator) {
        for (int r = 0; r < size(); r++) {
            if (!iterator.hasNext()) {
                break;
            }
            set(r, iterator.next());
        }
        return this;
    }

    @Override
    public NumberColumn fillWith(final NumberIterable iterable) {
        NumberIterator iterator = iterable.numberIterator();
        for (int r = 0; r < size(); r++) {
            if (iterator == null || (!iterator.hasNext())) {
                iterator = numberIterator();
                if (!iterator.hasNext()) {
                    break;
                }
            }
            set(r, iterator.next());
        }
        return this;
    }

    @Override
    public NumberColumn fillWith(final DoubleSupplier supplier) {
        for (int r = 0; r < size(); r++) {
            try {
                set(r, supplier.getAsDouble());
            } catch (final Exception e) {
                break;
            }
        }
        return this;
    }


}
