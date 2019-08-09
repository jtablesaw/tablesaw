package tech.tablesaw.filtering;

import java.time.LocalDateTime;
import java.util.function.Function;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

public class DeferredDateTimeColumn extends DeferredColumn {

  public DeferredDateTimeColumn(String columnName) {
    super(columnName);
  }

  public Function<Table, Selection> isMonday() {
    return table -> table.dateTimeColumn(name()).isMonday();
  }

  public Function<Table, Selection> isTuesday() {
    return table -> table.dateTimeColumn(name()).isTuesday();
  }

  public Function<Table, Selection> isWednesday() {
    return table -> table.dateTimeColumn(name()).isWednesday();
  }

  public Function<Table, Selection> isThursday() {
    return table -> table.dateTimeColumn(name()).isThursday();
  }

  public Function<Table, Selection> isFriday() {
    return table -> table.dateTimeColumn(name()).isFriday();
  }

  public Function<Table, Selection> isSaturday() {
    return table -> table.dateTimeColumn(name()).isSaturday();
  }

  public Function<Table, Selection> isSunday() {
    return table -> table.dateTimeColumn(name()).isSunday();
  }

  public Function<Table, Selection> isInJanuary() {
    return table -> table.dateTimeColumn(name()).isInJanuary();
  }

  public Function<Table, Selection> isInFebruary() {
    return table -> table.dateTimeColumn(name()).isInFebruary();
  }

  public Function<Table, Selection> isInMarch() {
    return table -> table.dateTimeColumn(name()).isInMarch();
  }

  public Function<Table, Selection> isInApril() {
    return table -> table.dateTimeColumn(name()).isInApril();
  }

  public Function<Table, Selection> isInMay() {
    return table -> table.dateTimeColumn(name()).isInMay();
  }

  public Function<Table, Selection> isInJune() {
    return table -> table.dateTimeColumn(name()).isInJune();
  }

  public Function<Table, Selection> isInJuly() {
    return table -> table.dateTimeColumn(name()).isInJuly();
  }

  public Function<Table, Selection> isInAugust() {
    return table -> table.dateTimeColumn(name()).isInAugust();
  }

  public Function<Table, Selection> isInSeptember() {
    return table -> table.dateTimeColumn(name()).isInSeptember();
  }

  public Function<Table, Selection> isInOctober() {
    return table -> table.dateTimeColumn(name()).isInOctober();
  }

  public Function<Table, Selection> isInNovember() {
    return table -> table.dateTimeColumn(name()).isInNovember();
  }

  public Function<Table, Selection> isInDecember() {
    return table -> table.dateTimeColumn(name()).isInDecember();
  }

  public Function<Table, Selection> isFirstDayOfMonth() {
    return table -> table.dateTimeColumn(name()).isFirstDayOfMonth();
  }

  public Function<Table, Selection> isLastDayOfMonth() {
    return table -> table.dateTimeColumn(name()).isLastDayOfMonth();
  }

  public Function<Table, Selection> isInQ1() {
    return table -> table.dateTimeColumn(name()).isInQ1();
  }

  public Function<Table, Selection> isInQ2() {
    return table -> table.dateTimeColumn(name()).isInQ2();
  }

  public Function<Table, Selection> isInQ3() {
    return table -> table.dateTimeColumn(name()).isInJanuary();
  }

  public Function<Table, Selection> isInQ4() {
    return table -> table.dateTimeColumn(name()).isInQ3();
  }

  public Function<Table, Selection> isInYear(int year) {
    return table -> table.dateTimeColumn(name()).isInYear(year);
  }

  public Function<Table, Selection> isBetweenExcluding(
      LocalDateTime lowValue, LocalDateTime highValue) {
    return table -> table.dateTimeColumn(name()).isBetweenExcluding(lowValue, highValue);
  }

  public Function<Table, Selection> isBetweenIncluding(
      LocalDateTime lowValue, LocalDateTime highValue) {
    return table -> table.dateTimeColumn(name()).isBetweenIncluding(lowValue, highValue);
  }

  public Function<Table, Selection> isBefore(LocalDateTime value) {
    return table -> table.dateTimeColumn(name()).isBefore(value);
  }

  public Function<Table, Selection> isOnOrBefore(LocalDateTime value) {
    return table -> table.dateTimeColumn(name()).isOnOrBefore(value);
  }

  public Function<Table, Selection> isOnOrAfter(LocalDateTime value) {
    return table -> table.dateTimeColumn(name()).isOnOrAfter(value);
  }

  public Function<Table, Selection> isEqualTo(LocalDateTime value) {
    return table -> table.dateTimeColumn(name()).isEqualTo(value);
  }

  public Function<Table, Selection> isEqualTo(DateTimeColumn column) {
    return table -> table.dateTimeColumn(name()).isEqualTo(column);
  }

  public Function<Table, Selection> isNotEqualTo(DateTimeColumn column) {
    return table -> table.dateTimeColumn(name()).isNotEqualTo(column);
  }

  public Function<Table, Selection> isOnOrBefore(DateTimeColumn column) {
    return table -> table.dateTimeColumn(name()).isOnOrBefore(column);
  }

  public Function<Table, Selection> isOnOrAfter(DateTimeColumn column) {
    return table -> table.dateTimeColumn(name()).isOnOrAfter(column);
  }

  public Function<Table, Selection> isAfter(DateTimeColumn column) {
    return table -> table.dateTimeColumn(name()).isAfter(column);
  }

  public Function<Table, Selection> isBefore(DateTimeColumn column) {
    return table -> table.dateTimeColumn(name()).isBefore(column);
  }

  public Function<Table, Selection> isMissing() {
    return table -> table.dateTimeColumn(name()).isMissing();
  }

  public Function<Table, Selection> isNotMissing() {
    return table -> table.dateTimeColumn(name()).isNotMissing();
  }

  public Function<Table, Selection> isMidnight() {
    return table -> table.dateTimeColumn(name()).isMidnight();
  }

  public Function<Table, Selection> isNoon() {
    return table -> table.dateTimeColumn(name()).isNoon();
  }

  public Function<Table, Selection> isAfter(LocalDateTime time) {
    return table -> table.dateTimeColumn(name()).isAfter(time);
  }

  public Function<Table, Selection> isBeforeNoon() {
    return table -> table.dateTimeColumn(name()).isBeforeNoon();
  }

  public Function<Table, Selection> isAfterNoon() {
    return table -> table.dateTimeColumn(name()).isAfterNoon();
  }

  public Function<Table, Selection> isNotEqualTo(LocalDateTime value) {
    return table -> table.dateTimeColumn(name()).isNotEqualTo(value);
  }
}
