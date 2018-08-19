package tech.tablesaw.api;

import java.nio.ByteBuffer;
import java.util.Iterator;

import com.google.common.base.Preconditions;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
import it.unimi.dsi.fastutil.floats.FloatOpenHashSet;
import it.unimi.dsi.fastutil.floats.FloatSet;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.FloatColumnType;
import tech.tablesaw.columns.numbers.NumberIterator;

public class FloatColumn extends NumberColumn<Float> implements NumericColumn<Float> {

    private static final FloatColumnType COLUMN_TYPE = ColumnType.FLOAT;

    /**
     * Compares two doubles, such that a sort based on this comparator would sort in descending order
     */
    private final FloatComparator descendingComparator = (o2, o1) -> (Float.compare(o1, o2));

    private final FloatArrayList data;    

    protected FloatColumn(final String name, FloatArrayList data) {
        super(COLUMN_TYPE, name, data);
        this.data = data;
    }

    public static FloatColumn create(final String name) {
        return new FloatColumn(name, new FloatArrayList());
    }

    public static FloatColumn create(final String name, final float[] arr) {
        return new FloatColumn(name, new FloatArrayList(arr));
    }

    public static FloatColumn create(final String name, final int initialSize) {
        return new FloatColumn(name, new FloatArrayList(initialSize));
    }

    @Override
    public FloatColumn createCol(final String name, final int initialSize) {
        return create(name, initialSize);
    }

    @Override
    public Float get(int index) {
        return data.getFloat(index);
    }

    @Override
    public FloatColumn unique() {
        final FloatSet values = new FloatOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissing(i)) {
                values.add(getFloat(i));
            }
        }
        final FloatColumn column = FloatColumn.create(name() + " Unique values", values.size());
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
        this.append(val.intValue());
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
    public Object[] asObjectArray() {
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
        for (int i = 0; i < numberColumn.size(); i++) {
            append(numberColumn.getFloat(i));
        }
        return this;
    }

    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(COLUMN_TYPE.byteSize()).putFloat(getFloat(rowNumber)).array();
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
            return DoubleColumnType.missingValueIndicator();
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
    public NumberIterator numberIterator() {
        return new NumberIterator(data);
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
    public FloatColumn appendCell(final String value, StringParser<?> parser) {
        try {
            return append(parser.parseFloat(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Error adding value to column " + name()  + ": " + e.getMessage());
        }
    }    

}
