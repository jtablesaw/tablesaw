package tech.tablesaw.api;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
import it.unimi.dsi.fastutil.floats.FloatOpenHashSet;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.numbers.FloatColumnType;
import tech.tablesaw.selection.Selection;

import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.DoublePredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class FloatColumn extends NumberColumn<Float> {

    /**
     * Compares two doubles, such that a sort based on this comparator would sort in descending order
     */
    private final FloatComparator descendingComparator = (o2, o1) -> (Float.compare(o1, o2));

    private final FloatArrayList data;    

    private FloatColumn(final String name, FloatArrayList data) {
        super(FloatColumnType.instance(), name);
        this.data = data;
    }

    @Override
    public String getString(final int row) {
        final float value = getFloat(row);
        if (FloatColumnType.isMissingValue(value)) {
            return "";
        }
        return String.valueOf(printFormatter.format(value));
    }


    public static FloatColumn create(final String name) {
        return new FloatColumn(name, new FloatArrayList());
    }

    public static FloatColumn create(final String name, final float[] arr) {
        return new FloatColumn(name, new FloatArrayList(arr));
    }

    public static FloatColumn create(final String name, final int initialSize) {
        FloatColumn column = new FloatColumn(name, new FloatArrayList(initialSize));
        for (int i = 0; i < initialSize; i++) {
            column.appendMissing();
        }
        return column;
    }

    @Override
    public FloatColumn createCol(final String name, final int initialSize) {
        return create(name, initialSize);
    }

    @Override
    public FloatColumn createCol(final String name) {
        return create(name);
    }

    @Override
    public Float get(int index) {
        return data.getFloat(index);
    }

    @Override
    public FloatColumn subset(final int[] rows) {
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
            if (!isMissing(i)) {
                values.add(getFloat(i));
            }
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
        FloatArrays.parallelQuickSort(values, descendingComparator);
        for (int i = 0; i < n && i < values.length; i++) {
            top.add(values[i]);
        }
        return new FloatColumn(name() + "[Top " + n  + "]", top);
    }

    @Override
    public FloatColumn bottom(final int n) {
        FloatArrayList bottom = new FloatArrayList();
        float[] values = data.toFloatArray();
        FloatArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(values[i]);
        }
        return new FloatColumn(name() + "[Bottoms " + n  + "]", bottom);
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
        this.append(val.floatValue());
        return this;
    }

    @Override
    public FloatColumn emptyCopy() {
        return (FloatColumn) super.emptyCopy();
    }

    @Override
    public FloatColumn emptyCopy(final int rowSize) {
        return (FloatColumn) super.emptyCopy(rowSize);
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
            output[i] = getFloat(i);
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
        return ByteBuffer.allocate(FloatColumnType.instance().byteSize()).putFloat(getFloat(rowNumber)).array();
    }

    @Override
    public int countUnique() {
        FloatSet uniqueElements = new FloatOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissing(i)) {
                uniqueElements.add(getFloat(i));
            }
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
     * Returns a float representation of the data at the given index. Some precision may be lost, and if the value is
     * to large to be cast to a float, an exception is thrown.
     *
     * @throws  ClassCastException if the value can't be cast to ta float
     */
    public float getFloat(int row) {
        return data.getFloat(row);
    }

    public boolean isMissingValue(float value) {
        return FloatColumnType.isMissingValue(value);
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
        FloatArrays.parallelQuickSort(data.elements());
    }

    @Override
    public void sortDescending() {
        FloatArrays.parallelQuickSort(data.elements(), descendingComparator);
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
            throw new NumberFormatException("Error adding value to column " + name() + ": " + e.getMessage());
        }
    }

    @Override
    public FloatColumn appendCell(final String value, AbstractColumnParser<?> parser) {
        try {
            return append(parser.parseFloat(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Error adding value to column " + name()  + ": " + e.getMessage());
        }
    }    

    @Override
    public String getUnformattedString(final int row) {
        final float value = getFloat(row);
        if (FloatColumnType.isMissingValue(value)) {
            return "";
        }
        return String.valueOf(value);
    }

    @Override
    public FloatColumn inRange(int start, int end) {
        return (FloatColumn) super.inRange(start, end);
    }

    @Override
    public FloatColumn where(Selection selection) {
        return (FloatColumn) super.where(selection);
    }

    @Override
    public FloatColumn lead(int n) {
        return (FloatColumn) super.lead(n);
    }

    @Override
    public FloatColumn setName(String name) {
        return (FloatColumn) super.setName(name);
    }

    @Override
    public FloatColumn filter(Predicate<? super Float> test) {
        return (FloatColumn) super.filter(test);
    }

    @Override
    public FloatColumn sorted(Comparator<? super Float> comp) {
        return (FloatColumn) super.sorted(comp);
    }

    @Override
    public FloatColumn map(Function<? super Float, ? extends Float> fun) {
        return (FloatColumn) super.map(fun);
    }

    @Override
    public FloatColumn min(Column<Float> other) {
        return (FloatColumn) super.min(other);
    }

    @Override
    public FloatColumn max(Column<Float> other) {
        return (FloatColumn) super.max(other);
    }

    @Override
    public FloatColumn set(Selection condition, Column<Float> other) {
        return (FloatColumn) super.set(condition, other);
    }

    @Override
    public FloatColumn set(Selection rowSelection, Float newValue) {
        return (FloatColumn) super.set(rowSelection, newValue);
    }

    @Override
    public FloatColumn set(DoublePredicate condition, Float newValue) {
        return (FloatColumn) super.set(condition, newValue);
    }

    @Override
    public FloatColumn set(DoublePredicate condition, NumberColumn<Float> other) {
      return (FloatColumn) super.set(condition, other);
    }

    @Override
    public FloatColumn first(int numRows) {
        return (FloatColumn) super.first(numRows);
    }

    @Override
    public FloatColumn last(int numRows) {
        return (FloatColumn) super.last(numRows);
    }

    @Override
    public FloatColumn sampleN(int n) {
        return (FloatColumn) super.sampleN(n);
    }

    @Override
    public FloatColumn sampleX(double proportion) {
        return (FloatColumn) super.sampleX(proportion);
    }

    /**
     * Returns a new LongColumn containing a value for each value in this column, truncating if necessary
     *
     * A narrowing primitive conversion such as this one may lose information about the overall magnitude of a
     * numeric value and may also lose precision and range. Specifically, if the value is too small (a negative value
     * of large magnitude or negative infinity), the result is the smallest representable value of type long.
     *
     * Similarly, if the value is too large (a positive value of large magnitude or positive infinity), the result is the
     * largest representable value of type long.
     *
     * Despite the fact that overflow, underflow, or other loss of information may occur, a narrowing primitive
     * conversion never results in a run-time exception.
     *
     * A missing value in the receiver is converted to a missing value in the result
     */
    @Override
    public LongColumn asLongColumn() {
        LongArrayList values = new LongArrayList();
        for (float f : data) {
            values.add((long) f);
        }
        values.trim();
        return LongColumn.create(this.name(), values.elements());
    }

    /**
     * Returns a new IntColumn containing a value for each value in this column, truncating if necessary.
     *
     * A narrowing primitive conversion such as this one may lose information about the overall magnitude of a
     * numeric value and may also lose precision and range. Specifically, if the value is too small (a negative value
     * of large magnitude or negative infinity), the result is the smallest representable value of type int.
     *
     * Similarly, if the value is too large (a positive value of large magnitude or positive infinity), the result is the
     * largest representable value of type int.
     *
     * Despite the fact that overflow, underflow, or other loss of information may occur, a narrowing primitive
     * conversion never results in a run-time exception.
     *
     * A missing value in the receiver is converted to a missing value in the result
     */
    @Override
    public IntColumn asIntColumn() {
        IntArrayList values = new IntArrayList();
        for (float d : data) {
            values.add((int) d);
        }
        values.trim();
        return IntColumn.create(this.name(), values.elements());
    }

    /**
     * Returns a new IntColumn containing a value for each value in this column, truncating if necessary.
     *
     * A narrowing primitive conversion such as this one may lose information about the overall magnitude of a
     * numeric value and may also lose precision and range. Specifically, if the value is too small (a negative value
     * of large magnitude or negative infinity), the result is the smallest representable value of type int.
     *
     * Similarly, if the value is too large (a positive value of large magnitude or positive infinity), the result is the
     * largest representable value of type int.
     *
     * Despite the fact that overflow, underflow, or other loss of information may occur, a narrowing primitive
     * conversion never results in a run-time exception.
     *
     * A missing value in the receiver is converted to a missing value in the result
     */
    @Override
    public ShortColumn asShortColumn() {
        ShortArrayList values = new ShortArrayList();
        for (float d : data) {
            values.add((short) d);
        }
        values.trim();
        return ShortColumn.create(this.name(), values.elements());
    }

    /**
     * Returns a new DoubleColumn containing a value for each value in this column.
     *
     * No information is lost in converting from the floats to doubles
     *
     * A missing value in the receiver is converted to a missing value in the result
     */
    @Override
    public DoubleColumn asDoubleColumn() {
        DoubleArrayList values = new DoubleArrayList();
        for (float d : data) {
            values.add(d);
        }
        values.trim();
        return DoubleColumn.create(this.name(), values.elements());
    }
}
