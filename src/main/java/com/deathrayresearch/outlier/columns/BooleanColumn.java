package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.io.TypeUtils;
import com.deathrayresearch.outlier.mapper.BooleanMapUtils;
import com.deathrayresearch.outlier.store.ColumnMetadata;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import org.roaringbitmap.RoaringBitmap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A column in a base table that contains float values
 */
public class BooleanColumn extends AbstractColumn implements BooleanMapUtils {

  private static int DEFAULT_ARRAY_SIZE = 128;

  // For internal iteration. What element are we looking at right now
  private int pointer = 0;

  // The number of elements, which may be less than the size of the array
  private int N = 0;

  private boolean[] data;

  public static BooleanColumn create(String name) {
    return new BooleanColumn(name);
  }

  public BooleanColumn(ColumnMetadata metadata) {
    super(metadata);
    data = new boolean[DEFAULT_ARRAY_SIZE];
  }

  private BooleanColumn(String name) {
    super(name);
    data = new boolean[DEFAULT_ARRAY_SIZE];
  }

  private BooleanColumn(String name, int initialSize) {
    super(name);
    data = new boolean[initialSize];
  }

  public int size() {
    return N;
  }

  @Override
  public Table summary() {

    Map<Boolean, Integer> counts = new HashMap<>(3);
    counts.put(true, 0);
    counts.put(false, 0);

    while (hasNext()) {
      boolean next = next();
      counts.put(next, counts.get(next) + 1);
    }

    Table table = new Table(name());

    BooleanColumn booleanColumn = BooleanColumn.create("Value");
    IntColumn countColumn = IntColumn.create("Count");
    table.addColumn(booleanColumn);
    table.addColumn(countColumn);

    for (Map.Entry<Boolean, Integer> entry : counts.entrySet()) {
      booleanColumn.add(entry.getKey());
      countColumn.add(entry.getValue());
    }
    reset();
    return table;
  }

  @Override
  public int countUnique() {
    Set<Boolean> count = new HashSet<>(3);
    while (hasNext()) {
      count.add(next());
    }
    return count.size();
  }

  @Override
  public ColumnType type() {
    return ColumnType.BOOLEAN;
  }

  @Override
  public boolean hasNext() {
    return pointer < N;
  }

  public boolean next() {
    return data[pointer++];
  }

  public void add(boolean f) {
    if (N >= data.length) {
      resize();
    }
    data[N++] = f;
  }

  // TODO(lwhite): Redo to reduce the increase for large columns
  private void resize() {
    boolean[] temp = new boolean[Math.round(data.length * 2)];
    System.arraycopy(data, 0, temp, 0, N);
    data = temp;
  }

  /**
   * Removes (most) extra space (empty elements) from the data array
   */
  public void compact() {
    boolean[] temp = new boolean[N + 100];
    System.arraycopy(data, 0, temp, 0, N);
    data = temp;
  }


  @Override
  public String getString(int row) {
    return String.valueOf(data[row]);
  }

  @Override
  public BooleanColumn emptyCopy() {
    return new BooleanColumn(name());
  }

  @Override
  public void clear() {
    data = new boolean[DEFAULT_ARRAY_SIZE];
  }

  public void reset() {
    pointer = 0;
  }

  private BooleanColumn copy() {
    BooleanColumn copy = emptyCopy();
    copy.data = this.data;
    copy.N = this.N;
    return copy;
  }

  @Override
  public Column sortAscending() {
    BooleanColumn copy = this.copy();
    //Arrays.sort(copy.data);
    return copy;
  }

  @Override
  public Column sortDescending() {
    BooleanColumn copy = this.copy();
//    Primitive.sort(copy.data, (d1, d2) -> Float.compare(d2, d1), false);
    return copy;
  }

  public static boolean convert(String stringValue) {
    if (Strings.isNullOrEmpty(stringValue) || TypeUtils.MISSING_INDICATORS.contains(stringValue)) {
      return (boolean) ColumnType.BOOLEAN.getMissingValue();
    } else if (TypeUtils.TRUE_STRINGS.contains(stringValue)) {
      return true;
    } else if (TypeUtils.FALSE_STRINGS.contains(stringValue)) {
      return false;
    } else {
      throw new IllegalArgumentException("Attempting to convert non-boolean value " +
          stringValue + " to Boolean");
    }
  }

  public void addCell(String object) {
    try {
      add(convert(object));
    } catch (NullPointerException e) {
      throw new RuntimeException(name() + ": "
          + String.valueOf(object) + ": "
          + e.getMessage());
    }
  }

  public boolean get(int i) {
    return data[i];
  }

  @Override
  public boolean isEmpty() {
    return N == 0;
  }

  public static BooleanColumn create(String fileName, BooleanArrayList bools) {
    BooleanColumn booleanColumn = new BooleanColumn(fileName, bools.size());
    booleanColumn.data = bools.elements();
    booleanColumn.N = bools.size();
    return booleanColumn;
  }

  public int countTrue() {
    int count = 0;
    for (boolean b : data) {
      if (b) {
        count++;
      }
    }
    return count;
  }

  public int countFalse() {
    int count = 0;
    for (boolean b : data) {
      if (!b) {
        count++;
      }
    }
    return count;
  }

  public RoaringBitmap isFalse() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    while (hasNext()) {
      if (!next()) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }

  public RoaringBitmap isTrue() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    while (hasNext()) {
      if (next()) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }

  public void set(int i, boolean b) {
    if (i > data.length) {
      resize();
    }
    data[i] = b;
  }

  @Override
  public IntComparator rowComparator() {
    return comparator;
  }

  IntComparator comparator = new IntComparator() {

    @Override
    public int compare(Integer r1, Integer r2) {
      boolean f1 = data[r1];
      boolean f2 = data[r2];
      return Boolean.compare(f1, f2);
    }

    @Override
    public int compare(int r1, int r2) {
      boolean f1 = data[r1];
      boolean f2 = data[r2];
      return Boolean.compare(f1, f2);
    }
  };

  @Override
  public String toString() {
    return "Boolean column: " + name();
  }
}
