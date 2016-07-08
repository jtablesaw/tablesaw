package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.util.Selection;

/**
 */
public class IntEqualTo extends ColumnFilter {

  private int value;

  public IntEqualTo(ColumnReference reference, int value) {
    super(reference);
    this.value = value;
  }

  public Selection apply(Table relation) {
    IntColumn intColumn = (IntColumn) relation.column(columnReference.getColumnName());
    return intColumn.isEqualTo(value);
  }
}
