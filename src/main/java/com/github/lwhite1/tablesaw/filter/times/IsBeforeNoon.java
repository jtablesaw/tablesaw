package com.github.lwhite1.tablesaw.filter.times;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.columns.TimeColumn;
import com.github.lwhite1.tablesaw.filter.ColumnFilter;
import org.roaringbitmap.RoaringBitmap;

/**
 *
 */
public class IsBeforeNoon extends ColumnFilter {

  public IsBeforeNoon(ColumnReference reference) {
    super(reference);
  }

  @Override
  public RoaringBitmap apply(Table relation) {
    TimeColumn timeColumn = (TimeColumn) relation.column(columnReference().getColumnName());
    return timeColumn.isBeforeNoon();
  }
}
