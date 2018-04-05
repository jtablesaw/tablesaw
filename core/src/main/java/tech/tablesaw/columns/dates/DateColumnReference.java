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

package tech.tablesaw.columns.dates;

import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.columns.dates.filters.BetweenExclusive;
import tech.tablesaw.columns.dates.filters.BetweenInclusive;
import tech.tablesaw.columns.dates.filters.ColumnEqualTo;
import tech.tablesaw.columns.dates.filters.ColumnIsAfter;
import tech.tablesaw.columns.dates.filters.ColumnIsBefore;
import tech.tablesaw.columns.dates.filters.ColumnNotEqualTo;
import tech.tablesaw.columns.dates.filters.EqualTo;
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
import tech.tablesaw.columns.datetimes.filters.IsSaturday;
import tech.tablesaw.columns.datetimes.filters.IsSunday;
import tech.tablesaw.columns.datetimes.filters.IsThursday;
import tech.tablesaw.columns.datetimes.filters.IsTuesday;
import tech.tablesaw.columns.datetimes.filters.IsWednesday;
import tech.tablesaw.filtering.Filter;

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
public class DateColumnReference extends ColumnReference {

    public DateColumnReference(String column) {
        super(column);
    }

    public Filter isEqualTo(DateColumnReference reference) {
        return new ColumnEqualTo(this, reference);
    }

    public Filter isNotEqualTo(DateColumnReference reference) {
        return new ColumnNotEqualTo(this, reference);
    }

    public Filter isAfter(DateColumnReference reference) {
        return new ColumnIsAfter(this, reference);
    }

    public Filter isBefore(DateColumnReference reference) {
        return new ColumnIsBefore(this, reference);
    }

    public Filter isBetweenIncluding(LocalDate low, LocalDate high) {
        return new BetweenInclusive(this, low, high);
    }

    public Filter isBetweenExcluding(LocalDate low, LocalDate high) {
        return new BetweenExclusive(this, low, high);
    }

    public Filter isEqualTo(LocalDate value) {
        return new EqualTo(this, value);
    }

    public Filter isBefore(LocalDateTime value) {
        return new IsBefore(this, value);
    }

    public Filter isAfter(LocalDateTime value) {
        return new IsAfter(this, value);
    }

    public Filter isBefore(LocalDate date) {
        return new tech.tablesaw.columns.dates.filters.IsBefore(this, PackedLocalDate.pack(date));
    }

    public Filter isAfter(LocalDate date) {
        return new tech.tablesaw.columns.dates.filters.IsAfter(this, PackedLocalDate.pack(date));
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
