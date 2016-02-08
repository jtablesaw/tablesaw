package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.columns.ColumnReference;
import com.deathrayresearch.outlier.columns.FloatColumn;
import com.deathrayresearch.outlier.Relation;
import org.roaringbitmap.RoaringBitmap;

/**
 */
public class FloatLessThanOrEqualTo extends ColumnFilter {

  private float value;

  public FloatLessThanOrEqualTo(ColumnReference reference, float value) {
    super(reference);
    this.value = value;
  }

  public RoaringBitmap apply(Relation relation) {
    FloatColumn floatColumn = (FloatColumn) relation.column(columnReference.getColumnName());
    return floatColumn.isLessThanOrEqualTo(value);
  }
}
