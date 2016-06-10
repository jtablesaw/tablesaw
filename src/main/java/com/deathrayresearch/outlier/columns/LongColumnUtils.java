package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.filter.LongPredicate;
import it.unimi.dsi.fastutil.longs.LongIterable;

/**
 * Pre-made predicates for common integer use cases, and other helpful things
 */
public interface LongColumnUtils extends Column, LongIterable {

  LongPredicate isZero = i -> i == 0;

  LongPredicate isNegative = i -> i < 0;

  LongPredicate isPositive = i -> i > 0;

  LongPredicate isNonNegative = i -> i >= 0;

  LongPredicate isEven = i -> (i & 1) == 0;

  LongPredicate isOdd = i -> (i & 1) != 0;

}
