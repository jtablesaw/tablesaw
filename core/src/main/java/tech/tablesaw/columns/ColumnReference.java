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

import tech.tablesaw.api.IntColumn;
import tech.tablesaw.columns.booleans.filters.IsFalse;
import tech.tablesaw.columns.booleans.filters.IsTrue;
import tech.tablesaw.columns.dates.PackedLocalDate;
import tech.tablesaw.columns.number.filters.DoubleEqualTo;
import tech.tablesaw.columns.number.filters.DoubleGreaterThan;
import tech.tablesaw.columns.number.filters.DoubleGreaterThanOrEqualTo;
import tech.tablesaw.columns.number.filters.DoubleLessThan;
import tech.tablesaw.columns.number.filters.DoubleLessThanOrEqualTo;
import tech.tablesaw.filtering.*;
import tech.tablesaw.filtering.columnbased.ColumnEqualTo;
import tech.tablesaw.filtering.columnbased.ColumnGreaterThan;
import tech.tablesaw.filtering.columnbased.ColumnLessThan;
import tech.tablesaw.columns.dates.filters.LocalDateIsAfter;
import tech.tablesaw.columns.dates.filters.LocalDateIsBefore;
import tech.tablesaw.columns.datetimes.filters.*;
import tech.tablesaw.columns.string.filters.*;
import tech.tablesaw.columns.times.filters.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
public class ColumnReference {

    private String columnName;

    public ColumnReference(String column) {
        this.columnName = column;
    }

    public Filter isNotMissing() {
        return new IsNotMissing(this);
    }

    public Filter isMissing() {
        return new IsMissing(this);
    }

    public Filter isEqualTo(int value) {
        return new IntEqualTo(this, value);
    }

    public Filter isEqualTo(ColumnReference reference) {
        return new ColumnEqualTo(this, reference);
    }

    public Filter isGreaterThan(ColumnReference reference) {
        return new ColumnGreaterThan(this, reference);
    }

    public Filter isLessThan(ColumnReference reference) {
        return new ColumnLessThan(this, reference);
    }

    public Filter isBetweenIncluding(int low, int high) {
        return new IntBetweenInclusive(this, low, high);
    }

    public Filter isBetweenIncluding(LocalDate low, LocalDate high) {
        return new LocalDateBetweenInclusive(this, low, high);
    }

    public Filter isBetweenExcluding(int low, int high) {
        return new IntBetweenExclusive(this, low, high);
    }

    public Filter isBetweenExcluding(LocalDate low, LocalDate high) {
        return new LocalDateBetweenExclusive(this, low, high);
    }

    public Filter isEqualTo(float value) {
        return new FloatEqualTo(this, value);
    }

    public Filter isEqualTo(double value) {
        return new DoubleEqualTo(this, value);
    }

    public Filter isEqualTo(LocalTime value) {
        return new TimeEqualTo(this, value);
    }

    public Filter isNotEqualTo(LocalTime value) {
        return new TimeNotEqualTo(this, value);
    }

    public Filter isEqualTo(LocalDate value) {
        return new DateEqualTo(this, value);
    }

    public Filter isEqualTo(String value) {
        return new StringEqualTo(this, value);
    }

    public Filter isNotEqualTo(String value) {
        return new StringNotEqualTo(this, value);
    }

    public Filter isGreaterThan(int value) {
        return new IntGreaterThan(this, value);
    }

    public Filter isIn(IntColumn intColumn) {
        return new IntIsIn(this, intColumn);
    }

    public Filter isIn(String... strings) {
        return new TextIsIn(this, strings);
    }

    public Filter isIn(int... ints) {
        return new IntIsIn(this, ints);
    }

    public Filter isLessThan(int value) {
        return new IntLessThan(this, value);
    }

    public Filter isLessThanOrEqualTo(int value) {
        return new IntLessThanOrEqualTo(this, value);
    }

    public Filter isGreaterThanOrEqualTo(int value) {
        return new IntGreaterThanOrEqualTo(this, value);
    }

    public Filter isGreaterThan(float value) {
        return new FloatGreaterThan(this, value);
    }

    public Filter isGreaterThan(double value) {
        return new DoubleGreaterThan(this, value);
    }

    public Filter isLessThan(float value) {
        return new FloatLessThan(this, value);
    }

    public Filter isLessThan(double value) {
        return new DoubleLessThan(this, value);
    }

    public Filter isLessThanOrEqualTo(float value) {
        return new FloatLessThanOrEqualTo(this, value);
    }

    public Filter isLessThanOrEqualTo(double value) {
        return new DoubleLessThanOrEqualTo(this, value);
    }

    public Filter isGreaterThanOrEqualTo(float value) {
        return new FloatGreaterThanOrEqualTo(this, value);
    }

    public Filter isGreaterThanOrEqualTo(double value) {
        return new DoubleGreaterThanOrEqualTo(this, value);
    }

    public String getColumnName() {
        return columnName;
    }

    public Filter isMidnight() {
        return new IsMidnight(this);
    }

    public Filter isNoon() {
        return new IsNoon(this);
    }

    public Filter isBeforeNoon() {
        return new IsBeforeNoon(this);
    }

    public Filter isAfterNoon() {
        return new IsAfterNoon(this);
    }

    public Filter isBefore(LocalTime value) {
        return new IsBefore(this, value);
    }

    public Filter isBefore(LocalDateTime value) {
        return new DateTimeIsBefore(this, value);
    }

    public Filter isAfter(LocalDateTime value) {
        return new DateTimeIsAfter(this, value);
    }

    public Filter isAfter(LocalTime value) {
        return new IsAfter(this, value);
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

    public Filter isBefore(LocalDate date) {
        return new LocalDateIsBefore(this, PackedLocalDate.pack(date));
    }

    public Filter isAfter(LocalDate date) {
        return new LocalDateIsAfter(this, PackedLocalDate.pack(date));
    }

    public Filter isUpperCase() {
        return new TextIsUpperCase(this);
    }

    public Filter isLowerCase() {
        return new TextIsLowerCase(this);
    }

    public Filter isAlpha() {
        return new TextIsAlpha(this);
    }

    public Filter isAlphaNumeric() {
        return new TextIsAlphaNumeric(this);
    }

    public Filter isNumeric() {
        return new TextIsNumeric(this);
    }

    public Filter isEmpty() {
        return new TextIsEmpty(this);
    }

    public Filter isLongerThan(int length) {
        return new TextIsLongerThan(this, length);
    }

    public Filter isShorterThan(int length) {
        return new TextIsShorterThan(this, length);
    }

    public Filter hasLengthEqualTo(int length) {
        return new TextHasLengthEqualTo(this, length);
    }

    public Filter equalToIgnoringCase(String string) {
        return new TextEqualToIgnoringCase(this, string);
    }

    public Filter startsWith(String string) {
        return new TextStartsWith(this, string);
    }

    public Filter endsWith(String string) {
        return new TextEndsWith(this, string);
    }

    public Filter contains(String string) {
        return new TextContains(this, string);
    }

    public Filter matchesRegex(String string) {
        return new TextMatchesRegex(this, string);
    }

    public Filter isTrue() {
        return new IsTrue(this);
    }

    public Filter isFalse() {
        return new IsFalse(this);
    }
}
