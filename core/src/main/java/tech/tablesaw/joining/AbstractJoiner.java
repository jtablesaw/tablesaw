package tech.tablesaw.joining;

import tech.tablesaw.api.Table;

public abstract class AbstractJoiner {

  public abstract DataFrameJoiner type(JoinType joinType);

  public abstract DataFrameJoiner keepAllJoinKeyColumns(boolean keep);

  public abstract DataFrameJoiner allowDuplicateColumnNames(boolean allow);

  public abstract DataFrameJoiner rightJoinColumns(String... rightJoinColumnNames);

  public abstract DataFrameJoiner with(Table... tables);

  public abstract Table join();

  public AbstractJoiner() {}

  /**
   * Joins to the given tables assuming that they have a column of the name we're joining on
   *
   * @param tables The tables to join with
   */
  @Deprecated
  public Table inner(Table... tables) {
    type(JoinType.INNER);
    with(tables);
    return join();
  }

  /**
   * Joins to the given tables assuming that they have a column of the name we're joining on
   *
   * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than
   *     the join column have the same name if {@code true} the join will succeed and duplicate
   *     columns are renamed*
   * @param tables The tables to join with
   */
  @Deprecated
  public Table inner(boolean allowDuplicateColumnNames, Table... tables) {
    type(JoinType.INNER);
    allowDuplicateColumnNames(allowDuplicateColumnNames);
    with(tables);
    return join();
  }

  /**
   * Joins the joiner to the table2, using the given column for the second table and returns the
   * resulting table
   *
   * @param table2 The table to join with
   * @param col2Name The column to join on. If col2Name refers to a double column, the join is
   *     performed after rounding to integers.
   * @return The resulting table
   */
  @Deprecated
  public Table inner(Table table2, String col2Name) {
    return inner(table2, false, col2Name);
  }

  /**
   * Joins the joiner to the table2, using the given columns for the second table and returns the
   * resulting table
   *
   * @param table2 The table to join with
   * @param col2Names The columns to join on. If a name refers to a double column, the join is
   *     performed after rounding to integers.
   * @return The resulting table
   */
  @Deprecated
  public Table inner(Table table2, String[] col2Names) {
    return inner(table2, false, col2Names);
  }

  /**
   * Joins the joiner to the table2, using the given column for the second table and returns the
   * resulting table
   *
   * @param table2 The table to join with
   * @param col2Name The column to join on. If col2Name refers to a double column, the join is
   *     performed after rounding to integers.
   * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than
   *     the join column have the same name if {@code true} the join will succeed and duplicate
   *     columns are renamed*
   * @return The resulting table
   */
  @Deprecated
  public Table inner(Table table2, String col2Name, boolean allowDuplicateColumnNames) {
    return inner(table2, allowDuplicateColumnNames, col2Name);
  }

  /**
   * Joins the joiner to the table2, using the given columns for the second table and returns the
   * resulting table
   *
   * @param table2 The table to join with
   * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than
   *     the join column have the same name if {@code true} the join will succeed and duplicate
   *     columns are renamed*
   * @param col2Names The columns to join on. If a name refers to a double column, the join is
   *     performed after rounding to integers.
   * @return The resulting table
   */
  @Deprecated
  public Table inner(Table table2, boolean allowDuplicateColumnNames, String... col2Names) {
    allowDuplicateColumnNames(allowDuplicateColumnNames);
    type(JoinType.INNER);
    rightJoinColumns(col2Names);
    with(table2);
    return join();
  }

  /**
   * Joins the joiner to the table2, using the given columns for the second table and returns the
   * resulting table
   *
   * @param table2 The table to join with
   * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than
   *     the join column have the same name if {@code true} the join will succeed and duplicate
   *     columns are renamed*
   * @param keepAllJoinKeyColumns if {@code false} the join will only keep join key columns in
   *     table1 if {@code true} the join will return all join key columns in both table, which may
   *     have difference when there are null values
   * @param col2Names The columns to join on. If a name refers to a double column, the join is
   *     performed after rounding to integers.
   * @return The resulting table
   */
  @Deprecated
  public Table inner(
      Table table2,
      boolean allowDuplicateColumnNames,
      boolean keepAllJoinKeyColumns,
      String... col2Names) {
    allowDuplicateColumnNames(allowDuplicateColumnNames);
    keepAllJoinKeyColumns(keepAllJoinKeyColumns);
    type(JoinType.INNER);
    rightJoinColumns(col2Names);
    with(table2);
    return join();
  }

