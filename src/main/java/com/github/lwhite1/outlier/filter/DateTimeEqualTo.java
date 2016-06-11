package com.github.lwhite1.outlier.filter;

import com.github.lwhite1.outlier.Table;
import com.github.lwhite1.outlier.columns.ColumnReference;
import com.github.lwhite1.outlier.columns.LocalDateTimeColumn;
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
