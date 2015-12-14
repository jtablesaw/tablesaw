package com.deathrayresearch.outlier;

import java.util.Arrays;

/**
 * A column in a base table that contains float values
 */
public class TextColumn extends AbstractColumn {

  private static int DEFAULT_ARRAY_SIZE = 128;

  // For internal iteration. What element are we looking at right now
  private int pointer = 0;

  // The number of elements, which may be less than the size of the array
  private int N = 0;

  private String[] data;

  public static TextColumn create(String name) {
    return new TextColumn(name);
  }

  private TextColumn(String name) {
    super(name);
    data = new String[DEFAULT_ARRAY_SIZE];
  }

  public TextColumn(String name, int initialSize) {
    super(name);
    data = new String[initialSize];
  }

  public int size() {
    return N;
  }

  @Override
  public ColumnType type() {
    return ColumnType.FLOAT;
  }

  @Override
  public boolean hasNext() {
    return pointer < N;
  }

  public String next() {
    return data[pointer++];
  }

  public void add(String f) {
    if (N >= data.length) {
      resize();
    }
    data[N++] = f;
  }

  // TODO(lwhite): Redo to reduce the increase for large columns
  private void resize() {
    String[] temp = new String[Math.round(data.length * 2)];
    System.arraycopy(data, 0, temp, 0, N);
    data = temp;
  }

  /**
   * Removes (most) extra space (empty elements) from the data array
   */
  public void compact() {
    String[] temp = new String[N + 100];
    System.arraycopy(data, 0, temp, 0, N);
    data = temp;
  }


  @Override
  public String getString(int row) {
    return String.valueOf(data[row]);
  }

  @Override
  public TextColumn emptyCopy() {
    return new TextColumn(name());
  }

  @Override
  public void clear() {
    data = new String[DEFAULT_ARRAY_SIZE];
  }

  public void reset() {
    pointer = 0;
  }

  private TextColumn copy() {
    TextColumn copy = emptyCopy();
    copy.data = this.data;
    copy.N = this.N;
    return copy;
  }

  @Override
  public Column sortAscending() {
    TextColumn copy = this.copy();
    Arrays.sort(copy.data);
    return copy;
  }

  @Override
  public Column sortDescending() {
    TextColumn copy = this.copy();
    // TODO(lwhite): BUG This sort is reversed (Q: Can we use this sort and reverse the iterator?)
    Arrays.sort(copy.data);
    return copy;
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
  public boolean isEmpty() {
    return N == 0;
  }
}
