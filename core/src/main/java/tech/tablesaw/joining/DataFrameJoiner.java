package tech.tablesaw.joining;

import com.google.common.collect.Streams;
import com.google.common.primitives.Ints;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.InstantColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.booleans.BooleanColumnType;
import tech.tablesaw.columns.dates.DateColumnType;
import tech.tablesaw.columns.datetimes.DateTimeColumnType;
import tech.tablesaw.columns.instant.InstantColumnType;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.FloatColumnType;
import tech.tablesaw.columns.numbers.IntColumnType;
import tech.tablesaw.columns.numbers.LongColumnType;
import tech.tablesaw.columns.numbers.ShortColumnType;
import tech.tablesaw.columns.strings.StringColumnType;
import tech.tablesaw.columns.strings.TextColumnType;
import tech.tablesaw.columns.times.TimeColumnType;
import tech.tablesaw.index.ByteIndex;
import tech.tablesaw.index.DoubleIndex;
import tech.tablesaw.index.FloatIndex;
import tech.tablesaw.index.Index;
import tech.tablesaw.index.IntIndex;
import tech.tablesaw.index.LongIndex;
import tech.tablesaw.index.ShortIndex;
import tech.tablesaw.index.StringIndex;
import tech.tablesaw.selection.Selection;

public class DataFrameJoiner {

  private enum JoinType {
    INNER,
    LEFT_OUTER,
    RIGHT_OUTER,
    FULL_OUTER
  }

  private static final String TABLE_ALIAS = "T";

  private final Table table;
  private final String[] joinColumnNames;
  private final List<Integer> joinColumnIndexes;
  private final AtomicInteger joinTableId = new AtomicInteger(2);

  /**
   * Constructor.
   *
   * @param table The table to join on.
   * @param joinColumnNames The join column names to join on.
   */
  public DataFrameJoiner(Table table, String... joinColumnNames) {
    this.table = table;
    this.joinColumnNames = joinColumnNames;
    this.joinColumnIndexes = getJoinIndexes(table, joinColumnNames);
  }

  /**
   * Finds the index of the columns corresponding to the columnNames. E.G. The column named "ID" is
   * located at index 5 in table.
   *
   * @param table the table that contains the columns.
   * @param columnNames the column names to find indexes of.
   * @return a list of column indexes within the table.
   */
  private List<Integer> getJoinIndexes(Table table, String[] columnNames) {
    return Arrays.stream(columnNames).map(table::columnIndex).collect(Collectors.toList());
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
          joinInternal(joined, currT, JoinType.INNER, allowDuplicateColumnNames, joinColumnNames);
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
    joinedTable = joinInternal(table, table2, JoinType.INNER, allowDuplicateColumnNames, col2Names);
    return joinedTable;
  }

  /**
   * Joins two tables.
   *
   * @param table1 the table on the left side of the join.
   * @param table2 the table on the right side of the join.
   * @param joinType the type of join.
   * @param allowDuplicates if {@code false} the join will fail if any columns other than the join
   *     column have the same name if {@code true} the join will succeed and duplicate columns are
   *     renamed
   * @param table2JoinColumnNames The names of the columns in table2 to join on.
   */
  private Table joinInternal(
      Table table1,
      Table table2,
      JoinType joinType,
      boolean allowDuplicates,
      String... table2JoinColumnNames) {
    List<Integer> table2JoinColumnIndexes = getJoinIndexes(table2, table2JoinColumnNames);

    Table result = Table.create(table1.name());
    // A set of column indexes in the result table that can be ignored. They are duplicate join
    // keys.
    Set<Integer> resultIgnoreColIndexes =
        emptyTableFromColumns(
            result, table1, table2, joinType, allowDuplicates, table2JoinColumnIndexes);

    List<Index> table1Indexes = buildIndexesForJoinColumns(joinColumnIndexes, table1);
    List<Index> table2Indexes = buildIndexesForJoinColumns(table2JoinColumnIndexes, table2);
    validateIndexes(table1Indexes, table2Indexes);

    if (table1.rowCount() == 0 &&
        (joinType == JoinType.LEFT_OUTER || joinType == JoinType.INNER)) {
      // Handle special case of empty table here so it doesn't fall through to the behavior
      // that adds rows for full outer and right outer joins 
      result.removeColumns(Ints.toArray(resultIgnoreColIndexes));
      return result;
    }

    Selection table1DoneRows = Selection.with();
    Selection table2DoneRows = Selection.with();
    for (Row row : table1) {
      int ri = row.getRowNumber();
      if (table1DoneRows.contains(ri)) {
        // Already processed a selection of table1 that contained this row.
        continue;
      }

      Selection table1Rows = createMultiColSelection(table1, ri, table1Indexes, table1.rowCount());
      Selection table2Rows = createMultiColSelection(table1, ri, table2Indexes, table2.rowCount());

      if ((joinType == JoinType.LEFT_OUTER || joinType == JoinType.FULL_OUTER)
          && table2Rows.isEmpty()) {
        withMissingLeftJoin(result, table1, table1Rows, resultIgnoreColIndexes);
      } else {
        crossProduct(result, table1, table2, table1Rows, table2Rows, resultIgnoreColIndexes);
      }

      table1DoneRows = table1DoneRows.or(table1Rows);
      if (joinType == JoinType.FULL_OUTER || joinType == JoinType.RIGHT_OUTER) {
        // Update done rows in table2 for full Outer.
        table2DoneRows = table2DoneRows.or(table2Rows);
      } else if (table1DoneRows.size() == table1.rowCount()) {
        // Processed all the rows in table1 exit early.
        result.removeColumns(Ints.toArray(resultIgnoreColIndexes));
        return result;
      }
    }

    // Add all rows from table2 that were not handled already.
    Selection table2Rows = table2DoneRows.flip(0, table2.rowCount());
    withMissingRight(
        result,
        table1.columnCount(),
        table2,
        table2Rows,
        joinType,
        table2JoinColumnIndexes,
        resultIgnoreColIndexes);
    result.removeColumns(Ints.toArray(resultIgnoreColIndexes));
    return result;
  }

