package tech.tablesaw.api;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.doubles.DoubleOpenHashSet;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.FloatColumnType;
import tech.tablesaw.columns.numbers.NumberFillers;
import tech.tablesaw.columns.numbers.fillers.DoubleRangeIterable;
import tech.tablesaw.selection.Selection;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.DoubleConsumer;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.Predicate;

public class DoubleColumn extends NumberColumn<Double> implements NumberFillers<DoubleColumn> {

    /**
     * Compares two doubles, such that a sort based on this comparator would sort in descending order
     */
    private final DoubleComparator descendingComparator = (o2, o1) -> (Double.compare(o1, o2));

    private final DoubleArrayList data;

    protected DoubleColumn(final String name, final DoubleArrayList data) {
        super(DoubleColumnType.instance(), name);
        this.data = data;
    }

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
    public void clear() {
        data.clear();
    }

    public DoubleColumn setMissing(int index) {
        set(index, DoubleColumnType.missingValueIndicator());
        return this;
    }

    protected DoubleColumn(final String name) {
        super(DoubleColumnType.instance(), name);
        this.data = new DoubleArrayList(DEFAULT_ARRAY_SIZE);
    }

    public static DoubleColumn create(final String name, final double[] arr) {
        return new DoubleColumn(name, new DoubleArrayList(arr));
    }

    public static DoubleColumn create(final String name) {
        return new DoubleColumn(name);
    }

