package tech.tablesaw.api;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongComparators;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.stream.LongStream;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.LongColumnType;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;

public class LongColumn extends NumberColumn<LongColumn, Long> implements CategoricalColumn<Long> {

  private final LongArrayList data;

  private LongColumn(String name, LongArrayList data) {
    super(LongColumnType.instance(), name);
    setPrintFormatter(NumberColumnFormatter.ints());
    this.data = data;
  }

  public static LongColumn create(final String name) {
    return new LongColumn(name, new LongArrayList());
  }

  public static LongColumn create(String name, long... arr) {
    return new LongColumn(name, new LongArrayList(arr));
  }

  public static LongColumn create(String name, int initialSize) {
    LongColumn column = new LongColumn(name, new LongArrayList(initialSize));
    for (int i = 0; i < initialSize; i++) {
      column.appendMissing();
    }
    return column;
  }

  public static LongColumn create(String name, LongStream stream) {
    LongArrayList list = new LongArrayList();
    stream.forEach(list::add);
    return new LongColumn(name, list);
  }

  @Override
  public LongColumn createCol(String name, int initialSize) {
    return create(name, initialSize);
  }

  @Override
  public LongColumn createCol(String name) {
    return create(name);
  }

  /**
   * Returns a new numeric column initialized with the given name and size. The values in the column
   * are integers beginning at startsWith and continuing through size (exclusive), monotonically
   * increasing by 1 TODO consider a generic fill function including steps or random samples from
   * various distributions
   */
  public static LongColumn indexColumn(
      final String columnName, final int size, final int startsWith) {
    final LongColumn indexColumn = LongColumn.create(columnName, size);
    for (long i = 0; i < size; i++) {
      indexColumn.append(i + startsWith);
    }
    return indexColumn;
  }

  @Override
  public String getString(final int row) {
    final long value = getLong(row);
    if (LongColumnType.valueIsMissing(value)) {
      return "";
    }
    return String.valueOf(getPrintFormatter().format(value));
  }

  public static boolean valueIsMissing(long value) {
    return LongColumnType.valueIsMissing(value);
  }

  @Override
  public int size() {
    return data.size();
  }

  @Override
  public void clear() {
    data.clear();
  }

  @Override
  public Long get(int index) {
    long result = getLong(index);
    return isMissingValue(result) ? null : result;
  }

  @Override
  public LongColumn subset(final int[] rows) {
    final LongColumn c = this.emptyCopy();
    for (final int row : rows) {
      c.append(getLong(row));
    }
    return c;
  }

  @Override
  public LongColumn unique() {
    final LongSet values = new LongOpenHashSet();
    for (int i = 0; i < size(); i++) {
      values.add(getLong(i));
    }
    final LongColumn column = LongColumn.create(name() + " Unique values");
    for (long value : values) {
      column.append(value);
    }
    return column;
  }

  @Override
  public LongColumn top(int n) {
    final LongArrayList top = new LongArrayList();
    final long[] values = data.toLongArray();
    LongArrays.parallelQuickSort(values, LongComparators.OPPOSITE_COMPARATOR);
    for (int i = 0; i < n && i < values.length; i++) {
      top.add(values[i]);
    }
    return new LongColumn(name() + "[Top " + n + "]", top);
  }

  @Override
  public LongColumn bottom(final int n) {
    final LongArrayList bottom = new LongArrayList();
    final long[] values = data.toLongArray();
    LongArrays.parallelQuickSort(values);
    for (int i = 0; i < n && i < values.length; i++) {
      bottom.add(values[i]);
    }
    return new LongColumn(name() + "[Bottoms " + n + "]", bottom);
  }

  @Override
  public LongColumn lag(int n) {
    final int srcPos = n >= 0 ? 0 : 0 - n;
    final long[] dest = new long[size()];
    final int destPos = n <= 0 ? 0 : n;
    final int length = n >= 0 ? size() - n : size() + n;

    for (int i = 0; i < size(); i++) {
      dest[i] = LongColumnType.missingValueIndicator();
    }

    long[] array = data.toLongArray();

    System.arraycopy(array, srcPos, dest, destPos, length);
    return new LongColumn(name() + " lag(" + n + ")", new LongArrayList(dest));
  }

