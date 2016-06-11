package com.github.lwhite1.outlier.filter.text;


import com.github.lwhite1.outlier.Table;
import com.github.lwhite1.outlier.columns.CategoryColumn;
import com.github.lwhite1.outlier.columns.Column;
import com.github.lwhite1.outlier.columns.ColumnReference;
import com.github.lwhite1.outlier.filter.ColumnFilter;
import org.roaringbitmap.RoaringBitmap;

import javax.annotation.concurrent.Immutable;

/**
 * A filter that selects cells in which all text is uppercase
 */
@Immutable
public class TextHasLengthEqualTo extends ColumnFilter {

  private int length;

  public TextHasLengthEqualTo(ColumnReference reference, int length) {
    super(reference);
    this.length = length;
  }

  @Override
  public RoaringBitmap apply(Table relation) {
    Column column = relation.column(columnReference().getColumnName());
    CategoryColumn textColumn = (CategoryColumn) column;
    return textColumn.hasLengthEqualTo(length);
  }
}
