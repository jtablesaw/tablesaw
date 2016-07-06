package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.api.DateColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.util.Selection;

import java.time.LocalDate;

/**
 */
public class DateEqualTo extends ColumnFilter {

  LocalDate value;

  public DateEqualTo(ColumnReference reference, LocalDate value) {
    super(reference);
    this.value = value;
  }

  public Selection apply(Table relation) {
    DateColumn dateColumn = (DateColumn) relation.column(columnReference.getColumnName());
    return dateColumn.isEqualTo(value);
  }
}
