package com.github.lwhite1.tablesaw.api;

import com.github.lwhite1.tablesaw.columns.AbstractColumn;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.columns.LongColumnUtils;
import com.github.lwhite1.tablesaw.columns.packeddata.PackedLocalDateTime;
import com.github.lwhite1.tablesaw.filtering.LocalDateTimePredicate;
import com.github.lwhite1.tablesaw.filtering.LongBiPredicate;
import com.github.lwhite1.tablesaw.filtering.LongPredicate;
import com.github.lwhite1.tablesaw.io.TypeUtils;
import com.github.lwhite1.tablesaw.mapping.DateTimeMapUtils;
import com.github.lwhite1.tablesaw.store.ColumnMetadata;
import com.github.lwhite1.tablesaw.util.ReverseLongComparator;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongIterable;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import org.roaringbitmap.RoaringBitmap;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A column in a table that contains long-integer encoded (packed) local date-time values
 */
public class DateTimeColumn extends AbstractColumn implements DateTimeMapUtils, LongIterable {

  public static final long MISSING_VALUE = Long.MIN_VALUE;

  private static final int BYTE_SIZE = 8;

  private static int DEFAULT_ARRAY_SIZE = 128;

  private LongArrayList data;

  /**
   * The formatter chosen to parse date-time strings for this particular column
   */
  private DateTimeFormatter selectedFormatter;

  @Override
  public void addCell(String stringValue) {
    if (stringValue == null) {
      add(Long.MIN_VALUE);
    } else {
      long dateTime = convert(stringValue);
      add(dateTime);
    }
  }

