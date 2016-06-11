package com.github.lwhite1.tablesaw.filter;

import com.github.lwhite1.tablesaw.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import org.roaringbitmap.RoaringBitmap;

/**
 */
public class ColumnFilter extends Filter {

  ColumnReference columnReference;

  public ColumnFilter(ColumnReference columnReference) {
    this.columnReference = columnReference;
  }

  public RoaringBitmap apply(Table relation) {
    return null;
  }

  public ColumnReference columnReference() {
    return columnReference;
  }
}
