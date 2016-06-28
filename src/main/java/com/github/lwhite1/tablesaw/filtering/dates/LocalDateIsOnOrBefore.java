package com.github.lwhite1.tablesaw.filtering.dates;


import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.api.DateColumn;
import com.github.lwhite1.tablesaw.filtering.ColumnFilter;
import org.roaringbitmap.RoaringBitmap;

import javax.annotation.concurrent.Immutable;

/**
 *
 */
@Immutable
public class LocalDateIsOnOrBefore extends ColumnFilter {

  private int value;

  public LocalDateIsOnOrBefore(ColumnReference reference, int value) {
    super(reference);
    this.value = value;
  }

  @Override
  public RoaringBitmap apply(Table relation) {

    DateColumn dateColumn = (DateColumn) relation.column(columnReference().getColumnName());
    return dateColumn.isOnOrBefore(value);
  }
}
