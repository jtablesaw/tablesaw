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

package tech.tablesaw.columns.times;

import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.columns.times.filters.EqualTo;
import tech.tablesaw.columns.times.filters.IsAfter;
import tech.tablesaw.columns.times.filters.IsAfterNoon;
import tech.tablesaw.columns.times.filters.IsBefore;
import tech.tablesaw.columns.times.filters.IsBeforeNoon;
import tech.tablesaw.columns.times.filters.IsMidnight;
import tech.tablesaw.columns.times.filters.IsNoon;
import tech.tablesaw.columns.times.filters.IsOnOrAfter;
import tech.tablesaw.columns.times.filters.IsOnOrBefore;
import tech.tablesaw.columns.times.filters.NotEqualTo;

import java.time.LocalTime;

/**
 * A reference to a column that can be used in evaluating query predicates. It is a key part of having a fluent API
 * for querying tables.
 * <p>
 * Basically, it lets you write a query like this:
 * <p>
 * table.select(column("foo").isEqualTo("Bar"));
 * <p>
 * In that example, column() is a static method that returns a ColumnReference for a column named "foo".
 * The method isEqualTo(), is implemented on ColumnReference in a way that it can be applied to potentially, multiple
 * column types, although in this case, it only makes sense for CategoryColumns since the argument is a string.
 * <p>
 * When select() isExecuted, it supplies the table to the ColumnReference. The ColumnReference uses the table
 * and columnName to get access to the right column, and then fulfils its role by ensuring that the filtering
 * "isEqualTo("Bar") is applied to all the cells in the column.
 */
public class TimeColumnReference extends ColumnReference {

    public TimeColumnReference(String column) {
        super(column);
    }

    public EqualTo isEqualTo(LocalTime value) {
        return new EqualTo(this, value);
    }

    public NotEqualTo isNotEqualTo(LocalTime value) {
        return new NotEqualTo(this, value);
    }

    public IsBefore isBefore(LocalTime value) {
        return new IsBefore(this, value);
    }

    public IsAfter isAfter(LocalTime value) {
        return new IsAfter(this, value);
    }

    public IsOnOrAfter isOnOrAfter(LocalTime value) {
        return new IsOnOrAfter(this, value);
    }

    public IsOnOrBefore isOnOrBefore(LocalTime value) {
        return new IsOnOrBefore(this, value);
    }

    public IsMidnight isMidnight() {
        return new IsMidnight(this);
    }

    public IsNoon isNoon() {
        return new IsNoon(this);
    }

    public IsBeforeNoon isBeforeNoon() {
        return new IsBeforeNoon(this);
    }

    public IsAfterNoon isAfterNoon() {
        return new IsAfterNoon(this);
    }

}
