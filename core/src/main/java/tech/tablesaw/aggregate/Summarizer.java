/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.aggregate;

import static tech.tablesaw.api.QuerySupport.numberColumn;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;
import tech.tablesaw.table.StandardTableSliceGroup;
import tech.tablesaw.table.TableSliceGroup;

/**
 * Summarizes the data in a table, by applying functions to a subset of its columns.
 *
 * <p>How to use:
 *
 * <p>1. Create an instance providing a source table, the column or columns to summarize, and a
 * function or functions to apply 2. Applying the functions to the designated columns, possibly
 * creating subgroup summaries using one of the by() methods
 */
public class Summarizer {

  private String[] groupColumnNames = new String[0];
  private final Table original;
  private Table temp;
  private final List<String> summarizedColumns = new ArrayList<>();
  private final AggregateFunction<?, ?>[] reductions;
  private static final String GROUP_COL_TEMP_NAME = "_temp_group_col_";

  /**
   * Returns an object capable of summarizing the given column in the given sourceTable, by applying
   * the given functions
   */
  public Summarizer(Table sourceTable, Column<?> column, AggregateFunction<?, ?>... functions) {
    Table tempTable = Table.create(sourceTable.name());
    tempTable.addColumns(column);
    this.temp = tempTable;
    this.original = sourceTable;
    summarizedColumns.add(column.name());
    this.reductions = functions;
  }

  /**
   * Returns an object capable of summarizing the given column in the given sourceTable, by applying
   * the given functions
   */
  public Summarizer(
      Table sourceTable, List<String> columnNames, AggregateFunction<?, ?>... functions) {
    Table tempTable = Table.create(sourceTable.name());
    for (String nm : columnNames) {
      tempTable.addColumns(sourceTable.column(nm));
    }
    this.temp = tempTable;
    this.original = sourceTable;
    summarizedColumns.addAll(columnNames);
    this.reductions = functions;
  }

  /**
   * Returns an object capable of summarizing the given columns in the given sourceTable, by
   * applying the given functions
   */
  public Summarizer(
      Table sourceTable,
      Column<?> column1,
      Column<?> column2,
      AggregateFunction<?, ?>... functions) {
    Table tempTable = Table.create(sourceTable.name());
    tempTable.addColumns(column1);
    tempTable.addColumns(column2);
    this.temp = tempTable;
    this.original = sourceTable;
    summarizedColumns.add(column1.name());
    summarizedColumns.add(column2.name());
    this.reductions = functions;
  }

  /**
   * Returns an object capable of summarizing the given columns in the given sourceTable, by
   * applying the given functions
   */
  public Summarizer(
      Table sourceTable,
      Column<?> column1,
      Column<?> column2,
      Column<?> column3,
      Column<?> column4,
      AggregateFunction<?, ?>... functions) {
    Preconditions.checkArgument(!sourceTable.isEmpty(), "The table to summarize is empty.");
    Table tempTable = Table.create(sourceTable.name());
    tempTable.addColumns(column1);
    tempTable.addColumns(column2);
    tempTable.addColumns(column3);
    tempTable.addColumns(column4);
    this.temp = tempTable;
    this.original = sourceTable;
    summarizedColumns.add(column1.name());
    summarizedColumns.add(column2.name());
    summarizedColumns.add(column3.name());
    summarizedColumns.add(column4.name());
    this.reductions = functions;
  }

  /**
   * Returns an object capable of summarizing the given column2 in the given sourceTable, by
   * applying the given functions
   */
  public Summarizer(
      Table sourceTable,
      Column<?> column1,
      Column<?> column2,
      Column<?> column3,
      AggregateFunction<?, ?>... functions) {
    Table tempTable = Table.create(sourceTable.name());
    tempTable.addColumns(column1);
    tempTable.addColumns(column2);
    tempTable.addColumns(column3);
    this.temp = tempTable;
    this.original = sourceTable;
    summarizedColumns.add(column1.name());
    summarizedColumns.add(column2.name());
    summarizedColumns.add(column3.name());
    this.reductions = functions;
  }

  /**
   * Similar in intent to the SQL "group by" statement, it produces a table with one row for each
   * subgroup of the output data containing the result of applying the summary functions to the
   * subgroup
   *
   * @param columnNames The names of the columns to group on
   * @return A table containing the grouped results
   */
  public Table by(String... columnNames) {
    for (String columnName : columnNames) {
      if (tableDoesNotContain(columnName, temp)) {
        temp.addColumns(original.column(columnName));
      }
    }
    TableSliceGroup group = StandardTableSliceGroup.create(temp, columnNames);
    return summarize(group);
  }

