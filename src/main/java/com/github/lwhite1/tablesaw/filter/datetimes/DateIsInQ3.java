package com.github.lwhite1.tablesaw.filter.datetimes;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.columns.DateTimeColumn;
import com.github.lwhite1.tablesaw.filter.ColumnFilter;
import org.roaringbitmap.RoaringBitmap;

/**
 *
 */
public class DateIsInQ3 extends ColumnFilter {


  public DateIsInQ3(ColumnReference reference) {
    super(reference);
  }

  @Override
  public RoaringBitmap apply(Table relation) {

    DateTimeColumn dateColumn = (DateTimeColumn) relation.column(columnReference().getColumnName());
    return dateColumn.isInQ3();
  }
}
