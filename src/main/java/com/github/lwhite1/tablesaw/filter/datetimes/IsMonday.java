package com.github.lwhite1.tablesaw.filter.datetimes;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.columns.DateColumn;
import com.github.lwhite1.tablesaw.columns.DateTimeColumn;
import com.github.lwhite1.tablesaw.filter.ColumnFilter;
import org.roaringbitmap.RoaringBitmap;

/**
 *
 */
public class IsMonday extends ColumnFilter {

  public IsMonday(ColumnReference reference) {
    super(reference);
  }

  @Override
  public RoaringBitmap apply(Table relation) {
    String name = columnReference().getColumnName();
    Column column = relation.column(name);
    ColumnType type = column.type();
    switch (type) {
      case LOCAL_DATE:
        DateColumn dateColumn = relation.dateColumn(name);
        return dateColumn.isMonday();
      case LOCAL_DATE_TIME:
        DateTimeColumn dateTimeColumn = relation.dateTimeColumn(name);
        return dateTimeColumn.isMonday();
      default:
        throw new UnsupportedOperationException("Columns of type " + type.name() + " do not support the operation "
            + "isMonday() ");
    }
  }
}
