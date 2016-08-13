package com.github.lwhite1.tablesaw.api.ml.classification;

import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

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
public class CategoryConfusionMatrix implements ConfusionMatrix {

  private final Table<Integer, Integer, Integer> table = TreeBasedTable.create();

  private SortedMap<Integer, String> labels = new TreeMap<>();
  private CategoryColumn labelColumn;

  public CategoryConfusionMatrix(CategoryColumn labelColumn, SortedSet<String> labels) {
    this.labelColumn = labelColumn;
    int i = 0;
    for (String object : labels) {
      this.labels.put(i, object);
      i++;
    }
  }

  @Override
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

  @Override
  public com.github.lwhite1.tablesaw.api.Table toTable() {

    Table<String, String, Integer> sortedTable = sortedTable();

    com.github.lwhite1.tablesaw.api.Table t = com.github.lwhite1.tablesaw.api.Table.create("Confusion Matrix");
    t.addColumn(CategoryColumn.create(""));

    for (String label : sortedTable.rowKeySet()) {
      t.addColumn(IntColumn.create(label));
      t.column(0).addCell("Predicted "  + label);
    }

    int n = 0;
    for (String rowLabel : sortedTable.rowKeySet()) {
      int c = 1;
      for(String colLabel : sortedTable.columnKeySet()) {
        Integer value = sortedTable.get(rowLabel, colLabel);
        if (value == null) {
          t.intColumn(c).add(0);
        } else {
          t.intColumn(c).add(value);
          n = n + value;
        }
        c++;
      }
    }
    t.column(0).setName("n = " + n);
    for (int col = 1; col <= sortedTable.columnKeySet().size(); col++) {
      t.column(col).setName("Actual " + t.column(col).name());
    }
    return t;
  }

  private Table<String, String, Integer> sortedTable() {
    Int2ObjectMap<String> labelKeys = labelColumn.dictionaryMap().keyToValueMap();
    Table<String, String, Integer> sortedTable = TreeBasedTable.create();
    // make a set of all the values needed, from the prediction set or the actual set
    TreeSet<Integer> allValues = new TreeSet<>();
    allValues.addAll(table.columnKeySet());
    allValues.addAll(table.rowKeySet());
    List<Comparable> valuesList = new ArrayList<>(allValues);

    for (int r = 0; r < valuesList.size(); r++) {
      for(int c = 0; c < valuesList.size(); c++) {
        Integer value = table.get(valuesList.get(r), valuesList.get(c));
        if (value == null) {
          sortedTable.put(labelKeys.get(r), labelKeys.get(c), 0);
        } else {
          sortedTable.put(labelKeys.get(r), labelKeys.get(c), value);
        }
      }
    }
    return sortedTable;
  }

  @Override
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
