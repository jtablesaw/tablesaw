package tech.tablesaw.columns.numbers;

import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;

public interface DoubleIterable extends Iterable<Double> {

    DoubleIterator doubleIterator();

    IntIterator intIterator();

}
