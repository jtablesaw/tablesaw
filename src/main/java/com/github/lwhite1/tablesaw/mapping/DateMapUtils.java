package com.github.lwhite1.tablesaw.mapping;

import com.github.lwhite1.tablesaw.api.DateColumn;
import com.github.lwhite1.tablesaw.api.DateTimeColumn;
import com.github.lwhite1.tablesaw.api.FloatColumn;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.columns.DateColumnUtils;
import com.github.lwhite1.tablesaw.columns.packeddata.PackedLocalDate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * An interface for mapping operations unique to Date columns
 */
public interface DateMapUtils extends DateColumnUtils {

  default FloatColumn differenceInDays(DateColumn column2) {
    DateColumn column1 = (DateColumn) this;
    return difference(column1, column2, ChronoUnit.DAYS);
  }

  default FloatColumn differenceInWeeks(DateColumn column2) {
    DateColumn column1 = (DateColumn) this;
    return difference(column1, column2, ChronoUnit.WEEKS);
  }

  default FloatColumn differenceInMonths(DateColumn column2) {
    DateColumn column1 = (DateColumn) this;
    return difference(column1, column2, ChronoUnit.MONTHS);
  }

  default FloatColumn differenceInYears(DateColumn column2) {
    DateColumn column1 = (DateColumn) this;
    return difference(column1, column2, ChronoUnit.YEARS);
  }

  default FloatColumn difference(DateColumn column1, DateColumn column2, ChronoUnit unit) {

    FloatColumn newColumn = FloatColumn.create(column1.name() + " - " + column2.name());
    for (int r = 0; r < column1.size(); r++) {
      int c1 = column1.getInt(r);
      int c2 = column2.getInt(r);
      if (c1 == FloatColumn.MISSING_VALUE || c2 == FloatColumn.MISSING_VALUE) {
        newColumn.add(FloatColumn.MISSING_VALUE);
      } else {
        LocalDate value1 = PackedLocalDate.asLocalDate(c1);
        LocalDate value2 = PackedLocalDate.asLocalDate(c2);
        newColumn.add(unit.between(value1, value2));
      }
    }
    return newColumn;
  }

  // These functions fill some amount of time to a date, producing a new date column
  default DateColumn plusDays(int days) {
    return plus(days, ChronoUnit.DAYS);
  }

  default DateColumn plusWeeks(int weeks) {
    return plus(weeks, ChronoUnit.WEEKS);
  }

  default DateColumn plusYears(int years) {
    return plus(years, ChronoUnit.YEARS);
  }

  default DateColumn plusMonths(int months) {
    return plus(months, ChronoUnit.MONTHS);
  }

  // These functions subtract some amount of time from a date, producing a new date column

  default DateColumn minusDays(int days) {
    return plusDays(-days);
  }

  default DateColumn minusWeeks(int weeks) {
    return plusWeeks(-weeks);
  }

  default DateColumn minusYears(int years) {
    return plusYears(-years);
  }

  default DateColumn minusMonths(int months) {
    return plusMonths(-months);
  }

  default DateColumn plus(int value, TemporalUnit unit) {

    DateColumn newColumn = DateColumn.create(dateColumnName(this, value, unit));
    DateColumn column1 = (DateColumn) this;

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

  default DateColumn minus(int value, TemporalUnit unit) {
    DateColumn column1 = (DateColumn) this;
    DateColumn newColumn = DateColumn.create(dateColumnName(column1, value, unit));
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

  default DateTimeColumn atStartOfDay() {
    DateTimeColumn newColumn = DateTimeColumn.create(this.name() + " " + " start");
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

    /**
     * Returns a DateTime column where each value consists of the dates from this column combined with the corresponding
     * times from the other column
     */
    default DateTimeColumn atTime(LocalTime time) {
    DateTimeColumn newColumn = DateTimeColumn.create(this.name() + " " + time.toString());
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
