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

package tech.tablesaw.columns.temporal;

import tech.tablesaw.columns.instant.InstantColumnType;
import tech.tablesaw.filtering.predicates.LongBiPredicate;

import java.util.function.LongPredicate;

public class TemporalPredicates {

    public static final LongPredicate isMissing = i -> i == InstantColumnType.missingValueIndicator();

    public static final LongPredicate isNotMissing = i -> i != InstantColumnType.missingValueIndicator();

    public static final LongBiPredicate isGreaterThan = (valueToTest, valueToCompareAgainst) -> valueToTest > valueToCompareAgainst;

    public static final LongBiPredicate isGreaterThanOrEqualTo = (valueToTest, valueToCompareAgainst) ->
            valueToTest >= valueToCompareAgainst;

    public static final LongBiPredicate isLessThan = (valueToTest, valueToCompareAgainst) -> valueToTest < valueToCompareAgainst;

    public static final LongBiPredicate isLessThanOrEqualTo = (valueToTest, valueToCompareAgainst) -> valueToTest <= valueToCompareAgainst;

    public static final LongBiPredicate isEqualTo = (long valueToTest, long valueToCompareAgainst) -> valueToTest == valueToCompareAgainst;

    public static final LongBiPredicate isNotEqualTo = (long valueToTest, long valueToCompareAgainst) -> valueToTest != valueToCompareAgainst;

}
