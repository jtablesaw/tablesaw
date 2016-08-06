package com.github.lwhite1.tablesaw.api.ml.classification;

import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * A confusion matrix is used to measure the accuracy of a classifier by counting the number of correct and
 * incorrect values produced when testing the classifier such that the counts are made for every combination of
 * correct and incorrect classification
 */
public class ConfusionMatrix {

  private final Table<Integer, Integer, Integer> table = TreeBasedTable.create();

  public void increment(Integer predicted, Integer actual) {
    Integer v = table.get(predicted, actual);
    if (v == null) {
      table.put(predicted, actual, 1);
    } else {
      table.put(predicted, actual, v + 1);
    }
  }

  @Override
  public String toString() {
    return toTable().print();
  }

  public com.github.lwhite1.tablesaw.api.Table toTable() {
    com.github.lwhite1.tablesaw.api.Table t = com.github.lwhite1.tablesaw.api.Table.create("Confusion Matrix");
    t.addColumn(CategoryColumn.create(""));

    // make a set of all the values needed, from the prediction set or the actual set
    TreeSet<Comparable> allValues = new TreeSet<>();
    allValues.addAll(table.columnKeySet());
    allValues.addAll(table.rowKeySet());

    for (Comparable comparable : allValues) {
      t.addColumn(IntColumn.create(String.valueOf(comparable)));
      t.column(0).addCell("Predicted "  + String.valueOf(comparable));
    }

    // put them in a list so they can be accessed by index number
    List<Comparable> valuesList = new ArrayList<>(allValues);
    int n = 0;
    for (int r = 0; r < valuesList.size(); r++) {
      for(int c = 0; c < valuesList.size(); c++) {
        Integer value = table.get(valuesList.get(r), valuesList.get(c));
        if (value == null) {
          t.intColumn(c + 1).add(0);
        } else {
          t.intColumn(c + 1).add(value);
          n = n + value;
        }
      }
    }
    t.column(0).setName("n = " + n);
    for (int c = 1; c <= valuesList.size(); c++) {
      t.column(c).setName("Actual " + t.column(c).name());
    }
    return t;
  }

  public double accuracy() {
    Set<Table.Cell<Integer, Integer, Integer>> cellSet = table.cellSet();
    int hits = 0;
    int misses = 0;
    for (Table.Cell cell : cellSet) {
      if (cell.getRowKey().equals(cell.getColumnKey())) {
        hits = hits + (int) cell.getValue();
      } else {
        misses = misses + (int) cell.getValue();
      }
    }
    return hits / ((hits + misses) * 1.0);
  }
}
