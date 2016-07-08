package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.util.Selection;

/**
 * A predicate applied to a Relation, to return a subset of the rows in that table
 */
public abstract class Filter {

  public abstract Selection apply(Table relation);
}
