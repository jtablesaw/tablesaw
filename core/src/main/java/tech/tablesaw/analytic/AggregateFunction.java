package tech.tablesaw.analytic;

/**
 * Analytic Aggregate functions.
 *
 * These require different implementations from regular aggregate functions because they can be called up to n times per
 * table. This causes a time complexity of O(n^2). A table with the window definition ROWS BETWEEN UNBOUNDED PRECEDING
 * AND CURRENT ROW would have n windows. If you call SUM over each window the algo will be very slow.
 *
 * There are three kinds of windows relevant to Analytic Aggregate functions. 1) Fixed windows where both sides are
 * unbounded.
 *
 *
 * 2) Growing analytic Aggregate functions are functions on windows where exactly one side of the window  is unbounded.
 * These have n windows per partition and every window will have one exactly one more (or less) row in it than the
 * window before. This allows the use of more efficient algorithms compared to a sliding window.
 *
 * These functions can be called n times per window and contain O(n) elements per window. This makes them the most
 * important to implement efficiently.
 *
 * 3) Sliding windows where neither side are unbounded.
 *
 * Sliding analytic Aggregate functions are functions on windows where both sides of the window are following(nrows),
 * preceding(nrows) or current row.
 *
 * Sliding windows generally have a fixed size, except for the start and end of the partition. These functions can be
 * called n times per window and but generally of a smaller size than unbounded or growing windows.
 *
 * @param <T> The input type
 * @param <R> The output type
 */
public interface AggregateFunction<T, R> {

  void removeLeftMost();
  void addRightMost(T value);
  void addRightMostMissing();

  default void addAllRightMost(Iterable<T> newValues) {
    newValues.forEach(this::addRightMost);
  }

  R getValue();
}

