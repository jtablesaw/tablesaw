package tech.tablesaw.joining;

import static tech.tablesaw.joining.JoinType.INNER;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import tech.tablesaw.api.*;

/** Implements joins between two or more Tables */
public class DataFrameJoiner extends AbstractJoiner {

  private final JoinStrategy strategy;

  private final Table table;
  private final String[] leftJoinColumnNames;
  private int[] leftJoinColumnIndexes;

  private JoinType joinType = INNER;
  private boolean allowDuplicateColumnNames = false;
  private boolean keepAllJoinKeyColumns = false;
  private String[] rightJoinColumnNames = new String[0];
  private List<Table> rightTables = new ArrayList<>();

  /**
   * Constructor.
   *
   * @param table The table to join on.
   * @param leftJoinColumnNames The join column names to join on.
   */
  public DataFrameJoiner(Table table, String... leftJoinColumnNames) {
    this.table = table;
    this.leftJoinColumnNames = leftJoinColumnNames;

    // we assume the join columns have the same names, unless names for the right table are
    // explicitly set
    this.rightJoinColumnNames = leftJoinColumnNames;

    this.leftJoinColumnIndexes = getJoinIndexes(table, leftJoinColumnNames);

    // TODO: Move this decision to the join() method?
    this.strategy = new SortMergeJoin(table, leftJoinColumnNames);
    // this.strategy = new CrossProductJoin(table, joinColumnNames);
  }

  public DataFrameJoiner type(JoinType joinType) {
    this.joinType = joinType;
    return this;
  }

  public DataFrameJoiner keepAllJoinKeyColumns(boolean keep) {
    this.keepAllJoinKeyColumns = keep;
    return this;
  }

  public DataFrameJoiner allowDuplicateColumnNames(boolean allow) {
    this.allowDuplicateColumnNames = allow;
    return this;
  }

  public DataFrameJoiner rightJoinColumns(String... rightJoinColumnNames) {
    this.rightJoinColumnNames = rightJoinColumnNames;
    return this;
  }

  public DataFrameJoiner with(Table... tables) {
    Preconditions.checkNotNull(tables);
    this.rightTables = Arrays.stream(tables).collect(Collectors.toList());
    ;
    return this;
  }

  public Table join() {
    return performJoin(table, rightTables);
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
      return performJoin(result, rightTables);
    }
  }

  Table joinInternal(
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
}
