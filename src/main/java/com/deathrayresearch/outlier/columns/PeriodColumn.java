package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.View;
import com.deathrayresearch.outlier.io.TypeUtils;
import com.deathrayresearch.outlier.store.ColumnMetadata;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.roaringbitmap.RoaringBitmap;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Map;

/**
 * A column in a table that contains period values. Periods are date oriented time units.
 * They differ from the java period class implementation in that they are more compact, and thus are more limited
 * in the range of durations they can express
 */
public class PeriodColumn extends AbstractColumn {

  public static final int MISSING_VALUE = (int) ColumnType.LOCAL_DATE.getMissingValue() ;
  private static int DEFAULT_ARRAY_SIZE = 128;

  private IntArrayList data;

  public PeriodColumn(ColumnMetadata metadata) {
    super(metadata);
    data = new IntArrayList(DEFAULT_ARRAY_SIZE);
  }

  private PeriodColumn(String name) {
    super(name);
    data = new IntArrayList(DEFAULT_ARRAY_SIZE);
  }

  private PeriodColumn(String name, int initialSize) {
    super(name);
    data = new IntArrayList(initialSize);
  }

  public int size() {
    return data.size();
  }

  @Override
  public ColumnType type() {
    return ColumnType.PERIOD;
  }

  public void add(int f) {
    data.add(f);
  }

  public void add(Period f) {
    add(PackedPeriod.pack(f));
  }

  @Override
  public String getString(int row) {
    return PackedLocalDate.toDateString(getInt(row));
  }

  @Override
  public PeriodColumn emptyCopy() {
    return new PeriodColumn(name());
  }

  @Override
  public void clear() {
    data.clear();
  }

  private PeriodColumn copy() {
    return PeriodColumn.create(name(), data);
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
    IntSet ints = new IntOpenHashSet(data.size());
    for (int i : data) {
      ints.add(i);
    }
    return ints.size();
  }

  public IntArrayList data() {
    return data;
  }

  @Override
  public PeriodColumn unique() {
    IntSet ints = new IntOpenHashSet(data.size());
    for (int i : data) {
      ints.add(i);
    }
    return PeriodColumn.create(name() + " Unique values", IntArrayList.wrap(ints.toIntArray()));
  }

  public Period firstElement() {
    if (isEmpty()) {
      return null;
    }
    return PackedPeriod.asPeriod(getInt(0));
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
      if (c1 == PeriodColumn.MISSING_VALUE) {
        newColumn.set(r, null);
      } else {
        newColumn.set(r, PackedLocalDate.getDayOfWeek(c1).toString());
      }
    }
    return newColumn;
  }

  public IntColumn dayOfMonth() {
    IntColumn newColumn = IntColumn.create(this.name() + " day of month");
    for (int r = 0; r < this.size(); r++) {
      int c1 = this.getInt(r);
      if (c1 == PeriodColumn.MISSING_VALUE) {
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
      if (c1 == PeriodColumn.MISSING_VALUE) {
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
      if (c1 == PeriodColumn.MISSING_VALUE) {
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

  public static PeriodColumn create(String name) {
    return new PeriodColumn(name);
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
      int f1 = getInt(r1);
      int f2 = getInt(r2);
      return Integer.compare(f1, f2);
    }

    @Override
    public int compare(int r1, int r2) {
      int f1 = getInt(r1);
      int f2 = getInt(r2);
      return Integer.compare(f1, f2);
    }
  };

  public static PeriodColumn create(String fileName, IntArrayList periods) {
    PeriodColumn column = new PeriodColumn(fileName, periods.size());
    column.data = periods;
    return column;
  }

  public static int convert(String value) {
    if (Strings.isNullOrEmpty(value)
        || TypeUtils.MISSING_INDICATORS.contains(value)
        || value.equals("-1")) {
      return (int) ColumnType.PERIOD.getMissingValue();
    }
    value = Strings.padStart(value, 4, '0');
    return PackedPeriod.pack(Period.parse(value));
  }

  /**
   * Converts the given String to a period and adds it to the column
   * @param string A string representing a period, in a format supported by Period.parse()
   */
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

  public RoaringBitmap isEqualTo(Period value) {
    RoaringBitmap results = new RoaringBitmap();
    int packedPeriod = PackedPeriod.pack(value);
    int i = 0;
    for (int next : data) {
      if (packedPeriod == next) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  /**
   * Returns a table of dates and the number of observations of those dates
   */
  public View summary() {

    Object2IntMap<Period> counts = new Object2IntOpenHashMap<>();

    for (int i = 0; i < size(); i++) {
      Period value;
      int next = getInt(i);
      if (next == Integer.MIN_VALUE) {
        value = null;
      } else {
        value = PackedPeriod.asPeriod(next);
      }
      if (counts.containsKey(value)) {
        counts.put(value, counts.getInt(value) + 1);
      } else {
        counts.put(value, 1);
      }
    }

    Table table = new Table(name());
    table.addColumn(PeriodColumn.create("Period"));
    table.addColumn(IntColumn.create("Count"));

    for (Map.Entry<Period, Integer> entry : counts.object2IntEntrySet()) {
      //Row row = table.newRow();
      table.periodColumn(0).add(entry.getKey());
      table.intColumn(1).add(entry.getValue());
    }
    table = table.sortDescendingOn("Count");

    return table.head(5);
  }


  public RoaringBitmap isLongerThan(int value) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedPeriod.isLongerThan(next, value)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isShorterThan(int value) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (PackedPeriod.isShorterThan(next, value)) {
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
      builder.append(String.valueOf(PackedPeriod.asPeriod(next)));
      builder.append('\n');
    }
    return builder.toString();
  }


  @Override
  public String toString() {
    return "Period column: " + name();
  }

  @Override
  public void appendColumnData(Column column) {
    Preconditions.checkArgument(column.type() == this.type());
    PeriodColumn intColumn = (PeriodColumn) column;
    for (int i = 0; i < intColumn.size(); i++) {
      add(intColumn.getInt(i));
    }
  }
}