  @Override
  public LongColumn removeMissing() {
    LongColumn result = copy();
    result.clear();
    LongListIterator iterator = data.iterator();
    while (iterator.hasNext()) {
      final long v = iterator.nextLong();
      if (!isMissingValue(v)) {
        result.append(v);
      }
    }
    return result;
  }

  public LongColumn append(long i) {
    data.add(i);
    return this;
  }

  public LongColumn append(Long val) {
    if (val == null) {
      appendMissing();
    } else {
      append(val.longValue());
    }
    return this;
  }

  @Override
  public LongColumn copy() {
    return new LongColumn(name(), data.clone());
  }

  public long[] asLongArray() {
    // TODO: Need to figure out how to handle NaN -> Maybe just use a list with nulls?
    final long[] result = new long[size()];
    for (int i = 0; i < size(); i++) {
      result[i] = getLong(i);
    }
    return result;
  }

  /**
   * Returns a DateTimeColumn where each value is the LocalDateTime represented by the values in
   * this column
   *
   * <p>The values in this column must be longs that represent the time in milliseconds from the
   * epoch as in standard Java date/time calculations
   *
   * @param offset The ZoneOffset to use in the calculation
   * @return A column of LocalDateTime values
   */
  public DateTimeColumn asDateTimes(ZoneOffset offset) {
    DateTimeColumn column = DateTimeColumn.create(name() + ": date time");
    for (int i = 0; i < size(); i++) {
      column.append(Instant.ofEpochMilli(getLong(i)).atZone(offset).toLocalDateTime());
    }
    return column;
  }

  @Override
  public Iterator<Long> iterator() {
    return data.iterator();
  }

  @Override
  public Long[] asObjectArray() {
    final Long[] output = new Long[size()];
    for (int i = 0; i < size(); i++) {
      if (!isMissing(i)) {
        output[i] = getLong(i);
      } else {
        output[i] = null;
      }
    }
    return output;
  }

  @Override
  public int compare(Long o1, Long o2) {
    return Long.compare(o1, o2);
  }

  @Override
  public LongColumn set(int i, Long val) {
    return set(i, (long) val);
  }

  public LongColumn set(int i, long val) {
    data.set(i, val);
    return this;
  }

  @Override
  public Column<Long> set(int row, String stringValue, AbstractColumnParser<?> parser) {
    return set(row, parser.parseLong(stringValue));
  }

  @Override
  public LongColumn append(final Column<Long> column) {
    Preconditions.checkArgument(column.type() == this.type());
    final LongColumn numberColumn = (LongColumn) column;
    final int size = numberColumn.size();
    for (int i = 0; i < size; i++) {
      append(numberColumn.getLong(i));
    }
    return this;
  }

  @Override
  public LongColumn append(Column<Long> column, int row) {
    Preconditions.checkArgument(column.type() == this.type());
    return append(((LongColumn) column).getLong(row));
  }

  @Override
  public LongColumn set(int row, Column<Long> column, int sourceRow) {
    Preconditions.checkArgument(column.type() == this.type());
    return set(row, ((LongColumn) column).getLong(sourceRow));
  }

  @Override
  public LongColumn appendMissing() {
    return append(LongColumnType.missingValueIndicator());
  }

  @Override
  public byte[] asBytes(int rowNumber) {
    return ByteBuffer.allocate(LongColumnType.instance().byteSize())
        .putLong(getLong(rowNumber))
        .array();
  }

