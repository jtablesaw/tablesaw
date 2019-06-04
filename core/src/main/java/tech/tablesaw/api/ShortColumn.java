package tech.tablesaw.api;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.columns.numbers.ShortColumnType;
import tech.tablesaw.selection.Selection;

import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.DoublePredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class ShortColumn extends NumberColumn<Short> implements CategoricalColumn<Short> {

    /**
     * Compares two ints, such that a sort based on this comparator would sort in descending order
     */
    private final ShortComparator descendingComparator = (o2, o1) -> (Short.compare(o1, o2));

    private final ShortArrayList data;

    protected ShortColumn(final String name, ShortArrayList data) {
        super(ShortColumnType.instance(), name);
        this.printFormatter = NumberColumnFormatter.ints();
        this.data = data;
    }

    public static ShortColumn create(final String name) {
        return new ShortColumn(name, new ShortArrayList());
    }

    public static ShortColumn create(final String name, final short[] arr) {
        return new ShortColumn(name, new ShortArrayList(arr));
    }

    public static ShortColumn create(final String name, final int initialSize) {
        ShortColumn column = new ShortColumn(name, new ShortArrayList(initialSize));
        for (int i = 0; i < initialSize; i++) {
            column.appendMissing();
        }
        return column;
    }

    @Override
    public ShortColumn createCol(final String name, final int initialSize) {
        return create(name, initialSize);
    }

    @Override
    public ShortColumn createCol(final String name) {
        return create(name);
    }

    public static boolean valueIsMissing(int value) {
        return value == ShortColumnType.missingValueIndicator();
    }

    @Override
    public Short get(int index) {
        return getShort(index);
    }

    public short getShort(int index) {
        return data.getShort(index);
    }

    @Override
    public ShortColumn subset(final int[] rows) {
        final ShortColumn c = this.emptyCopy();
        for (final int row : rows) {
            c.append(getShort(row));
        }
        return c;
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public ShortColumn unique() {
        final ShortSet values = new ShortOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissing(i)) {
                values.add(getShort(i));
            }
        }
        final ShortColumn column = ShortColumn.create(name() + " Unique values");

        for (short value : values) {
            column.append(value);
        }
        return column;
    }

    @Override
    public ShortColumn top(int n) {
        final ShortArrayList top = new ShortArrayList();
        final short[] values = data.toShortArray();
        ShortArrays.parallelQuickSort(values, descendingComparator);
        for (int i = 0; i < n && i < values.length; i++) {
            top.add(values[i]);
        }
        return new ShortColumn(name() + "[Top " + n  + "]", top);
    }

    @Override
    public ShortColumn bottom(final int n) {
        final ShortArrayList bottom = new ShortArrayList();
        final short[] values = data.toShortArray();
        ShortArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(values[i]);
        }
        return new ShortColumn(name() + "[Bottoms " + n  + "]", bottom);
    }

    @Override
    public ShortColumn lag(int n) {
        final int srcPos = n >= 0 ? 0 : 0 - n;
        final short[] dest = new short[size()];
        final int destPos = n <= 0 ? 0 : n;
        final int length = n >= 0 ? size() - n : size() + n;

        for (int i = 0; i < size(); i++) {
            dest[i] = ShortColumnType.missingValueIndicator();
        }

        short[] array = data.toShortArray();

        System.arraycopy(array, srcPos, dest, destPos, length);
        return new ShortColumn(name() + " lag(" + n + ")", new ShortArrayList(dest));
    }

    @Override
    public ShortColumn removeMissing() {
        ShortColumn result = copy();
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

    public ShortColumn append(short i) {
        data.add(i);
        return this;
    }

    public ShortColumn append(Short val) {
        this.append(val.shortValue());
        return this;
    }

    @Override
    public ShortColumn emptyCopy() {
        return (ShortColumn) super.emptyCopy();
    }

    @Override
    public ShortColumn emptyCopy(final int rowSize) {
        return (ShortColumn) super.emptyCopy(rowSize);
    }

    @Override
    public ShortColumn copy() {
        return new ShortColumn(name(), data.clone());
    }

    @Override
    public Iterator<Short> iterator() {
        return data.iterator();
    }

    @Override
    public Short[] asObjectArray() {
        final Short[] output = new Short[size()];
        for (int i = 0; i < size(); i++) {
            output[i] = getShort(i);
        }
        return output;
    }

    @Override
    public int compare(Short o1, Short o2) {
        return Short.compare(o1, o2);
    }

    @Override
    public ShortColumn set(int i, Short val) {
        return set(i, (short) val);
    }

    public ShortColumn set(int i, short val) {
        data.set(i, val);
        return this;
    }

    @Override
    public ShortColumn append(final Column<Short> column) {
        Preconditions.checkArgument(column.type() == this.type());
        final ShortColumn numberColumn = (ShortColumn) column;
        final int size = numberColumn.size();
        for (int i = 0; i < size; i++) {
            append(numberColumn.getShort(i));
        }
        return this;
    }

    @Override
    public String getString(final int row) {
        final short value = getShort(row);
        if (ShortColumnType.isMissingValue(value)) {
            return "";
        }
        return String.valueOf(printFormatter.format(value));
    }

    @Override
    public ShortColumn append(Column<Short> column, int row) {
        Preconditions.checkArgument(column.type() == this.type());
        return append(((ShortColumn) column).getShort(row));
    }

    @Override
    public ShortColumn set(int row, Column<Short> column, int sourceRow) {
        Preconditions.checkArgument(column.type() == this.type());
        return set(row, ((ShortColumn) column).getShort(sourceRow));
    }

    @Override
    public ShortColumn appendMissing() {
        return append(ShortColumnType.missingValueIndicator());
    }

    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(ShortColumnType.instance().byteSize()).putShort(getShort(rowNumber)).array();
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
        return data.getShort(row);
    }

    @Override
    public double getDouble(int row) {
        short value = data.getShort(row);
        if (isMissingValue(value)) {
            return DoubleColumnType.missingValueIndicator();
        }
        return value;
    }

    public boolean isMissingValue(short value) {
        return ShortColumnType.isMissingValue(value);
    }

    @Override
    public boolean isMissing(int rowNumber) {
        return isMissingValue(getShort(rowNumber));
    }

    @Override
    public Column<Short> setMissing(int i) {
        return set(i, ShortColumnType.missingValueIndicator());
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
    public ShortColumn appendObj(Object obj) {
        if (obj == null) {
            return appendMissing();
        }
        if (obj instanceof Short) {
            return append((short) obj);
        }
        throw new IllegalArgumentException("Could not append " + obj.getClass());
    }

    @Override
    public ShortColumn appendCell(final String value) {
        try {
            return append(ShortColumnType.DEFAULT_PARSER.parseShort(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Error adding value to column " + name() + ": " + e.getMessage());
        }
    }

    @Override
    public ShortColumn appendCell(final String value, AbstractColumnParser<?> parser) {
        try {
            return append(parser.parseShort(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Error adding value to column " + name()  + ": " + e.getMessage());
        }
    }

    @Override
    public String getUnformattedString(final int row) {
        final int value = getInt(row);
        if (ShortColumnType.isMissingValue(value)) {
            return "";
        }
        return String.valueOf(value);
    }

    @Override
    public ShortColumn inRange(int start, int end) {
        return (ShortColumn) super.inRange(start, end);
    }

    @Override
    public ShortColumn where(Selection selection) {
        return (ShortColumn) super.where(selection);
    }

    @Override
    public ShortColumn lead(int n) {
        return (ShortColumn) super.lead(n);
    }

    @Override
    public ShortColumn setName(String name) {
        return (ShortColumn) super.setName(name);
    }

    @Override
    public ShortColumn filter(Predicate<? super Short> test) {
        return (ShortColumn) super.filter(test);
    }

    @Override
    public ShortColumn sorted(Comparator<? super Short> comp) {
        return (ShortColumn) super.sorted(comp);
    }

    @Override
    public ShortColumn map(Function<? super Short, ? extends Short> fun) {
        return (ShortColumn) super.map(fun);
    }

    @Override
    public ShortColumn min(Column<Short> other) {
        return (ShortColumn) super.min(other);
    }

    @Override
    public ShortColumn max(Column<Short> other) {
        return (ShortColumn) super.max(other);
    }

    @Override
    public ShortColumn set(Selection condition, Column<Short> other) {
        return (ShortColumn) super.set(condition, other);
    }

    @Override
    public ShortColumn set(Selection rowSelection, Short newValue) {
        return (ShortColumn) super.set(rowSelection, newValue);
    }

    @Override
    public ShortColumn set(DoublePredicate condition, Short newValue) {
        return (ShortColumn) super.set(condition, newValue);
    }

    @Override
    public ShortColumn set(DoublePredicate condition, NumberColumn<Short> other) {
      return (ShortColumn) super.set(condition, other);
    }

    @Override
    public ShortColumn first(int numRows) {
        return (ShortColumn) super.first(numRows);
    }

    @Override
    public ShortColumn last(int numRows) {
        return (ShortColumn) super.last(numRows);
    }

    @Override
    public ShortColumn sampleN(int n) {
        return (ShortColumn) super.sampleN(n);
    }

    @Override
    public ShortColumn sampleX(double proportion) {
        return (ShortColumn) super.sampleX(proportion);
    }

    /**
     * Returns a new LongColumn containing a value for each value in this column
     *
     * A widening primitive conversion from short to long does not lose any information at all;
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
}
