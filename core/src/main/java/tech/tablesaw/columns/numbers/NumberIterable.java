package tech.tablesaw.columns.numbers;

public interface NumberIterable extends Iterable<Double> {

    NumberIterator numberIterator();
}
