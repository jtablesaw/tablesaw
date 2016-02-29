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
 * A filter that selects cells in which all text is lowercase
 */
@Immutable
public class TextIsAlphaNumeric extends ColumnFilter {

  public TextIsAlphaNumeric(ColumnReference reference) {
    super(reference);
  }

  @Override
  public RoaringBitmap apply(Relation relation) {
    Column column = relation.column(columnReference().getColumnName());
    if (column.type() == ColumnType.CAT) {
      CategoryColumn textColumn = (CategoryColumn) column;
      return textColumn.isAlphaNumeric();
    }
    TextColumn textColumn = (TextColumn) column;
    return textColumn.isAlphaNumeric();
  }
}
