package com.github.lwhite1.tablesaw.filter.dates;


import com.github.lwhite1.tablesaw.Table;
import com.github.lwhite1.tablesaw.filter.ColumnFilter;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.columns.LocalDateColumn;
import org.roaringbitmap.RoaringBitmap;

import javax.annotation.concurrent.Immutable;

/**
 *
 */
@Immutable
public class LocalDateIsAfter extends ColumnFilter {

  int value;

  public LocalDateIsAfter(ColumnReference reference, int value) {
    super(reference);
    this.value = value;
  }

  @Override
  public RoaringBitmap apply(Table relation) {

    LocalDateColumn dateColumn = (LocalDateColumn) relation.column(columnReference().getColumnName());
    return dateColumn.isAfter(value);
  }
}
