package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.io.TypeUtils;
import com.deathrayresearch.outlier.mapper.ShortMapUtils;
import com.deathrayresearch.outlier.sorting.IntComparisonUtil;
import com.deathrayresearch.outlier.store.ColumnMetadata;
import com.deathrayresearch.outlier.util.StatUtil;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import org.roaringbitmap.RoaringBitmap;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A column that contains signed 4 byte integer values
 */
public class ShortColumn extends AbstractColumn implements ShortMapUtils {

  public static final short MISSING_VALUE = (short) ColumnType.SHORT_INT.getMissingValue();

  private static final int DEFAULT_ARRAY_SIZE = 128;

  private ShortArrayList data;

  public static ShortColumn create(String name) {
    return new ShortColumn(name, DEFAULT_ARRAY_SIZE);
  }

  public static ShortColumn create(ColumnMetadata metadata) {
    return new ShortColumn(metadata);
  }

  public static ShortColumn create(String name, int arraySize) {
    return new ShortColumn(name, arraySize);
  }

  public static ShortColumn create(String name, ShortArrayList ints) {
    ShortColumn column = new ShortColumn(name, ints.size());
    column.data = ints;
    return column;
  }

  public ShortColumn(String name, int initialSize) {
    super(name);
    data = new ShortArrayList(initialSize);
  }

  public ShortColumn(ColumnMetadata metadata) {
    super(metadata);
    data = new ShortArrayList(metadata.getSize());
  }

  public ShortColumn(String name) {
    super(name);
    data = new ShortArrayList(DEFAULT_ARRAY_SIZE);
  }

  public int size() {
    return data.size();
  }

  @Override
  public ColumnType type() {
    return ColumnType.SHORT_INT;
  }

  public long sum() {
    long sum = 0;
    for (int i : data) {
      sum += i;
    }
    return sum;
  }

  public void add(short i) {
    data.add(i);
  }

  public void set(int index, short value) {
    data.set(index, value);
  }

  public RoaringBitmap isLessThan(short f) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (next < f) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isGreaterThan(short f) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (next > f) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isGreaterThanOrEqualTo(short f) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (next >= f) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isLessThanOrEqualTo(short f) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (next <= f) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isEqualTo(short f) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data) {
      if (next == f) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  @Override
  public Table summary() {
    return StatUtil.stats(this).asTable();
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
  public ShortColumn unique() {
    RoaringBitmap roaringBitmap = new RoaringBitmap();
    for (short i : data) {
      roaringBitmap.add(i);
    }
    int[] ints = roaringBitmap.toArray();
    short[] shorts = new short[ints.length];
    for (int i = 0; i < ints.length; i++) {
      shorts[i] = (short) ints[i];
    }
    return ShortColumn.create(name() + " Unique values", ShortArrayList.wrap(shorts));
  }

  @Override
  public String getString(int row) {
    return String.valueOf(data.getShort(row));
  }

  @Override
  public ShortColumn emptyCopy() {
    return new ShortColumn(name(), DEFAULT_ARRAY_SIZE);
  }

  @Override
  public void clear() {
    data.clear();
  }

  @Override
  public void sortAscending() {
    Arrays.parallelSort(data.elements());
  }

  @Override
  public void sortDescending() {
    ShortArrays.parallelQuickSort(data.elements(), reverseIntComparator);
  }

  ShortComparator reverseIntComparator =  new ShortComparator() {

    @Override
    public int compare(Short o2, Short o1) {
      return (o1 < o2 ? -1 : (o1.equals(o2) ? 0 : 1));
    }

    @Override
    public int compare(short o2, short o1) {
      return (o1 < o2 ? -1 : (o1 == o2 ? 0 : 1));
    }
  };


  private ShortColumn copy() {
    ShortColumn copy = emptyCopy();
    for (short i : data) {
      copy.add(i);
    }
    return copy;
  }

  @Override
  public boolean isEmpty() {
    return data.isEmpty();
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
  public static short convert(String stringValue) {
    if (Strings.isNullOrEmpty(stringValue) || TypeUtils.MISSING_INDICATORS.contains(stringValue)) {
      return (short) ColumnType.SHORT_INT.getMissingValue();
    }
    Matcher matcher = COMMA_PATTERN.matcher(stringValue);
    return Short.parseShort(matcher.replaceAll(""));
  }

  private static final Pattern COMMA_PATTERN = Pattern.compile(",");

  public short get(int index) {
    return data.getShort(index);
  }

  @Override
  public IntComparator rowComparator() {
    return comparator;
  }

  final IntComparator comparator = new IntComparator() {

    @Override
    public int compare(Integer i1, Integer i2) {
      return compare((int) i1, (int) i2);
    }

    public int compare(int i1, int i2) {
      int prim1 = get(i1);
      int prim2 = get(i2);
      return IntComparisonUtil.getInstance().compare(prim1, prim2);
    }
  };

  public short max() {
    return StatUtil.max(this);
  }

  public short min() {
    return StatUtil.min(this);
  }

  public short firstElement() {
    if (size() > 0) {
      return get(0);
    }
    return MISSING_VALUE;
  }

  public RoaringBitmap isPositive() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (short next : data) {
      if (next > 0) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isNegative() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (short next : data) {
      if (next < 0) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isNonNegative() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (short next : data) {
      if (next >= 0) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isZero() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (short next : data) {
      if (next == 0) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isEven() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (short next : data) {
      if ((next & 1) == 0) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isOdd() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (short x : data) {
      if ((x & 1) != 0) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public FloatArrayList toFloatArray() {
    FloatArrayList output = new FloatArrayList(data.size());
    for (short aData : data) {
      output.add(aData);
    }
    return output;
  }

  public String print() {
    StringBuilder builder = new StringBuilder();
    builder.append(title());
    for (short i : data){
      builder.append(String.valueOf(i));
      builder.append('\n');
    }
    return builder.toString();
  }

  @Override
  public String toString() {
    return "ShortInt column: " + name();
  }

  @Override
  public void appendColumnData(Column column) {
    Preconditions.checkArgument(column.type() == this.type());
    ShortColumn shortColumn = (ShortColumn) column;
    for (int i = 0; i < shortColumn.size(); i++) {
      add(shortColumn.get(i));
    }
  }

  @Override
  public ShortIterator iterator() {
    return data.iterator();
  }
}
