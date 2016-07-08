package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.api.FloatColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.util.Selection;

import static com.github.lwhite1.tablesaw.columns.FloatColumnUtils.isGreaterThan;

/**
 */
public class FloatGreaterThan extends ColumnFilter {

  private float value;

  public FloatGreaterThan(ColumnReference reference, float value) {
    super(reference);
    this.value = value;
  }

  public Selection apply(Table relation) {
    FloatColumn floatColumn = (FloatColumn) relation.column(columnReference.getColumnName());
    return floatColumn.select(isGreaterThan, value);
  }
}
