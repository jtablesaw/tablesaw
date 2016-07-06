package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.api.DateTimeColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.util.Selection;

import java.time.LocalDateTime;

/**
 */
public class DateTimeEqualTo extends ColumnFilter {

  LocalDateTime value;

  public DateTimeEqualTo(ColumnReference reference, LocalDateTime value) {
    super(reference);
    this.value = value;
  }

  public Selection apply(Table relation) {
    DateTimeColumn dateColumn = (DateTimeColumn) relation.column(columnReference.getColumnName());
    return dateColumn.isEqualTo(value);
  }
}
