package com.github.lwhite1.tablesaw.filtering.dates;

import com.github.lwhite1.tablesaw.api.DateColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.filtering.ColumnFilter;
import com.github.lwhite1.tablesaw.util.Selection;

/**
 *
 */
public class LocalDateIsBefore extends ColumnFilter {

  private int value;

  public LocalDateIsBefore(ColumnReference reference, int value) {
    super(reference);
    this.value = value;
  }

  @Override
  public Selection apply(Table relation) {

    DateColumn dateColumn = (DateColumn) relation.column(columnReference().getColumnName());
    return dateColumn.isBefore(value);
  }
}
