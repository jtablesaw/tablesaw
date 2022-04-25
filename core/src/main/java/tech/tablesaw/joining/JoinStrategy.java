package tech.tablesaw.joining;

import tech.tablesaw.api.Table;

interface JoinStrategy {

  Table performJoin(
      Table table1,
      Table table2,
      JoinType joinType,
      boolean allowDuplicates,
      boolean keepAllJoinKeyColumns,
      int[] leftJoinColumnIndexes,
      String... table2JoinColumnNames);
}
