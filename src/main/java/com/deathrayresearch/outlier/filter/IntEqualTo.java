package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.columns.ColumnReference;
import com.deathrayresearch.outlier.columns.IntColumn;
import com.deathrayresearch.outlier.Relation;
import org.roaringbitmap.RoaringBitmap;

/**
 */
public class IntEqualTo extends ColumnFilter {

  int value;

  public IntEqualTo(ColumnReference reference, int value) {
    super(reference);
    this.value = value;
  }

  public RoaringBitmap apply(Relation relation) {
    IntColumn intColumn = (IntColumn) relation.column(columnReference.getColumnName());
    return intColumn.isEqualTo(value);
  }
}
