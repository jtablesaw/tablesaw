package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Relation;
import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.io.TypeUtils;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.mintern.primitive.Primitive;
import org.roaringbitmap.RoaringBitmap;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Map;

/**
 * A column in a base table that contains float values
 */
public class LocalTimeColumn extends AbstractColumn {

  public static final int MISSING_VALUE = (int) ColumnType.LOCAL_TIME.getMissingValue() ;

  private static int DEFAULT_ARRAY_SIZE = 128;

  // For internal iteration. What element are we looking at right now
  private int pointer = 0;

  // The number of elements, which may be less than the size of the array
  private int N = 0;

  private int[] data;

  public static LocalTimeColumn create(String name) {
    return new LocalTimeColumn(name);
  }

  public static LocalTimeColumn create(String fileName, IntArrayList times) {
    LocalTimeColumn column = new LocalTimeColumn(fileName, times.size());
    column.data = times.elements();
    return column;
  }

  private LocalTimeColumn(String name) {
    super(name);
    data = new int[DEFAULT_ARRAY_SIZE];
  }

  public LocalTimeColumn(String name, int initialSize) {
    super(name);
    data = new int[initialSize];
  }

  public int size() {
    return N;
  }

  @Override
  public ColumnType type() {
    return ColumnType.LOCAL_TIME;
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
    return PackedLocalTime.toShortTimeString(data[row]);
  }

  @Override
  public LocalTimeColumn emptyCopy() {
    return new LocalTimeColumn(name());
  }

  @Override
  public void clear() {
    data = new int[DEFAULT_ARRAY_SIZE];
  }

  public void reset() {
    pointer = 0;
  }

  private LocalTimeColumn copy() {
    LocalTimeColumn copy = emptyCopy();
    copy.data = this.data;
    copy.N = this.N;
    return copy;
  }

  @Override
  public Column sortAscending() {
    LocalTimeColumn copy = this.copy();
    Arrays.sort(copy.data);
    return copy;
  }

  @Override
  public Column sortDescending() {
    LocalTimeColumn copy = this.copy();
    Primitive.sort(copy.data, (d1, d2) -> Integer.compare(d2, d1), false);
    return copy;
  }

  @Override
  public Relation summary() {

    Int2IntOpenHashMap counts = new Int2IntOpenHashMap();

    for (int i = 0; i < N; i++) {
      int value;
      int next = data[i];
      if (next == Integer.MIN_VALUE) {
        value = LocalTimeColumn.MISSING_VALUE;
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
    table.addColumn(LocalTimeColumn.create("Time"));
    table.addColumn(IntColumn.create("Count"));

    for (Map.Entry<Integer, Integer> entry : counts.int2IntEntrySet()) {
      table.localTimeColumn(0).add(entry.getKey());
      table.intColumn(1).add(entry.getValue());
    }
    table = table.sortDescendingOn("Count");

    return table.head(5);
  }

  @Override
  public int countUnique() {
    IntSet ints = new IntOpenHashSet();
    for (int i = 0; i < N; i++) {
      ints.add(data[i]);
    }
    return ints.size();
  }

  @Override
  public boolean isEmpty() {
    return N == 0;
  }

  public int convert(String value) {
    if (Strings.isNullOrEmpty(value)
        || TypeUtils.MISSING_INDICATORS.contains(value)
        || value.equals("-1")) {
      return (int) ColumnType.LOCAL_TIME.getMissingValue();
    }
    value = Strings.padStart(value, 4, '0');
    return PackedLocalTime.pack(LocalTime.parse(value, TypeUtils.timeFormatter));
  }

  @Override
  public void addCell(String object) {
    try {
      add(convert(object));
    } catch (NullPointerException e) {
      throw new RuntimeException(name() + ": "
          + String.valueOf(object) + ": "
          + e.getMessage());
    }
  }

  public int getInt(int index) {
    return data[index];
  }

  public LocalTime get(int index) {
    return PackedLocalTime.asLocalTime(data[index]);
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
      System.out.println("Comparing with object in time");
      return Integer.compare(f1, f2);
    }

    @Override
    public int compare(int r1, int r2) {
      int f1 = data[r1];
      int f2 = data[r2];
      return Integer.compare(f1, f2);
    }
  };

  public RoaringBitmap isEqualTo(LocalTime value) {
    RoaringBitmap results = new RoaringBitmap();
    int packedLocalTime = PackedLocalTime.pack(value);
    int i = 0;
    while (hasNext()) {
      if (packedLocalTime == next()) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }
}