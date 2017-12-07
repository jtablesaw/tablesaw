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

import it.unimi.dsi.fastutil.ints.IntIterable;
import tech.tablesaw.filtering.DoubleBiPredicate;
import tech.tablesaw.filtering.DoublePredicate;

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
