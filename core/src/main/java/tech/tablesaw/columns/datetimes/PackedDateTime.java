package tech.tablesaw.columns.datetimes;

import tech.tablesaw.api.DateTimeColumn;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class PackedDateTime {

    private int index = 0;
    private final DateTimeColumn dateTimeColumn;

    public PackedDateTime(DateTimeColumn column) {
        this.dateTimeColumn = column;
    }

    PackedDateTime next() {
        index++;
        return this;
    }

    public PackedDateTime get(int rowNumber) {
        index = rowNumber;
        return this;
    }

    public long getPackedValue() {
        return value();
    }

    public LocalDateTime asLocalDateTime() {
        return PackedLocalDateTime.asLocalDateTime(value());
    }

    public byte getDayOfMonth() {
        return PackedLocalDateTime.getDayOfMonth(value());
    }

    public short getYear() {
        return PackedLocalDateTime.getYear(value());
    }

    public byte getMonthValue() {
        return PackedLocalDateTime.getMonthValue(value());
    }

    public int date() {
        return PackedLocalDateTime.date(value());
    }

    public int time() {
        return PackedLocalDateTime.time(value());
    }

    public String toString() {
        return PackedLocalDateTime.toString(value());
    }

    public int getDayOfYear() {
        return PackedLocalDateTime.getDayOfYear(value());
    }

    public int getWeekOfYear() {
        return PackedLocalDateTime.getWeekOfYear(value());
    }

    public boolean isLeapYear() {
        return PackedLocalDateTime.isLeapYear(value());
    }

    public Month getMonth() {
        return PackedLocalDateTime.getMonth(value());
    }

    public int lengthOfMonth() {
        return PackedLocalDateTime.lengthOfMonth(value());
    }

    public DayOfWeek getDayOfWeek() {
        return PackedLocalDateTime.getDayOfWeek(value());
    }

    /**
     * Returns the quarter of the year of the given date as an int from 1 to 4, or -1, if the argument is the
     * MISSING_VALUE for DateTimeColumn
     */
    public int getQuarter() {
        return PackedLocalDateTime.getQuarter(value());
    }

    public boolean isInQ1() {
        return PackedLocalDateTime.isInQ1(value());
    }

    public boolean isInQ2() {
        return PackedLocalDateTime.isInQ2(value());
    }

    public boolean isInQ3() {
        return PackedLocalDateTime.isInQ3(value());
    }

    public boolean isInQ4() {
        return PackedLocalDateTime.isInQ4(value());
    }

    public boolean isAfter(long valueToTestAgainst) {
        return PackedLocalDateTime.isAfter(value(), valueToTestAgainst);
    }

    public boolean isAfter(LocalDateTime valueToTestAgainst) {
        return isAfter(PackedLocalDateTime.pack(valueToTestAgainst));
    }

    public boolean isBefore(long valueToTestAgainst) {
        return PackedLocalDateTime.isBefore(value(), valueToTestAgainst);
    }

    public boolean isBefore(LocalDateTime valueToTestAgainst) {
        return isBefore(PackedLocalDateTime.pack(valueToTestAgainst));
    }

    public boolean isSunday() {
        return PackedLocalDateTime.isSunday(value());
    }

    public boolean isMonday() {
        return PackedLocalDateTime.isMonday(value());
    }

    public boolean isTuesday() {
        return PackedLocalDateTime.isTuesday(value());
    }

    public boolean isWednesday() {
        return PackedLocalDateTime.isWednesday(value());
    }

    public boolean isThursday() {
        return PackedLocalDateTime.isThursday(value());
    }

    public boolean isFriday() {
        return PackedLocalDateTime.isFriday(value());
    }

    public boolean isSaturday() {
        return PackedLocalDateTime.isSaturday(value());
    }

    public boolean isFirstDayOfMonth() {
        return PackedLocalDateTime.isFirstDayOfMonth(value());
    }

    public boolean isInJanuary() {
        return PackedLocalDateTime.isInJanuary(value());
    }

    public boolean isInFebruary() {
        return PackedLocalDateTime.isInFebruary(value());
    }

    public boolean isInMarch() {
        return PackedLocalDateTime.isInMarch(value());
    }

    public boolean isInApril() {
        return PackedLocalDateTime.isInApril(value());
    }

    public boolean isInMay() {
        return PackedLocalDateTime.isInMay(value());
    }

    public boolean isInJune() {
        return PackedLocalDateTime.isInJune(value());
    }

    public boolean isInJuly() {
        return PackedLocalDateTime.isInJuly(value());
    }

    public boolean isInAugust() {
        return PackedLocalDateTime.isInAugust(value());
    }

    public boolean isInSeptember() {
        return PackedLocalDateTime.isInSeptember(value());
    }

    public boolean isInOctober() {
        return PackedLocalDateTime.isInOctober(value());
    }

    public boolean isInNovember() {
        return PackedLocalDateTime.isInNovember(value());
    }

    public boolean isInDecember() {
        return PackedLocalDateTime.isInDecember(value());
    }

    public boolean isLastDayOfMonth() {
        return PackedLocalDateTime.isLastDayOfMonth(value());
    }

    public boolean isInYear(int year) {
        return PackedLocalDateTime.isInYear(value(), year);
    }

    public boolean isMidnight() {
        return PackedLocalDateTime.isMidnight(value());
    }

    public boolean isNoon() {
        return PackedLocalDateTime.isNoon(value());
    }

    /**
     * Returns true if the time is in the AM or "before noon".
     * Note: we follow the convention that 12:00 NOON is PM and 12 MIDNIGHT is AM
     */
    public boolean AM() {
        return PackedLocalDateTime.AM(value());
    }

    /**
     * Returns true if the time is in the PM or "after noon".
     * Note: we follow the convention that 12:00 NOON is PM and 12 MIDNIGHT is AM
     */
    public boolean PM() {
        return PackedLocalDateTime.PM(value());
    }

    public int getMinuteOfDay() {
        return PackedLocalDateTime.getMinuteOfDay(value());
    }

    public byte getHour() {
        return PackedLocalDateTime.getHour(value());
    }

    public byte getMinute() {
        return PackedLocalDateTime.getMinute(value());
    }

    public byte getSecond() {
        return PackedLocalDateTime.getSecond(value());
    }

    public double getSecondOfDay() {
        return PackedLocalDateTime.getSecondOfDay(value());
    }

    public short getMillisecondOfMinute() {
        return PackedLocalDateTime.getMillisecondOfMinute(value());
    }

    public long getMillisecondOfDay() {
        return PackedLocalDateTime.getMillisecondOfDay(value());
    }

    public long toEpochMilli(ZoneOffset offset) {
        return PackedLocalDateTime.toEpochMilli(value(), offset);
    }

    public long ofEpochMilli(ZoneId zoneId) {
        return PackedLocalDateTime.ofEpochMilli(value(), zoneId);
    }

    public int lengthOfYear() {
        return PackedLocalDateTime.lengthOfYear(value());
    }

    public long minutesUntil(long packedDateTimeEnd) {
        return PackedLocalDateTime.minutesUntil(value(), packedDateTimeEnd);
    }

    public long minutesUntil(LocalDateTime packedDateTimeEnd) {
        return minutesUntil(PackedLocalDateTime.pack(packedDateTimeEnd));
    }

    public long hoursUntil(long packedDateTimeEnd) {
        return PackedLocalDateTime.hoursUntil(value(), packedDateTimeEnd);
    }

    public long hoursUntil(LocalDateTime packedDateTimeEnd) {
        return hoursUntil(PackedLocalDateTime.pack(packedDateTimeEnd));
    }

    public int daysUntil(long packedDateTimeEnd) {
        return PackedLocalDateTime.daysUntil(value(), packedDateTimeEnd);
    }

    public int daysUntil(LocalDateTime packedDateTimeEnd) {
        return daysUntil(PackedLocalDateTime.pack(packedDateTimeEnd));
    }

    public int weeksUntil(long packedDateTimeEnd) {
        return PackedLocalDateTime.weeksUntil(value(), packedDateTimeEnd);
    }

    public int weeksUntil(LocalDateTime packedDateTimeEnd) {
        return weeksUntil(PackedLocalDateTime.pack(packedDateTimeEnd));
    }

    public int monthsUntil(long packedDateTimeEnd) {
        return PackedLocalDateTime.monthsUntil(value(), packedDateTimeEnd);
    }

    public int monthsUntil(LocalDateTime packedDateTimeEnd) {
        return monthsUntil(PackedLocalDateTime.pack(packedDateTimeEnd));
    }

    public int yearsUntil(long packedDateEnd) {
        return PackedLocalDateTime.yearsUntil(value(), packedDateEnd);
    }

    public int yearsUntil(LocalDateTime packedDateEnd) {
        return yearsUntil(PackedLocalDateTime.pack(packedDateEnd));
    }

    public boolean isEqualTo(long value) {
        return PackedLocalDateTime.isEqualTo(value(), value);
    }

    public boolean isEqualTo(LocalDateTime value) {
        return isEqualTo(PackedLocalDateTime.pack(value));
    }

    public boolean isOnOrAfter(long valueToTestAgainst) {
        return PackedLocalDateTime.isOnOrAfter(value(), valueToTestAgainst);
    }

    public boolean isOnOrAfter(LocalDateTime valueToTestAgainst) {
        return isOnOrAfter(PackedLocalDateTime.pack(valueToTestAgainst));
    }

    public boolean isOnOrBefore(long valueToTestAgainst) {
        return PackedLocalDateTime.isOnOrBefore(value(), valueToTestAgainst);
    }

    public boolean isOnOrBefore(LocalDateTime valueToTestAgainst) {
        return isOnOrBefore(PackedLocalDateTime.pack(valueToTestAgainst));
    }

    private long value() {
        return dateTimeColumn.getLongInternal(index);
    }
}
