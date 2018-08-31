package tech.tablesaw.columns.numbers;

import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
import it.unimi.dsi.fastutil.floats.FloatOpenHashSet;
import it.unimi.dsi.fastutil.floats.FloatSet;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.AbstractParser;
import tech.tablesaw.filtering.predicates.FloatPredicate;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.nio.ByteBuffer;
import java.util.Iterator;

import static tech.tablesaw.selection.Selection.selectNRowsAtRandom;

public class FloatDataWrapper implements RealDataWrapper, Iterable<Double> {

    private static final float MISSING_VALUE_INDICATOR = Float.NaN;

    private static final FloatPredicate isMissing = value -> value != value;

    public static final FloatParser DEFAULT_PARSER = new FloatParser(ColumnType.DOUBLE);

    private static final int BYTE_SIZE = 2;

    /**
     * Compares two ints, such that a sort based on this comparator would sort in descending order
     */
    private final FloatComparator descendingComparator = (o2, o1) -> (Float.compare(o1, o2));

    private final FloatArrayList data;

    private FloatDataWrapper(FloatArrayList data) {
        this.data = data;
    }

    public static FloatDataWrapper create(final float[] arr) {
        return new FloatDataWrapper(new FloatArrayList(arr));
    }

    public static FloatDataWrapper create(final int initialSize) {
        return new FloatDataWrapper(new FloatArrayList(initialSize));
    }

    public static boolean valueIsMissing(int value) {
        return value == MISSING_VALUE_INDICATOR;
    }

    @Override
    public FloatDataWrapper subset(final int[] rows) {
        final FloatDataWrapper c = this.emptyCopy();
        for (final int row : rows) {
            c.data.add(getFloat(row));
        }
        return c;
    }

