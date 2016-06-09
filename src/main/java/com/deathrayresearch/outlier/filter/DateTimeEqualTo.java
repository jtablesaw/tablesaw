package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.columns.ColumnReference;
import com.deathrayresearch.outlier.columns.LocalDateTimeColumn;
import org.roaringbitmap.RoaringBitmap;

import java.time.LocalDateTime;

/**
 */
public class DateTimeEqualTo extends ColumnFilter {

  LocalDateTime value;

  public DateTimeEqualTo(ColumnReference reference, LocalDateTime value) {
    super(reference);
    this.value = value;
  }

  public RoaringBitmap apply(Table relation) {
    LocalDateTimeColumn dateColumn = (LocalDateTimeColumn) relation.column(columnReference.getColumnName());
    return dateColumn.isEqualTo(value);
  }
}
