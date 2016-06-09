package com.deathrayresearch.outlier.filter.text;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.columns.CategoryColumn;
import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.ColumnReference;
import com.deathrayresearch.outlier.filter.ColumnFilter;
import org.roaringbitmap.RoaringBitmap;

import javax.annotation.concurrent.Immutable;

/**
 * A filter that selects cells in which all text is lowercase
 */
@Immutable
public class TextIsAlpha extends ColumnFilter {

  public TextIsAlpha(ColumnReference reference) {
    super(reference);
  }

  @Override
  public RoaringBitmap apply(Table relation) {
    Column column = relation.column(columnReference().getColumnName());
    CategoryColumn textColumn = (CategoryColumn) column;
    return textColumn.isAlpha();
  }
}
