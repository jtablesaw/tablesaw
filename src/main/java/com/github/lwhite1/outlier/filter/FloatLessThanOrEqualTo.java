package com.github.lwhite1.outlier.filter;

import com.github.lwhite1.outlier.Table;
import com.github.lwhite1.outlier.columns.ColumnReference;
import com.github.lwhite1.outlier.columns.FloatColumn;
import org.roaringbitmap.RoaringBitmap;

/**
 */
public class FloatLessThanOrEqualTo extends ColumnFilter {

  private float value;

  public FloatLessThanOrEqualTo(ColumnReference reference, float value) {
    super(reference);
    this.value = value;
  }

  public RoaringBitmap apply(Table relation) {
    FloatColumn floatColumn = (FloatColumn) relation.column(columnReference.getColumnName());
    return floatColumn.isLessThanOrEqualTo(value);
  }
}
