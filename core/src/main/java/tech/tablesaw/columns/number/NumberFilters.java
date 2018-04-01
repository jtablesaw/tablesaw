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

package tech.tablesaw.columns.number;

import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.filtering.Filter;
import tech.tablesaw.filtering.predicates.DoubleBiPredicate;
import tech.tablesaw.filtering.predicates.DoublePredicate;
import tech.tablesaw.filtering.predicates.DoubleRangePredicate;
import tech.tablesaw.util.selection.BitmapBackedSelection;
import tech.tablesaw.util.selection.Selection;

import static tech.tablesaw.columns.number.NumberPredicates.*;

public interface NumberFilters extends Column {

    NumberColumn select(Filter filter);

    Selection eval(DoublePredicate predicate);

    Selection eval(DoubleRangePredicate predicate, double rangeStart, double rangeEnd);

    Selection eval(DoubleBiPredicate predicate, NumberColumn otherColumn);

    Selection eval(DoubleBiPredicate predicate, double value);

    default Selection isEqualTo(double d) {
        return eval(isEqualTo, d);
    }

    default Selection isNotEqualTo(double d) {
        return eval(isNotEqualTo, d);
    }

    default Selection isBetweenExclusive(double start, double end) {
        return eval(isBetweenExclusive, start, end);
    }

    default Selection isBetweenInclusive(double start, double end) {
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

    Selection isIn(double... doubles);

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
    default Selection isCloseTo(double target, double margin) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        for (double val : dataInternal()) {
            if (val > target - margin && val < target + margin) {
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

    /**
     * Returns a clone of the internal data structure
     */
    DoubleList dataInternal();
}
