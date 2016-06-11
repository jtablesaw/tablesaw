package com.github.lwhite1.outlier.filter.dates;

import com.github.lwhite1.outlier.Table;
import com.github.lwhite1.outlier.columns.ColumnReference;
import com.github.lwhite1.outlier.columns.LocalDateColumn;
import com.github.lwhite1.outlier.filter.ColumnFilter;
import org.roaringbitmap.RoaringBitmap;

/**
 *
 */
public class LocalDateIsInYear extends ColumnFilter {

  int year;

  public LocalDateIsInYear(ColumnReference reference, int year) {
    super(reference);
    this.year = year;
  }

  @Override
  public RoaringBitmap apply(Table relation) {

    LocalDateColumn dateColumn = (LocalDateColumn) relation.column(columnReference().getColumnName());
    return dateColumn.isInYear(year);
  }
}
