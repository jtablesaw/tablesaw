package com.deathrayresearch.outlier;

import com.deathrayresearch.outlier.io.TypeUtils;
import com.google.common.base.Strings;
import net.mintern.primitive.Primitive;
import org.roaringbitmap.RoaringBitmap;

import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class IntColumn extends AbstractColumn {

  private static int DEFAULT_ARRAY_SIZE = 128;
  private int pointer = 0;
  private int N = 0;

  private int[] data;

  public static IntColumn create(String name) {
    return new IntColumn(name);
  }

  private IntColumn(String name) {
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
    while (hasNext()) {
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
    while (hasNext()) {
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
    while (hasNext()) {
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
    while (hasNext()) {
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
    while (hasNext()) {
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
    return new Table(name());
  }

  @Override
  public int countUnique() {
    RoaringBitmap roaringBitmap = new RoaringBitmap();
    for (int i : data) {
      roaringBitmap.add(i);
    }
    return roaringBitmap.getCardinality();
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
    IntColumn copy = this.copy();
    Arrays.sort(copy.data);
    return copy;
  }

  @Override
  public Column sortDescending() {
    IntColumn copy = this.copy();
    Primitive.sort(copy.data, (d1, d2) -> Float.compare(d2, d1), false);
    return copy;
  }

  private IntColumn copy() {
    IntColumn copy = emptyCopy();
    copy.data = this.data;
    copy.N = this.N;
    return copy;
  }

  public void reset() {
    pointer = 0;
  }

  @Override
  public boolean isEmpty() {
    return N == 0;
  }

  @Override
  public void addCell(String object) {
    try {
      add(convert(object));
    } catch (NumberFormatException nfe) {
      throw new NumberFormatException(name() + ": " + nfe.getMessage());
    } catch (NullPointerException e) {
      throw new RuntimeException(name() + ": "
          + String.valueOf(object) + ": "
          + e.getMessage());
    }
  }

  /**
   * Returns a float that is parsed from the given String
   * <p>
   * We remove any commas before parsing
   */
  public static int convert(String stringValue) {
    if (Strings.isNullOrEmpty(stringValue) || TypeUtils.MISSING_INDICATORS.contains(stringValue)) {
      return (int) ColumnType.INTEGER.getMissingValue();
    }
    Matcher matcher = COMMA_PATTERN.matcher(stringValue);
    return Integer.parseInt(matcher.replaceAll(""));
  }

  private static final Pattern COMMA_PATTERN = Pattern.compile(",");

  public int get(int index) {
    return data[index];
  }

  @Override
  public Comparator<Integer> rowComparator() {
    return comparator;
  }

  Comparator<Integer> comparator = new Comparator<Integer>() {

    @Override
    public int compare(Integer r1, Integer r2) {
      int f1 = data[r1];
      int f2 = data[r2];
      return Integer.compare(f1, f2);
    }
  };

  public int min() {
    int min = Integer.MAX_VALUE;
    while (this.hasNext()) {
      int next = next();
      if (next < min) {
        min = next;
      }
    }
    return min;
  }
}
