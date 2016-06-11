package com.github.lwhite1.outlier.filter.dates;

import com.github.lwhite1.outlier.Table;
import com.github.lwhite1.outlier.columns.LocalDateColumn;
import com.github.lwhite1.outlier.filter.ColumnFilter;
import com.github.lwhite1.outlier.columns.ColumnReference;
import org.roaringbitmap.RoaringBitmap;

/**
 *
 */
public class LocalDateIsInFebruary extends ColumnFilter {

  public LocalDateIsInFebruary(ColumnReference reference) {
    super(reference);
  }

  @Override
  public RoaringBitmap apply(Table relation) {
    LocalDateColumn dateColumn = (LocalDateColumn) relation.column(columnReference().getColumnName());
    return dateColumn.isInFebruary();
  }
}
