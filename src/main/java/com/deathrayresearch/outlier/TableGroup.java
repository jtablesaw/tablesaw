package com.deathrayresearch.outlier;

import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.FloatColumn;
import com.deathrayresearch.outlier.columns.IntColumn;
import com.deathrayresearch.outlier.io.TypeUtils;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

/**
 * A group of tables formed by performing grouping operations on an original table
 */
public class TableGroup {

  private final Table original;
  private final List<View> subTables;
  private final String[] columnNames;

  public TableGroup(Table original, String... columnNames) {
    this.original = original;
    this.subTables = splitOn(columnNames);
    Preconditions.checkState(!subTables.isEmpty());
    this.columnNames = columnNames;
  }

  public TableGroup(Table original, Column... columns) {
    this.original = original;
    columnNames = new String[columns.length];
    for (int i = 0; i < columns.length; i++) {
      columnNames[i] = columns[i].name();
    }
    this.subTables = splitOn(columnNames);
    Preconditions.checkState(!subTables.isEmpty());
  }

  /**
   * Splits the original table into subtables, grouping on the columns whose names are given in columnNames
   */
  private List<View> splitOn(String... columnNames) {

    List<View> tables = new ArrayList<>();

    int[] indices = new int[columnNames.length];

    for (int i = 0; i < columnNames.length; i++) {
      indices[i] = original.columnIndex(columnNames[i]);
    }

    Table t = original.sortOn(columnNames);
    Relation empty = t.emptyCopy();

    View newView = new View(empty);
    String lastKey = "";

    for (int row = 0; row < t.rowCount(); row++) {

      String newKey = "";
      //List<Comparable> values = new ArrayList<>();
      for (int col = 0; col < columnNames.length; col++) {
        Comparable comparable = t.get(indices[col], row);
        newKey = newKey + comparable;
        //values.add(comparable);
        if (col < columnNames.length - 2)
          newKey = newKey + "|||";
      }

      if (!newKey.equals(lastKey)) {
        if (!newView.isEmpty()) {
          tables.add(newView);
        }

        newView = new View(empty);
        newView.setName(String.valueOf(newKey));
        //newView.setValues(values);
        lastKey = newKey;
      }
   //   newView.addRow(row);  //todo should clone row
    }

    if (!tables.contains(newView) && !newView.isEmpty()) {
      tables.add(newView);
    }
    return tables;
  }

  public Table apply(ToDoubleFunction<FloatColumn> fun,
                     String calcColumn,
                     String resultColumnName) {

    Table t = new Table(original.name() + " summary");
    for (String columnName : columnNames) {
      t.addColumn(TypeUtils.newColumn(columnName, original.column(columnName).type()));
    }
    t.addColumn(FloatColumn.create(resultColumnName));


/*
    for (View subTable : subTables) {
      float result = (float) fun.applyAsDouble(subTable.floatColumn(calcColumn));
      Row r = t.newRow();
      List<Comparable> values = subTable.getValues();
      for (int i = 0; i < columnNames.length; i++) {
        Comparable columnValue = values.get(i);
        r.set(i, columnValue);
      }
      r.set(values.size(), result);
    }
*/
    return t;
  }

  public Table apply(ToIntFunction<IntColumn> fun,
                     String calcColumn,
                     String resultColumnName) {

    Preconditions.checkArgument(!subTables.isEmpty());
    Table t = new Table(original.name() + " summary");
    for (String columnName : columnNames) {
      t.addColumn(TypeUtils.newColumn(columnName, original.column(columnName).type()));
    }
    t.addColumn(FloatColumn.create(resultColumnName));

/*
    for (View subTable : subTables) {
      int result = fun.applyAsInt(subTable.column(calcColumn));
      Row r = t.newRow();
      List<Comparable> values = subTable.getValues();
      for (int i = 0; i < columnNames.length; i++) {
        Comparable columnValue = values.get(i);
        r.set(i, columnValue);
      }
      r.set(values.size(), result);
    }*/
    return t;
  }
}
