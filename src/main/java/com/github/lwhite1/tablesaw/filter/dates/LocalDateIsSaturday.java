package com.github.lwhite1.tablesaw.filter.dates;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.filter.ColumnFilter;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.columns.LocalDateColumn;
import org.roaringbitmap.RoaringBitmap;

/**
 *
 */
public class LocalDateIsSaturday extends ColumnFilter {

  public LocalDateIsSaturday(ColumnReference columnReference) {
    super(columnReference);
  }

  @Override
  public RoaringBitmap apply(Table relation) {

    LocalDateColumn dateColumn = (LocalDateColumn) relation.column(columnReference().getColumnName());
    return dateColumn.isSaturday();
  }
}
