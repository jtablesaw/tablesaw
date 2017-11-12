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
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatIterable;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatOpenHashSet;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.ints.IntComparator;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.filtering.FloatBiPredicate;
import tech.tablesaw.filtering.FloatPredicate;
import tech.tablesaw.io.TypeUtils;
import tech.tablesaw.reducing.NumericReduceUtils;
import tech.tablesaw.store.ColumnMetadata;
import tech.tablesaw.util.BitmapBackedSelection;
import tech.tablesaw.util.Selection;
import tech.tablesaw.util.Stats;

import org.jetbrains.annotations.NotNull;

import static tech.tablesaw.columns.FloatColumnUtils.*;
import static tech.tablesaw.reducing.NumericReduceUtils.*;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A column in a base table that contains float values
 */
public class FloatColumn extends AbstractColumn implements FloatIterable, NumericColumn {

    public static final float MISSING_VALUE = (Float) ColumnType.FLOAT.getMissingValue();
    private static final int BYTE_SIZE = 4;
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    private static final int DEFAULT_ARRAY_SIZE = 128;

    /**
     * Compares two floats, such that a sort based on this comparator would sort in descending order
     */
    private FloatComparator reverseFloatComparator = new FloatComparator() {

        @Override
        public int compare(Float o2, Float o1) {
            return (o1 < o2 ? -1 : (o1.equals(o2) ? 0 : 1));
        }

        @Override
        public int compare(float o2, float o1) {
            return (o1 < o2 ? -1 : (o1 == o2 ? 0 : 1));
        }
    };

    private FloatArrayList data;

    private final IntComparator comparator = new IntComparator() {

        @Override
        public int compare(Integer r1, Integer r2) {
            float f1 = data.getFloat(r1);
            float f2 = data.getFloat(r2);
            return Float.compare(f1, f2);
        }

        public int compare(int r1, int r2) {
            float f1 = data.getFloat(r1);
            float f2 = data.getFloat(r2);
            return Float.compare(f1, f2);
        }
    };

    public FloatColumn(String name) {
        this(name, new FloatArrayList(DEFAULT_ARRAY_SIZE));
    }

    public FloatColumn(String name, int initialSize) {
        this(name, new FloatArrayList(initialSize));
    }

    public FloatColumn(String name, float[] arr) {
      this(name, new FloatArrayList(arr));
    }

    public FloatColumn(String name, FloatArrayList data) {
        super(name);
        this.data = data;
    }

    public FloatColumn(ColumnMetadata metadata) {
        super(metadata);
        data = new FloatArrayList(metadata.getSize());
    }

    /**
     * Returns a float that is parsed from the given String
     * <p>
     * We remove any commas before parsing
     */
    public static float convert(String stringValue) {
        if (Strings.isNullOrEmpty(stringValue) || TypeUtils.MISSING_INDICATORS.contains(stringValue)) {
            return MISSING_VALUE;
        }
        Matcher matcher = COMMA_PATTERN.matcher(stringValue);
        return Float.parseFloat(matcher.replaceAll(""));
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
        FloatSet floats = new FloatOpenHashSet();
        for (int i = 0; i < size(); i++) {
            floats.add(data.getFloat(i));
        }
        return floats.size();
    }

    /**
     * Returns the largest ("top") n values in the column
     *
     * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
     *          number of observations in the column
     * @return A list, possibly empty, of the largest observations
     */
    public FloatArrayList top(int n) {
        FloatArrayList top = new FloatArrayList();
        float[] values = data.toFloatArray();
        FloatArrays.parallelQuickSort(values, reverseFloatComparator);
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
    public FloatArrayList bottom(int n) {
        FloatArrayList bottom = new FloatArrayList();
        float[] values = data.toFloatArray();
        FloatArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(values[i]);
        }
        return bottom;
    }

    @Override
    public FloatColumn unique() {
        FloatSet floats = new FloatOpenHashSet();
        for (int i = 0; i < size(); i++) {
            floats.add(data.getFloat(i));
        }
        FloatColumn column = new FloatColumn(name() + " Unique values", floats.size());
        floats.forEach(column::append);
        return column;
    }

    public FloatArrayList data() {
        return data;
    }

    @Override
    public ColumnType type() {
        return ColumnType.FLOAT;
    }

