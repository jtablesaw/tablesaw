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

package tech.tablesaw.columns.datetimes;

import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.columns.datetimes.filters.EqualTo;
import tech.tablesaw.columns.datetimes.filters.IsAfter;
import tech.tablesaw.columns.datetimes.filters.IsBefore;
import tech.tablesaw.columns.datetimes.filters.IsFirstDayOfTheMonth;
import tech.tablesaw.columns.datetimes.filters.IsFriday;
import tech.tablesaw.columns.datetimes.filters.IsInApril;
import tech.tablesaw.columns.datetimes.filters.IsInAugust;
import tech.tablesaw.columns.datetimes.filters.IsInDecember;
import tech.tablesaw.columns.datetimes.filters.IsInFebruary;
import tech.tablesaw.columns.datetimes.filters.IsInJanuary;
import tech.tablesaw.columns.datetimes.filters.IsInJuly;
import tech.tablesaw.columns.datetimes.filters.IsInJune;
import tech.tablesaw.columns.datetimes.filters.IsInMarch;
import tech.tablesaw.columns.datetimes.filters.IsInMay;
import tech.tablesaw.columns.datetimes.filters.IsInNovember;
import tech.tablesaw.columns.datetimes.filters.IsInOctober;
import tech.tablesaw.columns.datetimes.filters.IsInQ1;
import tech.tablesaw.columns.datetimes.filters.IsInQ2;
import tech.tablesaw.columns.datetimes.filters.IsInQ3;
import tech.tablesaw.columns.datetimes.filters.IsInQ4;
import tech.tablesaw.columns.datetimes.filters.IsInSeptember;
import tech.tablesaw.columns.datetimes.filters.IsInYear;
import tech.tablesaw.columns.datetimes.filters.IsLastDayOfTheMonth;
import tech.tablesaw.columns.datetimes.filters.IsMonday;
import tech.tablesaw.columns.datetimes.filters.IsOnOrAfter;
import tech.tablesaw.columns.datetimes.filters.IsOnOrBefore;
import tech.tablesaw.columns.datetimes.filters.IsSaturday;
import tech.tablesaw.columns.datetimes.filters.IsSunday;
import tech.tablesaw.columns.datetimes.filters.IsThursday;
import tech.tablesaw.columns.datetimes.filters.IsTuesday;
import tech.tablesaw.columns.datetimes.filters.IsWednesday;
import tech.tablesaw.columns.datetimes.filters.NotEqualTo;
import tech.tablesaw.filtering.Filter;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
public class DateTimeColumnReference extends ColumnReference {

    public DateTimeColumnReference(String column) {
        super(column);
    }

    public Filter isBefore(LocalDateTime value) {
        return new IsBefore(this, value);
    }

    public Filter isBefore(LocalDate value) {
        return new IsBefore(this, value);
    }

    public Filter isEqualTo(LocalDateTime value) {
        return new EqualTo(this, value);
    }

    public Filter isNotEqualTo(LocalDateTime value) {
        return new NotEqualTo(this, value);
    }

    public Filter isOnOrBefore(LocalDateTime value) {
        return new IsOnOrBefore(this, value);
    }

    public Filter isAfter(LocalDateTime value) {
        return new IsAfter(this, value);
    }

    public Filter isAfter(LocalDate value) {
        return new IsAfter(this, value);
    }

    public Filter isOnOrAfter(LocalDateTime value) {
        return new IsOnOrAfter(this, value);
    }

    public Filter isSunday() {
        return new IsSunday(this);
    }

    public Filter isMonday() {
        return new IsMonday(this);
    }

    public Filter isTuesday() {
        return new IsTuesday(this);
    }

    public Filter isWednesday() {
        return new IsWednesday(this);
    }

    public Filter isThursday() {
        return new IsThursday(this);
    }

    public Filter isFriday() {
        return new IsFriday(this);
    }

    public Filter isSaturday() {
        return new IsSaturday(this);
    }

    public Filter isInJanuary() {
        return new IsInJanuary(this);
    }

    public Filter isInFebruary() {
        return new IsInFebruary(this);
    }

    public Filter isInMarch() {
        return new IsInMarch(this);
    }

    public Filter isInApril() {
        return new IsInApril(this);
    }

    public Filter isInMay() {
        return new IsInMay(this);
    }

    public Filter isInJune() {
        return new IsInJune(this);
    }

    public Filter isInJuly() {
        return new IsInJuly(this);
    }

    public Filter isInAugust() {
        return new IsInAugust(this);
    }

    public Filter isInSeptember() {
        return new IsInSeptember(this);
    }

    public Filter isInOctober() {
        return new IsInOctober(this);
    }

    public Filter isInNovember() {
        return new IsInNovember(this);
    }

    public Filter isInDecember() {
        return new IsInDecember(this);
    }

    public Filter isInQ1() {
        return new IsInQ1(this);
    }

    public Filter isInQ2() {
        return new IsInQ2(this);
    }

    public Filter isInQ3() {
        return new IsInQ3(this);
    }

    public Filter isInQ4() {
        return new IsInQ4(this);
    }

    public Filter isFirstDayOfMonth() {
        return new IsFirstDayOfTheMonth(this);
    }

    public Filter isLastDayOfMonth() {
        return new IsLastDayOfTheMonth(this);
    }

    public Filter isInYear(int year) {
        return new IsInYear(this, year);
    }


}