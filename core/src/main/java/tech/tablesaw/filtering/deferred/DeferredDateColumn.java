package tech.tablesaw.filtering.deferred;

import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

import java.time.LocalDate;
import java.util.function.Function;

public class DeferredDateColumn extends DeferredColumn  {

    public DeferredDateColumn(String columnName) {
        super(columnName);
    }

    public Function<Table, Selection> isMonday() {
        return table -> table.dateColumn(getColumnName()).isMonday();
    }

    public Function<Table, Selection> isTuesday() {
        return table -> table.dateColumn(getColumnName()).isTuesday();
    }

    public Function<Table, Selection> isWednesday() {
        return table -> table.dateColumn(getColumnName()).isWednesday();
    }

    public Function<Table, Selection> isThursday() {
        return table -> table.dateColumn(getColumnName()).isThursday();
    }

    public Function<Table, Selection> isFriday() {
        return table -> table.dateColumn(getColumnName()).isFriday();
    }

    public Function<Table, Selection> isSaturday() {
        return table -> table.dateColumn(getColumnName()).isSaturday();
    }
    
    public Function<Table, Selection> isSunday() {
        return table -> table.dateColumn(getColumnName()).isSunday();
    }

    public Function<Table, Selection> isInJanuary() {
        return table -> table.dateColumn(getColumnName()).isInJanuary();
    }

    public Function<Table, Selection> isInFebruary() {
        return table -> table.dateColumn(getColumnName()).isInFebruary();
    }

    public Function<Table, Selection> isInMarch() {
        return table -> table.dateColumn(getColumnName()).isInMarch();
    }

    public Function<Table, Selection> isInApril() {
        return table -> table.dateColumn(getColumnName()).isInApril();
    }

    public Function<Table, Selection> isInMay() {
        return table -> table.dateColumn(getColumnName()).isInMay();
    }

    public Function<Table, Selection> isInJune() {
        return table -> table.dateColumn(getColumnName()).isInJune();
    }

    public Function<Table, Selection> isInJuly() {
        return table -> table.dateColumn(getColumnName()).isInJuly();
    }

    public Function<Table, Selection> isInAugust() {
        return table -> table.dateColumn(getColumnName()).isInAugust();
    }

    public Function<Table, Selection> isInSeptember() {
        return table -> table.dateColumn(getColumnName()).isInSeptember();
    }

    public Function<Table, Selection> isInOctober() {
        return table -> table.dateColumn(getColumnName()).isInOctober();
    }

    public Function<Table, Selection> isInNovember() {
        return table -> table.dateColumn(getColumnName()).isInNovember();
    }

    public Function<Table, Selection> isInDecember() {
        return table -> table.dateColumn(getColumnName()).isInDecember();
    }

    public Function<Table, Selection> isFirstDayOfMonth() {
        return table -> table.dateColumn(getColumnName()).isFirstDayOfMonth();
    }

    public Function<Table, Selection> isLastDayOfMonth() {
        return table -> table.dateColumn(getColumnName()).isLastDayOfMonth();
    }

    public Function<Table, Selection> isInQ1() {
        return table -> table.dateColumn(getColumnName()).isInQ1();
    }

    public Function<Table, Selection> isInQ2() {
        return table -> table.dateColumn(getColumnName()).isInQ2();
    }

    public Function<Table, Selection> isInQ3() {
        return table -> table.dateColumn(getColumnName()).isInJanuary();
    }

    public Function<Table, Selection> isInQ4() {
        return table -> table.dateColumn(getColumnName()).isInQ3();
    }

    public Function<Table, Selection> isInYear(int year) {
        return table -> table.dateColumn(getColumnName()).isInYear(year);
    }

    public Function<Table, Selection> isAfter(LocalDate value) {
        return table -> table.dateColumn(getColumnName()).isAfter(value);
    }

    public Function<Table, Selection> isBetweenExcluding(LocalDate lowValue, LocalDate highValue) {
        return table -> table.dateColumn(getColumnName()).isBetweenExcluding(lowValue, highValue);
    }

    public Function<Table, Selection> isBetweenIncluding(LocalDate lowValue, LocalDate highValue) {
        return table -> table.dateColumn(getColumnName()).isBetweenIncluding(lowValue, highValue);
    }

    public Function<Table, Selection> isBefore(LocalDate value) {
        return table -> table.dateColumn(getColumnName()).isBefore(value);
    }

    public Function<Table, Selection> isOnOrBefore(LocalDate value) {
        return table -> table.dateColumn(getColumnName()).isOnOrBefore(value);
    }

    public Function<Table, Selection> isOnOrAfter(LocalDate value) {
        return table -> table.dateColumn(getColumnName()).isOnOrAfter(value);
    }

    public Function<Table, Selection> isEqualTo(LocalDate value) {
        return table -> table.dateColumn(getColumnName()).isEqualTo(value);
    }

    public Function<Table, Selection> isEqualTo(DateColumn column) {
        return table -> table.dateColumn(getColumnName()).isEqualTo(column);
    }

    public Function<Table, Selection> isNotEqualTo(DateColumn column) {
        return table -> table.dateColumn(getColumnName()).isNotEqualTo(column);
    }

    public Function<Table, Selection> isOnOrBefore(DateColumn column) {
        return table -> table.dateColumn(getColumnName()).isOnOrBefore(column);
    }

    public Function<Table, Selection> isOnOrAfter(DateColumn column) {
        return table -> table.dateColumn(getColumnName()).isOnOrAfter(column);
    }

    public Function<Table, Selection> isAfter(DateColumn column) {
        return table -> table.dateColumn(getColumnName()).isAfter(column);
    }

    public Function<Table, Selection> isBefore(DateColumn column) {
        return table -> table.dateColumn(getColumnName()).isBefore(column);
    }

    public Function<Table, Selection> isMissing() {
        return table -> table.dateColumn(getColumnName()).isMissing();
    }

    public Function<Table, Selection> isNotMissing() {
        return table -> table.dateColumn(getColumnName()).isNotMissing();
    }
}
