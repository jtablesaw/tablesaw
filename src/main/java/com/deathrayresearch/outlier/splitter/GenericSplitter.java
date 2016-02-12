package com.deathrayresearch.outlier.splitter;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.View;
import com.deathrayresearch.outlier.columns.ColumnReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Splits a table into a list of table, such that for every unique value
 * in the input table that is returned by the splitter function,
 * there is an output table containing all such rows
 */

public class GenericSplitter extends AbstractSplitter {

  private final Function<Comparable, Object> splitterFunction;

  public GenericSplitter(ColumnReference columnReference,
                         Function<Comparable, Object> splitterFunction) {
    super(columnReference);
    this.splitterFunction = splitterFunction;
  }

  @Override
  public List<View> split(Table table) {

    Map<Object, View> tableMap = Maps.newHashMap();
    Table empty = (Table) table.emptyCopy();
    int columnIndex = table.columnIndex(columnName());
    for (int rowIndex = 0; rowIndex < table.rowCount(); rowIndex++) {
      String newKey = String.valueOf(splitterFunction.apply(table.get(columnIndex, rowIndex)));
      if (!tableMap.containsKey(newKey)) {
        View newTable = new View(empty, empty.columns());
        newTable.setName(newKey);
        tableMap.put(newKey, newTable);
      }
      tableMap.get(newKey).addIndex(rowIndex);
    }
    return Lists.newArrayList(tableMap.values());
  }
}
