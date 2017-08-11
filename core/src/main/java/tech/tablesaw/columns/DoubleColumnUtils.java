package tech.tablesaw.columns;

import it.unimi.dsi.fastutil.ints.IntIterable;
import tech.tablesaw.filtering.DoubleBiPredicate;
import tech.tablesaw.filtering.DoublePredicate;

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

    DoubleBiPredicate isLessThanOrEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest <=
            valueToCompareAgainst;

    DoubleBiPredicate isEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest == valueToCompareAgainst;

    DoubleBiPredicate isNotEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest != valueToCompareAgainst;
    
    DoublePredicate isMissing = i -> i != i;

    DoublePredicate isNotMissing = i -> i == i;
}
