/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.columns;

import it.unimi.dsi.fastutil.longs.LongIterable;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.filtering.LongBiPredicate;
import tech.tablesaw.filtering.LongPredicate;

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

    LongBiPredicate isGreaterThan = (valueToTest, valueToCompareAgainst) -> valueToTest > valueToCompareAgainst;

    LongBiPredicate isGreaterThanOrEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest >=
            valueToCompareAgainst;

    LongBiPredicate isLessThan = (valueToTest, valueToCompareAgainst) -> valueToTest < valueToCompareAgainst;

    LongBiPredicate isLessThanOrEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest <= valueToCompareAgainst;

    LongBiPredicate isEqualTo = (long valueToTest, long valueToCompareAgainst) -> valueToTest == valueToCompareAgainst;

    LongBiPredicate isNotEqualTo = (long valueToTest, long valueToCompareAgainst) -> valueToTest != valueToCompareAgainst;

    LongPredicate isMissing = i -> i == LongColumn.MISSING_VALUE;
    LongPredicate isNotMissing = i -> i != LongColumn.MISSING_VALUE;

}
