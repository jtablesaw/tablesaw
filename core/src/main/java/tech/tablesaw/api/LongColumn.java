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
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.filtering.LongBiPredicate;
import tech.tablesaw.filtering.LongPredicate;
import tech.tablesaw.io.TypeUtils;
import tech.tablesaw.mapping.LongMapUtils;
import tech.tablesaw.reducing.NumericReduceUtils;
import tech.tablesaw.sorting.LongComparisonUtil;
import tech.tablesaw.store.ColumnMetadata;
import tech.tablesaw.util.BitmapBackedSelection;
import tech.tablesaw.util.ReverseLongComparator;
import tech.tablesaw.util.Selection;
import tech.tablesaw.util.Stats;

import static tech.tablesaw.reducing.NumericReduceUtils.*;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A column that contains signed 8 byte integer values
 */
public class LongColumn extends AbstractColumn implements LongMapUtils, NumericColumn {

    public static final long MISSING_VALUE = (Long) ColumnType.LONG_INT.getMissingValue();

    private static final int DEFAULT_ARRAY_SIZE = 128;
    private static final int BYTE_SIZE = 8;
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    private LongArrayList data;
    private final IntComparator comparator = new IntComparator() {

        @Override
        public int compare(Integer i1, Integer i2) {
            return compare((int) i1, (int) i2);
        }

        public int compare(int i1, int i2) {
            long prim1 = get(i1);
            long prim2 = get(i2);
            return LongComparisonUtil.getInstance().compare(prim1, prim2);
        }
    };

    public LongColumn(String name) {
      this(name, new LongArrayList(DEFAULT_ARRAY_SIZE));
    }

    public LongColumn(String name, int initialSize) {
        this(name, new LongArrayList(initialSize));
    }

    public LongColumn(String name, long[] arr) {
      this(name, new LongArrayList(arr));
    }

    private LongColumn(String name, LongArrayList data) {
        super(name);
        this.data = data;
    }

    public LongColumn(ColumnMetadata metadata) {
        super(metadata);
        data = new LongArrayList(metadata.getSize());
    }

    /**
     * Returns a float that is parsed from the given String
     * <p>
     * We remove any commas before parsing
     */
    public static long convert(String stringValue) {
        if (Strings.isNullOrEmpty(stringValue) || TypeUtils.MISSING_INDICATORS.contains(stringValue)) {
            return (Long) ColumnType.LONG_INT.getMissingValue();
        }
        Matcher matcher = COMMA_PATTERN.matcher(stringValue);
        return Long.parseLong(matcher.replaceAll(""));
    }

    public int size() {
        return data.size();
    }

    public LongArrayList data() {
        return data;
    }

    @Override
    public ColumnType type() {
        return ColumnType.LONG_INT;
    }

    public void append(long i) {
        data.add(i);
    }

    public void set(int index, long value) {
        data.set(index, value);
    }

    public Selection isLessThan(long i) {
        return select(isLessThan, i);
    }

    public Selection isGreaterThan(long i) {
        return select(isGreaterThan, i);
    }

    public Selection isGreaterThanOrEqualTo(long i) {
        return select(isGreaterThanOrEqualTo, i);
    }

    public Selection isLessThanOrEqualTo(long f) {
        return select(isLessThanOrEqualTo, f);
    }

    public Selection isEqualTo(long i) {
        return select(isEqualTo, i);
    }

    public Selection isNotEqualTo(long i) {
      return select(isNotEqualTo, i);
    }
 
    public Selection isEqualTo(LongColumn f) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        LongIterator longIterator = f.iterator();
        for (long next : data) {
            if (next == longIterator.nextLong()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    @Override
    public Table summary() {
        return stats().asTable();
    }

    public Stats stats() {
        FloatColumn values = new FloatColumn(name(), toFloatArray());
        return Stats.create(values);
    }

    @Override
    public int countUnique() {
        LongSet longSet = new LongArraySet();
        for (long i : data) {
            longSet.add(i);
        }
        return longSet.size();
    }

    @Override
    public LongColumn unique() {
        LongSet longSet = new LongArraySet();
        longSet.addAll(data);
        return new LongColumn(name() + " Unique values", new LongArrayList(longSet));
    }

    public LongColumn remainder(LongColumn column2) {
        LongColumn result = new LongColumn(name() + " % " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) % column2.get(r));
        }
        return result;
    }

