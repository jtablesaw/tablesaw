package tech.tablesaw.columns.numbers;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.ints.IntIterator;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.AbstractParser;
import tech.tablesaw.filtering.predicates.BytePredicate;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.nio.ByteBuffer;
import java.util.Iterator;

import static tech.tablesaw.selection.Selection.selectNRowsAtRandom;

public class ByteDataWrapper implements IntegerDataWrapper, Iterable<Integer> {

    private static final byte MISSING_VALUE = Byte.MIN_VALUE;

    private static final BytePredicate isMissing = value -> value == MISSING_VALUE;

    public static final IntParser DEFAULT_PARSER = new IntParser(ColumnType.INTEGER);

    private static final int BYTE_SIZE = 1;

    /**
     * Compares two ints, such that a sort based on this comparator would sort in descending order
     */
    private final ByteComparator descendingComparator = (o2, o1) -> (Byte.compare(o1, o2));

    private final ByteArrayList data;

    private ByteDataWrapper(ByteArrayList data) {
        this.data = data;
    }

    public static ByteDataWrapper create(final byte[] arr) {
        return new ByteDataWrapper(new ByteArrayList(arr));
    }

    public static ByteDataWrapper create(final int initialSize) {
        return new ByteDataWrapper(new ByteArrayList(initialSize));
    }

    public static boolean valueIsMissing(int value) {
        return value == MISSING_VALUE;
    }

    @Override
    public short getShort(int index) {
        byte result = getByte(index);
        if (result == MISSING_VALUE) {
            return Short.MIN_VALUE;
        }
        return result;
    }

    public byte getByte(int index) {
        return data.getByte(index);
    }

    @Override
    public ByteDataWrapper subset(final int[] rows) {
        final ByteDataWrapper c = this.emptyCopy();
        for (final int row : rows) {
            c.append(getShort(row));
        }
        return c;
    }

