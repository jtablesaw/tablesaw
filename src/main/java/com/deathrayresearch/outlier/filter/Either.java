package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.Table;
import org.roaringbitmap.RoaringBitmap;

/**
 *
 */
public class Either extends Filter {

  private final Filter a;
  private final Filter b;

  public Either(Filter a, Filter b) {

    this.a = a;
    this.b = b;
  }

  @Override
  public RoaringBitmap apply(Table relation) {
    RoaringBitmap rb =  a.apply(relation);
    rb.or(b.apply(relation));
    return rb;
  }
}
