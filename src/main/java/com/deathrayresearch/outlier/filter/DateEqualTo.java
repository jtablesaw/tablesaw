package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.columns.ColumnReference;
import com.deathrayresearch.outlier.columns.LocalDateColumn;
import org.roaringbitmap.RoaringBitmap;

import java.time.LocalDate;

/**
 */
public class DateEqualTo extends ColumnFilter {

  LocalDate value;

  public DateEqualTo(ColumnReference reference, LocalDate value) {
    super(reference);
    this.value = value;
  }

  public RoaringBitmap apply(Table relation) {
    LocalDateColumn dateColumn = (LocalDateColumn) relation.column(columnReference.getColumnName());
    return dateColumn.isEqualTo(value);
  }
}
