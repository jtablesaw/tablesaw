package com.github.lwhite1.tablesaw.filter;

import com.github.lwhite1.tablesaw.Table;
import com.github.lwhite1.tablesaw.columns.IntColumn;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
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

  public RoaringBitmap apply(Table relation) {
    IntColumn intColumn = (IntColumn) relation.column(columnReference.getColumnName());
    RoaringBitmap matches = intColumn.isGreaterThan(low);
    matches.and(intColumn.isLessThan(high));
    return matches;
  }
}
