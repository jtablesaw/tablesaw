package tech.tablesaw.api;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import tech.tablesaw.columns.AbstractParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.IntColumnType;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.columns.numbers.NumberOutOfRangeException;
import tech.tablesaw.selection.Selection;

import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

public class IntegerColumn extends NumberColumn<Integer> implements CategoricalColumn<Integer> {

    private static final IntColumnType COLUMN_TYPE = ColumnType.INTEGER;

    private NumericColumn<? extends Number> data;

    private IntegerColumn(final String name, IntArrayList data) {
        super(COLUMN_TYPE, name, data);
        this.printFormatter = NumberColumnFormatter.ints();
        this.data = IntColumn.create(name, data.toIntArray());
    }

    private IntegerColumn(String name, IntColumn copy) {
        super(ColumnType.INTEGER, name);
        this.data = copy;
    }

    private IntegerColumn(String name, ShortColumn copy) {
        super(ColumnType.INTEGER, name);
        this.data = copy;
    }

    public static IntegerColumn create(final String name) {
        return new IntegerColumn(name, ShortColumn.create(name));
    }

    public static IntegerColumn create(final String name, final int[] arr) {
        return new IntegerColumn(name, new IntArrayList(arr));
    }

    public static IntegerColumn create(final String name, final int initialSize) {
        return new IntegerColumn(name, new IntArrayList(initialSize));
    }

    @Override
    public IntegerColumn createCol(final String name, final int initialSize) {
        return create(name, initialSize);
    }

