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

  public View where(RoaringBitmap bitmap) {
    rowMap.and(bitmap);
    return this;
  }

  void print() {
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
}