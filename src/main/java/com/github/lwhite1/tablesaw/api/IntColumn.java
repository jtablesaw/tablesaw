package com.github.lwhite1.tablesaw.api;

import com.github.lwhite1.tablesaw.columns.AbstractColumn;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.filtering.IntBiPredicate;
import com.github.lwhite1.tablesaw.filtering.IntPredicate;
import com.github.lwhite1.tablesaw.io.TypeUtils;
import com.github.lwhite1.tablesaw.mapping.IntMapUtils;
import com.github.lwhite1.tablesaw.sorting.IntComparisonUtil;
import com.github.lwhite1.tablesaw.store.ColumnMetadata;
import com.github.lwhite1.tablesaw.util.ReverseIntComparator;
import com.github.lwhite1.tablesaw.util.StatUtil;
import com.github.lwhite1.tablesaw.util.Stats;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.roaringbitmap.RoaringBitmap;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A column that contains signed 4 byte integer values
 */
public class IntColumn extends AbstractColumn implements IntMapUtils {

  public static final int MISSING_VALUE = (int) ColumnType.INTEGER.getMissingValue();
  private static final int DEFAULT_ARRAY_SIZE = 128;
  private static final int BYTE_SIZE = 4;

  private IntArrayList data;

  public static IntColumn create(String name) {
    return new IntColumn(name, DEFAULT_ARRAY_SIZE);
  }

  public static IntColumn create(ColumnMetadata metadata) {
    return new IntColumn(metadata);
  }

  public static IntColumn create(String name, int arraySize) {
    return new IntColumn(name, arraySize);
  }

  public static IntColumn create(String name, IntArrayList ints) {
    IntColumn column = new IntColumn(name, ints.size());
    column.data.addAll(ints);
    return column;
  }

  public IntColumn(String name, int initialSize) {
    super(name);
    data = new IntArrayList(initialSize);
  }

  public IntColumn(ColumnMetadata metadata) {
    super(metadata);
    data = new IntArrayList(metadata.getSize());
  }

  public IntArrayList data() {
    return data;
  }

  public IntColumn(String name) {
    super(name);
    data = new IntArrayList(DEFAULT_ARRAY_SIZE);
  }

  public int size() {
    return data.size();
  }

  @Override
  public ColumnType type() {
    return ColumnType.INTEGER;
  }

  public long sum() {
    long sum = 0;
    for (int i : data) {
      sum += i;
    }
    return sum;
  }

  public void add(int i) {
    data.add(i);
  }

  public void set(int index, int value) {
    data.set(index, value);
  }

  public RoaringBitmap isLessThan(int i) {
    return apply(isLessThan, i);
  }

  public RoaringBitmap isGreaterThan(int i) {
    return apply(isGreaterThan, i);
  }

  public RoaringBitmap isGreaterThanOrEqualTo(int i) {
    return apply(isGreaterThanOrEqualTo, i);
  }

  public RoaringBitmap isLessThanOrEqualTo(int i) {
    return apply(isLessThanOrEqualTo, i);
  }

  public RoaringBitmap isEqualTo(int i) {
    return apply(isEqualTo, i);
  }

  public RoaringBitmap isMissing() {
    return apply(isMissing);
  }

  public RoaringBitmap isNotMissing() {
    return apply(isNotMissing);
  }

