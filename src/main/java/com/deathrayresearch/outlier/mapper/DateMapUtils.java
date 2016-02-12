package com.deathrayresearch.outlier.mapper;

import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.FloatColumn;
import com.deathrayresearch.outlier.columns.LocalDateColumn;
import com.deathrayresearch.outlier.columns.PackedLocalDate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * An interface for mapping operations unique to Date columns
 */
public interface DateMapUtils extends Column {

  default FloatColumn differenceInDays(LocalDateColumn column1, LocalDateColumn column2) {
    return difference(column1, column2, ChronoUnit.DAYS);
  }
/*
  default FloatColumn differenceInWeeks(LocalDateColumn column2) {
    return difference(this, column2, ChronoUnit.WEEKS);
  }

  default FloatColumn differenceInMonths(LocalDateColumn column2) {
    return difference(this, column2, ChronoUnit.MONTHS);
  }

  default FloatColumn differenceInYears(LocalDateColumn column2) {
    return difference(this, column2, ChronoUnit.YEARS);
  }

*/
  default FloatColumn difference(LocalDateColumn column1, LocalDateColumn column2, ChronoUnit unit) {


    FloatColumn newColumn = FloatColumn.create(column1.name() + " - " + column2.name());
    for (int r = 0; r < column1.size(); r++) {
      int c1 = column1.getInt(r);
      int c2 = column2.getInt(r);
      if (c1 == FloatColumn.MISSING_VALUE || c2 == FloatColumn.MISSING_VALUE) {
        newColumn.set(r, FloatColumn.MISSING_VALUE);
      } else {
        LocalDate value1 = PackedLocalDate.asLocalDate(c1);
        LocalDate value2 = PackedLocalDate.asLocalDate(c2);
        newColumn.set(r, unit.between(value1, value2));
      }
    }
    return newColumn;
  }

