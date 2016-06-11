package com.github.lwhite1.tablesaw.filter;

import com.github.lwhite1.tablesaw.Table;
import org.roaringbitmap.RoaringBitmap;

/**
 *  A predicate applied to a Table, to return a subset of the rows in that table
 */
public abstract class Filter {

  public abstract RoaringBitmap apply(Table relation);
}
