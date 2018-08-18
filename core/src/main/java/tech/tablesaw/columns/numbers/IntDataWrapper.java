package tech.tablesaw.columns.numbers;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.StringParser;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;

public class IntDataWrapper implements NumericDataWrapper {

    private static final IntColumnType COLUMN_TYPE = ColumnType.INTEGER;

    /**
     * Compares two ints, such that a sort based on this comparator would sort in descending order
     */
    private final IntComparator descendingComparator = (o2, o1) -> (Integer.compare(o1, o2));

    private final IntArrayList data;

    public IntDataWrapper(IntArrayList data) {
        this.data = data;
    }

    @Override
    public NumberIterator numberIterator() {
        return new NumberIterator(data);
    }

    @Override
    public Iterator<Integer> iterator() {
        return data.iterator();
    }

    @Override
    public NumericDataWrapper top(int n) {
        final IntArrayList top = new IntArrayList();
        final int[] values = data.toIntArray();
        IntArrays.parallelQuickSort(values, descendingComparator);
        for (int i = 0; i < n && i < values.length; i++) {
            top.add(values[i]);
        }
        return new IntDataWrapper(top);
    }

    @Override
    public NumericDataWrapper bottom(int n) {

        final IntArrayList bottom = new IntArrayList();
        final int[] values = data.toIntArray();
        IntArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(values[i]);
        }
        return new IntDataWrapper(bottom);
    }

    @Override
    public NumericDataWrapper removeMissing() {
        NumericDataWrapper wrapper = copy();
        wrapper.clear();
        final NumberIterator iterator = numberIterator();
        while (iterator.hasNext()) {
            final int v = iterator.nextInt();
            if (!isMissingValue(v)) {
                wrapper.append(v);
            }
        }
        return wrapper;
    }

    @Override
    public int size() {
        return data.size();
    }

    public void append(final float f) {
        append((double) f);
    }

    @Override
    public void append(double d) {
        if (isMissingValue(d)) {
            appendMissing();
        } else if (d == (int) d) {
            data.add((int) d);
        } else {
            throw new RuntimeException("Incompatible numeric type. Attempting to add a float to a column of integers.");
        }
    }

    @Override
    public void append(int i) {
        data.add(i);
    }

    @Override
    public void appendCell(String value) {
        append(IntColumnType.DEFAULT_PARSER.parseInt(value));
    }

    @Override
    public void appendCell(String value, StringParser<?> parser) {
        append(parser.parseInt(value));
    }

    @Override
    public int countMissing() {
        int count = 0;
        for (int i = 0; i < size(); i++) {
            if (isMissingValue(getInt(i))) {
                count++;
            }
        }
        return count;
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
    public float getFloat(int row) {
        int value = data.getInt(row);
        if (isMissingValue(value)) {
            return FloatColumnType.missingValueIndicator();
        }
        return value;
    }

    @Override
    public IntDataWrapper copy() {
        return new IntDataWrapper(data.clone());
    }

    @Override
    public IntDataWrapper emptyCopy() {
        return new IntDataWrapper(new IntArrayList());
    }

    @Override
    public IntDataWrapper emptyCopy(int size) {
        return new IntDataWrapper(new IntArrayList(size));
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
        IntArrays.parallelQuickSort(data.elements(), descendingComparator);
    }

    @Override
    public void set(int r, double value) {
        if (value == (int) value) {
            data.set(r, (int) value);
        } else {
            throw new RuntimeException("Incompatible numeric type. Attempting to add a float to a column of integers.");
        }
    }

    @Override
    public boolean contains(double value) {
        return (value == (int) value) && data.contains((int) value);
    }

    @Override
    public boolean contains(int value) {
        return data.contains(value);
    }

    @Override
    public IntDataWrapper lag(int n) {
        final int srcPos = n >= 0 ? 0 : 0 - n;
        final int[] dest = new int[size()];
        final int destPos = n <= 0 ? 0 : n;
        final int length = n >= 0 ? size() - n : size() + n;

        for (int i = 0; i < size(); i++) {
            dest[i] = IntColumnType.missingValueIndicator();
        }

        int[] array = data.toIntArray();

        System.arraycopy(array, srcPos, dest, destPos, length);
        return new IntDataWrapper(new IntArrayList(dest));
    }

    @Override
    public IntDataWrapper lead(int n) {
        return lag(-n);
    }

    @Override
    public int countUnique() {
        IntSet uniqueElements = new IntOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissingValue(getInt(i))) {
                uniqueElements.add(getInt(i));
            }
        }
        return uniqueElements.size();
    }

    public int getInt(int index) {
        return data.getInt(index);
    }

    @Override
    public void appendMissing() {
        append(IntColumnType.missingValueIndicator());
    }

    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(COLUMN_TYPE.byteSize()).putInt(getInt(rowNumber)).array();
    }

    @Override
    public IntColumnType type() {
        return ColumnType.INTEGER;
    }

    @Override
    public double missingValueIndicator() {
        return IntColumnType.missingValueIndicator();
    }
}
