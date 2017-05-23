package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.api.TimeColumn;
import com.github.lwhite1.tablesaw.filtering.IntBiPredicate;
import com.github.lwhite1.tablesaw.filtering.IntPredicate;
import it.unimi.dsi.fastutil.ints.IntArrayList;

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
