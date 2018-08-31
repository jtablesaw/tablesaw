package tech.tablesaw.columns.numbers;

import tech.tablesaw.columns.AbstractParser;
import tech.tablesaw.selection.Selection;

// TODO(lwhite): Should this class have type params?
public interface DataWrapper {

    double getDouble(int row);

    DataWrapper subset(int[] rows);

    DataWrapper unique();

    DataWrapper top(int n);

    DataWrapper bottom(int n);

    DataWrapper lead(int n);

    DataWrapper lag(int n);

    DataWrapper removeMissing();

    DataWrapper emptyCopy();

    DataWrapper emptyCopy(int rowSize);

    DataWrapper copy();

    Object[] asObjectArray();

    byte[] asBytes(int rowNumber);

    int countUnique();

    void setMissing(Selection condition);

    boolean isMissing(int rowNumber);

    void sortAscending();

    void sortDescending();

    DataWrapper inRange(int start, int end);

    DataWrapper where(Selection selection);

    DataWrapper first(int numRows);

    DataWrapper last(int numRows);

    DataWrapper sampleN(int n);

    DataWrapper sampleX(double proportion);

    int size();

    void clear();

    Selection isMissing();

    void appendMissing();

    void appendObj(Object obj);

    void appendCell(String value) throws NumberOutOfRangeException;

    void appendCell(String value, AbstractParser<?> parser) throws NumberOutOfRangeException;
}
