package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.filtering.IntBiPredicate;
import com.github.lwhite1.tablesaw.filtering.IntPredicate;
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

  IntPredicate isMissing = i -> i == IntColumn.MISSING_VALUE;

  IntPredicate isNotMissing = i -> i != IntColumn.MISSING_VALUE;
}
