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
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
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

import static tech.tablesaw.api.ColumnType.DOUBLE;

/**
 * A column in a base table that contains double precision floating point values
 */
public class DoubleColumn extends AbstractColumn<Double> implements NumberColumn {

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

    @Override
    public DoubleColumn removeMissing() {
        final DoubleColumn noMissing = emptyCopy();
        final DoubleIterator iterator = doubleIterator();
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
        return NumberColumn.valueIsMissing(getDouble(rowNumber));
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
        super(DOUBLE, name);
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
     *
     */
    @Override
    public DoubleColumn unique() {
        final DoubleSet doubles = new DoubleOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissing(i)) {
                doubles.add(data.getDouble(i));
            }
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
    public NumberColumn append(int i) {
        data.add(i);
        return this;
    }

    @Override
    public DoubleColumn appendObj(Object obj) {
        if (obj == null) {
            return appendMissing();
        }
        if (!(obj instanceof Double)) {
            throw new IllegalArgumentException("Cannot append " + obj.getClass().getName() + " to DoubleColumn");
        }
        return append((double) obj);
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
        return data.getDouble(row);
    }

    @Override
    public String getUnformattedString(final int row) {
        return String.valueOf(getDouble(row));
    }

    @Override
    public DoubleColumn emptyCopy() {
        return emptyCopy(DEFAULT_ARRAY_SIZE);
    }

    @Override
    public DoubleColumn append(Double val) {
        this.append(val.doubleValue());
        return this;
    }

    public DoubleColumn append(Integer val) {
        this.append(val.doubleValue());
        return this;
    }

    @Override
    public DoubleColumn emptyCopy(final int rowSize) {
        final DoubleColumn column = DoubleColumn.create(name(), rowSize);
        column.setPrintFormatter(printFormatter);
        column.locale = locale;
        return column;
    }

    @Override
    public DoubleColumn lead(final int n) {
        final DoubleColumn numberColumn = lag(-n);
        numberColumn.setName(name() + " lead(" + n + ")");
        return numberColumn;
    }

    @Override
    public DoubleColumn lag(final int n) {
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
    public DoubleColumn copy() {
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
            return append(DoubleColumnType.DEFAULT_PARSER.parseDouble(object));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException(name() + ": " + e.getMessage());
        }
    }

    @Override
    public DoubleColumn appendCell(final String object, StringParser<?> parser) {
        try {
            return append(parser.parseDouble(object));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException(name() + ": " + e.getMessage());
        }
    }

    /**
     * Returns the rounded value as an int
     *
     * @throws ClassCastException if the returned value will not fit in an int
     */
    @Override
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
    public Double get(final int index) {
        return data.getDouble(index);
    }

    @Override
    public DoubleColumn set(final int r, final double value) {
        data.set(r, value);
        return this;
    }

    @Override
    public DoubleColumn set(int i, Double val) {
        return set(i, (double) val);
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
    public DoubleColumn append(final Column<Double> column) {
        Preconditions.checkArgument(column.type() == this.type());
        final DoubleColumn numberColumn =  (DoubleColumn) column;
        for (int i = 0; i < numberColumn.size(); i++) {
            append(numberColumn.getDouble(i));
        }
        return this;
    }

    @Override
    public DoubleIterator doubleIterator() {
        return data.iterator();
    }

    @Override
    public Iterator<Double> iterator() {
        return data.iterator();
    }

    @Override
    public DoubleColumn where(final Selection selection) {
        return (DoubleColumn) subset(selection);
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

    public IntSet asIntegerSet() {
        final IntSet ints = new IntOpenHashSet();
        DoubleIterator it = doubleIterator();
        while (it.hasNext()) {
            double d = it.nextDouble();
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

    @Override
    public DateTimeColumn asDateTimes(ZoneOffset offset) {
        DateTimeColumn column = DateTimeColumn.create(name() + ": date time");
        DoubleIterator it = doubleIterator();
        while (it.hasNext()) {
            double d = it.nextDouble();
            LocalDateTime dateTime =
                    Instant.ofEpochMilli((long) d).atZone(offset).toLocalDateTime();
            column.append(dateTime);
        }
        return column;
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

    @Override
    public Object[] asObjectArray() {
        final Double[] output = new Double[data.size()];
        for (int i = 0; i < data.size(); i++) {
            output[i] = data.getDouble(i);
        }
        return output;
    }

    @Override
    public int compare(Double o1, Double o2) {
        return Double.compare(o1, o2);
    }
    
    // functional methods corresponding to those in Stream

    /**
     * Counts the number of rows satisfying predicate, but only upto the max value
     * @param test the predicate
     * @param max the maximum number of rows to count
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
     * Counts the number of rows satisfying predicate
     * @param test the predicate
     * @return the number of rows satisfying the predicate
     */
    public int count(DoublePredicate test) {
        return count(test, size());
    }

    /**
     * Returns true if all rows satisfy the predicate, false otherwise
     * @param test the predicate
     * @return true if all rows satisfy the predicate, false otherwise
     */
   public boolean allMatch(DoublePredicate test) {
        return count(test.negate(), 1) == 0;
    }

   /**
    * Returns true if any row satisfies the predicate, false otherwise
    * @param test the predicate
    * @return true if any rows satisfies the predicate, false otherwise
    */
    public boolean anyMatch(DoublePredicate test) {
        return count(test, 1) > 0;
    }

    /**
     * Returns true if no row satisfies the predicate, false otherwise
     * @param test the predicate
     * @return true if no row satisfies the predicate, false otherwise
     */
    public boolean noneMatch(DoublePredicate test) {
        return count(test, 1) == 0;
    }

    /**
     * Returns a new DoubleColumn with only those rows satisfying the predicate
     * @param test the predicate
     * @return a new DoubleColumn with only those rows satisfying the predicate
     */
    public DoubleColumn filter(DoublePredicate test) {
        DoubleColumn result = DoubleColumn.create(name());
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
     * @param fun function to map
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
     * Maps the function across all rows, appending the results to a new DoubleColumn
     * @param fun function to map
     * @return the DoubleColumn with the results
     */
    public DoubleColumn map(ToDoubleFunction<Double> fun) {
        DoubleColumn result = DoubleColumn.create(name());
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
     * @param initial initial value
     * @param op the operator
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

}
