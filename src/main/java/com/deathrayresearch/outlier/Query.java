package com.deathrayresearch.outlier;

import com.deathrayresearch.outlier.filter.Filter;
import org.roaringbitmap.RoaringBitmap;

/**
 */
public class Query {

  private Relation table;
  Column[] columnSelection = new Column[0];
  Filter filter;

  public Query(Relation table) {
    this.table = table;
    columnSelection = new Column[table.columnCount()];
    for (int i = 0; i < table.getColumns().size(); i++) {
      columnSelection[i] = table.column(i);
    }
  }

  Query where(Filter filter) {
    this.filter = filter;
    return this;
  }

  View run() {
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
