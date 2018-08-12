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
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.columns.datetimes.DateTimeColumnFormatter;
import tech.tablesaw.columns.datetimes.DateTimeColumnType;
import tech.tablesaw.columns.datetimes.DateTimeFillers;
import tech.tablesaw.columns.datetimes.DateTimeFilters;
import tech.tablesaw.columns.datetimes.DateTimeMapFunctions;
import tech.tablesaw.columns.datetimes.PackedLocalDateTime;
import tech.tablesaw.selection.Selection;
import tech.tablesaw.sorting.comparators.DescendingLongComparator;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static tech.tablesaw.api.ColumnType.LOCAL_DATE_TIME;

/**
 * A column in a table that contains long-integer encoded (packed) local date-time values
 */
public class DateTimeColumn extends AbstractColumn<LocalDateTime>
    implements DateTimeMapFunctions, DateTimeFilters, DateTimeFillers<DateTimeColumn> {

    public static final long MISSING_VALUE = (Long) ColumnType.LOCAL_DATE_TIME.getMissingValue();

    private final LongComparator reverseLongComparator = DescendingLongComparator.instance();

    private LongArrayList data;

    private final IntComparator comparator = (r1, r2) -> {
        long f1 = getPackedDateTime(r1);
        long f2 = getPackedDateTime(r2);
        return Long.compare(f1, f2);
    };

    private DateTimeColumnFormatter printFormatter = new DateTimeColumnFormatter();

    public static boolean valueIsMissing(long value) {
        return MISSING_VALUE == value;
    }

    @Override
    public boolean isMissing(int rowNumber) {
        return valueIsMissing(getLongInternal(rowNumber));
    }

    public static DateTimeColumn create(String name) {
        return create(name, DEFAULT_ARRAY_SIZE);
    }

    public static DateTimeColumn create(String name, int initialSize) {
        return new DateTimeColumn(name, new LongArrayList(initialSize));
    }

    public static DateTimeColumn create(String name, List<LocalDateTime> data) {
        DateTimeColumn column = new DateTimeColumn(name, new LongArrayList(data.size()));
        for (LocalDateTime date : data) {
            column.append(date);
        }
        return column;
    }

    public static DateTimeColumn create(String name, LocalDateTime[] data) {
        DateTimeColumn column = new DateTimeColumn(name, new LongArrayList(data.length));
        for (LocalDateTime date : data) {
            column.append(date);
        }
        return column;
    }

    private DateTimeColumn(String name, LongArrayList data) {
        super(LOCAL_DATE_TIME, name);
        this.data = data;
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

    public DateTimeColumn where(Selection selection) {
        return (DateTimeColumn) subset(selection);
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
            dest[i] = MISSING_VALUE;
        }

        System.arraycopy(data.toLongArray(), srcPos, dest, destPos, length);

        DateTimeColumn copy = emptyCopy(size());
        copy.data = new LongArrayList(dest);
        copy.setName(name() + " lag(" + n + ")");
        return copy;
    }

    @Override
    public DateTimeColumn appendCell(String stringValue) {
        appendInternal(PackedLocalDateTime.pack(DateTimeColumnType.DEFAULT_PARSER.parse(stringValue)));
        return this;
    }

    @Override
    public DateTimeColumn appendCell(String stringValue, StringParser parser) {
        appendInternal(PackedLocalDateTime.pack((LocalDateTime) parser.parse(stringValue)));
        return this;
    }

    public DateTimeColumn append(LocalDateTime dateTime) {
        if (dateTime != null) {
            final long dt = PackedLocalDateTime.pack(dateTime);
            appendInternal(dt);
        } else {
            appendInternal(MISSING_VALUE);
        }
        return this;
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
        return emptyCopy(DEFAULT_ARRAY_SIZE);
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

    long getPackedDateTime(int index) {
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
            if (getPackedDateTime(i) == MISSING_VALUE) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns an array where each entry is the difference, measured in seconds,
     * between the LocalDateTime and midnight, January 1, 1970 UTC.
     */
    public long[] asEpochSecondArray() {
        return asEpochSecondArray(ZoneOffset.UTC);
    }

    public long[] asEpochSecondArray(ZoneOffset offset) {
        long[] output = new long[data.size()];
        for (int i = 0; i < data.size(); i++) {
            output[i] = PackedLocalDateTime.asLocalDateTime(data.getLong(i)).toEpochSecond(offset);
        }
        return output;
    }

    /**
     * Returns an array where each entry is the difference, measured in milliseconds,
     * between the LocalDateTime and midnight, January 1, 1970 UTC.
     */
    public long[] asEpochMillisArray() {
        return asEpochMillisArray(ZoneOffset.UTC);
    }

    public long[] asEpochMillisArray(ZoneOffset offset) {
        long[] output = new long[data.size()];
        for (int i = 0; i < data.size(); i++) {
            output[i] = PackedLocalDateTime.asLocalDateTime(data.getLong(i)).toInstant(offset).toEpochMilli();
        }
        return output;
    }

    @Override
    public DateTimeColumn append(Column column) {
        Preconditions.checkArgument(column.type() == this.type());
        DateTimeColumn doubleColumn = (DateTimeColumn) column;
        for (int i = 0; i < doubleColumn.size(); i++) {
            append(doubleColumn.get(i));
        }
        return this;
    }

    public LocalDateTime max() {
        long max;
        if (!isEmpty()) {
            max = getPackedDateTime(0);
        } else {
            return null;
        }
        for (long aData : data) {
            if (MISSING_VALUE != aData) {
                max = (max > aData) ? max : aData;
            }
        }

        if (MISSING_VALUE == max) {
            return null;
        }
        return PackedLocalDateTime.asLocalDateTime(max);
    }

    @Override
    public DateTimeColumn appendMissing() {
        appendInternal(MISSING_VALUE);
        return this;
    }

    public LocalDateTime min() {
        long min;

        if (!isEmpty()) {
            min = getPackedDateTime(0);
        } else {
            return null;
        }
        for (long aData : data) {
            if (MISSING_VALUE != aData) {
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

    @Override
    public double getDouble(int i) {
        return getPackedDateTime(i);
    }

    @Override
    public double[] asDoubleArray() {
        double[] doubles = new double[size()];
        for (int i = 0; i < size(); i++) {
            doubles[i] = data.getLong(i);
        }
        return doubles;
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
        Iterator<LocalDateTime> iterator = null;
        for (int r = 0; r < count; r++) {
            if (iterator == null || (!iterator.hasNext())) {
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
    public Object[] asObjectArray() {
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
}
