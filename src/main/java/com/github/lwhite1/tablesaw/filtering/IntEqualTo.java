package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.FloatColumn;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.LongColumn;
import com.github.lwhite1.tablesaw.api.ShortColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.util.Selection;

/**
 */
public class IntEqualTo extends ColumnFilter {

  private int value;

  public IntEqualTo(ColumnReference reference, int value) {
    super(reference);
    this.value = value;
  }

  public Selection apply(Table table) {
    Column column = table.column(columnReference.getColumnName());
    ColumnType type = column.type();
    switch(type) {
      case INTEGER:
        IntColumn intColumn = (IntColumn) column;
        return intColumn.isEqualTo(value);
      case SHORT_INT:
        ShortColumn shorts = (ShortColumn) column;
        return shorts.isEqualTo((short) value);
      case LONG_INT:
        LongColumn longs = (LongColumn) column;
        return longs.isEqualTo(value);
      case FLOAT:
        FloatColumn floats = (FloatColumn) column;
        return floats.isEqualTo((float) value);
      default:
        throw new UnsupportedOperationException("IsEqualTo(anInt) is not supported for column type " + type);
    }
  }
}
