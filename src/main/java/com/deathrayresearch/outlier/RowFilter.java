package com.deathrayresearch.outlier;

import org.roaringbitmap.IntIterator;
import org.roaringbitmap.RoaringBitmap;

/**
 *
 */
public class RowFilter {

  IntIterator intIterator;

  public RowFilter(RoaringBitmap roaringBitmap) {
    this.intIterator = roaringBitmap.getIntIterator();
  }

  public boolean hasNext() {
    return intIterator.hasNext();
  }

  public int next() {
    return intIterator.next();
  }

}
