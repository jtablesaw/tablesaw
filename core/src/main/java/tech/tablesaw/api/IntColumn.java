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
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.filtering.IntBiPredicate;
import tech.tablesaw.filtering.IntPredicate;
import tech.tablesaw.io.TypeUtils;
import tech.tablesaw.mapping.IntMapUtils;
import tech.tablesaw.sorting.IntComparisonUtil;
import tech.tablesaw.store.ColumnMetadata;
import tech.tablesaw.util.BitmapBackedSelection;
import tech.tablesaw.util.ReverseIntComparator;
import tech.tablesaw.util.Selection;
import tech.tablesaw.util.Stats;

import static tech.tablesaw.aggregate.AggregateFunctions.*;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A column that contains signed 4 byte integer values
 */
public class IntColumn extends AbstractColumn implements IntMapUtils, NumericColumn, IntConvertibleColumn {

    public static final int MISSING_VALUE = (Integer) ColumnType.INTEGER.getMissingValue();
    public static final int DEFAULT_ARRAY_SIZE = 128;
    private static final int BYTE_SIZE = 4;
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    private IntArrayList data;
    final it.unimi.dsi.fastutil.ints.IntComparator comparator = new it.unimi.dsi.fastutil.ints.IntComparator() {

        @Override
        public int compare(Integer i1, Integer i2) {
            return compare((int) i1, (int) i2);
        }

        public int compare(int i1, int i2) {
            int prim1 = get(i1);
            int prim2 = get(i2);
            return IntComparisonUtil.getInstance().compare(prim1, prim2);
        }
    };

    public IntColumn(String name) {
      this(name, new IntArrayList(DEFAULT_ARRAY_SIZE));
    }

    public IntColumn(String name, int initialSize) {
        this(name, new IntArrayList(initialSize));
    }

    public IntColumn(String name, IntArrayList data) {
        super(name);
        this.data = data;
    }

    public IntColumn(String name, int[] arr) {
      this(name, new IntArrayList(arr));
    }

    public IntColumn(ColumnMetadata metadata) {
        super(metadata);
        data = new IntArrayList(metadata.getSize());
    }

    protected static boolean isMissing(int value) {
      return value == MISSING_VALUE;
    }

    /**
     * Returns a float that is parsed from the given String
     * <p>
     * We remove any commas before parsing
     */
    private static int convert(String stringValue) {
        if (Strings.isNullOrEmpty(stringValue) || TypeUtils.MISSING_INDICATORS.contains(stringValue)) {
            return MISSING_VALUE;
        }
        Matcher matcher = COMMA_PATTERN.matcher(stringValue);
        return Integer.parseInt(matcher.replaceAll(""));
    }

    public IntArrayList data() {
        return data;
    }

    public int size() {
        return data.size();
    }

    @Override
    public ColumnType type() {
        return ColumnType.INTEGER;
    }

    public void append(int i) {
        data.add(i);
    }

    public void set(int index, int value) {
        data.set(index, value);
    }

    /**
     * Conditionally update this column, replacing current values with newValue for all rows where the current value
     * matches the selection criteria
     *
     * Example:
     * myColumn.set(4, myColumn.isMissing()); // no more missing values
     */
    public void set(int newValue, Selection rowSelection) {
        for (int row : rowSelection) {
            set(row, newValue);
        }
    }

    public Selection isLessThan(int i) {
        return select(isLessThan, i);
    }

    public Selection isGreaterThan(int i) {
        return select(isGreaterThan, i);
    }

    public Selection isGreaterThanOrEqualTo(int i) {
        return select(isGreaterThanOrEqualTo, i);
    }

    public Selection isLessThanOrEqualTo(int i) {
        return select(isLessThanOrEqualTo, i);
    }

    public Selection isNotEqualTo(int i) {
      return select(isNotEqualTo, i);
    }

    public Selection isEqualTo(int i) {
        return select(isEqualTo, i);
    }

    public Selection isMissing() {
        return select(isMissing);
    }

    public Selection isNotMissing() {
        return select(isNotMissing);
    }

