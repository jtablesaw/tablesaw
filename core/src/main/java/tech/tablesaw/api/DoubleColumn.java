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
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleOpenHashSet;
import it.unimi.dsi.fastutil.doubles.DoubleRBTreeSet;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.columns.numbers.Stats;
import tech.tablesaw.filtering.Filter;
import tech.tablesaw.filtering.predicates.DoubleBiPredicate;
import tech.tablesaw.filtering.predicates.DoubleRangePredicate;
import tech.tablesaw.io.TypeUtils;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.BiPredicate;
import java.util.function.DoubleConsumer;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static tech.tablesaw.api.ColumnType.NUMBER;

/**
 * A column in a base table that contains double precision floating point values
 */
public class DoubleColumn extends AbstractColumn implements NumberColumn {

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    /**
     * Compares two doubles, such that a sort based on this comparator would sort in descending order
     */
    private final DoubleComparator descendingComparator = (o2, o1) -> (Double.compare(o1, o2));

    private DoubleArrayList data;

    private NumberColumnFormatter printFormatter = new NumberColumnFormatter();

    private Locale locale;

    private final IntComparator comparator = new IntComparator() {

        @Override
        public int compare(final int r1, final int r2) {
            final double f1 = data.getDouble(r1);
            final double f2 = data.getDouble(r2);
            return Double.compare(f1, f2);
        }
    };

    public static DoubleColumn create(final String name, final int initialSize) {
        return new DoubleColumn(name, new DoubleArrayList(initialSize));
    }

    public static DoubleColumn create(final String name, final double[] arr) {
        return new DoubleColumn(name, new DoubleArrayList(arr));
    }

