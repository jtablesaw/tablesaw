package tech.tablesaw.api;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.AbstractParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.ByteDataWrapper;
import tech.tablesaw.columns.numbers.IntColumnType;
import tech.tablesaw.columns.numbers.IntDataWrapper;
import tech.tablesaw.columns.numbers.IntegerDataWrapper;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.columns.numbers.NumberOutOfRangeException;
import tech.tablesaw.columns.numbers.ShortDataWrapper;
import tech.tablesaw.selection.Selection;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

public class IntegerColumn extends NumberColumn<Integer> implements CategoricalColumn<Integer> {

    private static final IntColumnType COLUMN_TYPE = ColumnType.INTEGER;

    private IntegerDataWrapper data;

    private IntegerColumn(final String name, IntArrayList data) {
        super(COLUMN_TYPE, name, data);
        this.printFormatter = NumberColumnFormatter.ints();
        this.data = IntDataWrapper.create(data.toIntArray());
    }

    private IntegerColumn(String name, IntegerDataWrapper copy) {
        super(ColumnType.INTEGER, name);
        this.printFormatter = NumberColumnFormatter.ints();
        this.data = copy;
    }

    public static IntegerColumn create(final String name) {
        return new IntegerColumn(name, ByteDataWrapper.create(AbstractColumn.DEFAULT_ARRAY_SIZE));
    }

    public static IntegerColumn create(final String name, final int[] arr) {
        return new IntegerColumn(name, new IntArrayList(arr));
    }

    public static IntegerColumn create(final String name, final int initialSize) {
        return new IntegerColumn(name, ByteDataWrapper.create(initialSize));
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
        return new IntegerColumn(name(), data.top(n));
    }

    @Override
    public IntegerColumn bottom(final int n) {
        return new IntegerColumn(name(), data.bottom(n));
    }

    @Override
    public IntegerColumn lag(int n) {
        return new IntegerColumn(name() + " lag(" + n + ")", data.lag(n));
    }

    @Override
    public IntegerColumn removeMissing() {
        return new IntegerColumn(name(), data.removeMissing());
    }

    @Override
    public int size() {
        return data.size();
    }

    public boolean contains(int value) {
        return data.contains(value);
    }