    public Selection isIn(int ... values) {
        Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            int next = data.getInt(idx);
            for (int v : values) {
                if (v == next) {
                    bitmap.add(idx);
                    break;
                }
            }
        }
        return bitmap;
    }

    public Selection isEqualTo(IntColumn other) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        IntIterator otherIterator = other.iterator();
        for (int next : data) {
            int otherNext = otherIterator.nextInt();
            if (next == otherNext) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    public Selection isGreaterThan(IntColumn other) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        IntIterator otherIterator = other.iterator();
        for (int next : data) {
            int otherNext = otherIterator.nextInt();
            if (next > otherNext) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    public Selection isLessThan(IntColumn other) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        IntIterator otherIterator = other.iterator();
        for (int next : data) {
            int otherNext = otherIterator.nextInt();
            if (next < otherNext) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    @Override
    public Table summary() {
        return Stats.create(this).asTable();
    }

    /**
     * Returns the count of missing values in this column
     */
    @Override
    public int countMissing() {
        int count = 0;
        for (int i = 0; i < size(); i++) {
            if (get(i) == MISSING_VALUE) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int countUnique() {
        Selection selection = new BitmapBackedSelection();
        data.forEach((int i) -> selection.add(i));
        return selection.size();
    }

    @Override
    public IntColumn unique() {
        Selection selection = new BitmapBackedSelection();
        data.forEach((int i) -> selection.add(i));
        return new IntColumn(name() + " Unique values", IntArrayList.wrap(selection.toArray()));
    }

    public IntSet asSet() {
        return new IntOpenHashSet(data);
    }

    @Override
    public String getString(int row) {
        int value = data.getInt(row);
        if (value == MISSING_VALUE) {
          return null;
      }
      return String.valueOf(value);
    }

    @Override
    public IntColumn emptyCopy() {
        IntColumn column = new IntColumn(name(), DEFAULT_ARRAY_SIZE);
        column.setComment(comment());
        return column;
    }

    @Override
    public IntColumn emptyCopy(int rowSize) {
        IntColumn column = new IntColumn(name(), rowSize);
        column.setComment(comment());
        return column;
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public void sortAscending() {
        Arrays.parallelSort(data.elements());
    }

    @Override
    public void sortDescending() {
        IntArrays.parallelQuickSort(data.elements(), ReverseIntComparator.instance());
    }

    @Override
    public IntColumn copy() {
        IntColumn column = new IntColumn(name(), data);
        column.setComment(comment());
        return column;
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public void appendCell(String object) {
        append(convert(object));
    }

    public int get(int index) {
        return data.getInt(index);
    }

    @Override
    public int getInt(int index) {
        return data.getInt(index);
    }

    @Override
    public long getLong(int index) {
        int value = data.getInt(index);
        return value == MISSING_VALUE ? LongColumn.MISSING_VALUE : value;
    }

    @Override
    public float getFloat(int index) {
        int value = data.getInt(index);
        return value == MISSING_VALUE ? FloatColumn.MISSING_VALUE : (float) value;
    }

    @Override
    public double getDouble(int index) {
        int value = data.getInt(index);
        return value == MISSING_VALUE ? DoubleColumn.MISSING_VALUE : (double) value;
    }

    @Override
    public it.unimi.dsi.fastutil.ints.IntComparator rowComparator() {
        return comparator;
    }

    public int firstElement() {
        if (size() > 0) {
            return get(0);
        }
        return MISSING_VALUE;
    }

    // Reduce functions applied to the whole column
    public long sum() {
        return Math.round(sum.agg(asDoubleArray()));
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
        return AggregateFunctions.percentile(this.asDoubleArray(), percentile);
    }

    public double range() {
        return range.agg(this);
    }

    public double max() {
        return (int) Math.round(max.agg(this));
    }

    public double min() {
        return (int) Math.round(min.agg(this));
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

    // boolean functions

    public Selection isPositive() {
        return select(isPositive);
    }

    public Selection isNegative() {
        return select(isNegative);
    }

    public Selection isNonNegative() {
        return select(isNonNegative);
    }

    public Selection isZero() {
        return select(isZero);
    }

    public Selection isEven() {
        return select(isEven);
    }

    public Selection isOdd() {
        return select(isOdd);
    }

    public FloatArrayList asFloatArray() {
        FloatArrayList output = new FloatArrayList(data.size());
        for (int aData : data) {
            output.add(aData);
        }
        return output;
    }

    @Override
    public int[] asIntArray() {
        int[] output = new int[data.size()];
        for (int i = 0; i < data.size(); i++) {
            output[i] = data.getInt(i);
        }
        return output;
    }

    public double[] asDoubleArray() {
        double[] output = new double[data.size()];
        for (int i = 0; i < data.size(); i++) {
            long val = data.getInt(i);
            if (val == MISSING_VALUE) {
                output[i] = Double.NaN;
            } else {
                output[i] = val;
            }
        }
        return output;
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(title());
        for (int i : data) {
            builder.append(String.valueOf(i));
            builder.append('\n');
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "Int column: " + name();
    }

    @Override
    public void append(Column column) {
        Preconditions.checkArgument(column.type() == this.type());
        IntColumn intColumn = (IntColumn) column;
        for (int i = 0; i < intColumn.size(); i++) {
            append(intColumn.get(i));
        }
    }

    public IntColumn selectIf(IntPredicate predicate) {
        IntColumn column = emptyCopy();
        IntIterator intIterator = iterator();
        while (intIterator.hasNext()) {
            int next = intIterator.nextInt();
            if (predicate.test(next)) {
                column.append(next);
            }
        }
        return column;
    }

    public IntColumn select(Selection selection) {
        IntColumn column = emptyCopy();
        for (Integer next : selection) {
            column.append(data.getInt(next));
        }
        return column;
    }

    public Selection select(IntPredicate predicate) {
        Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            int next = data.getInt(idx);
            if (predicate.test(next)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }

    public Selection select(IntBiPredicate predicate, int value) {
        Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            int next = data.getInt(idx);
            if (predicate.test(next, value)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }

    public long sumIf(IntPredicate predicate) {
        long sum = 0;
        IntIterator intIterator = iterator();
        while (intIterator.hasNext()) {
            int next = intIterator.nextInt();
            if (predicate.test(next)) {
                sum += next;
            }
        }
        return sum;
    }

    public long countIf(IntPredicate predicate) {
        long count = 0;
        IntIterator intIterator = iterator();
        while (intIterator.hasNext()) {
            int next = intIterator.nextInt();
            if (predicate.test(next)) {
                count++;
            }
        }
        return count;
    }

    public IntColumn remainder(IntColumn column2) {
        IntColumn result = new IntColumn(name() + " % " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) % column2.get(r));
        }
        return result;
    }

    /**
     * Returns the largest ("top") n values in the column
     *
     * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
     *          number of observations in the column
     * @return A list, possibly empty, of the largest observations
     */
    public IntArrayList top(int n) {
        IntArrayList top = new IntArrayList();
        int[] values = data.toIntArray();
        IntArrays.parallelQuickSort(values, ReverseIntComparator.instance());
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
    public IntArrayList bottom(int n) {
        IntArrayList bottom = new IntArrayList();
        int[] values = data.toIntArray();
        IntArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(values[i]);
        }
        return bottom;
    }

    @Override
    public IntIterator iterator() {
        return data.iterator();
    }

    public Stats stats() {
        FloatColumn values = new FloatColumn(name(), asFloatArray());
        return Stats.create(values);
    }

    public boolean contains(int i) {
        return data.contains(i);
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
        return ByteBuffer.allocate(4).putInt(get(rowNumber)).array();
    }

    @Override
    public IntColumn difference() {
        IntColumn returnValue = new IntColumn(this.name(), this.size());
        if (data.isEmpty()) {
            return returnValue;
        }

        returnValue.append(MISSING_VALUE);
        for (int current = 1; current < data.size(); current++) {
            returnValue.append(subtract(get(current), get(current - 1)));
        }
        return returnValue;
    }

    static int add(int val1, int val2) {
        if (val1 == MISSING_VALUE || val2 == MISSING_VALUE) {
            return MISSING_VALUE;
        }
        return val1 + val2;
    }

    static int multiply(int val1, int val2) {
        if (val1 == MISSING_VALUE || val2 == MISSING_VALUE) {
            return MISSING_VALUE;
        }
        return val1 * val2;
    }

    static int divide(int val1, int val2) {
        if (val1 == MISSING_VALUE || val2 == MISSING_VALUE) {
            return MISSING_VALUE;
        }
        return val1 / val2;
    }

    static int subtract(int val1, int val2) {
        if (val1 == MISSING_VALUE || val2 == MISSING_VALUE) {
            return MISSING_VALUE;
        }
        return val1 - val2;
    }

    /**
     * Returns a new column with a cumulative sum calculated
     */
    public IntColumn cumSum() {
        int total = 0;
        IntColumn newColumn = new IntColumn(name() + "[cumSum]", size());
        for (int value : this) {
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
    public IntColumn cumProd() {
        int total = 1;
        IntColumn newColumn = new IntColumn(name() + "[cumProd]", size());
        for (int value : this) {
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
        newColumn.append(DoubleColumn.MISSING_VALUE);
        for (int i = 1; i < size(); i++) {
            newColumn.append((double) get(i) / get(i-1) - 1);
        }
        return newColumn;
    }  

}
