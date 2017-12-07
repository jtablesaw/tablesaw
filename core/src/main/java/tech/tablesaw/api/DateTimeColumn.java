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
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.LongColumnUtils;
import tech.tablesaw.columns.packeddata.PackedLocalDateTime;
import tech.tablesaw.filtering.LocalDateTimePredicate;
import tech.tablesaw.filtering.LongBiPredicate;
import tech.tablesaw.filtering.LongPredicate;
import tech.tablesaw.io.TypeUtils;
import tech.tablesaw.io.TypeUtils.DateTimeConverter;
import tech.tablesaw.mapping.DateTimeMapUtils;
import tech.tablesaw.store.ColumnMetadata;
import tech.tablesaw.util.BitmapBackedSelection;
import tech.tablesaw.util.ReverseLongComparator;
import tech.tablesaw.util.Selection;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A column in a table that contains long-integer encoded (packed) local date-time values
 */
public class DateTimeColumn extends AbstractColumn implements DateTimeMapUtils, Iterable<LocalDateTime> {

    public static final long MISSING_VALUE = Long.MIN_VALUE;

    private static final int BYTE_SIZE = 8;

    private static int DEFAULT_ARRAY_SIZE = 128;
    private LongComparator reverseLongComparator = new LongComparator() {

        @Override
        public int compare(Long o2, Long o1) {
            return (o1 < o2 ? -1 : (o1.equals(o2) ? 0 : 1));
        }

        @Override
        public int compare(long o2, long o1) {
            return (o1 < o2 ? -1 : (o1 == o2 ? 0 : 1));
        }
    };

    private LongArrayList data;

    IntComparator comparator = new IntComparator() {

        @Override
        public int compare(Integer r1, Integer r2) {
            return compare((int) r1, (int) r2);
        }

        @Override
        public int compare(int r1, int r2) {
            long f1 = getLongInternal(r1);
            long f2 = getLongInternal(r2);
            return Long.compare(f1, f2);
        }
    };

    /**
     * The formatter chosen to parse date-time strings for this particular column
     */
    private DateTimeConverter selectedFormatter;

    public DateTimeColumn(String name) {
        super(name);
        data = new LongArrayList(DEFAULT_ARRAY_SIZE);
    }

    public DateTimeColumn(ColumnMetadata metadata) {
        super(metadata);
        data = new LongArrayList(DEFAULT_ARRAY_SIZE);
    }

    public DateTimeColumn(String name, int initialSize) {
        super(name);
        data = new LongArrayList(initialSize);
    }

    private DateTimeColumn(String name, LongArrayList data) {
        super(name);
        this.data = data;
    }

    public DateTimeColumn(String name, List<LocalDateTime> data) {
      this(name);
      for (LocalDateTime date : data) {
        append(date);
      }
    }

    @Override
    public void appendCell(String stringValue) {
        if (stringValue == null) {
            appendInternal(MISSING_VALUE);
        } else {
            long dateTime = convert(stringValue);
            appendInternal(dateTime);
        }
    }

    public void add(LocalDateTime dateTime) {
        if (dateTime != null) {
            final long dt = PackedLocalDateTime.pack(dateTime);
            appendInternal(dt);
        } else {
            appendInternal(MISSING_VALUE);
        }
    }

    /**
     * Returns a PackedDateTime as converted from the given string
     *
     * @param value A string representation of a time
     * @throws DateTimeParseException if no parser can be found for the time format used
     */
    public long convert(String value) {
        if (Strings.isNullOrEmpty(value)
                || TypeUtils.MISSING_INDICATORS.contains(value)
                || value.equals("-1")) {
            return MISSING_VALUE;
        }
        value = Strings.padStart(value, 4, '0');
        if (selectedFormatter == null) {
            selectedFormatter = TypeUtils.getDateTimeFormatter(value);
        }
        LocalDateTime datetime = selectedFormatter.convert(value);
        return PackedLocalDateTime.pack(datetime);
    }

    public int size() {
        return data.size();
    }

