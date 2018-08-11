package tech.tablesaw.columns.numbers;

import it.unimi.dsi.fastutil.doubles.DoubleIterator;

public interface DoubleIterable extends Iterable<Double> {

    DoubleIterator doubleIterator();

}
