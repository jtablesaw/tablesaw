package tech.tablesaw.api;

import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.function.DoublePredicate;

import it.unimi.dsi.fastutil.ints.IntComparator;
import tech.tablesaw.columns.AbstractColumn;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;

public abstract class NumberColumn<T extends Number> extends AbstractColumn<T> implements NumericColumn<T> {

    protected NumberColumnFormatter printFormatter = new NumberColumnFormatter();

    protected Locale locale;

    private List<T> data;

    protected final IntComparator comparator = new IntComparator() {

        @Override
        public int compare(final int r1, final int r2) {
            final double f1 = getDouble(r1);
            final double f2 = getDouble(r2);
            return Double.compare(f1, f2);
        }
    };

    protected NumberColumn(final ColumnType type, final String name, List<T> data) {
        super(type, name);
        this.data = data;
    }

    protected abstract NumberColumn<T> createCol(final String name, int size);

    public void setPrintFormatter(final NumberFormat format, final String missingValueString) {
        this.printFormatter = new NumberColumnFormatter(format, missingValueString);
    }

    public void setPrintFormatter(final NumberColumnFormatter formatter) {
        this.printFormatter = formatter;
    }

    /**
     * Returns the largest ("top") n values in the column
     * TODO(lwhite): Consider whether this should exclude missing
     *
     * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
     *          number of observations in the column
     * @return A list, possibly empty, of the largest observations
     */
    public abstract NumericColumn<T> top(final int n);

    /**
     * Returns the smallest ("bottom") n values in the column
     * TODO(lwhite): Consider whether this should exclude missing
     *
     * @param n The maximum number of records to return. The actual number will be smaller if n is greater than the
     *          number of observations in the column
     * @return A list, possibly empty, of the smallest n observations
     */
    public abstract NumericColumn<T> bottom(final int n);

    @Override
    public String getString(final int row) {
        final double value = getDouble(row);
        if (DoubleColumnType.isMissingValue(value)) {
            return "";
        }
        return String.valueOf(printFormatter.format(value));
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public NumberColumn<T> emptyCopy() {
        final NumberColumn<T> column = createCol(name(), DEFAULT_ARRAY_SIZE);
        column.setPrintFormatter(printFormatter);
        column.locale = locale;
        return column;
    }

    @Override
    public NumberColumn<T> emptyCopy(final int rowSize) {
        final NumberColumn<T> column = createCol(name(), rowSize);
        column.setPrintFormatter(printFormatter);
        column.locale = locale;
        return column;
    }

    public abstract NumberColumn<T> copy();

    /**
     * Compares the given ints, which refer to the indexes of the doubles in this column, according to the values of the
     * doubles themselves
     */
    @Override
    public IntComparator rowComparator() {
        return comparator;
    }

    @Override
    public void clear() {
        data.clear();
    }
    
    public boolean contains(T value) {
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
    public abstract byte[] asBytes(final int rowNumber);

    @Override
    public abstract NumberColumn<T> appendMissing();

    /**
     * Returns the count of missing values in this column
     */
    @Override
    public int countMissing() {
        int count = 0;
        for (int i = 0; i < size(); i++) {
            if (isMissing(i)) {
                count++;
            }
        }
        return count;
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
        for (int i = 0; i < size(); i++) {
            column.append(Instant.ofEpochMilli((long) getDouble(i)).atZone(offset).toLocalDateTime());
        }
        return column;
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
