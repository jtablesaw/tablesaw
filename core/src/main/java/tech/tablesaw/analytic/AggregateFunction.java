package tech.tablesaw.analytic;

/**
 * Analytic Aggregate function.
 *
 * <p>See {@link AggregateFunctions} for more background.
 *
 * <p>This class allows callers to independently slide the left side, and right side of the window.
 * Implementers must keep track of the state of the window as it slides.
 *
 * @param <T> the type of the values in the input column (E.G. Integer).
 * @param <R> the type of values in the return column (E.G. Double).
 */
interface AggregateFunction<T, R> {

  /**
   * Slides the left side (aka start) of the window window to the right (aka end) by removing the
   * first element in the window. E.G. [(1, 2, 3, 4, 5)] -> [1, (2, 3, 4, 5)]
   */
  void removeLeftMost();

  /**
   * Slides the right side (aka end) of the window to the right (aka end) by appending a new
   * non-missing element. E.G. [(1, 2, 3), 4, 5] -> [(1, 2, 3, 4), 5)]
   */
  void addRightMost(T value);

  /**
   * Slides the right side (aka end) of the window to the right (aka end) by appending a new missing
   * element. E.G. [(1, 2, 3), NULL, 5] -> [(1, 2, 3, NULL), 5)]
   */
  void addRightMostMissing();

  /** Shortcut to appending multiple values to the right (aka end) of a window in one pass. */
  default void addAllRightMost(Iterable<T> newValues) {
    newValues.forEach(this::addRightMost);
  }

  /**
   * Get the aggregate value for the current window.
   *
   * @return the aggregate value for the current window.
   */
  R getValue();
}