  /*

  // These functions fill some amount of time to a date, producing a new date column

/*
  default LocalDateColumn plusDays(Column column1, int days) {
    return and(column1, days, ChronoUnit.DAYS);
  }


  default LocalDateColumn plusWeeks(Column column1, int weeks) {
    return and(column1, weeks, ChronoUnit.WEEKS);
  }

  default LocalDateColumn plusYears(Column column1, int years) {
    return and(column1, years, ChronoUnit.YEARS);
  }

  default LocalDateColumn plusMonths(Column column1, int months) {
    return and(column1, months, ChronoUnit.MONTHS);
  }

  // These functions subtract some amount of time to a date, producing a new date column

  default LocalDateColumn minusDays(Column column1, int days) {
    return minus(column1, days, ChronoUnit.DAYS);
  }

  default LocalDateColumn minusWeeks(Column column1, int weeks) {
    return minus(column1, weeks, ChronoUnit.WEEKS);
  }

  default LocalDateColumn minusYears(Column column1, int years) {
    return minus(column1, years, ChronoUnit.YEARS);
  }

  default LocalDateColumn minusMonths(Column column1, int months) {
    return minus(column1, months, ChronoUnit.MONTHS);
  }

  default LocalDateColumn and(Column column1, int value, TemporalUnit unit) {

    LocalDateColumn newColumn = LocalDateColumn.create(dateColumnName(column1, value, unit));
    for (int r = 0; r < column1.size(); r++) {
      Comparable c1 = column1.get(r);
      if (c1 == null) {
        newColumn.set(r, null);
      } else {
        LocalDate value1 = (LocalDate) c1;
        newColumn.set(r, value1.and(value, unit));
      }
    }
    return newColumn;
  }

  default LocalDateColumn minus(Column column1, int value, TemporalUnit unit) {

    LocalDateColumn newColumn = LocalDateColumn.create(dateColumnName(column1, value, unit));
    for (int r = 0; r < column1.size(); r++) {
      Comparable c1 = column1.getDate(r);
      if (c1 == null) {
        newColumn.set(r, null);
      } else {
        LocalDate value1 = (LocalDate) c1;
        newColumn.set(r, value1.minus(value, unit));
      }
    }
    return newColumn;
  }
*/
  // misc functions

/*
  default LocalDateTimeColumn atStartOfDay() {
    LocalDateTimeColumn newColumn = LocalDateTimeColumn.create(this.name() + " " + " start");
    for (int r = 0; r < this.size(); r++) {
      Comparable c1 = this.getDate(r);
      if (c1 == null) {
        newColumn.set(r, null);
      } else {
        LocalDate value1 = (LocalDate) c1;
        newColumn.set(r, value1.atStartOfDay());
      }
    }
    return newColumn;
  }

  default LocalDateTimeColumn atTime(LocalTimeColumn c) {
    LocalDateTimeColumn newColumn = LocalDateTimeColumn.create(this.name() + " " + c.name());
    for (int r = 0; r < this.size(); r++) {
      Comparable c1 = this.get(r);
      Comparable c2 = c.get(r);
      if (c1 == null || c2 == null) {
        newColumn.set(r, null);
      } else {
        LocalDate value1 = (LocalDate) c1;
        LocalTime time = (LocalTime) c2;
        newColumn.set(r, value1.atTime(time));
      }
    }
    return newColumn;
  }

  default LocalDateTimeColumn atTime(LocalTime time) {
    LocalDateTimeColumn newColumn = LocalDateTimeColumn.create(this.name() + " " + time.toString());
    for (int r = 0; r < this.size(); r++) {
      Comparable c1 = this.get(r);
      if (c1 == null) {
        newColumn.set(r, null);
      } else {
        LocalDate value1 = (LocalDate) c1;
        newColumn.set(r, value1.atTime(time));
      }
    }
    return newColumn;
  }

  default FloatColumn year() {
    FloatColumn newColumn = FloatColumn.create(this.name() + " year");
    for (int r = 0; r < this.size(); r++) {
      Comparable c1 = this.get(r);
      if (c1 == null) {
        newColumn.set(r, null);
      } else {
        LocalDate value1 = (LocalDate) c1;
        newColumn.set(r, value1.getYear());
      }
    }
    return newColumn;
  }

  default CategoryColumn dayOfWeek() {
    CategoryColumn newColumn = CategoryColumn.create(this.name() + " year");
    for (int r = 0; r < this.size(); r++) {
      Comparable c1 = this.get(r);
      if (c1 == null) {
        newColumn.set(r, null);
      } else {
        LocalDate value1 = (LocalDate) c1;
        newColumn.set(r, value1.getDayOfWeek().toString());
      }
    }
    return newColumn;
  }

  default FloatColumn dayOfMonth() {
    FloatColumn newColumn = FloatColumn.create(this.name() + " day of month");
    for (int r = 0; r < this.size(); r++) {
      Comparable c1 = this.get(r);
      if (c1 == null) {
        newColumn.set(r, null);
      } else {
        LocalDate value1 = (LocalDate) c1;
        newColumn.set(r, value1.getDayOfMonth());
      }
    }
    return newColumn;
  }

  default FloatColumn dayOfYear() {
    FloatColumn newColumn = FloatColumn.create(this.name() + " day of year");
    for (int r = 0; r < this.size(); r++) {
      Comparable c1 = this.get(r);
      if (c1 == null) {
        newColumn.set(r, null);
      } else {
        LocalDate value1 = (LocalDate) c1;
        newColumn.set(r, value1.getDayOfYear());
      }
    }
    return newColumn;
  }

  default FloatColumn monthNumber() {
    FloatColumn newColumn = FloatColumn.create(this.name() + " month");
    for (int r = 0; r < this.size(); r++) {
      Comparable c1 = this.get(r);
      if (c1 == null) {
        newColumn.set(r, null);
      } else {
        LocalDate value1 = (LocalDate) c1;
        newColumn.set(r, value1.getMonthValue());
      }
    }
    return newColumn;
  }

  default CategoryColumn monthName() {
    CategoryColumn newColumn = CategoryColumn.create(this.name() + " month");
    for (int r = 0; r < this.size(); r++) {
      Comparable c1 = this.get(r);
      if (c1 == null) {
        newColumn.set(r, null);
      } else {
        LocalDate value1 = (LocalDate) c1;
        newColumn.set(r, value1.getMonth().name());
      }
    }
    return newColumn;
  }
*/

/*
  static String dateColumnName(Column column1, int value, TemporalUnit unit) {
    return column1.name() + " - " + value + " " + unit.toString() +"(s)";
  }

*/

}