  @Override
  public int countUnique() {
    LongSet uniqueElements = new LongOpenHashSet();
    for (int i = 0; i < size(); i++) {
      uniqueElements.add(getLong(i));
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
  public long getLong(int row) {
    return data.getLong(row);
  }

  @Override
  public double getDouble(int row) {
    long value = data.getLong(row);
    if (isMissingValue(value)) {
      return DoubleColumnType.missingValueIndicator();
    }
    return value;
  }

  public boolean isMissingValue(long value) {
    return LongColumnType.valueIsMissing(value);
  }

  @Override
  public boolean isMissing(int rowNumber) {
    return isMissingValue(getLong(rowNumber));
  }

  @Override
  public Column<Long> setMissing(int i) {
    return set(i, LongColumnType.missingValueIndicator());
  }

  @Override
  public void sortAscending() {
    data.sort(LongComparators.NATURAL_COMPARATOR);
  }

  @Override
  public void sortDescending() {
    data.sort(LongComparators.OPPOSITE_COMPARATOR);
  }

  @Override
  public LongColumn appendObj(Object obj) {
    if (obj == null) {
      return appendMissing();
    }
    if (obj instanceof Long) {
      return append((long) obj);
    }
    throw new IllegalArgumentException("Could not append " + obj.getClass());
  }

  @Override
  public LongColumn appendCell(final String value) {
    try {
      return append(LongColumnType.DEFAULT_PARSER.parseLong(value));
    } catch (final NumberFormatException e) {
      throw new NumberFormatException(
          "Error adding value to column " + name() + ": " + e.getMessage());
    }
  }

  @Override
  public LongColumn appendCell(final String value, AbstractColumnParser<?> parser) {
    try {
      return append(parser.parseLong(value));
    } catch (final NumberFormatException e) {
      throw new NumberFormatException(
          "Error adding value to column " + name() + ": " + e.getMessage());
    }
  }

  @Override
  public String getUnformattedString(final int row) {
    final long value = getLong(row);
    if (LongColumnType.valueIsMissing(value)) {
      return "";
    }
    return String.valueOf(value);
  }

  @Override
  public Table countByCategory() {
    return null;
  }

  /**
   * Returns a new IntColumn containing a value for each value in this column
   *
   * <p>A narrowing conversion of a signed integer to an integral type T simply discards all but the
   * n lowest order bits, where n is the number of bits used to represent type T. In addition to a
   * possible loss of information about the magnitude of the numeric value, this may cause the sign
   * of the resulting value to differ from the sign of the input value.
   *
   * <p>In other words, if the element being converted is larger (or smaller) than Integer.MAX_VALUE
   * (or Integer.MIN_VALUE) you will not get a conventionally good conversion.
   *
   * <p>Despite the fact that overflow, underflow, or other loss of information may occur, a
   * narrowing primitive conversion never results in a run-time exception.
   *
   * <p>A missing value in the receiver is converted to a missing value in the result
   */
  @Override
  public IntColumn asIntColumn() {
    IntColumn result = IntColumn.create(name());
    for (long d : data) {
      if (LongColumnType.valueIsMissing(d)) {
        result.appendMissing();
      } else {
        result.append((int) d);
      }
    }
    return result;
  }

  /**
   * Returns a new ShortColumn containing a value for each value in this column
   *
   * <p>A narrowing conversion of a signed long to an integral type T simply discards all but the n
   * lowest order bits, where n is the number of bits used to represent type T. In addition to a
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
    for (long d : data) {
      if (LongColumnType.valueIsMissing(d)) {
        result.appendMissing();
      } else {
        result.append((short) d);
      }
    }
    return result;
  }

  /**
   * Returns a new FloatColumn containing a value for each value in this column
   *
   * <p>A widening primitive conversion from a long to a float does not lose information about the
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
    for (long d : data) {
      if (LongColumnType.valueIsMissing(d)) {
        result.appendMissing();
      } else {
        result.append(d);
      }
    }
    return result;
  }

  /**
   * Returns a new DoubleColumn containing a value for each value in this column
   *
   * <p>A widening primitive conversion from a long to a double does not lose information about the
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
    for (long d : data) {
      if (LongColumnType.valueIsMissing(d)) {
        result.appendMissing();
      } else {
        result.append(d);
      }
    }
    return result;
  }
}
