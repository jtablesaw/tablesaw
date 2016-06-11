package com.github.lwhite1.tablesaw.filter;

import com.github.lwhite1.tablesaw.Table;
import org.roaringbitmap.RoaringBitmap;

/**
 *
 */
public abstract class Filter {

  public abstract RoaringBitmap apply(Table relation);

}
