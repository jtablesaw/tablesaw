package tech.tablesaw.columns;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.filtering.IntBiPredicate;
import tech.tablesaw.filtering.IntPredicate;

import java.time.LocalDate;

/**
 *
 */
public interface DateColumnUtils extends Column, Iterable<LocalDate> {

    IntPredicate isMissing = i -> i == DateColumn.MISSING_VALUE;
    IntPredicate isNotMissing = i -> i != DateColumn.MISSING_VALUE;
    IntBiPredicate isGreaterThan = (valueToTest, valueToCompareAgainst) -> valueToTest > valueToCompareAgainst;
    IntBiPredicate isGreaterThanOrEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest >=
            valueToCompareAgainst;
    IntBiPredicate isLessThan = (valueToTest, valueToCompareAgainst) -> valueToTest < valueToCompareAgainst;
    IntBiPredicate isLessThanOrEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest <= valueToCompareAgainst;
    IntBiPredicate isEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest == valueToCompareAgainst;

    IntArrayList data();

}
