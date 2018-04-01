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

import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.columns.number.filters.BetweenExclusive;
import tech.tablesaw.columns.number.filters.BetweenInclusive;
import tech.tablesaw.columns.number.filters.EqualTo;
import tech.tablesaw.columns.number.filters.GreaterThan;
import tech.tablesaw.columns.number.filters.GreaterThanOrEqualTo;
import tech.tablesaw.columns.number.filters.IsCloseTo;
import tech.tablesaw.columns.number.filters.IsIn;
import tech.tablesaw.columns.number.filters.IsNegative;
import tech.tablesaw.columns.number.filters.IsNonNegative;
import tech.tablesaw.columns.number.filters.IsNotIn;
import tech.tablesaw.columns.number.filters.IsPositive;
import tech.tablesaw.columns.number.filters.IsZero;
import tech.tablesaw.columns.number.filters.LessThan;
import tech.tablesaw.columns.number.filters.LessThanOrEqualTo;
import tech.tablesaw.columns.number.filters.NotEqualTo;
import tech.tablesaw.filtering.Filter;
import tech.tablesaw.filtering.IsMissing;
import tech.tablesaw.filtering.IsNotMissing;

/**
 * A reference to a column that can be used in evaluating query predicates. It is a key part of having a fluent API
 * for querying tables.
 * <p>
 * Basically, it lets you write a query like this:
 * <p>
 * table.selectWhere(column("foo").isEqualTo("Bar"));
 * <p>
 * In that example, column() is a static method that returns a ColumnReference for a column named "foo".
 * The method isEqualTo(), is implemented on ColumnReference in a way that it can be applied to potentially, multiple
 * column types, although in this case, it only makes sense for CategoryColumns since the argument is a string.
 * <p>
 * When selectWhere() isExecuted, it supplies the table to the ColumnReference. The ColumnReference uses the table
 * and columnName to get access to the right column, and then fulfils its role by ensuring that the filtering
 * "isEqualTo("Bar") is applied to all the cells in the column.
 */
public class NumberColumnReference extends ColumnReference {

    public NumberColumnReference(String column) {
        super(column);
    }

    public Filter isEqualTo(double value) {
        return new EqualTo(this, value);
    }

    public Filter isNotEqualTo(double value) {
        return new NotEqualTo(this, value);
    }

    public Filter isBetweenInclusive(double low, double high) {
        return new BetweenInclusive(this, low, high);
    }

    public Filter isBetweenExclusive(int low, int high) {
        return new BetweenExclusive(this, low, high);
    }

    public Filter isGreaterThan(double value) {
        return new GreaterThan(this, value);
    }

    public Filter isGreaterThanOrEqualTo(double value) {
        return new GreaterThanOrEqualTo(this, value);
    }

    public Filter isLessThan(double value) {
        return new LessThan(this, value);
    }

    public Filter isLessThanOrEqualTo(double value) {
        return new LessThanOrEqualTo(this, value);
    }

    public Filter isIn(double... doubles) {
        return new IsIn(this, doubles);
    }

    public Filter isNotIn(double... doubles) {
        return new IsNotIn(this, doubles);
    }

    public Filter isMissing() {
        return new IsMissing(this);
    }

    public Filter isZero() {
        return new IsZero(this);
    }

    public Filter isPositive() {
        return new IsPositive(this);
    }

    public Filter isNegative() {
        return new IsNegative(this);
    }

    public Filter isNonNegative() {
        return new IsNonNegative(this);
    }

    public Filter isCloseTo(double target, double margin) {
        return new IsCloseTo(this, target, margin);
    }

    public Filter isNotMissing() {
        return new IsNotMissing(this);
    }
}
