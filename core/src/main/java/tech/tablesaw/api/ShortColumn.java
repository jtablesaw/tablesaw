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
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.ShortColumnUtils;
import tech.tablesaw.filtering.ShortBiPredicate;
import tech.tablesaw.filtering.ShortPredicate;
import tech.tablesaw.io.TypeUtils;
import tech.tablesaw.mapping.ShortMapUtils;
import tech.tablesaw.sorting.IntComparisonUtil;
import tech.tablesaw.store.ColumnMetadata;
import tech.tablesaw.util.BitmapBackedSelection;
import tech.tablesaw.util.ReverseShortComparator;
import tech.tablesaw.util.Selection;
import tech.tablesaw.util.Stats;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

/**
 * A column that contains signed 2 byte integer values
 */
public class ShortColumn extends AbstractColumn implements ShortMapUtils, NumericColumn, IntConvertibleColumn {

    public static final short MISSING_VALUE = (Short) ColumnType.SHORT_INT.getMissingValue();

    private static final int DEFAULT_ARRAY_SIZE = 128;
    private static final int BYTE_SIZE = 2;
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    private ShortArrayList data;

    final IntComparator comparator = new IntComparator() {

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

    public ShortColumn(String name) {
        this(name, new ShortArrayList(DEFAULT_ARRAY_SIZE));
    }

    public ShortColumn(String name, int initialSize) {
        this(name, new ShortArrayList(initialSize));
    }

    public ShortColumn(String name, short[] arr) {
        this(name, new ShortArrayList(arr));
    }

    private ShortColumn(String name, ShortArrayList data) {
        super(name);
        this.data = data;
    }

    public ShortColumn(ColumnMetadata metadata) {
        super(metadata);
        data = new ShortArrayList(metadata.getSize());
    }

    protected static boolean isMissing(short value) {
      return value == MISSING_VALUE;
    }

    /**
     * Returns a float that is parsed from the given String
     * <p>
     * We remove any commas before parsing
     */
    public static short convert(String stringValue) {
        if (Strings.isNullOrEmpty(stringValue) || TypeUtils.MISSING_INDICATORS.contains(stringValue)) {
            return (Short) ColumnType.SHORT_INT.getMissingValue();
        }
        Matcher matcher = COMMA_PATTERN.matcher(stringValue);
        return Short.parseShort(matcher.replaceAll(""));
    }

    public int size() {
        return data.size();
    }

    @Override
    public ColumnType type() {
        return ColumnType.SHORT_INT;
    }

    public void append(short i) {
        data.add(i);
    }

    public void set(int index, short value) {
        data.set(index, value);
    }

    /**
     * Conditionally update this column, replacing current values with newValue for all rows where the current value
     * matches the selection criteria
     * <p>
     * Example:
     * myColumn.set((short) 4, myColumn.isMissing()); // no more missing values
     */
    public void set(short newValue, Selection rowSelection) {
        for (int row : rowSelection) {
            set(row, newValue);
        }
    }

    public Selection isLessThan(int i) {
        return select(ShortColumnUtils.isLessThan, i);
    }

    public Selection isGreaterThan(int i) {
        return select(ShortColumnUtils.isGreaterThan, i);
    }

    public Selection isGreaterThan(ShortColumn other) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        ShortIterator shortIterator = other.iterator();
        for (int next : data) {
            if (next > shortIterator.nextShort()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    public Selection isLessThan(ShortColumn other) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        ShortIterator shortIterator = other.iterator();
        for (int next : data) {
            if (next < shortIterator.nextShort()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    public Selection isGreaterThanOrEqualTo(int i) {
        return select(ShortColumnUtils.isGreaterThanOrEqualTo, i);
    }

    public Selection isLessThanOrEqualTo(int i) {
        return select(ShortColumnUtils.isLessThanOrEqualTo, i);
    }

    public Selection isNotEqualTo(int i) {
        return select(ShortColumnUtils.isNotEqualTo, i);
    }

    public Selection isEqualTo(int i) {
        return select(ShortColumnUtils.isEqualTo, i);
    }

    public Selection isEqualTo(ShortColumn f) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        ShortIterator shortIterator = f.iterator();
        for (int next : data) {
            if (next == shortIterator.nextShort()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    public ShortColumn select(Selection selection) {
        ShortColumn column = emptyCopy();
        for (int next : selection) {
            column.append(data.getShort(next));
        }
        return column;
    }

    @Override
    public Table summary() {
        return stats().asTable();
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
        for (int i : data) {
            selection.add(i);
        }
        return selection.size();
    }

    @Override
    public ShortColumn unique() {
        Selection selection = new BitmapBackedSelection();
        for (short i : data) {
            selection.add(i);
        }
        int[] ints = selection.toArray();
        short[] shorts = new short[ints.length];
        for (int i = 0; i < ints.length; i++) {
            shorts[i] = (short) ints[i];
        }
        return new ShortColumn(name() + " Unique values", ShortArrayList.wrap(shorts));
    }

    @Override
    public String getString(int row) {
        short value = data.getShort(row);
        if (value == MISSING_VALUE) {
            return null;
        }
        return String.valueOf(value);
    }

    @Override
    public ShortColumn emptyCopy() {
        ShortColumn column = new ShortColumn(name(), DEFAULT_ARRAY_SIZE);
        column.setComment(comment());
        return column;
    }

    @Override
    public ShortColumn emptyCopy(int rowSize) {
        ShortColumn column = new ShortColumn(name(), rowSize);
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
        ShortArrays.parallelQuickSort(data.elements(), ReverseShortComparator.instance());
    }

    @Override
    public ShortColumn copy() {
        ShortColumn copy = emptyCopy(size());
        copy.data.addAll(data);
        copy.setComment(comment());
        return copy;
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override  // TODO(lwhite): Move to AbstractColumn
    public void appendCell(String object) {
        try {
            append(convert(object));
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException(name() + ": " + nfe.getMessage());
        }
    }

    public short get(int index) {
        return data.getShort(index);
    }

    @Override
    public int getInt(int index) {
        int value = data.getShort(index);
        return value == MISSING_VALUE ? IntColumn.MISSING_VALUE : value;
    }

    @Override
    public long getLong(int index) {
        int value = data.getShort(index);
        return value == MISSING_VALUE ? LongColumn.MISSING_VALUE : value;
    }

    @Override
    public float getFloat(int index) {
        int value = data.getShort(index);
        return value == MISSING_VALUE ? FloatColumn.MISSING_VALUE : (float) value;
    }

    @Override
    public double getDouble(int index) {
        int value = data.getShort(index);
        return value == MISSING_VALUE ? DoubleColumn.MISSING_VALUE : (double) value;
    }

    @Override
    public IntComparator rowComparator() {
        return comparator;
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
        return (short) Math.round(max.agg(this));
    }

    public double min() {
        return (short) Math.round(min.agg(this));
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

    public short firstElement() {
        if (size() > 0) {
            return get(0);
        }
        return MISSING_VALUE;
    }

    public Selection isPositive() {
        return select(ShortColumnUtils.isPositive);
    }

    public Selection isNegative() {
        return select(ShortColumnUtils.isNegative);
    }

    public Selection isNonNegative() {
        return select(ShortColumnUtils.isNonNegative);
    }

    public Selection isZero() {
        return select(ShortColumnUtils.isZero);
    }

    public Selection isEven() {
        return select(ShortColumnUtils.isEven);
    }

    public Selection isOdd() {
        return select(ShortColumnUtils.isOdd);
    }

    public FloatArrayList asFloatArray() {
        FloatArrayList output = new FloatArrayList(data.size());
        for (short aData : data) {
            output.add(aData);
        }
        return output;
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(title());
        for (short i : data) {
            builder.append(String.valueOf(i));
            builder.append('\n');
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "ShortInt column: " + name();
    }

    @Override
    public void append(Column column) {
        Preconditions.checkArgument(column.type() == this.type());
        ShortColumn shortColumn = (ShortColumn) column;
        for (int i = 0; i < shortColumn.size(); i++) {
            append(shortColumn.get(i));
        }
    }

    ShortColumn selectIf(ShortPredicate predicate) {
        ShortColumn column = emptyCopy();
        ShortIterator intIterator = iterator();
        while (intIterator.hasNext()) {
            short next = intIterator.nextShort();
            if (predicate.test(next)) {
                column.append(next);
            }
        }
        return column;
    }

    public IntColumn remainder(ShortColumn column2) {
        IntColumn result = new IntColumn(name() + " % " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) % column2.get(r));
        }
        return result;
    }

    public IntColumn append(ShortColumn column2) {
        IntColumn result = new IntColumn(name() + " + " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) + column2.get(r));
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
    public ShortArrayList top(int n) {
        ShortArrayList top = new ShortArrayList();
        short[] values = data.toShortArray();
        ShortArrays.parallelQuickSort(values, ReverseShortComparator.instance());
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
    public ShortArrayList bottom(int n) {
        ShortArrayList bottom = new ShortArrayList();
        short[] values = data.toShortArray();
        ShortArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(values[i]);
        }
        return bottom;
    }

    @Override
    public ShortIterator iterator() {
        return data.iterator();
    }

    public Selection select(ShortPredicate predicate) {
        Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            short next = data.getShort(idx);
            if (predicate.test(next)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }

    public Selection select(ShortBiPredicate predicate, int valueToCompareAgainst) {
        Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            short next = data.getShort(idx);
            if (predicate.test(next, valueToCompareAgainst)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }

    public double[] asDoubleArray() {
        double[] output = new double[data.size()];
        for (int i = 0; i < data.size(); i++) {
            long val = data.getShort(i);
            if (val == MISSING_VALUE) {
                output[i] = Double.NaN;
            } else {
                output[i] = val;
            }
        }
        return output;
    }

    public ShortSet asSet() {
        return new ShortOpenHashSet(data);
    }

    public boolean contains(short value) {
        return data.contains(value);
    }

    public Stats stats() {
        FloatColumn values = new FloatColumn(name(), asFloatArray());
        return Stats.create(values);
    }

    public ShortArrayList data() {
        return data;
    }

    @Override
    public Selection isMissing() {
        return select(isMissing);
    }

    @Override
    public Selection isNotMissing() {
        return select(isNotMissing);
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
        return ByteBuffer.allocate(2).putShort(get(rowNumber)).array();
    }

    @Override
    public ShortColumn difference() {
        ShortColumn returnValue = new ShortColumn(this.name(), this.size());
        returnValue.append(ShortColumn.MISSING_VALUE);
        for (int current = 0; current < this.size(); current++) {
            if (current + 1 < this.size()) {
                int currentValue = this.get(current);
                int nextValue = this.get(current + 1);
                if (current == ShortColumn.MISSING_VALUE || nextValue == ShortColumn.MISSING_VALUE) {
                    returnValue.append(ShortColumn.MISSING_VALUE);
                } else {
                    returnValue.append((short) (nextValue - currentValue));
                }
            }
        }
        return returnValue;
    }

    @Override
    public int[] asIntArray() {
        int[] output = new int[data.size()];
        for (int i = 0; i < data.size(); i++) {
            output[i] = data.getShort(i);
        }
        return output;
    }

    /**
     * Returns a new column with a cumulative sum calculated
     */
    public ShortColumn cumSum() {
        short total = 0;
        ShortColumn newColumn = new ShortColumn(name() + "[cumSum]", size());
        for (short value : this) {
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
    public ShortColumn cumProd() {
        short total = 1;
        ShortColumn newColumn = new ShortColumn(name() + "[cumProd]", size());
        for (short value : this) {
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
    public FloatColumn pctChange() {
        FloatColumn newColumn = new FloatColumn(name() + "[pctChange]", size());
        newColumn.append(FloatColumn.MISSING_VALUE);
        for (int i = 1; i < size(); i++) {
            newColumn.append((float) get(i) / get(i-1) - 1);
        }
        return newColumn;
    }
}
