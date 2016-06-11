package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.Table;
import com.github.lwhite1.tablesaw.store.ColumnMetadata;
import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.columns.packeddata.PackedLocalDateTime;
import com.github.lwhite1.tablesaw.filter.LocalDateTimePredicate;
import com.github.lwhite1.tablesaw.filter.LongBiPredicate;
import com.github.lwhite1.tablesaw.filter.LongPredicate;
import com.github.lwhite1.tablesaw.io.TypeUtils;
import com.github.lwhite1.tablesaw.mapper.DateTimeMapUtils;
import com.github.lwhite1.tablesaw.util.ReverseLongComparator;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.longs.*;
import org.roaringbitmap.RoaringBitmap;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A column in a base table that contains float values
 */
public class LocalDateTimeColumn extends AbstractColumn implements DateTimeMapUtils {

  public static final long MISSING_VALUE = Long.MIN_VALUE;

  private static int DEFAULT_ARRAY_SIZE = 128;

  private LongArrayList data;

  @Override
  public void addCell(String stringValue) {

    if (stringValue == null) {
      add(Long.MIN_VALUE);
    } else {
      LocalDateTime dateTime = convert(stringValue);
      if (dateTime != null) {
        add(dateTime);
      } else {
        add(Long.MIN_VALUE);
      }
    }
  }

  public void add(LocalDateTime dateTime) {
    long dt = PackedLocalDateTime.pack(dateTime);
    add(dt);
  }

  public static LocalDateTime convert(String value) {
    if (Strings.isNullOrEmpty(value)
        || TypeUtils.MISSING_INDICATORS.contains(value)
        || value.equals("-1")) {
      return null;
    }
    value = Strings.padStart(value, 4, '0');
    return LocalDateTime.parse(value, TypeUtils.DATE_TIME_FORMATTER);
  }

  public static LocalDateTimeColumn create(String name) {
    return new LocalDateTimeColumn(name);
  }

  private LocalDateTimeColumn(String name) {
    super(name);
    data = new LongArrayList(DEFAULT_ARRAY_SIZE);
  }

  public LocalDateTimeColumn(ColumnMetadata metadata) {
    super(metadata);
    data = new LongArrayList(DEFAULT_ARRAY_SIZE);
  }

  public LocalDateTimeColumn(String name, int initialSize) {
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
  public LocalDateTimeColumn emptyCopy() {
    return new LocalDateTimeColumn(name());
  }

  @Override
  public void clear() {
    data.clear();
  }

  private LocalDateTimeColumn copy() {
    return LocalDateTimeColumn.create(name(), data);
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
  public LocalDateTimeColumn unique() {
    LongSet ints = new LongOpenHashSet(data.size());
    for (long i : data) {
      ints.add(i);
    }
    return LocalDateTimeColumn.create(name() + " Unique values",
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
      if (c1 == (LocalDateTimeColumn.MISSING_VALUE)) {
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
      if (c1 == (LocalDateTimeColumn.MISSING_VALUE)) {
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

  public static LocalDateTimeColumn create(String fileName, LongArrayList dateTimes) {
    LocalDateTimeColumn column = new LocalDateTimeColumn(fileName, dateTimes.size());
    column.data = dateTimes;
    return column;
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
  public String toString() {
    return "LocalDateTime column: " + name();
  }

  @Override
  public void append(Column column) {
    Preconditions.checkArgument(column.type() == this.type());
    LocalDateTimeColumn intColumn = (LocalDateTimeColumn) column;
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
      if (c1 == LocalDateColumn.MISSING_VALUE) {
        newColumn.add(IntColumn.MISSING_VALUE);
      } else {
        newColumn.add(PackedLocalDateTime.getMinuteOfDay(c1));
      }
    }
    return newColumn;
  }

  public LocalDateTimeColumn selectIf(LocalDateTimePredicate predicate) {
    LocalDateTimeColumn column = emptyCopy();
    LongIterator iterator = iterator();
    while (iterator.hasNext()) {
      long next = iterator.nextLong();
      if (predicate.test(PackedLocalDateTime.asLocalDateTime(next))) {
        column.add(next);
      }
    }
    return column;
  }

  public LocalDateTimeColumn selectIf(LongPredicate predicate) {
    LocalDateTimeColumn column = emptyCopy();
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
  public List<LocalDateTime> max(int n) {
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
  public List<LocalDateTime> min(int n) {
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
}
