package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.filter.ShortBiPredicate;
import com.github.lwhite1.tablesaw.filter.ShortPredicate;
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

  ShortBiPredicate isGreaterThan = new ShortBiPredicate() {
    @Override
    public boolean test(short valueToTest, int valueToCompareAgainst) {
      return valueToTest > valueToCompareAgainst;
    }
  };

  //ShortBiPredicate isGreaterThan = (valueToTest, valueToCompareAgainst) -> valueToTest > valueToCompareAgainst;

  ShortBiPredicate isGreaterThanOrEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest >=
      valueToCompareAgainst;

  ShortBiPredicate isLessThan = (valueToTest, valueToCompareAgainst) -> valueToTest < valueToCompareAgainst;

  ShortBiPredicate isLessThanOrEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest <= valueToCompareAgainst;

  ShortBiPredicate isEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest == valueToCompareAgainst;
}
