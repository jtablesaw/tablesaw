package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.columns.ColumnReference;
import com.deathrayresearch.outlier.columns.IntColumn;
import org.roaringbitmap.RoaringBitmap;

/**
 */
public class IntEqualTo extends ColumnFilter {

  private int value;

  public IntEqualTo(ColumnReference reference, int value) {
    super(reference);
    this.value = value;
  }

  public RoaringBitmap apply(Table relation) {
    IntColumn intColumn = (IntColumn) relation.column(columnReference.getColumnName());
    return intColumn.isEqualTo(value);
  }
}
