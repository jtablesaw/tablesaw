package com.github.lwhite1.outlier.filter.text;

import com.github.lwhite1.outlier.Table;
import com.github.lwhite1.outlier.columns.CategoryColumn;
import com.github.lwhite1.outlier.filter.ColumnFilter;
import com.github.lwhite1.outlier.columns.Column;
import com.github.lwhite1.outlier.columns.ColumnReference;
import org.roaringbitmap.RoaringBitmap;

import javax.annotation.concurrent.Immutable;

/**
 * A filter that selects cells in which all text is uppercase
 */
@Immutable
public class TextStartsWith extends ColumnFilter {

  private String string;

  public TextStartsWith(ColumnReference reference, String string) {
    super(reference);
    this.string = string;
  }

  @Override
  public RoaringBitmap apply(Table relation) {
    Column column = relation.column(columnReference().getColumnName());
    CategoryColumn textColumn = (CategoryColumn) column;
    return textColumn.startsWith(string);
  }
}
