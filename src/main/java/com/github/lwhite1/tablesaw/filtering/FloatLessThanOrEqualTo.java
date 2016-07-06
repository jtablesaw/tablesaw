package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.api.FloatColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.util.Selection;

/**
 */
public class FloatLessThanOrEqualTo extends ColumnFilter {

  private float value;

  public FloatLessThanOrEqualTo(ColumnReference reference, float value) {
    super(reference);
    this.value = value;
  }

  public Selection apply(Table relation) {
    FloatColumn floatColumn = (FloatColumn) relation.column(columnReference.getColumnName());
    return floatColumn.isLessThanOrEqualTo(value);
  }
}
