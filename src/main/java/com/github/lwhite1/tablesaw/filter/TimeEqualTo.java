package com.github.lwhite1.tablesaw.filter;

import com.github.lwhite1.tablesaw.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.columns.LocalTimeColumn;
import org.roaringbitmap.RoaringBitmap;

import java.time.LocalTime;

/**
 */
public class TimeEqualTo extends ColumnFilter {

  LocalTime value;

  public TimeEqualTo(ColumnReference reference, LocalTime value) {
    super(reference);
    this.value = value;
  }

  public RoaringBitmap apply(Table relation) {
    LocalTimeColumn dateColumn = (LocalTimeColumn) relation.column(columnReference.getColumnName());
    return dateColumn.isEqualTo(value);
  }
}
