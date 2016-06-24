package com.github.lwhite1.tablesaw.mapper;

import com.github.lwhite1.tablesaw.columns.DateColumnUtils;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.columns.FloatColumn;
import com.github.lwhite1.tablesaw.columns.LocalDateColumn;
import com.github.lwhite1.tablesaw.columns.LocalDateTimeColumn;
import com.github.lwhite1.tablesaw.columns.LocalTimeColumn;
import com.github.lwhite1.tablesaw.columns.packeddata.PackedLocalDate;
import org.roaringbitmap.RoaringBitmap;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * An interface for mapping operations unique to Date columns
 */
public interface DateMapUtils extends DateColumnUtils {

  default FloatColumn differenceInDays(LocalDateColumn column2) {
    LocalDateColumn column1 = (LocalDateColumn) this;
    return difference(column1, column2, ChronoUnit.DAYS);
  }

  default FloatColumn differenceInWeeks(LocalDateColumn column2) {
    LocalDateColumn column1 = (LocalDateColumn) this;
    return difference(column1, column2, ChronoUnit.WEEKS);
  }

  default FloatColumn differenceInMonths(LocalDateColumn column2) {
    LocalDateColumn column1 = (LocalDateColumn) this;
    return difference(column1, column2, ChronoUnit.MONTHS);
  }

  default FloatColumn differenceInYears(LocalDateColumn column2) {
    LocalDateColumn column1 = (LocalDateColumn) this;
    return difference(column1, column2, ChronoUnit.YEARS);
  }

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

  default RoaringBitmap isLessThan(LocalDate d) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (int next : data()) {
      if (next < PackedLocalDate.pack(d)) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  // These functions fill some amount of time to a date, producing a new date column

  default LocalDateColumn plusDays(int days) {
    return plus(days, ChronoUnit.DAYS);
  }

  default LocalDateColumn plusWeeks(int weeks) {
    return plus(weeks, ChronoUnit.WEEKS);
  }

  default LocalDateColumn plusYears(int years) {
    return plus(years, ChronoUnit.YEARS);
  }

  default LocalDateColumn plusMonths(int months) {
    return plus(months, ChronoUnit.MONTHS);
  }

  // These functions subtract some amount of time from a date, producing a new date column

  default LocalDateColumn minusDays(int days) {
    return plus((-1 * days), ChronoUnit.DAYS);
  }

  default LocalDateColumn minusWeeks(int weeks) {
    return minus((-1 * weeks), ChronoUnit.WEEKS);
  }

  default LocalDateColumn minusYears(int years) {
    return minus((-1 * years), ChronoUnit.YEARS);
  }

  default LocalDateColumn minusMonths(int months) {
    return minus((-1 * months), ChronoUnit.MONTHS);
  }

  default LocalDateColumn plus(int value, TemporalUnit unit) {

    LocalDateColumn newColumn = LocalDateColumn.create(dateColumnName(this, value, unit));
    LocalDateColumn column1 = (LocalDateColumn) this;

    for (int r = 0; r < column1.size(); r++) {
      Comparable c1 = column1.get(r);
      if (c1 == null) {
        newColumn.add(null);
      } else {
        LocalDate value1 = (LocalDate) c1;
        newColumn.add(value1.plus(value, unit));
      }
    }
    return newColumn;
  }

  default LocalDateColumn minus(int value, TemporalUnit unit) {
    LocalDateColumn column1 = (LocalDateColumn) this;
    LocalDateColumn newColumn = LocalDateColumn.create(dateColumnName(column1, value, unit));
    for (int r = 0; r < column1.size(); r++) {
      Comparable c1 = column1.get(r);
      if (c1 == null) {
        newColumn.add(null);
      } else {
        LocalDate value1 = (LocalDate) c1;
        newColumn.add(value1.minus(value, unit));
      }
    }
    return newColumn;
  }

  // misc functions

  default LocalDateTimeColumn atStartOfDay() {
    LocalDateTimeColumn newColumn = LocalDateTimeColumn.create(this.name() + " " + " start");
    for (int r = 0; r < this.size(); r++) {
      Comparable c1 = this.get(r);
      if (c1 == null) {
        newColumn.add(null);
      } else {
        LocalDate value1 = (LocalDate) c1;
        newColumn.add(value1.atStartOfDay());
      }
    }
    return newColumn;
  }

  default LocalDateTimeColumn atTime(LocalTime time) {
    LocalDateTimeColumn newColumn = LocalDateTimeColumn.create(this.name() + " " + time.toString());
    for (int r = 0; r < this.size(); r++) {
      Comparable c1 = this.get(r);
      if (c1 == null) {
        newColumn.add(null);
      } else {
        LocalDate value1 = (LocalDate) c1;
        newColumn.add(value1.atTime(time));
      }
    }
    return newColumn;
  }

  static String dateColumnName(Column column1, int value, TemporalUnit unit) {
    return column1.name() + ": " + value + " " + unit.toString() + "(s)";
  }

  LocalDate get(int index);
}
