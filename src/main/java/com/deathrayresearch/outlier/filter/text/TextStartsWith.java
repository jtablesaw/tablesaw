package com.deathrayresearch.outlier.filter.text;


import com.deathrayresearch.outlier.Relation;
import com.deathrayresearch.outlier.columns.ColumnReference;
import com.deathrayresearch.outlier.columns.TextColumn;
import com.deathrayresearch.outlier.filter.ColumnFilter;
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
  public RoaringBitmap apply(Relation relation) {

    TextColumn textColumn = (TextColumn) relation.column(columnReference().getColumnName());
    return textColumn.startsWith(string);
  }
}
