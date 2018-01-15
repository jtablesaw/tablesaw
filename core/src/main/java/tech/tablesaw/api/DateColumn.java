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
import tech.tablesaw.columns.IntColumnUtils;
import tech.tablesaw.columns.packeddata.PackedLocalDate;
import tech.tablesaw.columns.packeddata.PackedLocalDateTime;
import tech.tablesaw.filtering.IntBiPredicate;
import tech.tablesaw.filtering.IntPredicate;
import tech.tablesaw.filtering.LocalDatePredicate;
import tech.tablesaw.io.TypeUtils;
import tech.tablesaw.mapping.DateMapUtils;
import tech.tablesaw.store.ColumnMetadata;
import tech.tablesaw.util.BitmapBackedSelection;
import tech.tablesaw.util.ReverseIntComparator;
import tech.tablesaw.util.Selection;

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

/**
 * A column in a base table that contains float values
 */
public class DateColumn extends AbstractColumn implements DateMapUtils {

    public static final int MISSING_VALUE = (Integer) ColumnType.LOCAL_DATE.getMissingValue();

    private static final int DEFAULT_ARRAY_SIZE = 128;

    private static final int BYTE_SIZE = 4;

    private IntComparator reverseIntComparator = new IntComparator() {

        @Override
        public int compare(Integer o2, Integer o1) {
            return (o1 < o2 ? -1 : (o1.equals(o2) ? 0 : 1));
        }

        @Override
        public int compare(int o2, int o1) {
            return (o1 < o2 ? -1 : (o1 == o2 ? 0 : 1));
        }
    };

    private IntArrayList data;

    IntComparator comparator = new IntComparator() {

        @Override
        public int compare(Integer r1, Integer r2) {
            return compare((int) r1, (int) r2);
        }

        @Override
        public int compare(int r1, int r2) {
            int f1 = getIntInternal(r1);
            int f2 = getIntInternal(r2);
            return Integer.compare(f1, f2);
        }
    };

    /**
     * The formatter chosen to parse dates for this particular column
     */
    private DateTimeFormatter selectedFormatter;
    
    /** locale for formater */
    private final Locale locale;

    public DateColumn(String name) {
        this(name, Locale.getDefault());
    }
    
    public DateColumn(String name, Locale locale) {
        this(name, new IntArrayList(DEFAULT_ARRAY_SIZE), locale);
    }

    public DateColumn(String name, int initialSize) {
        this(name, new IntArrayList(initialSize));
    }

    public DateColumn(String name, List<LocalDate> data) {
      this(name);
      for (LocalDate date : data) {
        append(date);
      }
    }

    private DateColumn(String name, IntArrayList data) {
        this(name, data, Locale.getDefault());
    }

    private DateColumn(String name, IntArrayList data, Locale locale) {
        super(name);
        this.data = data;
        this.locale = locale;
    }

    public DateColumn(ColumnMetadata metadata) {
        this(metadata, Locale.getDefault());
    }

