package com.deathrayresearch.outlier.columns;

import it.unimi.dsi.fastutil.floats.FloatComparator;

/**
 *
 */
public class FloatTableComparator implements FloatComparator {

  private final int[] idx;

  public FloatTableComparator(int[] idx) {
    this.idx = idx;
  }

  @Override
  public int compare(float v, float v1) {
    return 0;
  }

  @Override
  public int compare(Float o1, Float o2) {
    return compare((float) o1, (float) o2);
  }
}
