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

import java.util.function.IntPredicate;
import tech.tablesaw.columns.times.TimeColumnType;
import tech.tablesaw.filtering.predicates.IntBiPredicate;

/**
 * Predicates for test DateColumn values and/or TimeColumn values
 *
 * <p>NOTE: These are not for testing DateTimeColumnValues, which are in the class
 * DateTimePredicates
 */
public class DateAndTimePredicates {

  private DateAndTimePredicates() {}

  public static final IntPredicate isMissing = i -> i == TimeColumnType.missingValueIndicator();

  public static final IntPredicate isNotMissing = i -> i != TimeColumnType.missingValueIndicator();

  public static final IntBiPredicate isGreaterThan =
      (valueToTest, valueToCompareAgainst) -> valueToTest > valueToCompareAgainst;

  public static final IntBiPredicate isGreaterThanOrEqualTo =
      (valueToTest, valueToCompareAgainst) -> valueToTest >= valueToCompareAgainst;

  public static final IntBiPredicate isLessThan =
      (valueToTest, valueToCompareAgainst) -> valueToTest < valueToCompareAgainst;

  public static final IntBiPredicate isLessThanOrEqualTo =
      (valueToTest, valueToCompareAgainst) -> valueToTest <= valueToCompareAgainst;

  public static final IntBiPredicate isEqualTo =
      (valueToTest, valueToCompareAgainst) -> valueToTest == valueToCompareAgainst;

  public static final IntBiPredicate isNotEqualTo =
      (valueToTest, valueToCompareAgainst) -> valueToTest != valueToCompareAgainst;
}
