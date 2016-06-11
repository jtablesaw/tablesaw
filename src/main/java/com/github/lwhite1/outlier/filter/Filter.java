package com.github.lwhite1.outlier.filter;

import com.github.lwhite1.outlier.Table;
import org.roaringbitmap.RoaringBitmap;

/**
 *
 */
public abstract class Filter {

  public abstract RoaringBitmap apply(Table relation);

}
