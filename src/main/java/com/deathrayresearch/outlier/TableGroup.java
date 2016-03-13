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

  private static final String SPLIT_STRING = "|||";

  private final Table original;
  private final List<SubTable> subTables;

  // the name(s) of the column(s) we're splitting the table on
  private final String[] splitColumnNames;

  public TableGroup(Table original, String... splitColumnNames) {
    this.original = original.sortOn(splitColumnNames);
    this.subTables = splitOn(splitColumnNames);
    Preconditions.checkState(!subTables.isEmpty());
    this.splitColumnNames = splitColumnNames;
  }

  public TableGroup(Table original, Column... columns) {
    splitColumnNames = new String[columns.length];
    for (int i = 0; i < columns.length; i++) {
      splitColumnNames[i] = columns[i].name();
    }
    this.original = original.sortOn(splitColumnNames);
    this.subTables = splitOn(splitColumnNames);
    Preconditions.checkState(!subTables.isEmpty());
  }

  /**
   * Splits the original table into sub-tables, grouping on the columns whose names are given in splitColumnNames
   */
  private List<SubTable> splitOn(String... columnNames) {

    int columnCount = columnNames.length;
    List<Column> columns = original.columns(columnNames);
    List<SubTable> tables = new ArrayList<>();

    int[] columnIndices = new int[columnCount];
    for (int i = 0; i < columnCount; i++) {
      columnIndices[i] = original.columnIndex(columnNames[i]);
    }

    Table empty = (Table) original.emptyCopy();

    SubTable newView = new SubTable(empty);
    String lastKey = "";
    newView.setName(lastKey);

    for (int row = 0; row < original.rowCount(); row++) {

      String newKey = "";
      List<String> values = new ArrayList<>();

      for (int col = 0; col < columnCount; col++) {
        String groupKey = original.get(columnIndices[col], row);
        newKey = newKey + groupKey;
        values.add(groupKey);
        if (col < columnCount - 2)
          newKey = newKey + SPLIT_STRING;
      }

      if (!newKey.equals(lastKey)) {
        if (!newView.isEmpty()) {
          tables.add(newView);
        }

        newView = new SubTable(empty);
        newView.setName(newKey);
        newView.setValues(values);
        lastKey = newKey;
      }
      newView.addRow(row, original);
    }

    if (!tables.contains(newView) && !newView.isEmpty()) {
      if (columnCount == 1) {
        tables.add(newView);
      } else {
        tables.add(splitGroupingColumn(newView, columns));
      }
    }
    return tables;
  }

  private SubTable splitGroupingColumn(SubTable subTable, List<Column> columnNames) {
    List<Column> temp = new ArrayList<>();
    for (Column column : columnNames) {
      Column newColumn = column.emptyCopy();
      subTable.addColumn(newColumn);
      temp.add(newColumn);
    }
    for (int row = 0; row < subTable.rowCount(); row++) {
      String[] strings = subTable.column(0).getString(row).split(SPLIT_STRING);
      for (int col = 0; col < temp.size(); col++) {
        temp.get(col).addCell(strings[col]);
      }
    }
    return subTable;
  }

  public Table apply(ToDoubleFunction<FloatColumn> fun,
                     String calcColumn,
                     String resultColumnName) {

    Table t = new Table(original.name() + " summary");
    for (String columnName : splitColumnNames) {
      t.addColumn(TypeUtils.newColumn(columnName, original.column(columnName).type()));
    }
    t.addColumn(FloatColumn.create(resultColumnName));


/*
    for (View subTable : subTables) {
      float result = (float) fun.applyAsDouble(subTable.floatColumn(calcColumn));
      Row r = t.newRow();
      List<Comparable> values = subTable.getValues();
      for (int i = 0; i < splitColumnNames.length; i++) {
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
    for (String columnName : splitColumnNames) {
      t.addColumn(TypeUtils.newColumn(columnName, original.column(columnName).type()));
    }
    t.addColumn(FloatColumn.create(resultColumnName));

/*
    for (View subTable : subTables) {
      int result = fun.applyAsInt(subTable.column(calcColumn));
      Row r = t.newRow();
      List<Comparable> values = subTable.getValues();
      for (int i = 0; i < splitColumnNames.length; i++) {
        Comparable columnValue = values.get(i);
        r.set(i, columnValue);
      }
      r.set(values.size(), result);
    }*/
    return t;
  }

  public List<SubTable> getSubTables() {
    return subTables;
  }

  public int size() {
    return subTables.size();
  }
}
