package tech.tablesaw.api;

import static tech.tablesaw.api.ColumnType.DOUBLE;
import static tech.tablesaw.api.ColumnType.FLOAT;
import static tech.tablesaw.api.ColumnType.INTEGER;

import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.function.DoublePredicate;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.columns.numbers.DoubleDataWrapper;
import tech.tablesaw.columns.numbers.FloatDataWrapper;
import tech.tablesaw.columns.numbers.IntDataWrapper;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.columns.numbers.NumberIterator;
import tech.tablesaw.columns.numbers.NumericDataWrapper;
import tech.tablesaw.selection.Selection;

public abstract class NumberColumn<T extends Number> extends AbstractColumn<T> implements NumericColumn<T> {

    protected NumericDataWrapper data;

    protected NumberColumnFormatter printFormatter = new NumberColumnFormatter();

    protected Locale locale;

    protected final IntComparator comparator = new IntComparator() {

        @Override
        public int compare(final int r1, final int r2) {
            final double f1 = getDouble(r1);
            final double f2 = getDouble(r2);
            return Double.compare(f1, f2);
        }
    };

    protected NumberColumn(final ColumnType type, final String name) {
        super(type, name);
    }

    protected NumberColumn(final String name, final DoubleArrayList data) {
        super(DOUBLE, name);
        setDataWrapper(new DoubleDataWrapper(data));
    }

    protected NumberColumn(final String name, final FloatArrayList data) {
        super(FLOAT, name);
        setDataWrapper(new FloatDataWrapper(data));
    }

    protected NumberColumn(final String name, IntArrayList data) {
        super(INTEGER, name);
        this.printFormatter = NumberColumnFormatter.ints();
        setDataWrapper(new IntDataWrapper(data));
    }

    protected NumberColumn(final String name, final NumericDataWrapper data) {
        super(data.type(), name);
        setDataWrapper(data);
    }

    protected abstract NumberColumn<T> createCol(final String name, final NumericDataWrapper data);

    protected void setDataWrapper(NumericDataWrapper wrapper) {
        if (wrapper instanceof IntDataWrapper) {
            printFormatter = NumberColumnFormatter.ints();
        }
        this.data = wrapper;
    }

    @Override
    public boolean isMissing(final int rowNumber) {
        return data.isMissingValue(getDouble(rowNumber));
    }

    public void setPrintFormatter(final NumberFormat format, final String missingValueString) {
        this.printFormatter = new NumberColumnFormatter(format, missingValueString);
    }

    public void setPrintFormatter(final NumberColumnFormatter formatter) {
        this.printFormatter = formatter;
    }

    @Override
    public int size() {
        return data.size();
    }

    /**
     * Returns the largest ("top") n values in the column
     * TODO(lwhite): Consider whether this should exclude missing
     *
     * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
     *          number of observations in the column
     * @return A list, possibly empty, of the largest observations
     */
    public NumericColumn<T> top(final int n) {
        return createCol(name() + "[Top " + n  + "]", data.top(n));
    }

    /**
     * Returns the smallest ("bottom") n values in the column
     * TODO(lwhite): Consider whether this should exclude missing
     *
     * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
     *          number of observations in the column
     * @return A list, possibly empty, of the smallest n observations
     */
    public NumericColumn<T> bottom(final int n) {
        return createCol(name() + "[Bottoms " + n  + "]", data.bottom(n));
    }

    @Override
    public String getString(final int row) {
        final double value = getDouble(row);
        if (data.isMissingValue(value)) {
            return "";
        }
        return String.valueOf(printFormatter.format(value));
    }

