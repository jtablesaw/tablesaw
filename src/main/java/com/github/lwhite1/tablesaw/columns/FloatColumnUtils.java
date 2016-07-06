package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.filtering.FloatBiPredicate;
import com.github.lwhite1.tablesaw.filtering.FloatPredicate;
import it.unimi.dsi.fastutil.ints.IntIterable;

/**
 *
 */
public interface FloatColumnUtils extends Column, IntIterable {

  FloatPredicate isZero = i -> i == 0.0f;

  FloatPredicate isNegative = i -> i < 0f;

  FloatPredicate isPositive = i -> i > 0f;

  FloatPredicate isNonNegative = i -> i >= 0f;

  FloatBiPredicate isGreaterThan = (valueToTest, valueToCompareAgainst) -> valueToTest > valueToCompareAgainst;

  FloatBiPredicate isGreaterThanOrEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest >=
      valueToCompareAgainst;

  FloatBiPredicate isLessThan = (valueToTest, valueToCompareAgainst) -> valueToTest < valueToCompareAgainst;

  FloatBiPredicate isLessThanOrEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest <= valueToCompareAgainst;

  FloatBiPredicate isEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest == valueToCompareAgainst;

  FloatPredicate isMissing = i -> i != i;

  FloatPredicate isNotMissing = i -> i == i;
}
