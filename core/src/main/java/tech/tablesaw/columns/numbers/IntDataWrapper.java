package tech.tablesaw.columns.numbers;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntIterable;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.AbstractParser;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.nio.ByteBuffer;
import java.util.function.IntPredicate;

import static tech.tablesaw.selection.Selection.selectNRowsAtRandom;

public class IntDataWrapper implements DataWrapper, IntIterable {

    private static final IntPredicate isMissing = value -> value == IntColumnType.missingValueIndicator();
    /**
     * Compares two ints, such that a sort based on this comparator would sort in descending order
     */
    private final IntComparator descendingComparator = (o2, o1) -> (Integer.compare(o1, o2));

    private final IntArrayList data;

    private IntDataWrapper(IntArrayList data) {
        this.data = data;
    }

    public static IntDataWrapper create(final int[] arr) {
        return new IntDataWrapper(new IntArrayList(arr));
    }

    public static IntDataWrapper create(final int initialSize) {
        return new IntDataWrapper(new IntArrayList(initialSize));
    }

    public static boolean valueIsMissing(int value) {
        return value == Integer.MIN_VALUE;
    }

    @Override
    public short getShort(int index) {
        return (short) data.getInt(index);
    }

    @Override
    public byte getByte(int index) {
        return (byte) data.getInt(index);
    }

    @Override
    public IntDataWrapper subset(final int[] rows) {
        final IntDataWrapper c = this.emptyCopy();
        for (final int row : rows) {
            c.append(getShort(row));
        }
        return c;
    }

