package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.aggregator.NumReduceUtils;
import com.deathrayresearch.outlier.io.TypeUtils;
import com.deathrayresearch.outlier.store.ColumnMetadata;
import com.deathrayresearch.outlier.util.StatUtil;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatOpenHashSet;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.ints.IntComparator;
import org.roaringbitmap.RoaringBitmap;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A column in a base table that contains float values
 */
public class FloatColumn extends AbstractColumn implements NumReduceUtils {

  public static final float MISSING_VALUE = (float) ColumnType.FLOAT.getMissingValue();

  private static int DEFAULT_ARRAY_SIZE = 128;

  private FloatArrayList data;

  public FloatColumn(String name) {
    super(name);
    data = new FloatArrayList(DEFAULT_ARRAY_SIZE);
  }

  public FloatColumn(String name, int initialSize) {
    super(name);
    data = new FloatArrayList(initialSize);
  }

  public FloatColumn(ColumnMetadata metadata) {
    super(metadata);
    data = new FloatArrayList(metadata.getSize());
  }

  public FloatArrayList data() {
    return data;
  }

  public int size() {
    return data.size();
  }

  @Override
  public Table summary() {
    return StatUtil.stats(this).asTable();
  }

  public String describe() {
    return StatUtil.stats(this).printString();
  }

  @Override
  public int countUnique() {
    FloatSet floats = new FloatOpenHashSet();
    for (int i = 0; i < size(); i++) {
      floats.add(data.getFloat(i));
    }
    return floats.size();
  }

  @Override
  public FloatColumn unique() {
    FloatSet floats = new FloatOpenHashSet();
    for (int i = 0; i < size(); i++) {
      floats.add(data.getFloat(i));
    }
    FloatColumn column = new FloatColumn(name() + " Unique values", floats.size());
    for (float f : floats) {
      column.add(f);
    }
    return column;
  }

  @Override
  public ColumnType type() {
    return ColumnType.FLOAT;
  }

  public float sum() {
    return StatUtil.sum(this);
  }

  public float mean() {
    return StatUtil.mean(this);
  }

  public float firstElement() {
    if (size() > 0) {
      return data.getFloat(0);
    }
    return MISSING_VALUE;
  }

  public float max() {
    return StatUtil.max(this);
  }

  public float min() {
    return StatUtil.min(this);
  }

  public void add(float f) {
    data.add(f);
  }