  public void add(LocalDateTime dateTime) {
    long dt = PackedLocalDateTime.pack(dateTime);
    add(dt);
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
      return Long.MIN_VALUE;
    }
    value = Strings.padStart(value, 4, '0');
    if (selectedFormatter == null) {
      selectedFormatter = TypeUtils.getDateTimeFormatter(value);
    }
    LocalDateTime time;
    try {
      time = LocalDateTime.parse(value, selectedFormatter);
    } catch (DateTimeParseException e) {
      selectedFormatter = TypeUtils.DATE_TIME_FORMATTER;
      time = LocalDateTime.parse(value, selectedFormatter);
    }
    return PackedLocalDateTime.pack(time);
  }

  public static DateTimeColumn create(String name) {
    return new DateTimeColumn(name);
  }

  private DateTimeColumn(String name) {
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

  public void add(long dateTime) {
    data.add(dateTime);
  }

  @Override
  public String getString(int row) {
    return PackedLocalDateTime.toString(getLong(row));
  }

  @Override
  public DateTimeColumn emptyCopy() {
    return new DateTimeColumn(name());
  }

  @Override
  public DateTimeColumn emptyCopy(int rowSize) {
    return new DateTimeColumn(name(), rowSize);
  }

  @Override
  public void clear() {
    data.clear();
  }

  @Override
  public DateTimeColumn copy() {
    return DateTimeColumn.create(name(), data);
  }

  @Override
  public void sortAscending() {
    Arrays.parallelSort(data.elements());
  }

  @Override
  public void sortDescending() {
    LongArrays.parallelQuickSort(data.elements(), reverseLongComparator);
  }

  LongComparator reverseLongComparator = new LongComparator() {

    @Override
    public int compare(Long o2, Long o1) {
      return (o1 < o2 ? -1 : (o1.equals(o2) ? 0 : 1));
    }

    @Override
    public int compare(long o2, long o1) {
      return (o1 < o2 ? -1 : (o1 == o2 ? 0 : 1));
    }
  };

  // TODO(lwhite): Implement column summary()
  @Override
  public Table summary() {
    return new Table("Column: " + "Unimplemented");
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
    return DateTimeColumn.create(name() + " Unique values",
        LongArrayList.wrap(ints.toLongArray()));
  }

  @Override
  public boolean isEmpty() {
    return data.isEmpty();
  }

  public long getLong(int index) {
    return data.getLong(index);
  }

  public LocalDateTime get(int index) {
    return PackedLocalDateTime.asLocalDateTime(getLong(index));
  }

  @Override
  public IntComparator rowComparator() {
    return comparator;
  }

  IntComparator comparator = new IntComparator() {

    @Override
    public int compare(Integer r1, Integer r2) {
      return compare((int) r1, (int) r2);
    }

    @Override
    public int compare(int r1, int r2) {
      long f1 = getLong(r1);
      long f2 = getLong(r2);
      return Long.compare(f1, f2);
    }
  };

  public CategoryColumn dayOfWeek() {
    CategoryColumn newColumn = CategoryColumn.create(this.name() + " day of week", this.size());
    for (int r = 0; r < this.size(); r++) {
      long c1 = this.getLong(r);
      if (c1 == (DateTimeColumn.MISSING_VALUE)) {
        newColumn.set(r, null);
      } else {
        newColumn.add(PackedLocalDateTime.getDayOfWeek(c1).toString());
      }
    }
    return newColumn;
  }

  public IntColumn dayOfYear() {
    IntColumn newColumn = IntColumn.create(this.name() + " day of year", this.size());
    for (int r = 0; r < this.size(); r++) {
      long c1 = this.getLong(r);
      if (c1 == (DateTimeColumn.MISSING_VALUE)) {
        newColumn.add(IntColumn.MISSING_VALUE);
      } else {
        newColumn.add(PackedLocalDateTime.getDayOfYear(c1));
      }
    }
    return newColumn;
  }

  public IntColumn dayOfMonth() {
    IntColumn newColumn = IntColumn.create(this.name() + " day of month");
    for (int r = 0; r < this.size(); r++) {
      long c1 = this.getLong(r);
      if (c1 == FloatColumn.MISSING_VALUE) {
        newColumn.add(IntColumn.MISSING_VALUE);
      } else {
        newColumn.add(PackedLocalDateTime.getDayOfMonth(c1));
      }
    }
    return newColumn;
  }

  public IntColumn monthNumber() {
    IntColumn newColumn = IntColumn.create(this.name() + " month");
    for (int r = 0; r < this.size(); r++) {
      long c1 = this.getLong(r);
      if (c1 == MISSING_VALUE) {
        newColumn.add(IntColumn.MISSING_VALUE);
      } else {
        newColumn.add(PackedLocalDateTime.getMonthValue(c1));
      }
    }
    return newColumn;
  }

  public CategoryColumn monthName() {
    CategoryColumn newColumn = CategoryColumn.create(this.name() + " month");
    for (int r = 0; r < this.size(); r++) {
      long c1 = this.getLong(r);
      if (c1 == MISSING_VALUE) {
        newColumn.add(CategoryColumn.MISSING_VALUE);
      } else {
        newColumn.add(Month.of(PackedLocalDateTime.getMonthValue(c1)).name());
      }
    }
    return newColumn;
  }

  public IntColumn year() {
    IntColumn newColumn = IntColumn.create(this.name() + " year");
    for (int r = 0; r < this.size(); r++) {
      long c1 = this.getLong(r);
      if (c1 == MISSING_VALUE) {
        newColumn.add(IntColumn.MISSING_VALUE);
      } else {
        newColumn.add(PackedLocalDateTime.getYear(PackedLocalDateTime.date(c1)));
      }
    }
    return newColumn;
  }

  public RoaringBitmap isEqualTo(LocalDateTime value) {
    long packed = PackedLocalDateTime.pack(value);
    return apply(LongColumnUtils.isEqualTo, packed);
  }

  public RoaringBitmap isEqualTo(DateTimeColumn column) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    LongIterator intIterator = column.iterator();
    for (long next : data) {
      if (next == intIterator.nextLong()) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isAfter(LocalDateTime value) {
    return apply(LongColumnUtils.isGreaterThan, PackedLocalDateTime.pack(value));
  }

  public RoaringBitmap isOnOrAfter(long value) {
    return apply(LongColumnUtils.isGreaterThanOrEqualTo, value);
  }

  public RoaringBitmap isBefore(LocalDateTime value) {
    return apply(LongColumnUtils.isLessThan, PackedLocalDateTime.pack(value));
  }

  public RoaringBitmap isOnOrBefore(long value) {
    return apply(LongColumnUtils.isLessThanOrEqualTo, value);
  }

  public RoaringBitmap isAfter(DateTimeColumn column) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    LongIterator intIterator = column.iterator();
    for (long next : data) {
      if (next > intIterator.nextLong()) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isBefore(DateTimeColumn column) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    LongIterator intIterator = column.iterator();
    for (long next : data) {
      if (next < intIterator.nextLong()) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public static DateTimeColumn create(String fileName, LongArrayList dateTimes) {
    DateTimeColumn column = new DateTimeColumn(fileName, dateTimes.size());
    column.data.addAll(dateTimes);
    return column;
  }

  /**
   * Returns the count of missing values in this column
   */
  @Override
  public int countEmpty() {
    int count = 0;
    for (int i = 0; i < size(); i++) {
      if (getLong(i) == MISSING_VALUE) {
        count++;
      }
    }
    return count;
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
  public RoaringBitmap isMissing() {
    return apply(isMissing);
  }

  @Override
  public RoaringBitmap isNotMissing() {
    return apply(isNotMissing);
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
    long missing = Long.MIN_VALUE;
    if (!isEmpty()) {
      max = getLong(0);
    } else {
      return null;
    }
    for (long aData : data) {
      if (missing != aData) {
        max = (max > aData) ? max : aData;
      }
    }

    if (missing == max) {
      return null;
    }
    return PackedLocalDateTime.asLocalDateTime(max);
  }

  public LocalDateTime min() {
    long min;
    long missing = Long.MIN_VALUE;

    if (!isEmpty()) {
      min = getLong(0);
    } else {
      return null;
    }
    for (long aData : data) {
      if (missing != aData) {
        min = (min < aData) ? min : aData;
      }
    }
    if (Integer.MIN_VALUE == min) {
      return null;
    }
    return PackedLocalDateTime.asLocalDateTime(min);
  }

  public IntColumn minuteOfDay() {
    IntColumn newColumn = IntColumn.create(this.name() + " minute of day");
    for (int r = 0; r < this.size(); r++) {
      long c1 = getLong(r);
      if (c1 == DateColumn.MISSING_VALUE) {
        newColumn.add(IntColumn.MISSING_VALUE);
      } else {
        newColumn.add(PackedLocalDateTime.getMinuteOfDay(c1));
      }
    }
    return newColumn;
  }

  public DateTimeColumn selectIf(LocalDateTimePredicate predicate) {
    DateTimeColumn column = emptyCopy();
    LongIterator iterator = iterator();
    while (iterator.hasNext()) {
      long next = iterator.nextLong();
      if (predicate.test(PackedLocalDateTime.asLocalDateTime(next))) {
        column.add(next);
      }
    }
    return column;
  }

  public DateTimeColumn selectIf(LongPredicate predicate) {
    DateTimeColumn column = emptyCopy();
    LongIterator iterator = iterator();
    while (iterator.hasNext()) {
      long next = iterator.nextLong();
      if (predicate.test(next)) {
        column.add(next);
      }
    }
    return column;
  }

  public RoaringBitmap isMonday() {
    return apply(PackedLocalDateTime::isMonday);
  }

  public RoaringBitmap isTuesday() {
    return apply(PackedLocalDateTime::isTuesday);
  }

  public RoaringBitmap isWednesday() {
    return apply(PackedLocalDateTime::isWednesday);
  }

  public RoaringBitmap isThursday() {
    return apply(PackedLocalDateTime::isThursday);
  }

  public RoaringBitmap isFriday() {
    return apply(PackedLocalDateTime::isFriday);
  }

  public RoaringBitmap isSaturday() {
    return apply(PackedLocalDateTime::isSaturday);
  }

  public RoaringBitmap isSunday() {
    return apply(PackedLocalDateTime::isSunday);
  }

  public RoaringBitmap isInJanuary() {
    return apply(PackedLocalDateTime::isInJanuary);
  }

  public RoaringBitmap isInFebruary() {
    return apply(PackedLocalDateTime::isInFebruary);
  }

  public RoaringBitmap isInMarch() {
    return apply(PackedLocalDateTime::isInMarch);
  }

  public RoaringBitmap isInApril() {
    return apply(PackedLocalDateTime::isInApril);
  }

  public RoaringBitmap isInMay() {
    return apply(PackedLocalDateTime::isInMay);
  }

  public RoaringBitmap isInJune() {
    return apply(PackedLocalDateTime::isInJune);
  }

  public RoaringBitmap isInJuly() {
    return apply(PackedLocalDateTime::isInJuly);
  }

  public RoaringBitmap isInAugust() {
    return apply(PackedLocalDateTime::isInAugust);
  }

  public RoaringBitmap isInSeptember() {
    return apply(PackedLocalDateTime::isInSeptember);
  }

  public RoaringBitmap isInOctober() {
    return apply(PackedLocalDateTime::isInOctober);
  }

  public RoaringBitmap isInNovember() {
    return apply(PackedLocalDateTime::isInNovember);
  }

  public RoaringBitmap isInDecember() {
    return apply(PackedLocalDateTime::isInDecember);
  }

  public RoaringBitmap isFirstDayOfMonth() {
    return apply(PackedLocalDateTime::isFirstDayOfMonth);
  }

  public RoaringBitmap isLastDayOfMonth() {
    return apply(PackedLocalDateTime::isLastDayOfMonth);
  }

  public RoaringBitmap isInQ1() {
    return apply(PackedLocalDateTime::isInQ1);
  }

  public RoaringBitmap isInQ2() {
    return apply(PackedLocalDateTime::isInQ2);
  }

  public RoaringBitmap isInQ3() {
    return apply(PackedLocalDateTime::isInQ3);
  }

  public RoaringBitmap isInQ4() {
    return apply(PackedLocalDateTime::isInQ4);
  }

  public RoaringBitmap isNoon() {
    return apply(PackedLocalDateTime::isNoon);
  }

  public RoaringBitmap isMidnight() {
    return apply(PackedLocalDateTime::isMidnight);
  }

  public RoaringBitmap isBeforeNoon() {
    return apply(PackedLocalDateTime::AM);
  }

  public RoaringBitmap isAfterNoon() {
    return apply(PackedLocalDateTime::PM);
  }

  public RoaringBitmap apply(LongPredicate predicate) {
    RoaringBitmap bitmap = new RoaringBitmap();
    for (int idx = 0; idx < data.size(); idx++) {
      long next = data.getLong(idx);
      if (predicate.test(next)) {
        bitmap.add(idx);
      }
    }
    return bitmap;
  }

  public RoaringBitmap apply(LongBiPredicate predicate, long value) {
    RoaringBitmap bitmap = new RoaringBitmap();
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

  public LongIterator iterator() {
    return data.iterator();
  }

  public Set<LocalDateTime> asSet() {
    Set<LocalDateTime> times = new HashSet<>();
    DateTimeColumn unique = unique();
    for (long i : unique) {
      times.add(PackedLocalDateTime.asLocalDateTime(i));
    }
    return times;
  }

  public RoaringBitmap isInYear(int year) {
    return apply(i -> PackedLocalDateTime.isInYear(i, year));
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
    return ByteBuffer.allocate(8).putLong(getLong(rowNumber)).array();
  }
}
