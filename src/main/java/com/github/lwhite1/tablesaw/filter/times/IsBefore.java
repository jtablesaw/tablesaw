package com.github.lwhite1.tablesaw.filter.times;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.columns.TimeColumn;
import com.github.lwhite1.tablesaw.filter.ColumnFilter;
import org.roaringbitmap.RoaringBitmap;

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

  public RoaringBitmap apply(Table relation) {
    TimeColumn timeColumn = (TimeColumn) relation.column(columnReference().getColumnName());
    return timeColumn.isBefore(value);
  }
}
