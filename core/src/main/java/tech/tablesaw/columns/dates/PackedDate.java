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
        return dateColumn.getPackedDate(index);
    }

    public LocalDate asLocalDate() {
        return PackedLocalDate.asLocalDate(dateColumn.getPackedDate(index));
    }

    public byte getMonthValue() {
        return PackedLocalDate.getMonthValue(dateColumn.getPackedDate(index));
    }

    public String toDateString() {
        return PackedLocalDate.toDateString(dateColumn.getPackedDate(index));
    }

    public String toString() {
        return toDateString();
    }

    public int getDayOfYear() {
        return PackedLocalDate.getDayOfYear(dateColumn.getPackedDate(index));
    }

    public boolean isLeapYear() {
        return PackedLocalDate.isLeapYear(dateColumn.getPackedDate(index));
    }

    public Month getMonth() {
        return PackedLocalDate.getMonth(dateColumn.getPackedDate(index));
    }

    public int lengthOfMonth() {
        return PackedLocalDate.lengthOfMonth(dateColumn.getPackedDate(index));
    }

    public long toEpochDay() {
        return PackedLocalDate.toEpochDay(dateColumn.getPackedDate(index));
    }

    public DayOfWeek getDayOfWeek() {
        return PackedLocalDate.getDayOfWeek(dateColumn.getPackedDate(index));
    }

    public int getQuarter() {
        return PackedLocalDate.getQuarter(dateColumn.getPackedDate(index));
    }

    public boolean isinQ1() {
        return PackedLocalDate.isInQ1(dateColumn.getPackedDate(index));
    }

    public boolean isinQ2() {
        return PackedLocalDate.isInQ2(dateColumn.getPackedDate(index));
    }

    public boolean isInQ3() {
        return PackedLocalDate.isInQ3(dateColumn.getPackedDate(index));
    }

    public boolean isInQ4() {
        return PackedLocalDate.isInQ4(dateColumn.getPackedDate(index));
    }

    public boolean isAfter(LocalDate date) {
        return PackedLocalDate.isAfter(dateColumn.getPackedDate(index), PackedLocalDate.pack(date));
    }

    public boolean isBefore(LocalDate date) {
        return PackedLocalDate.isBefore(dateColumn.getPackedDate(index), PackedLocalDate.pack(date));
    }

    public boolean isOnOrAfter(LocalDate date) {
        return PackedLocalDate.isOnOrAfter(dateColumn.getPackedDate(index), PackedLocalDate.pack(date));
    }

    public boolean isOnOrBefore(LocalDate date) {
        return PackedLocalDate.isOnOrBefore(dateColumn.getPackedDate(index), PackedLocalDate.pack(date));
    }

    public boolean isSunday() {
        return PackedLocalDate.isSunday(dateColumn.getPackedDate(index));
    }

    public boolean isMonday() {
        return PackedLocalDate.isMonday(dateColumn.getPackedDate(index));
    }

    public boolean isTuesday() {
        return PackedLocalDate.isTuesday(dateColumn.getPackedDate(index));
    }

    public boolean isWednesday() {
        return PackedLocalDate.isWednesday(dateColumn.getPackedDate(index));
    }

    public boolean isThursday() {
        return PackedLocalDate.isThursday(dateColumn.getPackedDate(index));
    }

    public boolean isFriday() {
        return PackedLocalDate.isFriday(dateColumn.getPackedDate(index));
    }

    public boolean isSaturday() {
        return PackedLocalDate.isSaturday(dateColumn.getPackedDate(index));
    }

    public boolean isFirstDayOfMonth() {
        return PackedLocalDate.isFirstDayOfMonth(dateColumn.getPackedDate(index));
    }

    public boolean isLastDayOfMonth() {
        return PackedLocalDate.isLastDayOfMonth(dateColumn.getPackedDate(index));
    }

    public boolean isInYear(int year) {
        return PackedLocalDate.isInYear(dateColumn.getPackedDate(index), year);
    }

    public int lengthOfYear() {
        return PackedLocalDate.lengthOfYear(dateColumn.getPackedDate(index));
    }

    public boolean isInJanuary() {
        return PackedLocalDate.isInJanuary(dateColumn.getPackedDate(index));
    }

    public boolean isInFebruary() {
        return PackedLocalDate.isInFebruary(dateColumn.getPackedDate(index));
    }

    public boolean isInMarch() {
        return PackedLocalDate.isInMarch(dateColumn.getPackedDate(index));
    }

    public boolean isInApril() {
        return PackedLocalDate.isInApril(dateColumn.getPackedDate(index));
    }

    public boolean isInMay() {
        return PackedLocalDate.isInMay(dateColumn.getPackedDate(index));
    }

    public boolean isInJune() {
        return PackedLocalDate.isInJune(dateColumn.getPackedDate(index));
    }

    public boolean isInJuly() {
        return PackedLocalDate.isInJuly(dateColumn.getPackedDate(index));
    }

    public boolean isInAugust() {
        return PackedLocalDate.isInAugust(dateColumn.getPackedDate(index));
    }

    public boolean isInSeptember() {
        return PackedLocalDate.isInSeptember(dateColumn.getPackedDate(index));
    }

    public boolean isInOctober() {
        return PackedLocalDate.isInOctober(dateColumn.getPackedDate(index));
    }

    public boolean isInNovember() {
        return PackedLocalDate.isInNovember(dateColumn.getPackedDate(index));
    }

    public boolean isInDecember() {
        return PackedLocalDate.isInDecember(dateColumn.getPackedDate(index));
    }

    public int getYear() {
        return PackedLocalDate.getYear(dateColumn.getPackedDate(index));
    }
}
