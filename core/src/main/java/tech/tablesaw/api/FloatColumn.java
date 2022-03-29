package tech.tablesaw.api;

import static com.google.common.base.Preconditions.checkArgument;

import it.unimi.dsi.fastutil.floats.*;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.FloatColumnType;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/** A column that contains float values */
public class FloatColumn extends NumberColumn<FloatColumn, Float> {

  protected final FloatArrayList data;

  private FloatColumn(String name, FloatArrayList data) {
    super(FloatColumnType.instance(), name, FloatColumnType.DEFAULT_PARSER);
    setPrintFormatter(NumberColumnFormatter.floatingPointDefault());
    this.data = data;
  }

  /** {@inheritDoc} */
  @Override
  public String getString(int row) {
    final float value = getFloat(row);
    return getPrintFormatter().format(value);
  }

  /** {@inheritDoc} */
  @Override
  public int valueHash(int rowNumber) {
    return Float.hashCode(getFloat(rowNumber));
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(int rowNumber1, int rowNumber2) {
    return getFloat(rowNumber1) == getFloat(rowNumber2);
  }

  public static FloatColumn create(String name) {
    return new FloatColumn(name, new FloatArrayList());
  }

  public static FloatColumn create(String name, float... arr) {
    return new FloatColumn(name, new FloatArrayList(arr));
  }

  public static FloatColumn create(String name, int initialSize) {
    FloatColumn column = new FloatColumn(name, new FloatArrayList(initialSize));
    for (int i = 0; i < initialSize; i++) {
      column.appendMissing();
    }
    return column;
  }

  public static FloatColumn create(String name, Float[] arr) {
    FloatColumn column = create(name);
    for (Float val : arr) {
      column.append(val);
    }
    return column;
  }

  public static FloatColumn create(String name, Stream<Float> stream) {
    FloatColumn column = create(name);
    stream.forEach(column::append);
    return column;
  }

  /** {@inheritDoc} */
  @Override
  public FloatColumn createCol(String name, int initialSize) {
    return create(name, initialSize);
  }

  /** {@inheritDoc} */
  @Override
  public FloatColumn createCol(String name) {
    return create(name);
  }

  /** {@inheritDoc} */
  @Override
  public Float get(int index) {
    float result = getFloat(index);
    return isMissingValue(result) ? null : result;
  }

  public static boolean valueIsMissing(float value) {
    return FloatColumnType.valueIsMissing(value);
  }

  /** {@inheritDoc} */
  @Override
  public FloatColumn subset(int[] rows) {
    final FloatColumn c = this.emptyCopy();
    for (final int row : rows) {
      c.append(getFloat(row));
    }
    return c;
  }

  public Selection isNotIn(final float... numbers) {
    final Selection results = new BitmapBackedSelection();
    results.addRange(0, size());
    results.andNot(isIn(numbers));
    return results;
  }

  public Selection isIn(final float... numbers) {
    final Selection results = new BitmapBackedSelection();
    final FloatRBTreeSet doubleSet = new FloatRBTreeSet(numbers);
    for (int i = 0; i < size(); i++) {
      if (doubleSet.contains(getFloat(i))) {
        results.add(i);
      }
    }
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
  public FloatColumn unique() {
    final FloatSet values = new FloatOpenHashSet();
    for (int i = 0; i < size(); i++) {
      values.add(getFloat(i));
    }
    final FloatColumn column = FloatColumn.create(name() + " Unique values");
    for (float value : values) {
      column.append(value);
    }
    return column;
  }

  /** {@inheritDoc} */
  @Override
  public FloatColumn top(int n) {
    FloatArrayList top = new FloatArrayList();
    float[] values = data.toFloatArray();
    FloatArrays.parallelQuickSort(values, FloatComparators.OPPOSITE_COMPARATOR);
    for (int i = 0; i < n && i < values.length; i++) {
      top.add(values[i]);
    }
    return new FloatColumn(name() + "[Top " + n + "]", top);
  }

  /** {@inheritDoc} */
  @Override
  public FloatColumn bottom(final int n) {
    FloatArrayList bottom = new FloatArrayList();
    float[] values = data.toFloatArray();
    FloatArrays.parallelQuickSort(values);
    for (int i = 0; i < n && i < values.length; i++) {
      bottom.add(values[i]);
    }
    return new FloatColumn(name() + "[Bottoms " + n + "]", bottom);
  }

  /** {@inheritDoc} */
  @Override
  public FloatColumn lag(int n) {
    final int srcPos = n >= 0 ? 0 : -n;
    final float[] dest = new float[size()];
    final int destPos = Math.max(n, 0);
    final int length = n >= 0 ? size() - n : size() + n;

    for (int i = 0; i < size(); i++) {
      dest[i] = FloatColumnType.missingValueIndicator();
    }

    float[] array = data.toFloatArray();

    System.arraycopy(array, srcPos, dest, destPos, length);
    return new FloatColumn(name() + " lag(" + n + ")", new FloatArrayList(dest));
  }

  /** {@inheritDoc} */
  @Override
  public FloatColumn removeMissing() {
    FloatColumn result = copy();
    result.clear();
    FloatListIterator iterator = data.iterator();
    while (iterator.hasNext()) {
      final float v = iterator.nextFloat();
      if (!isMissingValue(v)) {
        result.append(v);
      }
    }
    return result;
  }

  public FloatColumn append(float i) {
    data.add(i);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public FloatColumn append(Float val) {
    if (val == null) {
      appendMissing();
    } else {
      append(val.floatValue());
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public FloatColumn copy() {
    FloatColumn copy = new FloatColumn(name(), data.clone());
    copy.setPrintFormatter(getPrintFormatter());
    copy.locale = locale;
    return copy;
  }

  /** {@inheritDoc} */
  @Override
  public Iterator<Float> iterator() {
    return data.iterator();
  }

  public float[] asFloatArray() {
    return data.toFloatArray();
  }

  /** {@inheritDoc} */
  @Override
  public Float[] asObjectArray() {
    final Float[] output = new Float[size()];
    for (int i = 0; i < size(); i++) {
      if (!isMissing(i)) {
        output[i] = getFloat(i);
      } else {
        output[i] = null;
      }
    }
    return output;
  }

  /** {@inheritDoc} */
  @Override
  public int compare(Float o1, Float o2) {
    return Float.compare(o1, o2);
  }

  /** {@inheritDoc} */
  @Override
  public FloatColumn set(int i, Float val) {
    return val == null ? setMissing(i) : set(i, (float) val);
  }

  public FloatColumn set(int i, float val) {
    data.set(i, val);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public Column<Float> set(int row, String stringValue, AbstractColumnParser<?> parser) {
    return set(row, parser.parseFloat(stringValue));
  }

  /** {@inheritDoc} */
  @Override
  public FloatColumn append(final Column<Float> column) {
    checkArgument(
        column.type() == this.type(),
        "Column '%s' has type %s, but column '%s' has type %s.",
        name(),
        type(),
        column.name(),
        column.type());
    final FloatColumn numberColumn = (FloatColumn) column;
    final int size = numberColumn.size();
    for (int i = 0; i < size; i++) {
      append(numberColumn.getFloat(i));
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public FloatColumn append(Column<Float> column, int row) {
    checkArgument(
        column.type() == this.type(),
        "Column '%s' has type %s, but column '%s' has type %s.",
        name(),
        type(),
        column.name(),
        column.type());
    return append(((FloatColumn) column).getFloat(row));
  }

  /** {@inheritDoc} */
  @Override
  public FloatColumn set(int row, Column<Float> column, int sourceRow) {
    checkArgument(
        column.type() == this.type(),
        "Column '%s' has type %s, but column '%s' has type %s.",
        name(),
        type(),
        column.name(),
        column.type());
    return set(row, ((FloatColumn) column).getFloat(sourceRow));
  }

  /** {@inheritDoc} */
  @Override
  public byte[] asBytes(int rowNumber) {
    return ByteBuffer.allocate(FloatColumnType.instance().byteSize())
        .putFloat(getFloat(rowNumber))
        .array();
  }

  /** {@inheritDoc} */
  @Override
  public int countUnique() {
    FloatSet uniqueElements = new FloatOpenHashSet();
    for (int i = 0; i < size(); i++) {
      uniqueElements.add(getFloat(i));
    }
    return uniqueElements.size();
  }

  /** {@inheritDoc} */
  @Override
  public double getDouble(int row) {
    float value = data.getFloat(row);
    if (isMissingValue(value)) {
      return FloatColumnType.missingValueIndicator();
    }
    return value;
  }

  /**
   * Returns a float representation of the data at the given index. Some precision may be lost, and
   * if the value is to large to be cast to a float, an exception is thrown.
   *
   * @throws ClassCastException if the value can't be cast to ta float
   */
  public float getFloat(int row) {
    return data.getFloat(row);
  }

  public boolean isMissingValue(float value) {
    return FloatColumnType.valueIsMissing(value);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMissing(int rowNumber) {
    return isMissingValue(getFloat(rowNumber));
  }

  /** {@inheritDoc} */
  @Override
  public FloatColumn setMissing(int i) {
    return set(i, FloatColumnType.missingValueIndicator());
  }

  /** {@inheritDoc} */
  @Override
  public void sortAscending() {
    data.sort(FloatComparators.NATURAL_COMPARATOR);
  }

  /** {@inheritDoc} */
  @Override
  public void sortDescending() {
    data.sort(FloatComparators.OPPOSITE_COMPARATOR);
  }

  /** {@inheritDoc} */
  @Override
  public FloatColumn appendMissing() {
    return append(FloatColumnType.missingValueIndicator());
  }

  /** {@inheritDoc} */
  @Override
  public FloatColumn appendObj(Object obj) {
    if (obj == null) {
      return appendMissing();
    }
    if (obj instanceof Float) {
      return append((float) obj);
    }
    throw new IllegalArgumentException("Could not append " + obj.getClass());
  }

  /** {@inheritDoc} */
  @Override
  public FloatColumn appendCell(final String value) {
    try {
      return append(parser().parseFloat(value));
    } catch (final NumberFormatException e) {
      throw new NumberFormatException(
          "Error adding value to column " + name() + ": " + e.getMessage());
    }
  }

  /** {@inheritDoc} */
  @Override
  public FloatColumn appendCell(final String value, AbstractColumnParser<?> parser) {
    try {
      return append(parser.parseFloat(value));
    } catch (final NumberFormatException e) {
      throw new NumberFormatException(
          "Error adding value to column " + name() + ": " + e.getMessage());
    }
  }

  /** {@inheritDoc} */
  @Override
  public String getUnformattedString(final int row) {
    final float value = getFloat(row);
    if (FloatColumnType.valueIsMissing(value)) {
      return "";
    }
    return String.valueOf(value);
  }

  /**
   * Returns a new LongColumn containing a value for each value in this column, truncating if
   * necessary
   *
   * <p>A narrowing primitive conversion such as this one may lose information about the overall
   * magnitude of a numeric value and may also lose precision and range. Specifically, if the value
   * is too small (a negative value of large magnitude or negative infinity), the result is the
   * smallest representable value of type long.
   *
   * <p>Similarly, if the value is too large (a positive value of large magnitude or positive
   * infinity), the result is the largest representable value of type long.
   *
   * <p>Despite the fact that overflow, underflow, or other loss of information may occur, a
   * narrowing primitive conversion never results in a run-time exception.
   *
   * <p>A missing value in the receiver is converted to a missing value in the result
   */
  @Override
  public LongColumn asLongColumn() {
    LongColumn result = LongColumn.create(name());
    for (float d : data) {
      if (FloatColumnType.valueIsMissing(d)) {
        result.appendMissing();
      } else {
        result.append((long) d);
      }
    }
    return result;
  }

  /**
   * Returns a new IntColumn containing a value for each value in this column, truncating if
   * necessary.
   *
   * <p>A narrowing primitive conversion such as this one may lose information about the overall
   * magnitude of a numeric value and may also lose precision and range. Specifically, if the value
   * is too small (a negative value of large magnitude or negative infinity), the result is the
   * smallest representable value of type int.
   *
   * <p>Similarly, if the value is too large (a positive value of large magnitude or positive
   * infinity), the result is the largest representable value of type int.
   *
   * <p>Despite the fact that overflow, underflow, or other loss of information may occur, a
   * narrowing primitive conversion never results in a run-time exception.
   *
   * <p>A missing value in the receiver is converted to a missing value in the result
   */
  @Override
  public IntColumn asIntColumn() {
    IntColumn result = IntColumn.create(name());
    for (float d : data) {
      if (FloatColumnType.valueIsMissing(d)) {
        result.appendMissing();
      } else {
        result.append((int) d);
      }
    }
    return result;
  }

  /**
   * Returns a new IntColumn containing a value for each value in this column, truncating if
   * necessary.
   *
   * <p>A narrowing primitive conversion such as this one may lose information about the overall
   * magnitude of a numeric value and may also lose precision and range. Specifically, if the value
   * is too small (a negative value of large magnitude or negative infinity), the result is the
   * smallest representable value of type int.
   *
   * <p>Similarly, if the value is too large (a positive value of large magnitude or positive
   * infinity), the result is the largest representable value of type int.
   *
   * <p>Despite the fact that overflow, underflow, or other loss of information may occur, a
   * narrowing primitive conversion never results in a run-time exception.
   *
   * <p>A missing value in the receiver is converted to a missing value in the result
   */
  @Override
  public ShortColumn asShortColumn() {
    ShortColumn result = ShortColumn.create(name());
    for (float d : data) {
      if (FloatColumnType.valueIsMissing(d)) {
        result.appendMissing();
      } else {
        result.append((short) d);
      }
    }
    return result;
  }

  /**
   * Returns a new DoubleColumn containing a value for each value in this column.
   *
   * <p>No information is lost in converting from the floats to doubles
   *
   * <p>A missing value in the receiver is converted to a missing value in the result
   */
  @Override
  public DoubleColumn asDoubleColumn() {
    DoubleColumn result = DoubleColumn.create(name());
    for (float d : data) {
      if (FloatColumnType.valueIsMissing(d)) {
        result.appendMissing();
      } else {
        result.append(d);
      }
    }
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public Set<Float> asSet() {
    return new HashSet<>(unique().asList());
  }
}
