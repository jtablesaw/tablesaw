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
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.dates.DateColumnFormatter;
import tech.tablesaw.columns.dates.DateFilters;
import tech.tablesaw.columns.dates.DateMapUtils;
import tech.tablesaw.columns.dates.PackedLocalDate;
import tech.tablesaw.filtering.predicates.IntBiPredicate;
import tech.tablesaw.filtering.predicates.IntPredicate;
import tech.tablesaw.filtering.predicates.LocalDatePredicate;
import tech.tablesaw.io.TypeUtils;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;
import tech.tablesaw.sorting.comparators.DescendingIntComparator;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static tech.tablesaw.columns.DateAndTimePredicates.*;

/**
 * A column in a base table that contains float values
 */
public class DateColumn extends AbstractColumn implements DateFilters,
        DateMapUtils, CategoricalColumn, Iterable<LocalDate> {

    public static final int MISSING_VALUE = (Integer) ColumnType.LOCAL_DATE.getMissingValue();

    /**
     * locale for formatter
     */
    private final Locale locale;

    private final IntComparator reverseIntComparator = DescendingIntComparator.instance();

    private IntArrayList data;

    private final IntComparator comparator = (r1, r2) -> {
        int f1 = getIntInternal(r1);
        int f2 = getIntInternal(r2);
        return Integer.compare(f1, f2);
    };

    private DateColumnFormatter printFormatter = new DateColumnFormatter();

    /**
     * The formatter chosen to parse dates for this particular column
     */
    private DateTimeFormatter selectedFormatter;

    public static DateColumn create(String name) {
        return create(name, DEFAULT_ARRAY_SIZE, Locale.getDefault());
    }

    public static DateColumn create(String name, Locale locale) {
        return create(name, DEFAULT_ARRAY_SIZE, locale);
    }

    public static DateColumn create(String name, int initialSize, Locale locale) {
        return new DateColumn(name, new IntArrayList(initialSize), locale);
    }

    public static DateColumn create(String name, int initialSize) {
        return create(name, initialSize, Locale.getDefault());
    }

    public static DateColumn create(String name, List<LocalDate> data) {
        DateColumn column = new DateColumn(name, new IntArrayList(data.size()), Locale.getDefault());
        for (LocalDate date : data) {
            column.append(date);
        }
        return column;
    }

    public static DateColumn create(String name, LocalDate[] data) {
        DateColumn column = new DateColumn(name, new IntArrayList(data.length), Locale.getDefault());
        for (LocalDate date : data) {
            column.append(date);
        }
        return column;
    }

    private DateColumn(String name, IntArrayList data, Locale locale) {
        super(ColumnType.LOCAL_DATE, name);
        this.data = data;
        this.locale = locale;
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public ColumnType type() {
        return ColumnType.LOCAL_DATE;
    }

    public void appendInternal(int f) {
        data.add(f);
    }

    @Override
    public IntArrayList data() {
        return data;
    }

    public void set(int index, int value) {
        data.set(index, value);
    }

    public void set(int index, LocalDate value) {
        data.set(index, PackedLocalDate.pack(value));
    }

    public void append(LocalDate f) {
        appendInternal(PackedLocalDate.pack(f));
    }

    public void setPrintFormatter(DateTimeFormatter dateTimeFormatter, String missingValueString) {
        Preconditions.checkNotNull(dateTimeFormatter);
        Preconditions.checkNotNull(missingValueString);
        this.printFormatter = new DateColumnFormatter(dateTimeFormatter, missingValueString);
    }

    @Override
    public String getString(int row) {
        return printFormatter.format(getPackedDate(row));
    }

    @Override
    public String getUnformattedString(int row) {
        return PackedLocalDate.toDateString(getPackedDate(row));
    }

    @Override
    public DateColumn emptyCopy() {
        return emptyCopy(DEFAULT_ARRAY_SIZE);
    }

    @Override
    public DateColumn emptyCopy(int rowSize) {
        DateColumn copy = create(name(), rowSize, locale);
        copy.printFormatter = printFormatter;
        copy.selectedFormatter = selectedFormatter;
        return copy;
    }

    @Override
    public DateColumn copy() {
        DateColumn copy = emptyCopy(data.size());
        copy.data = data.clone();
        return copy;
    }

    @Override
    public void clear() {
        data.clear();
    }

    public DateColumn lead(int n) {
        DateColumn column = lag(-n);
        column.setName(name() + " lead(" + n + ")");
        return column;
    }

    public DateColumn lag(int n) {
        int srcPos = n >= 0 ? 0 : 0 - n;
        int[] dest = new int[size()];
        int destPos = n <= 0 ? 0 : n;
        int length = n >= 0 ? size() - n : size() + n;

        for (int i = 0; i < size(); i++) {
            dest[i] = MISSING_VALUE;
        }

        System.arraycopy(data.toIntArray(), srcPos, dest, destPos, length);

        DateColumn copy = emptyCopy(size());
        copy.data = new IntArrayList(dest);
        copy.setName(name() + " lag(" + n + ")");
        return copy;
    }

    @Override
    public void sortAscending() {
        Arrays.parallelSort(data.elements());
    }

    @Override
    public void sortDescending() {
        IntArrays.parallelQuickSort(data.elements(), reverseIntComparator);
    }

    @Override
    public int countUnique() {
        IntSet ints = new IntOpenHashSet(size());
        for (int i = 0; i < size(); i++) {
            ints.add(data.getInt(i));
        }
        return ints.size();
    }

    @Override
    public DateColumn unique() {
        IntSet ints = new IntOpenHashSet(data.size());
        for (int i = 0; i < size(); i++) {
            ints.add(data.getInt(i));
        }
        DateColumn copy = emptyCopy(ints.size());
        copy.setName(name() + " Unique values");
        copy.data = IntArrayList.wrap(ints.toIntArray());
        return copy;
    }

    public LocalDate firstElement() {
        if (isEmpty()) {
            return null;
        }
        return PackedLocalDate.asLocalDate(getPackedDate(0));
    }

    public LocalDate max() {
        if (isEmpty()) {
            return null;
        }

        Integer max = null;
        for (int aData : data) {
            if (DateColumn.MISSING_VALUE != aData) {
                if (max == null) {
                    max = aData;
                } else {
                    max = (max > aData) ? max : aData;
                }
            }
        }

        if (max == null) {
            return null;
        }
        return PackedLocalDate.asLocalDate(max);
    }

    public LocalDate min() {
        if (isEmpty()) {
            return null;
        }

        Integer min = null;
        for (int aData : data) {
            if (DateColumn.MISSING_VALUE != aData) {
                if (min == null) {
                    min = aData;
                } else {
                    min = (min < aData) ? min : aData;
                }
            }
        }
        if (min == null) {
            return null;
        }
        return PackedLocalDate.asLocalDate(min);
    }

    /**
     * Conditionally update this column, replacing current values with newValue for all rows where the current value
     * matches the selection criteria
     * <p>
     * Example:
     * myColumn.set(LocalDate.now(), myColumn.isMissing()); // no more missing values
     */
    public void set(LocalDate newValue, Selection rowSelection) {
        for (int row : rowSelection) {
            set(row, newValue);
        }
    }

    /**
     * Returns a StringColumn with the year and month from this column concatenated into a String that will sort
     * lexicographically in temporal order.
     * <p>
     * This simplifies the production of plots and tables that aggregate values into standard temporal units (e.g.,
     * you want monthly data but your source data is more than a year long and you don't want months from different
     * years aggregated together).
     */
    public StringColumn yearMonthString() {
        StringColumn newColumn = StringColumn.create(this.name() + " year & month");
        for (int r = 0; r < this.size(); r++) {
            int c1 = this.getPackedDate(r);
            if (c1 == MISSING_VALUE) {
                newColumn.append(StringColumn.MISSING_VALUE);
            } else {
                String ym = String.valueOf(PackedLocalDate.getYear(c1));
                ym = ym + "-" + Strings.padStart(
                        String.valueOf(PackedLocalDate.getMonthValue(c1)), 2, '0');
                newColumn.append(ym);
            }
        }
        return newColumn;
    }

    public LocalDate get(int index) {
        return PackedLocalDate.asLocalDate(getPackedDate(index));
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public IntComparator rowComparator() {
        return comparator;
    }

    /**
     * Returns a PackedDate as converted from the given string
     *
     * @param value A string representation of a date
     * @throws DateTimeParseException if no parser can be found for the date format
     */
    public int convert(String value) {
        if (Strings.isNullOrEmpty(value) || TypeUtils.MISSING_INDICATORS.contains(value) || value.equals("-1")) {
            return (Integer) ColumnType.LOCAL_DATE.getMissingValue();
        }
        String paddedValue = Strings.padStart(value, 4, '0');

        if (selectedFormatter == null) {
            selectedFormatter = TypeUtils.getDateFormatter(paddedValue).withLocale(locale);
        }
        LocalDate date;
        try {
            date = LocalDate.parse(paddedValue, selectedFormatter);
        } catch (DateTimeParseException e) {
            selectedFormatter = TypeUtils.DATE_FORMATTER.withLocale(locale);
            date = LocalDate.parse(paddedValue, selectedFormatter);
        }
        return PackedLocalDate.pack(date);
    }

    @Override
    public void appendCell(String string) {
        appendInternal(convert(string));
    }

    @Override
    public int getIntInternal(int index) {
        return data.getInt(index);
    }

    int getPackedDate(int index) {
        return getIntInternal(index);
    }

    /**
     * Returns a table of dates and the number of observations of those dates
     *
     * @return the summary table
     */
    @Override
    public Table summary() {

        Table table = Table.create("Column: " + name());
        StringColumn measure = StringColumn.create("Measure");
        StringColumn value = StringColumn.create("Value");
        table.addColumn(measure);
        table.addColumn(value);

        measure.append("Count");
        value.append(String.valueOf(size()));

        measure.append("Missing");
        value.append(String.valueOf(countMissing()));

        measure.append("Earliest");
        value.append(String.valueOf(min()));

        measure.append("Latest");
        value.append(String.valueOf(max()));

        return table;
    }

    public static boolean isMissing(int i) {
        return i == MISSING_VALUE;
    }

    @Override
    public Selection isMissing() {
        return eval(isMissing);
    }

    /**
     * Returns the count of missing values in this column
     */
    @Override
    public int countMissing() {
        int count = 0;
        for (int i = 0; i < size(); i++) {
            if (getPackedDate(i) == MISSING_VALUE) {
                count++;
            }
        }
        return count;
    }

    @Override
    public Selection isNotMissing() {
        return eval(isNotMissing);
    }

    @Override
    public void append(Column column) {
        Preconditions.checkArgument(column.type() == this.type());
        DateColumn doubleColumn = (DateColumn) column;
        for (int i = 0; i < doubleColumn.size(); i++) {
            appendInternal(doubleColumn.getPackedDate(i));
        }
    }

    /**
     * Returns a selection formed by applying the given predicate
     *
     * Prefer using an IntPredicate where the int is a PackedDate, as this version creates a date object
     * for each value in the column
     */
    public Selection eval(LocalDatePredicate predicate) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            int next = data.getInt(idx);
            if (predicate.test(get(next))) {
                selection.add(idx);
            }
        }
        return selection;
    }

    /**
     * Returns the largest ("top") n values in the column
     *
     * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
     *          number of observations in the column
     * @return A list, possibly empty, of the largest observations
     */
    public List<LocalDate> top(int n) {
        List<LocalDate> top = new ArrayList<>();
        int[] values = data.toIntArray();
        IntArrays.parallelQuickSort(values, DescendingIntComparator.instance());
        for (int i = 0; i < n && i < values.length; i++) {
            top.add(PackedLocalDate.asLocalDate(values[i]));
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
    public List<LocalDate> bottom(int n) {
        List<LocalDate> bottom = new ArrayList<>();
        int[] values = data.toIntArray();
        IntArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(PackedLocalDate.asLocalDate(values[i]));
        }
        return bottom;
    }

    public IntIterator intIterator() {
        return data.iterator();
    }

    public List<LocalDate> asList() {
        List<LocalDate> dates = new ArrayList<>(size());
        for (LocalDate localDate : this) {
            dates.add(localDate);
        }
        return dates;
    }

    @Override
    public DateColumn select(Selection selection) {
        return (DateColumn) subset(selection);
    }

    /**
     * This version operates on predicates that treat the given IntPredicate as operating on a packed local time
     * This is much more efficient that using a LocalTimePredicate, but requires that the developer understand the
     * semantics of packedLocalTimes
     */
    public Selection eval(IntPredicate predicate) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            int next = data.getInt(idx);
            if (predicate.test(next)) {
                selection.add(idx);
            }
        }
        return selection;
    }

    public Selection eval(IntBiPredicate predicate, int value) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            int next = data.getInt(idx);
            if (predicate.test(next, value)) {
                selection.add(idx);
            }
        }
        return selection;
    }

    public Selection eval(IntBiPredicate predicate, DateColumn otherColumn) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            if (predicate.test(getPackedDate(idx), otherColumn.getPackedDate(idx))) {
                selection.add(idx);
            }
        }
        return selection;
    }

    public Set<LocalDate> asSet() {
        Set<LocalDate> dates = new HashSet<>();
        DateColumn unique = unique();
        for (LocalDate d : unique) {
            dates.add(d);
        }
        return dates;
    }

    public boolean contains(LocalDate localDate) {
        int date = PackedLocalDate.pack(localDate);
        return data().contains(date);
    }


    @Override
    public double[] asDoubleArray() {
        double[] doubles = new double[size()];
        for (int i = 0; i < size(); i++) {
            doubles[i] = data.getInt(i);
        }
        return doubles;
    }

    @Override
    public double getDouble(int i) {
        return getIntInternal(i);
    }

    @Override
    public int byteSize() {
        return type().byteSize();
    }

    /**
     * Returns the contents of the cell at rowNumber as a byte[]
     *
     * @param rowNumber the number of the row as int
     */
    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(byteSize()).putInt(getPackedDate(rowNumber)).array();
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<LocalDate> iterator() {

        return new Iterator<LocalDate>() {

            final IntIterator intIterator = intIterator();

            @Override
            public boolean hasNext() {
                return intIterator.hasNext();
            }

            @Override
            public LocalDate next() {
                return PackedLocalDate.asLocalDate(intIterator.nextInt());
            }
        };
    }
}