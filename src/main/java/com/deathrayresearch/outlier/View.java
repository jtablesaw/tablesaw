package com.deathrayresearch.outlier;

import com.deathrayresearch.outlier.columns.Column;
import it.unimi.dsi.fastutil.ints.IntArrayList;
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
  private final IntArrayList columnIds = new IntArrayList();
  private final RoaringBitmap rowMap;
  private final int mask[];
  private final String id = UUID.randomUUID().toString();

  public View(Relation table, String... columnName) {
    this.table = table;
    for (String col : columnName) {
      columnIds.add(table.columnIndex(col));
    }
    rowMap = new RoaringBitmap();
    rowMap.add(0, table.rowCount());
    mask = rowMap.toArray();
  }

  public View(Table table, int headRows) {
    this.table = table;
    //noinspection Convert2streamapi
    for (String col : table.columnNames()) {
      columnIds.add(table.columnIndex(col));
    }
    rowMap = new RoaringBitmap();
    rowMap.add(0, headRows);
    mask = rowMap.toArray();
  }
  
  public View(View view, int headRows) {
    this.table = view;
    //noinspection Convert2streamapi
    for (String col : table.columnNames()) {
      columnIds.add(table.columnIndex(col));
    }
    int i = 0;
    Iterator<Integer> it = view.rowMap.iterator();
    rowMap = new RoaringBitmap();
    while (i < headRows && it.hasNext()) {
      rowMap.add(it.next());
    }
    mask = rowMap.toArray();
  }

  public View(Relation table, List<Column> columnSelection, RoaringBitmap rowSelection) {
    this.rowMap = rowSelection;
    this.table = table;
    //noinspection Convert2streamapi
    for (Column aColumnSelection : columnSelection) {
      this.columnIds.add(table.columnIndex(aColumnSelection));
    }
    mask = rowMap.toArray();
  }

  public View(Relation table, List<Column> columnSelection) {
    this.rowMap = new RoaringBitmap();
    this.table = table;
    //noinspection Convert2streamapi
    for (Column aColumnSelection : columnSelection) {
      this.columnIds.add(table.columnIndex(aColumnSelection));
    }
    mask = rowMap.toArray();
  }

  public View where(RoaringBitmap bitmap) {
    rowMap.and(bitmap);
    return this;
  }

  @Override
  public Column column(int columnIndex) {
    return table.column(columnIds.getInt(columnIndex));
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
      if (column(columnIds.getInt(i)).equals(column)) {
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
    //noinspection Convert2streamapi
    for (Integer columnId : columnIds) {
      names.add(table.column(columnId).name());
    }
    return names;
  }

  @Override
  public int row(int r) {
    return mask[r];
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
    columnIds.removeInt(columnIndex(column));
  }

  public View head(int nRows) {
    View view = new View(this, Math.min(nRows, rowCount()));
    view.setName(name);
    return view;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }
}