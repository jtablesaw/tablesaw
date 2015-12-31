package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.CategoryColumn;
import com.deathrayresearch.outlier.Column;
import com.deathrayresearch.outlier.ColumnReference;
import com.deathrayresearch.outlier.ColumnType;
import com.deathrayresearch.outlier.Relation;
import com.deathrayresearch.outlier.TextColumn;
import org.roaringbitmap.RoaringBitmap;

/**
 */
public class TextEqualTo extends ColumnFilter {

  String value;

  public TextEqualTo(ColumnReference reference, String value) {
    super(reference);
    this.value = value;
  }

  public RoaringBitmap apply(Relation relation) {
    Column column = relation.column(columnReference.getColumnName());
    ColumnType type = column.type();
    switch (type) {
      case TEXT: {
        TextColumn textColumn = (TextColumn) relation.column(columnReference.getColumnName());
        return textColumn.isEqualTo(value);
      }
      case CAT: {
        CategoryColumn categoryColumn = (CategoryColumn) relation.column(columnReference.getColumnName());
        return categoryColumn.isEqualTo(value);
      }
      default:
        throw new UnsupportedOperationException(
            String.format("ColumnType %s does not support equalTo on a String value", type));
    }
  }
}
