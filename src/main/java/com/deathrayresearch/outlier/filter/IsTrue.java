package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.Relation;
import org.roaringbitmap.RoaringBitmap;

import javax.annotation.concurrent.Immutable;

/**
 * A boolean filter. For symmetry with IsFalse
 */
@Immutable
public class IsTrue extends Filter {

  private final Filter filter;

  private IsTrue(Filter filter) {
    this.filter = filter;
  }

  public static IsTrue isTrue(Filter filter) {
    return new IsTrue(filter);
  }

  /**
   * Returns true if the element in the given row in my {@code column} is true
   */
  @Override
  public RoaringBitmap apply(Relation relation) {
    return filter.apply(relation);
  }
}