  public RoaringBitmap isLessThan(float f) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (float f1 : data) {
      if (f1 < f) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isGreaterThan(float f) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (float f1 : data) {
      if (f1 > f) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isGreaterThanOrEqualTo(float f) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (float f1 : data) {
      if (f1 >= f) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isLessThanOrEqualTo(float f) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (float floats : data) {
      if (floats <= f) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isEqualTo(float f) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (float floats : data) {
      if (floats == f) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  @Override
  public String getString(int row) {
    return String.valueOf(data.getFloat(row));
  }

  @Override
  public FloatColumn emptyCopy() {
    return new FloatColumn(name());
  }

  @Override
  public void clear() {
    data = new FloatArrayList(DEFAULT_ARRAY_SIZE);
  }

  private FloatColumn copy() {
    FloatColumn copy = emptyCopy();
    for (float f : data) {
      copy.add(f);
    }
    return copy;
  }

  @Override
  public FloatColumn sortAscending() {
    FloatColumn copy = copy();
    Arrays.parallelSort(copy.data.elements());
    return copy;
  }

  @Override
  public Column sortDescending() {
    FloatColumn copy = copy();
    FloatArrays.parallelQuickSort(copy.data.elements(), reverseFloatComparator);
    return copy;
  }

  @Override
  public boolean isEmpty() {
    return data.isEmpty();
  }

  public static FloatColumn create(String name) {
    return new FloatColumn(name);
  }

  public static FloatColumn create(String fileName, FloatArrayList floats) {
    FloatColumn column = new FloatColumn(fileName, floats.size());
    column.data = floats;
    return column;
  }

  FloatComparator reverseFloatComparator =  new FloatComparator() {

    @Override
    public int compare(Float o1, Float o2) {
      return (o1<o2 ? -1 : (o1==o2 ? 0 : 1));
    }

    @Override
    public int compare(float o2, float o1) {
      return (o1 < o2 ? -1 : (o1 == o2 ? 0 : 1));
    }
  };

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
      float value = data.getFloat(r);
      newColumn.set(r, (float) Math.log(value));
    }
    return newColumn;
  }

  public FloatColumn log10() {
    FloatColumn newColumn = FloatColumn.create(name() + "[log10]");

    for (int r = 0; r < size(); r++) {
      float value = data.getFloat(r);
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
      float value = data.getFloat(r);
      newColumn.set(r, (float) Math.log1p(value));
    }
    return newColumn;
  }


  public FloatColumn round() {

    FloatColumn newColumn = FloatColumn.create(name() + "[rounded]");

    for (int r = 0; r < size(); r++) {
      float value = data.getFloat(r);
      newColumn.set(r, Math.round(value));
    }
    return newColumn;
  }

  public FloatColumn abs() {

    FloatColumn newColumn = FloatColumn.create(name() + "[abs]");

    for (int r = 0; r < size(); r++) {
      float value = data.getFloat(r);
      newColumn.set(r, Math.abs(value));
    }
    return newColumn;
  }

  public FloatColumn square() {

    FloatColumn newColumn = FloatColumn.create(name() + "[sq]");

    for (int r = 0; r < size(); r++) {
      float value = data.getFloat(r);
      newColumn.set(r, value * value);
    }
    return newColumn;
  }

  public FloatColumn sqrt() {

    FloatColumn newColumn = FloatColumn.create(name() + "[sqrt]");

    for (int r = 0; r < size(); r++) {
      float value = data.getFloat(r);
      newColumn.set(r, (float) Math.sqrt(value));
    }
    return newColumn;
  }

  public FloatColumn cubeRoot() {

    FloatColumn newColumn = FloatColumn.create(name() + "[cbrt]");

    for (int r = 0; r < size(); r++) {
      float value = data.getFloat(r);

      newColumn.set(r, (float) Math.cbrt(value));
    }
    return newColumn;
  }

  public FloatColumn cube() {

    FloatColumn newColumn = FloatColumn.create(name() + "[cb]");

    for (int r = 0; r < size(); r++) {
      float value = data.getFloat(r);
      newColumn.set(r, value * value * value);
    }
    return newColumn;
  }

  public FloatColumn mod(FloatColumn column2) {

    FloatColumn result = FloatColumn.create(name() + " % " + column2.name());

    for (int r = 0; r < size(); r++) {
      result.set(r, get(r) % column2.get(r));
    }

    return result;
  }

  public FloatColumn difference(FloatColumn column2) {

    FloatColumn result = FloatColumn.create(name() + " - " + column2.name());

    for (int r = 0; r < size(); r++) {
      result.set(r, get(r) - column2.get(r));
    }

    return result;
  }

  public float[] mode() {
    return StatUtil.mode(data.elements());
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
      float value = data.getFloat(r);
      newColumn.set(r, value * -1);
    }
    return newColumn;
  }

  private static final Pattern COMMA_PATTERN = Pattern.compile(",");

  @Override
  public IntComparator rowComparator() {
    return comparator;
  }

  private final IntComparator comparator = new IntComparator() {

    @Override
    public int compare(Integer r1, Integer r2) {
      float f1 = data.getFloat(r1);
      float f2 = data.getFloat(r2);
      return Float.compare(f1, f2);
    }

    public int compare(int r1, int r2) {
      float f1 = data.getFloat(r1);
      float f2 = data.getFloat(r2);
      return Float.compare(f1, f2);
    }
  };

  public float get(int index) {
    return data.getFloat(index);
  }

  public void set(int r, float value) {
    data.set(r, value);
  }

  // TODO(lwhite): Reconsider the implementation of this functionality to allow user to provide a specific max error.
  // TODO(lwhite): continued: Also see section in Effective Java on floating point comparisons.
  RoaringBitmap isCloseTo(float target) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (float f : data) {
      if (Float.compare(f, target) == 0) {
        results.add(i);
      }
      i++;
    }
    
    return results;
  }

  RoaringBitmap isCloseTo(double target) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (float f : data) {
      if (Double.compare(f, 0.0) == 0) {
        results.add(i);
      }
      i++;
    }
    
    return results;
  }

  RoaringBitmap isPositive() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (float f : data) {
      if (f > 0.0) {
        results.add(i);
      }
      i++;
    }
    
    return results;
  }

  RoaringBitmap isNegative() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (float f : data) {
      if (f < 0.0) {
        results.add(i);
      }
      i++;
    }
    
    return results;
  }

  RoaringBitmap isNonNegative() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (float f : data) {
      if (f >= 0.0) {
        results.add(i);
      }
      i++;
    }
    
    return results;
  }

  public double[] toDoubleArray() {
    double[] output = new double[data.size()];
    for (int i = 0; i < data.size(); i++)
    {
      output[i] = data.getFloat(i);
    }
    return output;
  }

  public String print() {
    StringBuilder builder = new StringBuilder();
    for (Float aData : data) {
      builder.append(String.valueOf(aData));
      builder.append('\n');
    }
    return builder.toString();
  }

  @Override
  public String toString() {
    return "Float column: " + name();
  }

  @Override
  public void appendColumnData(Column column) {
    Preconditions.checkArgument(column.type() == this.type());
    FloatColumn floatColumn = (FloatColumn) column;
    for (int i = 0; i < floatColumn.size(); i++) {
      add(floatColumn.get(i));
    }
  }
}
