package com.deathrayresearch.outlier.filter.text;


import com.deathrayresearch.outlier.Relation;
import com.deathrayresearch.outlier.columns.ColumnReference;
import com.deathrayresearch.outlier.columns.TextColumn;
import com.deathrayresearch.outlier.filter.ColumnFilter;
import org.roaringbitmap.RoaringBitmap;

import javax.annotation.concurrent.Immutable;

/**
 * A filter that selects cells in which all text is lowercase
 */
@Immutable
public class TextIsEmpty extends ColumnFilter {

  public TextIsEmpty(ColumnReference reference) {
    super(reference);
  }

  @Override
  public RoaringBitmap apply(Relation relation) {

    TextColumn textColumn = (TextColumn) relation.column(columnReference().getColumnName());
    return textColumn.empty();
  }
}
