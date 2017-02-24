package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.filtering.DoubleBiPredicate;
import com.github.lwhite1.tablesaw.filtering.DoublePredicate;
import it.unimi.dsi.fastutil.ints.IntIterable;

/**
 *
 */
public interface DoubleColumnUtils extends Column, IntIterable {

  DoublePredicate isZero = i -> i == 0.0f;

  DoublePredicate isNegative = i -> i < 0f;

  DoublePredicate isPositive = i -> i > 0f;

  DoublePredicate isNonNegative = i -> i >= 0f;

  DoubleBiPredicate isGreaterThan = (valueToTest, valueToCompareAgainst) -> valueToTest > valueToCompareAgainst;

  DoubleBiPredicate isGreaterThanOrEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest >=
      valueToCompareAgainst;

  DoubleBiPredicate isLessThan = (valueToTest, valueToCompareAgainst) -> valueToTest < valueToCompareAgainst;

  DoubleBiPredicate isLessThanOrEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest <= valueToCompareAgainst;

  DoubleBiPredicate isEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest == valueToCompareAgainst;

  DoublePredicate isMissing = i -> i != i;

  DoublePredicate isNotMissing = i -> i == i;
}