    @Override
    public IntDataWrapper unique() {
        final ShortSet values = new ShortOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissing(i)) {
                values.add(getShort(i));
            }
        }
        final IntDataWrapper column = IntDataWrapper.create(values.size());
        for (short value : values) {
            column.append(value);
        }
        return column;
    }

    @Override
    public IntDataWrapper top(int n) {
        final IntArrayList top = new IntArrayList();
        final int[] values = data.toIntArray();
        IntArrays.parallelQuickSort(values, descendingComparator);
        for (int i = 0; i < n && i < values.length; i++) {
            top.add(values[i]);
        }
        return new IntDataWrapper(top);
    }

    @Override
    public IntDataWrapper bottom(final int n) {
        final IntArrayList bottom = new IntArrayList();
        final int[] values = data.toIntArray();
        IntArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(values[i]);
        }
        return new IntDataWrapper(bottom);
    }

    @Override
    public IntDataWrapper lag(int n) {
        final int srcPos = n >= 0 ? 0 : 0 - n;
        final int[] dest = new int[size()];
        final int destPos = n <= 0 ? 0 : n;
        final int length = n >= 0 ? size() - n : size() + n;

        for (int i = 0; i < size(); i++) {
            dest[i] = Integer.MIN_VALUE;
        }

        int[] array = data.toIntArray();

        System.arraycopy(array, srcPos, dest, destPos, length);
        return new IntDataWrapper(new IntArrayList(dest));
    }

    @Override
    public IntDataWrapper removeMissing() {
        IntDataWrapper result = copy();
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
            append(IntColumnType.DEFAULT_PARSER.parseInt(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Parsing error adding value to IntDataWrapper: " + e.getMessage());
        }
    }

    @Override
    public void appendCell(String value, AbstractParser<?> parser) {
        try {
            append(parser.parseInt(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Parsing error adding value to IntDataWrapper: " + e.getMessage());
        }
    }

    @Override
    public void append(short i) {
        data.add(i);
    }

    @Override
    public void append(byte value) {
        data.add(value);
    }

    @Override
    public void append(long value) {
        data.add((int) value);
    }

    @Override
    public IntDataWrapper emptyCopy() {
        return emptyCopy(AbstractColumn.DEFAULT_ARRAY_SIZE);
    }

    @Override
    public IntDataWrapper emptyCopy(final int rowSize) {
        return new IntDataWrapper(new IntArrayList(rowSize));
    }

    @Override
    public IntDataWrapper copy() {
        return new IntDataWrapper(data.clone());
    }

    @Override
    public Object[] asObjectArray() {
        final Integer[] output = new Integer[size()];
        for (int i = 0; i < size(); i++) {
            output[i] = getInt(i);
        }
        return output;
    }

    @Override
    public void set(int i, short val) {
        data.set(i, val);
    }

    @Override
    public void setMissing(Selection s) {
        for (int index : s) {
            data.set(index, Integer.MIN_VALUE);
        }
    }

    @Override
    public void set(int i, int val) {
        data.set(i, val);
    }

    @Override
    public void appendMissing() {
        append(Integer.MIN_VALUE);
    }

    @Override
    public void append(int value) {
        data.add(value);
    }

    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(IntColumnType.INSTANCE.byteSize()).putShort(getShort(rowNumber)).array();
    }

    @Override
    public int countUnique() {
        ShortSet uniqueElements = new ShortOpenHashSet();
        for (int i = 0; i < size(); i++) {
            short val = getShort(i);
            if (!isMissingValue(val)) {
                uniqueElements.add(val);
            }
        }
        return uniqueElements.size();
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

    @Override
    public boolean isMissingValue(int value) {
        return IntColumnType.isMissingValue(value);
    }

    @Override
    public boolean contains(int value) {
        return data.contains(value);
    }

    @Override
    public boolean isMissing(int rowNumber) {
        return isMissingValue(getInt(rowNumber));
    }

    @Override
    public void sortAscending() {
        IntArrays.parallelQuickSort(data.elements());
    }

    @Override
    public void sortDescending() {
        IntArrays.parallelQuickSort(data.elements(), descendingComparator);
    }

    @Override
    public void appendObj(Object obj) {
        if (obj == null) {
            appendMissing();
            return;
        }
        if (obj instanceof Integer) {
            append((int) obj);
            return;
        }
        if (obj instanceof Short) {
            append((short) obj);
            return;
        }
        throw new IllegalArgumentException("Could not append " + obj.getClass());
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public IntDataWrapper inRange(int start, int end) {
        return where(Selection.withRange(start, end));
    }

    @Override
    public IntDataWrapper where(Selection selection) {
        IntArrayList newList = new IntArrayList(selection.size());
        for (int i = 0; i < selection.size(); i++) {
            newList.add(getShort(selection.get(i)));
        }
        return new IntDataWrapper(newList);
    }

    @Override
    public IntDataWrapper lead(int n) {
        return lag(-n);
    }

    // TODO(lwhite): Should this class have type params?
/*
    @Override
    public ShortDataWrapper filter(Predicate<? super Short> test) {
        return (ShortDataWrapper) super.filter(test);
    }

    @Override
    public ShortDataWrapper sorted(Comparator<? super Short> comp) {
        return (ShortDataWrapper) super.sorted(comp);
    }

*/

    @Override
    public IntDataWrapper first(int numRows) {
        return where(Selection.withRange(0, numRows));
    }

    @Override
    public IntDataWrapper last(int numRows) {
        return where(Selection.withRange(size() - numRows, size()));
    }

    @Override
    public IntDataWrapper sampleN(int n) {
        return where(selectNRowsAtRandom(n, size()));
    }

    @Override
    public IntDataWrapper sampleX(double proportion) {
        int columnSize = (int) Math.round(size() * proportion);
        return where(selectNRowsAtRandom(columnSize, size()));
    }

    /**
     * Returns a new LongColumn containing a value for each value in this column
     *
     * A widening primitive conversion from short to long does not lose any information at all;
     * the numeric value is preserved exactly.
     *
     * A missing value in the receiver is converted to a missing value in the result
     */
    public LongArrayList asLongArrayList() {
        LongArrayList values = new LongArrayList();
        for (int f : data) {
            values.add(f);
        }
        values.trim();
        return values;
    }

    /**
     * Returns a new FloatColumn containing a value for each value in this column, truncating if necessary.
     *
     * A widening primitive conversion from an int to a float does not lose information about the overall magnitude
     * of a numeric value. It may, however, result in loss of precision - that is, the result may lose some of the
     * least significant bits of the value. In this case, the resulting floating-point value will be a correctly
     * rounded version of the integer value, using IEEE 754 round-to-nearest mode.
     *
     * Despite the fact that a loss of precision may occur, a widening primitive conversion never results in a
     * run-time exception.
     *
     * A missing value in the receiver is converted to a missing value in the result
     */
    public FloatArrayList asFloatArrayList() {
        FloatArrayList values = new FloatArrayList();
        for (int d : data) {
            values.add(d);
        }
        values.trim();
        return values;
    }

    /**
     * Returns a new DoubleColumn containing a value for each value in this column, truncating if necessary.
     *
     * A widening primitive conversion from an int to a double does not lose information about the overall magnitude
     * of a numeric value. It may, however, result in loss of precision - that is, the result may lose some of the
     * least significant bits of the value. In this case, the resulting floating-point value will be a correctly
     * rounded version of the integer value, using IEEE 754 round-to-nearest mode.
     *
     * Despite the fact that a loss of precision may occur, a widening primitive conversion never results in a
     * run-time exception.
     *
     * A missing value in the receiver is converted to a missing value in the result
     */
    public DoubleArrayList asDoubleArrayList() {
        DoubleArrayList values = new DoubleArrayList();
        for (int d : data) {
            values.add(d);
        }
        values.trim();
        return values;
    }

    public Selection eval(final IntPredicate predicate) {
        final Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            final int next = getInt(idx);
            if (predicate.test(next)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }

    @Override
    public IntIterator iterator() {
        return data.iterator();
    }
}
