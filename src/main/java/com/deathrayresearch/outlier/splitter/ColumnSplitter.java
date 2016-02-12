package com.deathrayresearch.outlier.splitter;

import com.deathrayresearch.outlier.Relation;
import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.View;
import com.deathrayresearch.outlier.columns.ColumnReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits a table into a list of table, such that for every unique value in column ColumnReference
 * in the input table, there is one output table containing all such rows
 */
public class ColumnSplitter extends AbstractSplitter {

  public ColumnSplitter(ColumnReference columnReference) {
    super(columnReference);
  }

  @Override
  public List<View> split(Table table) {

    List<View> results = new ArrayList<>();
    Table t = table.sortOn(columnName());
    Relation empty = t.emptyCopy();
    View newTable = (View) empty;
    String lastKey = "";
    int columnIndex = t.columnIndex(columnName());
    for (int rowIndex = 0; rowIndex < t.rowCount(); rowIndex++) {

      String newKey = t.get(columnIndex, rowIndex);

      if (!newKey.equals(lastKey)) {
        if (!newTable.isEmpty()) {
          results.add(newTable);
        }
        newTable = (View) empty;
        newTable.setName(String.valueOf(newKey));
        lastKey = newKey;
      }
      newTable.addIndex(rowIndex);
    }

    if (!results.contains(newTable) && !newTable.isEmpty()) {
      results.add(newTable);
    }
    return results;
  }

}
