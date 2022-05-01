package tech.tablesaw.joining;

import static tech.tablesaw.joining.JoinType.INNER;

import com.google.common.base.Preconditions;
import java.util.*;
import java.util.stream.Collectors;
import tech.tablesaw.api.*;

/** Implements joins between two or more Tables */
public class DataFrameJoiner extends AbstractJoiner {

  /** The join algorithm to be used */
  private JoinStrategy strategy;

  /** The first (left) table named in the join statement */
  private final Table table;

  /** The names of the columns to be used for the first (left) table */
  private final String[] leftJoinColumnNames;

  /**
   * The names of the columns to be joined on in the second (right) table. If these are not
   * explicitly provided, they default to the names used for the left table.
   */
  private String[] rightJoinColumnNames;

  /** The positions (indexes) in the table of the columns used in the first table */
  private int[] leftJoinColumnPositions;

  /**
   * The table(s) to be used on the right side. If more than one table is provided, the join is
   * executed repeatedly, merging the next right table with the prior results
   */
  private List<Table> rightTables = new ArrayList<>();

  /** The type of join to be performed (INNER, LEFT_OUTER, RIGHT_OUTER, or FULL_OUTER */
  private JoinType joinType = INNER;

  /**
   * When this is false, columns in the second (and subsequent) join tables are excluded from the
   * results if they have the same name as a column in the any prior table. When it is true, they
   * are give a prefix and included. The prefix used is "Tn." where n is the number of the table in
   * the join. The second table is (T2.column_name), for example.
   */
  private boolean allowDuplicateColumnNames = false;

  /**
   * When this is true, the columns of the second (and subsequent) join tables are included in the
   * results, even when they're identical in name and data with the first join table. When false,
   * only the first join columns are retained.
   *
   * <p>If the second (or any subsequent) table has the same join column names as the first (or any
   * prior) table, the same scheme used for non-join columns is used, and each column with a
   * duplicate name gets a prefix of "Tn." where n is the number of the table in the join.
   */
  private boolean keepAllJoinKeyColumns = false;

  /**
   * Constructor.
   *
   * @param table The table to join on.
   * @param leftJoinColumnNames The join column names in that table to be used. These names also
   *     serve as the default for the second table, unless other names are explicitly provided.
   */
  public DataFrameJoiner(Table table, String... leftJoinColumnNames) {
    this.table = table;
    this.leftJoinColumnNames = leftJoinColumnNames;

    // we assume the join columns for both tables have the same names,
    // unless names for the right table are explicitly set
    this.rightJoinColumnNames = leftJoinColumnNames;

    this.leftJoinColumnPositions = getJoinIndexes(table, leftJoinColumnNames);
  }

  /**
   * Sets the type of join, which defaults to INNER if not provided.
   *
   * @param joinType The type of join to perform (INNER, LEFT_OUTER, RIGHT_OUTER, FULL_OUTER)
   * @return This joiner object.
   */
  public DataFrameJoiner type(JoinType joinType) {
    this.joinType = joinType;
    return this;
  }

  /**
   * When the argument is true, the join columns of the second (and subsequent) tables are included
   * in the results, even when they're identical in name and data with the first join table. When
   * false, only one set of join columns is retained in the result.
   *
   * <p>Note that if the second (or any subsequent) table has the same join column names as the
   * first (or any prior) table, the same scheme used for non-join columns is used, and each column
   * with a duplicate name gets a prefix of "Tn." where n is the number of the table in the join.
   *
   * <p>If this method is not called, the default is false
   *
   * @param keep true or false
   * @return this DataFrameJoiner instance
   */
  public DataFrameJoiner keepAllJoinKeyColumns(boolean keep) {
    this.keepAllJoinKeyColumns = keep;
    return this;
  }

  /**
   * if {@code false} the join will fail if any columns other than the join column have the same
   * name; if {@code true} the join will succeed and duplicate columns are renamed and included in
   * the results. Specifically, the renamed columns are given a are give a prefix and the prefix
   * used is "Tn." where n is the number of the table in the join. The second table is
   * (T2.column_name), for example.
   *
   * <p>See also {@link DataFrameJoiner#keepAllJoinKeyColumns(boolean)} to determine whether to
   * retain the join columns from the second table
   *
   * @param allow true, if columns with duplicate names are to be retained; false otherwise. Default
   *     is false
   * @return this DataFrameJoiner instance
   */
  public DataFrameJoiner allowDuplicateColumnNames(boolean allow) {
    this.allowDuplicateColumnNames = allow;
    return this;
  }

  /**
   * The names of the columns to be joined on in the second (right) table. If this method is not
   * called, they default to the names used for the left table.
   *
   * @param rightJoinColumnNames The names to be used
   * @return This DataFrameJoiner instance
   */
  public DataFrameJoiner rightJoinColumns(String... rightJoinColumnNames) {
    Preconditions.checkNotNull(rightJoinColumnNames);
    this.rightJoinColumnNames = rightJoinColumnNames;
    return this;
  }

