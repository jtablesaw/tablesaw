package tech.tablesaw.columns.numbers;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import tech.tablesaw.api.NumberColumn;

import java.util.Arrays;
import java.util.Iterator;

import static tech.tablesaw.api.NumberColumn.MISSING_VALUE;

public class DoubleDataWrapper implements NumericDataWrapper<Double> {

    /**
     * Compares two doubles, such that a sort based on this comparator would sort in descending order
     */
    private final DoubleComparator descendingComparator = (o2, o1) -> (Double.compare(o1, o2));

    private DoubleArrayList data;

    public DoubleDataWrapper(DoubleArrayList data) {
        this.data = data;
    }

    @Override
    public NumberIterator numberIterator() {
        return new NumberIterator(data);
    }

    @Override
    public Iterator<Double> iterator() {
        return data.iterator();
    }

    @Override
    public NumericDataWrapper top(int n) {
        final DoubleArrayList top = new DoubleArrayList();
        final double[] values = data.toDoubleArray();
        DoubleArrays.parallelQuickSort(values, descendingComparator);
        for (int i = 0; i < n && i < values.length; i++) {
            top.add(values[i]);
        }
        return new DoubleDataWrapper(top);
    }

    @Override
    public NumericDataWrapper bottom(int n) {

        final DoubleArrayList bottom = new DoubleArrayList();
        final double[] values = data.toDoubleArray();
        DoubleArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(values[i]);
        }
        return new DoubleDataWrapper(bottom);
    }

    @Override
    public int size() {
        return data.size();
    }

    public void append(final float f) {
        data.add(f);
    }

    @Override
    public void append(double d) {
        data.add(d);
    }

    @Override
    public void append(int i) {
        data.add(i);
    }

    @Override
    public double getDouble(int row) {
        return data.getDouble(row);
    }

    @Override
    public DoubleDataWrapper copy() {
        return new DoubleDataWrapper(data.clone());
    }

    @Override
    public void clear() {
        this.data.clear();
    }

    @Override
    public void sortAscending() {
        Arrays.parallelSort(data.elements());
    }

    @Override
    public void sortDescending() {
        DoubleArrays.parallelQuickSort(data.elements(), descendingComparator);
    }

    @Override
    public void set(int r, double value) {
        this.data.set(r, value);
    }

    @Override
    public boolean contains(double value) {
        return data.contains(value);
    }

    @Override
    public boolean contains(int value) {
        return data.contains(value);
    }

    @Override
    public DoubleList dataInternal() {
        return null;
    }

    @Override
    public NumericDataWrapper lag(int n) {
        final int srcPos = n >= 0 ? 0 : 0 - n;
        final double[] dest = new double[size()];
        final int destPos = n <= 0 ? 0 : n;
        final int length = n >= 0 ? size() - n : size() + n;

        for (int i = 0; i < size(); i++) {
            dest[i] = MISSING_VALUE;
        }

        double[] array = data.toDoubleArray();

        System.arraycopy(array, srcPos, dest, destPos, length);
        return new DoubleDataWrapper(new DoubleArrayList(dest));
    }

    @Override
    public NumericDataWrapper lead(int n) {
        return lag(-n);
    }

    @Override
    public NumericDataWrapper removeMissing() {
        NumericDataWrapper wrapper = copy();
        wrapper.clear();;
        final NumberIterator iterator = numberIterator();
        while (iterator.hasNext()) {
            final double v = iterator.next();
            if (!NumberColumn.valueIsMissing(v)) {
                wrapper.append(v);
            }
        }
        return wrapper;
    }
}
