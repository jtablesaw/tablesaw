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
import tech.tablesaw.filtering.composite.AnyOf;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
public class DateTimeColumnReference extends ColumnReference {

    public DateTimeColumnReference(String column) {
        super(column);
    }

    public IsBefore isBefore(LocalDateTime value) {
        return new IsBefore(this, value);
    }

    public IsBefore isBefore(LocalDate value) {
        return new IsBefore(this, value);
    }

    public Filter isBetweenExcluding(LocalDateTime lowValue, LocalDateTime highValue) {
        return AnyOf.anyOf(isAfter(lowValue), isBefore(highValue));
    }

    public Filter isBetweenIncluding(LocalDateTime lowValue, LocalDateTime highValue) {
        return AnyOf.anyOf(isOnOrAfter(lowValue), isOnOrBefore(highValue));
    }

    public EqualTo isEqualTo(LocalDateTime value) {
        return new EqualTo(this, value);
    }

    public NotEqualTo isNotEqualTo(LocalDateTime value) {
        return new NotEqualTo(this, value);
    }

    public IsOnOrBefore isOnOrBefore(LocalDateTime value) {
        return new IsOnOrBefore(this, value);
    }

    public IsAfter isAfter(LocalDateTime value) {
        return new IsAfter(this, value);
    }

    public IsAfter isAfter(LocalDate value) {
        return new IsAfter(this, value);
    }

    public IsOnOrAfter isOnOrAfter(LocalDateTime value) {
        return new IsOnOrAfter(this, value);
    }

    public IsSunday isSunday() {
        return new IsSunday(this);
    }

    public IsMonday isMonday() {
        return new IsMonday(this);
    }

    public IsTuesday isTuesday() {
        return new IsTuesday(this);
    }

    public IsWednesday isWednesday() {
        return new IsWednesday(this);
    }

    public IsThursday isThursday() {
        return new IsThursday(this);
    }

    public IsFriday isFriday() {
        return new IsFriday(this);
    }

    public IsSaturday isSaturday() {
        return new IsSaturday(this);
    }

    public IsInJanuary isInJanuary() {
        return new IsInJanuary(this);
    }

    public IsInFebruary isInFebruary() {
        return new IsInFebruary(this);
    }

    public IsInMarch isInMarch() {
        return new IsInMarch(this);
    }

    public IsInApril isInApril() {
        return new IsInApril(this);
    }

    public IsInMay isInMay() {
        return new IsInMay(this);
    }

    public IsInJune isInJune() {
        return new IsInJune(this);
    }

    public IsInJuly isInJuly() {
        return new IsInJuly(this);
    }

    public IsInAugust isInAugust() {
        return new IsInAugust(this);
    }

    public IsInSeptember isInSeptember() {
        return new IsInSeptember(this);
    }

    public IsInOctober isInOctober() {
        return new IsInOctober(this);
    }

    public IsInNovember isInNovember() {
        return new IsInNovember(this);
    }

    public IsInDecember isInDecember() {
        return new IsInDecember(this);
    }

    public IsInQ1 isInQ1() {
        return new IsInQ1(this);
    }

    public IsInQ2 isInQ2() {
        return new IsInQ2(this);
    }

    public IsInQ3 isInQ3() {
        return new IsInQ3(this);
    }

    public IsInQ4 isInQ4() {
        return new IsInQ4(this);
    }

    public IsFirstDayOfTheMonth isFirstDayOfMonth() {
        return new IsFirstDayOfTheMonth(this);
    }

    public IsLastDayOfTheMonth isLastDayOfMonth() {
        return new IsLastDayOfTheMonth(this);
    }

    public IsInYear isInYear(int year) {
        return new IsInYear(this, year);
    }
}