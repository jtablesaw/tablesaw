package tech.tablesaw.joining;

import tech.tablesaw.api.*;

/** Implements joins between two or more Tables */
public class DataFrameJoiner {

  private final JoinStrategy strategy;

  private final Table table;
  private final String[] joinColumnNames;
  private int[] leftJoinColumnIndexes;

  /**
   * Constructor.
   *
   * @param table The table to join on.
   * @param joinColumnNames The join column names to join on.
   */
  public DataFrameJoiner(Table table, String... joinColumnNames) {
    this.table = table;
    this.joinColumnNames = joinColumnNames;
    this.leftJoinColumnIndexes = getJoinIndexes(table, joinColumnNames);

    // this.strategy = new SortMergeJoin(table, joinColumnNames);
    this.strategy = new CrossProductJoin(table, joinColumnNames);
  }

  /**
   * Finds the index of the columns corresponding to the columnNames. E.G. The column named "ID" is
   * located at index 5 in table.
   *
   * @param table the table that contains the columns.
   * @param columnNames the column names to find indexes of.
   * @return a list of column indexes within the table.
   */
  private int[] getJoinIndexes(Table table, String[] columnNames) {
    int[] results = new int[columnNames.length];
    for (int i = 0; i < columnNames.length; i++) {
      String nm = columnNames[i];
      results[i] = table.columnIndex(nm);
    }
    return results;
  }

  /**
   * Joins to the given tables assuming that they have a column of the name we're joining on
   *
   * @param tables The tables to join with
   */
  public Table inner(Table... tables) {
    return inner(false, tables);
  }

  /**
   * Joins to the given tables assuming that they have a column of the name we're joining on
   *
   * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than
   *     the join column have the same name if {@code true} the join will succeed and duplicate
   *     columns are renamed*
   * @param tables The tables to join with
   */
  public Table inner(boolean allowDuplicateColumnNames, Table... tables) {
    Table joined = table;
    for (Table currT : tables) {
      joined =
          joinInternal(
              joined, currT, JoinType.INNER, allowDuplicateColumnNames, false, joinColumnNames);
    }
    return joined;
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
  public Table inner(Table table2, boolean allowDuplicateColumnNames, String... col2Names) {
    Table joinedTable;
    joinedTable =
        joinInternal(table, table2, JoinType.INNER, allowDuplicateColumnNames, false, col2Names);
    return joinedTable;
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
  public Table inner(
      Table table2,
      boolean allowDuplicateColumnNames,
      boolean keepAllJoinKeyColumns,
      String... col2Names) {
    return joinInternal(
        table, table2, JoinType.INNER, allowDuplicateColumnNames, keepAllJoinKeyColumns, col2Names);
  }

  private Table joinInternal(
      Table table1,
      Table table2,
      JoinType joinType,
      boolean allowDuplicates,
      boolean keepAllJoinKeyColumns,
      String... table2JoinColumnNames) {
    return strategy.performJoin(
        table1,
        table2,
        joinType,
        allowDuplicates,
        keepAllJoinKeyColumns,
        leftJoinColumnIndexes,
        table2JoinColumnNames);
  }

  /**
   * Full outer join to the given tables assuming that they have a column of the name we're joining
   * on
   *
   * @param tables The tables to join with
   * @return The resulting table
   */
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
  public Table fullOuter(boolean allowDuplicateColumnNames, Table... tables) {
    Table joined = table;

    for (Table currT : tables) {
      joined =
          joinInternal(
              joined,
              currT,
              JoinType.FULL_OUTER,
              allowDuplicateColumnNames,
              false,
              joinColumnNames);
    }
    return joined;
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
  public Table fullOuter(
      Table table2,
      boolean allowDuplicateColumnNames,
      boolean keepAllJoinKeyColumns,
      String... col2Names) {
    return joinInternal(
        table,
        table2,
        JoinType.FULL_OUTER,
        allowDuplicateColumnNames,
        keepAllJoinKeyColumns,
        col2Names);
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
  public Table fullOuter(Table table2, String col2Name) {
    return joinInternal(table, table2, JoinType.FULL_OUTER, false, false, col2Name);
  }

  /**
   * Joins to the given tables assuming that they have a column of the name we're joining on
   *
   * @param tables The tables to join with
   * @return The resulting table
   */
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
  public Table leftOuter(boolean allowDuplicateColumnNames, Table... tables) {
    Table joined = table;
    for (Table table2 : tables) {
      joined =
          joinInternal(
              joined,
              table2,
              JoinType.LEFT_OUTER,
              allowDuplicateColumnNames,
              false,
              joinColumnNames);
    }
    return joined;
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
  public Table leftOuter(Table table2, boolean allowDuplicateColumnNames, String... col2Names) {
    return joinInternal(
        table, table2, JoinType.LEFT_OUTER, allowDuplicateColumnNames, false, col2Names);
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
  public Table leftOuter(
      Table table2,
      boolean allowDuplicateColumnNames,
      boolean keepAllJoinKeyColumns,
      String... col2Names) {
    return joinInternal(
        table,
        table2,
        JoinType.LEFT_OUTER,
        allowDuplicateColumnNames,
        keepAllJoinKeyColumns,
        col2Names);
  }

  /**
   * Joins to the given tables assuming that they have a column of the name we're joining on
   *
   * @param tables The tables to join with
   * @return The resulting table
   */
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
  public Table rightOuter(boolean allowDuplicateColumnNames, Table... tables) {
    Table joined = table;
    for (Table table2 : tables) {
      joined =
          strategy.performJoin(
              joined,
              table2,
              JoinType.RIGHT_OUTER,
              allowDuplicateColumnNames,
              false,
              leftJoinColumnIndexes,
              joinColumnNames);
      leftJoinColumnIndexes = getJoinIndexes(joined, joinColumnNames);
    }
    return joined;
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
  public Table rightOuter(Table table2, boolean allowDuplicateColumnNames, String... col2Names) {
    return joinInternal(
        table, table2, JoinType.RIGHT_OUTER, allowDuplicateColumnNames, false, col2Names);
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
  public Table rightOuter(
      Table table2,
      boolean allowDuplicateColumnNames,
      boolean keepAllJoinKeyColumns,
      String... col2Names) {
    return joinInternal(
        table,
        table2,
        JoinType.RIGHT_OUTER,
        allowDuplicateColumnNames,
        keepAllJoinKeyColumns,
        col2Names);
  }
}
