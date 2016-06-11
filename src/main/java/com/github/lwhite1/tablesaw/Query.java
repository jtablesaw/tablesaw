package com.github.lwhite1.tablesaw;

import com.github.lwhite1.tablesaw.filter.Filter;
import com.github.lwhite1.tablesaw.columns.Column;

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
}
