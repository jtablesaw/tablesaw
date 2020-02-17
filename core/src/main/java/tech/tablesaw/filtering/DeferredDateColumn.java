package tech.tablesaw.filtering;

import com.google.common.annotations.Beta;
import java.time.LocalDate;
import java.util.function.Function;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

@Beta
public class DeferredDateColumn extends DeferredColumn
    implements DateAndDateTimeFilterSpec<Function<Table, Selection>>,
        DateOnlyFilterSpec<Function<Table, Selection>> {

  public DeferredDateColumn(String columnName) {
    super(columnName);
  }

  @Override
  public Function<Table, Selection> isMonday() {
    return table -> table.dateColumn(name()).isMonday();
  }

  @Override
  public Function<Table, Selection> isTuesday() {
    return table -> table.dateColumn(name()).isTuesday();
  }

  @Override
  public Function<Table, Selection> isWednesday() {
    return table -> table.dateColumn(name()).isWednesday();
  }

  @Override
  public Function<Table, Selection> isThursday() {
    return table -> table.dateColumn(name()).isThursday();
  }

  @Override
  public Function<Table, Selection> isFriday() {
    return table -> table.dateColumn(name()).isFriday();
  }

  @Override
  public Function<Table, Selection> isSaturday() {
    return table -> table.dateColumn(name()).isSaturday();
  }

  @Override
  public Function<Table, Selection> isSunday() {
    return table -> table.dateColumn(name()).isSunday();
  }

  @Override
  public Function<Table, Selection> isInJanuary() {
    return table -> table.dateColumn(name()).isInJanuary();
  }

  @Override
  public Function<Table, Selection> isInFebruary() {
    return table -> table.dateColumn(name()).isInFebruary();
  }

  @Override
  public Function<Table, Selection> isInMarch() {
    return table -> table.dateColumn(name()).isInMarch();
  }

  @Override
  public Function<Table, Selection> isInApril() {
    return table -> table.dateColumn(name()).isInApril();
  }

  @Override
  public Function<Table, Selection> isInMay() {
    return table -> table.dateColumn(name()).isInMay();
  }

  @Override
  public Function<Table, Selection> isInJune() {
    return table -> table.dateColumn(name()).isInJune();
  }

  @Override
  public Function<Table, Selection> isInJuly() {
    return table -> table.dateColumn(name()).isInJuly();
  }

  @Override
  public Function<Table, Selection> isInAugust() {
    return table -> table.dateColumn(name()).isInAugust();
  }

  @Override
  public Function<Table, Selection> isInSeptember() {
    return table -> table.dateColumn(name()).isInSeptember();
  }

  @Override
  public Function<Table, Selection> isInOctober() {
    return table -> table.dateColumn(name()).isInOctober();
  }

  @Override
  public Function<Table, Selection> isInNovember() {
    return table -> table.dateColumn(name()).isInNovember();
  }

  @Override
  public Function<Table, Selection> isInDecember() {
    return table -> table.dateColumn(name()).isInDecember();
  }

  @Override
  public Function<Table, Selection> isFirstDayOfMonth() {
    return table -> table.dateColumn(name()).isFirstDayOfMonth();
  }

  @Override
  public Function<Table, Selection> isLastDayOfMonth() {
    return table -> table.dateColumn(name()).isLastDayOfMonth();
  }

  @Override
  public Function<Table, Selection> isInQ1() {
    return table -> table.dateColumn(name()).isInQ1();
  }

  @Override
  public Function<Table, Selection> isInQ2() {
    return table -> table.dateColumn(name()).isInQ2();
  }

  @Override
  public Function<Table, Selection> isInQ3() {
    return table -> table.dateColumn(name()).isInJanuary();
  }

  @Override
  public Function<Table, Selection> isInQ4() {
    return table -> table.dateColumn(name()).isInQ3();
  }

  @Override
  public Function<Table, Selection> isInYear(int year) {
    return table -> table.dateColumn(name()).isInYear(year);
  }

  @Override
  public Function<Table, Selection> isAfter(LocalDate value) {
    return table -> table.dateColumn(name()).isAfter(value);
  }

  @Override
  public Function<Table, Selection> isBetweenExcluding(LocalDate lowValue, LocalDate highValue) {
    return table -> table.dateColumn(name()).isBetweenExcluding(lowValue, highValue);
  }

  @Override
  public Function<Table, Selection> isBetweenIncluding(LocalDate lowValue, LocalDate highValue) {
    return table -> table.dateColumn(name()).isBetweenIncluding(lowValue, highValue);
  }

  @Override
  public Function<Table, Selection> isBefore(LocalDate value) {
    return table -> table.dateColumn(name()).isBefore(value);
  }

  @Override
  public Function<Table, Selection> isOnOrBefore(LocalDate value) {
    return table -> table.dateColumn(name()).isOnOrBefore(value);
  }

  @Override
  public Function<Table, Selection> isOnOrAfter(LocalDate value) {
    return table -> table.dateColumn(name()).isOnOrAfter(value);
  }

  @Override
  public Function<Table, Selection> isEqualTo(LocalDate value) {
    return table -> table.dateColumn(name()).isEqualTo(value);
  }

  @Override
  public Function<Table, Selection> isNotEqualTo(LocalDate value) {
    return table -> table.dateColumn(name()).isEqualTo(value);
  }

  @Override
  public Function<Table, Selection> isEqualTo(DateColumn column) {
    return table -> table.dateColumn(name()).isEqualTo(column);
  }

  @Override
  public Function<Table, Selection> isNotEqualTo(DateColumn column) {
    return table -> table.dateColumn(name()).isNotEqualTo(column);
  }

  @Override
  public Function<Table, Selection> isOnOrBefore(DateColumn column) {
    return table -> table.dateColumn(name()).isOnOrBefore(column);
  }

  @Override
  public Function<Table, Selection> isOnOrAfter(DateColumn column) {
    return table -> table.dateColumn(name()).isOnOrAfter(column);
  }

  @Override
  public Function<Table, Selection> isAfter(DateColumn column) {
    return table -> table.dateColumn(name()).isAfter(column);
  }

  @Override
  public Function<Table, Selection> isBefore(DateColumn column) {
    return table -> table.dateColumn(name()).isBefore(column);
  }
}
