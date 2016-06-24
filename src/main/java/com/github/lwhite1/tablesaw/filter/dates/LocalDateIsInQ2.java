package com.github.lwhite1.tablesaw.filter.dates;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.columns.LocalDateTimeColumn;
import com.github.lwhite1.tablesaw.filter.ColumnFilter;
import com.github.lwhite1.tablesaw.api.Table;
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
    Column column = relation.column(columnReference().getColumnName());
    if (column.type() == ColumnType.LOCAL_DATE) {
      LocalDateColumn dateColumn = (LocalDateColumn) column;
      return dateColumn.isInQ2();
    }
    if (column.type() == ColumnType.LOCAL_DATE_TIME) {
      LocalDateTimeColumn dateColumn = (LocalDateTimeColumn) column;
      return dateColumn.isInQ2();
    }
    else throw new UnsupportedOperationException("Column " + column.name() + "does not support method isInQ2");
  }
}
