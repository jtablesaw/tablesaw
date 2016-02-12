package com.deathrayresearch.outlier;

import com.deathrayresearch.outlier.columns.Column;
import org.roaringbitmap.RoaringBitmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * An un-materialized view on an underlying Table
 */
public class View implements Relation {

  private String name;
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

  public View(Table table, int headRows) {
    this.table = table;
    for (String col : table.columnNames()) {
      columnIds.add(table.columnIndex(col));
    }
    rowMap = new RoaringBitmap();
    rowMap.add(0, headRows);
  }

  public View(View view, int headRows) {
    this.table = view;
    for (String col : table.columnNames()) {
      columnIds.add(table.columnIndex(col));
    }
    int i = 0;
    Iterator<Integer> it = view.rowMap.iterator();
    rowMap = new RoaringBitmap();
    while (i < headRows && it.hasNext()) {
      rowMap.add(it.next());
    }
  }

  public View(Relation table, List<Column> columnSelection, RoaringBitmap rowSelection) {
    this.rowMap = rowSelection;
    this.table = table;
    for (Column aColumnSelection : columnSelection) {
      this.columnIds.add(table.columnIndex(aColumnSelection));
    }
  }

  public View(Relation table, List<Column> columnSelection) {
    this.rowMap = new RoaringBitmap();
    this.table = table;
    for (Column aColumnSelection : columnSelection) {
      this.columnIds.add(table.columnIndex(aColumnSelection));
    }
  }

  public View where(RoaringBitmap bitmap) {
    rowMap.and(bitmap);
    return this;
  }

  @Override
  public Column column(int columnIndex) {
    return table.column(columnIds.get(columnIndex));
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
  public List<Column> columns() {
    return null;
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
    return table.get(c, r);
  }

  @Override
  public void addColumn(Column column) {
  }

  @Override
  public String name() {
    return name;
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

  /**
   * Adds the given index to the rowmap for this view.
   * @param index an index representing a row in the backing table
   */
  public void addIndex(int index) {
    this.rowMap.add(index);
  }

  @Override
  public void removeColumn(Column column) {
    columnIds.remove(columnIndex(column));
  }

  public View head(int i) {
    return new View(this, i);
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }
}