package tech.tablesaw.filtering.deferred;

import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

import java.time.LocalDateTime;
import java.util.function.Function;

public class DeferredDateTimeColumn extends DeferredColumn {

    public DeferredDateTimeColumn(String columnName) {
        super(columnName);
    }

    public Function<Table, Selection> isMonday() {
        return table -> table.dateTimeColumn(getColumnName()).isMonday();
    }

    public Function<Table, Selection> isTuesday() {
        return table -> table.dateTimeColumn(getColumnName()).isTuesday();
    }

    public Function<Table, Selection> isWednesday() {
        return table -> table.dateTimeColumn(getColumnName()).isWednesday();
    }

    public Function<Table, Selection> isThursday() {
        return table -> table.dateTimeColumn(getColumnName()).isThursday();
    }

    public Function<Table, Selection> isFriday() {
        return table -> table.dateTimeColumn(getColumnName()).isFriday();
    }

    public Function<Table, Selection> isSaturday() {
        return table -> table.dateTimeColumn(getColumnName()).isSaturday();
    }

    public Function<Table, Selection> isSunday() {
        return table -> table.dateTimeColumn(getColumnName()).isSunday();
    }

    public Function<Table, Selection> isInJanuary() {
        return table -> table.dateTimeColumn(getColumnName()).isInJanuary();
    }

    public Function<Table, Selection> isInFebruary() {
        return table -> table.dateTimeColumn(getColumnName()).isInFebruary();
    }

    public Function<Table, Selection> isInMarch() {
        return table -> table.dateTimeColumn(getColumnName()).isInMarch();
    }

    public Function<Table, Selection> isInApril() {
        return table -> table.dateTimeColumn(getColumnName()).isInApril();
    }

    public Function<Table, Selection> isInMay() {
        return table -> table.dateTimeColumn(getColumnName()).isInMay();
    }

    public Function<Table, Selection> isInJune() {
        return table -> table.dateTimeColumn(getColumnName()).isInJune();
    }

    public Function<Table, Selection> isInJuly() {
        return table -> table.dateTimeColumn(getColumnName()).isInJuly();
    }

    public Function<Table, Selection> isInAugust() {
        return table -> table.dateTimeColumn(getColumnName()).isInAugust();
    }

    public Function<Table, Selection> isInSeptember() {
        return table -> table.dateTimeColumn(getColumnName()).isInSeptember();
    }

    public Function<Table, Selection> isInOctober() {
        return table -> table.dateTimeColumn(getColumnName()).isInOctober();
    }

    public Function<Table, Selection> isInNovember() {
        return table -> table.dateTimeColumn(getColumnName()).isInNovember();
    }

    public Function<Table, Selection> isInDecember() {
        return table -> table.dateTimeColumn(getColumnName()).isInDecember();
    }

    public Function<Table, Selection> isFirstDayOfMonth() {
        return table -> table.dateTimeColumn(getColumnName()).isFirstDayOfMonth();
    }

    public Function<Table, Selection> isLastDayOfMonth() {
        return table -> table.dateTimeColumn(getColumnName()).isLastDayOfMonth();
    }

    public Function<Table, Selection> isInQ1() {
        return table -> table.dateTimeColumn(getColumnName()).isInQ1();
    }

    public Function<Table, Selection> isInQ2() {
        return table -> table.dateTimeColumn(getColumnName()).isInQ2();
    }

    public Function<Table, Selection> isInQ3() {
        return table -> table.dateTimeColumn(getColumnName()).isInJanuary();
    }

    public Function<Table, Selection> isInQ4() {
        return table -> table.dateTimeColumn(getColumnName()).isInQ3();
    }

    public Function<Table, Selection> isInYear(int year) {
        return table -> table.dateTimeColumn(getColumnName()).isInYear(year);
    }

    public Function<Table, Selection> isBetweenExcluding(LocalDateTime lowValue, LocalDateTime highValue) {
        return table -> table.dateTimeColumn(getColumnName()).isBetweenExcluding(lowValue, highValue);
    }

    public Function<Table, Selection> isBetweenIncluding(LocalDateTime lowValue, LocalDateTime highValue) {
        return table -> table.dateTimeColumn(getColumnName()).isBetweenIncluding(lowValue, highValue);
    }

    public Function<Table, Selection> isBefore(LocalDateTime value) {
        return table -> table.dateTimeColumn(getColumnName()).isBefore(value);
    }

    public Function<Table, Selection> isOnOrBefore(LocalDateTime value) {
        return table -> table.dateTimeColumn(getColumnName()).isOnOrBefore(value);
    }

    public Function<Table, Selection> isOnOrAfter(LocalDateTime value) {
        return table -> table.dateTimeColumn(getColumnName()).isOnOrAfter(value);
    }

    public Function<Table, Selection> isEqualTo(LocalDateTime value) {
        return table -> table.dateTimeColumn(getColumnName()).isEqualTo(value);
    }

    public Function<Table, Selection> isEqualTo(DateTimeColumn column) {
        return table -> table.dateTimeColumn(getColumnName()).isEqualTo(column);
    }

    public Function<Table, Selection> isNotEqualTo(DateTimeColumn column) {
        return table -> table.dateTimeColumn(getColumnName()).isNotEqualTo(column);
    }

    public Function<Table, Selection> isOnOrBefore(DateTimeColumn column) {
        return table -> table.dateTimeColumn(getColumnName()).isOnOrBefore(column);
    }

    public Function<Table, Selection> isOnOrAfter(DateTimeColumn column) {
        return table -> table.dateTimeColumn(getColumnName()).isOnOrAfter(column);
    }

    public Function<Table, Selection> isAfter(DateTimeColumn column) {
        return table -> table.dateTimeColumn(getColumnName()).isAfter(column);
    }

    public Function<Table, Selection> isBefore(DateTimeColumn column) {
        return table -> table.dateTimeColumn(getColumnName()).isBefore(column);
    }

    public Function<Table, Selection> isMissing() {
        return table -> table.dateTimeColumn(getColumnName()).isMissing();
    }

    public Function<Table, Selection> isNotMissing() {
        return table -> table.dateTimeColumn(getColumnName()).isNotMissing();
    }

    public Function<Table, Selection> isMidnight() {
        return table -> table.dateTimeColumn(getColumnName()).isMidnight();
    }

    public Function<Table, Selection> isNoon() {
        return table -> table.dateTimeColumn(getColumnName()).isNoon();
    }

    public Function<Table, Selection> isAfter(LocalDateTime time) {
        return table -> table.dateTimeColumn(getColumnName()).isAfter(time);
    }

    public Function<Table, Selection> isBeforeNoon() {
        return table -> table.dateTimeColumn(getColumnName()).isBeforeNoon();
    }

    public Function<Table, Selection> isAfterNoon() {
        return table -> table.dateTimeColumn(getColumnName()).isAfterNoon();
    }

    public Function<Table, Selection> isNotEqualTo(LocalDateTime value) {
        return table -> table.dateTimeColumn(getColumnName()).isNotEqualTo(value);
    }
}
