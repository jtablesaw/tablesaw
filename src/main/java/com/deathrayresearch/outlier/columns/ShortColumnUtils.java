package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.filter.ShortPredicate;
import it.unimi.dsi.fastutil.shorts.ShortIterable;

/**
 *
 */
public interface ShortColumnUtils extends Column, ShortIterable {
  
  ShortPredicate isZero = i -> i == 0;

  ShortPredicate isNegative = i -> i < 0;

  ShortPredicate isPositive = i -> i > 0;

  ShortPredicate isNonNegative = i -> i >= 0;

  ShortPredicate isEven = i -> (i & 1) == 0;

  ShortPredicate isOdd = i -> (i & 1) != 0;

}
