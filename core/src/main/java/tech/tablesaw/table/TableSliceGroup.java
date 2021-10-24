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

package tech.tablesaw.table;

import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

/**
 * A group of virtual tables (table slices) formed by performing splitting operations on an original
 * table See: {@link TableSlice}
 */
public abstract class TableSliceGroup implements Iterable<TableSlice> {

  /**
   * A string that is used internally as a delimiter in creating a column name from all the grouping
   * columns
   */
  protected static final String SPLIT_STRING = "~~~";

  /**
   * A function that splits the group column name back into the original column names for the
   * grouping columns
   */
  private static final Splitter SPLITTER = Splitter.on(SPLIT_STRING);

  /** The list of slices or views over the source table that I contain */
  private final List<TableSlice> subTables = new ArrayList<>();

  /** An array containing the names of the columns that the backing table was split on */
  private final String[] splitColumnNames;

  // The table that underlies all the manipulations performed here
  private Table sourceTable;

  /**
   * Returns an instance for calculating a single summary for the given table, with no sub-groupings
   */
  protected TableSliceGroup(Table original) {
    sourceTable = original;
    splitColumnNames = new String[0];
  }

  /**
   * Returns an instance for calculating subgroups, one for each combination of the given
   * groupColumnNames that appear in the source table
   */
  protected TableSliceGroup(Table sourceTable, String[] groupColumnNames) {
    this.sourceTable = sourceTable;
    this.splitColumnNames = groupColumnNames;
  }

  /** Returns the names of the columns the backing table was split on. */
  protected String[] getSplitColumnNames() {
    return splitColumnNames;
  }

  /** Returns the sum of the sizes for the columns in the given {@link Column} list */
  protected int getByteSize(List<Column<?>> columns) {
    int byteSize = 0;
    for (Column<?> c : columns) {
      byteSize += c.byteSize();
    }
    return byteSize;
  }

  /** Add a slice to this group */
  protected void addSlice(TableSlice slice) {
    subTables.add(slice);
  }

  /** Returns the slices as a list */
  public List<TableSlice> getSlices() {
    return subTables;
  }

  /** Returns the ith slice in this group */
  public TableSlice get(int i) {
    return subTables.get(i);
  }

  /** Returns the table behind this slice group */
  public Table getSourceTable() {
    return sourceTable;
  }

  /** Returns the number of slices */
  public int size() {
    return subTables.size();
  }

  /**
   * For a subtable that is grouped by the values in more than one column, split the grouping column
   * into separate cols and return the revised view
   */
  private Table splitGroupingColumn(Table groupTable) {

    if (splitColumnNames.length > 0) {
      List<Column<?>> newColumns = new ArrayList<>();
      List<Column<?>> columns = sourceTable.columns(splitColumnNames);
      for (Column<?> column : columns) {
        Column<?> newColumn = column.emptyCopy();
        newColumns.add(newColumn);
      }
      // iterate through the rows in the table and split each of the grouping columns into multiple
      // columns
      for (int row = 0; row < groupTable.rowCount(); row++) {
        List<String> strings = SPLITTER.splitToList(groupTable.stringColumn("Group").get(row));
        for (int col = 0; col < newColumns.size(); col++) {
          newColumns.get(col).appendCell(strings.get(col));
        }
      }
      for (int col = 0; col < newColumns.size(); col++) {
        Column<?> c = newColumns.get(col);
        groupTable.insertColumn(col, c);
      }
      groupTable.removeColumns("Group");
    }
    return groupTable;
  }

  /**
   * Applies the given aggregation to the given column. The apply and combine steps of a
   * split-apply-combine.
   */
  public Table aggregate(String colName1, AggregateFunction<?, ?>... functions) {
    ArrayListMultimap<String, AggregateFunction<?, ?>> columnFunctionMap =
        ArrayListMultimap.create();
    columnFunctionMap.putAll(colName1, Lists.newArrayList(functions));
    return aggregate(columnFunctionMap);
  }

  /**
   * Applies the given aggregations to the given columns. The apply and combine steps of a
   * split-apply-combine.
   *
   * @param functions map from column name to aggregation to apply on that function
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public Table aggregate(ListMultimap<String, AggregateFunction<?, ?>> functions) {
    Table groupTable = summaryTableName(sourceTable);
    StringColumn groupColumn = StringColumn.create("Group");
    groupTable.addColumns(groupColumn);
    boolean firstFunction = true;
    for (Map.Entry<String, Collection<AggregateFunction<?, ?>>> entry :
        functions.asMap().entrySet()) {
      String columnName = entry.getKey();
      for (AggregateFunction function : entry.getValue()) {
        String colName = aggregateColumnName(columnName, function.functionName());
        ColumnType type = function.returnType();
        Column resultColumn = type.create(colName);
        for (TableSlice subTable : getSlices()) {
          Object result = function.summarize(subTable.column(columnName));
          if (firstFunction) {
            groupColumn.append(subTable.name());
          }
          if (function.returnType().equals(ColumnType.DOUBLE)) {
            Number number = (Number) result;
            resultColumn.append(number.doubleValue());
          } else {
            resultColumn.append(result);
          }
        }
        groupTable.addColumns(resultColumn);
        firstFunction = false;
      }
    }
    return splitGroupingColumn(groupTable);
  }

  /** Returns the name of a summary table made by aggregating on the slices in this group */
  public static Table summaryTableName(Table source) {
    return Table.create(source.name() + " summary");
  }

  /**
   * Returns an iterator over elements of type {@code T}.
   *
   * @return an Iterator.
   */
  @Override
  public Iterator<TableSlice> iterator() {
    return subTables.iterator();
  }

  /**
   * Returns a column name for aggregated data based on the given source column name and function
   */
  public static String aggregateColumnName(String columnName, String functionName) {
    return String.format("%s [%s]", functionName, columnName);
  }

  /**
   * Returns a list of Tables created by reifying my list of slices (views) over the original table
   */
  public List<Table> asTableList() {
    List<Table> tableList = new ArrayList<>();
    for (TableSlice view : this) {
      tableList.add(view.asTable());
    }
    return tableList;
  }

  /** Sets the source table that backs this TableSliceGroup */
  protected void setSourceTable(Table sourceTable) {
    this.sourceTable = sourceTable;
  }
}
