package tech.tablesaw.columns.numbers;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleIterable;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.doubles.DoubleOpenHashSet;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.AbstractParser;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.nio.ByteBuffer;
import java.util.function.DoublePredicate;

import static tech.tablesaw.selection.Selection.selectNRowsAtRandom;

public class DoubleDataWrapper implements RealDataWrapper, DoubleIterable {

    private static final double MISSING_VALUE_INDICATOR = Double.NaN;

    private static final DoublePredicate isMissing = value -> value != value;
    /**
     * Compares two ints, such that a sort based on this comparator would sort in descending order
     */
    private final DoubleComparator descendingComparator = (o2, o1) -> (Double.compare(o1, o2));

    private final DoubleArrayList data;

    private DoubleDataWrapper(DoubleArrayList data) {
        this.data = data;
    }

    public static DoubleDataWrapper create(final double[] arr) {
        return new DoubleDataWrapper(new DoubleArrayList(arr));
    }

    public static DoubleDataWrapper create(final int initialSize) {
        return new DoubleDataWrapper(new DoubleArrayList(initialSize));
    }

    public static boolean valueIsMissing(int value) {
        return value == Double.MIN_VALUE;
    }

    @Override
    public float getFloat(int index) {
        return (float) data.getDouble(index);
    }

    @Override
    public DoubleDataWrapper subset(final int[] rows) {
        final DoubleDataWrapper c = this.emptyCopy();
        for (final int row : rows) {
            c.append(getDouble(row));
        }
        return c;
    }