  /**
   * Full outer join to the given tables assuming that they have a column of the name we're joining
   * on
   *
   * @param tables The tables to join with
   * @return The resulting table
   */
  @Deprecated
  public Table fullOuter(Table... tables) {
    return fullOuter(false, tables);
  }

  /**
   * Full outer join to the given tables assuming that they have a column of the name we're joining
   * on
   *
   * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than
   *     the join column have the same name if {@code true} the join will succeed and duplicate
   *     columns are renamed*
   * @param tables The tables to join with
   * @return The resulting table
   */
  @Deprecated
  public Table fullOuter(boolean allowDuplicateColumnNames, Table... tables) {
    allowDuplicateColumnNames(allowDuplicateColumnNames);
    type(JoinType.FULL_OUTER);
    with(tables);
    return join();
  }

  /**
   * Joins the joiner to the table2, using the given columns for the second table and returns the
   * resulting table
   *
   * @param table2 The table to join with
   * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than
   *     the join column have the same name if {@code true} the join will succeed and duplicate
   *     columns are renamed
   * @param keepAllJoinKeyColumns if {@code false} the join will only keep join key columns in
   *     table1 if {@code true} the join will return all join key columns in both table, which may
   *     have difference when there are null values
   * @param col2Names The columns to join on. If a name refers to a double column, the join is
   *     performed after rounding to integers.
   * @return The resulting table
   */
  @Deprecated
  public Table fullOuter(
      Table table2,
      boolean allowDuplicateColumnNames,
      boolean keepAllJoinKeyColumns,
      String... col2Names) {
    allowDuplicateColumnNames(allowDuplicateColumnNames);
    type(JoinType.FULL_OUTER);
    rightJoinColumns(col2Names);
    keepAllJoinKeyColumns(keepAllJoinKeyColumns);
    with(table2);
    return join();
  }

  /**
   * Full outer join the joiner to the table2, using the given column for the second table and
   * returns the resulting table
   *
   * @param table2 The table to join with
   * @param col2Name The column to join on. If col2Name refers to a double column, the join is
   *     performed after rounding to integers.
   * @return The resulting table
   */
  @Deprecated
  public Table fullOuter(Table table2, String col2Name) {
    type(JoinType.FULL_OUTER);
    with(table2);
    rightJoinColumns(col2Name);
    return join();
  }

  /**
   * Joins to the given tables assuming that they have a column of the name we're joining on
   *
   * @param tables The tables to join with
   * @return The resulting table
   */
  @Deprecated
  public Table leftOuter(Table... tables) {
    return leftOuter(false, tables);
  }

  /**
   * Joins to the given tables assuming that they have a column of the name we're joining on
   *
   * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than
   *     the join column have the same name if {@code true} the join will succeed and duplicate
   *     columns are renamed*
   * @param tables The tables to join with
   * @return The resulting table
   */
  @Deprecated
  public Table leftOuter(boolean allowDuplicateColumnNames, Table... tables) {
    allowDuplicateColumnNames(allowDuplicateColumnNames);
    type(JoinType.LEFT_OUTER);
    with(tables);
    return join();
  }

  /**
   * Joins the joiner to the table2, using the given columns for the second table and returns the
   * resulting table
   *
   * @param table2 The table to join with
   * @param col2Names The columns to join on. If a name refers to a double column, the join is
   *     performed after rounding to integers.
   * @return The resulting table
   */
  @Deprecated
  public Table leftOuter(Table table2, String[] col2Names) {
    return leftOuter(table2, false, col2Names);
  }

  /**
   * Joins the joiner to the table2, using the given column for the second table and returns the
   * resulting table
   *
   * @param table2 The table to join with
   * @param col2Name The column to join on. If col2Name refers to a double column, the join is
   *     performed after rounding to integers.
   * @return The resulting table
   */
  @Deprecated
  public Table leftOuter(Table table2, String col2Name) {
    return leftOuter(table2, false, col2Name);
  }

  /**
   * Joins the joiner to the table2, using the given columns for the second table and returns the
   * resulting table
   *
   * @param table2 The table to join with
   * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than
   *     the join column have the same name if {@code true} the join will succeed and duplicate
   *     columns are renamed
   * @param col2Names The columns to join on. If a name refers to a double column, the join is
   *     performed after rounding to integers.
   * @return The resulting table
   */
  @Deprecated
  public Table leftOuter(Table table2, boolean allowDuplicateColumnNames, String... col2Names) {
    allowDuplicateColumnNames(allowDuplicateColumnNames);
    type(JoinType.LEFT_OUTER);
    with(table2);
    allowDuplicateColumnNames(allowDuplicateColumnNames);
    rightJoinColumns(col2Names);
    return join();
  }

