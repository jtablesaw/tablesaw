package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.util.Selection;

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

  public Selection apply(Table relation) {
    IntColumn intColumn = (IntColumn) relation.column(columnReference.getColumnName());
    Selection matches = intColumn.isGreaterThan(low);
    matches.toBitmap().and(intColumn.isLessThan(high).toBitmap());
    return matches;
  }
}