    public float firstElement() {
        if (size() > 0) {
            return data.getFloat(0);
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

    // Predicate  functions

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
    public void append(float f) {
        data.add(f);
    }

    /**
     * Adds the given double to this column, after casting it to a float
     */
    public void append(double d) {
        data.add((float) d);
    }

    public Selection isLessThan(float f) {
        return select(isLessThan, f);
    }

    public Selection isZero() {
        return select(isZero);
    }

    public Selection isNegative() {
        return select(isNegative);
    }
    public Selection isPositive() {
        return select(isPositive);
    }
    public Selection isNonNegative() {
        return select(isNonNegative);
    }

    public Selection isMissing() {
        return select(isMissing);
    }

    public Selection isNotMissing() {
        return select(isNotMissing);
    }

    public Selection isGreaterThan(float f) {
        return select(isGreaterThan, f);
    }

    public Selection isGreaterThanOrEqualTo(float f) {
        return select(isGreaterThanOrEqualTo, f);
    }

    public Selection isLessThanOrEqualTo(float f) {
        return select(isLessThanOrEqualTo, f);
    }

    public Selection isNotEqualTo(float f) {
      return select(isNotEqualTo, f);
    }    

    public Selection isEqualTo(float f) {
        return select(isEqualTo, f);
    }

    public Selection isEqualTo(FloatColumn f) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        FloatIterator floatIterator = f.iterator();
        for (float floats : data) {
            if (floats == floatIterator.nextFloat()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    @Override
    public String getString(int row) {
        return String.valueOf(data.getFloat(row));
    }

    @Override
    public FloatColumn emptyCopy() {
        FloatColumn column = new FloatColumn(name());
        column.setComment(comment());
        return column;
    }

    @Override
    public FloatColumn emptyCopy(int rowSize) {
        FloatColumn column = new FloatColumn(name(), rowSize);
        column.setComment(comment());
        return column;
    }

    @Override
    public void clear() {
        data = new FloatArrayList(DEFAULT_ARRAY_SIZE);
    }

    @Override
    public FloatColumn copy() {
        FloatColumn column = new FloatColumn(name(), data);
        column.setComment(comment());
        return column;
    }

    @Override
    public void sortAscending() {
        Arrays.parallelSort(data.elements());
    }

    @Override
    public void sortDescending() {
        FloatArrays.parallelQuickSort(data.elements(), reverseFloatComparator);
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

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
            float f = get(i);
            if (f != f) {
                count++;
            }
        }
        return count;
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

    /**
     * Returns the natural log of the values in this column as a new FloatColumn
     */
    public FloatColumn logN() {
        FloatColumn newColumn = new FloatColumn(name() + "[logN]", size());

        for (float value : this) {
            newColumn.append((float) Math.log(value));
        }
        return newColumn;
    }

    public FloatColumn log10() {
        FloatColumn newColumn = new FloatColumn(name() + "[log10]", size());

        for (float value : this) {
            newColumn.append((float) Math.log10(value));
        }
        return newColumn;
    }

    /**
     * Returns the natural log of the values in this column, after adding 1 to each so that zero
     * values don't return -Infinity
     */
    public FloatColumn log1p() {
        FloatColumn newColumn = new FloatColumn(name() + "[1og1p]", size());
        for (float value : this) {
            newColumn.append((float) Math.log1p(value));
        }
        return newColumn;
    }

    public FloatColumn round() {
        FloatColumn newColumn = new FloatColumn(name() + "[rounded]", size());
        for (float value : this) {
            newColumn.append(Math.round(value));
        }
        return newColumn;
    }

    public FloatColumn abs() {
        FloatColumn newColumn = new FloatColumn(name() + "[abs]", size());
        for (float value : this) {
            newColumn.append(Math.abs(value));
        }
        return newColumn;
    }

    public FloatColumn square() {
        FloatColumn newColumn = new FloatColumn(name() + "[sq]", size());
        for (float value : this) {
            newColumn.append(value * value);
        }
        return newColumn;
    }

    public FloatColumn sqrt() {
        FloatColumn newColumn = new FloatColumn(name() + "[sqrt]", size());
        for (float value : this) {
            newColumn.append((float) Math.sqrt(value));
        }
        return newColumn;
    }

    public FloatColumn cubeRoot() {
        FloatColumn newColumn = new FloatColumn(name() + "[cbrt]", size());
        for (float value : this) {
            newColumn.append((float) Math.cbrt(value));
        }
        return newColumn;
    }

    public FloatColumn cube() {
        FloatColumn newColumn = new FloatColumn(name() + "[cb]", size());
        for (float value : this) {
            newColumn.append(value * value * value);
        }
        return newColumn;
    }

    public FloatColumn remainder(FloatColumn column2) {
        FloatColumn result = new FloatColumn(name() + " % " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) % column2.get(r));
        }
        return result;
    }

    public FloatColumn append(FloatColumn column2) {
        FloatColumn result = new FloatColumn(name() + " + " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) + column2.get(r));
        }
        return result;
    }