  public RoaringBitmap isEqualTo(IntColumn other) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    IntIterator otherIterator = other.iterator();
    for (int next : data) {
      int otherNext = otherIterator.nextInt();
      if (next == otherNext) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  @Override
  public Table summary() {
    return StatUtil.stats(this).asTable("Column: " + name());
  }

  /**
   * Returns the count of missing values in this column
   */
  @Override
  public int countMissing() {
    int count = 0;
    for (int i = 0; i < size(); i++) {
      if (get(i) == MISSING_VALUE) {
        count++;
      }
    }
    return count;
  }

  @Override
  public int countUnique() {
    RoaringBitmap roaringBitmap = new RoaringBitmap();
    data.forEach(roaringBitmap::add);
    return roaringBitmap.getCardinality();
  }

  @Override
  public IntColumn unique() {
    RoaringBitmap roaringBitmap = new RoaringBitmap();
    data.forEach(roaringBitmap::add);
    return IntColumn.create(name() + " Unique values", IntArrayList.wrap(roaringBitmap.toArray()));
  }

  public IntSet asSet() {
    return new IntOpenHashSet(data);
  }

  @Override
  public String getString(int row) {
    return String.valueOf(data.getInt(row));
  }

  @Override
  public IntColumn emptyCopy() {
    return new IntColumn(name(), DEFAULT_ARRAY_SIZE);
  }

  @Override
  public IntColumn emptyCopy(int rowSize) {
    return new IntColumn(name(), rowSize);
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
    IntArrays.parallelQuickSort(data.elements(), ReverseIntComparator.instance());
  }

  @Override
  public IntColumn copy() {
    return create(name(), data);
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
  public static int convert(String stringValue) {
    if (Strings.isNullOrEmpty(stringValue) || TypeUtils.MISSING_INDICATORS.contains(stringValue)) {
      return MISSING_VALUE;
    }
    Matcher matcher = COMMA_PATTERN.matcher(stringValue);
    return Integer.parseInt(matcher.replaceAll(""));
  }

  private static final Pattern COMMA_PATTERN = Pattern.compile(",");

  public int get(int index) {
    return data.getInt(index);
  }

  @Override
  public it.unimi.dsi.fastutil.ints.IntComparator rowComparator() {
    return comparator;
  }

  final it.unimi.dsi.fastutil.ints.IntComparator comparator = new it.unimi.dsi.fastutil.ints.IntComparator() {

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

  public int max() {
    return StatUtil.max(this);
  }

  public int min() {
    return StatUtil.min(this);
  }

  public int firstElement() {
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
    for (int aData : data) {
      output.add(aData);
    }
    return output;
  }

  public double[] toDoubleArray() {
    double[] output = new double[data.size()];
    for (int i = 0; i < data.size(); i++) {
      output[i] = data.getInt(i);
    }
    return output;
  }

  public String print() {
    StringBuilder builder = new StringBuilder();
    builder.append(title());
    for (int i : data) {
      builder.append(String.valueOf(i));
      builder.append('\n');
    }
    return builder.toString();
  }

  @Override
  public String toString() {
    return "Int column: " + name();
  }

  @Override
  public void append(Column column) {
    Preconditions.checkArgument(column.type() == this.type());
    IntColumn intColumn = (IntColumn) column;
    for (int i = 0; i < intColumn.size(); i++) {
      add(intColumn.get(i));
    }
  }

  public IntColumn selectIf(IntPredicate predicate) {
    IntColumn column = emptyCopy();
    IntIterator intIterator = iterator();
    while (intIterator.hasNext()) {
      int next = intIterator.nextInt();
      if (predicate.test(next)) {
        column.add(next);
      }
    }
    return column;
  }

  public IntColumn select(RoaringBitmap bitmap) {
    IntColumn column = emptyCopy();
    org.roaringbitmap.IntIterator intIterator = bitmap.getIntIterator();
    while (intIterator.hasNext()) {
      int next = intIterator.next();
      column.add(data.getInt(next));
    }
    return column;
  }

  public RoaringBitmap apply(IntPredicate predicate) {
    RoaringBitmap bitmap = new RoaringBitmap();
    for (int idx = 0; idx < data.size(); idx++) {
      int next = data.getInt(idx);
      if (predicate.test(next)) {
        bitmap.add(idx);
      }
    }
    return bitmap;
  }

  public RoaringBitmap apply(IntBiPredicate predicate, int value) {
    RoaringBitmap bitmap = new RoaringBitmap();
    for (int idx = 0; idx < data.size(); idx++) {
      int next = data.getInt(idx);
      if (predicate.test(next, value)) {
        bitmap.add(idx);
      }
    }
    return bitmap;
  }

  public long sumIf(IntPredicate predicate) {
    long sum = 0;
    IntIterator intIterator = iterator();
    while (intIterator.hasNext()) {
      int next = intIterator.nextInt();
      if (predicate.test(next)) {
        sum += next;
      }
    }
    return sum;
  }

  public long countIf(IntPredicate predicate) {
    long count = 0;
    IntIterator intIterator = iterator();
    while (intIterator.hasNext()) {
      int next = intIterator.nextInt();
      if (predicate.test(next)) {
        count++;
      }
    }
    return count;
  }

  public IntColumn remainder(IntColumn column2) {
    IntColumn result = IntColumn.create(name() + " % " + column2.name(), size());
    for (int r = 0; r < size(); r++) {
      result.add(get(r) % column2.get(r));
    }
    return result;
  }

  public IntColumn add(IntColumn column2) {
    IntColumn result = IntColumn.create(name() + " + " + column2.name(), size());
    for (int r = 0; r < size(); r++) {
      result.add(get(r) - column2.get(r));
    }
    return result;
  }

  public IntColumn subtract(IntColumn column2) {
    IntColumn result = IntColumn.create(name() + " - " + column2.name(), size());
    for (int r = 0; r < size(); r++) {
      result.add(get(r) + column2.get(r));
    }
    return result;
  }

  public IntColumn multiply(IntColumn column2) {
    IntColumn result = IntColumn.create(name() + " * " + column2.name(), size());
    for (int r = 0; r < size(); r++) {
      result.add(get(r) * column2.get(r));
    }
    return result;
  }

  public FloatColumn multiply(FloatColumn column2) {
    FloatColumn result = FloatColumn.create(name() + " * " + column2.name(), size());
    for (int r = 0; r < size(); r++) {
      result.add(get(r) * column2.get(r));
    }
    return result;
  }

  public IntColumn divide(IntColumn column2) {
    IntColumn result = IntColumn.create(name() + " / " + column2.name(), size());
    for (int r = 0; r < size(); r++) {
      result.add(get(r) / column2.get(r));
    }
    return result;
  }

  public FloatColumn divide(FloatColumn column2) {
    FloatColumn result = FloatColumn.create(name() + " / " + column2.name(), size());
    for (int r = 0; r < size(); r++) {
      result.add(get(r) / column2.get(r));
    }
    return result;
  }

  /**
   * Returns the largest ("top") n values in the column
   *
   * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
   *          number of observations in the column
   * @return A list, possibly empty, of the largest observations
   */
  public IntArrayList top(int n) {
    IntArrayList top = new IntArrayList();
    int[] values = data.toIntArray();
    IntArrays.parallelQuickSort(values, ReverseIntComparator.instance());
    for (int i = 0; i < n && i < values.length; i++) {
      top.add(values[i]);
    }
    return top;
  }

  /**
   * Returns the smallest ("bottom") n values in the column
   *
   * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
   *          number of observations in the column
   * @return A list, possibly empty, of the smallest n observations
   */
  public IntArrayList bottom(int n) {
    IntArrayList bottom = new IntArrayList();
    int[] values = data.toIntArray();
    IntArrays.parallelQuickSort(values);
    for (int i = 0; i < n && i < values.length; i++) {
      bottom.add(values[i]);
    }
    return bottom;
  }

  @Override
  public IntIterator iterator() {
    return data.iterator();
  }

  public Stats stats() {
    FloatColumn values = FloatColumn.create(name(), toFloatArray());
    return StatUtil.stats(values);
  }

  public boolean contains(int i) {
    return data.contains(i);
  }

  @Override
  public int byteSize() {
    return BYTE_SIZE;
  }

  /**
   * Returns the contents of the cell at rowNumber as a byte[]
   */
  @Override
  public byte[] asBytes(int rowNumber) {
    return ByteBuffer.allocate(4).putInt(get(rowNumber)).array();
  }
}
