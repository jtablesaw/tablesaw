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

package tech.tablesaw.columns.numbers;

import java.util.function.DoublePredicate;

/**
 * Support for built-in predicates on double column
 * <p>
 * TODO(lwhite): Ensure each returns false when handling missing values
 */
public interface NumberPredicates {

    DoublePredicate isZero = i -> i == 0.0;

    DoublePredicate isNegative = i -> i < 0;

    DoublePredicate isPositive = i -> i > 0;

    DoublePredicate isNonNegative = i -> i >= 0;

    static DoublePredicate isGreaterThan(double valueToCompareAgainst) {
      return valueToTest -> valueToTest > valueToCompareAgainst;
    }

    static DoublePredicate isBetweenExclusive(double rangeStart, double rangeEnd) {
      return valueToTest -> valueToTest > rangeStart && valueToTest < rangeEnd;
    };

    static DoublePredicate isBetweenInclusive(double rangeStart, double rangeEnd) {
      return valueToTest -> valueToTest >= rangeStart && valueToTest <= rangeEnd;
    };

    static DoublePredicate isGreaterThanOrEqualTo(double valueToCompareAgainst) {
      return valueToTest -> valueToTest >= valueToCompareAgainst;  
    }

    static DoublePredicate isLessThan(double valueToCompareAgainst) {
      return valueToTest -> valueToTest < valueToCompareAgainst;
    }

    static DoublePredicate isLessThanOrEqualTo(double valueToCompareAgainst) {
       return valueToTest -> valueToTest <= valueToCompareAgainst;
    }

    static DoublePredicate isEqualTo(double valueToCompareAgainst) {
      return valueToTest -> valueToTest == valueToCompareAgainst;
    }

    static DoublePredicate isNotEqualTo(double valueToCompareAgainst) {
      return valueToTest -> valueToTest != valueToCompareAgainst;
    }

    DoublePredicate isMissing = i -> i != i;

    DoublePredicate isNotMissing = i -> i == i;
}
