package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.View;
import com.deathrayresearch.outlier.filter.IntPredicate;
import com.deathrayresearch.outlier.filter.LocalDatePredicate;
import com.deathrayresearch.outlier.io.TypeUtils;
import com.deathrayresearch.outlier.mapper.DateMapUtils;
import com.deathrayresearch.outlier.store.ColumnMetadata;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.roaringbitmap.RoaringBitmap;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

/**
 * A column in a base table that contains float values
 */
public class LocalDateColumn extends AbstractColumn implements DateMapUtils {

  public static final int MISSING_VALUE = (int) ColumnType.LOCAL_DATE.getMissingValue() ;

  private static final int DEFAULT_ARRAY_SIZE = 128;

  private IntArrayList data;

  private LocalDateColumn(String name) {
    super(name);
    data = new IntArrayList(DEFAULT_ARRAY_SIZE);
  }

  public LocalDateColumn(ColumnMetadata metadata) {
    super(metadata);
    data = new IntArrayList(DEFAULT_ARRAY_SIZE);
  }

  private LocalDateColumn(String name, int initialSize) {
    super(name);
    data = new IntArrayList(initialSize);
  }

  public int size() {
    return data.size();
  }

  @Override
  public ColumnType type() {
    return ColumnType.LOCAL_DATE;
  }

  public void add(int f) {
    data.add(f);
  }

  public IntArrayList data() {
    return data;
  }

  public void set(int index, int value) {
    data.set(index, value);
  }

  public void add(LocalDate f) {
    add(PackedLocalDate.pack(f));
  }

  @Override
  public String getString(int row) {
    return PackedLocalDate.toDateString(getInt(row));
  }

  @Override
  public LocalDateColumn emptyCopy() {
    return new LocalDateColumn(name());
  }

  @Override
  public void clear() {
    data.clear();
  }

  private LocalDateColumn copy() {
    return LocalDateColumn.create(name(), data);
  }

  @Override
  public void sortAscending() {
    Arrays.parallelSort(data.elements());
  }

  @Override
  public void sortDescending() {
    IntArrays.parallelQuickSort(data.elements(), reverseIntComparator);
  }

  IntComparator reverseIntComparator =  new IntComparator() {

    @Override
    public int compare(Integer o2, Integer o1) {
      return (o1 < o2 ? -1 : (o1.equals(o2) ? 0 : 1));
    }

    @Override
    public int compare(int o2, int o1) {
      return (o1 < o2 ? -1 : (o1 == o2 ? 0 : 1));
    }
  };

  @Override
  public int countUnique() {
    IntSet ints = new IntOpenHashSet(size());
    for (int i = 0; i < size(); i++) {
      ints.add(data.getInt(i));
    }
    return ints.size();
  }

  @Override
  public LocalDateColumn unique() {
    IntSet ints = new IntOpenHashSet(data.size());
    for (int i = 0; i < size(); i++) {
      ints.add(data.getInt(i));
    }
    return LocalDateColumn.create(name() + " Unique values", IntArrayList.wrap(ints.toIntArray()));
  }

  public LocalDate firstElement() {
    if (isEmpty()) {
      return null;
    }
    return PackedLocalDate.asLocalDate(getInt(0));
  }

  public LocalDate max() {
    int max;
    int missing = Integer.MIN_VALUE;
    if (!isEmpty()) {
      max = getInt(0);
    } else {
      return null;
    }
    for (int aData : data) {
      if (missing != aData) {
        max = (max > aData) ? max : aData;
      }
    }

    if (missing == max) {
      return null;
    }
    return PackedLocalDate.asLocalDate(max);
  }

  public LocalDate min() {
    int min;
    int missing = Integer.MIN_VALUE;

    if (!isEmpty()) {
      min = getInt(0);
    } else {
      return null;
    }
    for (int aData : data) {
      if (missing != aData) {
        min = (min < aData) ? min : aData;
      }
    }
    if (Integer.MIN_VALUE == min) {
      return null;
    }
    return PackedLocalDate.asLocalDate(min);
  }