  /**
   * The table or tables to be used on the right side of the join. If more than one table is
   * provided, the join is executed repeatedly, merging the next right table with the prior results
   *
   * @param tables The table or tables to be used on the right side
   * @return This DataFrameJoiner instance
   */
  public DataFrameJoiner with(Table... tables) {
    Preconditions.checkNotNull(tables);
    this.rightTables = Arrays.stream(tables).collect(Collectors.toList());
    return this;
  }

  /**
   * Performs the actual join and returns the results
   *
   * @return The combined table
   */
  public Table join() {

    selectJoinStrategy();

    if (!allowDuplicateColumnNames) {
      Set<String> rightJoinColumns = Set.of(rightJoinColumnNames);
      Set<String> leftJoinColumns = Set.of(leftJoinColumnNames);
      Set<String> nonJoinColumns =
          table.columnNames().stream()
              .filter(e -> !leftJoinColumns.contains(e))
              .collect(Collectors.toSet());

      for (Table t : rightTables) {
        List<String> names =
            t.columnNames().stream()
                .filter(e -> !rightJoinColumns.contains(e))
                .collect(Collectors.toList());
        for (String nm : names) {
          if (!nonJoinColumns.contains(nm)) {
            nonJoinColumns.add(nm);
          } else {
            throw new IllegalArgumentException(
                "Attempting to join tables containing non-join columns with at least one name: "
                    + nm
                    + " appears in more than one table. "
                    + "If you would like to join tables containing columns with duplicate names, "
                    + " the value of 'allowDuplicateColumnNames' must be true");
          }
        }
      }
    }
    return performJoin(table, rightTables);
  }

  private void selectJoinStrategy() {
    // System.out.println(table);
    // System.out.println(rightTables.get(0));

    int leftRowCount = table.rowCount();
    int rightRowCount = rightTables.get(0).rowCount();

    int minCardinalityLeft = Integer.MAX_VALUE;
    int minCardinalityRight = Integer.MAX_VALUE;

    for (int i = 0; i < rightJoinColumnNames.length; i++) {
      int cardinality = table.column(leftJoinColumnNames[i]).countUnique();
      if (cardinality < minCardinalityLeft) {
        minCardinalityLeft = cardinality;
      }
    }
    for (String rightJoinColumnName : rightJoinColumnNames) {
      int cardinality = rightTables.get(0).column(rightJoinColumnName).countUnique();
      if (cardinality < minCardinalityRight) {
        minCardinalityRight = cardinality;
      }
    }
    //    System.out.println("min cardinality left " + minCardinalityLeft);
    //    System.out.println("min cardinality right " + minCardinalityRight);
    //    System.out.println("Avg values left " + (leftRowCount/(minCardinalityLeft * 1.0)));
    //    System.out.println("Avg values right " + rightRowCount / minCardinalityRight);
    if ((leftRowCount / (minCardinalityLeft * 1.0)) > 1000
        || (rightRowCount / (minCardinalityRight * 1.0) > 1000)) {
      this.strategy = new SortMergeJoin(table, leftJoinColumnNames);
    } else {
      this.strategy = new CrossProductJoin(table, leftJoinColumnNames);
    }
    //    System.out.println(strategy + " selected.");
  }

  /**
   * Finds the position of the columns corresponding to the columnNames. E.G. The column named "ID"
   * is located at position 5 (0-based) in the table.
   *
   * @param table the table that contains the columns.
   * @param columnNames the column names to find position of.
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
   * Recursively joins the table on the left with each of the tables on the right, substituting the
   * result of the nth join as the left table for the nth + 1 join
   *
   * @param left The first (left) table participating in the join
   * @param rightTables One or more tables to be joined with the first (left) table
   * @return this DataFrameJoiner instance
   */
  private Table performJoin(Table left, List<Table> rightTables) {
    Table result =
        joinInternal(
            left,
            rightTables.remove(0),
            joinType,
            allowDuplicateColumnNames,
            keepAllJoinKeyColumns,
            rightJoinColumnNames);
    if (rightTables.isEmpty()) {
      return result;
    } else {
      // on subsequent calls, the left column may have a new structure
      this.leftJoinColumnPositions = getJoinIndexes(result, leftJoinColumnNames);
      return performJoin(result, rightTables);
    }
  }

  @Override
  Table joinInternal(
      Table table1,
      Table table2,
      JoinType joinType,
      boolean allowDuplicates,
      boolean keepAllJoinKeyColumns,
      String[] rightJoinColumnPositions) {
    return strategy.performJoin(
        table1,
        table2,
        joinType,
        allowDuplicates,
        keepAllJoinKeyColumns,
        leftJoinColumnPositions,
        rightJoinColumnPositions);
  }
}
