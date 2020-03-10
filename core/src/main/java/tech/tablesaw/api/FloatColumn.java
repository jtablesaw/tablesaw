package tech.tablesaw.api;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatComparators;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
import it.unimi.dsi.fastutil.floats.FloatOpenHashSet;
import it.unimi.dsi.fastutil.floats.FloatSet;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.stream.Stream;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.FloatColumnType;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;

public class FloatColumn extends NumberColumn<FloatColumn, Float> {

  private final FloatArrayList data;

  private FloatColumn(String name, FloatArrayList data) {
    super(FloatColumnType.instance(), name);
    setPrintFormatter(NumberColumnFormatter.floatingPointDefault());
    this.data = data;
  }

  @Override
  public String getString(int row) {
    final float value = getFloat(row);
    if (FloatColumnType.valueIsMissing(value)) {
      return "";
    }
    return String.valueOf(getPrintFormatter().format(value));
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

  @Override
  public FloatColumn createCol(String name, int initialSize) {
    return create(name, initialSize);
  }

  @Override
  public FloatColumn createCol(String name) {
    return create(name);
  }

  @Override
  public Float get(int index) {
    float result = getFloat(index);
    return isMissingValue(result) ? null : result;
  }

  public static boolean valueIsMissing(float value) {
    return FloatColumnType.valueIsMissing(value);
  }

  @Override
  public FloatColumn subset(int[] rows) {
    final FloatColumn c = this.emptyCopy();
    for (final int row : rows) {
      c.append(getFloat(row));
    }
    return c;
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

  @Override
  public FloatColumn lag(int n) {
    final int srcPos = n >= 0 ? 0 : 0 - n;
    final float[] dest = new float[size()];
    final int destPos = n <= 0 ? 0 : n;
    final int length = n >= 0 ? size() - n : size() + n;

    for (int i = 0; i < size(); i++) {
      dest[i] = FloatColumnType.missingValueIndicator();
    }

    float[] array = data.toFloatArray();

    System.arraycopy(array, srcPos, dest, destPos, length);
    return new FloatColumn(name() + " lag(" + n + ")", new FloatArrayList(dest));
  }

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

  public FloatColumn append(Float val) {
    if (val == null) {
      appendMissing();
    } else {
      append(val.floatValue());
    }
    return this;
  }

  @Override
  public FloatColumn copy() {
    return new FloatColumn(name(), data.clone());
  }

  @Override
  public Iterator<Float> iterator() {
    return data.iterator();
  }

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

  @Override
  public int compare(Float o1, Float o2) {
    return Float.compare(o1, o2);
  }

  @Override
  public FloatColumn set(int i, Float val) {
    return set(i, (float) val);
  }

  public FloatColumn set(int i, float val) {
    data.set(i, val);
    return this;
  }

  @Override
  public Column<Float> set(int row, String stringValue, AbstractColumnParser<?> parser) {
    return set(row, parser.parseFloat(stringValue));
  }

  @Override
  public FloatColumn append(final Column<Float> column) {
    Preconditions.checkArgument(column.type() == this.type());
    final FloatColumn numberColumn = (FloatColumn) column;
    final int size = numberColumn.size();
    for (int i = 0; i < size; i++) {
      append(numberColumn.getFloat(i));
    }
    return this;
  }

  @Override
  public FloatColumn append(Column<Float> column, int row) {
    Preconditions.checkArgument(column.type() == this.type());
    return append(((FloatColumn) column).getFloat(row));
  }

  @Override
  public FloatColumn set(int row, Column<Float> column, int sourceRow) {
    Preconditions.checkArgument(column.type() == this.type());
    return set(row, ((FloatColumn) column).getFloat(sourceRow));
  }

  @Override
  public byte[] asBytes(int rowNumber) {
    return ByteBuffer.allocate(FloatColumnType.instance().byteSize())
        .putFloat(getFloat(rowNumber))
        .array();
  }

  @Override
  public int countUnique() {
    FloatSet uniqueElements = new FloatOpenHashSet();
    for (int i = 0; i < size(); i++) {
      uniqueElements.add(getFloat(i));
    }
    return uniqueElements.size();
  }

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

  @Override
  public boolean isMissing(int rowNumber) {
    return isMissingValue(getFloat(rowNumber));
  }

  @Override
  public Column<Float> setMissing(int i) {
    return set(i, FloatColumnType.missingValueIndicator());
  }

  @Override
  public void sortAscending() {
    data.sort(FloatComparators.NATURAL_COMPARATOR);
  }

  @Override
  public void sortDescending() {
    data.sort(FloatComparators.OPPOSITE_COMPARATOR);
  }

  @Override
  public FloatColumn appendMissing() {
    return append(FloatColumnType.missingValueIndicator());
  }

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

  @Override
  public FloatColumn appendCell(final String value) {
    try {
      return append(FloatColumnType.DEFAULT_PARSER.parseFloat(value));
    } catch (final NumberFormatException e) {
      throw new NumberFormatException(
          "Error adding value to column " + name() + ": " + e.getMessage());
    }
  }

  @Override
  public FloatColumn appendCell(final String value, AbstractColumnParser<?> parser) {
    try {
      return append(parser.parseFloat(value));
    } catch (final NumberFormatException e) {
      throw new NumberFormatException(
          "Error adding value to column " + name() + ": " + e.getMessage());
    }
  }

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
}