  private void validateIndexes(List<Index> table1Indexes, List<Index> table2Indexes) {
    if (table1Indexes.size() != table2Indexes.size()) {
      throw new IllegalArgumentException(
          "Cannot join using a different number of indices on each table: "
              + table1Indexes
              + " and "
              + table2Indexes);
    }
    for (int i = 0; i < table1Indexes.size(); i++) {
      if (!table1Indexes.get(i).getClass().equals(table2Indexes.get(i).getClass())) {
        throw new IllegalArgumentException(
            "Cannot join using different index types: " + table1Indexes + " and " + table2Indexes);
      }
    }
  }

  /** Build a reverse index for every join column in the table. */
  private List<Index> buildIndexesForJoinColumns(List<Integer> joinColumnIndexes, Table table) {
    return joinColumnIndexes.stream().map(c -> indexFor(table, c)).collect(Collectors.toList());
  }

  /** Create a reverse index for a given column. */
  private Index indexFor(Table table, int colIndex) {
    ColumnType type = table.column(colIndex).type();
    if (type instanceof DateColumnType) {
      return new IntIndex(table.dateColumn(colIndex));
    } else if (type instanceof DateTimeColumnType) {
      return new LongIndex(table.dateTimeColumn(colIndex));
    } else if (type instanceof InstantColumnType) {
      return new LongIndex(table.instantColumn(colIndex));
    } else if (type instanceof TimeColumnType) {
      return new IntIndex(table.timeColumn(colIndex));
    } else if (type instanceof StringColumnType || type instanceof TextColumnType) {
      return new StringIndex(table.stringColumn(colIndex));
    } else if (type instanceof IntColumnType) {
      return new IntIndex(table.intColumn(colIndex));
    } else if (type instanceof LongColumnType) {
      return new LongIndex(table.longColumn(colIndex));
    } else if (type instanceof ShortColumnType) {
      return new ShortIndex(table.shortColumn(colIndex));
    } else if (type instanceof BooleanColumnType) {
      return new ByteIndex(table.booleanColumn(colIndex));
    } else if (type instanceof DoubleColumnType) {
      return new DoubleIndex(table.doubleColumn(colIndex));
    } else if (type instanceof FloatColumnType) {
      return new FloatIndex(table.floatColumn(colIndex));
    }
    throw new IllegalArgumentException("Joining attempted on unsupported column type " + type);
  }

