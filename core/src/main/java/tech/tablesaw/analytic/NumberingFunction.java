package tech.tablesaw.analytic;

/**
 * Analytic numbering functions give every value in the window a number based on some ordering
 * rules. // TODO make sure partition is ordered. They require the partition to be ordered.
 *
 * <p>A analytic Aggregate functions accepts a window of rows and returns a single value which is
 * used to set a single row in the output column. In contrast an Analytic Mapping function accepts a
 * window and returns a window of rows (of the same size) which is used to a set a window of rows in
 * the output column.
 *
 * <p>Rank denseRank and rowNumber are examples of analytic mapping functions.
 *
 * <p>Analytic mapping functions are called exactly once per window.
 *
 * <p>TODO be explicit about the use case. I am not sure what else they are used for outside of *
 * numbering functions.
 */
abstract class NumberingFunction {
  abstract void addEqualRow();

  abstract void addNextRow();

  abstract int getValue();
}