    public LongColumn append(LongColumn column2) {
        LongColumn result = new LongColumn(name() + " + " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) + column2.get(r));
        }
        return result;
    }

    public LongColumn subtract(LongColumn column2) {
        LongColumn result = new LongColumn(name() + " - " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) - column2.get(r));
        }
        return result;
    }

    public LongColumn multiply(LongColumn column2) {
        LongColumn result = new LongColumn(name() + " * " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) * column2.get(r));
        }
        return result;
    }

    public FloatColumn multiply(FloatColumn column2) {
        FloatColumn result = new FloatColumn(name() + " * " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) * column2.get(r));
        }
        return result;
    }

    public FloatColumn divide(FloatColumn column2) {
        FloatColumn result = new FloatColumn(name() + " / " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) / column2.get(r));
        }
        return result;
    }

    public LongColumn divide(LongColumn column2) {
        LongColumn result = new LongColumn(name() + " / " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) / column2.get(r));
        }
        return result;
    }

    @Override
    public String getString(int row) {
        return String.valueOf(data.getLong(row));
    }

    @Override
    public LongColumn emptyCopy() {
        LongColumn column = new LongColumn(name(), DEFAULT_ARRAY_SIZE);
        column.setComment(comment());
        return column;
    }

    @Override
    public LongColumn emptyCopy(int rowSize) {
        LongColumn column = new LongColumn(name(), rowSize);
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
        LongArrays.parallelQuickSort(data.elements(), ReverseLongComparator.instance());
    }

    @Override
    public LongColumn copy() {
        LongColumn copy = emptyCopy(size());
        copy.data.addAll(data);
        copy.setComment(comment());
        return copy;
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
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public void appendCell(String object) {
        Preconditions.checkNotNull(object);
        try {
            append(convert(object));
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException(name() + ": " + nfe.getMessage());
        }
    }

    public long get(int index) {
        return data.getLong(index);
    }

    @Override
    public float getFloat(int index) {
        return (float) data.getLong(index);
    }

    @Override
    public IntComparator rowComparator() {
        return comparator;
    }

    // Reduce functions applied to the whole column
    public long sum() {
        return Math.round(sum.reduce(toDoubleArray()));
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
        return Math.round(max.reduce(this));
    }

    public double min() {
        return Math.round(min.reduce(this));
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

    public long firstElement() {
        if (size() > 0) {
            return get(0);
        }
        return MISSING_VALUE;
    }

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

    public FloatArrayList toFloatArray() {
        FloatArrayList output = new FloatArrayList(data.size());
        for (long aData : data) {
            output.add(aData);
        }
        return output;
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(title());
        for (long i : data) {
            builder.append(String.valueOf(i));
            builder.append('\n');
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "LongInt column: " + name();
    }

    @Override
    public void append(Column column) {
        Preconditions.checkArgument(column.type() == this.type());
        LongColumn longColumn = (LongColumn) column;
        for (int i = 0; i < longColumn.size(); i++) {
            append(longColumn.get(i));
        }
    }

    public LongColumn select(Selection selection) {
        LongColumn column = emptyCopy();
        for (int next : selection) {
            column.append(data.getLong(next));
        }
        return column;
    }

    public LongColumn selectIf(LongPredicate predicate) {
        LongColumn column = emptyCopy();
        LongIterator intIterator = iterator();
        while (intIterator.hasNext()) {
            long next = intIterator.nextLong();
            if (predicate.test(next)) {
                column.append(next);
            }
        }
        return column;
    }

    /**
     * Returns the largest ("top") n values in the column
     *
     * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
     *          number of observations in the column
     * @return A list, possibly empty, of the largest observations
     */
    public LongArrayList top(int n) {
        LongArrayList top = new LongArrayList();
        long[] values = data.toLongArray();
        LongArrays.parallelQuickSort(values, ReverseLongComparator.instance());
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
    public LongArrayList bottom(int n) {
        LongArrayList bottom = new LongArrayList();
        long[] values = data.toLongArray();
        LongArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(values[i]);
        }
        return bottom;
    }

    @Override
    public LongIterator iterator() {
        return data.iterator();
    }

    public Selection select(LongPredicate predicate) {
        Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            long next = data.getLong(idx);
            if (predicate.test(next)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }

    public Selection select(LongBiPredicate predicate, long valueToCompareAgainst) {
        Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            long next = data.getLong(idx);
            if (predicate.test(next, valueToCompareAgainst)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }

    @Override
    public double[] toDoubleArray() {
        double[] output = new double[data.size()];
        for (int i = 0; i < data.size(); i++) {
            output[i] = data.getLong(i);
        }
        return output;
    }

    public LongSet asSet() {
        return new LongOpenHashSet(data);
    }

    public boolean contains(long value) {
        return data.contains(value);
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
        return ByteBuffer.allocate(8).putLong(get(rowNumber)).array();
    }

    @Override
    public LongColumn difference() {
        LongColumn returnValue = new LongColumn(this.name(), data.size());
        returnValue.append(LongColumn.MISSING_VALUE);
        for (int current = 1; current > data.size(); current++) {
            returnValue.append(get(current) - get(current + 1));
        }
        return returnValue;
    }
}
