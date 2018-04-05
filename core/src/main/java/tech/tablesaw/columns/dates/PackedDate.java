package tech.tablesaw.columns.dates;

import tech.tablesaw.api.DateColumn;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;

public class PackedDate {

    private int index = 0;
    private final DateColumn dateColumn;

    public PackedDate(DateColumn column) {
        this.dateColumn = column;
    }

    public PackedDate next() {
        index++;
        return this;
    }

    public PackedDate get(int rowNumber) {
        index = rowNumber;
        return this;
    }

    public int getPackedValue() {
        return dateColumn.getIntInternal(index);
    }

    public LocalDate asLocalDate() {
        return PackedLocalDate.asLocalDate(dateColumn.getIntInternal(index));
    }

    public byte getMonthValue() {
        return PackedLocalDate.getMonthValue(dateColumn.getIntInternal(index));
    }

    public String toDateString() {
        return PackedLocalDate.toDateString(dateColumn.getIntInternal(index));
    }

    public String toString() {
        return toDateString();
    }

    public int getDayOfYear() {
        return PackedLocalDate.getDayOfYear(dateColumn.getIntInternal(index));
    }

    public boolean isLeapYear() {
        return PackedLocalDate.isLeapYear(dateColumn.getIntInternal(index));
    }

    public Month getMonth() {
        return PackedLocalDate.getMonth(dateColumn.getIntInternal(index));
    }

    public int lengthOfMonth() {
        return PackedLocalDate.lengthOfMonth(dateColumn.getIntInternal(index));
    }

    public long toEpochDay() {
        return PackedLocalDate.toEpochDay(dateColumn.getIntInternal(index));
    }

    public DayOfWeek getDayOfWeek() {
        return PackedLocalDate.getDayOfWeek(dateColumn.getIntInternal(index));
    }

    public int getQuarter() {
        return PackedLocalDate.getQuarter(dateColumn.getIntInternal(index));
    }

    public boolean isinQ1() {
        return PackedLocalDate.isInQ1(dateColumn.getIntInternal(index));
    }

    public boolean isinQ2() {
        return PackedLocalDate.isInQ2(dateColumn.getIntInternal(index));
    }

    public boolean isInQ3() {
        return PackedLocalDate.isInQ3(dateColumn.getIntInternal(index));
    }

    public boolean isInQ4() {
        return PackedLocalDate.isInQ4(dateColumn.getIntInternal(index));
    }

    public boolean isAfter(LocalDate date) {
        return PackedLocalDate.isAfter(dateColumn.getIntInternal(index), PackedLocalDate.pack(date));
    }

    public boolean isBefore(LocalDate date) {
        return PackedLocalDate.isBefore(dateColumn.getIntInternal(index), PackedLocalDate.pack(date));
    }

    public boolean isOnOrAfter(LocalDate date) {
        return PackedLocalDate.isOnOrAfter(dateColumn.getIntInternal(index), PackedLocalDate.pack(date));
    }

    public boolean isOnOrBefore(LocalDate date) {
        return PackedLocalDate.isOnOrBefore(dateColumn.getIntInternal(index), PackedLocalDate.pack(date));
    }

    public boolean isSunday() {
        return PackedLocalDate.isSunday(dateColumn.getIntInternal(index));
    }

    public boolean isMonday() {
        return PackedLocalDate.isMonday(dateColumn.getIntInternal(index));
    }

    public boolean isTuesday() {
        return PackedLocalDate.isTuesday(dateColumn.getIntInternal(index));
    }

    public boolean isWednesday() {
        return PackedLocalDate.isWednesday(dateColumn.getIntInternal(index));
    }

    public boolean isThursday() {
        return PackedLocalDate.isThursday(dateColumn.getIntInternal(index));
    }

    public boolean isFriday() {
        return PackedLocalDate.isFriday(dateColumn.getIntInternal(index));
    }

    public boolean isSaturday() {
        return PackedLocalDate.isSaturday(dateColumn.getIntInternal(index));
    }

    public boolean isFirstDayOfMonth() {
        return PackedLocalDate.isFirstDayOfMonth(dateColumn.getIntInternal(index));
    }

    public boolean isLastDayOfMonth() {
        return PackedLocalDate.isLastDayOfMonth(dateColumn.getIntInternal(index));
    }

    public boolean isInYear(int year) {
        return PackedLocalDate.isInYear(dateColumn.getIntInternal(index), year);
    }

    public int lengthOfYear() {
        return PackedLocalDate.lengthOfYear(dateColumn.getIntInternal(index));
    }

    public boolean isInJanuary() {
        return PackedLocalDate.isInJanuary(dateColumn.getIntInternal(index));
    }

    public boolean isInFebruary() {
        return PackedLocalDate.isInFebruary(dateColumn.getIntInternal(index));
    }

    public boolean isInMarch() {
        return PackedLocalDate.isInMarch(dateColumn.getIntInternal(index));
    }

    public boolean isInApril() {
        return PackedLocalDate.isInApril(dateColumn.getIntInternal(index));
    }

    public boolean isInMay() {
        return PackedLocalDate.isInMay(dateColumn.getIntInternal(index));
    }

    public boolean isInJune() {
        return PackedLocalDate.isInJune(dateColumn.getIntInternal(index));
    }

    public boolean isInJuly() {
        return PackedLocalDate.isInJuly(dateColumn.getIntInternal(index));
    }

    public boolean isInAugust() {
        return PackedLocalDate.isInAugust(dateColumn.getIntInternal(index));
    }

    public boolean isInSeptember() {
        return PackedLocalDate.isInSeptember(dateColumn.getIntInternal(index));
    }

    public boolean isInOctober() {
        return PackedLocalDate.isInOctober(dateColumn.getIntInternal(index));
    }

    public boolean isInNovember() {
        return PackedLocalDate.isInNovember(dateColumn.getIntInternal(index));
    }

    public boolean isInDecember() {
        return PackedLocalDate.isInDecember(dateColumn.getIntInternal(index));
    }

    public int getYear() {
        return PackedLocalDate.getYear(dateColumn.getIntInternal(index));
    }
}
