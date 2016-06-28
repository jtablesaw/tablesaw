package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.columns.ColumnReference;

/**
 */
public abstract class ColumnFilter extends Filter {

  ColumnReference columnReference;

  public ColumnFilter(ColumnReference columnReference) {
    this.columnReference = columnReference;
  }

  public ColumnReference columnReference() {
    return columnReference;
  }

  public ColumnReference getColumnReference() {
    return columnReference;
  }

}
