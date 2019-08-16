package tech.tablesaw.analytic;

public interface AggregateFunction<T, R> {

  void removeLeftMost();
  void addRightMost(T value);
  void addRightMostMissing();

  default void addAllRightMost(Iterable<T> newValues) {
    newValues.forEach(this::addRightMost);
  }

  R getValue();
}

