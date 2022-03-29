package tech.tablesaw.api;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Shorts;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortComparators;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.columns.numbers.ShortColumnType;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/** A column that contains short values */
public class ShortColumn extends NumberColumn<ShortColumn, Short>
    implements CategoricalColumn<Short> {

  protected final ShortArrayList data;

  protected ShortColumn(final String name, ShortArrayList data) {
    super(ShortColumnType.instance(), name, ShortColumnType.DEFAULT_PARSER);
    setPrintFormatter(NumberColumnFormatter.ints());
    this.data = data;
  }

  /** {@inheritDoc} */
  @Override
  public int valueHash(int rowNumber) {
    return getShort(rowNumber);
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(int rowNumber1, int rowNumber2) {
    return getShort(rowNumber1) == getShort(rowNumber2);
  }

  public static ShortColumn create(final String name) {
    return new ShortColumn(name, new ShortArrayList());
  }

  public static ShortColumn create(final String name, final short... arr) {
    return new ShortColumn(name, new ShortArrayList(arr));
  }

  public static ShortColumn create(final String name, final int initialSize) {
    ShortColumn column = new ShortColumn(name, new ShortArrayList(initialSize));
    for (int i = 0; i < initialSize; i++) {
      column.appendMissing();
    }
    return column;
  }

  public static ShortColumn create(final String name, final Short[] arr) {
    return new ShortColumn(name, new ShortArrayList(Shorts.toArray(Arrays.asList(arr))));
  }

  public static ShortColumn create(String name, Stream<Short> stream) {
    ShortColumn column = create(name);
    stream.forEach(column::append);
    return column;
  }

  /** {@inheritDoc} */
  @Override
  public ShortColumn createCol(final String name, final int initialSize) {
    return create(name, initialSize);
  }

  /** {@inheritDoc} */
  @Override
  public ShortColumn createCol(final String name) {
    return create(name);
  }

  public static boolean valueIsMissing(int value) {
    return ShortColumnType.valueIsMissing(value);
  }

  /** {@inheritDoc} */
  @Override
  public Short get(int index) {
    short result = getShort(index);
    return isMissingValue(result) ? null : result;
  }

  public short getShort(int index) {
    return data.getShort(index);
  }

  /** {@inheritDoc} */
  @Override
  public ShortColumn subset(final int[] rows) {
    final ShortColumn c = this.emptyCopy();
    for (final int row : rows) {
      c.append(getShort(row));
    }
    return c;
  }

  public Selection isIn(final int... numbers) {
    final Selection results = new BitmapBackedSelection();
    final IntRBTreeSet intSet = new IntRBTreeSet(numbers);
    for (int i = 0; i < size(); i++) {
      if (intSet.contains(getInt(i))) {
        results.add(i);
      }
    }
    return results;
  }

  public Selection isNotIn(final int... numbers) {
    final Selection results = new BitmapBackedSelection();
    results.addRange(0, size());
    results.andNot(isIn(numbers));
    return results;
  }

  /** {@inheritDoc} */
  @Override
  public int size() {
    return data.size();
  }

  /** {@inheritDoc} */
  @Override
  public void clear() {
    data.clear();
  }

  /** {@inheritDoc} */
  @Override
  public ShortColumn unique() {
    final ShortSet values = new ShortOpenHashSet();
    for (int i = 0; i < size(); i++) {
      values.add(getShort(i));
    }
    final ShortColumn column = ShortColumn.create(name() + " Unique values");

    for (short value : values) {
      column.append(value);
    }
    return column;
  }

  /** {@inheritDoc} */
  @Override
  public ShortColumn top(int n) {
    final ShortArrayList top = new ShortArrayList();
    final short[] values = data.toShortArray();
    ShortArrays.parallelQuickSort(values, ShortComparators.OPPOSITE_COMPARATOR);
    for (int i = 0; i < n && i < values.length; i++) {
      top.add(values[i]);
    }
    return new ShortColumn(name() + "[Top " + n + "]", top);
  }

  /** {@inheritDoc} */
  @Override
  public ShortColumn bottom(final int n) {
    final ShortArrayList bottom = new ShortArrayList();
    final short[] values = data.toShortArray();
    ShortArrays.parallelQuickSort(values);
    for (int i = 0; i < n && i < values.length; i++) {
      bottom.add(values[i]);
    }
    return new ShortColumn(name() + "[Bottoms " + n + "]", bottom);
  }

  /** {@inheritDoc} */
  @Override
  public ShortColumn lag(int n) {
    final int srcPos = n >= 0 ? 0 : -n;
    final short[] dest = new short[size()];
    final int destPos = Math.max(n, 0);
    final int length = n >= 0 ? size() - n : size() + n;

    for (int i = 0; i < size(); i++) {
      dest[i] = ShortColumnType.missingValueIndicator();
    }

    short[] array = data.toShortArray();

    System.arraycopy(array, srcPos, dest, destPos, length);
    return new ShortColumn(name() + " lag(" + n + ")", new ShortArrayList(dest));
  }

  /** {@inheritDoc} */
  @Override
  public ShortColumn removeMissing() {
    ShortColumn result = copy();
    result.clear();
    ShortListIterator iterator = data.iterator();
    while (iterator.hasNext()) {
      final short v = iterator.nextShort();
      if (!isMissingValue(v)) {
        result.append(v);
      }
    }
    return result;
  }

  public ShortColumn append(short i) {
    data.add(i);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public ShortColumn append(Short val) {
    if (val == null) {
      appendMissing();
    } else {
      append(val.shortValue());
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public ShortColumn emptyCopy() {
    return super.emptyCopy();
  }

  /** {@inheritDoc} */
  @Override
  public ShortColumn emptyCopy(final int rowSize) {
    return super.emptyCopy(rowSize);
  }

  /** {@inheritDoc} */
  @Override
  public ShortColumn copy() {
    ShortColumn copy = new ShortColumn(name(), data.clone());
    copy.setPrintFormatter(getPrintFormatter());
    copy.locale = locale;
    return copy;
  }

  /** {@inheritDoc} */
  @Override
  public Iterator<Short> iterator() {
    return data.iterator();
  }

  /** {@inheritDoc} */
  @Override
  public Short[] asObjectArray() {
    final Short[] output = new Short[size()];
    for (int i = 0; i < size(); i++) {
      if (!isMissing(i)) {
        output[i] = getShort(i);
      } else {
        output[i] = null;
      }
    }
    return output;
  }

  public short[] asShortArray() {
    return data.toShortArray();
  }

  /** {@inheritDoc} */
  @Override
  public int compare(Short o1, Short o2) {
    return Short.compare(o1, o2);
  }

  /** {@inheritDoc} */
  @Override
  public ShortColumn set(int i, Short val) {
    return val == null ? setMissing(i) : set(i, (short) val);
  }

  public ShortColumn set(int i, short val) {
    data.set(i, val);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public Column<Short> set(int row, String stringValue, AbstractColumnParser<?> parser) {
    return set(row, parser.parseShort(stringValue));
  }

  /** {@inheritDoc} */
  @Override
  public ShortColumn append(final Column<Short> column) {
    Preconditions.checkArgument(
        column.type() == this.type(),
        "Column '%s' has type %s, but column '%s' has type %s.",
        name(),
        type(),
        column.name(),
        column.type());
    final ShortColumn numberColumn = (ShortColumn) column;
    final int size = numberColumn.size();
    for (int i = 0; i < size; i++) {
      append(numberColumn.getShort(i));
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public String getString(final int row) {
    final short value = getShort(row);
    return String.valueOf(getPrintFormatter().format(value));
  }

  /** {@inheritDoc} */
  @Override
  public ShortColumn append(Column<Short> column, int row) {
    Preconditions.checkArgument(
        column.type() == this.type(),
        "Column '%s' has type %s, but column '%s' has type %s.",
        name(),
        type(),
        column.name(),
        column.type());
    return append(((ShortColumn) column).getShort(row));
  }

  /** {@inheritDoc} */
  @Override
  public ShortColumn set(int row, Column<Short> column, int sourceRow) {
    Preconditions.checkArgument(
        column.type() == this.type(),
        "Column '%s' has type %s, but column '%s' has type %s.",
        name(),
        type(),
        column.name(),
        column.type());
    return set(row, ((ShortColumn) column).getShort(sourceRow));
  }

  /** {@inheritDoc} */
  @Override
  public ShortColumn appendMissing() {
    return append(ShortColumnType.missingValueIndicator());
  }

  /** {@inheritDoc} */
  @Override
  public byte[] asBytes(int rowNumber) {
    return ByteBuffer.allocate(ShortColumnType.instance().byteSize())
        .putShort(getShort(rowNumber))
        .array();
  }

  /** {@inheritDoc} */
  @Override
  public int countUnique() {
    ShortSet uniqueElements = new ShortOpenHashSet();
    for (int i = 0; i < size(); i++) {
      uniqueElements.add(getShort(i));
    }
    return uniqueElements.size();
  }

  /**
   * Returns the value at the given index. The actual value is returned if the ColumnType is INTEGER
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
  public int getInt(int row) {
    return data.getShort(row);
  }

  /** {@inheritDoc} */
  @Override
  public double getDouble(int row) {
    short value = data.getShort(row);
    if (isMissingValue(value)) {
      return DoubleColumnType.missingValueIndicator();
    }
    return value;
  }

  public boolean isMissingValue(short value) {
    return ShortColumnType.valueIsMissing(value);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMissing(int rowNumber) {
    return isMissingValue(getShort(rowNumber));
  }

  /** {@inheritDoc} */
  @Override
  public ShortColumn setMissing(int i) {
    return set(i, ShortColumnType.missingValueIndicator());
  }

  /** {@inheritDoc} */
  @Override
  public void sortAscending() {
    data.sort(ShortComparators.NATURAL_COMPARATOR);
  }

  /** {@inheritDoc} */
  @Override
  public void sortDescending() {
    data.sort(ShortComparators.OPPOSITE_COMPARATOR);
  }

  /** {@inheritDoc} */
  @Override
  public ShortColumn appendObj(Object obj) {
    if (obj == null) {
      return appendMissing();
    }
    if (obj instanceof Short) {
      return append((short) obj);
    }
    throw new IllegalArgumentException("Could not append " + obj.getClass());
  }

  /** {@inheritDoc} */
  @Override
  public ShortColumn appendCell(final String value) {
    try {
      return append(parser().parseShort(value));
    } catch (final NumberFormatException e) {
      throw new NumberFormatException(
          "Error adding value to column " + name() + ": " + e.getMessage());
    }
  }

  /** {@inheritDoc} */
  @Override
  public ShortColumn appendCell(final String value, AbstractColumnParser<?> parser) {
    try {
      return append(parser.parseShort(value));
    } catch (final NumberFormatException e) {
      throw new NumberFormatException(
          "Error adding value to column " + name() + ": " + e.getMessage());
    }
  }

  /** {@inheritDoc} */
  @Override
  public String getUnformattedString(final int row) {
    final int value = getInt(row);
    if (ShortColumnType.valueIsMissing(value)) {
      return "";
    }
    return String.valueOf(value);
  }

  /**
   * Returns a new LongColumn containing a value for each value in this column
   *
   * <p>A widening primitive conversion from short to long does not lose any information at all; the
   * numeric value is preserved exactly.
   *
   * <p>A missing value in the receiver is converted to a missing value in the result
   */
  @Override
  public LongColumn asLongColumn() {
    LongColumn result = LongColumn.create(name());
    for (short d : data) {
      if (ShortColumnType.valueIsMissing(d)) {
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
    for (short d : data) {
      if (ShortColumnType.valueIsMissing(d)) {
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
    for (short d : data) {
      if (ShortColumnType.valueIsMissing(d)) {
        result.appendMissing();
      } else {
        result.append(d);
      }
    }
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public IntColumn asIntColumn() {
    IntColumn result = IntColumn.create(name());
    for (short d : data) {
      if (ShortColumnType.valueIsMissing(d)) {
        result.appendMissing();
      } else {
        result.append(d);
      }
    }
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public Set<Short> asSet() {
    return new HashSet<>(unique().asList());
  }
}
