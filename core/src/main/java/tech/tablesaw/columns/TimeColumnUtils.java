package tech.tablesaw.columns;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.filtering.IntBiPredicate;
import tech.tablesaw.filtering.IntPredicate;

import java.time.LocalTime;

/**
 *
 */
public interface TimeColumnUtils extends Column, Iterable<LocalTime> {

    IntPredicate isMissing = i -> i == TimeColumn.MISSING_VALUE;
    IntPredicate isNotMissing = i -> i != TimeColumn.MISSING_VALUE;
    IntBiPredicate isGreaterThan = (valueToTest, valueToCompareAgainst) -> valueToTest > valueToCompareAgainst;
    IntBiPredicate isGreaterThanOrEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest >=
            valueToCompareAgainst;
    IntBiPredicate isLessThan = (valueToTest, valueToCompareAgainst) -> valueToTest < valueToCompareAgainst;
    IntBiPredicate isLessThanOrEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest <= valueToCompareAgainst;
    IntBiPredicate isEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest == valueToCompareAgainst;

    IntArrayList data();
}
