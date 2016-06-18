package com.github.lwhite1.tablesaw.filter.dates;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.filter.ColumnFilter;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.columns.LocalDateColumn;
import org.roaringbitmap.RoaringBitmap;

/**
 *
 */
public class LocalDateIsFriday extends ColumnFilter {

  public LocalDateIsFriday(ColumnReference columnReference) {
    super(columnReference);
  }

  @Override
  public RoaringBitmap apply(Table relation) {

    LocalDateColumn dateColumn = (LocalDateColumn) relation.column(columnReference().getColumnName());
    return dateColumn.isFriday();
  }
}
