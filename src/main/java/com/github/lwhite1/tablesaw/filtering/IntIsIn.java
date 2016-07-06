package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.util.Selection;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 */
public class IntIsIn extends ColumnFilter {

  private IntColumn filterColumn;

  public IntIsIn(ColumnReference reference, IntColumn filterColumn) {
    super(reference);
    this.filterColumn = filterColumn;
  }

  public Selection apply(Table relation) {
    IntColumn intColumn = (IntColumn) relation.column(columnReference.getColumnName());
    IntSet firstSet = intColumn.asSet();
    firstSet.retainAll(filterColumn.data());
    return intColumn.select(firstSet::contains);
  }
}
