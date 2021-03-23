package tech.tablesaw.columns.datetimes;

import static tech.tablesaw.columns.datetimes.DateTimePredicates.isInYear;
import static tech.tablesaw.columns.temporal.TemporalPredicates.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.function.Predicate;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.columns.temporal.TemporalFilters;
import tech.tablesaw.filtering.DateTimeFilterSpec;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

public interface DateTimeFilters
    extends TemporalFilters<LocalDateTime>, DateTimeFilterSpec<Selection> {

  default Selection isAfter(LocalDateTime value) {
    return eval(isGreaterThan, value.toEpochSecond(ZoneOffset.UTC));
  }

  default Selection isAfter(LocalDate value) {
    return isOnOrAfter(value.plusDays(1));
  }

  default Selection isOnOrAfter(LocalDate value) {
    return isOnOrAfter(value.atStartOfDay());
  }

  default Selection isOnOrAfter(LocalDateTime value) {
    return eval(isGreaterThanOrEqualTo, value.toEpochSecond(ZoneOffset.UTC));
  }

  default Selection isBefore(LocalDateTime value) {
    return eval(isLessThan, value.toEpochSecond(ZoneOffset.UTC));
  }

  default Selection isBefore(LocalDate value) {
    return isBefore(value.atStartOfDay());
  }

  default Selection isOnOrBefore(LocalDate value) {
    return isOnOrBefore(value.atStartOfDay());
  }

  default Selection isOnOrBefore(LocalDateTime value) {
    return eval(isLessThanOrEqualTo, value.toEpochSecond(ZoneOffset.UTC));
  }

  default Selection isAfter(DateTimeColumn column) {
    Selection results = new BitmapBackedSelection();
    for (int i = 0; i < size(); i++) {
      if (getLongInternal(i) > column.getLongInternal(i)) {
        results.add(i);
      }
    }
    return results;
  }

  default Selection isBefore(DateTimeColumn column) {
    Selection results = new BitmapBackedSelection();
    for (int i = 0; i < size(); i++) {
      if (getLongInternal(i) < column.getLongInternal(i)) {
        results.add(i);
      }
    }
    return results;
  }

  default Selection isEqualTo(LocalDateTime value) {
    long packed = value.toEpochSecond(ZoneOffset.UTC);
    return eval(isEqualTo, packed);
  }

  default Selection isNotEqualTo(LocalDateTime value) {
    long packed = value.toEpochSecond(ZoneOffset.UTC);
    return eval(isNotEqualTo, packed);
  }

  default Selection isEqualTo(DateTimeColumn column) {
    Selection results = new BitmapBackedSelection();
    for (int i = 0; i < size(); i++) {
      if (getLongInternal(i) == column.getLongInternal(i)) {
        results.add(i);
      }
    }
    return results;
  }

  default Selection isNotEqualTo(DateTimeColumn column) {
    Selection results = Selection.withRange(0, size());
    return results.andNot(isEqualTo(column));
  }

  default Selection isOnOrAfter(DateTimeColumn column) {
    Selection results = Selection.withRange(0, size());
    return results.andNot(isBefore(column));
  }

  default Selection isOnOrBefore(DateTimeColumn column) {
    Selection results = Selection.withRange(0, size());
    return results.andNot(isAfter(column));
  }

  default Selection isMonday() {
    return eval(
        ((Predicate<LocalDateTime>) localDateTime -> localDateTime.getDayOfWeek().getValue() == 1));
  }

  default Selection isTuesday() {
    return eval(
        ((Predicate<LocalDateTime>) localDateTime -> localDateTime.getDayOfWeek().getValue() == 2));
  }

  default Selection isWednesday() {
    return eval(
        ((Predicate<LocalDateTime>) localDateTime -> localDateTime.getDayOfWeek().getValue() == 3));
  }

  default Selection isThursday() {
    return eval(
        ((Predicate<LocalDateTime>) localDateTime -> localDateTime.getDayOfWeek().getValue() == 4));
  }

  default Selection isFriday() {
    return eval(
        ((Predicate<LocalDateTime>) localDateTime -> localDateTime.getDayOfWeek().getValue() == 5));
  }

  default Selection isSaturday() {
    return eval(
        ((Predicate<LocalDateTime>) localDateTime -> localDateTime.getDayOfWeek().getValue() == 6));
  }

  default Selection isSunday() {
    return eval(
        ((Predicate<LocalDateTime>) localDateTime -> localDateTime.getDayOfWeek().getValue() == 7));
  }

  default Selection isInJanuary() {
    return eval(((Predicate<LocalDateTime>) localDateTime -> localDateTime.getMonthValue() == 1));
  }

  default Selection isInFebruary() {
    return eval(((Predicate<LocalDateTime>) localDateTime -> localDateTime.getMonthValue() == 2));
  }

  default Selection isInMarch() {
    return eval(((Predicate<LocalDateTime>) localDateTime -> localDateTime.getMonthValue() == 3));
  }

  default Selection isInApril() {
    return eval(((Predicate<LocalDateTime>) localDateTime -> localDateTime.getMonthValue() == 4));
  }

  default Selection isInMay() {
    return eval(((Predicate<LocalDateTime>) localDateTime -> localDateTime.getMonthValue() == 5));
  }

  default Selection isInJune() {
    return eval(((Predicate<LocalDateTime>) localDateTime -> localDateTime.getMonthValue() == 6));
  }

  default Selection isInJuly() {
    return eval(((Predicate<LocalDateTime>) localDateTime -> localDateTime.getMonthValue() == 7));
  }

  default Selection isInAugust() {
    return eval(((Predicate<LocalDateTime>) localDateTime -> localDateTime.getMonthValue() == 8));
  }

  default Selection isInSeptember() {
    return eval(((Predicate<LocalDateTime>) localDateTime -> localDateTime.getMonthValue() == 9));
  }

  default Selection isInOctober() {
    return eval(((Predicate<LocalDateTime>) localDateTime -> localDateTime.getMonthValue() == 10));
  }

  default Selection isInNovember() {
    return eval(((Predicate<LocalDateTime>) localDateTime -> localDateTime.getMonthValue() == 11));
  }

  default Selection isInDecember() {
    return eval(((Predicate<LocalDateTime>) localDateTime -> localDateTime.getMonthValue() == 12));
  }

  default Selection isFirstDayOfMonth() {
    return eval(((Predicate<LocalDateTime>) localDateTime -> localDateTime.getDayOfMonth() == 1));
  }

  default Selection isLastDayOfMonth() {
    return eval(
        ((Predicate<LocalDateTime>)
            localDateTime ->
                localDateTime.getDayOfMonth() == localDateTime.getMonth().maxLength()));
  }

  default Selection isInQ1() {
    return eval(
        ((Predicate<LocalDateTime>) localDateTime -> localDateTime.getMonth().getValue() < 4));
  }

  default Selection isInQ2() {
    return eval(
        ((Predicate<LocalDateTime>)
            localDateTime ->
                localDateTime.getMonth().getValue() > 3
                    && localDateTime.getMonth().getValue() < 7));
  }

  default Selection isInQ3() {
    return eval(
        ((Predicate<LocalDateTime>)
            localDateTime ->
                localDateTime.getMonth().getValue() > 6
                    && localDateTime.getMonth().getValue() < 10));
  }

  default Selection isInQ4() {
    return eval(
        ((Predicate<LocalDateTime>) localDateTime -> localDateTime.getMonth().getValue() > 9));
  }

  default Selection isNoon() {
    return eval(
        ((Predicate<LocalDateTime>)
            localDateTime -> localDateTime.toLocalTime().equals(LocalTime.NOON)));
  }

  default Selection isMidnight() {
    return eval(
        ((Predicate<LocalDateTime>)
            localDateTime -> localDateTime.toLocalTime().equals(LocalTime.MIDNIGHT)));
  }

  default Selection isBeforeNoon() {
    return eval(
        ((Predicate<LocalDateTime>)
            localDateTime -> localDateTime.toLocalTime().compareTo(LocalTime.NOON) < 0));
  }

  default Selection isAfterNoon() {
    return eval(
        ((Predicate<LocalDateTime>)
            localDateTime -> localDateTime.toLocalTime().compareTo(LocalTime.NOON) > 0));
  }

  default Selection isBetweenExcluding(LocalDateTime lowValue, LocalDateTime highValue) {
    return eval(LocalDateTime::isAfter, lowValue).and(eval(LocalDateTime::isBefore, highValue));
  }

  default Selection isBetweenIncluding(LocalDateTime lowValue, LocalDateTime highValue) {
    return eval((Predicate<LocalDateTime>) ldt -> ldt.isBefore(lowValue) || ldt.isEqual(lowValue))
        .and(
            eval(
                (Predicate<LocalDateTime>)
                    ldt -> ldt.isAfter(highValue) || ldt.isEqual(highValue)));
  }

  default Selection isInYear(int year) {
    return eval(isInYear, year);
  }

  @Override
  default Selection isMissing() {
    return eval(isMissing);
  }

  @Override
  default Selection isNotMissing() {
    return eval(isNotMissing);
  }
}
