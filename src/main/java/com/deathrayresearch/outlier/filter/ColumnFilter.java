package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.columns.ColumnReference;
import com.deathrayresearch.outlier.Relation;
import org.roaringbitmap.RoaringBitmap;

/**
 */
public class ColumnFilter extends Filter {

  ColumnReference columnReference;

  public ColumnFilter(ColumnReference columnReference) {
    this.columnReference = columnReference;
  }

  public RoaringBitmap apply(Relation relation) {
    return null;
  }
}
