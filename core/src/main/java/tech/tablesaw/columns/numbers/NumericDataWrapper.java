package tech.tablesaw.columns.numbers;

import it.unimi.dsi.fastutil.doubles.DoubleList;

import java.util.Iterator;

public interface NumericDataWrapper<T> extends NumberIterable {

    int size();

    void append(final float f);

    void append(double d);

    void append(int i);

    double getDouble(final int row);

    NumericDataWrapper copy();

    void clear();

    void sortAscending();

    void sortDescending();

    void set(final int r, final double value);

    boolean contains(final double value);

    boolean contains(final int value);

    DoubleList dataInternal();

    NumericDataWrapper lag(final int n);

    NumericDataWrapper lead(final int n);

    NumberIterator numberIterator();

    Iterator<Double> iterator();

    NumericDataWrapper top(final int n);

    NumericDataWrapper bottom(final int n);

    NumericDataWrapper removeMissing();

    default boolean isMissingValue(double value) {
        return value != value;
    }

    default boolean isMissingValue(float value) {
        return value != value;
    }

    default boolean isMissingValue(int value) {
        return value == Integer.MIN_VALUE;
    }
}
