package com.github.lwhite1.outlier.filter.dates;

import com.github.lwhite1.outlier.Table;
import com.github.lwhite1.outlier.filter.ColumnFilter;
import com.github.lwhite1.outlier.columns.ColumnReference;
import com.github.lwhite1.outlier.columns.LocalDateColumn;
import org.roaringbitmap.RoaringBitmap;

/**
 *
 */
public class LocalDateIsWednesday extends ColumnFilter {

  public LocalDateIsWednesday(ColumnReference columnReference) {
    super(columnReference);
  }

  @Override
  public RoaringBitmap apply(Table relation) {

    LocalDateColumn dateColumn = (LocalDateColumn) relation.column(columnReference().getColumnName());
    return dateColumn.isWednesday();
  }
}
