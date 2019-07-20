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
import tech.tablesaw.columns.datetimes.DateTimeColumnFormatter;
import tech.tablesaw.columns.datetimes.DateTimeColumnType;
import tech.tablesaw.columns.datetimes.DateTimeFilters;
import tech.tablesaw.columns.datetimes.DateTimeMapFunctions;
import tech.tablesaw.columns.datetimes.PackedLocalDateTime;
import tech.tablesaw.columns.temporal.TemporalFillers;
import tech.tablesaw.selection.Selection;
import tech.tablesaw.sorting.comparators.DescendingLongComparator;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
public class DateTimeColumn extends AbstractColumn<LocalDateTime>
    implements DateTimeMapFunctions, DateTimeFilters, TemporalFillers<LocalDateTime, DateTimeColumn>,
        CategoricalColumn<LocalDateTime> {

    private final LongComparator reverseLongComparator = DescendingLongComparator.instance();

    private LongArrayList data;

    private final IntComparator comparator = (r1, r2) -> {
        long f1 = getPackedDateTime(r1);
        long f2 = getPackedDateTime(r2);
        return Long.compare(f1, f2);
    };

    private DateTimeColumnFormatter printFormatter = new DateTimeColumnFormatter();

    private DateTimeColumn(String name, LongArrayList data) {
        super(DateTimeColumnType.instance(), name);
        this.data = data;
    }

    public static DateTimeColumn create(String name) {
        return new DateTimeColumn(name, new LongArrayList(DEFAULT_ARRAY_SIZE));
    }

    public static DateTimeColumn create(String name, int initialSize) {
        DateTimeColumn column = new DateTimeColumn(name, new LongArrayList(initialSize));
        for (int i = 0; i < initialSize; i++) {
            column.appendMissing();
        }
        return column;
    }

    public static DateTimeColumn create(String name, List<LocalDateTime> data) {
        DateTimeColumn column = new DateTimeColumn(name, new LongArrayList(data.size()));
        for (LocalDateTime date : data) {
            column.append(date);
        }
        return column;
    }

    @Override
    public DateTimeColumn plus(long amountToAdd, ChronoUnit unit) {
        DateTimeColumn newColumn = emptyCopy();
        newColumn.setName(temporalColumnName(this, amountToAdd, unit));
        DateTimeColumn column1 = this;

        for (int r = 0; r < column1.size(); r++) {
            long packedDateTime = column1.getLongInternal(r);
            if (packedDateTime == DateTimeColumnType.missingValueIndicator()) {
                newColumn.appendMissing();
            } else {
                newColumn.appendInternal(PackedLocalDateTime.plus(packedDateTime, amountToAdd, unit));
            }
        }
        return newColumn;
    }

    public static DateTimeColumn create(String name, LocalDateTime[] data) {
        DateTimeColumn column = new DateTimeColumn(name, new LongArrayList(data.length));
        for (LocalDateTime date : data) {
            column.append(date);
        }
        return column;
    }

    public static boolean valueIsMissing(long value) {
        return DateTimeColumnType.missingValueIndicator() == value;
    }

    @Override
    public boolean isMissing(int rowNumber) {
        return valueIsMissing(getLongInternal(rowNumber));
    }

    @Override
    public DateTimeColumn subset(final int[] rows) {
        final DateTimeColumn c = this.emptyCopy();
        for (final int row : rows) {
            c.appendInternal(getLongInternal(row));
        }
        return c;
    }

    @Override
    public DateTimeColumn removeMissing() {
        DateTimeColumn noMissing = emptyCopy();
        LongIterator iterator = longIterator();
        while(iterator.hasNext()) {
            long i = iterator.nextLong();
            if (!valueIsMissing(i)) {
                noMissing.appendInternal(i);
            }
        }
        return noMissing;
    }

    public boolean contains(LocalDateTime dateTime) {
        long dt = PackedLocalDateTime.pack(dateTime);
        return data().contains(dt);
    }

    @Override
    public Column<LocalDateTime> setMissing(int i) {
        return set(i, DateTimeColumnType.missingValueIndicator());
    }

    public DateTimeColumn where(Selection selection) {
        return subset(selection.toArray());
    }

    public void setPrintFormatter(DateTimeFormatter dateTimeFormatter, String missingValueString) {
        Preconditions.checkNotNull(dateTimeFormatter);
        Preconditions.checkNotNull(missingValueString);
        this.printFormatter = new DateTimeColumnFormatter(dateTimeFormatter, missingValueString);
    }

    public void setPrintFormatter(DateTimeFormatter dateTimeFormatter) {
        Preconditions.checkNotNull(dateTimeFormatter);
        this.printFormatter = new DateTimeColumnFormatter(dateTimeFormatter);
    }

    public void setPrintFormatter(DateTimeColumnFormatter formatter) {
        Preconditions.checkNotNull(formatter);
        this.printFormatter = formatter;
    }

    @Override
    public DateTimeColumn lag(int n) {
        int srcPos = n >= 0 ? 0 : 0 - n;
        long[] dest = new long[size()];
        int destPos = n <= 0 ? 0 : n;
        int length = n >= 0 ? size() - n : size() + n;

        for (int i = 0; i < size(); i++) {
            dest[i] = DateTimeColumnType.missingValueIndicator();
        }

        System.arraycopy(data.toLongArray(), srcPos, dest, destPos, length);

        DateTimeColumn copy = emptyCopy(size());
        copy.data = new LongArrayList(dest);
        copy.setName(name() + " lag(" + n + ")");
        return copy;
    }

    @Override
    public DateTimeColumn appendCell(String stringValue) {
        return appendInternal(PackedLocalDateTime.pack(DateTimeColumnType.DEFAULT_PARSER.parse(stringValue)));
    }

    @Override
    public DateTimeColumn appendCell(String stringValue, AbstractColumnParser<?> parser) {
        return appendObj(parser.parse(stringValue));
    }

    public DateTimeColumn append(LocalDateTime dateTime) {
        if (dateTime != null) {
            final long dt = PackedLocalDateTime.pack(dateTime);
            appendInternal(dt);
        } else {
            appendInternal(DateTimeColumnType.missingValueIndicator());
        }
        return this;
    }

    @Override
    public DateTimeColumn appendObj(Object obj) {
        if (obj == null) {
            return appendMissing();
        }
        if (obj instanceof LocalDateTime) {
            return append((LocalDateTime) obj);
        }
        if (obj instanceof Timestamp ){
            Timestamp timestamp = (Timestamp) obj;
            return append(timestamp.toLocalDateTime());
        }
        throw new IllegalArgumentException("Cannot append " + obj.getClass().getName() + " to DateTimeColumn");
    }

    public int size() {
        return data.size();
    }

    public LongArrayList data() {
        return data;
    }

    public DateTimeColumn appendInternal(long dateTime) {
        data.add(dateTime);
        return this;
    }

    @Override
    public String getString(int row) {
        return printFormatter.format(getPackedDateTime(row));
    }

    @Override
    public String getUnformattedString(int row) {
        return PackedLocalDateTime.toString(getPackedDateTime(row));
    }

    @Override
    public DateTimeColumn emptyCopy() {
        DateTimeColumn empty = create(name());
        empty.printFormatter = printFormatter;
        return empty;
    }

    @Override
    public DateTimeColumn emptyCopy(int rowSize) {
        DateTimeColumn column = create(name(), rowSize);
        column.setPrintFormatter(printFormatter);
        return column;
    }

    @Override
    public DateTimeColumn copy() {
        DateTimeColumn column = emptyCopy(data.size());
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
    public DateTimeColumn unique() {
        LongSet ints = new LongOpenHashSet(data.size());
        for (long i : data) {
            ints.add(i);
        }
        DateTimeColumn column = emptyCopy(ints.size());
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

    public LocalDateTime get(int index) {
        return PackedLocalDateTime.asLocalDateTime(getPackedDateTime(index));
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
     * myColumn.set(myColumn.valueIsMissing(), LocalDateTime.now()); // no more missing values
     */
    public DateTimeColumn set(Selection rowSelection, LocalDateTime newValue) {
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
            if (getPackedDateTime(i) == DateTimeColumnType.missingValueIndicator()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns an array where each entry is the difference, measured in seconds,
     * between the LocalDateTime and midnight, January 1, 1970 UTC.
     *
     * If a value is missing, DateTimeColumnType.missingValueIndicator() is used
     */
    public long[] asEpochSecondArray() {
        return asEpochSecondArray(ZoneOffset.UTC);
    }

    /**
     * Returns the seconds from epoch for each value as an array based on the given offset
     *
     * If a value is missing, DateTimeColumnType.missingValueIndicator() is used
     */
    public long[] asEpochSecondArray(ZoneOffset offset) {
        long[] output = new long[data.size()];
        for (int i = 0; i < data.size(); i++) {
            LocalDateTime dateTime = PackedLocalDateTime.asLocalDateTime(data.getLong(i));
            if (dateTime == null) {
                output[i] = DateTimeColumnType.missingValueIndicator();
            } else {
                output[i] = dateTime.toEpochSecond(offset);
            }
        }
        return output;
    }

    /**
     * Returns an array where each entry is the difference, measured in milliseconds,
     * between the LocalDateTime and midnight, January 1, 1970 UTC.
     *
     * If a missing value is encountered, DateTimeColumnType.missingValueIndicator() is inserted in the array
     */
    public long[] asEpochMillisArray() {
        return asEpochMillisArray(ZoneOffset.UTC);
    }

    /**
     * Returns an array where each entry is the difference, measured in milliseconds,
     * between the LocalDateTime and midnight, January 1, 1970 UTC.
     *
     * If a missing value is encountered, DateTimeColumnType.missingValueIndicator() is inserted in the array
     */
    public long[] asEpochMillisArray(ZoneOffset offset) {
        long[] output = new long[data.size()];
        for (int i = 0; i < data.size(); i++) {
            LocalDateTime dateTime = PackedLocalDateTime.asLocalDateTime(data.getLong(i));
            if (dateTime == null) {
                output[i] = DateTimeColumnType.missingValueIndicator();
            } else {
                output[i] = dateTime.toInstant(offset).toEpochMilli();
            }
        }
        return output;
    }

    public InstantColumn asInstantColumn() {
        return asInstantColumn(ZoneOffset.UTC);
    }

    public InstantColumn asInstantColumn(ZoneId zone) {
        Instant[] output = new Instant[data.size()];
        for (int i = 0; i < data.size(); i++) {
            LocalDateTime dateTime = PackedLocalDateTime.asLocalDateTime(data.getLong(i));
            if (dateTime == null) {
                output[i] = null;
            } else {
                output[i] = dateTime.atZone(zone).toInstant();
            }
        }
        return InstantColumn.create(name(), output);
    }

    @Override
    public DateTimeColumn append(Column<LocalDateTime> column) {
        Preconditions.checkArgument(column.type() == this.type());
        DateTimeColumn dateTimeColumn = (DateTimeColumn) column;
        final int size = dateTimeColumn.size();
        for (int i = 0; i < size; i++) {
            append(dateTimeColumn.get(i));
        }
        return this;
    }

    @Override
    public DateTimeColumn append(Column<LocalDateTime> column, int row) {
        Preconditions.checkArgument(column.type() == this.type());
        return appendInternal(((DateTimeColumn) column).getLongInternal(row));
    }

    @Override
    public DateTimeColumn set(int row, Column<LocalDateTime> column, int sourceRow) {
        Preconditions.checkArgument(column.type() == this.type());
        return set(row, ((DateTimeColumn) column).getLongInternal(sourceRow));
    }

    public LocalDateTime max() {
        long max;
        if (!isEmpty()) {
            max = getPackedDateTime(0);
        } else {
            return null;
        }
        for (long aData : data) {
            if (DateTimeColumnType.missingValueIndicator() != aData) {
                max = (max > aData) ? max : aData;
            }
        }

        if (DateTimeColumnType.missingValueIndicator() == max) {
            return null;
        }
        return PackedLocalDateTime.asLocalDateTime(max);
    }

    @Override
    public DateTimeColumn appendMissing() {
        appendInternal(DateTimeColumnType.missingValueIndicator());
        return this;
    }

    @Override
    public LocalDateTime min() {
        long min;

        if (!isEmpty()) {
            min = getPackedDateTime(0);
        } else {
            return null;
        }
        for (long aData : data) {
            if (DateTimeColumnType.missingValueIndicator() != aData) {
                min = (min < aData) ? min : aData;
            }
        }
        if (Integer.MIN_VALUE == min) {
            return null;
        }
        return PackedLocalDateTime.asLocalDateTime(min);
    }

    public DateTimeColumn set(int index, long value) {
        data.set(index, value);
        return this;
    }

    @Override
    public DateTimeColumn set(int index, LocalDateTime value) {
        data.set(index, PackedLocalDateTime.pack(value));
        return this;
    }

    /**
     * Returns the largest ("top") n values in the column
     *
     * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
     *          number of observations in the column
     * @return A list, possibly empty, of the largest observations
     */
    public List<LocalDateTime> top(int n) {
        List<LocalDateTime> top = new ArrayList<>();
        long[] values = data.toLongArray();
        LongArrays.parallelQuickSort(values, DescendingLongComparator.instance());
        for (int i = 0; i < n && i < values.length; i++) {
            top.add(PackedLocalDateTime.asLocalDateTime(values[i]));
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
    public List<LocalDateTime> bottom(int n) {
        List<LocalDateTime> bottom = new ArrayList<>();
        long[] values = data.toLongArray();
        LongArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(PackedLocalDateTime.asLocalDateTime(values[i]));
        }
        return bottom;
    }

    public LongIterator longIterator() {
        return data.iterator();
    }

    public Set<LocalDateTime> asSet() {
        Set<LocalDateTime> times = new HashSet<>();
        DateTimeColumn unique = unique();
        for (LocalDateTime localDateTime : unique) {
            times.add(localDateTime);
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
    public Iterator<LocalDateTime> iterator() {

        return new Iterator<LocalDateTime>() {

            final LongIterator longIterator = longIterator();

            @Override
            public boolean hasNext() {
                return longIterator.hasNext();
            }

            @Override
            public LocalDateTime next() {
                return PackedLocalDateTime.asLocalDateTime(longIterator.nextLong());
            }
        };
    }

    // fillWith methods

    private DateTimeColumn fillWith(int count, Iterator<LocalDateTime> iterator, Consumer<LocalDateTime> acceptor) {
        for (int r = 0; r < count; r++) {
            if (!iterator.hasNext()) {
                break;
            }
            acceptor.accept(iterator.next());
        }
        return this;
    }

    @Override
    public DateTimeColumn fillWith(Iterator<LocalDateTime> iterator) {
        int[] r = new int[1];
        fillWith(size(), iterator, date -> set(r[0]++, date));
        return this;
    }

    private DateTimeColumn fillWith(int count, Iterable<LocalDateTime> iterable, Consumer<LocalDateTime> acceptor) {
        Iterator<LocalDateTime> iterator = iterable.iterator();
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
    public DateTimeColumn fillWith(Iterable<LocalDateTime> iterable) {
        int[] r = new int[1];
        fillWith(size(), iterable, date -> set(r[0]++, date));
        return this;
    }

    private DateTimeColumn fillWith(int count, Supplier<LocalDateTime> supplier, Consumer<LocalDateTime> acceptor) {
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
    public DateTimeColumn fillWith(Supplier<LocalDateTime> supplier) {
        int[] r = new int[1];
        fillWith(size(), supplier, date -> set(r[0]++, date));
        return this;
    }

    @Override
    public LocalDateTime[] asObjectArray() {
        final LocalDateTime[] output = new LocalDateTime[data.size()];
        for (int i = 0; i < data.size(); i++) {
            output[i] = get(i);
        }
        return output;
    }

    @Override
    public int compare(LocalDateTime o1, LocalDateTime o2) {
        return o1.compareTo(o2);
    }

    @Override
    public DateTimeColumn setName(String name) {
        return (DateTimeColumn) super.setName(name);
    }

    @Override
    public DateTimeColumn filter(Predicate<? super LocalDateTime> test) {
        return (DateTimeColumn) super.filter(test);
    }

    @Override
    public DateTimeColumn sorted(Comparator<? super LocalDateTime> comp) {
        return (DateTimeColumn) super.sorted(comp);
    }

    @Override
    public DateTimeColumn map(Function<? super LocalDateTime, ? extends LocalDateTime> fun) {
        return (DateTimeColumn) super.map(fun);
    }

    @Override
    public DateTimeColumn min(Column<LocalDateTime> other) {
        return (DateTimeColumn) super.min(other);
    }

    @Override
    public DateTimeColumn max(Column<LocalDateTime> other) {
        return (DateTimeColumn) super.max(other);
    }

    @Override
    public DateTimeColumn set(Selection condition, Column<LocalDateTime> other) {
        return (DateTimeColumn) super.set(condition, other);
    }

    @Override
    public DateTimeColumn first(int numRows) {
        return (DateTimeColumn) super.first(numRows);
    }

    @Override
    public DateTimeColumn last(int numRows) {
        return (DateTimeColumn) super.last(numRows);
    }

    @Override
    public DateTimeColumn inRange(int start, int end) {
        return (DateTimeColumn) super.inRange(start, end);
    }

    @Override
    public DateTimeColumn sampleN(int n) {
        return (DateTimeColumn) super.sampleN(n);
    }

    @Override
    public DateTimeColumn sampleX(double proportion) {
        return (DateTimeColumn) super.sampleX(proportion);
    }
}
