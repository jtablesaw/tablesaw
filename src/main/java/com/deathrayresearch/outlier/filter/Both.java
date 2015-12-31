package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.Relation;
import org.roaringbitmap.RoaringBitmap;

/**
 *
 */
public class Both extends CompositeFilter {

  private final Filter a;
  private final Filter b;

  public Both(Filter a, Filter b) {

    this.a = a;
    this.b = b;
  }

  @Override
  public RoaringBitmap apply(Relation relation) {
    return null;
  }
}
