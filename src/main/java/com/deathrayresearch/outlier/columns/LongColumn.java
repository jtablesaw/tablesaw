package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.io.TypeUtils;
import com.deathrayresearch.outlier.mapper.LongMapUtils;
import com.deathrayresearch.outlier.sorting.LongComparisonUtil;
import com.deathrayresearch.outlier.store.ColumnMetadata;
import com.deathrayresearch.outlier.util.StatUtil;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.longs.*;
import org.roaringbitmap.RoaringBitmap;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A column that contains signed 4 byte integer values
 */
public class LongColumn extends AbstractColumn implements LongMapUtils {

  public static final long MISSING_VALUE = (long) ColumnType.LONG_INT.getMissingValue();

  private static final int DEFAULT_ARRAY_SIZE = 128;

  private LongArrayList data;

  public static LongColumn create(String name) {
    return new LongColumn(name, DEFAULT_ARRAY_SIZE);
  }

  public static LongColumn create(ColumnMetadata metadata) {
    return new LongColumn(metadata);
  }

  public static LongColumn create(String name, int arraySize) {
    return new LongColumn(name, arraySize);
  }

  public static LongColumn create(String name, LongArrayList ints) {
    LongColumn column = new LongColumn(name, ints.size());
    column.data = ints;
    return column;
  }

  public LongColumn(String name, int initialSize) {
    super(name);
    data = new LongArrayList(initialSize);
  }

  public LongColumn(ColumnMetadata metadata) {
    super(metadata);
    data = new LongArrayList(metadata.getSize());
  }

  public LongColumn(String name) {
    super(name);
    data = new LongArrayList(DEFAULT_ARRAY_SIZE);
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
    for (long i : data) {
      sum += i;
    }
    return sum;
  }

  public void add(long i) {
    data.add(i);
  }

  public void set(int index, short value) {
    data.set(index, value);
  }

  public RoaringBitmap isLessThan(long f) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (long next : data) {
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
    for (long next : data) {
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
    for (long next : data) {
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
    for (long next : data) {
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
    for (long next : data) {
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
    LongSet longSet = new LongArraySet();
    for (long i : data) {
      longSet.add(i);
    }
    return longSet.size();
  }

  @Override
  public LongColumn unique() {
    LongSet longSet = new LongArraySet();
    for (long i : data) {
      longSet.add(i);
    }
    return LongColumn.create(name() + " Unique values", new LongArrayList(longSet));
  }

  @Override
  public String getString(int row) {
    return String.valueOf(data.getLong(row));
  }

  @Override
  public LongColumn emptyCopy() {
    return new LongColumn(name(), DEFAULT_ARRAY_SIZE);
  }

  @Override
  public void clear() {
    data.clear();
  }

  @Override
  public Column sortAscending() {
    LongColumn copy = copy();
    Arrays.parallelSort(copy.data.elements());
    return copy;
  }

  @Override
  public Column sortDescending() {
    LongColumn copy = copy();
    LongArrays.parallelQuickSort(copy.data.elements(), reverseIntComparator);
    return copy;
  }

  LongComparator reverseIntComparator =  new LongComparator() {

    @Override
    public int compare(Long o2, Long o1) {
      return (o1 < o2 ? -1 : (o1.equals(o2) ? 0 : 1));
    }

    @Override
    public int compare(long o2, long o1) {
      return (o1 < o2 ? -1 : (o1 == o2 ? 0 : 1));
    }
  };


  private LongColumn copy() {
    LongColumn copy = emptyCopy();
    for (long i : data) {
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

  public long get(int index) {
    return data.getLong(index);
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
      long prim1 = get(i1);
      long prim2 = get(i2);
      return LongComparisonUtil.getInstance().compare(prim1, prim2);
    }
  };

  public long max() {
    return StatUtil.max(this);
  }

  public long min() {
    return StatUtil.min(this);
  }

  public long firstElement() {
    if (size() > 0) {
      return get(0);
    }
    return MISSING_VALUE;
  }

  public RoaringBitmap isPositive() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (long next : data) {
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
    for (long next : data) {
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
    for (long next : data) {
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
    for (long next : data) {
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
    for (long next : data) {
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
    for (long x : data) {
      if ((x & 1) != 0) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public FloatArrayList toFloatArray() {
    FloatArrayList output = new FloatArrayList(data.size());
    for (long aData : data) {
      output.add(aData);
    }
    return output;
  }

  public String print() {
    StringBuilder builder = new StringBuilder();
    builder.append(title());
    for (long i : data){
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
    LongColumn longColumn = (LongColumn) column;
    for (int i = 0; i < longColumn.size(); i++) {
      add(longColumn.get(i));
    }
  }

  @Override
  public LongIterator iterator() {
    return data.iterator();
  }
}
