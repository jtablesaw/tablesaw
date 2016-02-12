package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.Relation;
import com.deathrayresearch.outlier.columns.ColumnReference;
import com.deathrayresearch.outlier.columns.IntColumn;
import org.roaringbitmap.RoaringBitmap;

/**
 */
public class IntBetween extends ColumnFilter {
  private int low;
  private int high;

  public IntBetween(ColumnReference reference, int lowValue, int highValue) {
    super(reference);
    this.low = lowValue;
    this.high = highValue;
  }

  public RoaringBitmap apply(Relation relation) {
    IntColumn intColumn = (IntColumn) relation.column(columnReference.getColumnName());
    RoaringBitmap matches = intColumn.isGreaterThan(low);
    matches.and(intColumn.isLessThan(high));
    return matches;
  }
}
