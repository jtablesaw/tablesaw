package com.github.lwhite1.tablesaw.filter;

import com.github.lwhite1.tablesaw.Table;
import com.github.lwhite1.tablesaw.columns.LocalDateColumn;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
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
