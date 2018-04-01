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
import tech.tablesaw.columns.times.PackedLocalTime;
import tech.tablesaw.columns.times.TimeColumnFormatter;
import tech.tablesaw.columns.times.TimeFilters;
import tech.tablesaw.columns.times.TimeMapUtils;
import tech.tablesaw.filtering.Filter;
import tech.tablesaw.filtering.predicates.IntBiPredicate;
import tech.tablesaw.filtering.predicates.IntPredicate;
import tech.tablesaw.filtering.predicates.LocalTimePredicate;
import tech.tablesaw.io.TypeUtils;
import tech.tablesaw.sorting.comparators.DescendingIntComparator;
import tech.tablesaw.util.selection.BitmapBackedSelection;
import tech.tablesaw.util.selection.Selection;

import java.nio.ByteBuffer;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static tech.tablesaw.api.ColumnType.*;
import static tech.tablesaw.columns.DateAndTimePredicates.*;

/**
 * A column in a base table that contains float values
 */
public class TimeColumn extends AbstractColumn implements Iterable<LocalTime>, TimeFilters, TimeMapUtils {

    public static final int MISSING_VALUE = (Integer) LOCAL_TIME.getMissingValue();

    /**
     * locale for formatter
     */
    private Locale locale;

    private final IntComparator descendingIntComparator = DescendingIntComparator.instance();
    /**
     * The formatter chosen to parse times for this particular column
     */
    private DateTimeFormatter selectedFormatter;

    private TimeColumnFormatter printFormatter = new TimeColumnFormatter();

    private IntArrayList data;

    private final IntComparator comparator = (r1, r2) -> {
        int f1 = getIntInternal(r1);
        int f2 = getIntInternal(r2);
        return Integer.compare(f1, f2);
    };

    private TimeColumn(String name, IntArrayList times, Locale locale) {
        super(LOCAL_TIME, name);
        data = times;
        this.locale = locale;
    }

    public static boolean isMissing(int i) {
        return i == MISSING_VALUE;
    }

    public static TimeColumn create(String name) {
        return create(name, DEFAULT_ARRAY_SIZE);
    }

    public static TimeColumn create(String name, List<LocalTime> data) {
        TimeColumn column = new TimeColumn(name, new IntArrayList(data.size()), Locale.getDefault());
        for (LocalTime time : data) {
            column.append(time);
        }
        return column;
    }

    public static TimeColumn create(String name, LocalTime[] data) {
        TimeColumn column = new TimeColumn(name, new IntArrayList(data.length), Locale.getDefault());
        for (LocalTime time : data) {
            column.append(time);
        }
        return column;
    }

    public static TimeColumn create(String name, int initialSize) {
        return create(name, initialSize, Locale.getDefault());
    }

    public static TimeColumn create(String name, int initialSize, Locale locale) {
        return new TimeColumn(name, new IntArrayList(initialSize), locale);
    }

    public TimeColumn lag(int n) {
        int srcPos = n >= 0 ? 0 : 0 - n;
        int[] dest = new int[size()];
        int destPos = n <= 0 ? 0 : n;
        int length = n >= 0 ? size() - n : size() + n;

        for (int i = 0; i < size(); i++) {
            dest[i] = MISSING_VALUE;
        }

        System.arraycopy(data.toIntArray(), srcPos, dest, destPos, length);

        TimeColumn copy = emptyCopy(size());
        copy.data = new IntArrayList(dest);
        copy.setName(name() + " lag(" + n + ")");
        return copy;
    }

    public int size() {
        return data.size();
    }

    public void appendInternal(int f) {
        data.add(f);
    }

    public void append(LocalTime time) {
        int value;
        if (time == null) {
            value = MISSING_VALUE;
        } else {
            value = PackedLocalTime.pack(time);
        }
        appendInternal(value);
    }

    @Override
    public ColumnType type() {
        return LOCAL_TIME;
    }

    @Override
    public String getString(int row) {
        return printFormatter.format(getPackedTime(row));
    }

    @Override
    public String getUnformattedString(int row) {
        return PackedLocalTime.toShortTimeString(getPackedTime(row));
    }

    public void setPrintFormatter(DateTimeFormatter dateTimeFormatter, String missingValueString) {
        Preconditions.checkNotNull(dateTimeFormatter);
        Preconditions.checkNotNull(missingValueString);
        this.printFormatter = new TimeColumnFormatter(dateTimeFormatter, missingValueString);
    }

    @Override
    public TimeColumn emptyCopy() {
        return emptyCopy(DEFAULT_ARRAY_SIZE);
    }