    @Override
    public FloatDataWrapper unique() {
        final FloatSet values = new FloatOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissing(i)) {
                values.add(getFloat(i));
            }
        }
        final FloatDataWrapper column = FloatDataWrapper.create(values.size());
        for (float value : values) {
            column.data.add(value);
        }
        return column;
    }

    @Override
    public FloatDataWrapper top(int n) {
        final FloatArrayList top = new FloatArrayList();
        final float[] values = data.toFloatArray();
        FloatArrays.parallelQuickSort(values, descendingComparator);
        for (int i = 0; i < n && i < values.length; i++) {
            top.add(values[i]);
        }
        return new FloatDataWrapper(top);
    }

    @Override
    public FloatDataWrapper bottom(final int n) {
        final FloatArrayList bottom = new FloatArrayList();
        final float[] values = data.toFloatArray();
        FloatArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(values[i]);
        }
        return new FloatDataWrapper(bottom);
    }

    @Override
    public FloatDataWrapper lag(int n) {
        final int srcPos = n >= 0 ? 0 : 0 - n;
        final float[] dest = new float[size()];
        final int destPos = n <= 0 ? 0 : n;
        final int length = n >= 0 ? size() - n : size() + n;

        for (int i = 0; i < size(); i++) {
            dest[i] = MISSING_VALUE_INDICATOR;
        }

        float[] array = data.toFloatArray();

        System.arraycopy(array, srcPos, dest, destPos, length);
        return new FloatDataWrapper(new FloatArrayList(dest));
    }

    @Override
    public FloatDataWrapper removeMissing() {
        FloatDataWrapper result = new FloatDataWrapper(new FloatArrayList(size()));
        FloatListIterator iterator = data.iterator();
        while (iterator.hasNext()) {
            final float v = iterator.nextFloat();
            if (!isMissingValue(v)) {
                result.data.add(v);
            }
        }
        return result;
    }

    @Override
    public Iterator<Double> iterator() {

        FloatListIterator listIterator = data.listIterator();

        return new DoubleIterator() {

            @Override
            public double nextDouble() {
                return listIterator.nextFloat();
            }

            @Override
            public boolean hasNext() {
                return listIterator.hasNext();
            }
        };
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public Selection isMissing() {
        return eval(isMissing);
    }

    @Override
    public void appendCell(String value) {
        try {
            append(DEFAULT_PARSER.parseFloat(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Parsing error adding value to FloatDataWrapper: " + e.getMessage());
        }
    }

    @Override
    public void appendCell(String value, AbstractParser<?> parser) {
        try {
            append(parser.parseFloat(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Parsing error adding value to FloatDataWrapper: " + e.getMessage());
        }
    }

    @Override
    public boolean contains(double value) {
        if (value > Float.MAX_VALUE || value < Float.MIN_VALUE) {
            return false;
        }
        return data.contains((short) value);
    }

    @Override
    public void append(double value) throws NumberOutOfRangeException {
        if (value > Float.MAX_VALUE) {
            throw new NumberOutOfRangeException(String.valueOf(value), value);
        }
        data.add((short) value);
    }

    @Override
    public void append(float value) {
        data.add(value);
    }

    @Override
    public FloatDataWrapper emptyCopy() {
        return emptyCopy(AbstractColumn.DEFAULT_ARRAY_SIZE);
    }

    @Override
    public FloatDataWrapper emptyCopy(final int rowSize) {
        return new FloatDataWrapper(new FloatArrayList(rowSize));
    }

    @Override
    public FloatDataWrapper copy() {
        return new FloatDataWrapper(data.clone());
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
    public void set(int i, float val) {
        data.set(i, val);
    }

    @Override
    public void set(int i, double val) throws NumberOutOfRangeException {
        if (val > Float.MAX_VALUE) {
            throw new NumberOutOfRangeException(String.valueOf(val), (long) val);
        }
        data.set(i, (float) val);
    }

    @Override
    public void appendMissing() {
        data.add(MISSING_VALUE_INDICATOR);
    }

    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(BYTE_SIZE).putFloat(getFloat(rowNumber)).array();
    }

    @Override
    public int countUnique() {
        FloatSet uniqueElements = new FloatOpenHashSet();
        for (int i = 0; i < size(); i++) {
            float val = getFloat(i);
            if (!isMissingValue(val)) {
                uniqueElements.add(val);
            }
        }
        return uniqueElements.size();
    }

    @Override
    public void setMissing(Selection condition) {
        for (int index : condition) {
            data.set(index, MISSING_VALUE_INDICATOR);
        }
    }

    /**
     * Returns the value at the given index. The actual value is returned if the ColumnType is INTEGER
     *
     * Returns the closest {@code int} to the argument, with ties
     * rounding to positive infinity.
     *
     * <p>
     * Special cases:
     * <ul><li>If the argument is NaN, the result is 0.
     * <li>If the argument is positive infinity or any value greater than or
     * equal to the value of {@code Integer.MAX_VALUE}, an error will be thrown
     *
     * @param   row the index of the value to be rounded to an integer.
     * @return  the value of the argument rounded to the nearest
     *          {@code int} value.
     * @throws  ClassCastException if the absolute value of the value to be rounded is too large to be cast to an int
     */
    @Override
    public float getFloat(int row) {
        float value = data.getFloat(row);
        if (! isMissingValue(value)) {
            return data.getFloat(row);
        }
        return FloatColumnType.missingValueIndicator();
    }

    @Override
    public double getDouble(int row) {
        float value = data.getFloat(row);
        if (isMissingValue(value)) {
            return DoubleColumnType.missingValueIndicator();
        }
        return value;
    }

    @Override
    public boolean isMissingValue(double value) {
        return value != value;
    }

    @Override
    public boolean isMissing(int rowNumber) {
        return isMissingValue(getFloat(rowNumber));
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
    public void appendObj(Object obj) {
        if (obj == null) {
            appendMissing();
            return;
        }
        if (obj instanceof Float) {
            data.add((short) obj);
            return;
        }
        throw new IllegalArgumentException("Could not append " + obj.getClass());
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public FloatDataWrapper inRange(int start, int end) {
        return where(Selection.withRange(start, end));
    }

    @Override
    public FloatDataWrapper where(Selection selection) {
        FloatArrayList newList = new FloatArrayList(selection.size());
        for (int i = 0; i < selection.size(); i++) {
            newList.add(getFloat(selection.get(i)));
        }
        return new FloatDataWrapper(newList);
    }

    @Override
    public FloatDataWrapper lead(int n) {
        return lag(-n);
    }

    @Override
    public FloatDataWrapper first(int numRows) {
        return where(Selection.withRange(0, numRows));
    }

    @Override
    public FloatDataWrapper last(int numRows) {
        return where(Selection.withRange(size() - numRows, size()));
    }

    @Override
    public FloatDataWrapper sampleN(int n) {
        return where(selectNRowsAtRandom(n, size()));
    }

    @Override
    public FloatDataWrapper sampleX(double proportion) {
        int columnSize = (int) Math.round(size() * proportion);
        return where(selectNRowsAtRandom(columnSize, size()));
    }

    public Selection eval(final FloatPredicate predicate) {
        final Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            final float next = getFloat(idx);
            if (predicate.test(next)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }
}
