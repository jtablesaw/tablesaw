package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.io.TypeUtils;
import com.deathrayresearch.outlier.util.StatUtil;
import com.google.common.base.Strings;
import net.mintern.primitive.Primitive;
import org.roaringbitmap.RoaringBitmap;

import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A column in a base table that contains float values
 */
public class FloatColumn extends AbstractColumn {

  private static int DEFAULT_ARRAY_SIZE = 128;

  // For internal iteration. What element are we looking at right now
  private int pointer = 0;

  // The number of elements, which may be less than the size of the array
  private int N = 0;

  private float[] data;

  public FloatColumn(String name) {
    super(name);
    data = new float[DEFAULT_ARRAY_SIZE];
  }

  public FloatColumn(String name, int initialSize) {
    super(name);
    data = new float[initialSize];
  }

  public int size() {
    return N;
  }

  // TODO(lwhite): Implement column summary()
  @Override
  public Table summary() {

    return null;
  }

  public String describe() {
    return StatUtil.stats(this);
  }

  // TODO(lwhite): Implement countUnique()
  @Override
  public int countUnique() {
    return 0;
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
    reset();
    return StatUtil.sum(this);
  }

  public float firstElement() {
    if (size() > 0) {
      return data[0];
    }
    return Float.MIN_VALUE;
  }

  public float max() {
    reset();
    float f = StatUtil.max(this);
    reset();
    return f;
  }

  public float min() {
    reset();
    float f = StatUtil.min(this);
    reset();
    return f;
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

  public RoaringBitmap isLessThan(float f) {
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

  public RoaringBitmap isGreaterThan(float f) {
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

  public RoaringBitmap isGreaterThanOrEqualTo(float f) {
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

  public RoaringBitmap isLessThanOrEqualTo(float f) {
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

  public RoaringBitmap isEqualTo(float f) {
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

  @Override
  public String getString(int row) {
    return String.valueOf(data[row]);
  }

  @Override
  public FloatColumn emptyCopy() {
    return new FloatColumn(name());
  }

  @Override
  public void clear() {
    data = new float[DEFAULT_ARRAY_SIZE];
  }

  public void reset() {
    pointer = 0;
  }

  private FloatColumn copy() {
    FloatColumn copy = emptyCopy();
    copy.data = this.data;
    copy.N = this.N;
    return copy;
  }

  @Override
  public Column sortAscending() {
    FloatColumn copy = this.copy();
    Arrays.sort(copy.data);
    return copy;
  }

  @Override
  public Column sortDescending() {
    FloatColumn copy = this.copy();
    Primitive.sort(copy.data, (d1, d2) -> Float.compare(d2, d1), false);
    return copy;
  }

  @Override
  public boolean isEmpty() {
    return N == 0;
  }

  public static FloatColumn create(String name) {
    return new FloatColumn(name);
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
  public static float convert(String stringValue) {
    if (Strings.isNullOrEmpty(stringValue) || TypeUtils.MISSING_INDICATORS.contains(stringValue)) {
      return Float.NaN;
    }
    Matcher matcher = COMMA_PATTERN.matcher(stringValue);
    return Float.parseFloat(matcher.replaceAll(""));
  }

  private static final Pattern COMMA_PATTERN = Pattern.compile(",");

  @Override
  public Comparator<Integer> rowComparator() {
    return comparator;
  }

  private final Comparator<Integer> comparator = new Comparator<Integer>() {

    @Override
    public int compare(Integer r1, Integer r2) {
      float f1 = data[r1];
      float f2 = data[r2];
      return Float.compare(f1, f2);
    }
  };

  public float get(int index) {
    return data[index];
  }

  public void set(int r, float value) {
    if (r >= data.length) {
      resize();
    }
    data[r] = value;
  }
}