    @Override
    public TimeColumn emptyCopy(int rowSize) {
        TimeColumn column = TimeColumn.create(name(), rowSize, locale);
        column.printFormatter = printFormatter;
        column.selectedFormatter = selectedFormatter;
        return column;
    }

    @Override
    public TimeColumn copy() {
        TimeColumn column = emptyCopy(size());
        column.data = data.clone();
        return column;
    }

    @Override
    public void clear() {
        data.clear();
    }

    /**
     * Returns the entire contents of this column as a list
     */
    public List<LocalTime> asList() {
        List<LocalTime> times = new ArrayList<>();
        for (LocalTime time : this) {
            times.add(time);
        }
        return times;
    }

    @Override
    public void sortAscending() {
        int[] sorted = data.toIntArray();
        Arrays.parallelSort(sorted);
        this.data = new IntArrayList(sorted);
    }

    @Override
    public void sortDescending() {
        IntArrays.parallelQuickSort(data.elements(), descendingIntComparator);
    }

    public LocalTime max() {

        if (isEmpty()) {
            return null;
        }
        int max = getIntInternal(0);

        for (int aData : data) {
            max = (max > aData) ? max : aData;
        }

        if (max == MISSING_VALUE) {
            return null;
        }
        return PackedLocalTime.asLocalTime(max);
    }

    public LocalTime min() {

        if (isEmpty()) {
            return null;
        }

        int min = Integer.MAX_VALUE;

        for (int aData : data) {
            if (aData != MISSING_VALUE) {
                min = (min < aData) ? min : aData;
            }
        }
        if (min == Integer.MAX_VALUE) {
            return null;
        }
        return PackedLocalTime.asLocalTime(min);
    }

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

