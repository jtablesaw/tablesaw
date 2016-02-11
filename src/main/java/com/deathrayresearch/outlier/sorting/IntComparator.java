package com.deathrayresearch.outlier.sorting;

/**
 *
 */
public class IntComparator {

  private static IntComparator instance = new IntComparator();

  public static IntComparator getInstance() {
    return instance;
  }

  private IntComparator() {}

  public int compare(int a, int b) {
    if (a > b) {
      return 1;
    } else if (b > a) {
      return -1;
    }
    return 0;
  }
}
