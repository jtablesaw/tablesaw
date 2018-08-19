package tech.tablesaw.api;

import java.nio.ByteBuffer;
import java.util.Iterator;

import com.google.common.base.Preconditions;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.IntColumnType;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.columns.numbers.NumberIterator;

public class IntColumn extends NumberColumn<Integer> implements NumericColumn<Integer>, CategoricalColumn<Integer> {

    private static final IntColumnType COLUMN_TYPE = ColumnType.INTEGER;

    /**
     * Compares two ints, such that a sort based on this comparator would sort in descending order
     */
    private final IntComparator descendingComparator = (o2, o1) -> (Integer.compare(o1, o2));

    private final IntArrayList data;

    protected IntColumn(final String name, IntArrayList data) {
        super(COLUMN_TYPE, name, data);
        this.printFormatter = NumberColumnFormatter.ints();
        this.data = data;
    }

    public static IntColumn create(final String name) {
        return new IntColumn(name, new IntArrayList());
    }

    public static IntColumn create(final String name, final int[] arr) {
        return new IntColumn(name, new IntArrayList(arr));
    }

    public static IntColumn create(final String name, final int initialSize) {
        return new IntColumn(name, new IntArrayList(initialSize));
    }

    @Override
    public IntColumn createCol(final String name, final int initialSize) {
        return create(name, initialSize);
    }

    /**
     * Returns a new numeric column initialized with the given name and size. The values in the column are
     * integers beginning at startsWith and continuing through size (exclusive), monotonically increasing by 1
     * TODO consider a generic fill function including steps or random samples from various distributions
     */
    public static IntColumn indexColumn(final String columnName, final int size, final int startsWith) {
        final IntColumn indexColumn = IntColumn.create(columnName, size);
        for (int i = 0; i < size; i++) {
            indexColumn.append(i + startsWith);
        }
        return indexColumn;
    }

    public static boolean valueIsMissing(int value) {
        return value == IntColumnType.missingValueIndicator();
    }

    @Override
    public Integer get(int index) {
        return getInt(index);
    }

    @Override
    public IntColumn unique() {
        final IntSet values = new IntOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissing(i)) {
                values.add(getInt(i));
            }
        }
        final IntColumn column = IntColumn.create(name() + " Unique values", values.size());
        for (int value : values) {
            column.append(value);
        }
        return column;
    }

    @Override
    public IntColumn top(int n) {
        final IntArrayList top = new IntArrayList();
        final int[] values = data.toIntArray();
        IntArrays.parallelQuickSort(values, descendingComparator);
        for (int i = 0; i < n && i < values.length; i++) {
            top.add(values[i]);
        }
        return new IntColumn(name() + "[Top " + n  + "]", top);
    }

    @Override
    public IntColumn bottom(final int n) {
        final IntArrayList bottom = new IntArrayList();
        final int[] values = data.toIntArray();
        IntArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(values[i]);
        }
        return new IntColumn(name() + "[Bottoms " + n  + "]", bottom);
    }

    @Override
    public IntColumn lag(int n) {
        final int srcPos = n >= 0 ? 0 : 0 - n;
        final int[] dest = new int[size()];
        final int destPos = n <= 0 ? 0 : n;
        final int length = n >= 0 ? size() - n : size() + n;

        for (int i = 0; i < size(); i++) {
            dest[i] = IntColumnType.missingValueIndicator();
        }

        int[] array = data.toIntArray();

        System.arraycopy(array, srcPos, dest, destPos, length);
        return new IntColumn(name() + " lag(" + n + ")", new IntArrayList(dest));
    }

    @Override
    public IntColumn removeMissing() {
        IntColumn result = copy();
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

    public IntColumn append(int i) {
        data.add(i);
        return this;
    }

    public IntColumn append(Integer val) {
        this.append(val.intValue());
        return this;
    }

    @Override
    public IntColumn emptyCopy() {
        return (IntColumn) super.emptyCopy();
    }

    @Override
    public IntColumn emptyCopy(final int rowSize) {
        return (IntColumn) super.emptyCopy(rowSize);
    }

    @Override
    public IntColumn copy() {
        return new IntColumn(name(), data.clone());
    }

    @Override
    public int[] asIntArray() {  // TODO: Need to figure out how to handle NaN -> Maybe just use a list with nulls?
        final int[] result = new int[size()];
        for (int i = 0; i < size(); i++) {
            result[i] = getInt(i);
        }
        return result;
    }

    @Override
    public Iterator<Integer> iterator() {
        return data.iterator();
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
    public int compare(Integer o1, Integer o2) {
        return Integer.compare(o1, o2);
    }

    @Override
    public IntColumn set(int i, Integer val) {
        return set(i, (int) val);
    }

    public IntColumn set(int i, int val) {
        data.set(i, val);
        return this;
    }

    @Override
    public IntColumn append(final Column<Integer> column) {
        Preconditions.checkArgument(column.type() == this.type());
        final IntColumn numberColumn = (IntColumn) column;
        for (int i = 0; i < numberColumn.size(); i++) {
            append(numberColumn.getInt(i));
        }
        return this;
    }

    @Override
    public IntColumn appendMissing() {
        return append(IntColumnType.missingValueIndicator());
    }

    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(COLUMN_TYPE.byteSize()).putInt(getInt(rowNumber)).array();
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

    /**
     * Returns the value at the given index. The actual value is returned if the ColumnType is INTEGER. Otherwise the
     * value is rounded as described below.
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

    public boolean isMissingValue(int value) {
        return IntColumnType.isMissingValue(value);
    }

    @Override
    public boolean isMissing(int rowNumber) {
        return isMissingValue(getInt(rowNumber));
    }

    @Override
    public NumberIterator numberIterator() {
        return new NumberIterator(data);
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
    public IntColumn appendObj(Object obj) {
        if (obj == null) {
            return appendMissing();
        }
        if (obj instanceof Integer) {
            return append((int) obj);
        }
        throw new IllegalArgumentException("Could not append " + obj.getClass());
    }

    @Override
    public IntColumn appendCell(final String value) {
        try {
            return append(IntColumnType.DEFAULT_PARSER.parseInt(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Error adding value to column " + name() + ": " + e.getMessage());
        }
    }

    @Override
    public IntColumn appendCell(final String value, StringParser<?> parser) {
        try {
            return append(parser.parseInt(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Error adding value to column " + name()  + ": " + e.getMessage());
        }
    }

}
