package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.api.TimeColumn;
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
    TimeColumn dateColumn = (TimeColumn) relation.column(columnReference.getColumnName());
    return dateColumn.isEqualTo(value);
  }
}
