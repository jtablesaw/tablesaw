package tech.tablesaw.filtering;

import java.time.LocalDate;
import java.util.function.Function;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

public class DeferredDateColumn extends DeferredColumn {

  public DeferredDateColumn(String columnName) {
    super(columnName);
  }

  public Function<Table, Selection> isMonday() {
    return table -> table.dateColumn(name()).isMonday();
  }

  public Function<Table, Selection> isTuesday() {
    return table -> table.dateColumn(name()).isTuesday();
  }

  public Function<Table, Selection> isWednesday() {
    return table -> table.dateColumn(name()).isWednesday();
  }

  public Function<Table, Selection> isThursday() {
    return table -> table.dateColumn(name()).isThursday();
  }

  public Function<Table, Selection> isFriday() {
    return table -> table.dateColumn(name()).isFriday();
  }

  public Function<Table, Selection> isSaturday() {
    return table -> table.dateColumn(name()).isSaturday();
  }

  public Function<Table, Selection> isSunday() {
    return table -> table.dateColumn(name()).isSunday();
  }

  public Function<Table, Selection> isInJanuary() {
    return table -> table.dateColumn(name()).isInJanuary();
  }

  public Function<Table, Selection> isInFebruary() {
    return table -> table.dateColumn(name()).isInFebruary();
  }

  public Function<Table, Selection> isInMarch() {
    return table -> table.dateColumn(name()).isInMarch();
  }

  public Function<Table, Selection> isInApril() {
    return table -> table.dateColumn(name()).isInApril();
  }

  public Function<Table, Selection> isInMay() {
    return table -> table.dateColumn(name()).isInMay();
  }

  public Function<Table, Selection> isInJune() {
    return table -> table.dateColumn(name()).isInJune();
  }

  public Function<Table, Selection> isInJuly() {
    return table -> table.dateColumn(name()).isInJuly();
  }

  public Function<Table, Selection> isInAugust() {
    return table -> table.dateColumn(name()).isInAugust();
  }

  public Function<Table, Selection> isInSeptember() {
    return table -> table.dateColumn(name()).isInSeptember();
  }

  public Function<Table, Selection> isInOctober() {
    return table -> table.dateColumn(name()).isInOctober();
  }

  public Function<Table, Selection> isInNovember() {
    return table -> table.dateColumn(name()).isInNovember();
  }

  public Function<Table, Selection> isInDecember() {
    return table -> table.dateColumn(name()).isInDecember();
  }

  public Function<Table, Selection> isFirstDayOfMonth() {
    return table -> table.dateColumn(name()).isFirstDayOfMonth();
  }

  public Function<Table, Selection> isLastDayOfMonth() {
    return table -> table.dateColumn(name()).isLastDayOfMonth();
  }

  public Function<Table, Selection> isInQ1() {
    return table -> table.dateColumn(name()).isInQ1();
  }

  public Function<Table, Selection> isInQ2() {
    return table -> table.dateColumn(name()).isInQ2();
  }

  public Function<Table, Selection> isInQ3() {
    return table -> table.dateColumn(name()).isInJanuary();
  }

  public Function<Table, Selection> isInQ4() {
    return table -> table.dateColumn(name()).isInQ3();
  }

  public Function<Table, Selection> isInYear(int year) {
    return table -> table.dateColumn(name()).isInYear(year);
  }

  public Function<Table, Selection> isAfter(LocalDate value) {
    return table -> table.dateColumn(name()).isAfter(value);
  }

  public Function<Table, Selection> isBetweenExcluding(LocalDate lowValue, LocalDate highValue) {
    return table -> table.dateColumn(name()).isBetweenExcluding(lowValue, highValue);
  }

  public Function<Table, Selection> isBetweenIncluding(LocalDate lowValue, LocalDate highValue) {
    return table -> table.dateColumn(name()).isBetweenIncluding(lowValue, highValue);
  }

  public Function<Table, Selection> isBefore(LocalDate value) {
    return table -> table.dateColumn(name()).isBefore(value);
  }

  public Function<Table, Selection> isOnOrBefore(LocalDate value) {
    return table -> table.dateColumn(name()).isOnOrBefore(value);
  }

  public Function<Table, Selection> isOnOrAfter(LocalDate value) {
    return table -> table.dateColumn(name()).isOnOrAfter(value);
  }

  public Function<Table, Selection> isEqualTo(LocalDate value) {
    return table -> table.dateColumn(name()).isEqualTo(value);
  }

  public Function<Table, Selection> isEqualTo(DateColumn column) {
    return table -> table.dateColumn(name()).isEqualTo(column);
  }

  public Function<Table, Selection> isNotEqualTo(DateColumn column) {
    return table -> table.dateColumn(name()).isNotEqualTo(column);
  }

  public Function<Table, Selection> isOnOrBefore(DateColumn column) {
    return table -> table.dateColumn(name()).isOnOrBefore(column);
  }

  public Function<Table, Selection> isOnOrAfter(DateColumn column) {
    return table -> table.dateColumn(name()).isOnOrAfter(column);
  }

  public Function<Table, Selection> isAfter(DateColumn column) {
    return table -> table.dateColumn(name()).isAfter(column);
  }

  public Function<Table, Selection> isBefore(DateColumn column) {
    return table -> table.dateColumn(name()).isBefore(column);
  }

  public Function<Table, Selection> isMissing() {
    return table -> table.dateColumn(name()).isMissing();
  }

  public Function<Table, Selection> isNotMissing() {
    return table -> table.dateColumn(name()).isNotMissing();
  }
}
