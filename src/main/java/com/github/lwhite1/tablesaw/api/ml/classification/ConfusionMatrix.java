package com.github.lwhite1.tablesaw.api.ml.classification;

import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A confusion matrix is used to measure the accuracy of a classifier by counting the number of correct and
 * incorrect values produced when testing the classifier such that the counts are made for every combination of
 * correct and incorrect classification
 */
public class ConfusionMatrix {

  private final Table<Integer, Integer, Integer> table = TreeBasedTable.create();

  private SortedMap<Integer, Object> labels = new TreeMap();

  public ConfusionMatrix(SortedSet<Object> labels) {

    int i = 0;
    for (Object object : labels) {
      this.labels.put(i, object);
      i++;
    }
  }

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
    TreeSet<Integer> allValues = new TreeSet<>();
    allValues.addAll(table.columnKeySet());
    allValues.addAll(table.rowKeySet());

    for (Integer comparable : allValues) {
      t.addColumn(IntColumn.create(String.valueOf(labels.get(comparable))));
      t.column(0).addCell("Predicted "  + labels.get(comparable));
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
      t.column(c).setName("Actual " + labels.get(c - 1));
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