    public static DoubleColumn create(final String name, final float[] arr) {
        final double[] doubles = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            doubles[i] = arr[i];
        }
        return new DoubleColumn(name, new DoubleArrayList(doubles));
    }

    public static DoubleColumn create(final String name, final int[] arr) {
        final double[] doubles = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            doubles[i] = arr[i];
        }
        return new DoubleColumn(name, new DoubleArrayList(doubles));
    }

    public static DoubleColumn create(final String name, final long[] arr) {
        final double[] doubles = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            doubles[i] = arr[i];
        }
        return new DoubleColumn(name, new DoubleArrayList(doubles));
    }

    public static DoubleColumn create(final String name, final List<Number> numberList) {
        final double[] doubles = new double[numberList.size()];
        for (int i = 0; i < numberList.size(); i++) {
            doubles[i] = numberList.get(i).doubleValue();
        }
        return new DoubleColumn(name, new DoubleArrayList(doubles));
    }

    public static DoubleColumn create(final String name, final Number[] numbers) {
        final double[] doubles = new double[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            doubles[i] = numbers[i].doubleValue();
        }
        return new DoubleColumn(name, new DoubleArrayList(doubles));
    }

    public static DoubleColumn create(final String name, final int initialSize) {
        DoubleColumn column = new DoubleColumn(name);
        for (int i = 0; i < initialSize; i++) {
            column.appendMissing();
        }
        return column;
    }

    @Override
    public DoubleColumn createCol(final String name, final int initialSize) {
        return create(name, initialSize);
    }

    @Override
    public DoubleColumn createCol(final String name) {
        return create(name);
    }

    @Override
    public Double get(int index) {
        return getDouble(index);
    }

    @Override
    public DoubleColumn subset(final int[] rows) {
        final DoubleColumn c = this.emptyCopy();
        for (final int row : rows) {
            c.append(getDouble(row));
        }
        return c;
    }

    @Override
    public DoubleColumn unique() {
        final DoubleSet doubles = new DoubleOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissing(i)) {
                doubles.add(getDouble(i));
            }
        }
        final DoubleColumn column = DoubleColumn.create(name() + " Unique values");
        doubles.forEach((DoubleConsumer) column::append);
        return column;
    }

    @Override
    public DoubleColumn top(int n) {
        DoubleArrayList top = new DoubleArrayList();
        double[] values = data.toDoubleArray();
        DoubleArrays.parallelQuickSort(values, descendingComparator);
        for (int i = 0; i < n && i < values.length; i++) {
            top.add(values[i]);
        }
        return new DoubleColumn(name() + "[Top " + n  + "]", top);
    }

    @Override
    public DoubleColumn bottom(final int n) {
        DoubleArrayList bottom = new DoubleArrayList();
        double[] values = data.toDoubleArray();
        DoubleArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(values[i]);
        }
        return new DoubleColumn(name() + "[Bottoms " + n  + "]", bottom);
    }    

    @Override
    public DoubleColumn lag(int n) {
        final int srcPos = n >= 0 ? 0 : 0 - n;
        final double[] dest = new double[size()];
        final int destPos = n <= 0 ? 0 : n;
        final int length = n >= 0 ? size() - n : size() + n;

        for (int i = 0; i < size(); i++) {
            dest[i] = FloatColumnType.missingValueIndicator();
        }

        double[] array = data.toDoubleArray();

        System.arraycopy(array, srcPos, dest, destPos, length);
        return new DoubleColumn(name() + " lag(" + n + ")", new DoubleArrayList(dest));
    }

    @Override
    public DoubleColumn removeMissing() {
        DoubleColumn result = copy();
        result.clear();
        DoubleListIterator iterator = data.iterator();
        while (iterator.hasNext()) {
            double v = iterator.nextDouble();
            if (!isMissingValue(v)) {
                result.append(v);
            }
        }
        return result;
    }

    /**
     * Adds the given float to this column
     */
    public DoubleColumn append(final float f) {
        data.add(f);
        return this;
    }

    /**
     * Adds the given double to this column
     */
    public DoubleColumn append(double d) {
        data.add(d);
        return this;
    }

    public DoubleColumn append(int i) {
        data.add(i);
        return this;
    }

    @Override
    public DoubleColumn append(Double val) {
        this.append(val.doubleValue());
        return this;
    }

    public DoubleColumn append(Integer val) {
        this.append(val.doubleValue());
        return this;
    }

    @Override
    public DoubleColumn emptyCopy() {
        return (DoubleColumn) super.emptyCopy();
    }

    @Override
    public DoubleColumn emptyCopy(final int rowSize) {
        return (DoubleColumn) super.emptyCopy(rowSize);
    }

    @Override
    public DoubleColumn copy() {
        return new DoubleColumn(name(), data.clone());
    }

    @Override
    public Iterator<Double> iterator() {
        return (Iterator<Double>) data.iterator();
    }

    @Override
    public Double[] asObjectArray() {
        final Double[] output = new Double[size()];
        for (int i = 0; i < size(); i++) {
            output[i] = getDouble(i);
        }
        return output;
    }

    @Override
    public int compare(Double o1, Double o2) {
        return Double.compare(o1, o2);
    }

    @Override
    public DoubleColumn set(int i, Double val) {
        return set(i, (double) val);
    }

    public DoubleColumn set(int i, double val) {
        data.set(i, val);
        return this;
    }

    /**
     * Updates this column where values matching the selection are replaced with the corresponding value
     * from the given column
     */
    public DoubleColumn set(DoublePredicate condition, NumericColumn<?> other) {
        for (int row = 0; row < size(); row++) {
            if (condition.test(getDouble(row))) {
                set(row, other.getDouble(row));
            }
        }
        return this;
    }

    @Override
    public DoubleColumn set(DoublePredicate condition, Double newValue) {
        return (DoubleColumn) super.set(condition, newValue);
    }

    @Override
    public DoubleColumn append(final Column<Double> column) {
        Preconditions.checkArgument(column.type() == this.type());
        final DoubleColumn numberColumn = (DoubleColumn) column;
        final int size = numberColumn.size();
        for (int i = 0; i < size; i++) {
            append(numberColumn.getDouble(i));
        }
        return this;
    }

    @Override
    public DoubleColumn append(Column<Double> column, int row) {
        Preconditions.checkArgument(column.type() == this.type());
        DoubleColumn doubleColumn = (DoubleColumn) column;
        return append(doubleColumn.getDouble(row));
    }

    @Override
    public DoubleColumn set(int row, Column<Double> column, int sourceRow) {
        Preconditions.checkArgument(column.type() == this.type());
        DoubleColumn doubleColumn = (DoubleColumn) column;
        return set(row, doubleColumn.getDouble(sourceRow));
    }

    /**
     * Returns a new NumberColumn with only those rows satisfying the predicate
     *
     * @param test the predicate
     * @return a new NumberColumn with only those rows satisfying the predicate
     */
    public DoubleColumn filter(DoublePredicate test) {
        DoubleColumn result = DoubleColumn.create(name());
        for (int i = 0; i < size(); i++) {
            double d = getDouble(i);
            if (test.test(d)) {
                result.append(d);
            }
        }
        return result;
    }

    @Override
    public byte[] asBytes(int rowNumber) {
        return ByteBuffer.allocate(DoubleColumnType.instance().byteSize()).putDouble(getDouble(rowNumber)).array();
    }

    @Override
    public int countUnique() {
        DoubleSet uniqueElements = new DoubleOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissing(i)) {
                uniqueElements.add(getDouble(i));
            }
        }
        return uniqueElements.size();
    }

    @Override
    public double getDouble(int row) {
        return data.getDouble(row);
    }

    public boolean isMissingValue(double value) {
        return DoubleColumnType.isMissingValue(value);
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
    public DoubleColumn appendMissing() {
        return append(DoubleColumnType.missingValueIndicator());
    }

    @Override
    public DoubleColumn appendObj(Object obj) {
        if (obj == null) {
            return appendMissing();
        }
        if (obj instanceof Double) {
            return append((double) obj);
        }
        if (obj instanceof BigDecimal) {
            return append(((BigDecimal) obj).doubleValue());
        }
        throw new IllegalArgumentException("Could not append " + obj.getClass());
    }

    @Override
    public DoubleColumn appendCell(final String value) {
        try {
            return append(DoubleColumnType.DEFAULT_PARSER.parseDouble(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Error adding value to column " + name() + ": " + e.getMessage());
        }
    }

    @Override
    public DoubleColumn appendCell(final String value, AbstractColumnParser<?> parser) {
        try {
            return append(parser.parseDouble(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Error adding value to column " + name()  + ": " + e.getMessage());
        }
    }

    @Override
    public String getUnformattedString(final int row) {
        final double value = getDouble(row);
        if (DoubleColumnType.isMissingValue(value)) {
            return "";
        }
        return String.valueOf(value);
    }

    // fillWith methods

    @Override
    public DoubleColumn fillWith(final DoubleIterator iterator) {
        for (int r = 0; r < size(); r++) {
            if (!iterator.hasNext()) {
                break;
            }
            set(r, iterator.nextDouble());
        }
        return this;
    }

    @Override
    public DoubleColumn fillWith(final DoubleRangeIterable iterable) {
        DoubleIterator iterator = iterable.iterator();
        for (int r = 0; r < size(); r++) {
            if (!iterator.hasNext()) {
                iterator = iterable.iterator();
                if (!iterator.hasNext()) {
                    break;
                }
            }
            set(r, iterator.nextDouble());
        }
        return this;
    }

    @Override
    public DoubleColumn fillWith(final DoubleSupplier supplier) {
        for (int r = 0; r < size(); r++) {
            try {
                set(r, supplier.getAsDouble());
            } catch (final Exception e) {
                break;
            }
        }
        return this;
    }

    @Override
    public DoubleColumn fillWith(double d) {
        for (int r = 0; r < size(); r++) {
            set(r, d);
        }
        return this;
    }

    @Override
    public DoubleColumn inRange(int start, int end) {
        return (DoubleColumn) super.inRange(start, end);
    }

    @Override
    public DoubleColumn where(Selection selection) {
        return (DoubleColumn) super.where(selection);
    }

    @Override
    public DoubleColumn lead(int n) {
        return (DoubleColumn) super.lead(n);
    }

    @Override
    public DoubleColumn setName(String name) {
        return (DoubleColumn) super.setName(name);
    }

    @Override
    public DoubleColumn filter(Predicate<? super Double> test) {
        return (DoubleColumn) super.filter(test);
    }

    @Override
    public DoubleColumn sorted(Comparator<? super Double> comp) {
        return (DoubleColumn) super.sorted(comp);
    }

    @Override
    public DoubleColumn map(Function<? super Double, ? extends Double> fun) {
        return (DoubleColumn) super.map(fun);
    }

    @Override
    public DoubleColumn min(Column<Double> other) {
        return (DoubleColumn) super.min(other);
    }

    @Override
    public DoubleColumn max(Column<Double> other) {
        return (DoubleColumn) super.max(other);
    }

    @Override
    public DoubleColumn set(Selection condition, Column<Double> other) {
        return (DoubleColumn) super.set(condition, other);
    }

    @Override
    public DoubleColumn set(Selection rowSelection, Double newValue) {
        return (DoubleColumn) super.set(rowSelection, newValue);
    }

    @Override
    public DoubleColumn first(int numRows) {
        return (DoubleColumn) super.first(numRows);
    }

    @Override
    public DoubleColumn last(int numRows) {
        return (DoubleColumn) super.last(numRows);
    }

    @Override
    public DoubleColumn sampleN(int n) {
        return (DoubleColumn) super.sampleN(n);
    }

    @Override
    public DoubleColumn sampleX(double proportion) {
        return (DoubleColumn) super.sampleX(proportion);
    }

    /**
     * Returns a new LongColumn containing a value for each value in this column, truncating if necessary
     *
     * A narrowing primitive conversion such as this one may lose information about the overall magnitude of a
     * numeric value and may also lose precision and range. Specifically, if the value is too small (a negative value
     * of large magnitude or negative infinity), the result is the smallest representable value of type long.
     *
     * Similarly, if the value is too large (a positive value of large magnitude or positive infinity), the result is the
     * largest representable value of type long.
     *
     * Despite the fact that overflow, underflow, or other loss of information may occur, a narrowing primitive
     * conversion never results in a run-time exception.
     *
     * A missing value in the receiver is converted to a missing value in the result
     */
    @Override
    public LongColumn asLongColumn() {
        LongArrayList values = new LongArrayList();
        for (double d : data) {
            values.add((long) d);
        }
        values.trim();
        return LongColumn.create(this.name(), values.elements());
    }

    /**
     * Returns a new IntColumn containing a value for each value in this column, truncating if necessary.
     *
     * A narrowing primitive conversion such as this one may lose information about the overall magnitude of a
     * numeric value and may also lose precision and range. Specifically, if the value is too small (a negative value
     * of large magnitude or negative infinity), the result is the smallest representable value of type int.
     *
     * Similarly, if the value is too large (a positive value of large magnitude or positive infinity), the result is the
     * largest representable value of type int.
     *
     * Despite the fact that overflow, underflow, or other loss of information may occur, a narrowing primitive
     * conversion never results in a run-time exception.
     *
     * A missing value in the receiver is converted to a missing value in the result
     */
    @Override
    public IntColumn asIntColumn() {
        IntArrayList values = new IntArrayList();
        for (double d : data) {
            values.add((int) d);
        }
        values.trim();
        return IntColumn.create(this.name(), values.elements());
    }

    /**
     * Returns a new ShortColumn containing a value for each value in this column, truncating if necessary.
     *
     * A narrowing primitive conversion such as this one may lose information about the overall magnitude of a
     * numeric value and may also lose precision and range. Specifically, if the value is too small (a negative value
     * of large magnitude or negative infinity), the result is the smallest representable value of type int.
     *
     * Similarly, if the value is too large (a positive value of large magnitude or positive infinity), the result is the
     * largest representable value of type short.
     *
     * Despite the fact that overflow, underflow, or other loss of information may occur, a narrowing primitive
     * conversion never results in a run-time exception.
     *
     * A missing value in the receiver is converted to a missing value in the result
     */
    @Override
    public ShortColumn asShortColumn() {
        ShortArrayList values = new ShortArrayList();
        for (double d : data) {
            values.add((short) d);
        }
        values.trim();
        return ShortColumn.create(this.name(), values.elements());
    }

    /**
     * Returns a new FloatColumn containing a value for each value in this column, truncating if necessary.
     *
     * A narrowing primitive conversion such as this one may lose information about the overall magnitude of a
     * numeric value and may also lose precision and range. Specifically, if the value is too small (a negative value
     * of large magnitude or negative infinity), the result is the smallest representable value of type float.
     *
     * Similarly, if the value is too large (a positive value of large magnitude or positive infinity), the result is the
     * largest representable value of type float.
     *
     * Despite the fact that overflow, underflow, or other loss of information may occur, a narrowing primitive
     * conversion never results in a run-time exception.
     *
     * A missing value in the receiver is converted to a missing value in the result
     */
    @Override
    public FloatColumn asFloatColumn() {
        FloatArrayList values = new FloatArrayList();
        for (double d : data) {
            values.add((float) d);
        }
        values.trim();
        return FloatColumn.create(this.name(), values.elements());
    }

}