  /**
   * Given a reverse index find a selection of rows that have the same value as the supplied column
   * does in the given row index.
   */
  private Selection selectionForColumn(Column<?> valueColumn, int rowIndex, Index rawIndex) {

    ColumnType type = valueColumn.type();
    if (type instanceof DateColumnType) {
      IntIndex index = (IntIndex) rawIndex;
      int value = ((DateColumn) valueColumn).getIntInternal(rowIndex);
      return index.get(value);
    } else if (type instanceof TimeColumnType) {
      IntIndex index = (IntIndex) rawIndex;
      int value = ((TimeColumn) valueColumn).getIntInternal(rowIndex);
      return index.get(value);
    } else if (type instanceof DateTimeColumnType) {
      LongIndex index = (LongIndex) rawIndex;
      long value = ((DateTimeColumn) valueColumn).getLongInternal(rowIndex);
      return index.get(value);
    } else if (type instanceof InstantColumnType) {
      LongIndex index = (LongIndex) rawIndex;
      long value = ((InstantColumn) valueColumn).getLongInternal(rowIndex);
      return index.get(value);
    } else if (type instanceof StringColumnType || type instanceof TextColumnType) {
      StringIndex index = (StringIndex) rawIndex;
      String value = ((StringColumn) valueColumn).get(rowIndex);
      return index.get(value);
    } else if (type instanceof IntColumnType) {
      IntIndex index = (IntIndex) rawIndex;
      int value = ((IntColumn) valueColumn).getInt(rowIndex);
      return index.get(value);
    } else if (type instanceof LongColumnType) {
      LongIndex index = (LongIndex) rawIndex;
      long value = ((LongColumn) valueColumn).getLong(rowIndex);
      return index.get(value);
    } else if (type instanceof ShortColumnType) {
      ShortIndex index = (ShortIndex) rawIndex;
      short value = ((ShortColumn) valueColumn).getShort(rowIndex);
      return index.get(value);
    } else if (type instanceof BooleanColumnType) {
      ByteIndex index = (ByteIndex) rawIndex;
      byte value = ((BooleanColumn) valueColumn).getByte(rowIndex);
      return index.get(value);
    } else if (type instanceof DoubleColumnType) {
      DoubleIndex index = (DoubleIndex) rawIndex;
      double value = ((DoubleColumn) valueColumn).getDouble(rowIndex);
      return index.get(value);
    } else if (type instanceof FloatColumnType) {
      FloatIndex index = (FloatIndex) rawIndex;
      float value = ((FloatColumn) valueColumn).getFloat(rowIndex);
      return index.get(value);
    } else {
      throw new IllegalArgumentException(
          "Joining is supported on numeric, string, and date-like columns. Column "
              + valueColumn.name()
              + " is of type "
              + valueColumn.type());
    }
  }

  /** Create a big multicolumn selection for all join columns in the given table. */
  private Selection createMultiColSelection(
      Table table1, int ri, List<Index> indexes, int selectionSize) {
    Selection multiColSelection = Selection.withRange(0, selectionSize);
    int i = 0;
    for (Integer joinColumnIndex : joinColumnIndexes) {
      Column<?> col = table1.column(joinColumnIndex);
      Selection oneColSelection = selectionForColumn(col, ri, indexes.get(i));
      // and the selections.
      multiColSelection = multiColSelection.and(oneColSelection);
      i++;
    }
    return multiColSelection;
  }

