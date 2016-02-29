package com.deathrayresearch.outlier.filter.text;


import com.deathrayresearch.outlier.Relation;
import com.deathrayresearch.outlier.columns.CategoryColumn;
import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.ColumnReference;
import com.deathrayresearch.outlier.columns.ColumnType;
import com.deathrayresearch.outlier.columns.TextColumn;
import com.deathrayresearch.outlier.filter.ColumnFilter;
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
  public RoaringBitmap apply(Relation relation) {
    Column column = relation.column(columnReference().getColumnName());
    if (column.type() == ColumnType.CAT) {
      CategoryColumn textColumn = (CategoryColumn) column;
      return textColumn.hasLengthEqualTo(length);
    }
    TextColumn textColumn = (TextColumn) column;
    return textColumn.hasLengthEqualTo(length);
  }
}
