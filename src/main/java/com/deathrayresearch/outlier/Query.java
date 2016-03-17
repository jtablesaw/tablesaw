package com.deathrayresearch.outlier;

import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.filter.Filter;
import org.roaringbitmap.RoaringBitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * A query, waiting to be executed
 */
public class Query {

  private Relation table;
  private List<Column> columnSelection;
  private Filter filter;

  public Query(Relation table) {
    this.table = table;
    columnSelection = new ArrayList<>(table.columnCount());
    for (int i = 0; i < table.columns().size(); i++) {
      columnSelection.add(table.column(i));
    }
  }

  public Query(Table table, String[] columnName) {
    this.table = table;
    columnSelection = new ArrayList<>(columnName.length);
    for (String name : columnName) {
      columnSelection.add(table.column(name));
    }
  }

  public Query where(Filter filter) {
    this.filter = filter;
    return this;
  }

  public View run() {
    RoaringBitmap rowSelection;
    if (filter != null) {
      rowSelection = filter.apply(this.table);
    } else {
      rowSelection = new RoaringBitmap();
      rowSelection.flip(0, table.rowCount());
    }
    return new View(table, columnSelection, rowSelection);
  }
}