  /**
   * Similar in intent to the SQL "group by" statement, it produces a table with one row for each
   * subgroup of the output data containing the result of applying the summary functions to the
   * subgroup
   *
   * @param columns The columns to group on
   * @return A table containing the grouped results
   */
  public Table by(CategoricalColumn<?>... columns) {
    for (Column<?> c : columns) {
      if (!temp.containsColumn(c)) {
        temp.addColumns(c);
      }
    }
    TableSliceGroup group = StandardTableSliceGroup.create(temp, columns);
    return summarize(group);
  }

  private Table getSummaryTable(IntColumn groupColumn) {
    TableSliceGroup group = StandardTableSliceGroup.create(temp, groupColumn);
    return summarize(group);
  }

  /**
   * Returns a summary of the records grouped into subsets of the same size, in the order they
   * appear
   *
   * <p>All groups have the same number of records. If the final group has fewer than step records
   * it is dropped.
   *
   * @param step the number or records to include in each group
   */
  public Table by(int step) {

    IntColumn groupColumn = assignToGroupsByStep(step);
    Table t = getSummaryTable(groupColumn);
    t.column(GROUP_COL_TEMP_NAME).setName("Group");
    return t;
  }

  /**
   * Returns the result of applying to the functions to all the values in the appropriate column
   * TODO add a test that uses a non numeric return type with apply
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public Table apply() {

    if (groupColumnNames.length > 0) {
      TableSliceGroup group = StandardTableSliceGroup.create(temp, groupColumnNames);
      return summarize(group);
    } else {
      List<Table> results = new ArrayList<>();
      ArrayListMultimap<String, AggregateFunction<?, ?>> reductionMultimap =
          getAggregateFunctionMultimap();

      for (String name : reductionMultimap.keys()) {
        List<AggregateFunction<?, ?>> reductions = reductionMultimap.get(name);
        Table table = TableSliceGroup.summaryTableName(temp);
        for (AggregateFunction function : reductions) {
          Column column = temp.column(name);
          Object result = function.summarize(column);
          ColumnType type = function.returnType();
          Column newColumn =
              type.create(TableSliceGroup.aggregateColumnName(name, function.functionName()));
          if (result instanceof Number) {
            Number number = (Number) result;
            newColumn.append(number.doubleValue());
          } else {
            newColumn.append(result);
          }
          table.addColumns(newColumn);
        }
        results.add(table);
      }
      return (combineTables(results));
    }
  }

  /**
   * Similar in intent to the SQL having command, it enables the user to apply a filter to the
   * grouped results of summary. Only groups that pass the filter are included in the output.
   *
   * @param selection A {@link Selection} where each index provided refers to a row in the output
   *     table
   * @return A table of filtered summarized data
   */
  public Table having(Function<Table, Selection> selection) {
    Preconditions.checkState(
        groupColumnNames.length > 0,
        "Cannot perform having() on summary that has not been grouped first");

    if (groupColumnNames[0].equals(GROUP_COL_TEMP_NAME)) {
      IntColumn groupColumn = temp.intColumn(GROUP_COL_TEMP_NAME);
      TableSliceGroup group = StandardTableSliceGroup.create(temp, groupColumn);
      return summarizeForHaving(group, selection);
    } else {
      TableSliceGroup group = StandardTableSliceGroup.create(temp, groupColumnNames);
      return summarizeForHaving(group, selection);
    }
  }

  /** TODO: research how the groupBy() methods differ from the by() methods? Are they synonyms? */
  public Summarizer groupBy(CategoricalColumn<?>... columns) {
    groupColumnNames = new String[columns.length];
    for (int i = 0; i < columns.length; i++) {
      Column<?> c = columns[i];
      if (!temp.containsColumn(c)) {
        temp.addColumns(c);
        groupColumnNames[i] = c.name();
      }
    }
    return this;
  }

  /** TODO: research how the groupBy() methods differ from the by() methods? Are they synonyms? */
  public Summarizer groupBy(String... columnNames) {
    for (String columnName : columnNames) {
      if (tableDoesNotContain(columnName, temp)) {
        temp.addColumns(original.column(columnName));
      }
    }
    groupColumnNames = columnNames;
    return this;
  }

