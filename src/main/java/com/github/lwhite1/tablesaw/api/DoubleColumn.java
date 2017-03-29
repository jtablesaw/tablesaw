package com.github.lwhite1.tablesaw.api;

import com.github.lwhite1.tablesaw.columns.AbstractColumn;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.filtering.DoubleBiPredicate;
import com.github.lwhite1.tablesaw.filtering.DoublePredicate;
import com.github.lwhite1.tablesaw.io.TypeUtils;
import com.github.lwhite1.tablesaw.reducing.NumericReduceUtils;
import com.github.lwhite1.tablesaw.store.ColumnMetadata;
import com.github.lwhite1.tablesaw.util.BitmapBackedSelection;
import com.github.lwhite1.tablesaw.util.Selection;
import com.github.lwhite1.tablesaw.util.Stats;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleIterable;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleOpenHashSet;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.ints.IntComparator;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.lwhite1.tablesaw.columns.DoubleColumnUtils.*;
import static com.github.lwhite1.tablesaw.reducing.NumericReduceUtils.*;

/**
 * A column in a base table that contains double precision floating point values
 */
public class DoubleColumn extends AbstractColumn implements DoubleIterable, NumericColumn {

    public static final double MISSING_VALUE = (double) ColumnType.DOUBLE.getMissingValue();
    private static final int BYTE_SIZE = 8;

    private static int DEFAULT_ARRAY_SIZE = 128;

    private DoubleArrayList data;

    public DoubleColumn(String name) {
        super(name);
        data = new DoubleArrayList(DEFAULT_ARRAY_SIZE);
    }

    public DoubleColumn(String name, int initialSize) {
        super(name);
        data = new DoubleArrayList(initialSize);
    }

    public DoubleColumn(ColumnMetadata metadata) {
        super(metadata);
        data = new DoubleArrayList(metadata.getSize());
    }

    public int size() {
        return data.size();
    }

    @Override
    public Table summary() {
        return stats().asTable();
    }

    public Stats stats() {
        return Stats.create(this);
    }

    @Override
    public int countUnique() {
        DoubleSet doubles = new DoubleOpenHashSet();
        for (int i = 0; i < size(); i++) {
            doubles.add(data.getDouble(i));
        }
        return doubles.size();
    }

    /**
     * Returns the largest ("top") n values in the column
     *
     * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
     *          number of observations in the column
     * @return A list, possibly empty, of the largest observations
     */
    public DoubleArrayList top(int n) {
        DoubleArrayList top = new DoubleArrayList();
        double[] values = data.toDoubleArray();
        DoubleArrays.parallelQuickSort(values, reverseDoubleComparator);
        for (int i = 0; i < n && i < values.length; i++) {
            top.add(values[i]);
        }
        return top;
    }

