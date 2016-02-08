package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.columns.ColumnReference;
import com.deathrayresearch.outlier.columns.LocalTimeColumn;
import com.deathrayresearch.outlier.Relation;
import org.roaringbitmap.RoaringBitmap;

import java.time.LocalTime;

/**
 */
public class TimeEqualTo extends ColumnFilter {

  LocalTime value;

  public TimeEqualTo(ColumnReference reference, LocalTime value) {
    super(reference);
    this.value = value;
  }

  public RoaringBitmap apply(Relation relation) {
    LocalTimeColumn dateColumn = (LocalTimeColumn) relation.column(columnReference.getColumnName());
    return dateColumn.isEqualTo(value);
  }
}
