package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.Relation;
import org.roaringbitmap.RoaringBitmap;

import javax.annotation.concurrent.Immutable;

/**
 * A boolean filter, returns true if the filter it wraps returns false, and vice-versa.
 */
@Immutable
public class IsFalse extends Filter {

  private final Filter filter;

  private IsFalse(Filter filter) {
    this.filter = filter;
  }

  public static IsFalse isFalse(Filter filter) {
    return new IsFalse(filter);
  }

  /**
   * Returns true if the element in the given row in my {@code column} is true
   */
  @Override
  public RoaringBitmap apply(Relation relation) {
    RoaringBitmap roaringBitmap = new RoaringBitmap();
    roaringBitmap.add(0, relation.rowCount());
    roaringBitmap.andNot(filter.apply(relation));
    return roaringBitmap;
  }
}
