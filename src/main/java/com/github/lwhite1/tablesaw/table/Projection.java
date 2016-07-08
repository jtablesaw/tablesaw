package com.github.lwhite1.tablesaw.table;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.filtering.Filter;
import com.github.lwhite1.tablesaw.util.Selection;

/**
 * A table projection, i.e. the subset of columns in a table that should be returned in a query
 */
public class Projection {

  private final Table table;
  private final Column[] columns;

  public Projection(Table table, String[] columnNames) {
    this.table = table;
    columns = new Column[columnNames.length];
    for (int i = 0; i < columnNames.length; i++) {
      String name = columnNames[i];
      columns[i] = table.column(name);
    }
  }

  public Table where(Filter filter) {
    Table projectedTable = Table.create(table.name(), columns);
    Table newTable = projectedTable.emptyCopy();
    Selection map = filter.apply(table);
    Rows.copyRowsToTable(map, projectedTable, newTable);
    return newTable;
  }
}