  private String newName(String table2Alias, String columnName) {
    return table2Alias + "." + columnName;
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
              joined, currT, JoinType.FULL_OUTER, allowDuplicateColumnNames, joinColumnNames);
    }
    return joined;
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
    return joinInternal(table, table2, JoinType.FULL_OUTER, false, col2Name);
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
      joined = leftOuter(table2, allowDuplicateColumnNames, joinColumnNames);
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
    return joinInternal(table, table2, JoinType.LEFT_OUTER, allowDuplicateColumnNames, col2Names);
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
      joined = rightOuter(table2, allowDuplicateColumnNames, joinColumnNames);
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
   * @return The resulting table
   */
  public Table rightOuter(Table table2, boolean allowDuplicateColumnNames, String... col2Names) {
    return joinInternal(table, table2, JoinType.RIGHT_OUTER, allowDuplicateColumnNames, col2Names);
  }

  /**
   * Adds empty columns to the destination table with the same type as columns in table1 and table2.
   *
   * <p>For inner, left and full outer join types the join columns in table2 are not needed and will
   * be marked as placeholders. The indexes of those columns will be returned. The downstream logic
   * is easier if we wait to remove the redundant columns until the last step.
   *
   * @param destination the table to fill up with columns. Will be mutated in place.
   * @param table1 the table on left side of the join.
   * @param table2 the table on the right side of the join.
   * @param joinType the type of join.
   * @param allowDuplicates whether to allow duplicates. If yes rename columns in table2 that have
   *     the same name as columns in table1 with the exception of join columns in table2 when
   *     performing a right join.
   * @param table2JoinColumnIndexes the index locations of the table2 join columns.
   * @return A
   */
  private Set<Integer> emptyTableFromColumns(
      Table destination,
      Table table1,
      Table table2,
      JoinType joinType,
      boolean allowDuplicates,
      List<Integer> table2JoinColumnIndexes) {

    Column<?>[] cols =
        Streams.concat(table1.columns().stream(), table2.columns().stream())
            .map(Column::emptyCopy)
            .toArray(Column[]::new);

    // For inner join, left join and full outer join mark the join columns in table2 as
    // placeholders.
    // For right join mark the join columns in table1 as placeholders.
    // Keep track of which join columns are placeholders so they can be ignored.
    Set<Integer> ignoreColumns = new HashSet<>();
    for (int c = 0; c < cols.length; c++) {
      if (joinType == JoinType.RIGHT_OUTER) {
        if (c < table1.columnCount() && joinColumnIndexes.contains(c)) {
          cols[c].setName("Placeholder_" + ignoreColumns.size());
          ignoreColumns.add(c);
        }
      } else {
        int table2Index = c - table1.columnCount();
        if (c >= table1.columnCount() && table2JoinColumnIndexes.contains(table2Index)) {
          cols[c].setName("Placeholder_" + ignoreColumns.size());
          ignoreColumns.add(c);
        }
      }
    }

    // Rename duplicate columns in second table
    if (allowDuplicates) {
      Set<String> table1ColNames =
          Arrays.stream(cols)
              .map(Column::name)
              .map(String::toLowerCase)
              .limit(table1.columnCount())
              .collect(Collectors.toSet());

      String table2Alias = TABLE_ALIAS + joinTableId.getAndIncrement();
      for (int c = table1.columnCount(); c < cols.length; c++) {
        String columnName = cols[c].name();
        if (table1ColNames.contains(columnName.toLowerCase())) {
          cols[c].setName(newName(table2Alias, columnName));
        }
      }
    }
    destination.addColumns(cols);
    return ignoreColumns;
  }

  /**
   * Creates cross product for the selection of two tables.
   *
   * @param destination the destination table.
   * @param table1 the table on left of join.
   * @param table2 the table on right of join.
   * @param table1Rows the selection of rows in table1.
   * @param table2Rows the selection of rows in table2.
   * @param ignoreColumns a set of column indexes in the result to ignore. They are redundant join
   *     columns.
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  private void crossProduct(
      Table destination,
      Table table1,
      Table table2,
      Selection table1Rows,
      Selection table2Rows,
      Set<Integer> ignoreColumns) {
    for (int c = 0; c < table1.columnCount() + table2.columnCount(); c++) {
      if (ignoreColumns.contains(c)) {
        continue;
      }
      int table2Index = c - table1.columnCount();
      for (int r1 : table1Rows) {
        for (int r2 : table2Rows) {
          if (c < table1.columnCount()) {
            Column t1Col = table1.column(c);
            destination.column(c).append(t1Col, r1);
          } else {
            Column t2Col = table2.column(table2Index);
            destination.column(c).append(t2Col, r2);
          }
        }
      }
    }
  }

  /**
   * Adds rows to destination for each row in table1 with the columns from table2 added as missing
   * values.
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  private void withMissingLeftJoin(
      Table destination, Table table1, Selection table1Rows, Set<Integer> ignoreColumns) {
    for (int c = 0; c < destination.columnCount(); c++) {
      if (ignoreColumns.contains(c)) {
        continue;
      }
      if (c < table1.columnCount()) {
        Column t1Col = table1.column(c);
        for (int index : table1Rows) {
          destination.column(c).append(t1Col, index);
        }
      } else {
        for (int r1 = 0; r1 < table1Rows.size(); r1++) {
          destination.column(c).appendMissing();
        }
      }
    }
  }

  /**
   * Adds rows to destination for each row in table2 with the columns from table1 added as missing
   * values.
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  private void withMissingRight(
      Table destination,
      int table1ColCount,
      Table table2,
      Selection table2Rows,
      JoinType joinType,
      List<Integer> col2Indexes,
      Set<Integer> ignoreColumns) {

    // Add index data from table2 into join column positions in table one.
    if (joinType == JoinType.FULL_OUTER) {
      for (int i = 0; i < col2Indexes.size(); i++) {
        Column t2Col = table2.column(col2Indexes.get(i));
        for (int index : table2Rows) {
          destination.column(joinColumnIndexes.get(i)).append(t2Col, index);
        }
      }
    }

    for (int c = 0; c < destination.columnCount(); c++) {
      if (ignoreColumns.contains(c) || joinColumnIndexes.contains(c)) {
        continue;
      }
      if (c < table1ColCount) {
        for (int r1 = 0; r1 < table2Rows.size(); r1++) {
          destination.column(c).appendMissing();
        }
      } else {
        Column t2Col = table2.column(c - table1ColCount);
        for (int index : table2Rows) {
          destination.column(c).append(t2Col, index);
        }
      }
    }
  }
}
