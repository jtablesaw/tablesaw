package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.filter.DateEqualTo;
import com.deathrayresearch.outlier.filter.Filter;
import com.deathrayresearch.outlier.filter.FloatEqualTo;
import com.deathrayresearch.outlier.filter.FloatGreaterThan;
import com.deathrayresearch.outlier.filter.FloatGreaterThanOrEqualTo;
import com.deathrayresearch.outlier.filter.FloatLessThan;
import com.deathrayresearch.outlier.filter.FloatLessThanOrEqualTo;
import com.deathrayresearch.outlier.filter.IntBetween;
import com.deathrayresearch.outlier.filter.IntEqualTo;
import com.deathrayresearch.outlier.filter.IntGreaterThan;
import com.deathrayresearch.outlier.filter.IntGreaterThanOrEqualTo;
import com.deathrayresearch.outlier.filter.IntLessThan;
import com.deathrayresearch.outlier.filter.IntLessThanOrEqualTo;
import com.deathrayresearch.outlier.filter.StringEqualTo;
import com.deathrayresearch.outlier.filter.TimeEqualTo;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsAfter;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsBefore;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsFirstDayOfTheMonth;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsFriday;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsInApril;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsInAugust;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsInDecember;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsInFebruary;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsInJanuary;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsInJuly;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsInJune;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsInMarch;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsInMay;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsInNovember;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsInOctober;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsInQ1;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsInQ2;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsInQ3;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsInQ4;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsInSeptember;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsInYear;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsLastDayOfTheMonth;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsMonday;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsSaturday;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsSunday;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsThursday;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsTuesday;
import com.deathrayresearch.outlier.filter.dates.LocalDateIsWednesday;
import com.deathrayresearch.outlier.filter.text.TextContains;
import com.deathrayresearch.outlier.filter.text.TextEndsWith;
import com.deathrayresearch.outlier.filter.text.TextEqualToIgnoringCase;
import com.deathrayresearch.outlier.filter.text.TextHasLengthEqualTo;
import com.deathrayresearch.outlier.filter.text.TextIsAlpha;
import com.deathrayresearch.outlier.filter.text.TextIsAlphaNumeric;
import com.deathrayresearch.outlier.filter.text.TextIsEmpty;
import com.deathrayresearch.outlier.filter.text.TextIsLongerThan;
import com.deathrayresearch.outlier.filter.text.TextIsLowerCase;
import com.deathrayresearch.outlier.filter.text.TextIsNumeric;
import com.deathrayresearch.outlier.filter.text.TextIsShorterThan;
import com.deathrayresearch.outlier.filter.text.TextIsUpperCase;
import com.deathrayresearch.outlier.filter.text.TextMatchesRegex;
import com.deathrayresearch.outlier.filter.text.TextStartsWith;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * A reference to a column, that can be used in evaluating query predicates
 */
public class ColumnReference {

  private String columnName;

  public ColumnReference(String column) {
    this.columnName = column;
  }

  public Filter isEqualTo(int value) {
    return new IntEqualTo(this, value);
  }

  public Filter isBetween(int low, int high) {
    return new IntBetween(this, low, high);
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

  public Filter isGreaterThan(int value) {
    return new IntGreaterThan(this, value);
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
