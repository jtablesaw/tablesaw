package tech.tablesaw.filtering;

import com.google.common.annotations.Beta;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

@Beta
public class DeferredDateTimeColumn extends DeferredColumn
    implements DateTimeFilterSpec<Function<Table, Selection>> {

  public DeferredDateTimeColumn(String columnName) {
    super(columnName);
  }

  @Override
  public Function<Table, Selection> isMonday() {
    return table -> table.dateTimeColumn(name()).isMonday();
  }

  @Override
  public Function<Table, Selection> isTuesday() {
    return table -> table.dateTimeColumn(name()).isTuesday();
  }

  @Override
  public Function<Table, Selection> isWednesday() {
    return table -> table.dateTimeColumn(name()).isWednesday();
  }

  @Override
  public Function<Table, Selection> isThursday() {
    return table -> table.dateTimeColumn(name()).isThursday();
  }

  @Override
  public Function<Table, Selection> isFriday() {
    return table -> table.dateTimeColumn(name()).isFriday();
  }

  @Override
  public Function<Table, Selection> isSaturday() {
    return table -> table.dateTimeColumn(name()).isSaturday();
  }

  @Override
  public Function<Table, Selection> isSunday() {
    return table -> table.dateTimeColumn(name()).isSunday();
  }

  @Override
  public Function<Table, Selection> isInJanuary() {
    return table -> table.dateTimeColumn(name()).isInJanuary();
  }

  @Override
  public Function<Table, Selection> isInFebruary() {
    return table -> table.dateTimeColumn(name()).isInFebruary();
  }

  @Override
  public Function<Table, Selection> isInMarch() {
    return table -> table.dateTimeColumn(name()).isInMarch();
  }

  @Override
  public Function<Table, Selection> isInApril() {
    return table -> table.dateTimeColumn(name()).isInApril();
  }

  @Override
  public Function<Table, Selection> isInMay() {
    return table -> table.dateTimeColumn(name()).isInMay();
  }

  @Override
  public Function<Table, Selection> isInJune() {
    return table -> table.dateTimeColumn(name()).isInJune();
  }

  @Override
  public Function<Table, Selection> isInJuly() {
    return table -> table.dateTimeColumn(name()).isInJuly();
  }

  @Override
  public Function<Table, Selection> isInAugust() {
    return table -> table.dateTimeColumn(name()).isInAugust();
  }

  @Override
  public Function<Table, Selection> isInSeptember() {
    return table -> table.dateTimeColumn(name()).isInSeptember();
  }

  @Override
  public Function<Table, Selection> isInOctober() {
    return table -> table.dateTimeColumn(name()).isInOctober();
  }

  @Override
  public Function<Table, Selection> isInNovember() {
    return table -> table.dateTimeColumn(name()).isInNovember();
  }

  @Override
  public Function<Table, Selection> isInDecember() {
    return table -> table.dateTimeColumn(name()).isInDecember();
  }

  @Override
  public Function<Table, Selection> isFirstDayOfMonth() {
    return table -> table.dateTimeColumn(name()).isFirstDayOfMonth();
  }

  @Override
  public Function<Table, Selection> isLastDayOfMonth() {
    return table -> table.dateTimeColumn(name()).isLastDayOfMonth();
  }

  @Override
  public Function<Table, Selection> isInQ1() {
    return table -> table.dateTimeColumn(name()).isInQ1();
  }

  @Override
  public Function<Table, Selection> isInQ2() {
    return table -> table.dateTimeColumn(name()).isInQ2();
  }

  @Override
  public Function<Table, Selection> isInQ3() {
    return table -> table.dateTimeColumn(name()).isInJanuary();
  }

  @Override
  public Function<Table, Selection> isInQ4() {
    return table -> table.dateTimeColumn(name()).isInQ3();
  }

  @Override
  public Function<Table, Selection> isInYear(int year) {
    return table -> table.dateTimeColumn(name()).isInYear(year);
  }

  @Override
  public Function<Table, Selection> isAfter(LocalDate value) {
    return table -> table.dateTimeColumn(name()).isAfter(value);
  }

  @Override
  public Function<Table, Selection> isBefore(LocalDate value) {
    return table -> table.dateTimeColumn(name()).isBefore(value);
  }

  @Override
  public Function<Table, Selection> isOnOrBefore(LocalDate value) {
    return table -> table.dateTimeColumn(name()).isOnOrBefore(value);
  }

  @Override
  public Function<Table, Selection> isOnOrAfter(LocalDate value) {
    return table -> table.dateTimeColumn(name()).isOnOrAfter(value);
  }

  @Override
  public Function<Table, Selection> isBetweenExcluding(
      LocalDateTime lowValue, LocalDateTime highValue) {
    return table -> table.dateTimeColumn(name()).isBetweenExcluding(lowValue, highValue);
  }

  @Override
  public Function<Table, Selection> isBetweenIncluding(
      LocalDateTime lowValue, LocalDateTime highValue) {
    return table -> table.dateTimeColumn(name()).isBetweenIncluding(lowValue, highValue);
  }

  @Override
  public Function<Table, Selection> isBefore(LocalDateTime value) {
    return table -> table.dateTimeColumn(name()).isBefore(value);
  }

  @Override
  public Function<Table, Selection> isOnOrBefore(LocalDateTime value) {
    return table -> table.dateTimeColumn(name()).isOnOrBefore(value);
  }

  @Override
  public Function<Table, Selection> isOnOrAfter(LocalDateTime value) {
    return table -> table.dateTimeColumn(name()).isOnOrAfter(value);
  }

  @Override
  public Function<Table, Selection> isEqualTo(LocalDateTime value) {
    return table -> table.dateTimeColumn(name()).isEqualTo(value);
  }

  @Override
  public Function<Table, Selection> isEqualTo(DateTimeColumn column) {
    return table -> table.dateTimeColumn(name()).isEqualTo(column);
  }

  @Override
  public Function<Table, Selection> isNotEqualTo(DateTimeColumn column) {
    return table -> table.dateTimeColumn(name()).isNotEqualTo(column);
  }

  @Override
  public Function<Table, Selection> isOnOrBefore(DateTimeColumn column) {
    return table -> table.dateTimeColumn(name()).isOnOrBefore(column);
  }

  @Override
  public Function<Table, Selection> isOnOrAfter(DateTimeColumn column) {
    return table -> table.dateTimeColumn(name()).isOnOrAfter(column);
  }

  @Override
  public Function<Table, Selection> isAfter(DateTimeColumn column) {
    return table -> table.dateTimeColumn(name()).isAfter(column);
  }

  @Override
  public Function<Table, Selection> isBefore(DateTimeColumn column) {
    return table -> table.dateTimeColumn(name()).isBefore(column);
  }

  @Override
  public Function<Table, Selection> isMidnight() {
    return table -> table.dateTimeColumn(name()).isMidnight();
  }

  @Override
  public Function<Table, Selection> isNoon() {
    return table -> table.dateTimeColumn(name()).isNoon();
  }

  @Override
  public Function<Table, Selection> isAfter(LocalDateTime time) {
    return table -> table.dateTimeColumn(name()).isAfter(time);
  }

  @Override
  public Function<Table, Selection> isBeforeNoon() {
    return table -> table.dateTimeColumn(name()).isBeforeNoon();
  }

  @Override
  public Function<Table, Selection> isAfterNoon() {
    return table -> table.dateTimeColumn(name()).isAfterNoon();
  }

  @Override
  public Function<Table, Selection> isNotEqualTo(LocalDateTime value) {
    return table -> table.dateTimeColumn(name()).isNotEqualTo(value);
  }
}
