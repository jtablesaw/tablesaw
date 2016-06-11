package com.github.lwhite1.tablesaw.filter.dates;

import com.github.lwhite1.tablesaw.filter.ColumnFilter;
import com.github.lwhite1.tablesaw.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.columns.LocalDateColumn;
import org.roaringbitmap.RoaringBitmap;

/**
 *
 */
public class LocalDateIsInQ2 extends ColumnFilter {

  public LocalDateIsInQ2(ColumnReference reference) {
    super(reference);
  }

  @Override
  public RoaringBitmap apply(Table relation) {

    LocalDateColumn dateColumn = (LocalDateColumn) relation.column(columnReference().getColumnName());
    return dateColumn.isInQ2();
  }
}
