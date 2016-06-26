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
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsFirstDayOfTheMonth;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsFriday;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsInApril;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsInAugust;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsInDecember;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsInFebruary;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsInJanuary;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsInJuly;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsInJune;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsInMarch;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsInMay;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsInNovember;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsInOctober;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsInQ1;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsInQ2;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsInQ3;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsInQ4;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsInSeptember;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsInYear;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsLastDayOfTheMonth;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsMonday;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsSaturday;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsSunday;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsThursday;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsTuesday;
import com.github.lwhite1.tablesaw.filter.dates.LocalDateIsWednesday;
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
import com.github.lwhite1.tablesaw.filter.times.IsBefore;
import com.github.lwhite1.tablesaw.filter.times.IsMidnight;
import com.github.lwhite1.tablesaw.filter.times.IsNoon;

import java.time.LocalDate;
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
    return new IsNoon(this);
  }

  public Filter isAfterNoon() {
    return new IsNoon(this);
  }

  public Filter isBefore(LocalTime value) {
    return new IsBefore(this, value);
  }

  public Filter isAfter(LocalTime value) {
    return new IsAfter(this, value);
  }

  public Filter isSunday() {
    return new LocalDateIsSunday(this);
  }

  public Filter isMonday() {
    return new LocalDateIsMonday(this);
  }

  public Filter isTuesday() {
    return new LocalDateIsTuesday(this);
  }

  public Filter isWednesday() {
    return new LocalDateIsWednesday(this);
  }

  public Filter isThursday() {
    return new LocalDateIsThursday(this);
  }

  public Filter isFriday() {
    return new LocalDateIsFriday(this);
  }

  public Filter isSaturday() {
    return new LocalDateIsSaturday(this);
  }

  public Filter isInJanuary() {
    return new LocalDateIsInJanuary(this);
  }

  public Filter isInFebruary() {
    return new LocalDateIsInFebruary(this);
  }

  public Filter isInMarch() {
    return new LocalDateIsInMarch(this);
  }

  public Filter isInApril() {
    return new LocalDateIsInApril(this);
  }

  public Filter isInMay() {
    return new LocalDateIsInMay(this);
  }

  public Filter isInJune() {
    return new LocalDateIsInJune(this);
  }

  public Filter isInJuly() {
    return new LocalDateIsInJuly(this);
  }

  public Filter isInAugust() {
    return new LocalDateIsInAugust(this);
  }

  public Filter isInSeptember() {
    return new LocalDateIsInSeptember(this);
  }

  public Filter isInOctober() {
    return new LocalDateIsInOctober(this);
  }

  public Filter isInNovember() {
    return new LocalDateIsInNovember(this);
  }

  public Filter isInDecember() {
    return new LocalDateIsInDecember(this);
  }

  public Filter isInQ1() {
    return new LocalDateIsInQ1(this);
  }

  public Filter isInQ2() {
    return new LocalDateIsInQ2(this);
  }

  public Filter isInQ3() {
    return new LocalDateIsInQ3(this);
  }

  public Filter isInQ4() {
    return new LocalDateIsInQ4(this);
  }

  public Filter isFirstDayOfMonth() {
    return new LocalDateIsFirstDayOfTheMonth(this);
  }

  public Filter isLastDayOfMonth() {
    return new LocalDateIsLastDayOfTheMonth(this);
  }

  public Filter isInYear(int year) {
    return new LocalDateIsInYear(this, year);
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