    /**
     * Returns the count of missing values in this column
     */
    @Override
    public int countMissing() {
        int count = 0;
        for (int i = 0; i < size(); i++) {
            if (getIntInternal(i) == MISSING_VALUE) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int countUnique() {
        IntOpenHashSet hashSet = new IntOpenHashSet(data);
        hashSet.remove(MISSING_VALUE);
        return hashSet.size();
    }

    @Override
    public TimeColumn unique() {
        IntSet ints = new IntOpenHashSet(data);
        TimeColumn column = emptyCopy(ints.size());
        column.data = IntArrayList.wrap(ints.toIntArray());
        column.setName(name() + " Unique values");
        return column;
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    /**
     * Returns a PackedTime as converted from the given string
     *
     * @param value A string representation of a time
     * @throws DateTimeParseException if no parser can be found for the time format used
     */
    public int convert(String value) {
        if (Strings.isNullOrEmpty(value)
                || TypeUtils.MISSING_INDICATORS.contains(value)
                || value.equals("-1")) {
            return MISSING_VALUE;
        }
        value = Strings.padStart(value, 4, '0');
        if (selectedFormatter == null) {
            selectedFormatter = TypeUtils.getTimeFormatter(value);
        }
        LocalTime time;
        try {
            time = LocalTime.parse(value, selectedFormatter);
        } catch (DateTimeParseException e) {
            selectedFormatter = TypeUtils.TIME_FORMATTER;
            time = LocalTime.parse(value, selectedFormatter);
        }
        return PackedLocalTime.pack(time);
    }

    @Override
    public void appendCell(String object) {
        appendInternal(convert(object));
    }

    @Override
    public int getIntInternal(int index) {
        return data.getInt(index);
    }

    public int getPackedTime(int index) {
        return getIntInternal(index);
    }

    public LocalTime get(int index) {
        return PackedLocalTime.asLocalTime(getIntInternal(index));
    }

    @Override
    public IntComparator rowComparator() {
        return comparator;
    }

    public Selection isNotEqualTo(LocalTime value) {
        Selection results = new BitmapBackedSelection();
        int packedLocalTime = PackedLocalTime.pack(value);
        int i = 0;
        for (int next : data) {
            if (packedLocalTime != next) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    public Selection isEqualTo(LocalTime value) {
        Selection results = new BitmapBackedSelection();
        int packedLocalTime = PackedLocalTime.pack(value);
        int i = 0;
        for (int next : data) {
            if (packedLocalTime == next) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    public IntArrayList data() {
        return data;
    }

    @Override
    public String toString() {
        return "LocalTime column: " + name();
    }

    public TimeColumn selectIf(LocalTimePredicate predicate) {
        TimeColumn column = emptyCopy();
        IntIterator iterator = intIterator();
        while (iterator.hasNext()) {
            int next = iterator.nextInt();
            if (predicate.test(PackedLocalTime.asLocalTime(next))) {
                column.appendInternal(next);
            }
        }
        return column;
    }

    /**
     * This version operates on predicates that treat the given IntPredicate as operating on a packed local time
     * This is much more efficient that using a LocalTimePredicate, but requires that the developer understand the
     * semantics of packedLocalTimes
     */
    public TimeColumn selectIf(IntPredicate predicate) {
        TimeColumn column = emptyCopy();
        IntIterator iterator = intIterator();
        while (iterator.hasNext()) {
            int next = iterator.nextInt();
            if (predicate.test(next)) {
                column.appendInternal(next);
            }
        }
        return column;
    }

    @Override
    public void append(Column column) {
        Preconditions.checkArgument(column.type() == this.type());
        TimeColumn intColumn = (TimeColumn) column;
        for (int i = 0; i < intColumn.size(); i++) {
            appendInternal(intColumn.getIntInternal(i));
        }
    }

    /**
     * Returns the largest ("top") n values in the column. Does not change the order in this column
     *
     * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
     *          number of observations in the column
     * @return A list, possibly empty, of the largest observations
     */
    public List<LocalTime> top(int n) {
        List<LocalTime> top = new ArrayList<>();
        int[] values = data.toIntArray();
        IntArrays.parallelQuickSort(values, descendingIntComparator);
        for (int i = 0; i < n && i < values.length; i++) {
            top.add(PackedLocalTime.asLocalTime(values[i]));
        }
        return top;
    }

    /**
     * Returns the smallest ("bottom") n values in the column,  Does not change the order in this column
     *
     * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
     *          number of observations in the column
     * @return A list, possibly empty, of the smallest n observations
     */
    public List<LocalTime> bottom(int n) {
        List<LocalTime> bottom = new ArrayList<>();
        int[] values = data.toIntArray();
        IntArrays.parallelQuickSort(values);
        int rowCount = 0;
        int validCount = 0;
        while (validCount < n && rowCount < size()) {
            int value = values[rowCount];
            if (value != MISSING_VALUE) {
                bottom.add(PackedLocalTime.asLocalTime(value));
                validCount++;
            }
            rowCount++;
        }
        return bottom;
    }

    public void set(int index, int value) {
        data.set(index, value);
    }

    public void set(int index, LocalTime value) {
        set(index, PackedLocalTime.pack(value));
    }

    /**
     * Conditionally update this column, replacing current values with newValue for all rows where the current value
     * matches the selection criteria
     * <p>
     * Example:
     * myColumn.set(LocalTime.now(), myColumn.isMissing()); // no more missing values
     */
    public void set(LocalTime newValue, Selection rowSelection) {
        for (int row : rowSelection) {
            set(row, newValue);
        }
    }

    /**
     * Returns a bitmap flagging the records for which the value in this column is equal to the value in the given
     * column
     * Columnwise isEqualTo.
     */
    public Selection isEqualTo(TimeColumn column) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        IntIterator intIterator = column.intIterator();
        for (int next : data) {
            if (next == intIterator.nextInt()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    public IntIterator intIterator() {
        return data.iterator();
    }

    public Selection select(IntPredicate predicate) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            int next = data.getInt(idx);
            if (predicate.test(next)) {
                selection.add(idx);
            }
        }
        return selection;
    }

    public Selection select(IntBiPredicate predicate, int value) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            int next = data.getInt(idx);
            if (predicate.test(next, value)) {
                selection.add(idx);
            }
        }
        return selection;
    }

    Set<LocalTime> asSet() {
        Set<LocalTime> times = new HashSet<>();
        TimeColumn unique = unique();
        for (LocalTime t : unique) {
            times.add(t);
        }
        return times;
    }

    public boolean contains(LocalTime time) {
        int t = PackedLocalTime.pack(time);
        return data().contains(t);
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
        return type().byteSize();
    }

    /**
     * Returns the contents of the cell at rowNumber as a byte[]
     */
    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(byteSize()).putInt(getIntInternal(rowNumber)).array();
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<LocalTime> iterator() {

        return new Iterator<LocalTime>() {

            final IntIterator intIterator = intIterator();

            @Override
            public boolean hasNext() {
                return intIterator.hasNext();
            }

            @Override
            public LocalTime next() {
                return PackedLocalTime.asLocalTime(intIterator.nextInt());
            }
        };
    }

    @Override
    public TimeColumn select(Filter filter) {
        return (TimeColumn) subset(filter.apply(this));
    }

}