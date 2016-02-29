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
public class TextMatchesRegex extends ColumnFilter {

  private String string;

  public TextMatchesRegex(ColumnReference reference, String string) {
    super(reference);
    this.string = string;
  }

  @Override
  public RoaringBitmap apply(Relation relation) {

    TextColumn textColumn = (TextColumn) relation.column(columnReference().getColumnName());
    return textColumn.matchesRegex(string);
  }
}
