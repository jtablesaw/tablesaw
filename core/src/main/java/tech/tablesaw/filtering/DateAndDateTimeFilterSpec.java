package tech.tablesaw.filtering;

import com.google.common.annotations.Beta;
import java.time.LocalDate;

@Beta
public interface DateAndDateTimeFilterSpec<T> extends FilterSpec<T> {

  T isMonday();

  T isTuesday();

  T isWednesday();

  T isThursday();

  T isFriday();

  T isSaturday();

  T isSunday();

  T isInJanuary();

  T isInFebruary();

  T isInMarch();

  T isInApril();

  T isInMay();

  T isInJune();

  T isInJuly();

  T isInAugust();

  T isInSeptember();

  T isInOctober();

  T isInNovember();

  T isInDecember();

  T isFirstDayOfMonth();

  T isLastDayOfMonth();

  T isInQ1();

  T isInQ2();

  T isInQ3();

  T isInQ4();

  T isInYear(int year);

  T isAfter(LocalDate value);

  T isBefore(LocalDate value);

  T isOnOrBefore(LocalDate value);

  T isOnOrAfter(LocalDate value);
}
