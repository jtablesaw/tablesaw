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

import static tech.tablesaw.columns.DateAndTimePredicates.isMissing;
import static tech.tablesaw.columns.DateAndTimePredicates.isNotMissing;

import java.nio.ByteBuffer;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.times.PackedLocalTime;
import tech.tablesaw.columns.times.TimeColumnFormatter;
import tech.tablesaw.columns.times.TimeColumnType;
import tech.tablesaw.columns.times.TimeFillers;
import tech.tablesaw.columns.times.TimeFilters;
import tech.tablesaw.columns.times.TimeMapFunctions;
import tech.tablesaw.selection.Selection;
import tech.tablesaw.sorting.comparators.DescendingIntComparator;

/**
 * A column in a base table that contains float values
 */
public class TimeColumn extends AbstractColumn<LocalTime>
        implements CategoricalColumn<LocalTime>, TimeFilters, TimeFillers<TimeColumn>, TimeMapFunctions {

    private final IntComparator descendingIntComparator = DescendingIntComparator.instance();

    private TimeColumnFormatter printFormatter = new TimeColumnFormatter();

    private IntArrayList data;

    private final IntComparator comparator = (r1, r2) -> {
        int f1 = getIntInternal(r1);
        int f2 = getIntInternal(r2);
        return Integer.compare(f1, f2);
    };

    private TimeColumn(String name, IntArrayList times) {
        super(TimeColumnType.instance(), name);
        data = times;
    }

    private TimeColumn(String name) {
        super(TimeColumnType.instance(), name);
        data = new IntArrayList(DEFAULT_ARRAY_SIZE);
    }

    public static boolean valueIsMissing(int i) {
        return i == TimeColumnType.missingValueIndicator();
    }

    public static TimeColumn create(String name) {
        return new TimeColumn(name);
    }

    public static TimeColumn create(String name, List<LocalTime> data) {
        TimeColumn column = new TimeColumn(name, new IntArrayList(data.size()));
        for (LocalTime time : data) {
            column.append(time);
        }
        return column;
    }

    public static TimeColumn create(String name, LocalTime[] data) {
        TimeColumn column = new TimeColumn(name, new IntArrayList(data.length));
        for (LocalTime time : data) {
            column.append(time);
        }
        return column;
    }

    public static TimeColumn create(String name, int initialSize) {
        TimeColumn column = new TimeColumn(name, new IntArrayList(initialSize));
        for (int i = 0; i < initialSize; i++) {
            column.appendMissing();
        }
        return column;
    }

    @Override
    public TimeColumn appendMissing() {
        appendInternal(TimeColumnType.missingValueIndicator());
        return this;
    }

    @Override
    public TimeColumn subset(int[] rows) {
        final TimeColumn c = this.emptyCopy();
        for (final int row : rows) {
            c.appendInternal(getIntInternal(row));
        }
        return c;
    }

    @Override
    public TimeColumn lag(int n) {
        int srcPos = n >= 0 ? 0 : 0 - n;
        int[] dest = new int[size()];
        int destPos = n <= 0 ? 0 : n;
        int length = n >= 0 ? size() - n : size() + n;

        for (int i = 0; i < size(); i++) {
            dest[i] = TimeColumnType.missingValueIndicator();
        }

        System.arraycopy(data.toIntArray(), srcPos, dest, destPos, length);

        TimeColumn copy = emptyCopy(size());
        copy.data = new IntArrayList(dest);
        copy.setName(name() + " lag(" + n + ")");
        return copy;
    }

    @Override
    public boolean isMissing(int rowNumber) {
        return valueIsMissing(getIntInternal(rowNumber));
    }

    public int size() {
        return data.size();
    }

    public TimeColumn appendInternal(int f) {
        data.add(f);
        return this;
    }

    public TimeColumn append(LocalTime time) {
        int value;
        if (time == null) {
            value = TimeColumnType.missingValueIndicator();
        } else {
            value = PackedLocalTime.pack(time);
        }
        appendInternal(value);
        return this;
    }

    @Override
    public TimeColumn appendObj(Object obj) {
        if (obj == null) {
            return appendMissing();
        }
        if (!(obj instanceof LocalTime)) {
            throw new IllegalArgumentException("Cannot append " + obj.getClass().getName() + " to TimeColumn");
        }
        return append((LocalTime) obj);
    }

    @Override
    public TimeColumn removeMissing() {
        TimeColumn noMissing = emptyCopy();
        IntIterator iterator = intIterator();
        while(iterator.hasNext()) {
            int i = iterator.nextInt();
            if (!valueIsMissing(i)) {
                noMissing.appendInternal(i);
            }
        }
        return noMissing;
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

    public void setPrintFormatter(DateTimeFormatter dateTimeFormatter) {
        Preconditions.checkNotNull(dateTimeFormatter);
        this.printFormatter = new TimeColumnFormatter(dateTimeFormatter);
    }

    @Override
    public TimeColumn emptyCopy() {
        TimeColumn empty = create(name());
        empty.printFormatter = printFormatter;
        return empty;
    }

    @Override
    public TimeColumn emptyCopy(int rowSize) {
        TimeColumn column = TimeColumn.create(name(), rowSize);
        column.printFormatter = printFormatter;
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

        if (max == TimeColumnType.missingValueIndicator()) {
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
            if (aData != TimeColumnType.missingValueIndicator()) {
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
        table.addColumns(measure);
        table.addColumns(value);

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
            if (getIntInternal(i) == TimeColumnType.missingValueIndicator()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int countUnique() {
        IntOpenHashSet hashSet = new IntOpenHashSet(data);
        hashSet.remove(TimeColumnType.missingValueIndicator());
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

    @Override
    public TimeColumn appendCell(String object) {
        appendInternal(PackedLocalTime.pack(TimeColumnType.DEFAULT_PARSER.parse(object)));
        return this;
    }

    @Override
    public TimeColumn appendCell(String object, AbstractColumnParser<?> parser) {
        return appendObj(parser.parse(object));
    }

    @Override
    public int getIntInternal(int index) {
        return data.getInt(index);
    }

    protected int getPackedTime(int index) {
        return getIntInternal(index);
    }

    public LocalTime get(int index) {
        return PackedLocalTime.asLocalTime(getIntInternal(index));
    }

    @Override
    public IntComparator rowComparator() {
        return comparator;
    }

    public IntArrayList data() {
        return data;
    }

    public double getDouble(int i) {
        return getIntInternal(i);
    }

    public double[] asDoubleArray() {
        double[] doubles = new double[data.size()];
        for (int i = 0; i < size(); i++) {
            doubles[i] = data.getInt(i);
        }
        return doubles;
    }

    public DoubleColumn asDoubleColumn() {
        return DoubleColumn.create(name(), asDoubleArray());
    }

    @Override
    public String toString() {
        return "LocalTime column: " + name();
    }

    @Override
    public TimeColumn append(Column<LocalTime> column) {
        Preconditions.checkArgument(column.type() == this.type());
        TimeColumn timeCol = (TimeColumn) column;
        final int size = timeCol.size();
        for (int i = 0; i < size; i++) {
            appendInternal(timeCol.getIntInternal(i));
        }
        return this;
    }

    @Override
    public TimeColumn append(Column<LocalTime> column, int row) {
        Preconditions.checkArgument(column.type() == this.type());
        return appendInternal(((TimeColumn) column).getIntInternal(row));
    }

    @Override
    public TimeColumn set(int row, Column<LocalTime> column, int sourceRow) {
        Preconditions.checkArgument(column.type() == this.type());
        return set(row, ((TimeColumn) column).getIntInternal(sourceRow));
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
            if (value != TimeColumnType.missingValueIndicator()) {
                bottom.add(PackedLocalTime.asLocalTime(value));
                validCount++;
            }
            rowCount++;
        }
        return bottom;
    }

    public TimeColumn set(int index, int value) {
        data.set(index, value);
        return this;
    }

    public TimeColumn set(int index, LocalTime value) {
        set(index, PackedLocalTime.pack(value));
        return this;
    }

    /**
     * Conditionally update this column, replacing current values with newValue for all rows where the current value
     * matches the selection criteria
     * <p>
     * Example:
     * myColumn.set(myColumn.valueIsMissing(), LocalTime.now()); // no more missing values
     */
    public TimeColumn set(Selection rowSelection, LocalTime newValue) {
        for (int row : rowSelection) {
            set(row, newValue);
        }
        return this;
    }

    public IntIterator intIterator() {
        return data.iterator();
    }

    public boolean contains(LocalTime time) {
        int t = PackedLocalTime.pack(time);
        return data().contains(t);
    }

    @Override
    public TimeColumn setMissing(int i) {
        data.set(i, TimeColumnType.missingValueIndicator());
        return this;
    }

    @Override
    public Selection isMissing() {
        return eval(isMissing);
    }

    @Override
    public Selection isNotMissing() {
        return eval(isNotMissing);
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
    public TimeColumn where(Selection selection) {
        return subset(selection.toArray());
    }

    // fillWith methods

    private TimeColumn fillWith(int count, Iterator<LocalTime> iterator, Consumer<LocalTime> acceptor) {
        for (int r = 0; r < count; r++) {
            if (!iterator.hasNext()) {
                break;
            }
            acceptor.accept(iterator.next());
        }
        return this;
    }

    @Override
    public LocalTime[] asObjectArray() {
        final LocalTime[] output = new LocalTime[data.size()];
        for (int i = 0; i < data.size(); i++) {
            output[i] = get(i);
        }
        return output;
    }

    @Override
    public TimeColumn fillWith(Iterator<LocalTime> iterator) {
        int[] r = new int[1];
        fillWith(size(), iterator, date -> set(r[0]++, date));
        return this;
    }

    private TimeColumn fillWith(int count, Iterable<LocalTime> iterable, Consumer<LocalTime> acceptor) {
        Iterator<LocalTime> iterator = iterable.iterator();
        for (int r = 0; r < count; r++) {
            if (!iterator.hasNext()) {
                iterator = iterable.iterator();
                if (!iterator.hasNext()) {
                    break;
                }
            }
            acceptor.accept(iterator.next());
        }
        return this;
    }

    @Override
    public TimeColumn fillWith(Iterable<LocalTime> iterable) {
        int[] r = new int[1];
        fillWith(size(), iterable, date -> set(r[0]++, date));
        return this;
    }

    private TimeColumn fillWith(int count, Supplier<LocalTime> supplier, Consumer<LocalTime> acceptor) {
        for (int r = 0; r < count; r++) {
            try {
                acceptor.accept(supplier.get());
            } catch (Exception e) {
                break;
            }
        }
        return this;
    }

    @Override
    public TimeColumn fillWith(Supplier<LocalTime> supplier) {
        int[] r = new int[1];
        fillWith(size(), supplier, date -> set(r[0]++, date));
        return this;
    }

    @Override
    public int compare(LocalTime o1, LocalTime o2) {
        return o1.compareTo(o2);
    }

    @Override
    public TimeColumn filter(Predicate<? super LocalTime> test) {
        return (TimeColumn) super.filter(test);
    }

    @Override
    public TimeColumn sorted(Comparator<? super LocalTime> comp) {
        return (TimeColumn) super.sorted(comp);
    }

    @Override
    public TimeColumn map(Function<? super LocalTime, ? extends LocalTime> fun) {
        return (TimeColumn) super.map(fun);
    }

    @Override
    public TimeColumn min(Column<LocalTime> other) {
        return (TimeColumn) super.min(other);
    }

    @Override
    public TimeColumn max(Column<LocalTime> other) {
        return (TimeColumn) super.max(other);
    }

    @Override
    public TimeColumn set(Selection condition, Column<LocalTime> other) {
        return (TimeColumn) super.set(condition, other);
    }

    @Override
    public TimeColumn first(int numRows) {
        return (TimeColumn) super.first(numRows);
    }

    @Override
    public TimeColumn last(int numRows) {
        return (TimeColumn) super.last(numRows);
    }

    @Override
    public TimeColumn inRange(int start, int end) {
        return (TimeColumn) super.inRange(start, end);
    }

    @Override
    public TimeColumn sampleN(int n) {
        return (TimeColumn) super.sampleN(n);
    }

    @Override
    public TimeColumn sampleX(double proportion) {
        return (TimeColumn) super.sampleX(proportion);
    }
}