    /**
     * Returns a new numeric column initialized with the given name and size. The values in the column are
     * integers beginning at startsWith and continuing through size (exclusive), monotonically increasing by 1
     * TODO consider a generic fill function including steps or random samples from various distributions
     */
    public static IntegerColumn indexColumn(final String columnName, final int size, final int startsWith) {
        final IntegerColumn indexColumn = IntegerColumn.create(columnName, size);
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
    public IntegerColumn subset(final int[] rows) {
        final IntegerColumn c = this.emptyCopy();
        for (final int row : rows) {
            c.append(getInt(row));
        }
        return c;
    }

    @Override
    public IntegerColumn unique() {
        final IntSet values = new IntOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissing(i)) {
                values.add(getInt(i));
            }
        }
        final IntegerColumn column = IntegerColumn.create(name() + " Unique values", values.size());
        for (int value : values) {
            column.append(value);
        }
        return column;
    }

    @Override
    public Selection isMissing() {
        return data.isMissing();
    }

    @Override
    public IntegerColumn top(int n) {
        return (IntegerColumn) data.top(n);
    }

    @Override
    public IntegerColumn bottom(final int n) {
        return (IntegerColumn) data.bottom(n);
    }

    @Override
    public IntegerColumn lag(int n) {
        return (IntegerColumn) data.lag(n);
    }

    @Override
    public IntegerColumn removeMissing() {
        return (IntegerColumn) data.removeMissing();
    }

    @Override
    public int size() {
        return data.size();
    }

    public void append(int i) {
        if (data instanceof IntColumn) {
            ((IntColumn) data).append(i);
        } else if (data instanceof ShortColumn) {
            ((ShortColumn) data).append((short) i);
        } else {
            throw new RuntimeException("Unsupported column type");
        }
    }

    @Override
    public void append(short value) {
        data.append(value);
    }

    @Override
    public void append(byte value) {
        data.append(value);
    }

    @Override
    public void append(long value) {
        data.append(value);
    }

    public IntegerColumn append(Integer val) {
        this.append(val.intValue());
        return this;
    }

    @Override
    public IntegerColumn emptyCopy() {
        return (IntegerColumn) super.emptyCopy();
    }

    @Override
    public IntegerColumn emptyCopy(final int rowSize) {
        return (IntegerColumn) super.emptyCopy(rowSize);
    }

    @Override
    public IntegerColumn copy() {
        if (data instanceof IntColumn) {
            return new IntegerColumn(name(), (IntColumn) data.copy());
        }
        if (data instanceof ShortColumn) {
            return new IntegerColumn(name(), (ShortColumn) data.copy());
        }
        throw new RuntimeException("Unexpected column type");
    }

    @Override
    public Iterator<Integer> iterator() {
        return (Iterator<Integer>) data.iterator();
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
    public IntegerColumn set(int i, Integer val) {
        if (data instanceof IntColumn)  {
            ((IntColumn) data).set(i, val);
            return this;
        }
        if (data instanceof ShortColumn) {
            ((ShortColumn) data).set(i, (short) val.intValue());
            return this;
        }
        throw new IllegalArgumentException("Could not set int " + val);
    }

    @Override
    public IntegerColumn append(final Column<Integer> column) {
        Preconditions.checkArgument(column.type() == this.type());
        final IntegerColumn numberColumn = (IntegerColumn) column;
        for (int i = 0; i < numberColumn.size(); i++) {
            append(numberColumn.getInt(i));
        }
        return this;
    }

    @Override
    public IntegerColumn append(Column<Integer> column, int row) {
        Preconditions.checkArgument(column.type() == this.type());
        append(((IntegerColumn) column).getInt(row));
        return this;
    }

    @Override
    public IntegerColumn appendMissing() {
        append(IntColumnType.missingValueIndicator());
        return this;
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
        return (Integer) data.get(row);
    }

    @Override
    public double getDouble(int row) {
        return data.getDouble(row);
    }

    public boolean isMissingValue(int value) {
        return IntColumnType.isMissingValue(value);
    }

    @Override
    public boolean isMissing(int rowNumber) {
        return data.isMissing(rowNumber);
    }

    @Override
    public void sortAscending() {
        data.sortAscending();
    }

    @Override
    public void sortDescending() {
        data.sortDescending();
    }

    @Override
    public IntegerColumn appendObj(Object obj) {
        if (obj == null) {
            return appendMissing();
        }
        if (obj instanceof Integer) {
            append((int) obj);
            return this;
        }
        throw new IllegalArgumentException("Could not append " + obj.getClass());
    }

    @Override
    public IntegerColumn appendCell(final String value) {
        try {
            data.appendCell(value);
            return this;
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Error adding value to column " + name() + ": " + e.getMessage());
        }
    }

    @Override
    public IntegerColumn appendCell(final String value, AbstractParser<?> parser) {
        try {
            data.appendCell(value, parser);
            return this;
        } catch (final NumberOutOfRangeException e) {
            ColumnType failingType = e.getFailingType();
            String inputString = e.getInputValue();
            Long parsedValue = e.getParsedValue();

            if (parsedValue != null) {
                promoteColumnType();
            }
            data.appendCell(value, parser);
        }
        return this;
    }

    private void promoteColumnType() {
        if (data instanceof ShortColumn) {
            ShortColumn shorts = (ShortColumn) data;
            IntColumn column = IntColumn.create(name());
            for (short s : shorts) {
                column.append(s);
            }
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
    public IntegerColumn inRange(int start, int end) {
        return (IntegerColumn) super.inRange(start, end);
    }

    @Override
    public IntegerColumn where(Selection selection) {
        return (IntegerColumn) super.where(selection);
    }

    @Override
    public IntegerColumn lead(int n) {
        return (IntegerColumn) super.lead(n);
    }

    @Override
    public IntegerColumn setName(String name) {
        return (IntegerColumn) super.setName(name);
    }

    @Override
    public IntegerColumn filter(Predicate<? super Integer> test) {
        return (IntegerColumn) super.filter(test);
    }

    @Override
    public IntegerColumn sorted(Comparator<? super Integer> comp) {
        return (IntegerColumn) super.sorted(comp);
    }

    @Override
    public IntegerColumn map(Function<? super Integer, ? extends Integer> fun) {
        return (IntegerColumn) super.map(fun);
    }

    @Override
    public IntegerColumn min(Column<Integer> other) {
        return (IntegerColumn) super.min(other);
    }

    @Override
    public IntegerColumn max(Column<Integer> other) {
        return (IntegerColumn) super.max(other);
    }

    @Override
    public IntegerColumn set(Selection condition, Column<Integer> other) {
        return (IntegerColumn) super.set(condition, other);
    }

    @Override
    public IntegerColumn set(Selection rowSelection, Integer newValue) {
        return (IntegerColumn) super.set(rowSelection, newValue);
    }

    @Override
    public IntegerColumn first(int numRows) {
        return (IntegerColumn) super.first(numRows);
    }

    @Override
    public IntegerColumn last(int numRows) {
        return (IntegerColumn) super.last(numRows);
    }

    @Override
    public IntegerColumn sampleN(int n) {
        return (IntegerColumn) super.sampleN(n);
    }

    @Override
    public IntegerColumn sampleX(double proportion) {
        return (IntegerColumn) super.sampleX(proportion);
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
        return data.asLongColumn();
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
        return data.asFloatColumn();
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
        return data.asDoubleColumn();
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
        return data.asShortColumn();
    }
}
