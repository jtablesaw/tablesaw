package com.deathrayresearch.outlier.filter.dates;

import com.deathrayresearch.outlier.Relation;
import com.deathrayresearch.outlier.columns.ColumnReference;
import com.deathrayresearch.outlier.columns.LocalDateColumn;
import com.deathrayresearch.outlier.filter.ColumnFilter;
import org.roaringbitmap.RoaringBitmap;

/**
 *
 */
public class LocalDateIsFriday extends ColumnFilter {

  public LocalDateIsFriday(ColumnReference columnReference) {
    super(columnReference);
  }

  @Override
  public RoaringBitmap apply(Relation relation) {

    LocalDateColumn dateColumn = (LocalDateColumn) relation.column(columnReference().getColumnName());
    return dateColumn.isFriday();
  }
}
