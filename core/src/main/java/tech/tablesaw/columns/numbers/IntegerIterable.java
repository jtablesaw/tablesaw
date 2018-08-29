package tech.tablesaw.columns.numbers;

import it.unimi.dsi.fastutil.ints.IntIterator;

public interface IntegerIterable extends Iterable<Integer> {

    IntIterator intIterator();

}
