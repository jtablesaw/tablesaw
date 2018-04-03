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
import tech.tablesaw.columns.dates.PackedLocalDate;
import tech.tablesaw.columns.datetimes.DateTimeColumnFormatter;
import tech.tablesaw.columns.datetimes.DateTimeMapUtils;
import tech.tablesaw.columns.datetimes.PackedLocalDateTime;
import tech.tablesaw.filtering.predicates.LocalDateTimePredicate;
import tech.tablesaw.filtering.predicates.LongBiPredicate;
import tech.tablesaw.filtering.predicates.LongPredicate;
import tech.tablesaw.io.TypeUtils;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;
import tech.tablesaw.sorting.comparators.DescendingLongComparator;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
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
import static tech.tablesaw.columns.datetimes.DateTimePredicates.*;

/**
 * A column in a table that contains long-integer encoded (packed) local date-time values
 */
public class DateTimeColumn extends AbstractColumn
        implements DateTimeMapUtils, Iterable<LocalDateTime> {

    public static final long MISSING_VALUE = (Long) ColumnType.LOCAL_DATE_TIME.getMissingValue();

    private final LongComparator reverseLongComparator = DescendingLongComparator.instance();

    private LongArrayList data;

    private final IntComparator comparator = (r1, r2) -> {
        long f1 = getPackedDateTime(r1);
        long f2 = getPackedDateTime(r2);
        return Long.compare(f1, f2);
    };

    /**
     * The formatter chosen to parse date-time strings for this particular column
     */
    private TypeUtils.DateTimeConverter selectedFormatter;

    private DateTimeColumnFormatter printFormatter = new DateTimeColumnFormatter();

    private Locale locale;

    public static boolean isMissing(long value) {
        return MISSING_VALUE == value;
    }

    public static DateTimeColumn create(String name) {
        return create(name, DEFAULT_ARRAY_SIZE);
    }

    public static DateTimeColumn create(String name, int initialSize) {
        return create(name, initialSize, Locale.getDefault());
    }

    public static DateTimeColumn create(String name, int initialSize, Locale locale) {
        return new DateTimeColumn(name, new LongArrayList(initialSize), locale);
    }

    public static DateTimeColumn create(String name, List<LocalDateTime> data) {
        DateTimeColumn column = new DateTimeColumn(name, new LongArrayList(data.size()), Locale.getDefault());
        for (LocalDateTime date : data) {
            column.append(date);
        }
        return column;
    }

    public static DateTimeColumn create(String name, LocalDateTime[] data) {
        DateTimeColumn column = new DateTimeColumn(name, new LongArrayList(data.length), Locale.getDefault());
        for (LocalDateTime date : data) {
            column.append(date);
        }
        return column;
    }

    private DateTimeColumn(String name, LongArrayList data, Locale locale) {
        super(LOCAL_DATE_TIME, name);
        this.data = data;
        this.locale = locale;
    }

    public void setPrintFormatter(DateTimeFormatter dateTimeFormatter, String missingValueString) {
        Preconditions.checkNotNull(dateTimeFormatter);
        Preconditions.checkNotNull(missingValueString);
        this.printFormatter = new DateTimeColumnFormatter(dateTimeFormatter, missingValueString);
    }

    public void setPrintFormatter(DateTimeColumnFormatter formatter) {
        Preconditions.checkNotNull(formatter);
        this.printFormatter = formatter;
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

    public DateTimeColumn lead(int n) {
        DateTimeColumn column = lag(-n);
        column.setName(name() + " lead(" + n + ")");
        return column;
    }

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
        return LOCAL_DATE_TIME;
    }

    public void appendInternal(long dateTime) {
        data.add(dateTime);
    }

    public void append(LocalDateTime dateTime) {
        data.add(PackedLocalDateTime.pack(dateTime));
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
        DateTimeColumn column = create(name(), rowSize, locale);
        column.selectedFormatter = selectedFormatter;
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

    public long getPackedDateTime(int index) {
        return getLongInternal(index);
    }

    public LocalDateTime get(int index) {
        return PackedLocalDateTime.asLocalDateTime(getPackedDateTime(index));
    }

    @Override
    public IntComparator rowComparator() {
        return comparator;
    }

    public StringColumn dayOfWeek() {
        StringColumn newColumn = StringColumn.create(this.name() + " day of week", this.size());
        for (int r = 0; r < this.size(); r++) {
            long c1 = this.getPackedDateTime(r);
            if (DateTimeColumn.isMissing(c1)) {
                newColumn.append(StringColumn.MISSING_VALUE);
            } else {
                newColumn.append(PackedLocalDateTime.getDayOfWeek(c1).toString());
            }
        }
        return newColumn;
    }

    public NumberColumn dayOfWeekValue() {
        NumberColumn newColumn = NumberColumn.create(this.name() + " day of week", this.size());
        for (int r = 0; r < this.size(); r++) {
            long c1 = this.getPackedDateTime(r);
            if (DateTimeColumn.isMissing(c1)) {
                newColumn.append(NumberColumn.MISSING_VALUE);
            } else {
                newColumn.append((short) PackedLocalDateTime.getDayOfWeek(c1).getValue());
            }
        }
        return newColumn;
    }

    public NumberColumn dayOfYear() {
        NumberColumn newColumn = NumberColumn.create(this.name() + " day of year", this.size());
        for (int r = 0; r < this.size(); r++) {
            long c1 = this.getPackedDateTime(r);
            if (DateTimeColumn.isMissing(c1)) {
                newColumn.append(NumberColumn.MISSING_VALUE);
            } else {
                newColumn.append((short) PackedLocalDateTime.getDayOfYear(c1));
            }
        }
        return newColumn;
    }

    public NumberColumn dayOfMonth() {
        NumberColumn newColumn = NumberColumn.create(this.name() + " day of month");
        for (int r = 0; r < this.size(); r++) {
            long c1 = this.getPackedDateTime(r);
            if (DateTimeColumn.isMissing(c1)) {
                newColumn.append(NumberColumn.MISSING_VALUE);
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
        TimeColumn newColumn = TimeColumn.create(this.name() + " time");
        for (int r = 0; r < this.size(); r++) {
            long c1 = this.getPackedDateTime(r);
            if (DateTimeColumn.isMissing(c1)) {
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
        DateColumn newColumn = DateColumn.create(this.name() + " date");
        for (int r = 0; r < this.size(); r++) {
            long c1 = this.getPackedDateTime(r);
            if (DateTimeColumn.isMissing(c1)) {
                newColumn.appendInternal(DateColumn.MISSING_VALUE);
            } else {
                newColumn.appendInternal(PackedLocalDateTime.date(c1));
            }
        }
        return newColumn;
    }

    public NumberColumn monthNumber() {
        NumberColumn newColumn = NumberColumn.create(this.name() + " month");
        for (int r = 0; r < this.size(); r++) {
            long c1 = this.getPackedDateTime(r);
            if (DateTimeColumn.isMissing(c1)) {
                newColumn.append(NumberColumn.MISSING_VALUE);
            } else {
                newColumn.append((short) PackedLocalDateTime.getMonthValue(c1));
            }
        }
        return newColumn;
    }

    public StringColumn monthName() {
        StringColumn newColumn = StringColumn.create(this.name() + " month");
        for (int r = 0; r < this.size(); r++) {
            long c1 = this.getPackedDateTime(r);
            if (DateTimeColumn.isMissing(c1)) {
                newColumn.append(StringColumn.MISSING_VALUE);
            } else {
                newColumn.append(Month.of(PackedLocalDateTime.getMonthValue(c1)).name());
            }
        }
        return newColumn;
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
            long c1 = this.getPackedDateTime(r);
            if (DateTimeColumn.isMissing(c1)) {
                newColumn.append(StringColumn.MISSING_VALUE);
            } else {
                String ym = String.valueOf(PackedLocalDateTime.getYear(c1));
                ym = ym + "-" + Strings.padStart(
                        String.valueOf(PackedLocalDateTime.getMonthValue(c1)), 2, '0');
                newColumn.append(ym);
            }
        }
        return newColumn;
    }

    /**
     * Returns a StringColumn with the year and day-of-year derived from this column concatenated into a String
     * that will sort lexicographically in temporal order.
     * <p>
     * This simplifies the production of plots and tables that aggregate values into standard temporal units (e.g.,
     * you want monthly data but your source data is more than a year long and you don't want months from different
     * years aggregated together).
     */
    public StringColumn yearDayString() {
        StringColumn newColumn = StringColumn.create(this.name() + " year & month");
        for (int r = 0; r < this.size(); r++) {
            long c1 = this.getPackedDateTime(r);
            if (DateTimeColumn.isMissing(c1)) {
                newColumn.append(StringColumn.MISSING_VALUE);
            } else {
                String ym = String.valueOf(PackedLocalDateTime.getYear(c1));
                ym = ym + "-" + Strings.padStart(
                        String.valueOf(PackedLocalDateTime.getDayOfYear(c1)), 3, '0');
                newColumn.append(ym);
            }
        }
        return newColumn;
    }

    /**
     * Returns a StringColumn with the year and week-of-year derived from this column concatenated into a String
     * that will sort lexicographically in temporal order.
     * <p>
     * This simplifies the production of plots and tables that aggregate values into standard temporal units (e.g.,
     * you want monthly data but your source data is more than a year long and you don't want months from different
     * years aggregated together).
     */
    public StringColumn yearWeekString() {
        StringColumn newColumn = StringColumn.create(this.name() + " year & month");
        for (int r = 0; r < this.size(); r++) {
            long c1 = this.getPackedDateTime(r);
            if (DateTimeColumn.isMissing(c1)) {
                newColumn.append(StringColumn.MISSING_VALUE);
            } else {
                String ym = String.valueOf(PackedLocalDateTime.getYear(c1));
                ym = ym + "-" + Strings.padStart(
                        String.valueOf(PackedLocalDateTime.getWeekOfYear(c1)), 2, '0');
                newColumn.append(ym);
            }
        }
        return newColumn;
    }

    public NumberColumn year() {
        NumberColumn newColumn = NumberColumn.create(this.name() + " year");
        for (int r = 0; r < this.size(); r++) {
            long c1 = this.getPackedDateTime(r);
            if (DateTimeColumn.isMissing(c1)) {
                newColumn.append(NumberColumn.MISSING_VALUE);
            } else {
                newColumn.append(PackedLocalDate.getYear(PackedLocalDateTime.date(c1)));
            }
        }
        return newColumn;
    }

    /**
     * Conditionally update this column, replacing current values with newValue for all rows where the current value
     * matches the selection criteria
     * <p>
     * Example:
     * myColumn.set(LocalDateTime.now(), myColumn.isMissing()); // no more missing values
     */
    public void set(LocalDateTime newValue, Selection rowSelection) {
        for (int row : rowSelection) {
            set(row, newValue);
        }
    }

    public Selection isEqualTo(LocalDateTime value) {
        long packed = PackedLocalDateTime.pack(value);
        return eval(isEqualTo, packed);
    }

    public Selection isNotEqualTo(LocalDateTime value) {
        long packed = PackedLocalDateTime.pack(value);
        return eval(isNotEqualTo, packed);
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
        return eval(isGreaterThan, PackedLocalDateTime.pack(value));
    }

    public Selection isAfter(Long packedDateTime) {
        return eval(isGreaterThan, packedDateTime);
    }

    public Selection isOnOrAfter(long value) {
        return eval(isGreaterThanOrEqualTo, value);
    }

    public Selection isOnOrAfter(LocalDateTime value) {
        return eval(isGreaterThanOrEqualTo, PackedLocalDateTime.pack(value));
    }

    public Selection isBefore(LocalDateTime value) {
        return eval(isLessThan, PackedLocalDateTime.pack(value));
    }

    public Selection isBefore(Long packedDateTime) {
        return eval(isLessThan, packedDateTime);
    }

    public Selection isOnOrBefore(long value) {
        return eval(isLessThanOrEqualTo, value);
    }

    public Selection isOnOrBefore(LocalDateTime value) {
        return eval(isLessThanOrEqualTo, PackedLocalDateTime.pack(value));
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
    public Selection isMissing() {
        return eval(isMissing);
    }

    @Override
    public Selection isNotMissing() {
        return eval(isNotMissing);
    }

    @Override
    public void append(Column column) {
        Preconditions.checkArgument(column.type() == this.type());
        DateTimeColumn doubleColumn = (DateTimeColumn) column;
        for (int i = 0; i < doubleColumn.size(); i++) {
            add(doubleColumn.get(i));
        }
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

    public NumberColumn minuteOfDay() {
        NumberColumn newColumn = NumberColumn.create(this.name() + " minute of day");
        for (int r = 0; r < this.size(); r++) {
            long c1 = getPackedDateTime(r);
            if (c1 == DateTimeColumn.MISSING_VALUE) {
                newColumn.append(NumberColumn.MISSING_VALUE);
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
        return eval(PackedLocalDateTime::isMonday);
    }

    public Selection isTuesday() {
        return eval(PackedLocalDateTime::isTuesday);
    }

    public Selection isWednesday() {
        return eval(PackedLocalDateTime::isWednesday);
    }

    public Selection isThursday() {
        return eval(PackedLocalDateTime::isThursday);
    }

    public Selection isFriday() {
        return eval(PackedLocalDateTime::isFriday);
    }

    public Selection isSaturday() {
        return eval(PackedLocalDateTime::isSaturday);
    }

    public Selection isSunday() {
        return eval(PackedLocalDateTime::isSunday);
    }

    public Selection isInJanuary() {
        return eval(PackedLocalDateTime::isInJanuary);
    }

    public Selection isInFebruary() {
        return eval(PackedLocalDateTime::isInFebruary);
    }

    public Selection isInMarch() {
        return eval(PackedLocalDateTime::isInMarch);
    }

    public Selection isInApril() {
        return eval(PackedLocalDateTime::isInApril);
    }

    public Selection isInMay() {
        return eval(PackedLocalDateTime::isInMay);
    }

    public Selection isInJune() {
        return eval(PackedLocalDateTime::isInJune);
    }

    public Selection isInJuly() {
        return eval(PackedLocalDateTime::isInJuly);
    }

    public Selection isInAugust() {
        return eval(PackedLocalDateTime::isInAugust);
    }

    public Selection isInSeptember() {
        return eval(PackedLocalDateTime::isInSeptember);
    }

    public Selection isInOctober() {
        return eval(PackedLocalDateTime::isInOctober);
    }

    public Selection isInNovember() {
        return eval(PackedLocalDateTime::isInNovember);
    }

    public Selection isInDecember() {
        return eval(PackedLocalDateTime::isInDecember);
    }

    public Selection isFirstDayOfMonth() {
        return eval(PackedLocalDateTime::isFirstDayOfMonth);
    }

    public Selection isLastDayOfMonth() {
        return eval(PackedLocalDateTime::isLastDayOfMonth);
    }

    public Selection isInQ1() {
        return eval(PackedLocalDateTime::isInQ1);
    }

    public Selection isInQ2() {
        return eval(PackedLocalDateTime::isInQ2);
    }

    public Selection isInQ3() {
        return eval(PackedLocalDateTime::isInQ3);
    }

    public Selection isInQ4() {
        return eval(PackedLocalDateTime::isInQ4);
    }

    public Selection isNoon() {
        return eval(PackedLocalDateTime::isNoon);
    }

    public Selection isMidnight() {
        return eval(PackedLocalDateTime::isMidnight);
    }

    public Selection isBeforeNoon() {
        return eval(PackedLocalDateTime::AM);
    }

    public Selection isAfterNoon() {
        return eval(PackedLocalDateTime::PM);
    }

    public Selection eval(LongPredicate predicate) {
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
        data.set(index, value);
    }

    public void set(int index, LocalDateTime value) {
        data.set(index, PackedLocalDateTime.pack(value));
    }

    public Selection eval(LongBiPredicate predicate, long value) {
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

    public Selection isInYear(int year) {
        return eval(i -> PackedLocalDateTime.isInYear(i, year));
    }

    public boolean contains(LocalDateTime dateTime) {
        long dt = PackedLocalDateTime.pack(dateTime);
        return data().contains(dt);
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

}
