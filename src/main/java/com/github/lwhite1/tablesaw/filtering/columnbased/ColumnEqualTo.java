package com.github.lwhite1.tablesaw.filtering.columnbased;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.LongColumn;
import com.github.lwhite1.tablesaw.api.ShortColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.filtering.ColumnFilter;
import com.github.lwhite1.tablesaw.util.Selection;
import com.google.common.base.Preconditions;

/**
 *
 */
public class ColumnEqualTo extends ColumnFilter {

  private final ColumnReference otherColumn;

  public ColumnEqualTo(ColumnReference a, ColumnReference b) {
    super(a);
    otherColumn = b;
  }

  public Selection apply(Table relation) {

    Column column = relation.column(columnReference().getColumnName());
    Column other = relation.column(otherColumn.getColumnName());

    Preconditions.checkArgument(column.type() == other.type());

    if (column.type() == ColumnType.INTEGER)
      return apply((IntColumn) column, (IntColumn) other);

    if (column.type() == ColumnType.LONG_INT)
      return apply((LongColumn) column, (LongColumn) other);

    if (column.type() == ColumnType.SHORT_INT)
      return apply((ShortColumn) column, (ShortColumn) other);

    throw new UnsupportedOperationException("Not yet implemented for this column type");
  }

  private static Selection apply(IntColumn column1, IntColumn column2) {
    return column1.isEqualTo(column2);
  }

  private static Selection apply(ShortColumn column1, ShortColumn column2) {
    return column1.isEqualTo(column2);
  }

  private static Selection apply(LongColumn column1, LongColumn column2) {
    return column1.isEqualTo(column2);
  }
}