  /** TODO: research how the groupBy() methods differ from the by() methods? Are they synonyms? */
  public Summarizer groupBy(int step) {
    IntColumn groupColumn = assignToGroupsByStep(step);
    if (tableDoesNotContain(groupColumn.name(), temp)) {
      temp.addColumns(groupColumn);
    }
    groupColumnNames = new String[] {GROUP_COL_TEMP_NAME};

    return this;
  }

  /**
   * Associates the columns to be summarized with the functions that match their type. All valid
   * combinations are used
   *
   * @param group A table slice group
   * @param selectionFunction Function that provides the filter for the having clause
   * @return A table containing a row of summarized data for each group in the table slice group
   */
  private Table summarizeForHaving(
      TableSliceGroup group, Function<Table, Selection> selectionFunction) {
    List<Table> results = new ArrayList<>();

    ArrayListMultimap<String, AggregateFunction<?, ?>> reductionMultimap =
        getAggregateFunctionMultimap();

    for (String name : reductionMultimap.keys()) {
      List<AggregateFunction<?, ?>> reductions = reductionMultimap.get(name);
      Table groupTable = group.aggregate(name, reductions.toArray(new AggregateFunction<?, ?>[0]));
      groupTable = groupTable.where(selectionFunction);
      if (!groupTable.isEmpty()) {
        results.add(groupTable);
      }
    }
    return combineTables(results);
  }

  private IntColumn assignToGroupsByStep(int step) {
    IntColumn groupColumn = IntColumn.create(GROUP_COL_TEMP_NAME, temp.rowCount());
    temp.addColumns(groupColumn);

    int groupId = 1;
    int withinGroupCount = 0;
    Row row = new Row(temp);

    while (row.hasNext()) {
      row.next();
      if (withinGroupCount < step) {
        withinGroupCount++;
        groupColumn.set(row.getRowNumber(), groupId);
      } else {
        groupId++;
        groupColumn.set(row.getRowNumber(), groupId);
        withinGroupCount = 1;
      }
    }
    int lastGroupSize = temp.where(numberColumn(GROUP_COL_TEMP_NAME).isEqualTo(groupId)).rowCount();
    if (lastGroupSize < step) {
      temp = temp.dropWhere(numberColumn(GROUP_COL_TEMP_NAME).isEqualTo(groupId));
    }
    temp.addColumns(IntColumn.indexColumn("index", temp.rowCount(), 1));
    return groupColumn;
  }

  /**
   * Associates the columns to be summarized with the functions that match their type. All valid
   * combinations are used
   *
   * @param group A table slice group
   * @return A table containing a row of summarized data for each group in the table slice group
   */
  private Table summarize(TableSliceGroup group) {
    List<Table> results = new ArrayList<>();

    ArrayListMultimap<String, AggregateFunction<?, ?>> reductionMultimap =
        getAggregateFunctionMultimap();

    for (String name : reductionMultimap.keys()) {
      List<AggregateFunction<?, ?>> reductions = reductionMultimap.get(name);
      results.add(group.aggregate(name, reductions.toArray(new AggregateFunction<?, ?>[0])));
    }
    return combineTables(results);
  }

  private ArrayListMultimap<String, AggregateFunction<?, ?>> getAggregateFunctionMultimap() {
    ArrayListMultimap<String, AggregateFunction<?, ?>> reductionMultimap =
        ArrayListMultimap.create();
    for (String name : summarizedColumns) {
      Column<?> column = temp.column(name);
      ColumnType type = column.type();
      for (AggregateFunction<?, ?> reduction : reductions) {
        if (reduction.isCompatibleColumn(type)) {
          reductionMultimap.put(name, reduction);
        }
      }
    }
    if (reductionMultimap.isEmpty()) {
      throw new IllegalArgumentException(
          "None of the aggregate functions provided apply to the summarized column type(s).");
    }
    return reductionMultimap;
  }

  private Table combineTables(List<Table> tables) {
    Preconditions.checkArgument(!tables.isEmpty());

    Table result = tables.get(0);
    for (int i = 1; i < tables.size(); i++) {
      Table table = tables.get(i);
      for (Column<?> column : table.columns()) {
        if (tableDoesNotContain(column.name(), result)) {
          result.addColumns(column);
        }
      }
    }
    return result;
  }

  private boolean tableDoesNotContain(String columnName, Table table) {
    List<String> upperCase =
        table.columnNames().stream().map(String::toUpperCase).collect(Collectors.toList());
    return !upperCase.contains(columnName.toUpperCase());
  }
}
