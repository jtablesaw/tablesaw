package tech.tablesaw.analytic;

/**
 * Analytic numbering function.
 *
 * <p>See {@link NumberingFunctions} for more background.
 *
 * <p>When iterating over a partition this allows callers indicate whether the next row in the
 * partition is equal to or not equal to the previous row. Implementers can use this information to
 * rank rows.
 */
abstract class NumberingFunction {

  /**
   * Called when the next row in the partition is equal to the previous row.
   *
   * <p>E.G. [(1, 1, 1), 1] -> [(1, 1, 1, 1)]
   */
  abstract void addEqualRow();

  /**
   * Called when the next row in the partition is not equal to the previous row.
   *
   * <p>E.G. [(1, 1, 1), 3] -> [(1, 1, 1, 4)]
   */
  abstract void addNextRow();

  /**
   * Returns the numbering value (E.G. Rank) for the current row. Is be called once for every row in
   * the partition while iterating through the partition.
   *
   * @return the numbering value for the current row.
   */
  abstract int getValue();
}
