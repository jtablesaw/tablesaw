package tech.tablesaw.columns.numbers;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatOpenHashSet;
import it.unimi.dsi.fastutil.floats.FloatSet;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.StringParser;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;

public class FloatDataWrapper implements NumericDataWrapper {

    private FloatColumnType COLUMN_TYPE = ColumnType.FLOAT;
    /**
     * Compares two doubles, such that a sort based on this comparator would sort in descending order
     */
    private final FloatComparator descendingComparator = (o2, o1) -> (Float.compare(o1, o2));

    private final FloatArrayList data;

    public FloatDataWrapper(FloatArrayList data) {
        this.data = data;
    }

    @Override
    public NumberIterator numberIterator() {
        return new NumberIterator(data);
    }

    @Override
    public Iterator<Float> iterator() {
        return data.iterator();
    }

    @Override
    public NumericDataWrapper top(int n) {
        final FloatArrayList top = new FloatArrayList();
        final float[] values = data.toFloatArray();
        FloatArrays.parallelQuickSort(values, descendingComparator);
        for (int i = 0; i < n && i < values.length; i++) {
            top.add(values[i]);
        }
        return new FloatDataWrapper(top);
    }

    @Override
    public NumericDataWrapper bottom(int n) {

        final FloatArrayList bottom = new FloatArrayList();
        final float[] values = data.toFloatArray();
        FloatArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(values[i]);
        }
        return new FloatDataWrapper(bottom);
    }

    @Override
    public int size() {
        return data.size();
    }

    public void append(final float f) {
        data.add(f);
    }

    @Override
    public void append(double d) {
        data.add((float) d);
    }

    @Override
    public void append(int i) {
        data.add(i);
    }

    @Override
    public void appendCell(String value) {
        append(FloatColumnType.DEFAULT_PARSER.parseFloat(value));
    }

    @Override
    public void appendCell(String value, StringParser<?> parser) {
        append(parser.parseFloat(value));
    }

    @Override
    public int countMissing() {
        int count = 0;
        for (int i = 0; i < size(); i++) {
            if (isMissingValue(getFloat(i))) {
                count++;
            }
        }
        return count;
    }

    @Override
    public double getDouble(int row) {
        return data.getFloat(row);
    }

    @Override
    public FloatDataWrapper copy() {
        return new FloatDataWrapper(data.clone());
    }

    @Override
    public FloatDataWrapper emptyCopy() {
        return new FloatDataWrapper(new FloatArrayList());
    }

    @Override
    public FloatDataWrapper emptyCopy(int size) {
        return new FloatDataWrapper(new FloatArrayList(size));
    }

    @Override
    public void clear() {
        this.data.clear();
    }

    @Override
    public void sortAscending() {
        Arrays.parallelSort(data.elements());
    }

    @Override
    public void sortDescending() {
        FloatArrays.parallelQuickSort(data.elements(), descendingComparator);
    }

    @Override
    public void set(int r, double value) {
        this.data.set(r, (float) value);
    }

    @Override
    public boolean contains(double value) {
        return data.contains((float) value);
    }

    @Override
    public boolean contains(int value) {
        return data.contains(value);
    }

    @Override
    public NumericDataWrapper lag(int n) {
        final int srcPos = n >= 0 ? 0 : 0 - n;
        final float[] dest = new float[size()];
        final int destPos = n <= 0 ? 0 : n;
        final int length = n >= 0 ? size() - n : size() + n;

        for (int i = 0; i < size(); i++) {
            dest[i] = FloatColumnType.missingValueIndicator();
        }

        float[] array = data.toFloatArray();

        System.arraycopy(array, srcPos, dest, destPos, length);
        return new FloatDataWrapper(new FloatArrayList(dest));
    }

    @Override
    public NumericDataWrapper lead(int n) {
        return lag(-n);
    }

    @Override
    public int countUnique() {
        FloatSet floats = new FloatOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissingValue(getFloat(i))) {
                floats.add(getFloat(i));
            }
        }
        return floats.size();
    }

    @Override
    public void appendMissing() {
        data.add(FloatColumnType.missingValueIndicator());
    }

    public float getFloat(int index) {
        return data.getFloat(index);
    }

    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(COLUMN_TYPE.byteSize()).putFloat(getFloat(rowNumber)).array();
    }

    @Override
    public ColumnType type() {
        return ColumnType.FLOAT;
    }

    /**
     * Returns the closest {@code int} to the argument, with ties
     * rounding to positive infinity.
     *
     * <p>
     * Special cases:
     * <ul><li>If the argument is NaN, the result is 0.
     * <li>If the argument is negative infinity or any value less than or
     * equal to the value of {@code Integer.MIN_VALUE}, the result is
     * equal to the value of {@code Integer.MIN_VALUE}.
     * <li>If the argument is positive infinity or any value greater than or
     * equal to the value of {@code Integer.MAX_VALUE}, the result is
     * equal to the value of {@code Integer.MAX_VALUE}.</ul>
     *
     * @param   index the index of the value to be rounded to an integer.
     * @return  the value of the argument rounded to the nearest
     *          {@code int} value.
     * @see     java.lang.Integer#MAX_VALUE
     * @see     java.lang.Integer#MIN_VALUE
     */
    @Override
    public int getInt(int index) {
        return Math.round(data.getFloat(index));
    }

    @Override
    public double missingValueIndicator() {
        return FloatColumnType.missingValueIndicator();
    }
}