    @Override
    public DoubleDataWrapper unique() {
        final DoubleSet values = new DoubleOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissing(i)) {
                values.add(getDouble(i));
            }
        }
        final DoubleDataWrapper column = DoubleDataWrapper.create(values.size());
        for (double value : values) {
            column.append(value);
        }
        return column;
    }

    @Override
    public DoubleDataWrapper top(int n) {
        final DoubleArrayList top = new DoubleArrayList();
        final double[] values = data.toDoubleArray();
        DoubleArrays.parallelQuickSort(values, descendingComparator);
        for (int i = 0; i < n && i < values.length; i++) {
            top.add(values[i]);
        }
        return new DoubleDataWrapper(top);
    }

    @Override
    public DoubleDataWrapper bottom(final int n) {
        final DoubleArrayList bottom = new DoubleArrayList();
        final double[] values = data.toDoubleArray();
        DoubleArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(values[i]);
        }
        return new DoubleDataWrapper(bottom);
    }

    @Override
    public DoubleDataWrapper lag(int n) {
        final int srcPos = n >= 0 ? 0 : 0 - n;
        final double[] dest = new double[size()];
        final int destPos = n <= 0 ? 0 : n;
        final int length = n >= 0 ? size() - n : size() + n;

        for (int i = 0; i < size(); i++) {
            dest[i] = Integer.MIN_VALUE;
        }

        double[] array = data.toDoubleArray();

        System.arraycopy(array, srcPos, dest, destPos, length);
        return new DoubleDataWrapper(new DoubleArrayList(dest));
    }

    @Override
    public DoubleDataWrapper removeMissing() {
        DoubleDataWrapper result = copy();
        result.clear();
        DoubleListIterator iterator = data.iterator();
        while (iterator.hasNext()) {
            final double v = iterator.nextDouble();
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
    public void appendCell(String value) throws NumberOutOfRangeException {
        try {
            append(IntColumnType.DEFAULT_PARSER.parseInt(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Parsing error adding value to IntDataWrapper: " + e.getMessage());
        }
    }

    @Override
    public void appendCell(String value, AbstractParser<?> parser) throws NumberOutOfRangeException {
        try {
            append(parser.parseInt(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Parsing error adding value to IntDataWrapper: " + e.getMessage());
        }
    }

    @Override
    public void append(float value) {
        data.add(value);
    }

    @Override
    public DoubleDataWrapper emptyCopy() {
        return emptyCopy(AbstractColumn.DEFAULT_ARRAY_SIZE);
    }

    @Override
    public DoubleDataWrapper emptyCopy(final int rowSize) {
        return new DoubleDataWrapper(new DoubleArrayList(rowSize));
    }

    @Override
    public DoubleDataWrapper copy() {
        return new DoubleDataWrapper(data.clone());
    }

    @Override
    public Object[] asObjectArray() {
        final Double[] output = new Double[size()];
        for (int i = 0; i < size(); i++) {
            output[i] = getDouble(i);
        }
        return output;
    }

    @Override
    public void set(int i, float val) {
        data.set(i, val);
    }

    @Override
    public void set(int i, double val) {
        data.set(i, val);
    }

    @Override
    public void setMissing(Selection s) {
        for (int index : s) {
            data.set(index, MISSING_VALUE_INDICATOR);
        }
    }

    @Override
    public void appendMissing() {
        append(MISSING_VALUE_INDICATOR);
    }

    @Override
    public void append(double value) {
        data.add(value);
    }

    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(DoubleColumnType.INSTANCE.byteSize()).putDouble(getDouble(rowNumber)).array();
    }

    @Override
    public int countUnique() {
        DoubleSet uniqueElements = new DoubleOpenHashSet();
        for (int i = 0; i < size(); i++) {
            double val = getDouble(i);
            if (!isMissingValue(val)) {
                uniqueElements.add(val);
            }
        }
        return uniqueElements.size();
    }

    /**
     * Returns the value at the given index. The actual value is returned if the ColumnType is DOUBLE
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
    public double getDouble(int row) {
        return data.getDouble(row);
    }

    @Override
    public boolean isMissingValue(double value) {
        return DoubleColumnType.isMissingValue(value);
    }

    @Override
    public boolean contains(double value) {
        return data.contains(value);
    }

    @Override
    public boolean isMissing(int rowNumber) {
        return isMissingValue(getDouble(rowNumber));
    }

    @Override
    public void sortAscending() {
        DoubleArrays.parallelQuickSort(data.elements());
    }

    @Override
    public void sortDescending() {
        DoubleArrays.parallelQuickSort(data.elements(), descendingComparator);
    }

    @Override
    public void appendObj(Object obj) {
        if (obj == null) {
            appendMissing();
            return;
        }
        if (obj instanceof Double) {
            append((double) obj);
            return;
        }
        if (obj instanceof Float) {
            append((float) obj);
            return;
        }
        throw new IllegalArgumentException("Could not append " + obj.getClass());
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public DoubleDataWrapper inRange(int start, int end) {
        return where(Selection.withRange(start, end));
    }

    @Override
    public DoubleDataWrapper where(Selection selection) {
        DoubleArrayList newList = new DoubleArrayList(selection.size());
        for (int i = 0; i < selection.size(); i++) {
            newList.add(getDouble(selection.get(i)));
        }
        return new DoubleDataWrapper(newList);
    }

    @Override
    public DoubleDataWrapper lead(int n) {
        return lag(-n);
    }

    @Override
    public DoubleDataWrapper first(int numRows) {
        return where(Selection.withRange(0, numRows));
    }

    @Override
    public DoubleDataWrapper last(int numRows) {
        return where(Selection.withRange(size() - numRows, size()));
    }

    @Override
    public DoubleDataWrapper sampleN(int n) {
        return where(selectNRowsAtRandom(n, size()));
    }

    @Override
    public DoubleDataWrapper sampleX(double proportion) {
        int columnSize = (int) Math.round(size() * proportion);
        return where(selectNRowsAtRandom(columnSize, size()));
    }

    public Selection eval(final DoublePredicate predicate) {
        final Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            final double next = getDouble(idx);
            if (predicate.test(next)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }

    @Override
    public DoubleIterator iterator() {
        return data.iterator();
    }
}
