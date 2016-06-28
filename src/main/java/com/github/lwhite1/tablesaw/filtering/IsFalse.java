package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.api.Table;
import org.roaringbitmap.RoaringBitmap;

import javax.annotation.concurrent.Immutable;

/**
 * A boolean filtering, returns true if the filtering it wraps returns false, and vice-versa.
 */
@Immutable
public class IsFalse extends CompositeFilter {

  private final Filter filter;

  private IsFalse(Filter filter) {
    this.filter = filter;
  }

  public static IsFalse isFalse(Filter filter) {
    return new IsFalse(filter);
  }

  /**
   * Returns true if the element in the given row in my {@code column} is true
   *
   * @param relation
   */
  @Override
  public RoaringBitmap apply(Table relation) {
    RoaringBitmap roaringBitmap = new RoaringBitmap();
    roaringBitmap.add(0, relation.rowCount());
    roaringBitmap.andNot(filter.apply(relation));
    return roaringBitmap;
  }
}