    /**
     * Returns the smallest ("bottom") n values in the column
     *
     * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
     *          number of observations in the column
     * @return A list, possibly empty, of the smallest n observations
     */
    public DoubleArrayList bottom(int n) {
        DoubleArrayList bottom = new DoubleArrayList();
        double[] values = data.toDoubleArray();
        DoubleArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(values[i]);
        }
        return bottom;
    }

    @Override
    public DoubleColumn unique() {
        DoubleSet doubles = new DoubleOpenHashSet();
        for (int i = 0; i < size(); i++) {
            doubles.add(data.getDouble(i));
        }
        DoubleColumn column = new DoubleColumn(name() + " Unique values", doubles.size());
        doubles.forEach(column::add);
        return column;
    }

    public DoubleArrayList data() {
        return data;
    }

    @Override
    public ColumnType type() {
        return ColumnType.DOUBLE;
    }

    public double firstElement() {
        if (size() > 0) {
            return data.getDouble(0);
        }
        return MISSING_VALUE;
    }

    // Reduce functions applied to the whole column
    public double sum() {
        return sum.reduce(this);
    }

    public double product() {
        return product.reduce(this);
    }

    public double mean() {
        return mean.reduce(this);
    }

    public double median() {
        return median.reduce(this);
    }

    public double quartile1() {
        return quartile1.reduce(this);
    }

    public double quartile3() {
        return quartile3.reduce(this);
    }

    public double percentile(double percentile) {
        return NumericReduceUtils.percentile(this.toDoubleArray(), percentile);
    }

    public double range() {
        return range.reduce(this);
    }

    public double max() {
        return max.reduce(this);
    }

    public double min() {
        return min.reduce(this);
    }

    public double variance() {
        return variance.reduce(this);
    }

    public double populationVariance() {
        return populationVariance.reduce(this);
    }

    public double standardDeviation() {
        return stdDev.reduce(this);
    }

    public double sumOfLogs() {
        return sumOfLogs.reduce(this);
    }

    public double sumOfSquares() {
        return sumOfSquares.reduce(this);
    }

    public double geometricMean() {
        return geometricMean.reduce(this);
    }

    /**
     * Returns the quadraticMean, aka the root-mean-square, for all values in this column
     */
    public double quadraticMean() {
        return quadraticMean.reduce(this);
    }

    public double kurtosis() {
        return kurtosis.reduce(this);
    }

    public double skewness() {
        return skewness.reduce(this);
    }

    /**
     * Adds the given float to this column
     */
    public void add(float f) {
        data.add(f);
    }

    /**
     * Adds the given double to this column
     */
    public void add(double d) {
        data.add(d);
    }

    // Predicate  functions

    public Selection isLessThan(double f) {
        return select(isLessThan, f);
    }

    public Selection isMissing() {
        return select(isMissing);
    }

    public Selection isNotMissing() {
        return select(isNotMissing);
    }

    public Selection isGreaterThan(double f) {
        return select(isGreaterThan, f);
    }

    public Selection isGreaterThanOrEqualTo(double f) {
        return select(isGreaterThanOrEqualTo, f);
    }

    public Selection isLessThanOrEqualTo(double f) {
        return select(isLessThanOrEqualTo, f);
    }

    public Selection isEqualTo(double d) {
        return select(isEqualTo, d);
    }

    public Selection isEqualTo(DoubleColumn d) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        DoubleIterator doubleIterator = d.iterator();
        for (double doubles : data) {
            if (doubles == doubleIterator.nextDouble()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    @Override
    public String getString(int row) {
        return String.valueOf(data.getDouble(row));
    }

    @Override
    public DoubleColumn emptyCopy() {
        DoubleColumn column = new DoubleColumn(name());
        column.setComment(comment());
        return column;
    }

    @Override
    public DoubleColumn emptyCopy(int rowSize) {
        DoubleColumn column = new DoubleColumn(name(), rowSize);
        column.setComment(comment());
        return column;
    }

    @Override
    public void clear() {
        data = new DoubleArrayList(DEFAULT_ARRAY_SIZE);
    }

    @Override
    public DoubleColumn copy() {
        DoubleColumn column = DoubleColumn.create(name(), data);
        column.setComment(comment());
        return column;
    }

    @Override
    public void sortAscending() {
        Arrays.parallelSort(data.elements());
    }

    @Override
    public void sortDescending() {
        DoubleArrays.parallelQuickSort(data.elements(), reverseDoubleComparator);
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    public static DoubleColumn create(String name) {
        return new DoubleColumn(name);
    }

    public static DoubleColumn create(String name, int initialSize) {
        return new DoubleColumn(name, initialSize);
    }

    public static DoubleColumn create(String name, DoubleArrayList doubles) {
        DoubleColumn column = new DoubleColumn(name, doubles.size());
        column.data = new DoubleArrayList(doubles.size());
        column.data.addAll(doubles);
        return column;
    }

    /**
     * Compares two doubles, such that a sort based on this comparator would sort in descending order
     */
    DoubleComparator reverseDoubleComparator = new DoubleComparator() {

        @Override
        public int compare(Double o2, Double o1) {
            return (o1 < o2 ? -1 : (o1.equals(o2) ? 0 : 1));
        }

        @Override
        public int compare(double o2, double o1) {
            return (o1 < o2 ? -1 : (o1 == o2 ? 0 : 1));
        }
    };

    /**
     * Returns the count of missing values in this column
     * <p>
     * Implementation note: We use NaN for missing, so we can't compare against the MISSING_VALUE and use val != val
     * instead
     */
    @Override
    public int countMissing() {
        int count = 0;
        for (int i = 0; i < size(); i++) {
            double f = get(i);
            if (f != f) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void addCell(String object) {
        try {
            add(convert(object));
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException(name() + ": " + nfe.getMessage());
        } catch (NullPointerException e) {
            throw new RuntimeException(name() + ": "
                    + String.valueOf(object) + ": "
                    + e.getMessage());
        }
    }

    /**
     * Returns a double that is parsed from the given String
     * <p>
     * We remove any commas before parsing
     */
    public static double convert(String stringValue) {
        if (Strings.isNullOrEmpty(stringValue) || TypeUtils.MISSING_INDICATORS.contains(stringValue)) {
            return MISSING_VALUE;
        }
        Matcher matcher = COMMA_PATTERN.matcher(stringValue);
        return Double.parseDouble(matcher.replaceAll(""));
    }

    /**
     * Returns the natural log of the values in this column as a new DoubleColumn
     */
    public DoubleColumn logN() {
        DoubleColumn newColumn = DoubleColumn.create(name() + "[logN]", size());

        for (double value : this) {
            newColumn.add(Math.log(value));
        }
        return newColumn;
    }

    /**
     * Returns the base 10 log of the values in this column as a new DoubleColumn
     *
     * @return
     */
    public DoubleColumn log10() {
        DoubleColumn newColumn = DoubleColumn.create(name() + "[log10]", size());

        for (double value : this) {
            newColumn.add(Math.log10(value));
        }
        return newColumn;
    }

    /**
     * Returns the natural log of the values in this column, after adding 1 to each so that zero
     * values don't return -Infinity
     */
    public DoubleColumn log1p() {
        DoubleColumn newColumn = DoubleColumn.create(name() + "[1og1p]", size());
        for (double value : this) {
            newColumn.add(Math.log1p(value));
        }
        return newColumn;
    }

    public DoubleColumn round() {
        DoubleColumn newColumn = DoubleColumn.create(name() + "[rounded]", size());
        for (double value : this) {
            newColumn.add(Math.round(value));
        }
        return newColumn;
    }

    /**
     * Returns a doubleColumn with the absolute value of each value in this column
     */
    public DoubleColumn abs() {
        DoubleColumn newColumn = DoubleColumn.create(name() + "[abs]", size());
        for (double value : this) {
            newColumn.add(Math.abs(value));
        }
        return newColumn;
    }

    /**
     * Returns a doubleColumn with the square of each value in this column
     */
    public DoubleColumn square() {
        DoubleColumn newColumn = DoubleColumn.create(name() + "[sq]", size());
        for (double value : this) {
            newColumn.add(value * value);
        }
        return newColumn;
    }

    public DoubleColumn sqrt() {
        DoubleColumn newColumn = DoubleColumn.create(name() + "[sqrt]", size());
        for (double value : this) {
            newColumn.add(Math.sqrt(value));
        }
        return newColumn;
    }

    public DoubleColumn cubeRoot() {
        DoubleColumn newColumn = DoubleColumn.create(name() + "[cbrt]", size());
        for (double value : this) {
            newColumn.add(Math.cbrt(value));
        }
        return newColumn;
    }

    public DoubleColumn cube() {
        DoubleColumn newColumn = DoubleColumn.create(name() + "[cb]", size());
        for (double value : this) {
            newColumn.add(value * value * value);
        }
        return newColumn;
    }

    public DoubleColumn remainder(DoubleColumn column2) {
        DoubleColumn result = DoubleColumn.create(name() + " % " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.add(get(r) % column2.get(r));
        }
        return result;
    }

    public DoubleColumn add(DoubleColumn column2) {
        DoubleColumn result = DoubleColumn.create(name() + " + " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.add(get(r) + column2.get(r));
        }
        return result;
    }

    public DoubleColumn subtract(DoubleColumn column2) {
        DoubleColumn result = DoubleColumn.create(name() + " - " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.add(get(r) - column2.get(r));
        }
        return result;
    }

    public DoubleColumn multiply(DoubleColumn column2) {
        DoubleColumn result = DoubleColumn.create(name() + " * " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.add(get(r) * column2.get(r));
        }
        return result;
    }

    public DoubleColumn multiply(IntColumn column2) {
        DoubleColumn result = DoubleColumn.create(name() + " * " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.add(get(r) * column2.get(r));
        }
        return result;
    }

    public DoubleColumn multiply(LongColumn column2) {
        DoubleColumn result = DoubleColumn.create(name() + " * " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.add(get(r) * column2.get(r));
        }
        return result;
    }

    public DoubleColumn multiply(ShortColumn column2) {
        DoubleColumn result = DoubleColumn.create(name() + " * " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.add(get(r) * column2.get(r));
        }
        return result;
    }

    public DoubleColumn divide(DoubleColumn column2) {
        DoubleColumn result = DoubleColumn.create(name() + " / " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.add(get(r) / column2.get(r));
        }
        return result;
    }

    public DoubleColumn divide(IntColumn column2) {
        DoubleColumn result = DoubleColumn.create(name() + " / " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.add(get(r) / column2.get(r));
        }
        return result;
    }

    public DoubleColumn divide(LongColumn column2) {
        DoubleColumn result = DoubleColumn.create(name() + " / " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.add(get(r) / column2.get(r));
        }
        return result;
    }

    public DoubleColumn divide(ShortColumn column2) {
        DoubleColumn result = DoubleColumn.create(name() + " / " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.add(get(r) / column2.get(r));
        }
        return result;
    }

    /**
     * For each item in the column, returns the same number with the sign changed.
     * For example:
     * -1.3   returns  1.3,
     * 2.135 returns -2.135
     * 0     returns  0
     */
    public DoubleColumn neg() {
        DoubleColumn newColumn = DoubleColumn.create(name() + "[neg]", size());
        for (double value : this) {
            newColumn.add(value * -1);
        }
        return newColumn;
    }

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    /**
     * Compares the given ints, which refer to the indexes of the doubles in this column, according to the values of the
     * doubles themselves
     */
    @Override
    public IntComparator rowComparator() {
        return comparator;
    }

    private final IntComparator comparator = new IntComparator() {

        @Override
        public int compare(Integer r1, Integer r2) {
            double f1 = data.getDouble(r1);
            double f2 = data.getDouble(r2);
            return Double.compare(f1, f2);
        }

        public int compare(int r1, int r2) {
            double f1 = data.getDouble(r1);
            double f2 = data.getDouble(r2);
            return Double.compare(f1, f2);
        }
    };

    public double get(int index) {
        return data.getDouble(index);
    }

    @Override
    public float getFloat(int index) {
        return (float) data.getDouble(index);
    }

    public double getDouble(int index) {
        return data.getDouble(index);
    }

    public void set(int r, float value) {
        data.set(r, value);
    }

    // TODO(lwhite): Reconsider the implementation of this functionality to allow user to provide a specific max error.
    // TODO(lwhite): continued: Also see section in Effective Java on doubleing point comparisons.
    Selection isCloseTo(float target) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        for (double f : data) {
            if (Double.compare(f, target) == 0) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    Selection isCloseTo(double target) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        for (double f : data) {
            if (Double.compare(f, 0.0) == 0) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    Selection isPositive() {
        return select(isPositive);
    }

    Selection isNegative() {
        return select(isNegative);
    }

    Selection isNonNegative() {
        return select(isNonNegative);
    }

    public double[] toDoubleArray() {
        double[] output = new double[data.size()];
        for (int i = 0; i < data.size(); i++) {
            output[i] = data.getDouble(i);
        }
        return output;
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(title());
        for (double aData : data) {
            builder.append(String.valueOf(aData));
            builder.append('\n');
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "Double column: " + name();
    }

    @Override
    public void append(Column column) {
        Preconditions.checkArgument(column.type() == this.type());
        DoubleColumn doubleColumn = (DoubleColumn) column;
        for (int i = 0; i < doubleColumn.size(); i++) {
            add(doubleColumn.get(i));
        }
    }

    @Override
    public DoubleIterator iterator() {
        return data.iterator();
    }

    public Selection select(DoublePredicate predicate) {
        Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            double next = data.getDouble(idx);
            if (predicate.test(next)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }

    public Selection select(DoubleBiPredicate predicate, double value) {
        Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            double next = data.getDouble(idx);
            if (predicate.test(next, value)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }

    DoubleSet asSet() {
        return new DoubleOpenHashSet(data);
    }

    public boolean contains(double value) {
        return data.contains(value);
    }

    @Override
    public int byteSize() {
        return BYTE_SIZE;
    }

    /**
     * Returns the contents of the cell at rowNumber as a byte[]
     */
    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(BYTE_SIZE).putDouble(get(rowNumber)).array();
    }

    @Override
    public DoubleColumn difference() {
        DoubleColumn returnValue = new DoubleColumn(this.name(), this.size());
        returnValue.add(DoubleColumn.MISSING_VALUE);
        for (int current = 0; current < this.size(); current++) {
            if (current + 1 < this.size()) {

            /*
             * check for missing values:
             * note that for doubles you test val != val,
             * since a missing double is encoded as Double.NaN and nothing is equal to NaN.
             */

                double currentValue = get(current);
                double nextValue = get(current + 1);

                if (currentValue != currentValue || nextValue != nextValue) {
                    returnValue.add(Double.NaN);
                } else {
                    returnValue.add(nextValue - currentValue);
                }
            }
        }
        return returnValue;
    }
}
