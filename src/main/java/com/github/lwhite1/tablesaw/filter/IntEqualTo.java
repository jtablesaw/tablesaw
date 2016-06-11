package com.github.lwhite1.tablesaw.filter;

import com.github.lwhite1.tablesaw.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.columns.IntColumn;
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
