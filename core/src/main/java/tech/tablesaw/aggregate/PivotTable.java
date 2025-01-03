package tech.tablesaw.aggregate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.api.Table;
import tech.tablesaw.table.TableSlice;
import tech.tablesaw.table.TableSliceGroup;

/**
 * PivotTable is used to 'rotate' a source table such that it is summarized on the values of some
 * column. As implemented here, you supply: - a "key" categorical column from which the primary
 * grouping is created, there will be one on each row of the result - a second categorical column
 * for which a subtotal is created; this produces n columns on each row of the result - one column
 * for each unique value - a numeric column that provides the values to be summarized - an
 * aggregation function that defines what operation is performed on the values in the subgroups
 */
public class PivotTable {

  /**
   * Returns a table that is a rotation of the given table pivoted around the key columns, and
   * filling the output cells using the values calculated by the {@code aggregateFunction} when
   * applied to the {@code aggregatedColumn} grouping by the key columns
   * 
   * Handles the case whereby there is a single groupingColumn and aggregatedColumn
   *
   * @param table The table that provides the data to be pivoted
   * @param groupingColumn A "key" categorical column from which the primary grouping is created. There
   *     will be one on each row of the result
   * @param pivotColumn A second categorical column for which a subtotal is created; this produces n
   *     columns on each row of the result
   * @param aggregatedColumn A numeric column that provides the values to be summarized
   * @param aggregateFunction function that defines what operation is performed on the values in the
   *     subgroups
   * @return A new, pivoted table
   */
  
  public static Table pivot(
      Table table,
      CategoricalColumn<?> groupingColumn,
      CategoricalColumn<?> pivotColumn,
      NumericColumn<?> aggregatedColumns,
      AggregateFunction<?, ?> aggregateFunction) {
      return pivot(table, List.of(groupingColumn), pivotColumn, List.of(aggregatedColumns), aggregateFunction);
  }
      
   /**
   * Returns a table that is a rotation of the given table pivoted around the key columns, and
   * filling the output cells using the values calculated by the {@code aggregateFunction} when
   * applied to the {@code aggregatedColumns} grouping by the key columns
   * 
   * Handles the case whereby there may be multiple groupingColumns and/or multiple aggregatedColumns
    * @param table
    * @param groupingColumn
    * @param pivotColumn
    * @param aggregatedColumns
    * @param aggregateFunction
    * @return
    */
  public static Table pivot(
      Table table,
      List<CategoricalColumn<?>> groupingColumns,
      CategoricalColumn<?> pivotColumn,
      List<NumericColumn<?>> aggregatedColumns,
      AggregateFunction<?, ?> aggregateFunction) {

    boolean multiAggregated = aggregatedColumns.size() > 1;

    TableSliceGroup tsg = table.splitOn(groupingColumns.toArray(CategoricalColumn[]::new));

    List<String> groupingColumnNames = groupingColumns.stream().map(_c -> _c.name()).collect(Collectors.toList());

    Table pivotTable = Table.create("Pivot: " + String.join(",", groupingColumnNames)  + " x " + pivotColumn.name());

    pivotTable.addColumns(groupingColumns.stream().map(_c -> _c.type().create(_c.name())).toArray(Column[]::new));

    List<String> valueColumnNames = getValueColumnNames(table, pivotColumn);

    if(multiAggregated){
      for (String colName : valueColumnNames) 
        for(NumericColumn<?> aggColumn : aggregatedColumns) {
        pivotTable.addColumns(DoubleColumn.create(colName + "." + aggColumn.name()));
      }
    }
    else{
      for (String colName : valueColumnNames) {
        pivotTable.addColumns(DoubleColumn.create(colName));
      }
    }

    for (TableSlice slice : tsg.getSlices()) {

      for (int i = 0; i < groupingColumns.size(); i++) {
        String key = String.valueOf(slice.get(0, table.columnIndex(groupingColumns.get(i))));
        pivotTable.column(i).appendCell(key);
      }

      Map<String, Double> valueMap =
          getValueMap(groupingColumns, pivotColumn, aggregatedColumns, slice, aggregateFunction);

      for (String columnName : valueColumnNames) {
          for (NumericColumn<?> aggregatedColumn: aggregatedColumns) {
            
            String appendedColumnName;

            if(multiAggregated){
              appendedColumnName = columnName + "." + aggregatedColumn.name();
            } else {
              appendedColumnName = columnName;
            }
            
            NumericColumn<?> pivotValueColumn = pivotTable.numberColumn(appendedColumnName);
            
            Double aDouble = valueMap.get(appendedColumnName);

            if (aDouble == null) {
              pivotValueColumn.appendMissing();
            } else {
              pivotValueColumn.appendObj(aDouble);
            }
          }
        }  

    }

    return pivotTable;
  }

  private static Map<String, Double> getValueMap(
      List<CategoricalColumn<?>> groupingColumns,
      CategoricalColumn<?> pivotColumn,
      List<NumericColumn<?>> aggregatedColumns,
      TableSlice slice,
      AggregateFunction<?, ?> function) {

    boolean multiAggregated = aggregatedColumns.size() > 1;
    Table temp = slice.asTable();
    List<CategoricalColumn<?>> allKeyColumns = new LinkedList<>(groupingColumns);
    allKeyColumns.add(pivotColumn);

    List<String> aggregatedColumnNames = aggregatedColumns.stream().map(NumericColumn::name).collect(Collectors.toList());

    Table summary = temp.summarize(aggregatedColumnNames, function).by(allKeyColumns.stream().map(CategoricalColumn::name).toArray(String[]::new));

    Map<String, Double> valueMap = new HashMap<>();

    if(multiAggregated){
      for (int i = 0; i < summary.rowCount(); i++) {
        for (int k = 0; k < aggregatedColumns.size(); k++) {
          NumericColumn<?> nc = summary.numberColumn(groupingColumns.size() + k + 1);
          valueMap.put(String.valueOf(summary.get(i, groupingColumns.size())) + "." + aggregatedColumns.get(k).name(), nc.getDouble(i));
        }
      }
    }
    else{
      NumericColumn<?> nc = summary.numberColumn(summary.columnCount() - 1);
      for (int i = 0; i < summary.rowCount(); i++) {
        valueMap.put(String.valueOf(summary.get(i, groupingColumns.size())), nc.getDouble(i));
      }
    }

    return valueMap;
  }

  private static List<String> getValueColumnNames(Table table, CategoricalColumn<?> column2) {
    List<String> valueColumnNames = new ArrayList<>();

    for (Object colName : table.column(column2.name()).unique()) {
      valueColumnNames.add(String.valueOf(colName));
    }
    valueColumnNames.sort(String::compareTo);
    return valueColumnNames;
  }
}
