package com.github.lwhite1.outlier.filter;

import com.github.lwhite1.outlier.Table;
import com.github.lwhite1.outlier.columns.ColumnReference;
import com.github.lwhite1.outlier.columns.IntColumn;
import org.roaringbitmap.RoaringBitmap;

/**
 */
public class IntLessThanOrEqualTo extends ColumnFilter {

  private int value;

  public IntLessThanOrEqualTo(ColumnReference reference, int value) {
    super(reference);
    this.value = value;
  }

  public RoaringBitmap apply(Table relation) {
    IntColumn intColumn = (IntColumn) relation.column(columnReference.getColumnName());
    return intColumn.isLessThanOrEqualTo(value);
  }
}
