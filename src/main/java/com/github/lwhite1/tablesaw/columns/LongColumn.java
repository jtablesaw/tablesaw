package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.Table;
import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.filter.LongBiPredicate;
import com.github.lwhite1.tablesaw.filter.LongPredicate;
import com.github.lwhite1.tablesaw.io.TypeUtils;
import com.github.lwhite1.tablesaw.mapper.LongMapUtils;
import com.github.lwhite1.tablesaw.sorting.LongComparisonUtil;
import com.github.lwhite1.tablesaw.store.ColumnMetadata;
import com.github.lwhite1.tablesaw.util.ReverseLongComparator;
import com.github.lwhite1.tablesaw.util.StatUtil;
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
    return ColumnType.LONG_INT;
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

  public void set(int index, long value) {
    data.set(index, value);
  }

  public RoaringBitmap isLessThan(long i) {
    return apply(isLessThan, i);
  }

  public RoaringBitmap isGreaterThan(int i) {
    return apply(isGreaterThan, i);
  }

  public RoaringBitmap isGreaterThanOrEqualTo(int i) {
    return apply(isGreaterThanOrEqualTo, i);
  }

  public RoaringBitmap isLessThanOrEqualTo(int f) {
    return apply(isLessThanOrEqualTo, f);
  }

  public RoaringBitmap isEqualTo(long i) {
    return apply(isEqualTo, i);
  }

  public RoaringBitmap isEqualTo(LongColumn f) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    LongIterator longIterator = f.iterator();
    for (long next : data) {
      if (next == longIterator.next()) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  @Override
  public Table summary() {
    return StatUtil.stats(this).asTable(name());
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
  public void sortAscending() {
    Arrays.parallelSort(data.elements());
  }

  @Override
  public void sortDescending() {
    LongArrays.parallelQuickSort(data.elements(), ReverseLongComparator.instance());
  }

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
  public static long convert(String stringValue) {
    if (Strings.isNullOrEmpty(stringValue) || TypeUtils.MISSING_INDICATORS.contains(stringValue)) {
      return (long) ColumnType.LONG_INT.getMissingValue();
    }
    Matcher matcher = COMMA_PATTERN.matcher(stringValue);
    return Long.parseLong(matcher.replaceAll(""));
  }

  private static final Pattern COMMA_PATTERN = Pattern.compile(",");

  public long get(int index) {
    return data.getLong(index);
  }

  @Override
  public IntComparator rowComparator() {
    return comparator;
  }

  private final IntComparator comparator = new IntComparator() {

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
    return apply(isPositive);
  }

  public RoaringBitmap isNegative() {
    return apply(isNegative);
  }

  public RoaringBitmap isNonNegative() {
    return apply(isNonNegative);
  }

  public RoaringBitmap isZero() {
    return apply(isZero);
  }

  public RoaringBitmap isEven() {
    return apply(isEven);
  }

  public RoaringBitmap isOdd() {
    return apply(isOdd);
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
    return "LongInt column: " + name();
  }

  @Override
  public void append(Column column) {
    Preconditions.checkArgument(column.type() == this.type());
    LongColumn longColumn = (LongColumn) column;
    for (int i = 0; i < longColumn.size(); i++) {
      add(longColumn.get(i));
    }
  }

  public LongColumn selectIf(LongPredicate predicate) {
    LongColumn column = emptyCopy();
    LongIterator intIterator = iterator();
    while(intIterator.hasNext()) {
      long next = intIterator.nextLong();
      if (predicate.test(next)) {
        column.add(next);
      }
    }
    return column;
  }

  //TODO(lwhite): Implement
  @Override
  public LongColumn max(int n) {
    return null;
  }

  //TODO(lwhite): Implement
  @Override
  public LongColumn min(int n) {
    return null;
  }

  @Override
  public LongIterator iterator() {
    return data.iterator();
  }

  public RoaringBitmap apply(LongPredicate predicate) {
    RoaringBitmap bitmap = new RoaringBitmap();
    for(int idx = 0; idx < data.size(); idx++) {
      long next = data.getLong(idx);
      if (predicate.test(next)) {
        bitmap.add(idx);
      }
    }
    return bitmap;
  }

  public RoaringBitmap apply(LongBiPredicate predicate, long valueToCompareAgainst) {
    RoaringBitmap bitmap = new RoaringBitmap();
    for(int idx = 0; idx < data.size(); idx++) {
      long next = data.getLong(idx);
      if (predicate.test(next, valueToCompareAgainst)) {
        bitmap.add(idx);
      }
    }
    return bitmap;
  }
}
