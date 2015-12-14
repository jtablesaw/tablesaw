package com.deathrayresearch.outlier;

import com.deathrayresearch.outlier.io.TypeUtils;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;

/**
 * A column in a base table that contains float values
 */
public class BooleanColumn extends AbstractColumn {

  private static int DEFAULT_ARRAY_SIZE = 128;

  // For internal iteration. What element are we looking at right now
  private int pointer = 0;

  // The number of elements, which may be less than the size of the array
  private int N = 0;

  private boolean[] data;

  public static BooleanColumn create(String name) {
    return new BooleanColumn(name);
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

  // TODO(lwhite): Implement column summary()
  @Override
  public Table summary() {
    return null;
  }

  // TODO(lwhite): Implement countUnique()
  @Override
  public int countUnique() {
    return 0;
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
    }
    else {
      throw new IllegalArgumentException("Attempting to convert non-boolean value " +
          stringValue + " to Boolean");
    }
  };

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
    return booleanColumn;
  }
}
