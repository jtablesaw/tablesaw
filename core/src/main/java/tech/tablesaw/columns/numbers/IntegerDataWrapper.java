package tech.tablesaw.columns.numbers;

import java.util.Iterator;

public interface IntegerDataWrapper extends DataWrapper {

    byte getByte(int row);

    @Override
    IntegerDataWrapper top(int n);

    @Override
    IntegerDataWrapper bottom(int n);

    @Override
    IntegerDataWrapper lead(int n);

    @Override
    IntegerDataWrapper lag(int n);

    @Override
    IntegerDataWrapper copy();

    @Override
    IntegerDataWrapper removeMissing();

    short getShort(int index);

    int getInt(int row);

    void append(int value) throws NumberOutOfRangeException;

    void append(long value) throws NumberOutOfRangeException;

    boolean isMissingValue(int value);

    void set(int i, short val) throws NumberOutOfRangeException;

    void set(int i, int val) throws NumberOutOfRangeException;

    Iterator<Integer> iterator();

    boolean contains(int value);
}
