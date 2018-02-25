/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.api;

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
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.filtering.DoubleBiPredicate;
import tech.tablesaw.filtering.DoublePredicate;
import tech.tablesaw.io.TypeUtils;
import tech.tablesaw.store.ColumnMetadata;
import tech.tablesaw.util.BitmapBackedSelection;
import tech.tablesaw.util.Selection;
import tech.tablesaw.util.Stats;

import static tech.tablesaw.aggregate.AggregateFunctions.*;
import static tech.tablesaw.columns.DoubleColumnUtils.*;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A column in a base table that contains double precision floating point values
 */
public class DoubleColumn extends AbstractColumn implements DoubleIterable, NumericColumn {

    public static final double MISSING_VALUE = (Double) ColumnType.DOUBLE.getMissingValue();
    private static final int BYTE_SIZE = 8;
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    private static int DEFAULT_ARRAY_SIZE = 128;
    /**
     * Compares two doubles, such that a sort based on this comparator would sort in descending order
     */
    private DoubleComparator reverseDoubleComparator = new DoubleComparator() {

        @Override
        public int compare(Double o2, Double o1) {
            return (o1 < o2 ? -1 : (o1.equals(o2) ? 0 : 1));
        }

        @Override
        public int compare(double o2, double o1) {
            return (Double.compare(o1, o2));
        }
    };

    private DoubleArrayList data;
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

    public DoubleColumn(String name) {
        this(name, new DoubleArrayList(DEFAULT_ARRAY_SIZE));
    }

    public DoubleColumn(String name, int initialSize) {
        this(name, new DoubleArrayList(initialSize));
    }

    public DoubleColumn(String name, double[] arr) {
      this(name, new DoubleArrayList(arr));
    }

    private DoubleColumn(String name, DoubleArrayList data) {
        super(name);
        this.data = data;
    }

    public DoubleColumn(ColumnMetadata metadata) {
        super(metadata);
        data = new DoubleArrayList(metadata.getSize());
    }

    protected static boolean isMissing(double value) {
      return Double.isNaN(value);
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
        doubles.forEach((double i) -> column.append(i));
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
        return sum.agg(this);
    }

    public double product() {
        return product.agg(this);
    }

    public double mean() {
        return mean.agg(this);
    }

    public double median() {
        return median.agg(this);
    }

    public double quartile1() {
        return quartile1.agg(this);
    }

    public double quartile3() {
        return quartile3.agg(this);
    }

    public double percentile(double percentile) {
        return AggregateFunctions.percentile(this.toDoubleArray(), percentile);
    }

    public double range() {
        return range.agg(this);
    }

    public double max() {
        return max.agg(this);
    }

    public double min() {
        return min.agg(this);
    }

    public double variance() {
        return variance.agg(this);
    }

    public double populationVariance() {
        return populationVariance.agg(this);
    }

    public double standardDeviation() {
        return stdDev.agg(this);
    }

    public double sumOfLogs() {
        return sumOfLogs.agg(this);
    }

    // Predicate  functions

    public double sumOfSquares() {
        return sumOfSquares.agg(this);
    }

    public double geometricMean() {
        return geometricMean.agg(this);
    }

    /**
     * Returns the quadraticMean, aka the root-mean-square, for all values in this column
     */
    public double quadraticMean() {
        return quadraticMean.agg(this);
    }

    public double kurtosis() {
        return kurtosis.agg(this);
    }

    public double skewness() {
        return skewness.agg(this);
    }

    /**
     * Adds the given float to this column
     */
    public void append(float f) {
        data.add(f);
    }

    /**
     * Adds the given double to this column
     */
    public void append(double d) {
        data.add(d);
    }

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

