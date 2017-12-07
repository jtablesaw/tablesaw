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

import it.unimi.dsi.fastutil.shorts.ShortIterable;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.filtering.ShortBiPredicate;
import tech.tablesaw.filtering.ShortPredicate;

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

    ShortBiPredicate isNotEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest != valueToCompareAgainst;

    ShortPredicate isMissing = i -> i == ShortColumn.MISSING_VALUE;
    ShortPredicate isNotMissing = i -> i != ShortColumn.MISSING_VALUE;
}
