package com.deathrayresearch.outlier;

import com.deathrayresearch.outlier.columns.Column;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.apache.commons.lang3.StringUtils;
import org.roaringbitmap.IntIterator;
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
  private IntArrayList columnIds = new IntArrayList();
  private final RoaringBitmap rowMap;
  private int mask[];
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
    for (String col : table.columnNames()) {
      columnIds.add(table.columnIndex(col));
    }
    rowMap = new RoaringBitmap();
    rowMap.add(0, headRows);
    mask = rowMap.toArray();
  }
  
  public View(View view, int headRows) {
    this.table = view;

    columnIds = view.columnIds.clone();
    int i = 0;
    Iterator<Integer> it = view.rowMap.iterator();
    rowMap = new RoaringBitmap();
    while (i < headRows && it.hasNext()) {
      rowMap.add(it.next());
      i++;
    }
    mask = rowMap.toArray();
  }

  public View(Relation table, List<Column> columnSelection, RoaringBitmap rowSelection) {
    this.rowMap = rowSelection;
    this.table = table;
    for (Column aColumnSelection : columnSelection) {
      this.columnIds.add(table.columnIndex(aColumnSelection));
    }
    mask = rowMap.toArray();
  }

  public View(Relation table, List<Column> columnSelection) {
    this.rowMap = new RoaringBitmap();
    this.table = table;
    for (Column aColumnSelection : columnSelection) {
      this.columnIds.add(table.columnIndex(aColumnSelection));
    }
    mask = rowMap.toArray();
  }

  public View where(RoaringBitmap bitmap) {
    rowMap.and(bitmap);
    mask = rowMap.toArray();
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
    List<Column> columns = new ArrayList<>();
    for (int i = 0; i < columnCount(); i++) {
      columns.add(column(i));
    }
    return columns;
  }

  @Override
  public int columnIndex(Column column) {
    int columnIndex = -1;
    for (int i = 0; i < columnIds.size(); i++) {
      int viewIndex = columnIds.getInt(i);
      if (column(i).equals(column)) {
        columnIndex = viewIndex;
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
   */
  @Override
  public int row(int r) {
    return mask[r];
  }

  /**
   * Adds the given index to the rowmap for this view.
   * @param index an index representing a row in the backing table
   */
  public void addIndex(int index) {
    //TODO(lwhite): Need to find another way to locate an item in bitmap by index. this is VERY BAD.

    //TODO(lwhite): Could set a flag to mark the mask as dirty and recalculate when needed?
    this.rowMap.add(index);
    mask = rowMap.toArray();
  }

  @Override
  public void removeColumns(Column... columns) {
    for (Column c : columns) {
      columnIds.removeInt(columnIndex(c));
    }
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

  public String print() {
    StringBuilder buf = new StringBuilder();

    int[] colWidths = colWidths();
    buf.append(name()).append('\n');
    List<String> names = this.columnNames();

    for (int colNum = 0; colNum < columnCount(); colNum++) {
      buf.append(
          StringUtils.rightPad(
              StringUtils.defaultString(String.valueOf(names.get(colNum))), colWidths[colNum]));
      buf.append(' ');
    }
    buf.append('\n');
    IntIterator iterator = rowMap.getIntIterator();
    while (iterator.hasNext()) {
      int r = iterator.next();
      for (int i = 0; i < columnCount(); i++) {
        int c = columnIds.getInt(i);
        String cell = StringUtils.rightPad(get(c, r), colWidths[i]);
        buf.append(cell);
        buf.append(' ');
      }
      buf.append('\n');
    }
    return buf.toString();
  }

  /**
   * Returns an array of column widths for printing tables
   */
  public int[] colWidths() {

    int cols = columnCount();
    int[] widths = new int[cols];
    List<String> columnNames = columnNames();

    for (int i = 0; i < columnCount(); i++) {
      widths[i] = columnNames.get(i).length();
    }

    for (int rowNum = 0; rowNum < rowCount(); rowNum++) {
      for (int colNum = 0; colNum < cols; colNum++) {
        int c = columnIds.getInt(colNum);
        widths[colNum]
            = Math.max(widths[colNum], StringUtils.length(get(c, rowNum)));
      }
    }
    return widths;
  }
}