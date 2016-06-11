package com.github.lwhite1.outlier.filter;

import com.github.lwhite1.outlier.Table;
import com.github.lwhite1.outlier.columns.Column;
import com.github.lwhite1.outlier.columns.ColumnReference;
import com.github.lwhite1.outlier.columns.IntColumn;
import com.github.lwhite1.outlier.columns.LongColumn;
import com.github.lwhite1.outlier.columns.ShortColumn;
import org.roaringbitmap.RoaringBitmap;

/**
 */
public class IntGreaterThan extends ColumnFilter {

  private int value;

  public IntGreaterThan(ColumnReference reference, int value) {
    super(reference);
    this.value = value;
  }

  public RoaringBitmap apply(Table relation) {
    Column column = relation.column(columnReference.getColumnName());
    switch(column.type()) {
      case INTEGER : return ((IntColumn) column).isGreaterThan(value);
      case SHORT_INT : return ((ShortColumn) column).isGreaterThan((short) value);
      case LONG_INT : return ((LongColumn) column).isGreaterThan(value);
    }
    IntColumn intColumn = (IntColumn) column;
    return intColumn.isGreaterThan(value);
  }
}