  public CategoryColumn dayOfWeek() {
    CategoryColumn newColumn = CategoryColumn.create(this.name() + " day of week");
    for (int r = 0; r < this.size(); r++) {
      int c1 = this.getInt(r);
      if (c1 == LocalDateColumn.MISSING_VALUE) {
        newColumn.add(null);
      } else {
        newColumn.add(PackedLocalDate.getDayOfWeek(c1).toString());
      }
    }
    return newColumn;
  }

  public IntColumn dayOfMonth() {
    IntColumn newColumn = IntColumn.create(this.name() + " day of month");
    for (int r = 0; r < this.size(); r++) {
      int c1 = this.getInt(r);
      if (c1 == LocalDateColumn.MISSING_VALUE) {
        newColumn.add(IntColumn.MISSING_VALUE);
      } else {
        newColumn.add(PackedLocalDate.getDayOfMonth(c1));
      }
    }
    return newColumn;
  }

  public IntColumn dayOfYear() {
    IntColumn newColumn = IntColumn.create(this.name() + " day of month");
    for (int r = 0; r < this.size(); r++) {
      int c1 = this.getInt(r);
      if (c1 == LocalDateColumn.MISSING_VALUE) {
        newColumn.add(IntColumn.MISSING_VALUE);
      } else {
        newColumn.add(PackedLocalDate.getDayOfYear(c1));
      }
    }
    return newColumn;
  }

  public IntColumn month() {
    IntColumn newColumn = IntColumn.create(this.name() + " month");

    for (int r = 0; r < this.size(); r++) {
      int c1 = this.getInt(r);
      if (c1 == LocalDateColumn.MISSING_VALUE) {
        newColumn.add(IntColumn.MISSING_VALUE);
      } else {
        newColumn.add(PackedLocalDate.getMonthValue(c1));
      }
    }
    return newColumn;
  }

  public LocalDate get(int index) {
    return PackedLocalDate.asLocalDate(getInt(index));
  }

  public static LocalDateColumn create(String name) {
    return new LocalDateColumn(name);
  }