    @Override
    public double getDouble(final int row) {
        return data.getDouble(row);
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
    public int getInt(final int row) {
        return data.getInt(row);
    }

    /**
     * Returns a float representation of the data at the given index. Some precision may be lost, and if the value is
     * to large to be cast to a float, an exception is thrown.
     *
     * @throws  ClassCastException if the value can't be cast to ta float
     */
    public float getFloat(final int index) {
        return data.getFloat(index);
    }

    @Override
    public NumberColumn<T> emptyCopy() {
        final NumberColumn<T> column = createCol(name(), data.emptyCopy(DEFAULT_ARRAY_SIZE));
        column.setPrintFormatter(printFormatter);
        column.locale = locale;
        return column;
    }

    @Override
    public NumberColumn<T> emptyCopy(final int rowSize) {
        final NumberColumn<T> column = createCol(name(), data.emptyCopy(rowSize));
        column.setPrintFormatter(printFormatter);
        column.locale = locale;
        return column;
    }

    @Override
    public NumberColumn<T> copy() {
        return createCol(name(), data.copy());
    }

    @Override
    public void clear() {
        data.clear();
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
    public NumberColumn<T> appendCell(final String object) {
        try {
            data.appendCell(object);
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Error adding value to column " + name() + ": " + e.getMessage());
        }
        return this;
    }

    @Override
    public NumberColumn<T> appendCell(final String object, StringParser<?> parser) {
        try {
            data.appendCell(object, parser);
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Error adding value to column " + name()  + ": " + e.getMessage());
        }
        return this;
    }

    /**
     * Returns the rounded value as an int
     *
     * @throws ClassCastException if the returned value will not fit in an int
     */
    public Integer roundInt(final int i) {
        return getInt(i);
    }

    /**
     * Returns the value of the ith element rounded to the nearest long
     *
     * @param i the index in the column
     * @return the value at i, rounded to the nearest integer
     */
    public long getLong(final int i) {
        final double value = getDouble(i);
        return data.isMissingValue(value) ? DateTimeColumn.MISSING_VALUE : Math.round(value);
    }

    /**
     * Compares the given ints, which refer to the indexes of the doubles in this column, according to the values of the
     * doubles themselves
     */
    @Override
    public IntComparator rowComparator() {
        return comparator;
    }

    public NumberColumn<T> set(final int r, final double value) {
        data.set(r, value);
        return this;
    }

    public NumberColumn<T> set(final int r, final int value) {
        data.set(r, value);
        return this;
    }

    public NumberColumn<T> set(final int r, final float value) {
        data.set(r, value);
        return this;
    }

    /**
     * Conditionally update this column, replacing current values with newValue for all rows where the current value
     * matches the selection criteria
     * <p>
     * Example:
     * myColumn.set(4.0, myColumn.valueIsMissing()); // no more missing values
     */
    public NumberColumn<T> set(final Selection rowSelection, final double newValue) {
        for (final int row : rowSelection) {
            set(row, newValue);
        }
        return this;
    }

    public boolean contains(final double value) {
        return data.contains(value);
    }

    public boolean contains(final int value) {
        return data.contains(value);
    }

    @Override
    public int byteSize() {
        return type().byteSize();
    }

    /**
     * Returns the contents of the cell at rowNumber as a byte[]
     */
    @Override
    public byte[] asBytes(final int rowNumber) {
        return data.asBytes(rowNumber);
    }

    @Override
    public NumberColumn<T> appendMissing() {
        data.appendMissing();
        return this;
    }

    /**
     * Returns the count of missing values in this column
     */
    @Override
    public int countMissing() {
        return data.countMissing();
    }

    /**
     * Returns a DateTimeColumn where each value is the LocalDateTime represented by the values in this column
     * <p>
     * The values in this column must be longs that represent the time in milliseconds from the epoch as in standard
     * Java date/time calculations
     *
     * @param offset The ZoneOffset to use in the calculation
     * @return A column of LocalDateTime values
     */
    public DateTimeColumn asDateTimes(ZoneOffset offset) {
        DateTimeColumn column = DateTimeColumn.create(name() + ": date time");
        NumberIterator it = numberIterator();
        while (it.hasNext()) {
            double d = it.next();
            LocalDateTime dateTime =
                    Instant.ofEpochMilli((long) d).atZone(offset).toLocalDateTime();
            column.append(dateTime);
        }
        return column;
    }

    /**
     * Returns the number of unique values in this column, excluding missing values
     */
    @Override
    public int countUnique() {
        return data.countUnique();
    }

    @Override
    public NumberColumn<T> appendObj(Object obj) {
        data.appendObj(obj);
        return this;
    }

    @Override
    public NumberColumn<T> lag(final int n) {
        return createCol(name() + " lag(" + n + ")", data.lag(n));
    }

    @Override
    public NumberColumn<T> removeMissing() {
        return createCol(name(), data.removeMissing());
    }

    @Override
    public NumberIterator numberIterator() {
        return data.numberIterator();
    }

    public IntSet asIntegerSet() {
        final IntSet ints = new IntOpenHashSet();
        NumberIterator it = numberIterator();
        while (it.hasNext()) {
            double d = it.next();
            if (!data.isMissingValue(d)) {
                ints.add((int) Math.round(d));
            }
        }
        return ints;
    }

    @Override
    public Object[] asObjectArray() {
        final Double[] output = new Double[size()];
        for (int i = 0; i < size(); i++) {
            output[i] = getDouble(i);
        }
        return output;
    }

    /**
     * Returns true if all rows satisfy the predicate, false otherwise
     *
     * @param test the predicate
     * @return true if all rows satisfy the predicate, false otherwise
     */
    public boolean allMatch(DoublePredicate test) {
        return count(test.negate(), 1) == 0;
    }

    /**
     * Returns true if any row satisfies the predicate, false otherwise
     *
     * @param test the predicate
     * @return true if any rows satisfies the predicate, false otherwise
     */
    public boolean anyMatch(DoublePredicate test) {
        return count(test, 1) > 0;
    }

    /**
     * Returns true if no row satisfies the predicate, false otherwise
     *
     * @param test the predicate
     * @return true if no row satisfies the predicate, false otherwise
     */
    public boolean noneMatch(DoublePredicate test) {
        return count(test, 1) == 0;
    }

}
