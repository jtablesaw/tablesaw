package com.github.lwhite1.tablesaw.filter;

import com.github.lwhite1.tablesaw.Table;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.columns.IntColumn;
import com.github.lwhite1.tablesaw.columns.LongColumn;
import com.github.lwhite1.tablesaw.columns.ShortColumn;
import org.roaringbitmap.RoaringBitmap;

import static com.github.lwhite1.tablesaw.columns.IntColumnUtils.isGreaterThan;

/**
 *
 */
public class IntGreaterThan extends ColumnFilter {

  private int value;

  public IntGreaterThan(ColumnReference reference, int value) {
    super(reference);
    this.value = value;
  }

  public RoaringBitmap apply(Table relation) {
    Column column = relation.column(columnReference.getColumnName());
    switch (column.type()) {
      case INTEGER:
        return ((IntColumn) column).isGreaterThan(value);
      case SHORT_INT:
        return ((ShortColumn) column).isGreaterThan((short) value);
      case LONG_INT:
        return ((LongColumn) column).isGreaterThan(value);
    }
    IntColumn intColumn = (IntColumn) column;
    return intColumn.apply(isGreaterThan, value);
  }
}
