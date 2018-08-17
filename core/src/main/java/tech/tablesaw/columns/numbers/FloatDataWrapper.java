package tech.tablesaw.columns.numbers;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatComparator;

import java.util.Arrays;
import java.util.Iterator;

public class FloatDataWrapper implements NumericDataWrapper {

    /**
     * Compares two doubles, such that a sort based on this comparator would sort in descending order
     */
    private final FloatComparator descendingComparator = (o2, o1) -> (Float.compare(o1, o2));

    private final FloatArrayList data;

    public FloatDataWrapper(FloatArrayList data) {
        this.data = data;
    }

    @Override
    public NumberIterator numberIterator() {
        return new NumberIterator(data);
    }

    @Override
    public Iterator<Double> iterator() {
        return new NumberIterator(data).iterator();
    }

    @Override
    public NumericDataWrapper top(int n) {
        final FloatArrayList top = new FloatArrayList();
        final float[] values = data.toFloatArray();
        FloatArrays.parallelQuickSort(values, descendingComparator);
        for (int i = 0; i < n && i < values.length; i++) {
            top.add(values[i]);
        }
        return new FloatDataWrapper(top);
    }

    @Override
    public NumericDataWrapper bottom(int n) {

        final FloatArrayList bottom = new FloatArrayList();
        final float[] values = data.toFloatArray();
        FloatArrays.parallelQuickSort(values);
        for (int i = 0; i < n && i < values.length; i++) {
            bottom.add(values[i]);
        }
        return new FloatDataWrapper(bottom);
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
        data.add((float) d);
    }

    @Override
    public void append(int i) {
        data.add(i);
    }

    @Override
    public double getDouble(int row) {
        return data.getFloat(row);
    }

    @Override
    public FloatDataWrapper copy() {
        return new FloatDataWrapper(data.clone());
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
        FloatArrays.parallelQuickSort(data.elements(), descendingComparator);
    }

    @Override
    public void set(int r, double value) {
        this.data.set(r, (float) value);
    }

    @Override
    public boolean contains(double value) {
        return data.contains((float) value);
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
        final float[] dest = new float[size()];
        final int destPos = n <= 0 ? 0 : n;
        final int length = n >= 0 ? size() - n : size() + n;

        for (int i = 0; i < size(); i++) {
            dest[i] = Float.NaN;
        }

        float[] array = data.toFloatArray();

        System.arraycopy(array, srcPos, dest, destPos, length);
        return new FloatDataWrapper(new FloatArrayList(dest));
    }

    @Override
    public NumericDataWrapper lead(int n) {
        return lag(-n);
    }
}
