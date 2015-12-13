package com.deathrayresearch.outlier;

import org.roaringbitmap.RoaringBitmap;

/**
 *
 */
public class IntColumn extends AbstractColumn {

  private static int DEFAULT_ARRAY_SIZE = 128;
  private int pointer = 0;
  private int N = 0;

  private int[] data;

  public IntColumn(String name) {
    super(name);
    data = new int[DEFAULT_ARRAY_SIZE];
  }

  public IntColumn(String name, int initialSize) {
    super(name);
    data = new int[initialSize];
  }

  public int size() {
    return N;
  }

  @Override
  public ColumnType type() {
    return ColumnType.INTEGER;
  }

  @Override
  public boolean hasNext() {
    return pointer < N;
  }

  public int next() {
    return data[pointer++];
  }

  public int sum() {
    int sum = 0;
    while (hasNext()) {
      sum += next();
    }
    return sum;
  }

  public void add(int i) {
    if (N >= data.length) {
      resize();
    }
    data[N++] = i;
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

  public RoaringBitmap isLessThan(int f) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    while(hasNext()) {
      if (next() < f) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }

  public RoaringBitmap isGreaterThan(int f) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    while(hasNext()) {
      if (next() > f) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }

  public RoaringBitmap isGreaterThanOrEqualTo(int f) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    while(hasNext()) {
      if (next() >= f) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }

  public RoaringBitmap isLessThanOrEqualTo(int f) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    while(hasNext()) {
      if (next() <= f) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }

  public RoaringBitmap isEqualTo(int f) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    while(hasNext()) {
      if (next() == f) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
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
  public String getString(int row) {
    return String.valueOf(data[row]);
  }

  @Override
  public IntColumn emptyCopy() {
    return new IntColumn(name());
  }

  @Override
  public void clear() {
    data = new int[DEFAULT_ARRAY_SIZE];
  }

  @Override
  public Column sortAscending() {
    return null;
  }

  @Override
  public Column sortDescending() {
    return null;
  }

  public void reset() {
    pointer = 0;
  }
}
