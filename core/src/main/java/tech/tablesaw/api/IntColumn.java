package tech.tablesaw.api;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.IntColumnType;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.selection.Selection;

import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.DoublePredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class IntColumn extends NumberColumn<Integer> implements CategoricalColumn<Integer> {

    /**
     * Compares two ints, such that a sort based on this comparator would sort in descending order
     */
    private final IntComparator descendingComparator = (o2, o1) -> (Integer.compare(o1, o2));

    private final IntArrayList data;

    protected IntColumn(final String name, IntArrayList data) {
        super(IntColumnType.instance(), name);
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
        IntColumn column = new IntColumn(name, new IntArrayList(initialSize));
        for (int i = 0; i < initialSize; i++) {
            column.appendMissing();
        }
        return column;
    }

    @Override
    public IntColumn createCol(final String name, final int initialSize) {
        return create(name, initialSize);
    }

    @Override
    public IntColumn createCol(final String name) {
        return create(name);
    }

    /**
     * Returns a new numeric column initialized with the given name and size. The values in the column are
     * integers beginning at startsWith and continuing through size (exclusive), monotonically increasing by 1
     * TODO consider a generic fill function including steps or random samples from various distributions
     */
    public static IntColumn indexColumn(final String columnName, final int size, final int startsWith) {
        final IntColumn indexColumn = IntColumn.create(columnName, size);
        for (int i = 0; i < size; i++) {
            indexColumn.set(i, i + startsWith);
        }
        return indexColumn;
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public void clear() {
        data.clear();
    }

    public static boolean valueIsMissing(int value) {
        return value == IntColumnType.missingValueIndicator();
    }

    @Override
    public Integer get(int index) {
        return getInt(index);
    }

    @Override
    public IntColumn subset(final int[] rows) {
        final IntColumn c = this.emptyCopy();
        for (final int row : rows) {
            c.append(getInt(row));
        }
        return c;
    }

    @Override
    public IntColumn unique() {
        final IntSet values = new IntOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissing(i)) {
                values.add(getInt(i));
            }
        }
        final IntColumn column = IntColumn.create(name() + " Unique values");
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
    public Iterator<Integer> iterator() {
        return data.iterator();
    }

    @Override
    public Integer[] asObjectArray() {
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
        final int size = numberColumn.size();
        for (int i = 0; i < size; i++) {
            append(numberColumn.getInt(i));
        }
        return this;
    }

    @Override
    public IntColumn append(Column<Integer> column, int row) {
        Preconditions.checkArgument(column.type() == this.type());
        return append(((IntColumn) column).getInt(row));
    }

    @Override
    public IntColumn set(int row, Column<Integer> column, int sourceRow) {
        Preconditions.checkArgument(column.type() == this.type());
        return set(row, ((IntColumn) column).getInt(sourceRow));
    }

    @Override
    public IntColumn appendMissing() {
        return append(IntColumnType.missingValueIndicator());
    }

    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(IntColumnType.instance().byteSize()).putInt(getInt(rowNumber)).array();
    }

    @Override
    public String getString(final int row) {
        final int value = getInt(row);
        if (IntColumnType.isMissingValue(value)) {
            return "";
        }
        return String.valueOf(printFormatter.format(value));
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
     * <ul>
     *   <li>If the argument is NaN, the result is 0.
     *   <li>If the argument is positive infinity or any value greater than or
     *     equal to the value of {@code Integer.MAX_VALUE}, an error will be thrown
     * </ul>
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
    public IntColumn appendCell(final String value, AbstractColumnParser<?> parser) {
        try {
            return append(parser.parseInt(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Error adding value to column " + name()  + ": " + e.getMessage());
        }
    }

    @Override
    public String getUnformattedString(final int row) {
        final int value = getInt(row);
        if (IntColumnType.isMissingValue(value)) {
            return "";
        }
        return String.valueOf(value);
    }

    @Override
    public IntColumn inRange(int start, int end) {
        return (IntColumn) super.inRange(start, end);
    }

    @Override
    public IntColumn where(Selection selection) {
        return (IntColumn) super.where(selection);
    }

    @Override
    public IntColumn lead(int n) {
        return (IntColumn) super.lead(n);
    }

    @Override
    public IntColumn setName(String name) {
        return (IntColumn) super.setName(name);
    }

    @Override
    public IntColumn filter(Predicate<? super Integer> test) {
        return (IntColumn) super.filter(test);
    }

    @Override
    public IntColumn sorted(Comparator<? super Integer> comp) {
        return (IntColumn) super.sorted(comp);
    }

    @Override
    public IntColumn map(Function<? super Integer, ? extends Integer> fun) {
        return (IntColumn) super.map(fun);
    }

    @Override
    public IntColumn min(Column<Integer> other) {
        return (IntColumn) super.min(other);
    }

    @Override
    public IntColumn max(Column<Integer> other) {
        return (IntColumn) super.max(other);
    }

    @Override
    public IntColumn set(Selection condition, Column<Integer> other) {
        return (IntColumn) super.set(condition, other);
    }

    @Override
    public IntColumn set(Selection rowSelection, Integer newValue) {
        return (IntColumn) super.set(rowSelection, newValue);
    }

    @Override
    public IntColumn set(DoublePredicate condition, Integer newValue) {
        return (IntColumn) super.set(condition, newValue);
    }

    @Override
    public IntColumn set(DoublePredicate condition, NumberColumn<Integer> other) {
      return (IntColumn) super.set(condition, other);
    }

    @Override
    public IntColumn first(int numRows) {
        return (IntColumn) super.first(numRows);
    }

    @Override
    public IntColumn last(int numRows) {
        return (IntColumn) super.last(numRows);
    }

    @Override
    public IntColumn sampleN(int n) {
        return (IntColumn) super.sampleN(n);
    }

    @Override
    public IntColumn sampleX(double proportion) {
        return (IntColumn) super.sampleX(proportion);
    }

    /**
     * Returns a new LongColumn containing a value for each value in this column
     *
     * A widening primitive conversion from int to long does not lose any information at all;
     * the numeric value is preserved exactly.
     *
     * A missing value in the receiver is converted to a missing value in the result
     */
    @Override
    public LongColumn asLongColumn() {
        LongArrayList values = new LongArrayList();
        for (int f : data) {
            values.add(f);
        }
        values.trim();
        return LongColumn.create(this.name(), values.elements());
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
    @Override
    public FloatColumn asFloatColumn() {
        FloatArrayList values = new FloatArrayList();
        for (int d : data) {
            values.add(d);
        }
        values.trim();
        return FloatColumn.create(this.name(), values.elements());
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
    @Override
    public DoubleColumn asDoubleColumn() {
        DoubleArrayList values = new DoubleArrayList();
        for (int d : data) {
            values.add(d);
        }
        values.trim();
        return DoubleColumn.create(this.name(), values.elements());
    }

    /**
     * Returns a new ShortColumn containing a value for each value in this column
     *
     * A narrowing conversion of a signed integer to an integral type T simply discards all but the n lowest order bits,
     * where n is the number of bits used to represent type T. In addition to a possible loss of information about
     * the magnitude of the numeric value, this may cause the sign of the resulting value to differ from the sign of
     * the input value.
     *
     * In other words, if the element being converted is larger (or smaller) than Short.MAX_VALUE
     * (or Short.MIN_VALUE) you will not get a conventionally good conversion.
     *
     * Despite the fact that overflow, underflow, or other loss of information may occur, a narrowing primitive
     * conversion never results in a run-time exception.
     *
     * A missing value in the receiver is converted to a missing value in the result
     */
    @Override
    public ShortColumn asShortColumn() {
        ShortArrayList values = new ShortArrayList();
        for (int f : data) {
            values.add((short) f);
        }
        values.trim();
        return ShortColumn.create(this.name(), values.elements());
    }

    public IntColumn setMissing(int r) {
        set(r, IntColumnType.missingValueIndicator());
        return this;
    }
}