    public FloatColumn subtract(FloatColumn column2) {
        FloatColumn result = new FloatColumn(name() + " - " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) - column2.get(r));
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

    public FloatColumn multiply(IntColumn column2) {
        FloatColumn result = new FloatColumn(name() + " * " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) * column2.get(r));
        }
        return result;
    }

    public FloatColumn multiply(LongColumn column2) {
        FloatColumn result = new FloatColumn(name() + " * " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) * column2.get(r));
        }
        return result;
    }

    public FloatColumn multiply(ShortColumn column2) {
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

    public FloatColumn divide(IntColumn column2) {
        FloatColumn result = new FloatColumn(name() + " / " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) / column2.get(r));
        }
        return result;
    }

    public FloatColumn divide(LongColumn column2) {
        FloatColumn result = new FloatColumn(name() + " / " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) / column2.get(r));
        }
        return result;
    }

    public FloatColumn divide(ShortColumn column2) {
        FloatColumn result = new FloatColumn(name() + " / " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) / column2.get(r));
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
    public FloatColumn neg() {
        FloatColumn newColumn = new FloatColumn(name() + "[neg]", size());
        for (float value : this) {
            newColumn.append(value * -1);
        }
        return newColumn;
    }

    /**
     * Compares the given ints, which refer to the indexes of the floats in this column, according to the values of the
     * floats themselves
     */
    @Override
    public IntComparator rowComparator() {
        return comparator;
    }

    public float get(int index) {
        return data.getFloat(index);
    }

    @Override
    public float getFloat(int index) {
        return data.getFloat(index);
    }

    public void set(int r, float value) {
        data.set(r, value);
    }

    // TODO(lwhite): Reconsider the implementation of this functionality to allow user to provide a specific max error.
    // TODO(lwhite): continued: Also see section in Effective Java on floating point comparisons.
    Selection isCloseTo(float target) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        for (float f : data) {
            if (Float.compare(f, target) == 0) {
                results.add(i);
            }
            i++;
        }

        return results;
    }

    Selection isCloseTo(double target) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        for (float f : data) {
            if (Double.compare(f, 0.0) == 0) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    public double[] toDoubleArray() {
        double[] output = new double[data.size()];
        for (int i = 0; i < data.size(); i++) {
            output[i] = data.getFloat(i);
        }
        return output;
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(title());
        for (Float aData : data) {
            builder.append(String.valueOf(aData));
            builder.append('\n');
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "Float column: " + name();
    }

    @Override
    public void append(Column column) {
        Preconditions.checkArgument(column.type() == this.type());
        FloatColumn floatColumn = (FloatColumn) column;
        for (int i = 0; i < floatColumn.size(); i++) {
            append(floatColumn.get(i));
        }
    }

    @Override
    public FloatIterator iterator() {
        return data.iterator();
    }

    public Selection select(FloatPredicate predicate) {
        Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            float next = data.getFloat(idx);
            if (predicate.test(next)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }

    public Selection select(FloatBiPredicate predicate, float value) {
        Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            float next = data.getFloat(idx);
            if (predicate.test(next, value)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }

    FloatSet asSet() {
        return new FloatOpenHashSet(data);
    }

    public boolean contains(float value) {
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
        return ByteBuffer.allocate(4).putFloat(get(rowNumber)).array();
    }

    @Override
    public FloatColumn difference() {
        FloatColumn returnValue = new FloatColumn(this.name(), this.size());
        returnValue.append(FloatColumn.MISSING_VALUE);
        for (int current = 0; current < this.size(); current++) {
            if (current + 1 < this.size()) {

            /*
             * check for missing values:
             * note that for floats you test val != val,
             * since a missing float is encoded as Float.NaN and nothing is equal to NaN.
             */

                float currentValue = get(current);
                float nextValue = get(current + 1);

                if (currentValue != currentValue || nextValue != nextValue) {
                    returnValue.append(Float.NaN);
                } else {
                    returnValue.append(nextValue - currentValue);
                }
            }
        }
        return returnValue;
    }

    @NotNull
    public FloatColumn add(@NotNull FloatColumn column2) {
        FloatColumn result = new FloatColumn(name() + " - " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) + column2.get(r));
        }
        return result;
    }

    @NotNull
    public FloatColumn addToEach(float value) {
        FloatColumn result = new FloatColumn(name() + " + " + value, size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) + value);
        }
        return result;
    }

    @NotNull
    public FloatColumn addToEach(int value) {
        FloatColumn result = new FloatColumn(name() + " + " + value, size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) + value);
        }
        return result;
    }
}
