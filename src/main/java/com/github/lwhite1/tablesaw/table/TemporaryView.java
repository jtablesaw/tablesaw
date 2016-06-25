package com.github.lwhite1.tablesaw.table;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.Column;
import org.apache.commons.lang3.StringUtils;
import org.roaringbitmap.IntIterator;
import org.roaringbitmap.RoaringBitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * A TemporaryView is a facade around a Relation that acts as a filter.
 * Requests for data are forwarded to the underlying table.
 *
 * The view is only good until the structure of the underlying table changes, after which it is marked 'stale'.
 * At that point, it's operations will return an error.
 *
 * View is something of a misnomer, as it is not like a database view, which is merely a query masquerading as a table, 
 * nor is it like a materialized database view, which is like a real table. 
 */
class TemporaryView implements Relation {

  private String name;
  private Table table;
  private final RoaringBitmap rowMap;

  // True, if the underlying table has changed in ways that invalidate this view
  private boolean stale = false;

  /**
   * Returns a new View constructed from the given table, containing only the rows represented by the bitmpa
   */
  public TemporaryView(Table table, RoaringBitmap rowSelection) {
    this.rowMap = rowSelection;
    this.table = table;
  }

  public TemporaryView where(RoaringBitmap bitmap) {
    if (stale) {
      throw new StaleViewException();
    }
    rowMap.and(bitmap);
    return this;
  }

  @Override
  public Column column(int columnIndex) {
    if (stale) {
      throw new StaleViewException();
    }
    return table.column(columnIndex);
  }

  @Override
  public int columnCount() {
    if (stale) {
      throw new StaleViewException();
    }
    return table.columnCount();
  }

  @Override
  public int rowCount() {
    if (stale) {
      throw new StaleViewException();
    }
    return rowMap.getCardinality();
  }

  @Override
  public List<Column> columns() {
    if (stale) {
      throw new StaleViewException();
    }
    List<Column> columns = new ArrayList<>();
    for (int i = 0; i < columnCount(); i++) {
      columns.add(column(i));
    }
    return columns;
  }

  @Override
  public int columnIndex(Column column) {
    if (stale) {
      throw new StaleViewException();
    }

    return table.columnIndex(column);
  }

  @Override
  public String get(int c, int r) {
    if (stale) {
      throw new StaleViewException();
    }
    return table.get(c, r);
  }

  @Override
  public void addColumn(Column ... column) {
    throw new UnsupportedOperationException("TemporaryView does not support the addColumn operation");
  }

  @Override
  public String name() {
    return name;
  }

  /**
   * Clears all rows from this View, leaving the structure in place
   */
  @Override
  public void clear() {
    rowMap.clear();
  }

  @Override
  public List<String> columnNames() {
    if (stale) {
      throw new StaleViewException();
    }
    return table.columnNames();
  }
  
  @Override
  public void removeColumns(Column... columns) {
    throw new UnsupportedOperationException("TemporaryView does not support the removeColumns operation");
  }

  @Override
  public Table first(int nRows) {
    if (stale) {
      throw new StaleViewException();
    }
    RoaringBitmap newMap = new RoaringBitmap();
    int count = 0;
    IntIterator it = intIterator();
    while(it.hasNext() && count < nRows) {
      int row = it.next();
      newMap.add(row);
      count++;
    }
    return table.selectWhere(newMap);
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }


  @Override
  public String print() {
    if (stale) {
      throw new StaleViewException();
    }

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
    IntIterator iterator = intIterator();
    while (iterator.hasNext()) {
      int r = iterator.next();
      for (int i = 0; i < columnCount(); i++) {
        String cell = StringUtils.rightPad(get(i, r), colWidths[i]);
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
  @Override
  public int[] colWidths() {
    if (stale) {
      throw new StaleViewException();
    }

    int cols = columnCount();
    int[] widths = new int[cols];
    List<String> columnNames = columnNames();

    for (int i = 0; i < columnCount(); i++) {
      widths[i] = columnNames.get(i).length();
    }

    for (int rowNum = 0; rowNum < rowCount(); rowNum++) {
      for (int colNum = 0; colNum < cols; colNum++) {
        String value = get(colNum, rowNum);
        widths[colNum]
            = Math.max(widths[colNum], StringUtils.length(value));
      }
    }
    return widths;
  }

  public Table asTable() {
    if (stale) {
      throw new StaleViewException();
    }
    Table table = new Table(this.name());
    for (Column column : columns()) {
      table.addColumn(column.subset(rowMap));
    }
    return table;
  }

  public void markStale() {
    this.stale = true;
  }

  /**
   * Returns true if the underlying table has changed in a way that invalidates this TemporaryView
   */
  public boolean isStale() {
    return stale;
  }

  public IntIterator intIterator() {
    return rowMap.getIntIterator();
  }

  static class StaleViewException extends RuntimeException {

  }
}