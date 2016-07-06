package com.github.lwhite1.tablesaw.filtering.times;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.api.TimeColumn;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.filtering.ColumnFilter;
import com.github.lwhite1.tablesaw.util.Selection;

import java.time.LocalTime;

/**
 *
 */
public class IsBefore extends ColumnFilter {

  private LocalTime value;

  public IsBefore(ColumnReference reference, LocalTime value) {
    super(reference);
    this.value = value;
  }

  public Selection apply(Table relation) {
    TimeColumn timeColumn = (TimeColumn) relation.column(columnReference().getColumnName());
    return timeColumn.isBefore(value);
  }
}