    public Selection isNotEqualTo(double d) {
      return select(isNotEqualTo, d);
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

    public Selection isGreaterThan(DoubleColumn d) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        DoubleIterator doubleIterator = d.iterator();
        for (double doubles : data) {
            if (doubles > doubleIterator.nextDouble()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    public Selection isLessThan(DoubleColumn d) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        DoubleIterator doubleIterator = d.iterator();
        for (double doubles : data) {
            if (doubles < doubleIterator.nextDouble()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    @Override
    public String getString(int row) {
      double value = data.getDouble(row);
      if (isMissing(value)) {
          return null;
      }
      return String.valueOf(value);
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
        DoubleColumn column = new DoubleColumn(name(), data);
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

    /**
     * Returns the count of missing values in this column
     */
    @Override
    public int countMissing() {
        int count = 0;
        for (int i = 0; i < size(); i++) {
            if (isMissing(get(i))) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void appendCell(String object) {
        try {
            append(convert(object));
        } catch (NumberFormatException e) {
            throw new NumberFormatException(name() + ": " + e.getMessage());
        }
    }

    /**
     * Returns the natural log of the values in this column as a new DoubleColumn
     */
    public DoubleColumn logN() {
        DoubleColumn newColumn = new DoubleColumn(name() + "[logN]", size());

        for (double value : this) {
            newColumn.append(Math.log(value));
        }
        return newColumn;
    }

    /**
     * Returns the base 10 log of the values in this column as a new DoubleColumn
     *
     * @return
     */
    public DoubleColumn log10() {
        DoubleColumn newColumn = new DoubleColumn(name() + "[log10]", size());

        for (double value : this) {
            newColumn.append(Math.log10(value));
        }
        return newColumn;
    }

    /**
     * Returns the natural log of the values in this column, after adding 1 to each so that zero
     * values don't return -Infinity
     */
    public DoubleColumn log1p() {
        DoubleColumn newColumn = new DoubleColumn(name() + "[1og1p]", size());
        for (double value : this) {
            newColumn.append(Math.log1p(value));
        }
        return newColumn;
    }

    public DoubleColumn round() {
        DoubleColumn newColumn = new DoubleColumn(name() + "[rounded]", size());
        for (double value : this) {
            newColumn.append(Math.round(value));
        }
        return newColumn;
    }

    /**
     * Returns the rounded values as a LongColumn
     */
    public LongColumn roundLong() {
        LongColumn newColumn = new LongColumn(name() + "[rounded]", size());
        for (double value : this) {
            newColumn.append(Math.round(value));
        }
        return newColumn;
    }

    /**
     * Returns the rounded values as a IntColumn. This will throw an exception if the values are too large to fit,
     * however many double columns can safely use this method. Use roundLong() if larger
     *
     * @throws ClassCastException if the returned value will not fit in an int
     */
    public IntColumn roundInt() {
        IntColumn newColumn = new IntColumn(name() + "[rounded]", size());
        for (double value : this) {
            newColumn.append((int) Math.round(value));
        }
        return newColumn;
    }

    /**
     * Returns a doubleColumn with the absolute value of each value in this column
     */
    public DoubleColumn abs() {
        DoubleColumn newColumn = new DoubleColumn(name() + "[abs]", size());
        for (double value : this) {
            newColumn.append(Math.abs(value));
        }
        return newColumn;
    }

    /**
     * Returns a doubleColumn with the square of each value in this column
     */
    public DoubleColumn square() {
        DoubleColumn newColumn = new DoubleColumn(name() + "[sq]", size());
        for (double value : this) {
            newColumn.append(value * value);
        }
        return newColumn;
    }

    public DoubleColumn sqrt() {
        DoubleColumn newColumn = new DoubleColumn(name() + "[sqrt]", size());
        for (double value : this) {
            newColumn.append(Math.sqrt(value));
        }
        return newColumn;
    }

    public DoubleColumn cubeRoot() {
        DoubleColumn newColumn = new DoubleColumn(name() + "[cbrt]", size());
        for (double value : this) {
            newColumn.append(Math.cbrt(value));
        }
        return newColumn;
    }

    public DoubleColumn cube() {
        DoubleColumn newColumn = new DoubleColumn(name() + "[cb]", size());
        for (double value : this) {
            newColumn.append(value * value * value);
        }
        return newColumn;
    }

    public DoubleColumn remainder(DoubleColumn column2) {
        DoubleColumn result = new DoubleColumn(name() + " % " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) % column2.get(r));
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
        DoubleColumn newColumn = new DoubleColumn(name() + "[neg]", size());
        for (double value : this) {
            newColumn.append(value * -1);
        }
        return newColumn;
    }

    /**
     * Compares the given ints, which refer to the indexes of the doubles in this column, according to the values of the
     * doubles themselves
     */
    @Override
    public IntComparator rowComparator() {
        return comparator;
    }

    public double get(int index) {
        return data.getDouble(index);
    }

    @Override
    public double getDouble(int index) {
        return data.getDouble(index);
    }

/*
    @Override
    public float getFloat(int index) {
        double value = data.getDouble(index);
        return value == MISSING_VALUE ? FloatColumn.MISSING_VALUE : (float) value;
    }
*/

    public void set(int r, double value) {
        data.set(r, value);
    }

    /**
     * Conditionally update this column, replacing current values with newValue for all rows where the current value
     * matches the selection criteria
     *
     * Example:
     * myColumn.set(4.0, myColumn.isMissing()); // no more missing values
     */
    public void set(double newValue, Selection rowSelection) {
        for (int row : rowSelection) {
            set(row, newValue);
        }
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

    public Selection isPositive() {
        return select(isPositive);
    }

    public Selection isZero() {
        return select(isZero);
    }

    public Selection isNegative() {
        return select(isNegative);
    }

    public Selection isNonNegative() {
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
            append(doubleColumn.get(i));
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
        if (data.isEmpty()) {
            return returnValue;
        }

        returnValue.append(DoubleColumn.MISSING_VALUE);
        for (int current = 1; current < data.size(); current++) {
            returnValue.append(subtract(get(current), get(current - 1)));
        }
        return returnValue;
    }

    static double add(double val1, double val2) {
        if (val1 == MISSING_VALUE || val2 == MISSING_VALUE) {
            return MISSING_VALUE;
        }
        return val1 + val2;
    }

    static double multiply(double val1, double val2) {
        if (val1 == MISSING_VALUE || val2 == MISSING_VALUE) {
            return MISSING_VALUE;
        }
        return val1 * val2;
    }

    static double divide(double val1, double val2) {
        if (val1 == MISSING_VALUE || val2 == MISSING_VALUE) {
            return MISSING_VALUE;
        }
        return val1 / val2;
    }

    static double subtract(double val1, double val2) {
        if (isMissing(val1) || isMissing(val2)) {
            return MISSING_VALUE;
        }
        return val1 - val2;
    }

    /**
     * Returns a new column with a cumulative sum calculated
     */
    public DoubleColumn cumSum() {
        double total = 0.0;
        DoubleColumn newColumn = new DoubleColumn(name() + "[cumSum]", size());
        for (double value : this) {
            if (isMissing(value)) {
                newColumn.append(MISSING_VALUE);
            } else {
                total += value;
                newColumn.append(total);
            }
        }
        return newColumn;
    }

    /**
     * Returns a new column with a cumulative product calculated
     */
    public DoubleColumn cumProd() {
        double total = 1.0;
        DoubleColumn newColumn = new DoubleColumn(name() + "[cumProd]", size());
        for (double value : this) {
            if (isMissing(value)) {
                newColumn.append(MISSING_VALUE);
            } else {
                total *= value;
                newColumn.append(total);
            }
        }
        return newColumn;
    }

    /**
     * Returns a new column with a percent change calculated
     */
    public DoubleColumn pctChange() {
        DoubleColumn newColumn = new DoubleColumn(name() + "[pctChange]", size());
        newColumn.append(MISSING_VALUE);
        for (int i = 1; i < size(); i++) {
            newColumn.append(get(i) / get(i-1) - 1);
        }
        return newColumn;
    }

}
