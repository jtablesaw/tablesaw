package com.github.lwhite1.tablesaw;

import com.github.lwhite1.tablesaw.columns.Column;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * A group of tables formed by performing splitting operations on an original table
 */
public class TableGroup {

  private static final String SPLIT_STRING = "|||";

  private final Table original;

  private final List<SubTable> subTables;

  // the name(s) of the column(s) we're splitting the table on
  private String[] splitColumnNames;

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

    Table empty = original.emptyCopy();

    SubTable newView = new SubTable(empty);
    String lastKey = "";
    newView.setName(lastKey);

    for (int row = 0; row < original.rowCount(); row++) {

      String newKey = "";
      List<String> values = new ArrayList<>();

      for (int col = 0; col < columnCount; col++) {
        if (col > 0)
          newKey = newKey + SPLIT_STRING;

        String groupKey = original.get(columnIndices[col], row);
        newKey = newKey + groupKey;
        values.add(groupKey);
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

    List<Column> newColumns = new ArrayList<>();

    for (Column column : columnNames) {
      Column newColumn = column.emptyCopy();
      newColumns.add(newColumn);
    }
    // iterate through the rows in the table and split each of the grouping columns into multiple columns
    for (int row = 0; row < subTable.rowCount(); row++) {
      String[] strings = subTable.name().split(SPLIT_STRING);
      for (int col = 0; col < newColumns.size(); col++) {
        newColumns.get(col).addCell(strings[col]);
      }
    }
    for (Column c : newColumns) {
      subTable.addColumn(c);
    }
    return subTable;
  }

  public List<SubTable> getSubTables() {
    return subTables;
  }

  public int size() {
    return subTables.size();
  }
}
