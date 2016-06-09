package com.deathrayresearch.outlier.filter.text;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.columns.CategoryColumn;
import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.ColumnReference;
import com.deathrayresearch.outlier.filter.ColumnFilter;
import org.roaringbitmap.RoaringBitmap;

import javax.annotation.concurrent.Immutable;

/**
 * A filter that selects cells in which all text is uppercase
 */
@Immutable
public class TextMatchesRegex extends ColumnFilter {

  private String string;

  public TextMatchesRegex(ColumnReference reference, String string) {
    super(reference);
    this.string = string;
  }

  @Override
  public RoaringBitmap apply(Table relation) {

    Column column = relation.column(columnReference().getColumnName());
    CategoryColumn textColumn = (CategoryColumn) column;
    return textColumn.matchesRegex(string);
  }
}