    public void append(int i) {
        if (data instanceof ByteDataWrapper) {
            if (i > Byte.MAX_VALUE) {
                promoteColumnType(i);
            } else {
                try {
                    data.append(i);
                    return;
                } catch (NumberOutOfRangeException e) {
                    // should never get here
                    throw new RuntimeException(e);
                }
            }
        }
        if (data instanceof ShortDataWrapper) {
            if (i > Short.MAX_VALUE) {
                promoteColumnType(i);
            } else {
                try {
                    data.append(i);
                    return;
                } catch (NumberOutOfRangeException e) {
                    // should never get here
                    throw new RuntimeException(e);
                }
            }
        }
        if (data instanceof IntDataWrapper) {
            try {
                data.append(i);
            } catch (NumberOutOfRangeException e) {
                // this shouldn't happen if promotion logic is correct
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Unsupported column type");
        }
    }

    @Override
    public void append(long value) {
        append((int) value);
    }

    public IntegerColumn append(Integer val) {
        append(val.intValue());
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
        if (data instanceof IntDataWrapper
                || data instanceof ShortDataWrapper
                || data instanceof ByteDataWrapper) {
            return new IntegerColumn(name(), data.copy());
        }
        throw new RuntimeException("Unexpected data wrapper type for Integer Column");
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
    public IntegerColumn set(int i, Integer val) {
        try {
            if (data instanceof IntDataWrapper) {
                data.set(i, val);
            } else if (data instanceof ShortDataWrapper) {
                data.set(i, (short) val.intValue());
            } else if (data instanceof ByteDataWrapper) {
                data.set(i, (byte) val.intValue());
            } else {
                throw new IllegalArgumentException("Could not set int " + val);
            }
        } catch (NumberOutOfRangeException ex) {
            promoteColumnType(ex.getParsedValue().intValue());
        }
        return this;
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
        data.appendMissing();
        return this;
    }

    @Override
    public byte[] asBytes(int rowNumber) {
        return data.asBytes(rowNumber);
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
     * <p>
     * Returns the closest {@code int} to the argument, with ties
     * rounding to positive infinity.
     *
     * <p>
     * Special cases:
     * <ul><li>If the argument is NaN, the result is 0.
     * <li>If the argument is positive infinity or any value greater than or
     * equal to the value of {@code Integer.MAX_VALUE}, an error will be thrown
     *
     * @param row the index of the value to be rounded to an integer.
     * @return the value of the argument rounded to the nearest
     * {@code int} value.
     * @throws ClassCastException if the absolute value of the value to be rounded is too large to be cast to an int
     */
    public int getInt(int row) {
        return data.getInt(row);
    }

    public short getShort(int row) {
        return data.getShort(row);
    }

    public short getByte(int row) {
        return data.getByte(row);
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
        if (obj instanceof Short) {
            append((short) obj);
            return this;
        }
        if (obj instanceof Byte) {
            append((byte) obj);
            return this;
        }
        throw new IllegalArgumentException("Could not append " + obj.getClass());
    }

    @Override
    public IntegerColumn appendCell(final String value) {
        try {
            data.appendCell(value);
        } catch (final NumberOutOfRangeException e) {
            Number parsedValue = e.getParsedValue();
            if (parsedValue != null) {
                promoteColumnType(parsedValue.intValue());
            }
            try {
                data.appendCell(value);
            } catch (NumberOutOfRangeException e2) {
                // this shouldn't happen
                throw new RuntimeException(e2);
            }
        }
        return this;
    }

    @Override
    public IntegerColumn appendCell(final String value, AbstractParser<?> parser) {
        try {
            data.appendCell(value, parser);
        } catch (final NumberOutOfRangeException e) {
            Number parsedValue = e.getParsedValue();
            if (parsedValue != null) {
                promoteColumnType(parsedValue.intValue());
            }
            try {
                data.appendCell(value, parser);
            } catch (NumberOutOfRangeException e2) {
                // this shouldn't happen
                throw new RuntimeException(e2);
            }
        }
        return this;
    }

    private void promoteColumnType(int valueCausingPromotion) {
        if (data instanceof ShortDataWrapper) {
            ShortDataWrapper shorts = (ShortDataWrapper) data;
            IntDataWrapper ints = IntDataWrapper.create(shorts.size());
            for (int s : shorts) {
                if (shorts.isMissingValue(s)) {
                    ints.appendMissing();
                }
                ints.append(s);
            }
            data = ints;
        }

        if (data instanceof ByteDataWrapper) {
            promoteByteDataWrapper(valueCausingPromotion);
        }
    }

    private void promoteByteDataWrapper(int valueCausingPromotion) {
        try {
            ByteDataWrapper bytes = (ByteDataWrapper) data;
            if (valueCausingPromotion <= Short.MAX_VALUE) {
                ShortDataWrapper shorts = ShortDataWrapper.create(bytes.size());
                for (int s : bytes) {
                    if (bytes.isMissingValue(s)) {
                        shorts.appendMissing();
                    }
                    shorts.append(s);
                }
                data = shorts;
            } else {
                IntDataWrapper ints = IntDataWrapper.create(bytes.size());
                for (int s : bytes) {
                    if (bytes.isMissingValue(s)) {
                        ints.appendMissing();
                    }
                    ints.append(s);
                }
                data = ints;
            }
        } catch (NumberOutOfRangeException e) {
            // this shouldn't happen if the promotion logic is correct
            throw new RuntimeException(e);
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

    public IntegerColumn setMissing(Selection condition) {
        data.setMissing(condition);
        return this;
    }

    /**
     * Returns a new LongColumn containing a value for each value in this column
     * <p>
     * A widening primitive conversion from int to long does not lose any information at all;
     * the numeric value is preserved exactly.
     * <p>
     * A missing value in the receiver is converted to a missing value in the result
     */
    @Override
    public LongColumn asLongColumn() {

        LongArrayList values = new LongArrayList();
        for (int i = 0; i < size(); i++) {
            values.add(getInt(i));
        }
        values.trim();
        return LongColumn.create(this.name(), values.elements());
    }

    /**
     * Returns a new FloatColumn containing a value for each value in this column, truncating if necessary.
     * <p>
     * A widening primitive conversion from an int to a float does not lose information about the overall magnitude
     * of a numeric value. It may, however, result in loss of precision - that is, the result may lose some of the
     * least significant bits of the value. In this case, the resulting floating-point value will be a correctly
     * rounded version of the integer value, using IEEE 754 round-to-nearest mode.
     * <p>
     * Despite the fact that a loss of precision may occur, a widening primitive conversion never results in a
     * run-time exception.
     * <p>
     * A missing value in the receiver is converted to a missing value in the result
     */
    @Override
    public FloatColumn asFloatColumn() {
        FloatArrayList values = new FloatArrayList();
        for (int i = 0; i < size(); i++) {
            values.add(getInt(i));
        }
        values.trim();
        return FloatColumn.create(this.name(), values.elements());
    }

    /**
     * Returns a new DoubleColumn containing a value for each value in this column, truncating if necessary.
     * <p>
     * A widening primitive conversion from an int to a double does not lose information about the overall magnitude
     * of a numeric value. It may, however, result in loss of precision - that is, the result may lose some of the
     * least significant bits of the value. In this case, the resulting floating-point value will be a correctly
     * rounded version of the integer value, using IEEE 754 round-to-nearest mode.
     * <p>
     * Despite the fact that a loss of precision may occur, a widening primitive conversion never results in a
     * run-time exception.
     * <p>
     * A missing value in the receiver is converted to a missing value in the result
     */
    @Override
    public DoubleColumn asDoubleColumn() {
        DoubleArrayList values = new DoubleArrayList();
        for (int i = 0; i < size(); i++) {
            values.add(getInt(i));
        }
        values.trim();
        return DoubleColumn.create(this.name(), values.elements());
    }
}
