package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.api.DateColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.util.Selection;

import java.time.LocalDate;

/**
 */
public class LocalDateBetween extends ColumnFilter {
  private LocalDate low;
  private LocalDate high;

  public LocalDateBetween(ColumnReference reference, LocalDate lowValue, LocalDate highValue) {
    super(reference);
    this.low = lowValue;
    this.high = highValue;
  }

  public Selection apply(Table relation) {
    DateColumn column = (DateColumn) relation.column(columnReference.getColumnName());
    Selection matches = column.isAfter(low);
    matches.and(column.isBefore(high));
    return matches;
  }
}
