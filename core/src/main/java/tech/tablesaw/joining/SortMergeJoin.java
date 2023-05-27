package tech.tablesaw.joining;

import com.google.common.collect.Streams;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

/** Implements joins between two or more Tables */
class SortMergeJoin implements JoinStrategy {

  private static final String LEFT_RECORD_ID_NAME = "_left_record_id_";
  private static final String RIGHT_RECORD_ID_NAME = "_right_record_id_";

  private static final String TABLE_ALIAS = "T";
  public static final String PLACEHOLDER_COL_PREFIX = "Placeholder_";

  private final String[] leftjoinColumnNames;
  private int[] leftJoinColumnPositions;
  private int[] rightJoinColumnPositions;

  private final AtomicInteger joinTableId = new AtomicInteger(1);

  /**
   * Constructor.
   *
   * @param table The table to join on.
   * @param joinColumnNames The join column names to join on.
   */
  public SortMergeJoin(Table table, String... joinColumnNames) {
    this.leftJoinColumnPositions = getJoinIndexes(table, joinColumnNames);
    this.leftjoinColumnNames = joinColumnNames;
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
   * Joins two tables.
   *
   * @param t1 the table on the left side of the join.
   * @param t2 the table on the right side of the join.
   * @param joinType the type of join.
   * @param allowDuplicates if {@code false} the join will fail if any columns other than the join
   *     column have the same name if {@code true} the join will succeed and duplicate columns are
   *     renamed
   * @param keepAllJoinKeyColumns if {@code false} the join will only keep join key columns in
   *     table1 if {@code true} the join will return all join key columns in both table, which may
   *     have difference when there are null values
   * @param table2JoinColumnNames The names of the columns in table2 to join on.
   * @return the joined table
   */
  public Table performJoin(
      Table t1,
      Table t2,
      JoinType joinType,
      boolean allowDuplicates,
      boolean keepAllJoinKeyColumns,
      int[] leftJoinColumnIndexes,
      String... table2JoinColumnNames) {

    this.leftJoinColumnPositions = leftJoinColumnIndexes;
    rightJoinColumnPositions = getJoinIndexes(t2, table2JoinColumnNames);

    Table table1 = t1.sortAscendingOn(leftjoinColumnNames);
    Table table2 = t2.sortAscendingOn(table2JoinColumnNames);

    Column<?>[] cols =
        Streams.concat(table1.columns().stream(), table2.columns().stream())
            .map(Column::emptyCopy)
            .toArray(Column[]::new);

    // A set of column indexes in the result table that can be ignored. They are duplicate join
    // keys.
    int[] resultIgnoreColIndexes =
        keepAllJoinKeyColumns ? new int[0] : getIgnoredColumns(table1, joinType, cols);

    Table result = emptyTableFromColumns(table1, allowDuplicates, cols);

    // add indexes for outer join processing
    IntColumn indexLeft = IntColumn.indexColumn(LEFT_RECORD_ID_NAME, table1.rowCount(), 0);
    table1.addColumns(indexLeft);
    result.addColumns(IntColumn.create(LEFT_RECORD_ID_NAME));

    IntColumn indexRight = IntColumn.indexColumn(RIGHT_RECORD_ID_NAME, table2.rowCount(), 0);
    table2.addColumns(indexRight);
    result.addColumns(IntColumn.create(RIGHT_RECORD_ID_NAME));

    validateJoinColumns(table1, table2);

    if (table1.rowCount() == 0 && (joinType == JoinType.LEFT_OUTER || joinType == JoinType.INNER)) {
      // Handle special case of empty table here so it doesn't fall through to the behavior
      // that adds rows for full outer and right outer joins
      if (!keepAllJoinKeyColumns) {
        result.removeColumns(resultIgnoreColIndexes);
      }
      return result;
    }
    SortMergeJoinPerformer sortMergeJoinPerformer = new SortMergeJoinPerformer(joinType, LEFT_RECORD_ID_NAME,
            RIGHT_RECORD_ID_NAME, leftJoinColumnPositions, rightJoinColumnPositions);
    sortMergeJoinPerformer.join(result, table1, table2, resultIgnoreColIndexes);
    result.removeColumns(LEFT_RECORD_ID_NAME, RIGHT_RECORD_ID_NAME);

    if (!keepAllJoinKeyColumns) {
      result = result.removeColumns(resultIgnoreColIndexes);
    } else {
      renameJoinColumns(result, table1, resultIgnoreColIndexes);
    }
    return result;
  }

  /**
   * Renames the column indexes for the second table from Placeholder_X to their original names
   *
   * @param resultIgnoreColIndexes The positions of the secondary join columns
   */
  private void renameJoinColumns(Table result, Table left, int[] resultIgnoreColIndexes) {

    String table2Alias = TABLE_ALIAS + joinTableId.get();

    for (int position : resultIgnoreColIndexes) {
      String realName = result.column(position).name().replace(PLACEHOLDER_COL_PREFIX, "");
      if (position >= left.columnCount()) {
        if (result.containsColumn(realName.toLowerCase())) {
          result.column(position).setName(newName(table2Alias, realName));
        } else {
          result.column(position).setName(realName);
        }
      } else {
        result.column(position).setName(realName);
      }
    }
  }

  private String newName(String table2Alias, String columnName) {
    return table2Alias + "." + columnName;
  }

  /**
   * Adds empty columns to the destination table with the same type as columns in table1 and table2.
   *
   * <p>For inner, left and full outer join types the join columns in table2 are not needed and will
   * be marked as placeholders. The indexes of those columns will be returned. The downstream logic
   * is easier if we wait to remove the redundant columns until the last step.
   *
   * @param table1 the table on left side of the join.
   * @param allowDuplicates whether to allow duplicates. If yes rename columns in table2 that have
   *     the same name as columns in table1, with the exception of join columns in table2 when
   *     performing a right join.
   * @param cols An array of columns from both join tables
   * @return the table to use for the join results
   */
  Table emptyTableFromColumns(Table table1, boolean allowDuplicates, Column<?>[] cols) {

    Table destination = Table.create(table1.name());

    // Rename duplicate columns in second table
    if (allowDuplicates) {
      Set<String> table1ColNames =
          Arrays.stream(cols)
              .map(Column::name)
              .map(String::toLowerCase)
              .limit(table1.columnCount())
              .collect(Collectors.toSet());

      String table2Alias = TABLE_ALIAS + joinTableId.incrementAndGet();
      for (int c = table1.columnCount(); c < cols.length; c++) {
        String columnName = cols[c].name();
        if (table1ColNames.contains(columnName.toLowerCase())) {
          cols[c].setName(newName(table2Alias, columnName));
        }
      }
    }
    destination.addColumns(cols);
    return destination;
  }

  /**
   * Returns the positions of columns that can be ignored in the result table
   *
   * <p>For inner join, left join and full outer join mark the join columns in table2 as
   * placeholders.
   *
   * <p>For right join, mark the join columns in table1 as placeholders. Keep track of which join
   * columns are placeholders so they can be ignored.
   */
  private int[] getIgnoredColumns(Table table1, JoinType joinType, Column<?>[] cols) {
    int[] ignoreColumns = new int[leftJoinColumnPositions.length];
    int ignoreIndex = 0;
    for (int c = 0; c < cols.length; c++) {
      if (joinType == JoinType.RIGHT_OUTER) {
        if (c < table1.columnCount() && indexesContainsValue(leftJoinColumnPositions, c)) {
          ignoreColumns[ignoreIndex] = c;
          cols[c].setName(PLACEHOLDER_COL_PREFIX + cols[c].name());
          ignoreIndex++;
        }
      } else { // JoinType is LEFT, INNER, or FULL
        int table2Index = c + table1.columnCount();
        if (indexesContainsValue(rightJoinColumnPositions, c)) {
          ignoreColumns[ignoreIndex] = table2Index;
          cols[table2Index].setName(PLACEHOLDER_COL_PREFIX + cols[table2Index].name());
          ignoreIndex++;
        }
      }
    }
    return ignoreColumns;
  }

  private boolean indexesContainsValue(int[] joinColumnIndexes, int columnIndex) {
    for (int i : joinColumnIndexes) {
      if (columnIndex == i) {
        return true;
      }
    }
    return false;
  }

  private void validateJoinColumns(Table table1, Table table2) {
    if (leftJoinColumnPositions.length != rightJoinColumnPositions.length) {
      throw new IllegalArgumentException(
          "Cannot join using a different number of indices on each table: "
              + Arrays.toString(leftJoinColumnPositions)
              + " and "
              + Arrays.toString(rightJoinColumnPositions));
    }
    for (int i = 0; i < leftJoinColumnPositions.length; i++) {
      if (!table1
          .column(leftJoinColumnPositions[i])
          .getClass()
          .equals(table2.column(rightJoinColumnPositions[i]).getClass())) {
        throw new IllegalArgumentException(
            "Cannot join using different index types: "
                + Arrays.toString(leftJoinColumnPositions)
                + " and "
                + Arrays.toString(rightJoinColumnPositions));
      }
    }
  }

  @Override
  public String toString() {
    return "SortMergeJoin";
  }
}
