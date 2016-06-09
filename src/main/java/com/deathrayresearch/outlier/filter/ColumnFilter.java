package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.columns.ColumnReference;
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
