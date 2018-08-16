package tech.tablesaw.columns.numbers;

import it.unimi.dsi.fastutil.ints.IntIterator;

public interface DoubleIterable extends Iterable<Double> {

    NumberIterator doubleIterator();

    IntIterator intIterator();

}
