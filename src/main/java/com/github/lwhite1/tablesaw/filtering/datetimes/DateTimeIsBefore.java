package com.github.lwhite1.tablesaw.filtering.datetimes;

import com.github.lwhite1.tablesaw.api.DateTimeColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.filtering.ColumnFilter;
import com.github.lwhite1.tablesaw.util.Selection;

import java.time.LocalDateTime;

/**
 *
 */
public class DateTimeIsBefore extends ColumnFilter {

  private LocalDateTime value;

  public DateTimeIsBefore(ColumnReference reference, LocalDateTime value) {
    super(reference);
    this.value = value;
  }

  @Override
  public Selection apply(Table relation) {

    DateTimeColumn dateColumn = (DateTimeColumn) relation.column(columnReference().getColumnName());
    return dateColumn.isBefore(value);
  }
}
