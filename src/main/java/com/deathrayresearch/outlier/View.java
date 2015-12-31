package com.deathrayresearch.outlier;

import org.roaringbitmap.IntIterator;
import org.roaringbitmap.RoaringBitmap;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * An un-materialized view on an underlying Table
 */
public class View implements Relation {

  private Relation table;
  private final List<Integer> columnIds = new ArrayList<>();
  private final RoaringBitmap rowMap;
  private final String id = UUID.randomUUID().toString();

  public View(Relation table, String... columnName) {
    this.table = table;
    for (String col : columnName) {
      columnIds.add(table.columnIndex(col));
    }
    rowMap = new RoaringBitmap();
    rowMap.add(0, table.rowCount());
  }

  public View(Relation table, List<String> columnName) {
    this.table = table;
    for (String col : columnName) {
      columnIds.add(table.columnIndex(col));
    }
    rowMap = new RoaringBitmap();
    rowMap.add(0, table.rowCount());
  }

  public View(Relation table, int headRows) {
    this.table = table;
    for (String col : table.columnNames()) {
      columnIds.add(table.columnIndex(col));
    }
    rowMap = new RoaringBitmap();
    rowMap.add(0, table.rowCount());
    rowMap.flip(headRows, table.rowCount());
  }

  public View(Relation table, Column[] columnSelection, RoaringBitmap rowSelection) {
    this.rowMap = rowSelection;
    this.table = table;
    for (Column aColumnSelection : columnSelection) {
      this.columnIds.add(table.columnIndex(aColumnSelection));
    }
  }

  public View where(RoaringBitmap bitmap) {
    rowMap.and(bitmap);
    return this;
  }

  public void print() {
    for (int i : columnIds) {
      System.out.println(table.column(i).name());
    }
    IntIterator intIterator = rowMap.getIntIterator();
    while (intIterator.hasNext()) {
      int r = intIterator.next();
      for (int c : columnIds) {
        System.out.println(table.get(c, r));
      }
    }
  }

  @Override
  public Column column(int columnIndex) {
    return null;
  }

  @Override
  public int columnCount() {
    return columnIds.size();
  }

  @Override
  public int rowCount() {
    return rowMap.getCardinality();
  }

  @Override
  public List<Column> getColumns() {
    return null;
  }

  @Override
  public int columnIndex(String col) {
    return 0;
  }

  @Override
  public int columnIndex(Column column) {
    int columnIndex = -1;
    for (int i = 0; i < columnIds.size(); i++) {
      if (column(columnIds.get(i)).equals(column)) {
        columnIndex = i;
        break;
      }
    }
    if (columnIndex == -1) {
      throw new IllegalArgumentException(String.format("Column %s is not present in view %s", column.name(), name()));
    }
    return columnIndex;
  }

  @Override
  public String get(int c, int r) {
    return null;
  }

  @Override
  public void addColumn(Column column) {}

  @Override
  public Column column(String columnName) {
    return null;
  }

  @Override
  public String name() {
    return table.name();
  }

  @Override
  public Relation emptyCopy() {
    return null;
  }

  @Override
  public void clear() {
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public List<String> columnNames() {
    List<String> names = new ArrayList<>();
    for (Integer columnId : columnIds) {
      names.add(table.column(columnId).name());
    }
    return names;
  }

}