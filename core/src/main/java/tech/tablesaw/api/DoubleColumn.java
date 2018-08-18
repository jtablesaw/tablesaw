package tech.tablesaw.api;

import static tech.tablesaw.api.ColumnType.DOUBLE;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.List;
import java.util.function.DoubleConsumer;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.ToDoubleFunction;

import com.google.common.base.Preconditions;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleOpenHashSet;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.DoubleDataWrapper;
import tech.tablesaw.columns.numbers.NumberFillers;
import tech.tablesaw.columns.numbers.NumberIterable;
import tech.tablesaw.columns.numbers.NumberIterator;
import tech.tablesaw.columns.numbers.NumericDataWrapper;

public class DoubleColumn extends NumberColumn<Double> implements NumericColumn<Double>, NumberFillers<DoubleColumn> {

    protected DoubleColumn(final String name, final DoubleArrayList data) {
        super(DOUBLE, name);
        setDataWrapper(new DoubleDataWrapper(data));
    }

    protected DoubleColumn(final String name, final NumericDataWrapper data) {
        super(data.type(), name);
        setDataWrapper(data);
    }

    public static DoubleColumn create(final String name, final double[] arr) {
        return new DoubleColumn(name, new DoubleArrayList(arr));
    }

    public static DoubleColumn create(final String name, final NumericDataWrapper data) {
        return new DoubleColumn(name, data);
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
    protected NumberColumn<Double> createCol(String name, NumericDataWrapper data) {
	return DoubleColumn.create(name, data);
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

    public double firstElement() {
        if (size() > 0) {
            return getDouble(0);
        }
        return data.missingValueIndicator();
    }

    /**
     * Adds the given float to this column
     */
    public DoubleColumn append(final float f) {
        data.append(f);
        return this;
    }

    /**
     * Adds the given double to this column
     */
    public DoubleColumn append(double d) {
        data.append(d);
        return this;
    }

    public DoubleColumn append(int i) {
        data.append(i);
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
        return (DoubleColumn) super.copy();
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

    @Override
    @SuppressWarnings("unchecked")
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
        return (DoubleColumn) set(i, (double) val);
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

}
