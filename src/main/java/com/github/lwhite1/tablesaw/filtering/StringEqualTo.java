package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.util.Selection;

/**
 * Implements EqualTo testing for Category and Text Columns
 */
public class StringEqualTo extends ColumnFilter {

  private String value;

  public StringEqualTo(ColumnReference reference, String value) {
    super(reference);
    this.value = value;
  }

  public Selection apply(Table relation) {
    Column column = relation.column(columnReference.getColumnName());
    ColumnType type = column.type();
    switch (type) {
      case CATEGORY: {
        CategoryColumn categoryColumn = (CategoryColumn) relation.column(columnReference.getColumnName());
        return categoryColumn.isEqualTo(value);
      }
      default:
        throw new UnsupportedOperationException(
            String.format("ColumnType %s does not support equalTo on a String value", type));
    }
  }
}
