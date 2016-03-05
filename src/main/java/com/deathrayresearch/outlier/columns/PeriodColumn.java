package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.View;
import com.deathrayresearch.outlier.io.TypeUtils;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.mintern.primitive.Primitive;
import org.roaringbitmap.RoaringBitmap;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

/**
 * A column in a table that contains period values. Periods are date oriented time units.
 * They differ from the java period class implementation in that they are more compact, and thus are more limited
 * in the range of durations they can express
 */
public class PeriodColumn extends AbstractColumn {

  public static final int MISSING_VALUE = (int) ColumnType.LOCAL_DATE.getMissingValue() ;
  private static int DEFAULT_ARRAY_SIZE = 128;

  // For internal iteration. What element are we looking at right now
  private int pointer = 0;

  // The number of elements, which may be less than the size of the array
  private int N = 0;

  private int[] data;

  private PeriodColumn(String name) {
    super(name);
    data = new int[DEFAULT_ARRAY_SIZE];
  }

  private PeriodColumn(String name, int initialSize) {
    super(name);
    data = new int[initialSize];
  }

  public int size() {
    return N;
  }

  @Override
  public ColumnType type() {
    return ColumnType.PERIOD;
  }

  @Override
  public boolean hasNext() {
    return pointer < N;
  }

  public int next() {
    return data[pointer++];
  }

  public void add(int f) {
    if (N >= data.length) {
      resize();
    }
    data[N++] = f;
  }

  public void add(Period f) {
    if (N >= data.length) {
      resize();
    }
    data[N++] = PackedPeriod.pack(f);
  }

  // TODO(lwhite): Redo to reduce the increase for large columns
  private void resize() {
    int[] temp = new int[Math.round(data.length * 2)];
    System.arraycopy(data, 0, temp, 0, N);
    data = temp;
  }

  /**
   * Removes (most) extra space (empty elements) from the data array
   */
  public void compact() {
    int[] temp = new int[N + 100];
    System.arraycopy(data, 0, temp, 0, N);
    data = temp;
  }


  @Override
  public String getString(int row) {
    return PackedLocalDate.toDateString(data[row]);
  }

  @Override
  public PeriodColumn emptyCopy() {
    return new PeriodColumn(name());
  }

  @Override
  public void clear() {
    data = new int[DEFAULT_ARRAY_SIZE];
  }

  public void reset() {
    pointer = 0;
  }

  private PeriodColumn copy() {
    PeriodColumn copy = emptyCopy();
    copy.data = this.data;
    copy.N = this.N;
    return copy;
  }

  @Override
  public Column sortAscending() {
    PeriodColumn copy = this.copy();
    Arrays.sort(copy.data);
    return copy;
  }

  @Override
  public Column sortDescending() {
    PeriodColumn copy = this.copy();
    Primitive.sort(copy.data, (d1, d2) -> Integer.compare(d2, d1), false);
    return copy;
  }

  @Override
  public int countUnique() {
    IntSet ints = new IntOpenHashSet(data.length);
    for (int i : data) {
      ints.add(i);
    }
    return ints.size();
  }

  public Period firstElement() {
    if (isEmpty()) {
      return null;
    }
    return PackedPeriod.asPeriod(data[0]);
  }

  public LocalDate max() {
    int max;
    int missing = Integer.MIN_VALUE;
    if (!isEmpty()) {
      max = data[0];
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
      min = data[0];
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
    return PackedLocalDate.asLocalDate(data[index]);
  }

  public static PeriodColumn create(String name) {
    return new PeriodColumn(name);
  }

  @Override
  public boolean isEmpty() {
    return N == 0;
  }

  @Override
  public IntComparator rowComparator() {
    return comparator;
  }

  IntComparator comparator = new IntComparator() {

    @Override
    public int compare(Integer r1, Integer r2) {
      int f1 = data[r1];
      int f2 = data[r2];
      return Integer.compare(f1, f2);
    }

    @Override
    public int compare(int r1, int r2) {
      int f1 = data[r1];
      int f2 = data[r2];
      return Integer.compare(f1, f2);
    }
  };

  public static PeriodColumn create(String fileName, IntArrayList dates) {
    PeriodColumn column = new PeriodColumn(fileName, dates.size());
    column.data = dates.elements();
    return column;
  }

  public int convert(String value) {
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
    return data[index];
  }

  public RoaringBitmap isEqualTo(Period value) {
    RoaringBitmap results = new RoaringBitmap();
    int packedPeriod = PackedPeriod.pack(value);
    int i = 0;
    while (hasNext()) {
      if (packedPeriod == next()) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }

  /**
   * Returns a table of dates and the number of observations of those dates
   */
  public View summary() {

    Object2IntMap<Period> counts = new Object2IntOpenHashMap<>();

    for (int i = 0; i < N; i++) {
      Period value;
      int next = data[i];
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
    while (hasNext()) {
      int next = next();
      if (PackedPeriod.isLongerThan(next, value)) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }

  public RoaringBitmap isShorterThan(int value) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    while (hasNext()) {
      int next = next();
      if (PackedPeriod.isShorterThan(next, value)) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }
}
