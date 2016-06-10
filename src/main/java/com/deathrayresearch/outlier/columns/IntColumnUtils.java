package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.filter.IntBiPredicate;
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

  IntBiPredicate isGreaterThan = (valueToTest, valueToCompareAgainst) -> valueToTest > valueToCompareAgainst;
  IntBiPredicate isGreaterThanOrEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest >= valueToCompareAgainst;

  IntBiPredicate isLessThan = (valueToTest, valueToCompareAgainst) -> valueToTest < valueToCompareAgainst;
  IntBiPredicate isLessThanOrEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest <= valueToCompareAgainst;

  IntBiPredicate isEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest == valueToCompareAgainst;


}
