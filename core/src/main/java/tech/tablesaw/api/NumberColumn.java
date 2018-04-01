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
import tech.tablesaw.columns.number.NumberColumnFormatter;
import tech.tablesaw.columns.number.NumberFilters;
import tech.tablesaw.columns.number.NumberMapUtils;
import tech.tablesaw.columns.number.NumberReduceUtils;
import tech.tablesaw.columns.number.Stats;
import tech.tablesaw.filtering.Filter;
import tech.tablesaw.filtering.predicates.DoubleBiPredicate;
import tech.tablesaw.filtering.predicates.DoublePredicate;
import tech.tablesaw.filtering.predicates.DoubleRangePredicate;
import tech.tablesaw.io.TypeUtils;
import tech.tablesaw.util.selection.BitmapBackedSelection;
import tech.tablesaw.util.selection.Selection;

import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.DoubleConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static tech.tablesaw.api.ColumnType.*;

/**
 * A column in a base table that contains double precision floating point values
 */
public class NumberColumn extends AbstractColumn implements DoubleIterable, IntConvertibleColumn,
        NumberMapUtils, NumberReduceUtils, NumberFilters, CategoricalColumn {

    public static final double MISSING_VALUE = (Double) NUMBER.getMissingValue();

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    /**
     * Compares two doubles, such that a sort based on this comparator would sort in descending order
     */
    private final DoubleComparator descendingComparator = (o2, o1) -> (Double.compare(o1, o2));

    private DoubleArrayList data;

    private NumberColumnFormatter printFormatter = new NumberColumnFormatter();

    private Locale locale;

    private final IntComparator comparator = new IntComparator() {

        public int compare(int r1, int r2) {
            double f1 = data.getDouble(r1);
            double f2 = data.getDouble(r2);
            return Double.compare(f1, f2);
        }
    };

    public static boolean isMissing(double value) {
        return Double.isNaN(value);
    }

    public void setPrintFormatter(NumberFormat format, String missingValueString) {
        this.printFormatter = new NumberColumnFormatter(format, missingValueString);
    }

    public void setPrintFormatter(NumberColumnFormatter formatter) {
        this.printFormatter = formatter;
    }

    public static NumberColumn create(String name) {
        return create(name, DEFAULT_ARRAY_SIZE);
    }

    public static NumberColumn create(String name, int initialSize) {
        return new NumberColumn(name, new DoubleArrayList(initialSize));
    }

    public static NumberColumn create(String name, double[] arr) {
        return new NumberColumn(name, new DoubleArrayList(arr));
    }

    public static NumberColumn create(String name, float[] arr) {
        double[] doubles = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            doubles[i] = arr[i];
        }
        return new NumberColumn(name, new DoubleArrayList(doubles));
    }

    public static NumberColumn create(String name, int[] arr) {
        double[] doubles = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            doubles[i] = arr[i];
        }
        return new NumberColumn(name, new DoubleArrayList(doubles));
    }

    public static NumberColumn create(String name, long[] arr) {
        double[] doubles = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            doubles[i] = arr[i];
        }
        return new NumberColumn(name, new DoubleArrayList(doubles));
    }

    public static NumberColumn create(String name, List<Number> numberList) {
        double[] doubles = new double[numberList.size()];
        for (int i = 0; i < numberList.size(); i++) {
            doubles[i] = numberList.get(i).doubleValue();
        }
        return new NumberColumn(name, new DoubleArrayList(doubles));
    }

    public static NumberColumn create(String name, Number[] numbers) {
        double[] doubles = new double[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            doubles[i] = numbers[i].doubleValue();
        }
        return new NumberColumn(name, new DoubleArrayList(doubles));
    }

    /**
     * Returns a new numeric column initialized with the given name and size. The values in the column are
     * integers beginning at startsWith and continuing through size (exclusive), monotonically increasing by 1
     * TODO consider a generic fill funciton including steps or random samples from various distributions
     */
    public static NumberColumn indexColumn(String columnName, int size, int startsWith) {
        NumberColumn indexColumn = NumberColumn.create(columnName, size);
        for (int i = 0; i < size; i++) {
            indexColumn.append(i + startsWith);
        }
        indexColumn.setPrintFormatter(NumberColumnFormatter.ints());
        return indexColumn;
    }

    private NumberColumn(String name, DoubleArrayList data) {
        super(NUMBER, name);
        this.data = data;
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

    /**
     * Returns the largest ("top") n values in the column
     * TODO(lwhite): Consider whether this should exclude missing
     *
     * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
     *          number of observations in the column
     * @return A list, possibly empty, of the largest observations
     */
    public DoubleArrayList top(int n) {
        DoubleArrayList top = new DoubleArrayList();
        double[] values = data.toDoubleArray();
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
    public DoubleArrayList bottom(int n) {
        DoubleArrayList bottom = new DoubleArrayList();
        double[] values = data.toDoubleArray();
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
        DoubleSet doubles = new DoubleOpenHashSet();
        for (int i = 0; i < size(); i++) {
            doubles.add(data.getDouble(i));
        }
        NumberColumn column = NumberColumn.create(name() + " Unique values", doubles.size());
        doubles.forEach((DoubleConsumer) column::append);
        return column;
    }

    public DoubleArrayList data() {
        return data;
    }

    public double firstElement() {
        if (size() > 0) {
            return data.getDouble(0);
        }
        return MISSING_VALUE;
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


    @Override
    public String getString(int row) {
        double value = data.getDouble(row);
        if (isMissing(value)) {
            return "";
        }
        return String.valueOf(printFormatter.format(value));
    }

    @Override
    public String getUnformattedString(int row) {
        return String.valueOf(get(row));
    }

    @Override
    public NumberColumn emptyCopy() {
        return emptyCopy(DEFAULT_ARRAY_SIZE);
    }

    @Override
    public NumberColumn emptyCopy(int rowSize) {
        NumberColumn column = NumberColumn.create(name(), rowSize);
        column.setPrintFormatter(printFormatter);
        column.locale = locale;
        return column;
    }

    public NumberColumn lead(int n) {
        NumberColumn numberColumn = lag(-n);
        numberColumn.setName(name() + " lead(" + n + ")");
        return numberColumn;
    }

    public NumberColumn lag(int n) {
        int srcPos = n >= 0 ? 0 : 0 - n;
        double[] dest = new double[size()];
        int destPos = n <= 0 ? 0 : n;
        int length = n >= 0 ? size() - n : size() + n;

        for (int i = 0; i < size(); i++) {
            dest[i] = MISSING_VALUE;
        }

        System.arraycopy(data.toDoubleArray(), srcPos, dest, destPos, length);

        NumberColumn copy = emptyCopy(size());
        copy.data = new DoubleArrayList(dest);
        copy.setName(name() + " lag(" + n + ")");
        return copy;
    }

    @Override
    public NumberColumn copy() {
        NumberColumn column = emptyCopy(size());
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
    public void appendCell(String object) {
        try {
            append(convert(object));
        } catch (NumberFormatException e) {
            throw new NumberFormatException(name() + ": " + e.getMessage());
        }
    }

    /**
     * Returns the rounded value as an int
     *
     * @throws ClassCastException if the returned value will not fit in an int
     */
    public Integer roundInt(int i) {
        double value = get(i);
        if (isMissing(value)) {
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
    public long getLong(int i) {
        double value = data.getDouble(i);
        return isMissing(value) ? DateTimeColumn.MISSING_VALUE : Math.round(value);
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

    public void set(int r, double value) {
        data.set(r, value);
    }

    /**
     * Conditionally update this column, replacing current values with newValue for all rows where the current value
     * matches the selection criteria
     * <p>
     * Example:
     * myColumn.set(4.0, myColumn.isMissing()); // no more missing values
     */
    public void set(Selection rowSelection, double newValue) {
        for (int row : rowSelection) {
            set(row, newValue);
        }
    }

    public double[] asDoubleArray() {
        double[] output = new double[data.size()];
        for (int i = 0; i < data.size(); i++) {
            output[i] = data.getDouble(i);
        }
        return output;
    }

    @Override
    public void append(Column column) {
        Preconditions.checkArgument(column.type() == this.type());
        NumberColumn numberColumn = (NumberColumn) column;
        for (int i = 0; i < numberColumn.size(); i++) {
            append(numberColumn.get(i));
        }
    }

    @Override
    public DoubleIterator iterator() {
        return data.iterator();
    }

    @Override
    public NumberColumn select(Filter filter) {
        return (NumberColumn) subset(filter.apply(this));
    }

    public NumberColumn select(Selection selection) {
        return (NumberColumn) subset(selection);
    }

    public Selection eval(DoublePredicate predicate) {
        Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            double next = data.getDouble(idx);
            if (predicate.test(next)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }

    public Selection eval(DoubleBiPredicate predicate, NumberColumn otherColumn) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            if (predicate.test(get(idx), otherColumn.get(idx))) {
                selection.add(idx);
            }
        }
        return selection;
    }

    public Selection eval(DoubleBiPredicate predicate, double value) {
        Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            double next = data.getDouble(idx);
            if (predicate.test(next, value)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }

    public Selection eval(DoubleRangePredicate predicate, double rangeStart, double rangeEnd) {
        Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            double next = data.getDouble(idx);
            if (predicate.test(next, rangeStart, rangeEnd)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }

    @Override
    public Selection isIn(double... doubles) {
        return selectIsIn(doubles);
    }

    private Selection selectIsIn(double... doubles) {
        Selection results = new BitmapBackedSelection();
        DoubleRBTreeSet doubleSet = new DoubleRBTreeSet(doubles);
        for (int i = 0; i < size(); i++) {
            if (doubleSet.contains(get(i))) {
                results.add(i);
            }
        }
        return results;
    }

    @Override
    public Selection isNotIn(double... doubles) {
        Selection results = new BitmapBackedSelection();
        results.addRange(0, size());
        results.andNot(selectIsIn(doubles));
        return results;
    }

    public DoubleSet asSet() {
        return new DoubleOpenHashSet(data);
    }

    public boolean contains(double value) {
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
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(byteSize()).putDouble(get(rowNumber)).array();
    }

    @Override
    public int[] asIntArray() {  // TODO: Need to figure out how to handle NaN -> Maybe just use a list with nulls?
        int[] result = new int[size()];
        for (int i = 0; i < size(); i++) {
            result[i] = roundInt(i);
        }
        return result;
    }

    @Override
    public IntSet asIntegerSet() {
        IntSet ints = new IntOpenHashSet();
        for (double d : this) {
            if (!isMissing(d)) {
                ints.add((int) Math.round(d));
            }
        }
        return ints;
    }

    @Override
    public DoubleList dataInternal() {
        return data.clone();
    }

}
