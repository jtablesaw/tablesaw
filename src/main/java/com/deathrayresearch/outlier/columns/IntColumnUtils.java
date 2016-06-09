package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.filter.IntPredicate;
import it.unimi.dsi.fastutil.ints.IntIterable;

/**
 *
 */
public interface IntColumnUtils extends Column, IntIterable {

  IntPredicate isZero = i -> i == 0;

  IntPredicate isNegative = i -> i < 0;

  IntPredicate isPositive = i -> i > 0;

  IntPredicate isNonNegative = i -> i >= 0;

  IntPredicate isEven = i -> (i & 1) == 0;

  IntPredicate isOdd = i -> (i & 1) != 0;
}
