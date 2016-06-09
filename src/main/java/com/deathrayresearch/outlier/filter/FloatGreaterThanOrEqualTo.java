package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.columns.ColumnReference;
import com.deathrayresearch.outlier.columns.FloatColumn;
import org.roaringbitmap.RoaringBitmap;

/**
 */
public class FloatGreaterThanOrEqualTo extends ColumnFilter {

  private float value;

  public FloatGreaterThanOrEqualTo(ColumnReference reference, float value) {
    super(reference);
    this.value = value;
  }

  public RoaringBitmap apply(Table relation) {
    FloatColumn floatColumn = (FloatColumn) relation.column(columnReference.getColumnName());
    return floatColumn.isGreaterThanOrEqualTo(value);
  }
}
