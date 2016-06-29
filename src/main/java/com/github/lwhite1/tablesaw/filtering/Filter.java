package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.api.Table;
import org.roaringbitmap.RoaringBitmap;

/**
 * A predicate applied to a Relation, to return a subset of the rows in that table
 */
public abstract class Filter {

  public abstract RoaringBitmap apply(Table relation);
}