    @Override
    public ByteDataWrapper unique() {
        final ByteSet values = new ByteOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissing(i)) {
                values.add(getByte(i));
            }
        }
        final ByteDataWrapper wrapper = ByteDataWrapper.create(values.size());
        for (byte value : values) {
            wrapper.append(value);
        }
        return wrapper;
    }

    @Override
    public ByteDataWrapper top(int n) {
        final ByteArrayList top = new ByteArrayList();
        final byte[] values = data.toByteArray();
        ByteArrays.parallelQuickSort(values, descendingComparator);
        for (int i = 0; i < n && i < values.length; i++) {
            top.add(values[i]);
        }
        return new ByteDataWrapper(top);
    }

    @Override
    public ByteDataWrapper bottom(final int n) {
        final ByteArrayList bottom = new ByteArrayList();
        final byte[] values = data.toByteArray();
        ByteArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(values[i]);
        }
        return new ByteDataWrapper(bottom);
    }

    @Override
    public ByteDataWrapper lag(int n) {
        final int srcPos = n >= 0 ? 0 : 0 - n;
        final byte[] dest = new byte[size()];
        final int destPos = n <= 0 ? 0 : n;
        final int length = n >= 0 ? size() - n : size() + n;

        for (int i = 0; i < size(); i++) {
            dest[i] = MISSING_VALUE;
        }

        byte[] array = data.toByteArray();

        System.arraycopy(array, srcPos, dest, destPos, length);
        return new ByteDataWrapper(new ByteArrayList(dest));
    }

    @Override
    public ByteDataWrapper removeMissing() {
        ByteDataWrapper result = new ByteDataWrapper(new ByteArrayList());
        ByteListIterator iterator = data.iterator();
        while (iterator.hasNext()) {
            final byte v = iterator.nextByte();
            if (!isMissingValue(v)) {
                result.append(v);
            }
        }
        return result;
    }

    @Override
    public Iterator<Integer> iterator() {
        ByteListIterator listIterator = data.listIterator();

        return new IntIterator() {

            @Override
            public int nextInt() {
                return listIterator.nextByte();
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
    public void appendCell(String value) throws NumberOutOfRangeException {
        try {
            append(DEFAULT_PARSER.parseByte(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Parsing error adding value to ByteDataWrapper: " + e.getMessage());
        }
    }

    @Override
    public void appendCell(String value, AbstractParser<?> parser) throws NumberOutOfRangeException {
        try {
            append(parser.parseByte(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Parsing error adding value to ByteDataWrapper: " + e.getMessage());
        }
    }

    @Override
    public boolean contains(int value) {
        if (value > Byte.MAX_VALUE || value < Byte.MIN_VALUE) {
            return false;
        }
        return data.contains((byte) value);
    }

    @Override
    public void append(long value) {
        data.add((byte) value);
    }

    @Override
    public ByteDataWrapper emptyCopy() {
        return emptyCopy(AbstractColumn.DEFAULT_ARRAY_SIZE);
    }

    @Override
    public ByteDataWrapper emptyCopy(final int rowSize) {
        return new ByteDataWrapper(new ByteArrayList(rowSize));
    }

    @Override
    public ByteDataWrapper copy() {
        return new ByteDataWrapper(data.clone());
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
    public void set(int i, short val) throws NumberOutOfRangeException {
        if (val > Byte.MAX_VALUE) {
            throw new NumberOutOfRangeException(String.valueOf(val), (long) val);
        }
        data.set(i, (byte) val);
    }

    @Override
    public void set(int i, int val) throws NumberOutOfRangeException {
        if (val > Byte.MAX_VALUE) {
            throw new NumberOutOfRangeException(String.valueOf(val), (long) val);
        }
        data.set(i, (byte) val);
    }

    public void set(int i, byte val) {
        data.set(i, val);
    }

    @Override
    public void appendMissing() {
        append(MISSING_VALUE);
    }

    @Override
    public void append(int value) {
        data.add((byte) value);
    }

    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(BYTE_SIZE).put(getByte(rowNumber)).array();
    }

    @Override
    public int countUnique() {
        ByteSet uniqueElements = new ByteOpenHashSet();
        for (int i = 0; i < size(); i++) {
            byte val = getByte(i);
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
        short value = data.getByte(row);
        if (! isMissingValue(value)) {
            return data.getByte(row);
        }
        return IntColumnType.missingValueIndicator();
    }

    @Override
    public double getDouble(int row) {
        byte value = data.getByte(row);
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
        return isMissingValue(getByte(rowNumber));
    }

    @Override
    public void sortAscending() {
        ByteArrays.parallelQuickSort(data.elements());
    }

    @Override
    public void sortDescending() {
        ByteArrays.parallelQuickSort(data.elements(), descendingComparator);
    }

    @Override
    public void appendObj(Object obj) {
        if (obj == null) {
            appendMissing();
            return;
        }
        if (obj instanceof Byte) {
            append((byte) obj);
            return;
        }
        throw new IllegalArgumentException("Could not append " + obj.getClass());
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public ByteDataWrapper inRange(int start, int end) {
        return where(Selection.withRange(start, end));
    }

    @Override
    public ByteDataWrapper where(Selection selection) {
        ByteArrayList newList = new ByteArrayList(selection.size());
        for (int i = 0; i < selection.size(); i++) {
            newList.add(getByte(selection.get(i)));
        }
        return new ByteDataWrapper(newList);

    }

    @Override
    public ByteDataWrapper lead(int n) {
        return lag(-n);
    }

    // TODO(lwhite): Should this class have type params?

    @Override
    public ByteDataWrapper first(int numRows) {
        return where(Selection.withRange(0, numRows));
    }

    @Override
    public ByteDataWrapper last(int numRows) {
        return where(Selection.withRange(size() - numRows, size()));
    }

    @Override
    public ByteDataWrapper sampleN(int n) {
        return where(selectNRowsAtRandom(n, size()));
    }

    @Override
    public ByteDataWrapper sampleX(double proportion) {
        int columnSize = (int) Math.round(size() * proportion);
        return where(selectNRowsAtRandom(columnSize, size()));
    }

    public Selection eval(final BytePredicate predicate) {
        final Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            final byte next = getByte(idx);
            if (predicate.test(next)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }
}
