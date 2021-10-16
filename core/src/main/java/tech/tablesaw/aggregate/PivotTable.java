package tech.tablesaw.aggregate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumericColumn;
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
   * applied to the {@code values column} grouping by the key columns
   *
   * @param table The table that provides the data to be pivoted
   * @param column1 A "key" categorical column from which the primary grouping is created. There
   *     will be one on each row of the result
   * @param column2 A second categorical column for which a subtotal is created; this produces n
   *     columns on each row of the result
   * @param values A numeric column that provides the values to be summarized
   * @param aggregateFunction function that defines what operation is performed on the values in the
   *     subgroups
   * @return A new, pivoted table
   */
  public static Table pivot(
      Table table,
      CategoricalColumn<?> column1,
      CategoricalColumn<?> column2,
      NumericColumn<?> values,
      AggregateFunction<?, ?> aggregateFunction) {

    TableSliceGroup tsg = table.splitOn(column1);

    Table pivotTable = Table.create("Pivot: " + column1.name() + " x " + column2.name());
    pivotTable.addColumns(column1.type().create(column1.name()));

    List<String> valueColumnNames = getValueColumnNames(table, column2);

    for (String colName : valueColumnNames) {
      pivotTable.addColumns(DoubleColumn.create(colName));
    }

    int valueIndex = table.columnIndex(column2);
    int keyIndex = table.columnIndex(column1);

    String key;

    for (TableSlice slice : tsg.getSlices()) {
      key = String.valueOf(slice.get(0, keyIndex));
      pivotTable.column(0).appendCell(key);

      Map<String, Double> valueMap =
          getValueMap(column1, column2, values, valueIndex, slice, aggregateFunction);

      for (String columnName : valueColumnNames) {
        Double aDouble = valueMap.get(columnName);
        NumericColumn<?> pivotValueColumn = pivotTable.numberColumn(columnName);
        if (aDouble == null) {
          pivotValueColumn.appendMissing();
        } else {
          pivotValueColumn.appendObj(aDouble);
        }
      }
    }

    return pivotTable;
  }

  private static Map<String, Double> getValueMap(
      CategoricalColumn<?> column1,
      CategoricalColumn<?> column2,
      NumericColumn<?> values,
      int valueIndex,
      TableSlice slice,
      AggregateFunction<?, ?> function) {

    Table temp = slice.asTable();
    Table summary = temp.summarize(values.name(), function).by(column1.name(), column2.name());

    Map<String, Double> valueMap = new HashMap<>();
    NumericColumn<?> nc = summary.numberColumn(summary.columnCount() - 1);
    for (int i = 0; i < summary.rowCount(); i++) {
      valueMap.put(String.valueOf(summary.get(i, 1)), nc.getDouble(i));
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
