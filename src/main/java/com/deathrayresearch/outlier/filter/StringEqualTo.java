package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.columns.CategoryColumn;
import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.ColumnReference;
import com.deathrayresearch.outlier.api.ColumnType;
import org.roaringbitmap.RoaringBitmap;

/**
 * Implements EqualTo testing for Category and Text Columns
 */
public class StringEqualTo extends ColumnFilter {

  private String value;

  public StringEqualTo(ColumnReference reference, String value) {
    super(reference);
    this.value = value;
  }

  public RoaringBitmap apply(Table relation) {
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
