package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.columns.packeddata.PackedLocalDate;
import com.github.lwhite1.tablesaw.filter.DateEqualTo;
import com.github.lwhite1.tablesaw.filter.Filter;
import com.github.lwhite1.tablesaw.filter.FloatEqualTo;
import com.github.lwhite1.tablesaw.filter.FloatGreaterThan;
import com.github.lwhite1.tablesaw.filter.FloatGreaterThanOrEqualTo;
import com.github.lwhite1.tablesaw.filter.FloatLessThan;
import com.github.lwhite1.tablesaw.filter.FloatLessThanOrEqualTo;
import com.github.lwhite1.tablesaw.filter.IntBetween;
import com.github.lwhite1.tablesaw.filter.IntEqualTo;
import com.github.lwhite1.tablesaw.filter.IntGreaterThan;
import com.github.lwhite1.tablesaw.filter.IntGreaterThanOrEqualTo;
import com.github.lwhite1.tablesaw.filter.IntIsIn;
import com.github.lwhite1.tablesaw.filter.IntLessThan;
import com.github.lwhite1.tablesaw.filter.IntLessThanOrEqualTo;
import com.github.lwhite1.tablesaw.filter.IsMissing;
import com.github.lwhite1.tablesaw.filter.IsNotMissing;
import com.github.lwhite1.tablesaw.filter.LocalDateBetween;
import com.github.lwhite1.tablesaw.filter.StringEqualTo;
import com.github.lwhite1.tablesaw.filter.StringNotEqualTo;
import com.github.lwhite1.tablesaw.filter.TimeEqualTo;
import com.github.lwhite1.tablesaw.filter.columnbased.ColumnEqualTo;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsAfter;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsBefore;
import com.github.lwhite1.tablesaw.filter.datetimes.DateTimeIsBefore;
import com.github.lwhite1.tablesaw.filter.datetimes.IsFirstDayOfTheMonth;
import com.github.lwhite1.tablesaw.filter.datetimes.IsFriday;
import com.github.lwhite1.tablesaw.filter.datetimes.IsInApril;
import com.github.lwhite1.tablesaw.filter.datetimes.IsInAugust;
import com.github.lwhite1.tablesaw.filter.datetimes.IsInDecember;
import com.github.lwhite1.tablesaw.filter.datetimes.IsInFebruary;
import com.github.lwhite1.tablesaw.filter.datetimes.IsInJanuary;
import com.github.lwhite1.tablesaw.filter.datetimes.IsInJuly;
import com.github.lwhite1.tablesaw.filter.datetimes.IsInJune;
import com.github.lwhite1.tablesaw.filter.datetimes.IsInMarch;
import com.github.lwhite1.tablesaw.filter.datetimes.IsInMay;
import com.github.lwhite1.tablesaw.filter.datetimes.IsInNovember;
import com.github.lwhite1.tablesaw.filter.datetimes.IsInOctober;
import com.github.lwhite1.tablesaw.filter.datetimes.IsInQ1;
import com.github.lwhite1.tablesaw.filter.datetimes.IsInQ2;
import com.github.lwhite1.tablesaw.filter.datetimes.IsInQ3;
import com.github.lwhite1.tablesaw.filter.datetimes.IsInQ4;
import com.github.lwhite1.tablesaw.filter.datetimes.IsInSeptember;
import com.github.lwhite1.tablesaw.filter.datetimes.IsInYear;
import com.github.lwhite1.tablesaw.filter.datetimes.IsLastDayOfTheMonth;
import com.github.lwhite1.tablesaw.filter.datetimes.IsMonday;
import com.github.lwhite1.tablesaw.filter.datetimes.IsSaturday;
import com.github.lwhite1.tablesaw.filter.datetimes.IsSunday;
import com.github.lwhite1.tablesaw.filter.datetimes.IsThursday;
import com.github.lwhite1.tablesaw.filter.datetimes.IsTuesday;
import com.github.lwhite1.tablesaw.filter.datetimes.IsWednesday;
import com.github.lwhite1.tablesaw.filter.text.TextContains;
import com.github.lwhite1.tablesaw.filter.text.TextEndsWith;
import com.github.lwhite1.tablesaw.filter.text.TextEqualToIgnoringCase;
import com.github.lwhite1.tablesaw.filter.text.TextHasLengthEqualTo;
import com.github.lwhite1.tablesaw.filter.text.TextIsAlpha;
import com.github.lwhite1.tablesaw.filter.text.TextIsAlphaNumeric;
import com.github.lwhite1.tablesaw.filter.text.TextIsEmpty;
import com.github.lwhite1.tablesaw.filter.text.TextIsLongerThan;
import com.github.lwhite1.tablesaw.filter.text.TextIsLowerCase;
import com.github.lwhite1.tablesaw.filter.text.TextIsNumeric;
import com.github.lwhite1.tablesaw.filter.text.TextIsShorterThan;
import com.github.lwhite1.tablesaw.filter.text.TextIsUpperCase;
import com.github.lwhite1.tablesaw.filter.text.TextMatchesRegex;
import com.github.lwhite1.tablesaw.filter.text.TextStartsWith;
import com.github.lwhite1.tablesaw.filter.times.IsAfter;
import com.github.lwhite1.tablesaw.filter.times.IsAfterNoon;
import com.github.lwhite1.tablesaw.filter.times.IsBefore;
import com.github.lwhite1.tablesaw.filter.times.IsBeforeNoon;
import com.github.lwhite1.tablesaw.filter.times.IsMidnight;
import com.github.lwhite1.tablesaw.filter.times.IsNoon;

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
 * and columnName to get access to the right column, and then fulfils its role by ensuring that the filter
 * "isEqualTo("Bar") is applied to all the cells in the column.
 */
public class ColumnReference {

  private String columnName;

  public ColumnReference(String column) {
    this.columnName = column;
  }

  public Filter isNotMissing() {return new IsNotMissing(this);}

  public Filter isMissing() {return new IsMissing(this);}

  public Filter isEqualTo(int value) {
    return new IntEqualTo(this, value);
  }

  public Filter isEqualTo(ColumnReference reference) {
    return new ColumnEqualTo(this, reference);
  }

  public Filter isBetween(int low, int high) {
    return new IntBetween(this, low, high);
  }

  public Filter isBetween(LocalDate low, LocalDate high) {
    return new LocalDateBetween(this, low, high);
  }

  public Filter isEqualTo(float value) {
    return new FloatEqualTo(this, value);
  }

  public Filter isEqualTo(LocalTime value) {
    return new TimeEqualTo(this, value);
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

  public Filter isLessThan(float value) {
    return new FloatLessThan(this, value);
  }

  public Filter isLessThanOrEqualTo(float value) {
    return new FloatLessThanOrEqualTo(this, value);
  }

  public Filter isGreaterThanOrEqualTo(float value) {
    return new FloatGreaterThanOrEqualTo(this, value);
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
}
