package com.deathrayresearch.outlier.filter.dates;

import com.deathrayresearch.outlier.Relation;
import com.deathrayresearch.outlier.columns.ColumnReference;
import com.deathrayresearch.outlier.columns.LocalDateColumn;
import com.deathrayresearch.outlier.filter.ColumnFilter;
import org.roaringbitmap.RoaringBitmap;

import java.time.LocalDate;
import java.time.Month;

/**
 *
 */
public class LocalDateIsInQ3 extends ColumnFilter {


  public LocalDateIsInQ3(ColumnReference reference, int value) {
    super(reference);
    this.value = value;
  }

  int value;

  @Override
  public RoaringBitmap apply(Relation relation) {

    LocalDateColumn dateColumn = (LocalDateColumn) relation.column(columnReference().getColumnName());;
    return dateColumn.isInQ3();
  }
}
