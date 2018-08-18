package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.ColumnType;

import java.util.Iterator;

public interface NumericDataWrapper extends NumberIterable {

    int size();

    void append(final float f);

    void append(double d);

    void append(int i);

    double getDouble(final int row);

    float getFloat(final int row);

    /**
     * Returns an int from the data at the given index.
     *
     * NOTE: If the data is floating point, it is cast to int and precision may be lost
     */
    int getInt(int index);

    NumericDataWrapper copy();

    NumericDataWrapper emptyCopy();

    NumericDataWrapper emptyCopy(int rowSize);

    void clear();

    void sortAscending();

    void sortDescending();

    void set(final int r, final double value);

    boolean contains(final double value);

    boolean contains(final int value);

    NumericDataWrapper lag(final int n);

    NumericDataWrapper lead(final int n);

    NumberIterator numberIterator();

    Iterator<Double> iterator();

    NumericDataWrapper top(final int n);

    NumericDataWrapper bottom(final int n);

    default NumericDataWrapper removeMissing() {
        NumericDataWrapper wrapper = copy();
        wrapper.clear();;
        final NumberIterator iterator = numberIterator();
        while (iterator.hasNext()) {
            final double v = iterator.next();
            if (!isMissingValue(v)) {
                wrapper.append(v);
            }
        }
        return wrapper;
    }

    default boolean isMissingValue(double value) {
        return value != value;
    }

    default boolean isMissingValue(float value) {
        return value != value;
    }

    default boolean isMissingValue(int value) {
        return value == Integer.MIN_VALUE;
    }

    /**
     * Returns the count of unique, non-missing values in the dataset.
     */
    int countUnique();

    void appendMissing();

    ColumnType type();

    /**
     * Returns the contents of the cell at rowNumber as a byte[]
     */
    byte[] asBytes(final int rowNumber);

    double missingValueIndicator();
}
