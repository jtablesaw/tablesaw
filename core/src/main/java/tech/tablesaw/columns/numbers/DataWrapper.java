package tech.tablesaw.columns.numbers;

import tech.tablesaw.columns.AbstractParser;
import tech.tablesaw.selection.Selection;

import java.util.Iterator;

// TODO(lwhite): Should this class have type params?
public interface DataWrapper {

    short getShort(int index);

    int getInt(int row);

    double getDouble(int row);

    DataWrapper subset(int[] rows);

    DataWrapper unique();

    DataWrapper top(int n);

    DataWrapper bottom(int n);

    DataWrapper lead(int n);

    DataWrapper lag(int n);

    DataWrapper removeMissing();

    void append(short i);

    void append(byte value);

    void append(int value);

    void append(long value);

    DataWrapper emptyCopy();

    DataWrapper emptyCopy(int rowSize);

    DataWrapper copy();

    Object[] asObjectArray();

    void appendMissing();

    byte[] asBytes(int rowNumber);

    int countUnique();

    void setMissing(Selection condition);

    boolean isMissingValue(int value);

    boolean isMissing(int rowNumber);

    void sortAscending();

    void sortDescending();

    void appendObj(Object obj);

    DataWrapper inRange(int start, int end);

    DataWrapper where(Selection selection);

    void set(int i, short val);

    void set(int i, int val);

    DataWrapper first(int numRows);

    DataWrapper last(int numRows);

    DataWrapper sampleN(int n);

    DataWrapper sampleX(double proportion);

    int size();

    Iterator<Integer> iterator();

    void clear();

    Selection isMissing();

    void appendCell(String value);

    void appendCell(String value, AbstractParser<?> parser);

    boolean contains(int value);

    byte getByte(int row);
}
