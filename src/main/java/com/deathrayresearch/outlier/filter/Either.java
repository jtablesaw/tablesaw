package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.Relation;
import org.roaringbitmap.RoaringBitmap;

/**
 *
 */
public class Either extends CompositeFilter {

  private final Filter a;
  private final Filter b;

  public Either(Filter a, Filter b) {

    this.a = a;
    this.b = b;
  }

  @Override
  public RoaringBitmap apply(Relation relation) {
    RoaringBitmap rb =  a.apply(relation);
    rb.or(b.apply(relation));
    return rb;
  }
}
