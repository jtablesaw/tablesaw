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

import static tech.tablesaw.columns.numbers.NumberPredicates.isBetweenExclusive;
import static tech.tablesaw.columns.numbers.NumberPredicates.isBetweenInclusive;
import static tech.tablesaw.columns.numbers.NumberPredicates.isEqualTo;
import static tech.tablesaw.columns.numbers.NumberPredicates.isGreaterThan;
import static tech.tablesaw.columns.numbers.NumberPredicates.isGreaterThanOrEqualTo;
import static tech.tablesaw.columns.numbers.NumberPredicates.isLessThan;
import static tech.tablesaw.columns.numbers.NumberPredicates.isLessThanOrEqualTo;
import static tech.tablesaw.columns.numbers.NumberPredicates.isNegative;
import static tech.tablesaw.columns.numbers.NumberPredicates.isNonNegative;
import static tech.tablesaw.columns.numbers.NumberPredicates.isNotEqualTo;
import static tech.tablesaw.columns.numbers.NumberPredicates.isPositive;
import static tech.tablesaw.columns.numbers.NumberPredicates.isZero;

import java.util.function.BiPredicate;
import java.util.function.DoublePredicate;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.filtering.predicates.DoubleBiPredicate;
import tech.tablesaw.filtering.predicates.DoubleRangePredicate;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

public interface NumberFilters {

    Selection eval(DoublePredicate predicate);

    Selection eval(DoubleRangePredicate predicate, Number rangeStart, Number rangeEnd);

    Selection eval(DoubleBiPredicate predicate, DoubleColumn otherColumn);

    Selection eval(DoubleBiPredicate predicate, Number value);

    Selection eval(BiPredicate<Number, Number> predicate, Number value);


    default Selection isEqualTo(double d) {
        return eval(isEqualTo, d);
    }

    default Selection isNotEqualTo(double d) {
        return eval(isNotEqualTo, d);
    }

    default Selection isBetweenExclusive(double start, Number end) {
        return eval(isBetweenExclusive, start, end);
    }

    default Selection isBetweenInclusive(double start, Number end) {
        return eval(isBetweenInclusive, start, end);
    }

    default Selection isGreaterThan(double f) {
        return eval(isGreaterThan, f);
    }

    default Selection isGreaterThanOrEqualTo(double f) {
        return eval(isGreaterThanOrEqualTo, f);
    }

    default Selection isLessThan(double f) {
        return eval(isLessThan, f);
    }

    default Selection isLessThanOrEqualTo(double f) {
        return eval(isLessThanOrEqualTo, f);
    }

    Selection isIn(Number... numbers);

    Selection isIn(double... doubles);

    Selection isNotIn(Number... doubles);

    Selection isNotIn(double... doubles);

    default Selection isZero() {
        return eval(isZero);
    }

    default Selection isPositive() {
        return eval(isPositive);
    }

    default Selection isNegative() {
        return eval(isNegative);
    }

    default Selection isNonNegative() {
        return eval(isNonNegative);
    }

    // TODO(lwhite): see section in Effective Java on double point comparisons.
    default Selection isCloseTo(Number target, Number margin) {
        Selection results = new BitmapBackedSelection();
        for (int i = 0; i < size(); i++) {
            double targetValue = target.doubleValue();
            double marginValue = margin.doubleValue();
            double val = getDouble(i);
            if (val > targetValue - marginValue && val < targetValue + marginValue) {
                results.add(i);
            }
        }
        return results;
    }

    Selection isMissing();

    Selection isNotMissing();

    // Column filters

    default Selection isGreaterThan(NumericColumn<?> d) {
        Selection results = new BitmapBackedSelection();
        for (int i = 0; i < size(); i++) {
            if (this.getDouble(i) > d.getDouble(i)) {
                results.add(i);
            }
        }
        return results;
    }

    default Selection isGreaterThanOrEqualTo(NumericColumn<?> d) {
        Selection results = new BitmapBackedSelection();
        for (int i = 0; i < size(); i++) {
            if (this.getDouble(i) >= d.getDouble(i)) {
                results.add(i);
            }
        }
        return results;
    }

    default Selection isEqualTo(NumericColumn<?> d) {
        Selection results = new BitmapBackedSelection();
        for (int i = 0; i < size(); i++) {
            if (this.getDouble(i) == d.getDouble(i)) {
                results.add(i);
            }
        }
        return results;
    }

    int size();

    double getDouble(int i);

    default Selection isNotEqualTo(NumericColumn<?> d) {
        Selection results = new BitmapBackedSelection();
        for (int i = 0; i < size(); i++) {
            if (this.getDouble(i) != d.getDouble(i)) {
                results.add(i);
            }
        }
        return results;
    }

    default Selection isLessThan(NumericColumn<?> d) {
        Selection results = new BitmapBackedSelection();
        for (int i = 0; i < size(); i++) {
            if (this.getDouble(i) < d.getDouble(i)) {
                results.add(i);
            }
        }
        return results;
    }

    default Selection isLessThanOrEqualTo(NumericColumn<?> d) {
        Selection results = new BitmapBackedSelection();
        for (int i = 0; i < size(); i++) {
            if (this.getDouble(i) <= d.getDouble(i)) {
                results.add(i);
            }
        }
        return results;
    }
}