    public LongArrayList data() {
        return data;
    }

    @Override
    public ColumnType type() {
        return ColumnType.LOCAL_DATE_TIME;
    }

    public void appendInternal(long dateTime) {
        data.add(dateTime);
    }

    public void append(LocalDateTime dateTime) {
        data.add(PackedLocalDateTime.pack(dateTime));
    }

    @Override
    public String getString(int row) {
        return PackedLocalDateTime.toString(getLongInternal(row));
    }

    @Override
    public DateTimeColumn emptyCopy() {
        DateTimeColumn column = new DateTimeColumn(name());
        column.setComment(comment());
        return column;
    }

    @Override
    public DateTimeColumn emptyCopy(int rowSize) {
        DateTimeColumn column = new DateTimeColumn(name(), rowSize);
        column.setComment(comment());
        return column;
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public DateTimeColumn copy() {
        DateTimeColumn column = new DateTimeColumn(name(), data);
        column.setComment(comment());
        return column;
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
        CategoryColumn measure = new CategoryColumn("Measure");
        CategoryColumn value = new CategoryColumn("Value");
        table.addColumn(measure);
        table.addColumn(value);

        measure.add("Count");
        value.add(String.valueOf(size()));

        measure.add("Missing");
        value.add(String.valueOf(countMissing()));

        measure.add("Earliest");
        value.add(String.valueOf(min()));

        measure.add("Latest");
        value.add(String.valueOf(max()));

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
        return new DateTimeColumn(name() + " Unique values",
                LongArrayList.wrap(ints.toLongArray()));
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    public long getLongInternal(int index) {
        return data.getLong(index);
    }

    public LocalDateTime get(int index) {
        return PackedLocalDateTime.asLocalDateTime(getLongInternal(index));
    }

    @Override
    public IntComparator rowComparator() {
        return comparator;
    }

    public CategoryColumn dayOfWeek() {
        CategoryColumn newColumn = new CategoryColumn(this.name() + " day of week", this.size());
        for (int r = 0; r < this.size(); r++) {
            long c1 = this.getLongInternal(r);
            if (c1 == (DateTimeColumn.MISSING_VALUE)) {
                newColumn.set(r, CategoryColumn.MISSING_VALUE);
            } else {
                newColumn.add(PackedLocalDateTime.getDayOfWeek(c1).toString());
            }
        }
        return newColumn;
    }

    public ShortColumn dayOfWeekValue() {
        ShortColumn newColumn = new ShortColumn(this.name() + " day of week", this.size());
        for (int r = 0; r < this.size(); r++) {
            long c1 = this.getLongInternal(r);
            if (c1 == (DateTimeColumn.MISSING_VALUE)) {
                newColumn.set(r, ShortColumn.MISSING_VALUE);
            } else {
                newColumn.append((short) PackedLocalDateTime.getDayOfWeek(c1).getValue());
            }
        }
        return newColumn;
    }

    public ShortColumn dayOfYear() {
        ShortColumn newColumn = new ShortColumn(this.name() + " day of year", this.size());
        for (int r = 0; r < this.size(); r++) {
            long c1 = this.getLongInternal(r);
            if (c1 == (DateTimeColumn.MISSING_VALUE)) {
                newColumn.append(ShortColumn.MISSING_VALUE);
            } else {
                newColumn.append((short) PackedLocalDateTime.getDayOfYear(c1));
            }
        }
        return newColumn;
    }

    public ShortColumn dayOfMonth() {
        ShortColumn newColumn = new ShortColumn(this.name() + " day of month");
        for (int r = 0; r < this.size(); r++) {
            long c1 = this.getLongInternal(r);
            if (c1 == FloatColumn.MISSING_VALUE) {
                newColumn.append(ShortColumn.MISSING_VALUE);
            } else {
                newColumn.append(PackedLocalDateTime.getDayOfMonth(c1));
            }
        }
        return newColumn;
    }

    /**
     * Returns a TimeColumn containing the time portion of each dateTime in this DateTimeColumn
     */
    public TimeColumn time() {
        TimeColumn newColumn = new TimeColumn(this.name() + " time");
        for (int r = 0; r < this.size(); r++) {
            long c1 = this.getLongInternal(r);
            if (c1 == MISSING_VALUE) {
                newColumn.appendInternal(TimeColumn.MISSING_VALUE);
            } else {
                newColumn.appendInternal(PackedLocalDateTime.time(c1));
            }
        }
        return newColumn;
    }

    /**
     * Returns a DateColumn containing the date portion of each dateTime in this DateTimeColumn
     */
    public DateColumn date() {
        DateColumn newColumn = new DateColumn(this.name() + " date");
        for (int r = 0; r < this.size(); r++) {
            long c1 = this.getLongInternal(r);
            if (c1 == MISSING_VALUE) {
                newColumn.appendInternal(DateColumn.MISSING_VALUE);
            } else {
                newColumn.appendInternal(PackedLocalDateTime.date(c1));
            }
        }
        return newColumn;
    }

    public ShortColumn monthNumber() {
        ShortColumn newColumn = new ShortColumn(this.name() + " month");
        for (int r = 0; r < this.size(); r++) {
            long c1 = this.getLongInternal(r);
            if (c1 == MISSING_VALUE) {
                newColumn.append(ShortColumn.MISSING_VALUE);
            } else {
                newColumn.append((short) PackedLocalDateTime.getMonthValue(c1));
            }
        }
        return newColumn;
    }

    public CategoryColumn monthName() {
        CategoryColumn newColumn = new CategoryColumn(this.name() + " month");
        for (int r = 0; r < this.size(); r++) {
            long c1 = this.getLongInternal(r);
            if (c1 == MISSING_VALUE) {
                newColumn.add(CategoryColumn.MISSING_VALUE);
            } else {
                newColumn.add(Month.of(PackedLocalDateTime.getMonthValue(c1)).name());
            }
        }
        return newColumn;
    }

    public ShortColumn year() {
        ShortColumn newColumn = new ShortColumn(this.name() + " year");
        for (int r = 0; r < this.size(); r++) {
            long c1 = this.getLongInternal(r);
            if (c1 == MISSING_VALUE) {
                newColumn.append(ShortColumn.MISSING_VALUE);
            } else {
                newColumn.append(PackedLocalDateTime.getYear(PackedLocalDateTime.date(c1)));
            }
        }
        return newColumn;
    }

    public Selection isEqualTo(LocalDateTime value) {
        long packed = PackedLocalDateTime.pack(value);
        return select(LongColumnUtils.isEqualTo, packed);
    }

    public Selection isEqualTo(DateTimeColumn column) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        LongIterator intIterator = column.longIterator();
        for (long next : data) {
            if (next == intIterator.nextLong()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    public Selection isAfter(LocalDateTime value) {
        return select(LongColumnUtils.isGreaterThan, PackedLocalDateTime.pack(value));
    }

    public Selection isAfter(Long packedDateTime) {
        return select(LongColumnUtils.isGreaterThan, packedDateTime);
    }

    public Selection isOnOrAfter(long value) {
        return select(LongColumnUtils.isGreaterThanOrEqualTo, value);
    }

    public Selection isOnOrAfter(LocalDateTime value) {
        return select(LongColumnUtils.isGreaterThanOrEqualTo, PackedLocalDateTime.pack(value));
    }

    public Selection isBefore(LocalDateTime value) {
        return select(LongColumnUtils.isLessThan, PackedLocalDateTime.pack(value));
    }

    public Selection isBefore(Long packedDateTime) {
        return select(LongColumnUtils.isLessThan, packedDateTime);
    }

    public Selection isOnOrBefore(long value) {
        return select(LongColumnUtils.isLessThanOrEqualTo, value);
    }

    public Selection isOnOrBefore(LocalDateTime value) {
        return select(LongColumnUtils.isLessThanOrEqualTo, PackedLocalDateTime.pack(value));
    }

    public Selection isAfter(DateTimeColumn column) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        LongIterator intIterator = column.longIterator();
        for (long next : data) {
            if (next > intIterator.nextLong()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    public Selection isBefore(DateTimeColumn column) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        LongIterator intIterator = column.longIterator();
        for (long next : data) {
            if (next < intIterator.nextLong()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    /**
     * Returns the count of missing values in this column
     */
    @Override
    public int countMissing() {
        int count = 0;
        for (int i = 0; i < size(); i++) {
            if (getLongInternal(i) == MISSING_VALUE) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns an array where each entry is the difference, measured in seconds,
     * between the LocalDateTime and midnight, January 1, 1970 UTC.
     */
    public long[] toEpochSecondArray() {
      return toEpochSecondArray(ZoneOffset.UTC);
    }

    public long[] toEpochSecondArray(ZoneOffset offset) {
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
    public long[] toEpochMillisArray() {
      return toEpochMillisArray(ZoneOffset.UTC);
    }

    public long[] toEpochMillisArray(ZoneOffset offset) {
      long[] output = new long[data.size()];
      for (int i = 0; i < data.size(); i++) {
          output[i] = PackedLocalDateTime.asLocalDateTime(data.getLong(i)).toInstant(offset).toEpochMilli();
      }
      return output;
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(title());
        for (long next : data) {
            builder.append(String.valueOf(PackedLocalDateTime.asLocalDateTime(next)));
            builder.append('\n');
        }
        return builder.toString();
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
    public String toString() {
        return "LocalDateTime column: " + name();
    }

    @Override
    public void append(Column column) {
        Preconditions.checkArgument(column.type() == this.type());
        DateTimeColumn intColumn = (DateTimeColumn) column;
        for (int i = 0; i < intColumn.size(); i++) {
            add(intColumn.get(i));
        }
    }

    public LocalDateTime max() {
        long max;
        if (!isEmpty()) {
            max = getLongInternal(0);
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

    public LocalDateTime min() {
        long min;

        if (!isEmpty()) {
            min = getLongInternal(0);
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

    public ShortColumn minuteOfDay() {
        ShortColumn newColumn = new ShortColumn(this.name() + " minute of day");
        for (int r = 0; r < this.size(); r++) {
            long c1 = getLongInternal(r);
            if (c1 == DateTimeColumn.MISSING_VALUE) {
                newColumn.append(ShortColumn.MISSING_VALUE);
            } else {
                newColumn.append((short) PackedLocalDateTime.getMinuteOfDay(c1));
            }
        }
        return newColumn;
    }

    public DateTimeColumn selectIf(LocalDateTimePredicate predicate) {
        DateTimeColumn column = emptyCopy();
        LongIterator iterator = longIterator();
        while (iterator.hasNext()) {
            long next = iterator.nextLong();
            if (predicate.test(PackedLocalDateTime.asLocalDateTime(next))) {
                column.appendInternal(next);
            }
        }
        return column;
    }

    public DateTimeColumn selectIf(LongPredicate predicate) {
        DateTimeColumn column = emptyCopy();
        LongIterator iterator = longIterator();
        while (iterator.hasNext()) {
            long next = iterator.nextLong();
            if (predicate.test(next)) {
                column.appendInternal(next);
            }
        }
        return column;
    }

    public Selection isMonday() {
        return select(PackedLocalDateTime::isMonday);
    }

    public Selection isTuesday() {
        return select(PackedLocalDateTime::isTuesday);
    }

    public Selection isWednesday() {
        return select(PackedLocalDateTime::isWednesday);
    }

    public Selection isThursday() {
        return select(PackedLocalDateTime::isThursday);
    }

    public Selection isFriday() {
        return select(PackedLocalDateTime::isFriday);
    }

    public Selection isSaturday() {
        return select(PackedLocalDateTime::isSaturday);
    }

    public Selection isSunday() {
        return select(PackedLocalDateTime::isSunday);
    }

    public Selection isInJanuary() {
        return select(PackedLocalDateTime::isInJanuary);
    }

    public Selection isInFebruary() {
        return select(PackedLocalDateTime::isInFebruary);
    }

    public Selection isInMarch() {
        return select(PackedLocalDateTime::isInMarch);
    }

    public Selection isInApril() {
        return select(PackedLocalDateTime::isInApril);
    }

    public Selection isInMay() {
        return select(PackedLocalDateTime::isInMay);
    }

    public Selection isInJune() {
        return select(PackedLocalDateTime::isInJune);
    }

    public Selection isInJuly() {
        return select(PackedLocalDateTime::isInJuly);
    }

    public Selection isInAugust() {
        return select(PackedLocalDateTime::isInAugust);
    }

    public Selection isInSeptember() {
        return select(PackedLocalDateTime::isInSeptember);
    }

    public Selection isInOctober() {
        return select(PackedLocalDateTime::isInOctober);
    }

    public Selection isInNovember() {
        return select(PackedLocalDateTime::isInNovember);
    }

    public Selection isInDecember() {
        return select(PackedLocalDateTime::isInDecember);
    }

    public Selection isFirstDayOfMonth() {
        return select(PackedLocalDateTime::isFirstDayOfMonth);
    }

    public Selection isLastDayOfMonth() {
        return select(PackedLocalDateTime::isLastDayOfMonth);
    }

    public Selection isInQ1() {
        return select(PackedLocalDateTime::isInQ1);
    }

    public Selection isInQ2() {
        return select(PackedLocalDateTime::isInQ2);
    }

    public Selection isInQ3() {
        return select(PackedLocalDateTime::isInQ3);
    }

    public Selection isInQ4() {
        return select(PackedLocalDateTime::isInQ4);
    }

    public Selection isNoon() {
        return select(PackedLocalDateTime::isNoon);
    }

    public Selection isMidnight() {
        return select(PackedLocalDateTime::isMidnight);
    }

    public Selection isBeforeNoon() {
        return select(PackedLocalDateTime::AM);
    }

    public Selection isAfterNoon() {
        return select(PackedLocalDateTime::PM);
    }

    public Selection select(LongPredicate predicate) {
        Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            long next = data.getLong(idx);
            if (predicate.test(next)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }

    public void set(int index, long value) {
        data.add(index, value);
    }

    public Selection select(LongBiPredicate predicate, long value) {
        Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < data.size(); idx++) {
            long next = data.getLong(idx);
            if (predicate.test(next, value)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
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
        LongArrays.parallelQuickSort(values, ReverseLongComparator.instance());
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

    public Selection isInYear(int year) {
        return select(i -> PackedLocalDateTime.isInYear(i, year));
    }

    public boolean contains(LocalDateTime dateTime) {
        long dt = PackedLocalDateTime.pack(dateTime);
        return data().contains(dt);
    }

    public int byteSize() {
        return BYTE_SIZE;
    }

    /**
     * Returns the contents of the cell at rowNumber as a byte[]
     */
    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(8).putLong(getLongInternal(rowNumber)).array();
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<LocalDateTime> iterator() {

        return new Iterator<LocalDateTime>() {

            LongIterator longIterator = longIterator();

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

    @Override
    public DateTimeColumn difference() {
        throw new UnsupportedOperationException("DateTimeColumn.difference() currently not supported");
   /*
    DateTimeColumn returnValue = new DateTimeColumn(this.name(), data.size());
    returnValue.add(DateTimeColumn.MISSING_VALUE);
    for (int current = 1; current > data.size(); current++) {
      LocalDateTime currentValue = get(current);
      LocalDateTime nextValue = get(current+1);
      Duration duration = Duration.between(currentValue, nextValue);
      LocalDateTime date =
              LocalDateTime.ofInstant(Instant.ofEpochMilli(duration.toMillis()), ZoneId.systemDefault());
      returnValue.add(date);
    }
    return returnValue;
    */
    }

}
