package tech.tablesaw.columns.numbers;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.AbstractParser;
import tech.tablesaw.filtering.predicates.ShortPredicate;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.nio.ByteBuffer;
import java.util.Iterator;

import static tech.tablesaw.selection.Selection.selectNRowsAtRandom;

public class ShortDataWrapper implements DataWrapper, Iterable<Integer> {

    private static final short MISSING_VALUE = Short.MIN_VALUE;

    private static final ShortPredicate isMissing = value -> value == MISSING_VALUE;

    public static final IntParser DEFAULT_PARSER = new IntParser(ColumnType.INTEGER);

    private static final int BYTE_SIZE = 2;

    /**
     * Compares two ints, such that a sort based on this comparator would sort in descending order
     */
    private final ShortComparator descendingComparator = (o2, o1) -> (Short.compare(o1, o2));

    private final ShortArrayList data;

    private ShortDataWrapper(ShortArrayList data) {
        this.data = data;
    }

    public static ShortDataWrapper create(final short[] arr) {
        return new ShortDataWrapper(new ShortArrayList(arr));
    }

    public static ShortDataWrapper create(final int initialSize) {
        return new ShortDataWrapper(new ShortArrayList(initialSize));
    }

    public static boolean valueIsMissing(int value) {
        return value == MISSING_VALUE;
    }

    @Override
    public short getShort(int index) {
        return data.getShort(index);
    }

    @Override
    public byte getByte(int index) {
        return (byte) data.getShort(index);
    }

    @Override
    public ShortDataWrapper subset(final int[] rows) {
        final ShortDataWrapper c = this.emptyCopy();
        for (final int row : rows) {
            c.append(getShort(row));
        }
        return c;
    }

    @Override
    public ShortDataWrapper unique() {
        final ShortSet values = new ShortOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissing(i)) {
                values.add(getShort(i));
            }
        }
        final ShortDataWrapper column = ShortDataWrapper.create(values.size());
        for (short value : values) {
            column.append(value);
        }
        return column;
    }

    @Override
    public ShortDataWrapper top(int n) {
        final ShortArrayList top = new ShortArrayList();
        final short[] values = data.toShortArray();
        ShortArrays.parallelQuickSort(values, descendingComparator);
        for (int i = 0; i < n && i < values.length; i++) {
            top.add(values[i]);
        }
        return new ShortDataWrapper(top);
    }

    @Override
    public ShortDataWrapper bottom(final int n) {
        final ShortArrayList bottom = new ShortArrayList();
        final short[] values = data.toShortArray();
        ShortArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(values[i]);
        }
        return new ShortDataWrapper(bottom);
    }

    @Override
    public ShortDataWrapper lag(int n) {
        final int srcPos = n >= 0 ? 0 : 0 - n;
        final short[] dest = new short[size()];
        final int destPos = n <= 0 ? 0 : n;
        final int length = n >= 0 ? size() - n : size() + n;

        for (int i = 0; i < size(); i++) {
            dest[i] = MISSING_VALUE;
        }

        short[] array = data.toShortArray();

        System.arraycopy(array, srcPos, dest, destPos, length);
        return new ShortDataWrapper(new ShortArrayList(dest));
    }

    @Override
    public ShortDataWrapper removeMissing() {
        ShortDataWrapper result = copy();
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

    @Override
    public Iterator<Integer> iterator() {
        ShortListIterator listIterator = data.listIterator();

        return new IntIterator() {

            @Override
            public int nextInt() {
                return listIterator.nextShort();
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
            append(DEFAULT_PARSER.parseShort(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Parsing error adding value to ShortDataWrapper: " + e.getMessage());
        }
    }

    @Override
    public void appendCell(String value, AbstractParser<?> parser) {
        try {
            append(parser.parseShort(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Parsing error adding value to ShortDataWrapper: " + e.getMessage());
        }
    }

    @Override
    public boolean contains(int value) {
        if (value > Short.MAX_VALUE || value < Short.MIN_VALUE) {
            return false;
        }
        return data.contains((short) value);
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
        data.add((short) value);
    }

    @Override
    public ShortDataWrapper emptyCopy() {
        return emptyCopy(AbstractColumn.DEFAULT_ARRAY_SIZE);
    }

    @Override
    public ShortDataWrapper emptyCopy(final int rowSize) {
        return new ShortDataWrapper(new ShortArrayList(rowSize));
    }

    @Override
    public ShortDataWrapper copy() {
        return new ShortDataWrapper(data.clone());
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
    public void set(int i, int val) {
        data.set(i, (short) val);
    }

    @Override
    public void appendMissing() {
        append(MISSING_VALUE);
    }

    @Override
    public void append(int value) {
        data.add((short) value);
    }

    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(BYTE_SIZE).putShort(getShort(rowNumber)).array();
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

    @Override
    public void setMissing(Selection condition) {
        for (int index : condition) {
            data.set(index, MISSING_VALUE);
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
    public int getInt(int row) {
        short value = data.getShort(row);
        if (! isMissingValue(value)) {
            return data.getShort(row);
        }
        return IntColumnType.missingValueIndicator();
    }

    @Override
    public double getDouble(int row) {
        short value = data.getShort(row);
        if (isMissingValue(value)) {
            return DoubleColumnType.missingValueIndicator();
        }
        return value;
    }

    @Override
    public boolean isMissingValue(int value) {
        return value == MISSING_VALUE;
    }

    @Override
    public boolean isMissing(int rowNumber) {
        return isMissingValue(getShort(rowNumber));
    }

    @Override
    public void sortAscending() {
        ShortArrays.parallelQuickSort(data.elements());
    }

    @Override
    public void sortDescending() {
        ShortArrays.parallelQuickSort(data.elements(), descendingComparator);
    }

    @Override
    public void appendObj(Object obj) {
        if (obj == null) {
            appendMissing();
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
    public ShortDataWrapper inRange(int start, int end) {
        return where(Selection.withRange(start, end));
    }

    @Override
    public ShortDataWrapper where(Selection selection) {
        ShortArrayList newList = new ShortArrayList(selection.size());
        for (int i = 0; i < selection.size(); i++) {
            newList.add(getShort(selection.get(i)));
        }
        return new ShortDataWrapper(newList);
    }

    @Override
    public ShortDataWrapper lead(int n) {
        return lag(-n);
    }

    @Override
    public ShortDataWrapper first(int numRows) {
        return where(Selection.withRange(0, numRows));
    }

    @Override
    public ShortDataWrapper last(int numRows) {
        return where(Selection.withRange(size() - numRows, size()));
    }

    @Override
    public ShortDataWrapper sampleN(int n) {
        return where(selectNRowsAtRandom(n, size()));
    }

    @Override
    public ShortDataWrapper sampleX(double proportion) {
        int columnSize = (int) Math.round(size() * proportion);
        return where(selectNRowsAtRandom(columnSize, size()));
    }

    public Selection eval(final ShortPredicate predicate) {
        final Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            final short next = getShort(idx);
            if (predicate.test(next)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }
}
