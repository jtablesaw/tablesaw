package com.deathrayresearch.outlier;

import net.mintern.primitive.Primitive;

import java.util.Arrays;

/**
 * A column in a base table that contains float values
 */
public class BooleanColumn extends AbstractColumn {

  private static int DEFAULT_ARRAY_SIZE = 128;

  // For internal iteration. What element are we looking at right now
  private int pointer = 0;

  // The number of elements, which may be less than the size of the array
  private int N = 0;

  private float[] data;

  public BooleanColumn(String name) {
    super(name);
    data = new float[DEFAULT_ARRAY_SIZE];
  }

  public BooleanColumn(String name, int initialSize) {
    super(name);
    data = new float[initialSize];
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

  public float next() {
    return data[pointer++];
  }

  public float sum() {
    float sum = 0.0f;
    while (hasNext()) {
      sum += next();
    }
    return sum;
  }

  public void add(float f) {
    if (N >= data.length) {
      resize();
    }
    data[N++] = f;
  }

  // TODO(lwhite): Redo to reduce the increase for large columns
  private void resize() {
    float[] temp = new float[Math.round(data.length * 2)];
    System.arraycopy(data, 0, temp, 0, N);
    data = temp;
  }

  /**
   * Removes (most) extra space (empty elements) from the data array
   */
  public void compact() {
    float[] temp = new float[N + 100];
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
    data = new float[DEFAULT_ARRAY_SIZE];
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
    Arrays.sort(copy.data);
    return copy;
  }

  @Override
  public Column sortDescending() {
    BooleanColumn copy = this.copy();
    Arrays.sort(copy.data);
    Primitive.sort(copy.data, (d1, d2) -> Float.compare(d2, d1), false);
    return copy;
  }
}