    public static DoubleColumn create(final String name, final float[] arr) {
        final double[] doubles = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            doubles[i] = arr[i];
        }
        return new DoubleColumn(name, new DoubleArrayList(doubles));
    }

    public static DoubleColumn create(final String name, final int[] arr) {
        final double[] doubles = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            doubles[i] = arr[i];
        }
        return new DoubleColumn(name, new DoubleArrayList(doubles));
    }

    public static DoubleColumn create(final String name, final long[] arr) {
        final double[] doubles = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            doubles[i] = arr[i];
        }
        return new DoubleColumn(name, new DoubleArrayList(doubles));
    }

    public static DoubleColumn create(final String name, final List<Number> numberList) {
        final double[] doubles = new double[numberList.size()];
        for (int i = 0; i < numberList.size(); i++) {
            doubles[i] = numberList.get(i).doubleValue();
        }
        return new DoubleColumn(name, new DoubleArrayList(doubles));
    }

    public static DoubleColumn create(final String name, final Number[] numbers) {
        final double[] doubles = new double[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            doubles[i] = numbers[i].doubleValue();
        }
        return new DoubleColumn(name, new DoubleArrayList(doubles));
    }

    /**
     * Returns a double that is parsed from the given String
     * <p>
     * We remove any commas before parsing
     */
    public static double convert(final String stringValue) {
        if (Strings.isNullOrEmpty(stringValue) || TypeUtils.MISSING_INDICATORS.contains(stringValue)) {
            return MISSING_VALUE;
        }
        final Matcher matcher = DoubleColumn.COMMA_PATTERN.matcher(stringValue);
        return Double.parseDouble(matcher.replaceAll(""));
    }

    @Override
    public DoubleColumn removeMissing() {
        final DoubleColumn noMissing = (DoubleColumn) emptyCopy();
        final DoubleIterator iterator = iterator();
        while(iterator.hasNext()) {
            final double v = iterator.nextDouble();
            if (!NumberColumn.valueIsMissing(v)) {
                noMissing.append(v);
            }
        }
        return noMissing;
    }

    /**
     * Returns a new numeric column initialized with the given name and size. The values in the column are
     * integers beginning at startsWith and continuing through size (exclusive), monotonically increasing by 1
     * TODO consider a generic fill function including steps or random samples from various distributions
     */
    public static DoubleColumn indexColumn(final String columnName, final int size, final int startsWith) {
        final DoubleColumn indexColumn = DoubleColumn.create(columnName, size);
        for (int i = 0; i < size; i++) {
            indexColumn.append(i + startsWith);
        }
        indexColumn.setPrintFormatter(NumberColumnFormatter.ints());
        return indexColumn;
    }

    public static DoubleColumn create(final String columnName) {
        return create(columnName, DEFAULT_ARRAY_SIZE);
    }

    @Override
    public boolean isMissing(final int rowNumber) {
        return NumberColumn.valueIsMissing(get(rowNumber));
    }

    @Override
    public void setPrintFormatter(final NumberFormat format, final String missingValueString) {
        this.printFormatter = new NumberColumnFormatter(format, missingValueString);
    }

    @Override
    public DoubleColumn appendMissing() {
        append(MISSING_VALUE);
        return this;
    }

    @Override
    public void setPrintFormatter(final NumberColumnFormatter formatter) {
        this.printFormatter = formatter;
    }

    private DoubleColumn(final String name, final DoubleArrayList data) {
        super(NUMBER, name);
        this.data = data;
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public Table summary() {
        return stats().asTable();
    }

    @Override
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
    @Override
    public DoubleArrayList top(final int n) {
        final DoubleArrayList top = new DoubleArrayList();
        final double[] values = data.toDoubleArray();
        DoubleArrays.parallelQuickSort(values, descendingComparator);
        for (int i = 0; i < n && i < values.length; i++) {
            top.add(values[i]);
        }
        return top;
    }

    /**
     * Returns the smallest ("bottom") n values in the column
     * TODO(lwhite): Consider whether this should exclude missing
     *
     * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
     *          number of observations in the column
     * @return A list, possibly empty, of the smallest n observations
     */
    @Override
    public DoubleArrayList bottom(final int n) {
        final DoubleArrayList bottom = new DoubleArrayList();
        final double[] values = data.toDoubleArray();
        DoubleArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(values[i]);
        }
        return bottom;
    }

    /**
     * TODO(lwhite): Ensure proper handling of missing values. They should not end up in the result set
     */
    @Override
    public Column unique() {
        final DoubleSet doubles = new DoubleOpenHashSet();
        for (int i = 0; i < size(); i++) {
            doubles.add(data.getDouble(i));
        }
        final DoubleColumn column = DoubleColumn.create(name() + " Unique values", doubles.size());
        doubles.forEach((DoubleConsumer) column::append);
        return column;
    }

    @Override
    public double firstElement() {
        if (size() > 0) {
            return data.getDouble(0);
        }
        return MISSING_VALUE;
    }

    /**
     * Adds the given float to this column
     */
    @Override
    public DoubleColumn append(final float f) {
        data.add(f);
        return this;
    }

    /**
     * Adds the given double to this column
     */
    @Override
    public DoubleColumn append(double d) {
        data.add(d);
        return this;
    }


    @Override
    public String getString(final int row) {
        final double value = data.getDouble(row);
        if (NumberColumn.valueIsMissing(value)) {
            return "";
        }
        return String.valueOf(printFormatter.format(value));
    }

    @Override
    public double getDouble(final int row) {
        return get(row);
    }

    @Override
    public String getUnformattedString(final int row) {
        return String.valueOf(get(row));
    }

    @Override
    public NumberColumn emptyCopy() {
        return emptyCopy(DEFAULT_ARRAY_SIZE);
    }

    @Override
    public DoubleColumn emptyCopy(final int rowSize) {
        final DoubleColumn column = DoubleColumn.create(name(), rowSize);
        column.setPrintFormatter(printFormatter);
        column.locale = locale;
        return column;
    }

    @Override
    public NumberColumn lead(final int n) {
        final NumberColumn numberColumn = lag(-n);
        numberColumn.setName(name() + " lead(" + n + ")");
        return numberColumn;
    }

    @Override
    public NumberColumn lag(final int n) {
        final int srcPos = n >= 0 ? 0 : 0 - n;
        final double[] dest = new double[size()];
        final int destPos = n <= 0 ? 0 : n;
        final int length = n >= 0 ? size() - n : size() + n;

        for (int i = 0; i < size(); i++) {
            dest[i] = MISSING_VALUE;
        }

        System.arraycopy(data.toDoubleArray(), srcPos, dest, destPos, length);

        final DoubleColumn copy = emptyCopy(size());
        copy.data = new DoubleArrayList(dest);
        copy.setName(name() + " lag(" + n + ")");
        return copy;
    }

    @Override
    public NumberColumn copy() {
        final DoubleColumn column = emptyCopy(size());
        column.data = data.clone();
        return column;
    }

    @Override
    public void clear() {
        data = new DoubleArrayList(DEFAULT_ARRAY_SIZE);
    }

    @Override
    public void sortAscending() {
        Arrays.parallelSort(data.elements());
    }

    @Override
    public void sortDescending() {
        DoubleArrays.parallelQuickSort(data.elements(), descendingComparator);
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public DoubleColumn appendCell(final String object) {
        try {
            append(convert(object));
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
    @Override
    public Integer roundInt(final int i) {
        final double value = get(i);
        if (NumberColumn.valueIsMissing(value)) {
            return null;
        }
        return (int) Math.round(get(i));
    }

    /**
     * Returns the value of the ith element rounded to the nearest long
     *
     * @param i the index in the column
     * @return the value at i, rounded to the nearest integer
     */
    @Override
    public long getLong(final int i) {
        final double value = data.getDouble(i);
        return NumberColumn.valueIsMissing(value) ? DateTimeColumn.MISSING_VALUE : Math.round(value);
    }

    /**
     * Compares the given ints, which refer to the indexes of the doubles in this column, according to the values of the
     * doubles themselves
     */
    @Override
    public IntComparator rowComparator() {
        return comparator;
    }

    @Override
    public double get(final int index) {
        return data.getDouble(index);
    }

    @Override
    public DoubleColumn set(final int r, final double value) {
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
    @Override
    public DoubleColumn set(final Selection rowSelection, final double newValue) {
        for (final int row : rowSelection) {
            set(row, newValue);
        }
        return this;
    }

    @Override
    public double[] asDoubleArray() {
        final double[] output = new double[data.size()];
        for (int i = 0; i < data.size(); i++) {
            output[i] = data.getDouble(i);
        }
        return output;
    }

    @Override
    public void append(final Column column) {
        Preconditions.checkArgument(column.type() == this.type());
        final NumberColumn numberColumn = (NumberColumn) column;
        for (int i = 0; i < numberColumn.size(); i++) {
            append(numberColumn.get(i));
        }
    }

    @Override
    public DoubleIterator iterator() {
        return data.iterator();
    }

    @Override
    public NumberColumn where(final Filter filter) {
        return (NumberColumn) subset(filter.apply(this));
    }

    @Override
    public NumberColumn where(final Selection selection) {
        return (NumberColumn) subset(selection);
    }

    @Override
    public Selection eval(final DoublePredicate predicate) {
        final Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            final double next = data.getDouble(idx);
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
            if (predicate.test(get(idx), otherColumn.get(idx))) {
                selection.add(idx);
            }
        }
        return selection;
    }

    @Override
    public Selection eval(final DoubleBiPredicate predicate, final Number number) {
        final double value = number.doubleValue();
        final Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            final double next = data.getDouble(idx);
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
        for (int idx = 0; idx < data.size(); idx++) {
            final double next = data.getDouble(idx);
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
        for (int idx = 0; idx < data.size(); idx++) {
            final double next = data.getDouble(idx);
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
            if (doubleSet.contains(get(i))) {
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

    @Override
    public DoubleSet asSet() {
        return new DoubleOpenHashSet(data);
    }

    @Override
    public boolean contains(final double value) {
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
        return ByteBuffer.allocate(byteSize()).putDouble(get(rowNumber)).array();
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
    public IntSet asIntegerSet() {
        final IntSet ints = new IntOpenHashSet();
        for (final double d : this) {
            if (!NumberColumn.valueIsMissing(d)) {
                ints.add((int) Math.round(d));
            }
        }
        return ints;
    }

    @Override
    public DoubleList dataInternal() {
        return data.clone();
    }

    // fillWith methods

    @Override
    public DoubleColumn fillWith(final DoubleIterator iterator) {
        for (int r = 0; r < size(); r++) {
            if (!iterator.hasNext()) {
                break;
            }
            set(r, iterator.nextDouble());
        }
        return this;
    }

    @Override
    public DoubleColumn fillWith(final DoubleIterable iterable) {
        DoubleIterator iterator = null;
        for (int r = 0; r < size(); r++) {
            if (iterator == null || (!iterator.hasNext())) {
                iterator = iterable.iterator();
                if (!iterator.hasNext()) {
                    break;
                }
            }
            set(r, iterator.nextDouble());
        }
        return this;
    }

    @Override
    public DoubleColumn fillWith(final DoubleSupplier supplier) {
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
