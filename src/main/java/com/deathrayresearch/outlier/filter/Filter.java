package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.Relation;
import org.roaringbitmap.RoaringBitmap;

/**
 *
 */
public abstract class Filter {

  public abstract RoaringBitmap apply(Relation relation);

}
