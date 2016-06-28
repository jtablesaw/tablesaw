package com.github.lwhite1.tablesaw.table;

import com.github.lwhite1.tablesaw.aggregator.NumericReduceFunction;
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

  /**
   * Returns a new View constructed from the given table, containing only the rows represented by the bitmpa
   */
  public TemporaryView(Table table, RoaringBitmap rowSelection) {
    this.name = table.name();  //TODO(lwhite): Is this really needed, or can we reference the table name?
    this.rowMap = rowSelection;
    this.table = table;
  }

  public TemporaryView where(RoaringBitmap bitmap) {
    rowMap.and(bitmap);
    return this;
  }

  @Override
  public Column column(int columnIndex) {
    return table.column(columnIndex);
  }

  @Override
  public int columnCount() {
    return table.columnCount();
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
    return table.columnIndex(column);
  }

  @Override
  public String get(int c, int r) {
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
    return table.columnNames();
  }
  
  @Override
  public void removeColumns(Column... columns) {
    throw new UnsupportedOperationException("TemporaryView does not support the removeColumns operation");
  }

  @Override
  public Table first(int nRows) {
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
    Table table = new Table(this.name());
    for (Column column : columns()) {
      table.addColumn(column.subset(rowMap));
    }
    return table;
  }

  IntIterator intIterator() {
    return rowMap.getIntIterator();
  }

  /**
   * Returns the result of applying the given function to the specified column
   *
   * @param numericColumnName   The name of a numeric (integer, float, etc.) column in this table
   * @param function            A numeric reduce function
   *
   * @throws IllegalArgumentException if numericColumnName doesn't name a numeric column in this table
   * @return  the function result
   */
  public double reduce(String numericColumnName, NumericReduceFunction function) {
    Column column = column(numericColumnName);
    return function.reduce(column.subset(rowMap).toDoubleArray());
  }

  public String toString() {
    return "View " + name() + ": Size = " + rowCount() + " x " + columns().size();
  }
}