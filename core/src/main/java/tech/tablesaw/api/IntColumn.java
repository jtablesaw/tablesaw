package tech.tablesaw.api;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.stream.IntStream;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.IntColumnType;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;

public class IntColumn extends NumberColumn<IntColumn, Integer>
    implements CategoricalColumn<Integer> {

  private final IntArrayList data;

  protected IntColumn(final String name, IntArrayList data) {
    super(IntColumnType.instance(), name);
    setPrintFormatter(NumberColumnFormatter.ints());
    this.data = data;
  }

  public static IntColumn create(final String name) {
    return new IntColumn(name, new IntArrayList());
  }

  public static IntColumn create(final String name, final int... arr) {
    return new IntColumn(name, new IntArrayList(arr));
  }

  public static IntColumn create(final String name, final Integer[] arr) {
    IntColumn newColumn = IntColumn.create(name, 0);
    for (Integer integer : arr) {
      newColumn.append(integer);
    }
    return newColumn;
  }

  public static IntColumn create(String name, int initialSize) {
    IntColumn column = new IntColumn(name, new IntArrayList(initialSize));
    for (int i = 0; i < initialSize; i++) {
      column.appendMissing();
    }
    return column;
  }

  public static IntColumn create(String name, IntStream stream) {
    IntArrayList list = new IntArrayList();
    stream.forEach(list::add);
    return new IntColumn(name, list);
  }

  @Override
  public IntColumn createCol(String name, int initialSize) {
    return create(name, initialSize);
  }

  @Override
  public IntColumn createCol(String name) {
    return create(name);
  }

  /**
   * Returns a new numeric column initialized with the given name and size. The values in the column
   * are integers beginning at startsWith and continuing through size (exclusive), monotonically
   * increasing by 1 TODO consider a generic fill function including steps or random samples from
   * various distributions
   */
  public static IntColumn indexColumn(
      final String columnName, final int size, final int startsWith) {
    final IntColumn indexColumn = IntColumn.create(columnName, size);
    for (int i = 0; i < size; i++) {
      indexColumn.set(i, i + startsWith);
    }
    return indexColumn;
  }

  @Override
  public int size() {
    return data.size();
  }

  @Override
  public void clear() {
    data.clear();
  }

  public static boolean valueIsMissing(int value) {
    return IntColumnType.valueIsMissing(value);
  }

  @Override
  public Integer get(int index) {
    int result = getInt(index);
    return isMissingValue(result) ? null : result;
  }

  @Override
  public IntColumn subset(final int[] rows) {
    final IntColumn c = this.emptyCopy();
    for (final int row : rows) {
      c.append(getInt(row));
    }
    return c;
  }

  @Override
  public IntColumn unique() {
    final IntSet values = new IntOpenHashSet();
    for (int i = 0; i < size(); i++) {
      values.add(getInt(i));
    }
    final IntColumn column = IntColumn.create(name() + " Unique values");
    for (int value : values) {
      column.append(value);
    }
    return column;
  }

  @Override
  public IntColumn top(int n) {
    final IntArrayList top = new IntArrayList();
    final int[] values = data.toIntArray();
    IntArrays.parallelQuickSort(values, IntComparators.OPPOSITE_COMPARATOR);
    for (int i = 0; i < n && i < values.length; i++) {
      top.add(values[i]);
    }
    return new IntColumn(name() + "[Top " + n + "]", top);
  }

  @Override
  public IntColumn bottom(final int n) {
    final IntArrayList bottom = new IntArrayList();
    final int[] values = data.toIntArray();
    IntArrays.parallelQuickSort(values);
    for (int i = 0; i < n && i < values.length; i++) {
      bottom.add(values[i]);
    }
    return new IntColumn(name() + "[Bottoms " + n + "]", bottom);
  }

  @Override
  public IntColumn lag(int n) {
    final int srcPos = n >= 0 ? 0 : 0 - n;
    final int[] dest = new int[size()];
    final int destPos = n <= 0 ? 0 : n;
    final int length = n >= 0 ? size() - n : size() + n;

    for (int i = 0; i < size(); i++) {
      dest[i] = IntColumnType.missingValueIndicator();
    }

    int[] array = data.toIntArray();

    System.arraycopy(array, srcPos, dest, destPos, length);
    return new IntColumn(name() + " lag(" + n + ")", new IntArrayList(dest));
  }

  @Override
  public IntColumn removeMissing() {
    IntColumn result = copy();
    result.clear();
    IntListIterator iterator = data.iterator();
    while (iterator.hasNext()) {
      final int v = iterator.nextInt();
      if (!isMissingValue(v)) {
        result.append(v);
      }
    }
    return result;
  }

  public IntColumn append(int i) {
    data.add(i);
    return this;
  }

  public IntColumn append(Integer val) {
    if (val == null) {
      appendMissing();
    } else {
      append(val.intValue());
    }
    return this;
  }

  @Override
  public IntColumn copy() {
    return new IntColumn(name(), data.clone());
  }

  @Override
  public Iterator<Integer> iterator() {
    return data.iterator();
  }

  @Override
  public Integer[] asObjectArray() {
    final Integer[] output = new Integer[size()];
    for (int i = 0; i < size(); i++) {
      if (!isMissing(i)) {
        output[i] = getInt(i);
      } else {
        output[i] = null;
      }
    }
    return output;
  }

  @Override
  public int compare(Integer o1, Integer o2) {
    return Integer.compare(o1, o2);
  }

  @Override
  public IntColumn set(int i, Integer val) {
    return set(i, (int) val);
  }

  public IntColumn set(int i, int val) {
    data.set(i, val);
    return this;
  }

  @Override
  public IntColumn append(final Column<Integer> column) {
    Preconditions.checkArgument(column.type() == this.type());
    final IntColumn numberColumn = (IntColumn) column;
    final int size = numberColumn.size();
    for (int i = 0; i < size; i++) {
      append(numberColumn.getInt(i));
    }
    return this;
  }

  @Override
  public IntColumn append(Column<Integer> column, int row) {
    Preconditions.checkArgument(column.type() == this.type());
    return append(((IntColumn) column).getInt(row));
  }

  @Override
  public IntColumn set(int row, Column<Integer> column, int sourceRow) {
    Preconditions.checkArgument(column.type() == this.type());
    return set(row, ((IntColumn) column).getInt(sourceRow));
  }

  @Override
  public Column<Integer> set(int row, String stringValue, AbstractColumnParser<?> parser) {
    return set(row, parser.parseInt(stringValue));
  }

  @Override
  public IntColumn appendMissing() {
    return append(IntColumnType.missingValueIndicator());
  }

  @Override
  public byte[] asBytes(int rowNumber) {
    return ByteBuffer.allocate(IntColumnType.instance().byteSize())
        .putInt(getInt(rowNumber))
        .array();
  }

  @Override
  public String getString(final int row) {
    final int value = getInt(row);
    if (IntColumnType.valueIsMissing(value)) {
      return "";
    }
    return String.valueOf(getPrintFormatter().format(value));
  }

  @Override
  public int countUnique() {
    IntSet uniqueElements = new IntOpenHashSet();
    for (int i = 0; i < size(); i++) {
      uniqueElements.add(getInt(i));
    }
    return uniqueElements.size();
  }

  /**
   * Returns the value at the given index. The actual value is returned if the ColumnType is
   * INTEGER. Otherwise the value is rounded as described below.
   *
   * <p>Returns the closest {@code int} to the argument, with ties rounding to positive infinity.
   *
   * <p>Special cases:
   *
   * <ul>
   *   <li>If the argument is NaN, the result is 0.
   *   <li>If the argument is positive infinity or any value greater than or equal to the value of
   *       {@code Integer.MAX_VALUE}, an error will be thrown
   * </ul>
   *
   * @param row the index of the value to be rounded to an integer.
   * @return the value of the argument rounded to the nearest {@code int} value.
   * @throws ClassCastException if the absolute value of the value to be rounded is too large to be
   *     cast to an int
   */
  public int getInt(int row) {
    return data.getInt(row);
  }

  @Override
  public double getDouble(int row) {
    int value = data.getInt(row);
    if (isMissingValue(value)) {
      return DoubleColumnType.missingValueIndicator();
    }
    return value;
  }

  public boolean isMissingValue(int value) {
    return IntColumnType.valueIsMissing(value);
  }

  @Override
  public boolean isMissing(int rowNumber) {
    return isMissingValue(getInt(rowNumber));
  }

  @Override
  public void sortAscending() {
    data.sort(IntComparators.NATURAL_COMPARATOR);
  }

  @Override
  public void sortDescending() {
    data.sort(IntComparators.OPPOSITE_COMPARATOR);
  }

  @Override
  public IntColumn appendObj(Object obj) {
    if (obj == null) {
      return appendMissing();
    }
    if (obj instanceof Integer) {
      return append((int) obj);
    }
    throw new IllegalArgumentException("Could not append " + obj.getClass());
  }

  @Override
  public IntColumn appendCell(final String value) {
    try {
      return append(IntColumnType.DEFAULT_PARSER.parseInt(value));
    } catch (final NumberFormatException e) {
      throw new NumberFormatException(
          "Error adding value to column " + name() + ": " + e.getMessage());
    }
  }

  @Override
  public IntColumn appendCell(final String value, AbstractColumnParser<?> parser) {
    try {
      return append(parser.parseInt(value));
    } catch (final NumberFormatException e) {
      throw new NumberFormatException(
          "Error adding value to column " + name() + ": " + e.getMessage());
    }
  }

  @Override
  public String getUnformattedString(final int row) {
    final int value = getInt(row);
    if (IntColumnType.valueIsMissing(value)) {
      return "";
    }
    return String.valueOf(value);
  }

  /**
   * Returns a new LongColumn containing a value for each value in this column
   *
   * <p>A widening primitive conversion from int to long does not lose any information at all; the
   * numeric value is preserved exactly.
   *
   * <p>A missing value in the receiver is converted to a missing value in the result
   */
  @Override
  public LongColumn asLongColumn() {
    LongColumn result = LongColumn.create(name());
    for (int d : data) {
      if (IntColumnType.valueIsMissing(d)) {
        result.appendMissing();
      } else {
        result.append(d);
      }
    }
    return result;
  }

  /**
   * Returns a new FloatColumn containing a value for each value in this column, truncating if
   * necessary.
   *
   * <p>A widening primitive conversion from an int to a float does not lose information about the
   * overall magnitude of a numeric value. It may, however, result in loss of precision - that is,
   * the result may lose some of the least significant bits of the value. In this case, the
   * resulting floating-point value will be a correctly rounded version of the integer value, using
   * IEEE 754 round-to-nearest mode.
   *
   * <p>Despite the fact that a loss of precision may occur, a widening primitive conversion never
   * results in a run-time exception.
   *
   * <p>A missing value in the receiver is converted to a missing value in the result
   */
  @Override
  public FloatColumn asFloatColumn() {
    FloatColumn result = FloatColumn.create(name());
    for (int d : data) {
      if (IntColumnType.valueIsMissing(d)) {
        result.appendMissing();
      } else {
        result.append(d);
      }
    }
    return result;
  }

  /**
   * Returns a new DoubleColumn containing a value for each value in this column, truncating if
   * necessary.
   *
   * <p>A widening primitive conversion from an int to a double does not lose information about the
   * overall magnitude of a numeric value. It may, however, result in loss of precision - that is,
   * the result may lose some of the least significant bits of the value. In this case, the
   * resulting floating-point value will be a correctly rounded version of the integer value, using
   * IEEE 754 round-to-nearest mode.
   *
   * <p>Despite the fact that a loss of precision may occur, a widening primitive conversion never
   * results in a run-time exception.
   *
   * <p>A missing value in the receiver is converted to a missing value in the result
   */
  @Override
  public DoubleColumn asDoubleColumn() {
    DoubleColumn result = DoubleColumn.create(name());
    for (int d : data) {
      if (IntColumnType.valueIsMissing(d)) {
        result.appendMissing();
      } else {
        result.append(d);
      }
    }
    return result;
  }

  /**
   * Returns a new ShortColumn containing a value for each value in this column
   *
   * <p>A narrowing conversion of a signed integer to an integral type T simply discards all but the
   * n lowest order bits, where n is the number of bits used to represent type T. In addition to a
   * possible loss of information about the magnitude of the numeric value, this may cause the sign
   * of the resulting value to differ from the sign of the input value.
   *
   * <p>In other words, if the element being converted is larger (or smaller) than Short.MAX_VALUE
   * (or Short.MIN_VALUE) you will not get a conventionally good conversion.
   *
   * <p>Despite the fact that overflow, underflow, or other loss of information may occur, a
   * narrowing primitive conversion never results in a run-time exception.
   *
   * <p>A missing value in the receiver is converted to a missing value in the result
   */
  @Override
  public ShortColumn asShortColumn() {
    ShortColumn result = ShortColumn.create(name());
    for (int d : data) {
      if (IntColumnType.valueIsMissing(d)) {
        result.appendMissing();
      } else {
        result.append((short) d);
      }
    }
    return result;
  }

  public IntColumn setMissing(int r) {
    set(r, IntColumnType.missingValueIndicator());
    return this;
  }
}