  @Override
  public boolean isEmpty() {
    return data.isEmpty();
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
      int f1 = getInt(r1);
      int f2 = getInt(r2);
      return Integer.compare(f1, f2);
    }
  };

  public static LocalDateColumn create(String columnName, IntArrayList dates) {
    LocalDateColumn column = new LocalDateColumn(columnName, dates.size());
    column.data = dates;
    return column;
  }

  public static int convert(String value) {
    if (Strings.isNullOrEmpty(value)
        || TypeUtils.MISSING_INDICATORS.contains(value)
        || value.equals("-1")) {
      return (int) ColumnType.LOCAL_DATE.getMissingValue();
    }
    value = Strings.padStart(value, 4, '0');
    return PackedLocalDate.pack(LocalDate.parse(value, TypeUtils.DATE_FORMATTER));
  }

  public void addCell(String string) {
    try {
      add(convert(string));
    } catch (NullPointerException e) {
      throw new RuntimeException(name() + ": "
          + string + ": "
          + e.getMessage());
    }
  }

  public int getInt(int index) {
    return data.getInt(index);
  }

  public RoaringBitmap isEqualTo(LocalDate value) {
    RoaringBitmap results = new RoaringBitmap();
    int packedLocalDate = PackedLocalDate.pack(value);
    int i = 0;
    for (int next : data) {
      if (packedLocalDate == next) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isEqualTo(LocalDateColumn column) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    IntIterator intIterator = column.iterator();
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
   */
  @Override
  public View summary() {

    Int2IntOpenHashMap counts = new Int2IntOpenHashMap();

    for (int i = 0; i < size(); i++) {
      int value;
      int next = getInt(i);
      if (next == Integer.MIN_VALUE) {
        value = LocalDateColumn.MISSING_VALUE;
      } else {
        value = next;
      }
      if (counts.containsKey(value)) {
        counts.addTo(value, 1);
      } else {
        counts.put(value, 1);
      }
    }
    Table table = new Table(name());
    table.addColumn(LocalDateColumn.create("Date"));
    table.addColumn(IntColumn.create("Count"));

    for (Int2IntMap.Entry entry : counts.int2IntEntrySet()) {
      table.localDateColumn(0).add(entry.getIntKey());
      table.intColumn(1).add(entry.getIntValue());
    }
    table = table.sortDescendingOn("Count");

    return table.head(5);
  }

  public LocalDateTimeColumn atTime(LocalTimeColumn c) {
    LocalDateTimeColumn newColumn = LocalDateTimeColumn.create(this.name() + " " + c.name());
    for (int r = 0; r < this.size(); r++) {
      int c1 = this.getInt(r);
      int c2 = c.getInt(r);
      if (c1 == MISSING_VALUE || c2 == LocalTimeColumn.MISSING_VALUE) {
        newColumn.add(LocalDateTimeColumn.MISSING_VALUE);
      } else {
        LocalDate value1 = PackedLocalDate.asLocalDate(c1);
        LocalTime time = PackedLocalTime.asLocalTime(c2);
        newColumn.add(PackedLocalDateTime.pack(value1, time));
      }
    }
    return newColumn;
  }

  public RoaringBitmap isInQ1() {

    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isInQ1(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isInQ2() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isInQ2(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isInQ3() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isInQ3(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isInQ4() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isInQ4(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isAfter(int value) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isAfter(next, value)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isBefore(int value) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isBefore(next, value)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isMonday() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isMonday(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isTuesday() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isTuesday(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isWednesday() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isWednesday(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }
  public RoaringBitmap isThursday() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isThursday(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isFriday() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isFriday(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isSaturday() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isSaturday(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isSunday() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isSunday(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isInJanuary() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isInJanuary(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isInFebruary() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isInFebruary(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isInMarch() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isInMarch(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isInApril() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isInApril(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isInMay() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isInMay(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isInJune() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isInJune(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isInJuly() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isInJuly(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isInAugust() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isInAugust(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isInSeptember() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isInSeptember(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isInOctober() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isInOctober(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isInNovember() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isInNovember(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isInDecember() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isInDecember(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isFirstDayOfMonth() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isFirstDayOfMonth(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isLastDayOfMonth() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isLastDayOfMonth(next)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isInYear(int year) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedLocalDate.isInYear(next, year)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

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
  public String toString() {
    return "LocalDate column: " + name();
  }

  @Override
  public void append(Column column) {
    Preconditions.checkArgument(column.type() == this.type());
    LocalDateColumn intColumn = (LocalDateColumn) column;
    for (int i = 0; i < intColumn.size(); i++) {
      add(intColumn.getInt(i));
    }
  }

  public LocalDateColumn selectIf(LocalDatePredicate predicate) {
    LocalDateColumn column = emptyCopy();
    IntIterator iterator = iterator();
    while(iterator.hasNext()) {
      int next = iterator.nextInt();
      if (predicate.test(PackedLocalDate.asLocalDate(next))) {
        column.add(next);
      }
    }
    return column;
  }

  /**
   * This version operates on predicates that treat the given IntPredicate as operating on a packed local time
   * This is much more efficient that using a LocalTimePredicate, but requires that the developer understand the
   * semantics of packedLocalTimes
   */
  public LocalDateColumn selectIf(IntPredicate predicate) {
    LocalDateColumn column = emptyCopy();
    IntIterator iterator = iterator();
    while(iterator.hasNext()) {
      int next = iterator.nextInt();
      if (predicate.test(next)) {
        column.add(next);
      }
    }
    return column;
  }

  //TODO(lwhite): Implement
  @Override
  public LocalDateColumn max(int n) {
    return null;
  }

  //TODO(lwhite): Implement
  @Override
  public LocalDateColumn min(int n) {
    return null;
  }


  public IntIterator iterator() {
    return data.iterator();
  }
}
