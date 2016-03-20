package com.deathrayresearch.outlier.filter.dates;


import com.deathrayresearch.outlier.Relation;
import com.deathrayresearch.outlier.columns.ColumnReference;
import com.deathrayresearch.outlier.columns.LocalDateColumn;
import com.deathrayresearch.outlier.filter.ColumnFilter;
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
  public RoaringBitmap apply(Relation relation) {

    LocalDateColumn dateColumn = (LocalDateColumn) relation.column(columnReference().getColumnName());
    return dateColumn.isAfter(value);
  }
}
