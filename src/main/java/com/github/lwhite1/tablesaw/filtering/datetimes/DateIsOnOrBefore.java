package com.github.lwhite1.tablesaw.filtering.datetimes;


import com.github.lwhite1.tablesaw.api.DateTimeColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.filtering.ColumnFilter;
import com.github.lwhite1.tablesaw.util.Selection;

import javax.annotation.concurrent.Immutable;

/**
 *
 */
@Immutable
public class DateIsOnOrBefore extends ColumnFilter {

  private int value;

  public DateIsOnOrBefore(ColumnReference reference, int value) {
    super(reference);
    this.value = value;
  }

  @Override
  public Selection apply(Table relation) {

    DateTimeColumn dateColumn = (DateTimeColumn) relation.column(columnReference().getColumnName());
    return dateColumn.isOnOrBefore(value);
  }
}
