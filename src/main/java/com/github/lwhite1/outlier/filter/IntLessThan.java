package com.github.lwhite1.outlier.filter;

import com.github.lwhite1.outlier.columns.IntColumn;
import com.github.lwhite1.outlier.Table;
import com.github.lwhite1.outlier.columns.ColumnReference;
import org.roaringbitmap.RoaringBitmap;

/**
 */
public class IntLessThan extends ColumnFilter {

  private int value;

  public IntLessThan(ColumnReference reference, int value) {
    super(reference);
    this.value = value;
  }

  public RoaringBitmap apply(Table relation) {
    IntColumn intColumn = (IntColumn) relation.column(columnReference.getColumnName());
    return intColumn.isLessThan(value);
  }
}
