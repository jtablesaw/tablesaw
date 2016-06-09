package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.Table;
import org.roaringbitmap.RoaringBitmap;

/**
 *
 */
public abstract class Filter {

  public abstract RoaringBitmap apply(Table relation);

}
