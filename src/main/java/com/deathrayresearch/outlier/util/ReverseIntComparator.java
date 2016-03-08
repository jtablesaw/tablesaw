package com.deathrayresearch.outlier.util;

import it.unimi.dsi.fastutil.ints.IntComparator;

/**
 *
 */

public class ReverseIntComparator implements IntComparator {

  public static IntComparator reverse(IntComparator intComparator) {
    return new ReverseIntComparator(intComparator);
  }

  private final IntComparator intComparator;

  private ReverseIntComparator(IntComparator intComparator) {
    this.intComparator = intComparator;
  }

  @Override
  public int compare(int i, int i1) {
    return -intComparator.compare(i, i1);
  }

  @Override
  public int compare(Integer o1, Integer o2) {
    return -intComparator.compare(o1, o2);
  }
}