  /**
   * Joins the joiner to the table2, using the given columns for the second table and returns the
   * resulting table
   *
   * @param table2 The table to join with
   * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than
   *     the join column have the same name if {@code true} the join will succeed and duplicate
   *     columns are renamed
   * @param keepAllJoinKeyColumns if {@code false} the join will only keep join key columns in
   *     table1 if {@code true} the join will return all join key columns in both table, which may
   *     have difference when there are null values
   * @param col2Names The columns to join on. If a name refers to a double column, the join is
   *     performed after rounding to integers.
   * @return The resulting table
   */
  @Deprecated
  public Table leftOuter(
      Table table2,
      boolean allowDuplicateColumnNames,
      boolean keepAllJoinKeyColumns,
      String... col2Names) {
    allowDuplicateColumnNames(allowDuplicateColumnNames);
    type(JoinType.LEFT_OUTER);
    with(table2);
    keepAllJoinKeyColumns(keepAllJoinKeyColumns);
    rightJoinColumns(col2Names);
    return join();
  }

  /**
   * Joins to the given tables assuming that they have a column of the name we're joining on
   *
   * @param tables The tables to join with
   * @return The resulting table
   */
  @Deprecated
  public Table rightOuter(Table... tables) {
    return rightOuter(false, tables);
  }

  /**
   * Joins to the given tables assuming that they have a column of the name we're joining on
   *
   * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than
   *     the join column have the same name if {@code true} the join will succeed and duplicate
   *     columns are renamed
   * @param tables The tables to join with
   * @return The resulting table
   */
  @Deprecated
  public Table rightOuter(boolean allowDuplicateColumnNames, Table... tables) {
    allowDuplicateColumnNames(allowDuplicateColumnNames);
    type(JoinType.RIGHT_OUTER);
    with(tables);
    return join();
  }

  /**
   * Joins the joiner to the table2, using the given column for the second table and returns the
   * resulting table
   *
   * @param table2 The table to join with
   * @param col2Name The column to join on. If col2Name refers to a double column, the join is
   *     performed after rounding to integers.
   * @return The resulting table
   */
  @Deprecated
  public Table rightOuter(Table table2, String col2Name) {
    return rightOuter(table2, false, col2Name);
  }

  /**
   * Joins the joiner to the table2, using the given columns for the second table and returns the
   * resulting table
   *
   * @param table2 The table to join with
   * @param col2Names The columns to join on. If a name refers to a double column, the join is
   *     performed after rounding to integers.
   * @return The resulting table
   */
  @Deprecated
  public Table rightOuter(Table table2, String[] col2Names) {
    return rightOuter(table2, false, col2Names);
  }

  /**
   * Joins the joiner to the table2, using the given columns for the second table and returns the
   * resulting table
   *
   * @param table2 The table to join with
   * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than
   *     the join column have the same name if {@code true} the join will succeed and duplicate
   *     columns are renamed
   * @param col2Names The columns to join on. If a name refers to a double column, the join is
   *     performed after rounding to integers.
   */
  @Deprecated
  public Table rightOuter(Table table2, boolean allowDuplicateColumnNames, String... col2Names) {
    allowDuplicateColumnNames(allowDuplicateColumnNames);
    type(JoinType.RIGHT_OUTER);
    with(table2);
    rightJoinColumns(col2Names);
    return join();
  }

  /**
   * Joins the joiner to the table2, using the given columns for the second table and returns the
   * resulting table
   *
   * @param table2 The table to join with
   * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than
   *     the join column have the same name if {@code true} the join will succeed and duplicate
   *     columns are renamed
   * @param keepAllJoinKeyColumns if {@code false} the join will only keep join key columns in
   *     table1 if {@code true} the join will return all join key columns in both table, which may
   *     have difference when there are null values
   * @param col2Names The columns to join on. If a name refers to a double column, the join is
   *     performed after rounding to integers.
   * @return The resulting table
   */
  @Deprecated
  public Table rightOuter(
      Table table2,
      boolean allowDuplicateColumnNames,
      boolean keepAllJoinKeyColumns,
      String... col2Names) {
    allowDuplicateColumnNames(allowDuplicateColumnNames);
    keepAllJoinKeyColumns(keepAllJoinKeyColumns);
    type(JoinType.RIGHT_OUTER);
    with(table2);
    rightJoinColumns(col2Names);
    return join();
  }

  abstract Table joinInternal(
      Table table,
      Table table2,
      JoinType rightOuter,
      boolean allowDuplicateColumnNames,
      boolean keepAllJoinKeyColumns,
      String[] col2Names);
}
