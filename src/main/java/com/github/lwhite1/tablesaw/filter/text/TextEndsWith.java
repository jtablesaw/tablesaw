package com.github.lwhite1.tablesaw.filter.text;

import com.github.lwhite1.tablesaw.Table;
import com.github.lwhite1.tablesaw.columns.CategoryColumn;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.filter.ColumnFilter;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import org.roaringbitmap.RoaringBitmap;

import javax.annotation.concurrent.Immutable;

/**
 * A filter that selects cells in which all text is uppercase
 */
@Immutable
public class TextEndsWith extends ColumnFilter {

  private String string;

  public TextEndsWith(ColumnReference reference, String string) {
    super(reference);
    this.string = string;
  }

  @Override
  public RoaringBitmap apply(Table relation) {
    Column column = relation.column(columnReference().getColumnName());
    CategoryColumn textColumn = (CategoryColumn) column;
    return textColumn.endsWith(string);
  }
}
