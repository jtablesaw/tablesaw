package tech.tablesaw.api;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Iterator;

import com.google.common.base.Preconditions;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.LongColumnType;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.columns.numbers.NumberIterator;

public class LongColumn extends NumberColumn<Long> implements NumericColumn<Long>, CategoricalColumn<Long> {

    private static final LongColumnType COLUMN_TYPE = ColumnType.LONG;

    /**
     * Compares two ints, such that a sort based on this comparator would sort in descending order
     */
    private final LongComparator descendingComparator = (o2, o1) -> (Long.compare(o1, o2));

    private final LongArrayList data;

    protected LongColumn(final String name, LongArrayList data) {
        super(COLUMN_TYPE, name, data);
        this.printFormatter = NumberColumnFormatter.ints();
        this.data = data;
    }

    public static LongColumn create(final String name) {
        return new LongColumn(name, new LongArrayList());
    }

    public static LongColumn create(final String name, final long[] arr) {
        return new LongColumn(name, new LongArrayList(arr));
    }

    public static LongColumn create(final String name, final int initialSize) {
        return new LongColumn(name, new LongArrayList(initialSize));
    }

    @Override
    public LongColumn createCol(final String name, final int initialSize) {
        return create(name, initialSize);
    }

    /**
     * Returns a new numeric column initialized with the given name and size. The values in the column are
     * integers beginning at startsWith and continuing through size (exclusive), monotonically increasing by 1
     * TODO consider a generic fill function including steps or random samples from various distributions
     */
    public static LongColumn indexColumn(final String columnName, final int size, final int startsWith) {
        final LongColumn indexColumn = LongColumn.create(columnName, size);
        for (int i = 0; i < size; i++) {
            indexColumn.append(i + startsWith);
        }
        return indexColumn;
    }

    public static boolean valueIsMissing(long value) {
        return value == LongColumnType.missingValueIndicator();
    }

    @Override
    public Long get(int index) {
        return getLong(index);
    }

    @Override
    public LongColumn unique() {
        final LongSet values = new LongOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissing(i)) {
                values.add(getLong(i));
            }
        }
        final LongColumn column = LongColumn.create(name() + " Unique values", values.size());
        for (long value : values) {
            column.append(value);
        }
        return column;
    }

    @Override
    public LongColumn top(int n) {
        final LongArrayList top = new LongArrayList();
        final long[] values = data.toLongArray();
        LongArrays.parallelQuickSort(values, descendingComparator);
        for (int i = 0; i < n && i < values.length; i++) {
            top.add(values[i]);
        }
        return new LongColumn(name() + "[Top " + n  + "]", top);
    }

    @Override
    public LongColumn bottom(final int n) {
        final LongArrayList bottom = new LongArrayList();
        final long[] values = data.toLongArray();
        LongArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(values[i]);
        }
        return new LongColumn(name() + "[Bottoms " + n  + "]", bottom);
    }

    @Override
    public LongColumn lag(int n) {
        final int srcPos = n >= 0 ? 0 : 0 - n;
        final long[] dest = new long[size()];
        final int destPos = n <= 0 ? 0 : n;
        final int length = n >= 0 ? size() - n : size() + n;

        for (int i = 0; i < size(); i++) {
            dest[i] = LongColumnType.missingValueIndicator();
        }

        long[] array = data.toLongArray();

        System.arraycopy(array, srcPos, dest, destPos, length);
        return new LongColumn(name() + " lag(" + n + ")", new LongArrayList(dest));
    }

    @Override
    public LongColumn removeMissing() {
        LongColumn result = copy();
        result.clear();
        LongListIterator iterator = data.iterator();
        while (iterator.hasNext()) {
            final long v = iterator.nextLong();
            if (!isMissingValue(v)) {
                result.append(v);
            }
        }
        return result;
    }

    public LongColumn append(long i) {
        data.add(i);
        return this;
    }

    public LongColumn append(Long val) {
        this.append(val.longValue());
        return this;
    }

    @Override
    public LongColumn emptyCopy() {
        return (LongColumn) super.emptyCopy();
    }

    @Override
    public LongColumn emptyCopy(final int rowSize) {
        return (LongColumn) super.emptyCopy(rowSize);
    }

    @Override
    public LongColumn copy() {
        return new LongColumn(name(), data.clone());
    }

    public long[] asLongArray() {  // TODO: Need to figure out how to handle NaN -> Maybe just use a list with nulls?
        final long[] result = new long[size()];
        for (int i = 0; i < size(); i++) {
            result[i] = getLong(i);
        }
        return result;
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
            column.append(Instant.ofEpochMilli(getLong(i)).atZone(offset).toLocalDateTime());
        }
        return column;
    }

    @Override
    public Iterator<Long> iterator() {
        return data.iterator();
    }

    @Override
    public Object[] asObjectArray() {
        final Long[] output = new Long[size()];
        for (int i = 0; i < size(); i++) {
            output[i] = getLong(i);
        }
        return output;
    }

    @Override
    public int compare(Long o1, Long o2) {
        return Long.compare(o1, o2);
    }

    @Override
    public LongColumn set(int i, Long val) {
        return set(i, (long) val);
    }

    public LongColumn set(int i, long val) {
        data.set(i, val);
        return this;
    }

    @Override
    public LongColumn append(final Column<Long> column) {
        Preconditions.checkArgument(column.type() == this.type());
        final LongColumn numberColumn = (LongColumn) column;
        for (int i = 0; i < numberColumn.size(); i++) {
            append(numberColumn.getLong(i));
        }
        return this;
    }

    @Override
    public LongColumn appendMissing() {
        return append(LongColumnType.missingValueIndicator());
    }

    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(COLUMN_TYPE.byteSize()).putLong(getLong(rowNumber)).array();
    }

    @Override
    public int countUnique() {
        LongSet uniqueElements = new LongOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissingValue(getLong(i))) {
                uniqueElements.add(getLong(i));
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
    public long getLong(int row) {
        return data.getLong(row);
    }

    @Override
    public double getDouble(int row) {
        long value = data.getLong(row);
        if (isMissingValue(value)) {
            return DoubleColumnType.missingValueIndicator();
        }
        return value;
    }

    public boolean isMissingValue(long value) {
        return LongColumnType.isMissingValue(value);
    }

    @Override
    public boolean isMissing(int rowNumber) {
        return isMissingValue(getLong(rowNumber));
    }

    @Override
    public NumberIterator numberIterator() {
        return new NumberIterator(data);
    }

    @Override
    public void sortAscending() {
        LongArrays.parallelQuickSort(data.elements());
    }

    @Override
    public void sortDescending() {
        LongArrays.parallelQuickSort(data.elements(), descendingComparator);
    }

    @Override
    public LongColumn appendObj(Object obj) {
        if (obj == null) {
            return appendMissing();
        }
        if (obj instanceof Long) {
            return append((long) obj);
        }
        throw new IllegalArgumentException("Could not append " + obj.getClass());
    }

    @Override
    public LongColumn appendCell(final String value) {
        try {
            return append(LongColumnType.DEFAULT_PARSER.parseLong(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Error adding value to column " + name() + ": " + e.getMessage());
        }
    }

    @Override
    public LongColumn appendCell(final String value, StringParser<?> parser) {
        try {
            return append(parser.parseInt(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Error adding value to column " + name()  + ": " + e.getMessage());
        }
    }

    public long firstElement() {
        if (size() > 0) {
            return getLong(0);
        }
        return (Long) COLUMN_TYPE.getMissingValueIndicator();
    }

}
