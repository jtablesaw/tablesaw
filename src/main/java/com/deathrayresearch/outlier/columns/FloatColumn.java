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

  public static final float MISSING_VALUE = (float) ColumnType.FLOAT.getMissingValue();

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

  public float mean() {
    reset();
    return StatUtil.mean(this);
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

  /**
   * Returns the natural log of the values in this column as a new FloatColumn
   */
  public FloatColumn logN() {
    FloatColumn newColumn = FloatColumn.create(name() + "[logN]");

    for (int r = 0; r < size(); r++) {
      float value = data[r];
      newColumn.set(r, (float) Math.log(value));
    }
    return newColumn;
  }

  public FloatColumn log10() {
    FloatColumn newColumn = FloatColumn.create(name() + "[log10]");

    for (int r = 0; r < size(); r++) {
      float value = data[r];
      newColumn.set(r, (float) Math.log10(value));
    }
    return newColumn;
  }

  /**
   * Returns the natural log of the values in this column, after adding 1 to each so that zero
   * values don't return -Infinity
   */
  public FloatColumn log1p() {
    FloatColumn newColumn = FloatColumn.create(name() + "[1og1p]");
    for (int r = 0; r < size(); r++) {
      float value = data[r];
      newColumn.set(r, (float) Math.log1p(value));
    }
    return newColumn;
  }


  public FloatColumn round() {

    FloatColumn newColumn = FloatColumn.create(name() + "[rounded]");

    for (int r = 0; r < size(); r++) {
      float value = data[r];
      newColumn.set(r, Math.round(value));
    }
    return newColumn;
  }

  public FloatColumn abs() {

    FloatColumn newColumn = FloatColumn.create(name() + "[abs]");

    for (int r = 0; r < size(); r++) {
      float value = data[r];
      newColumn.set(r, Math.abs(value));
    }
    return newColumn;
  }

  public FloatColumn square() {

    FloatColumn newColumn = FloatColumn.create(name() + "[sq]");

    for (int r = 0; r < size(); r++) {
      float value = data[r];
      newColumn.set(r, value * value);
    }
    return newColumn;
  }

  public FloatColumn sqrt() {

    FloatColumn newColumn = FloatColumn.create(name() + "[sqrt]");

    for (int r = 0; r < size(); r++) {
      float value = data[r];
      newColumn.set(r, (float) Math.sqrt(value));
    }
    return newColumn;
  }

  public FloatColumn cubeRoot() {

    FloatColumn newColumn = FloatColumn.create(name() + "[cbrt]");

    for (int r = 0; r < size(); r++) {
      float value = data[r];
      newColumn.set(r, (float) Math.cbrt(value));
    }
    return newColumn;
  }

  public FloatColumn cube() {

    FloatColumn newColumn = FloatColumn.create(name() + "[cb]");

    for (int r = 0; r < size(); r++) {
      float value = data[r];
      newColumn.set(r, value * value * value);
    }
    return newColumn;
  }

  public FloatColumn mod(FloatColumn column2) {

    FloatColumn result = FloatColumn.create(name() + " % " + column2.name());

    for (int r = 0; r < size(); r++) {
      result.set(r, data[r] % column2.data[r]);
    }

    return result;
  }

  public FloatColumn difference(FloatColumn column2) {

    FloatColumn result = FloatColumn.create(name() + " - " + column2.name());

    for (int r = 0; r < size(); r++) {
      result.set(r, data[r] - column2.data[r]);
    }

    return result;
  }

  public float[] mode() {
    return StatUtil.mode(data);
  }

  /**
   * For each item in the column, returns the same number with the sign changed.
   * For example:
   * -1.3   returns  1.3,
   *  2.135 returns -2.135
   *  0     returns  0
   */
  public FloatColumn neg() {
    FloatColumn newColumn = FloatColumn.create(name() + "[neg]");

    for (int r = 0; r < size(); r++) {
      float value = data[r];
      newColumn.set(r, value * -1);
    }
    return newColumn;
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

  // TODO(lwhite): Reconsider the implementation of this functionality to allow user to provide a specific max error.
  // TODO(lwhite): continued: Also see section in Effective Java on floating point comparisons.
  RoaringBitmap isCloseTo(float target) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    while (hasNext()) {
      if (Float.compare(next(), target) == 0) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }

  RoaringBitmap isCloseTo(double target) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    while (hasNext()) {
      if (Double.compare(next(), 0.0) == 0) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }

  RoaringBitmap isPositive() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    while (hasNext()) {
      if (next() > 0.0) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }

  RoaringBitmap isNegative() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    while (hasNext()) {
      if (next() < 0.0) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }

  RoaringBitmap isNoNegative() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    while (hasNext()) {
      if (next() >= 0.0) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }
}
