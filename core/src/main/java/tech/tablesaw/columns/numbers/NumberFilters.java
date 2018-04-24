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

import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.filtering.Filter;
import tech.tablesaw.filtering.predicates.DoubleBiPredicate;
import tech.tablesaw.filtering.predicates.DoubleRangePredicate;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.util.function.BiPredicate;
import java.util.function.DoublePredicate;

import static tech.tablesaw.columns.numbers.NumberPredicates.*;

public interface NumberFilters extends Column {

    NumberColumn where(Filter filter);

    Selection eval(DoublePredicate predicate);

    Selection eval(DoubleRangePredicate predicate, Number rangeStart, Number rangeEnd);

    Selection eval(DoubleBiPredicate predicate, NumberColumn otherColumn);

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
        int i = 0;
        for (double val : dataInternal()) {
            double targetValue = target.doubleValue();
            double marginValue = margin.doubleValue();
            if (val > targetValue - marginValue && val < targetValue + marginValue) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    @Override
    default Selection isMissing() {
        return eval(isMissing);
    }

    @Override
    default Selection isNotMissing() {
        return eval(isNotMissing);
    }

    // Column filters

    default Selection isGreaterThan(NumberColumn d) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        DoubleIterator doubleIterator = d.iterator();
        for (double doubles : dataInternal()) {
            if (doubles > doubleIterator.nextDouble()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    default Selection isGreaterThanOrEqualTo(NumberColumn d) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        DoubleIterator doubleIterator = d.iterator();
        for (double doubles : dataInternal()) {
            if (doubles >= doubleIterator.nextDouble()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    default Selection isEqualTo(NumberColumn d) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        DoubleIterator doubleIterator = d.iterator();
        for (double doubles : dataInternal()) {
            if (doubles == doubleIterator.nextDouble()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    default Selection isNotEqualTo(NumberColumn d) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        DoubleIterator doubleIterator = d.iterator();
        for (double doubles : dataInternal()) {
            if (doubles != doubleIterator.nextDouble()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    default Selection isLessThan(NumberColumn d) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        DoubleIterator doubleIterator = d.iterator();
        for (double doubles : dataInternal()) {
            if (doubles < doubleIterator.nextDouble()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    default Selection isLessThanOrEqualTo(NumberColumn d) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        DoubleIterator doubleIterator = d.iterator();
        for (double doubles : dataInternal()) {
            if (doubles <= doubleIterator.nextDouble()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    /**
     * Returns a clone of the internal data structure
     */
    DoubleList dataInternal();
}