    public DateColumn(ColumnMetadata metadata, Locale locale) {
        super(metadata);
        this.data = new IntArrayList(DEFAULT_ARRAY_SIZE);
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

    @Override
    public String getString(int row) {
        return PackedLocalDate.toDateString(getIntInternal(row));
    }

    @Override
    public DateColumn emptyCopy() {
        DateColumn column = new DateColumn(name());
        column.setComment(comment());
        return column;
    }

    @Override
    public DateColumn emptyCopy(int rowSize) {
        DateColumn column = new DateColumn(name(), rowSize);
        column.setComment(comment());
        return column;
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public DateColumn copy() {
        DateColumn column = new DateColumn(name(), data);
        column.setComment(comment());
        return column;
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
        return new DateColumn(name() + " Unique values", IntArrayList.wrap(ints.toIntArray()));
    }

    public LocalDate firstElement() {
        if (isEmpty()) {
            return null;
        }
        return PackedLocalDate.asLocalDate(getIntInternal(0));
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

    public CategoryColumn dayOfWeek() {
        CategoryColumn newColumn = new CategoryColumn(this.name() + " day of week");
        for (int r = 0; r < this.size(); r++) {
            int c1 = this.getIntInternal(r);
            if (c1 == DateColumn.MISSING_VALUE) {
                newColumn.add(CategoryColumn.MISSING_VALUE);
            } else {
                newColumn.add(PackedLocalDate.getDayOfWeek(c1).toString());
            }
        }
        return newColumn;
    }

    /**
     * Conditionally update this column, replacing current values with newValue for all rows where the current value
     * matches the selection criteria
     *
     * Example:
     * myColumn.set(LocalDate.now(), myColumn.isMissing()); // no more missing values
     */
    public void set(LocalDate newValue, Selection rowSelection) {
        for (int row : rowSelection) {
            set(row, newValue);
        }
    }

    public ShortColumn dayOfWeekValue() {
        ShortColumn newColumn = new ShortColumn(this.name() + " day of week", this.size());
        for (int r = 0; r < this.size(); r++) {
            int c1 = this.getIntInternal(r);
            if (c1 == (DateColumn.MISSING_VALUE)) {
                newColumn.set(r, ShortColumn.MISSING_VALUE);
            } else {
                newColumn.append((short) PackedLocalDate.getDayOfWeek(c1).getValue());
            }
        }
        return newColumn;
    }

    public ShortColumn dayOfMonth() {
        ShortColumn newColumn = new ShortColumn(this.name() + " day of month");
        for (int r = 0; r < this.size(); r++) {
            int c1 = this.getIntInternal(r);
            if (c1 == DateColumn.MISSING_VALUE) {
                newColumn.append(ShortColumn.MISSING_VALUE);
            } else {
                newColumn.append(PackedLocalDate.getDayOfMonth(c1));
            }
        }
        return newColumn;
    }

    public ShortColumn dayOfYear() {
        ShortColumn newColumn = new ShortColumn(this.name() + " day of year");
        for (int r = 0; r < this.size(); r++) {
            int c1 = this.getIntInternal(r);
            if (c1 == DateColumn.MISSING_VALUE) {
                newColumn.append(ShortColumn.MISSING_VALUE);
            } else {
                newColumn.append((short) PackedLocalDate.getDayOfYear(c1));
            }
        }
        return newColumn;
    }

    public ShortColumn monthValue() {
        ShortColumn newColumn = new ShortColumn(this.name() + " month");

        for (int r = 0; r < this.size(); r++) {
            int c1 = this.getIntInternal(r);
            if (c1 == DateColumn.MISSING_VALUE) {
                newColumn.append(ShortColumn.MISSING_VALUE);
            } else {
                newColumn.append(PackedLocalDate.getMonthValue(c1));
            }
        }
        return newColumn;
    }

    /**
     * Returns a CategoryColumn with the year and month from this column concatenated into a String that will sort
     * lexicographically in temporal order.
     *
     * This simplifies the production of plots and tables that aggregate values into standard temporal units (e.g.,
     * you want monthly data but your source data is more than a year long and you don't want months from different
     * years aggregated together).
     */
    public CategoryColumn yearMonthString() {
        CategoryColumn newColumn = new CategoryColumn(this.name() + " year & month");
        for (int r = 0; r < this.size(); r++) {
            int c1 = this.getIntInternal(r);
            if (c1 == MISSING_VALUE) {
                newColumn.append(CategoryColumn.MISSING_VALUE);
            } else {
                String ym = String.valueOf(PackedLocalDate.getYear(c1));
                ym = ym + "-" + Strings.padStart(
                        String.valueOf(PackedLocalDate.getMonthValue(c1)), 2, '0');
                newColumn.append(ym);
            }
        }
        return newColumn;
    }

    public CategoryColumn month() {
        CategoryColumn newColumn = new CategoryColumn(this.name() + " month");

        for (int r = 0; r < this.size(); r++) {
            int c1 = this.getIntInternal(r);
            if (c1 == DateColumn.MISSING_VALUE) {
                newColumn.add(CategoryColumn.MISSING_VALUE);
            } else {
                newColumn.add(PackedLocalDate.getMonth(c1).name());
            }
        }
        return newColumn;
    }

    public ShortColumn year() {
        ShortColumn newColumn = new ShortColumn(this.name() + " year");
        for (int r = 0; r < this.size(); r++) {
            int c1 = this.getIntInternal(r);
            if (c1 == MISSING_VALUE) {
                newColumn.append(ShortColumn.MISSING_VALUE);
            } else {
                newColumn.append(PackedLocalDate.getYear(c1));
            }
        }
        return newColumn;
    }

    public LocalDate get(int index) {
        return PackedLocalDate.asLocalDate(getIntInternal(index));
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

    public Selection isEqualTo(LocalDate value) {
        int packed = PackedLocalDate.pack(value);
        return select(IntColumnUtils.isEqualTo, packed);
    }

    /**
     * Returns a bitmap flagging the records for which the value in this column is equal to the value in the given
     * column
     * Columnwise isEqualTo.
     */
    public Selection isEqualTo(DateColumn column) {
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

    /**
     * Returns a table of dates and the number of observations of those dates
     * 
     * @return the summary table
     */
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

    public Selection isAfter(int value) {
        return select(PackedLocalDate::isAfter, value);
    }

    public Selection isAfter(LocalDate value) {
        int packed = PackedLocalDate.pack(value);
        return select(PackedLocalDate::isAfter, packed);
    }

    public Selection isBefore(int value) {
        return select(PackedLocalDate::isBefore, value);
    }

    public Selection isBefore(LocalDate value) {
        int packed = PackedLocalDate.pack(value);
        return select(PackedLocalDate::isBefore, packed);
    }

    public Selection isOnOrBefore(LocalDate value) {
        int packed = PackedLocalDate.pack(value);
        return select(PackedLocalDate::isOnOrBefore, packed);
    }

    public Selection isOnOrBefore(int value) {
        return select(PackedLocalDate::isOnOrBefore, value);
    }

    public Selection isOnOrAfter(LocalDate value) {
        int packed = PackedLocalDate.pack(value);
        return select(PackedLocalDate::isOnOrAfter, packed);
    }

    public Selection isOnOrAfter(int value) {
        return select(PackedLocalDate::isOnOrAfter, value);
    }

    public Selection isMonday() {
        return select(PackedLocalDate::isMonday);
    }

    public Selection isTuesday() {
        return select(PackedLocalDate::isTuesday);
    }

    public Selection isWednesday() {
        return select(PackedLocalDate::isWednesday);
    }

    public Selection isThursday() {
        return select(PackedLocalDate::isThursday);
    }

    public Selection isFriday() {
        return select(PackedLocalDate::isFriday);
    }

    public Selection isSaturday() {
        return select(PackedLocalDate::isSaturday);
    }

    public Selection isSunday() {
        return select(PackedLocalDate::isSunday);
    }

    public Selection isInJanuary() {
        return select(PackedLocalDate::isInJanuary);
    }

    public Selection isInFebruary() {
        return select(PackedLocalDate::isInFebruary);
    }

    public Selection isInMarch() {
        return select(PackedLocalDate::isInMarch);
    }

    public Selection isInApril() {
        return select(PackedLocalDate::isInApril);
    }

    public Selection isInMay() {
        return select(PackedLocalDate::isInMay);
    }

    public Selection isInJune() {
        return select(PackedLocalDate::isInJune);
    }

    public Selection isInJuly() {
        return select(PackedLocalDate::isInJuly);
    }

    public Selection isInAugust() {
        return select(PackedLocalDate::isInAugust);
    }

    public Selection isInSeptember() {
        return select(PackedLocalDate::isInSeptember);
    }

    public Selection isInOctober() {
        return select(PackedLocalDate::isInOctober);
    }

    public Selection isInNovember() {
        return select(PackedLocalDate::isInNovember);
    }

    public Selection isInDecember() {
        return select(PackedLocalDate::isInDecember);
    }

    public Selection isFirstDayOfMonth() {
        return select(PackedLocalDate::isFirstDayOfMonth);
    }

    public Selection isLastDayOfMonth() {
        return select(PackedLocalDate::isLastDayOfMonth);
    }

    public Selection isInQ1() {
        return select(PackedLocalDate::isInQ1);
    }

    public Selection isInQ2() {
        return select(PackedLocalDate::isInQ2);
    }

    public Selection isInQ3() {
        return select(PackedLocalDate::isInQ3);
    }

    public Selection isInQ4() {
        return select(PackedLocalDate::isInQ4);
    }

    public Selection isInYear(int year) {
        return select(PackedLocalDate::isInYear, year);
    }

    @Override
    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(title());
        for (int next : data) {
            builder.append(String.valueOf(PackedLocalDate.asLocalDate(next)));
            builder.append('\n');
        }
        return builder.toString();
    }

    @Override
    public Selection isMissing() {
        return select(isMissing);
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
    public Selection isNotMissing() {
        return select(isNotMissing);
    }

    @Override
    public String toString() {
        return "LocalDate column: " + name();
    }

    @Override
    public void append(Column column) {
        Preconditions.checkArgument(column.type() == this.type());
        DateColumn intColumn = (DateColumn) column;
        for (int i = 0; i < intColumn.size(); i++) {
            appendInternal(intColumn.getIntInternal(i));
        }
    }

    public DateColumn selectIf(LocalDatePredicate predicate) {
        DateColumn column = emptyCopy();
        IntIterator iterator = intIterator();
        while (iterator.hasNext()) {
            int next = iterator.nextInt();
            if (predicate.test(PackedLocalDate.asLocalDate(next))) {
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
    public DateColumn selectIf(IntPredicate predicate) {
        DateColumn column = emptyCopy();
        IntIterator iterator = intIterator();
        while (iterator.hasNext()) {
            int next = iterator.nextInt();
            if (predicate.test(next)) {
                column.appendInternal(next);
            }
        }
        return column;
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
        IntArrays.parallelQuickSort(values, ReverseIntComparator.instance());
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

    public List<LocalDate> asList() {
      List<LocalDate> dates = new ArrayList<>(size());
      for (Iterator<LocalDate> iter = iterator(); iter.hasNext();) {
          dates.add(iter.next());
      }
      return dates;
    }
 
    public Set<LocalDate> asSet() {
        Set<LocalDate> dates = new HashSet<>();
        DateColumn unique = unique();
        for (LocalDate d : unique) {
            dates.add(d);
        }
        return dates;
    }

    // TODO(lwhite): Is this duplicating the functionality of at()?
    public DateTimeColumn with(TimeColumn timeColumn) {
        String dateTimeColumnName = name() + " : " + timeColumn.name();
        DateTimeColumn dateTimeColumn = new DateTimeColumn(dateTimeColumnName, size());
        for (int row = 0; row < size(); row++) {
            int date = getIntInternal(row);
            int time = timeColumn.getIntInternal(row);
            long packedLocalDateTime = PackedLocalDateTime.create(date, time);
            dateTimeColumn.appendInternal(packedLocalDateTime);
        }
        return dateTimeColumn;
    }

    public boolean contains(LocalDate localDate) {
        int date = PackedLocalDate.pack(localDate);
        return data().contains(date);
    }

    @Override
    public int byteSize() {
        return BYTE_SIZE;
    }

    /**
     * Returns the contents of the cell at rowNumber as a byte[]
     * @param rowNumber the number of the row as int
     */
    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(4).putInt(getIntInternal(rowNumber)).array();
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<LocalDate> iterator() {

        return new Iterator<LocalDate>() {

            IntIterator intIterator = intIterator();

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

    @Override
    public DateColumn difference() {
        throw new UnsupportedOperationException("DateTimeColumn.difference() currently not supported");
/*
        DateColumn returnValue = new DateColumn(this.name(), data.size());
        returnValue.add(DateColumn.MISSING_VALUE);
        for (int current = 1; current > data.size(); current++) {
            LocalDate currentValue = get(current);
            LocalDate nextValue = get(current+1);
            Duration duration = Duration.between(currentValue, nextValue);
            LocalDateTime date =
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(duration.toMillis()), ZoneId.systemDefault());
            returnValue.add(date.toLocalDate());
        }
        return returnValue;
  */
    }

}