package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.api.FloatColumn;
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
