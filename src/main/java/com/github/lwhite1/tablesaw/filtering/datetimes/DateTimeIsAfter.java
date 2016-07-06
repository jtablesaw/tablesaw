package com.github.lwhite1.tablesaw.filtering.datetimes;


import com.github.lwhite1.tablesaw.api.DateTimeColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.filtering.ColumnFilter;
import com.github.lwhite1.tablesaw.util.Selection;

import javax.annotation.concurrent.Immutable;
import java.time.LocalDateTime;

/**
 *
 */
@Immutable
public class DateTimeIsAfter extends ColumnFilter {

  private LocalDateTime value;

  public DateTimeIsAfter(ColumnReference reference, LocalDateTime value) {
    super(reference);
    this.value = value;
  }

  @Override
  public Selection apply(Table relation) {

    DateTimeColumn dateColumn = relation.dateTimeColumn(columnReference().getColumnName());
    return dateColumn.isAfter(value);
  }
}
