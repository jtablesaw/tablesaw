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
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.instant.InstantColumnFormatter;
import tech.tablesaw.columns.instant.InstantColumnType;
import tech.tablesaw.columns.instant.InstantMapFunctions;
import tech.tablesaw.columns.instant.PackedInstant;
import tech.tablesaw.columns.temporal.TemporalFillers;
import tech.tablesaw.columns.temporal.TemporalFilters;
import tech.tablesaw.selection.Selection;
import tech.tablesaw.sorting.comparators.DescendingLongComparator;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A column in a table that contains long-integer encoded (packed) local date-time values
 */
public class InstantColumn extends AbstractColumn<Instant>
    implements InstantMapFunctions, TemporalFillers<Instant, InstantColumn>,
        TemporalFilters<Instant>, CategoricalColumn<Instant> {

    private final LongComparator reverseLongComparator = DescendingLongComparator.instance();

    private LongArrayList data;

    private final IntComparator comparator = (r1, r2) -> {
        long f1 = getPackedDateTime(r1);
        long f2 = getPackedDateTime(r2);
        return Long.compare(f1, f2);
    };

    private InstantColumnFormatter printFormatter = new InstantColumnFormatter();

    private InstantColumn(String name, LongArrayList data) {
        super(InstantColumnType.instance(), name);
        this.data = data;
    }

    public static InstantColumn create(String name) {
        return new InstantColumn(name, new LongArrayList(DEFAULT_ARRAY_SIZE));
    }

    public static InstantColumn create(String name, int initialSize) {
        InstantColumn column = new InstantColumn(name, new LongArrayList(initialSize));
        for (int i = 0; i < initialSize; i++) {
            column.appendMissing();
        }
        return column;
    }

    public static InstantColumn create(String name, List<Instant> data) {
        InstantColumn column = new InstantColumn(name, new LongArrayList(data.size()));
        for (Instant date : data) {
            column.append(date);
        }
        return column;
    }

    public static InstantColumn create(String name, Instant[] data) {
        InstantColumn column = new InstantColumn(name, new LongArrayList(data.length));
        for (Instant date : data) {
            column.append(date);
        }
        return column;
    }

    public static boolean valueIsMissing(long value) {
        return InstantColumnType.missingValueIndicator() == value;
    }

    @Override
    public boolean isMissing(int rowNumber) {
        return valueIsMissing(getLongInternal(rowNumber));
    }

    @Override
    public InstantColumn plus(long amountToAdd, ChronoUnit unit) {
        InstantColumn newColumn = emptyCopy();
        newColumn.setName(temporalColumnName(this, amountToAdd, unit));
        InstantColumn column1 = this;

        for (int r = 0; r < column1.size(); r++) {
            long packedDateTime = column1.getLongInternal(r);
            if (packedDateTime == InstantColumnType.missingValueIndicator()) {
                newColumn.appendMissing();
            } else {
                newColumn.appendInternal(PackedInstant.plus(packedDateTime, amountToAdd, unit));
            }
        }
        return newColumn;
    }


    @Override
    public InstantColumn subset(final int[] rows) {
        final InstantColumn c = this.emptyCopy();
        for (final int row : rows) {
            c.appendInternal(getLongInternal(row));
        }
        return c;
    }

    @Override
    public InstantColumn removeMissing() {
        InstantColumn noMissing = emptyCopy();
        LongIterator iterator = longIterator();
        while (iterator.hasNext()) {
            long i = iterator.nextLong();
            if (!valueIsMissing(i)) {
                noMissing.appendInternal(i);
            }
        }
        return noMissing;
    }

    public boolean contains(Instant dateTime) {
        long dt = PackedInstant.pack(dateTime);
        return data().contains(dt);
    }

    @Override
    public Column<Instant> setMissing(int i) {
        return set(i, InstantColumnType.missingValueIndicator());
    }

    public InstantColumn where(Selection selection) {
        return subset(selection.toArray());
    }

    public void setPrintFormatter(InstantColumnFormatter formatter) {
        Preconditions.checkNotNull(formatter);
        this.printFormatter = formatter;
    }

    @Override
    public InstantColumn lag(int n) {
        int srcPos = n >= 0 ? 0 : 0 - n;
        long[] dest = new long[size()];
        int destPos = n <= 0 ? 0 : n;
        int length = n >= 0 ? size() - n : size() + n;

        for (int i = 0; i < size(); i++) {
            dest[i] = InstantColumnType.missingValueIndicator();
        }

        System.arraycopy(data.toLongArray(), srcPos, dest, destPos, length);

        InstantColumn copy = emptyCopy(size());
        copy.data = new LongArrayList(dest);
        copy.setName(name() + " lag(" + n + ")");
        return copy;
    }

    @Override
    public InstantColumn appendCell(String stringValue) {
        return appendInternal(PackedInstant.pack(InstantColumnType.DEFAULT_PARSER.parse(stringValue)));
    }

    @Override
    public InstantColumn appendCell(String stringValue, AbstractColumnParser<?> parser) {
        return appendObj(parser.parse(stringValue));
    }

    public InstantColumn append(Instant dateTime) {
        if (dateTime != null) {
            final long dt = PackedInstant.pack(dateTime);
            appendInternal(dt);
        } else {
            appendInternal(InstantColumnType.missingValueIndicator());
        }
        return this;
    }

    @Override
    public InstantColumn appendObj(Object obj) {
        if (obj == null) {
            return appendMissing();
        }
        if (obj instanceof Instant) {
            return append((Instant) obj);
        }
        if (obj instanceof Timestamp ){
            Timestamp timestamp = (Timestamp) obj;
            return append(timestamp.toInstant());
        }
        throw new IllegalArgumentException("Cannot append " + obj.getClass().getName() + " to DateTimeColumn");
    }

    public int size() {
        return data.size();
    }

    public LongArrayList data() {
        return data;
    }

    public InstantColumn appendInternal(long dateTime) {
        data.add(dateTime);
        return this;
    }

    @Override
    public String getString(int row) {
        return printFormatter.format(getPackedDateTime(row));
    }

    @Override
    public String getUnformattedString(int row) {
        return PackedInstant.toString(getPackedDateTime(row));
    }

    @Override
    public InstantColumn emptyCopy() {
        InstantColumn empty = create(name());
        empty.printFormatter = printFormatter;
        return empty;
    }

    @Override
    public InstantColumn emptyCopy(int rowSize) {
        InstantColumn column = create(name(), rowSize);
        column.setPrintFormatter(printFormatter);
        return column;
    }

    @Override
    public InstantColumn copy() {
        InstantColumn column = emptyCopy(data.size());
        column.data = data.clone();
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
        LongArrays.parallelQuickSort(data.elements(), reverseLongComparator);
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

    @Override
    public int countUnique() {
        LongSet ints = new LongOpenHashSet(data.size());
        for (long i : data) {
            ints.add(i);
        }
        return ints.size();
    }

    @Override
    public InstantColumn unique() {
        LongSet ints = new LongOpenHashSet(data.size());
        for (long i : data) {
            ints.add(i);
        }
        InstantColumn column = emptyCopy(ints.size());
        column.setName(name() + " Unique values");
        column.data = LongArrayList.wrap(ints.toLongArray());
        return column;
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    public long getLongInternal(int index) {
        return data.getLong(index);
    }

    protected long getPackedDateTime(int index) {
        return getLongInternal(index);
    }

    public Instant get(int index) {
        return PackedInstant.asInstant(getPackedDateTime(index));
    }

    @Override
    public IntComparator rowComparator() {
        return comparator;
    }

    /**
     * Conditionally update this column, replacing current values with newValue for all rows where the current value
     * matches the selection criteria
     * <p>
     * Example:
     * myColumn.set(myColumn.valueIsMissing(), Instant.now()); // no more missing values
     */
    public InstantColumn set(Selection rowSelection, Instant newValue) {
        for (int row : rowSelection) {
            set(row, newValue);
        }
        return this;
    }

    /**
     * Returns the count of missing values in this column
     */
    @Override
    public int countMissing() {
        int count = 0;
        for (int i = 0; i < size(); i++) {
            if (getPackedDateTime(i) == InstantColumnType.missingValueIndicator()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns an array where each entry is the difference, measured in seconds,
     * between the Instant and midnight, January 1, 1970 UTC.
     *
     * If a value is missing, InstantColumnType.missingValueIndicator() is used
     */
    public long[] asEpochSecondArray() {
        return asEpochSecondArray(ZoneOffset.UTC);
    }

    /**
     * Returns the seconds from epoch for each value as an array based on the given offset
     *
     * If a value is missing, InstantColumnType.missingValueIndicator() is used
     */
    public long[] asEpochSecondArray(ZoneOffset offset) {
        long[] output = new long[data.size()];
        for (int i = 0; i < data.size(); i++) {
            Instant instant = PackedInstant.asInstant(data.getLong(i));
            if (instant == null) {
                output[i] = InstantColumnType.missingValueIndicator();
            } else {
                output[i] = instant.getEpochSecond();
            }
        }
        return output;
    }

    /**
     * Returns an array where each entry is the difference, measured in milliseconds,
     * between the Instant and midnight, January 1, 1970 UTC.
     *
     * If a missing value is encountered, InstantColumnType.missingValueIndicator() is inserted in the array
     */
    public long[] asEpochMillisArray() {
        return asEpochMillisArray(ZoneOffset.UTC);
    }

    /**
     * Returns an array where each entry is the difference, measured in milliseconds,
     * between the Instant and midnight, January 1, 1970 UTC.
     *
     * If a missing value is encountered, InstantColumnType.missingValueIndicator() is inserted in the array
     */
    public long[] asEpochMillisArray(ZoneOffset offset) {
        long[] output = new long[data.size()];
        for (int i = 0; i < data.size(); i++) {
            Instant instant = PackedInstant.asInstant(data.getLong(i));
            if (instant == null) {
                output[i] = InstantColumnType.missingValueIndicator();
            } else {
                output[i] = instant.toEpochMilli();
            }
        }
        return output;
    }

    public DateTimeColumn asLocalDateTimeColumn() {
        return asLocalDateTimeColumn(ZoneOffset.UTC);
    }

    public DateTimeColumn asLocalDateTimeColumn(ZoneId zone) {
        LocalDateTime[] output = new LocalDateTime[data.size()];
        for (int i = 0; i < data.size(); i++) {
            Instant instant = PackedInstant.asInstant(data.getLong(i));
            if (instant == null) {
                output[i] = null;
            } else {
                output[i] = LocalDateTime.ofInstant(instant, zone);
            }
        }
        return DateTimeColumn.create(name(), output);
    }

    @Override
    public InstantColumn append(Column<Instant> column) {
        Preconditions.checkArgument(column.type() == this.type());
        InstantColumn dateTimeColumn = (InstantColumn) column;
        final int size = dateTimeColumn.size();
        for (int i = 0; i < size; i++) {
            append(dateTimeColumn.get(i));
        }
        return this;
    }

    @Override
    public InstantColumn append(Column<Instant> column, int row) {
        Preconditions.checkArgument(column.type() == this.type());
        return appendInternal(((InstantColumn) column).getLongInternal(row));
    }

    @Override
    public InstantColumn set(int row, Column<Instant> column, int sourceRow) {
        Preconditions.checkArgument(column.type() == this.type());
        return set(row, ((InstantColumn) column).getLongInternal(sourceRow));
    }

    public Instant max() {
        long max;
        if (!isEmpty()) {
            max = getPackedDateTime(0);
        } else {
            return null;
        }
        for (long aData : data) {
            if (InstantColumnType.missingValueIndicator() != aData) {
                max = (max > aData) ? max : aData;
            }
        }

        if (InstantColumnType.missingValueIndicator() == max) {
            return null;
        }
        return PackedInstant.asInstant(max);
    }

    @Override
    public InstantColumn appendMissing() {
        appendInternal(InstantColumnType.missingValueIndicator());
        return this;
    }

    @Override
    public Instant min() {
        long min;

        if (!isEmpty()) {
            min = getPackedDateTime(0);
        } else {
            return null;
        }
        for (long aData : data) {
            if (InstantColumnType.missingValueIndicator() != aData) {
                min = (min < aData) ? min : aData;
            }
        }
        if (Integer.MIN_VALUE == min) {
            return null;
        }
        return PackedInstant.asInstant(min);
    }

    public InstantColumn set(int index, long value) {
        data.set(index, value);
        return this;
    }

    @Override
    public InstantColumn set(int index, Instant value) {
        data.set(index, PackedInstant.pack(value));
        return this;
    }

    /**
     * Returns the largest ("top") n values in the column
     *
     * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
     *          number of observations in the column
     * @return A list, possibly empty, of the largest observations
     */
    public List<Instant> top(int n) {
        List<Instant> top = new ArrayList<>();
        long[] values = data.toLongArray();
        LongArrays.parallelQuickSort(values, DescendingLongComparator.instance());
        for (int i = 0; i < n && i < values.length; i++) {
            top.add(PackedInstant.asInstant(values[i]));
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
    public List<Instant> bottom(int n) {
        List<Instant> bottom = new ArrayList<>();
        long[] values = data.toLongArray();
        LongArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(PackedInstant.asInstant(values[i]));
        }
        return bottom;
    }

    public LongIterator longIterator() {
        return data.iterator();
    }

    public Set<Instant> asSet() {
        Set<Instant> times = new HashSet<>();
        InstantColumn unique = unique();
        for (Instant Instant : unique) {
            times.add(Instant);
        }
        return times;
    }

    public int byteSize() {
        return type().byteSize();
    }

    /**
     * Returns the contents of the cell at rowNumber as a byte[]
     */
    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(byteSize()).putLong(getPackedDateTime(rowNumber)).array();
    }

    public double getDouble(int i) {
        return getPackedDateTime(i);
    }

    public double[] asDoubleArray() {
        double[] doubles = new double[size()];
        long[] millis = asEpochSecondArray();
        for (int i = 0; i < millis.length; i++) {
            doubles[i] = millis[i];
        }
        return doubles;
    }

    public DoubleColumn asDoubleColumn() {
        return DoubleColumn.create(name(), asEpochSecondArray());
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Instant> iterator() {

        return new Iterator<Instant>() {

            final LongIterator longIterator = longIterator();

            @Override
            public boolean hasNext() {
                return longIterator.hasNext();
            }

            @Override
            public Instant next() {
                return PackedInstant.asInstant(longIterator.nextLong());
            }
        };
    }

    // fillWith methods

    private InstantColumn fillWith(int count, Iterator<Instant> iterator, Consumer<Instant> acceptor) {
        for (int r = 0; r < count; r++) {
            if (!iterator.hasNext()) {
                break;
            }
            acceptor.accept(iterator.next());
        }
        return this;
    }

    @Override
    public InstantColumn fillWith(Iterator<Instant> iterator) {
        int[] r = new int[1];
        fillWith(size(), iterator, date -> set(r[0]++, date));
        return this;
    }

    private InstantColumn fillWith(int count, Iterable<Instant> iterable, Consumer<Instant> acceptor) {
        Iterator<Instant> iterator = iterable.iterator();
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
    public InstantColumn fillWith(Iterable<Instant> iterable) {
        int[] r = new int[1];
        fillWith(size(), iterable, date -> set(r[0]++, date));
        return this;
    }

    private InstantColumn fillWith(int count, Supplier<Instant> supplier, Consumer<Instant> acceptor) {
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
    public InstantColumn fillWith(Supplier<Instant> supplier) {
        int[] r = new int[1];
        fillWith(size(), supplier, date -> set(r[0]++, date));
        return this;
    }

    @Override
    public Instant[] asObjectArray() {
        final Instant[] output = new Instant[data.size()];
        for (int i = 0; i < data.size(); i++) {
            output[i] = get(i);
        }
        return output;
    }

    @Override
    public int compare(Instant o1, Instant o2) {
        return o1.compareTo(o2);
    }

    @Override
    public InstantColumn setName(String name) {
        return (InstantColumn) super.setName(name);
    }

    @Override
    public InstantColumn filter(Predicate<? super Instant> test) {
        return (InstantColumn) super.filter(test);
    }

    @Override
    public InstantColumn sorted(Comparator<? super Instant> comp) {
        return (InstantColumn) super.sorted(comp);
    }

    @Override
    public InstantColumn map(Function<? super Instant, ? extends Instant> fun) {
        return (InstantColumn) super.map(fun);
    }

    @Override
    public InstantColumn min(Column<Instant> other) {
        return (InstantColumn) super.min(other);
    }

    @Override
    public InstantColumn max(Column<Instant> other) {
        return (InstantColumn) super.max(other);
    }

    @Override
    public InstantColumn set(Selection condition, Column<Instant> other) {
        return (InstantColumn) super.set(condition, other);
    }

    @Override
    public InstantColumn first(int numRows) {
        return (InstantColumn) super.first(numRows);
    }

    @Override
    public InstantColumn last(int numRows) {
        return (InstantColumn) super.last(numRows);
    }

    @Override
    public InstantColumn inRange(int start, int end) {
        return (InstantColumn) super.inRange(start, end);
    }

    @Override
    public InstantColumn sampleN(int n) {
        return (InstantColumn) super.sampleN(n);
    }

    @Override
    public InstantColumn sampleX(double proportion) {
        return (InstantColumn) super.sampleX(proportion);
    }
}
