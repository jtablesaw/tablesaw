package tech.tablesaw.api;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.function.DoubleConsumer;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.ToDoubleFunction;

import com.google.common.base.Preconditions;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.doubles.DoubleOpenHashSet;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.FloatColumnType;
import tech.tablesaw.columns.numbers.NumberFillers;
import tech.tablesaw.columns.numbers.NumberIterable;
import tech.tablesaw.columns.numbers.NumberIterator;

public class DoubleColumn extends NumberColumn<Double> implements NumericColumn<Double>, NumberFillers<DoubleColumn> {

    private static final ColumnType COLUMN_TYPE = ColumnType.DOUBLE;

    /**
     * Compares two doubles, such that a sort based on this comparator would sort in descending order
     */
    private final DoubleComparator descendingComparator = (o2, o1) -> (Double.compare(o1, o2));

    private final DoubleArrayList data;

    protected DoubleColumn(final String name, final DoubleArrayList data) {
        super(COLUMN_TYPE, name, data);
        this.data = data;
    }

    public static DoubleColumn create(final String name, final double[] arr) {
        return new DoubleColumn(name, new DoubleArrayList(arr));
    }

    public static DoubleColumn create(final String name) {
        return new DoubleColumn(name, new DoubleArrayList());
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
        // TODO This should be pushed down to the dataWrappers
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
        return new DoubleColumn(name, new DoubleArrayList(initialSize));
    }

    @Override
    public DoubleColumn createCol(final String name, final int initialSize) {
        return create(name, initialSize);
    }

    @Override
    public Double get(int index) {
        return getDouble(index);
    }

    @Override
    public DoubleColumn unique() {
        final DoubleSet doubles = new DoubleOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissing(i)) {
                doubles.add(getDouble(i));
            }
        }
        final DoubleColumn column = DoubleColumn.create(name() + " Unique values", doubles.size());
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

    public double firstElement() {
        if (size() > 0) {
            return getDouble(0);
        }
        return (Double) COLUMN_TYPE.getMissingValueIndicator();
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
    public Object[] asObjectArray() {
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

    @Override
    public DoubleColumn append(final Column<Double> column) {
        Preconditions.checkArgument(column.type() == this.type());
        final DoubleColumn numberColumn = (DoubleColumn) column;
        for (int i = 0; i < numberColumn.size(); i++) {
            append(numberColumn.getDouble(i));
        }
        return this;
    }

    // fillWith methods

    @Override
    public DoubleColumn fillWith(final NumberIterator iterator) {
        for (int r = 0; r < size(); r++) {
            if (!iterator.hasNext()) {
                break;
            }
            set(r, iterator.next());
        }
        return this;
    }

    @Override
    public DoubleColumn fillWith(final NumberIterable iterable) {
        NumberIterator iterator = iterable.numberIterator();
        for (int r = 0; r < size(); r++) {
            if (iterator == null || (!iterator.hasNext())) {
                iterator = numberIterator();
                if (!iterator.hasNext()) {
                    break;
                }
            }
            set(r, iterator.next());
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

    /**
     * Maps the function across all rows, appending the results to a new NumberColumn
     *
     * @param fun function to map
     * @return the NumberColumn with the results
     */
    public DoubleColumn map(ToDoubleFunction<Double> fun) {
        DoubleColumn result = DoubleColumn.create(name());
        for (double t : this) {
            try {
                result.append(fun.applyAsDouble(t));
            } catch (Exception e) {
                result.appendMissing();
            }
        }
        return result;
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
        return ByteBuffer.allocate(COLUMN_TYPE.byteSize()).putDouble(getDouble(rowNumber)).array();
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
    public NumberIterator numberIterator() {
        return new NumberIterator(data);
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
    public DoubleColumn appendCell(final String value, StringParser<?> parser) {
        try {
            return append(parser.parseDouble(value));
        } catch (final NumberFormatException e) {
            throw new NumberFormatException("Error adding value to column " + name()  + ": " + e.getMessage());
        }
    }

}
