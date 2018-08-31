package tech.tablesaw.columns.numbers;

import java.util.Iterator;

public interface RealDataWrapper extends DataWrapper {

    float getFloat(int row);

    void append(float value) throws NumberOutOfRangeException;

    void append(double value) throws NumberOutOfRangeException;

    boolean isMissingValue(double value);

    void set(int i, float val) throws NumberOutOfRangeException;

    void set(int i, double val) throws NumberOutOfRangeException;

    Iterator<Double> iterator();

    boolean contains(double value);

}
