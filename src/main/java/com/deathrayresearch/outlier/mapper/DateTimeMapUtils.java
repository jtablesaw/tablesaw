package com.deathrayresearch.outlier.mapper;

import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.FloatColumn;
import com.deathrayresearch.outlier.columns.LocalDateTimeColumn;
import com.deathrayresearch.outlier.columns.PackedLocalDateTime;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public interface DateTimeMapUtils extends Column {

  default FloatColumn differenceInMilliseconds(LocalDateTimeColumn column2) {
    return difference(column2, ChronoUnit.MILLIS);
  }

  default FloatColumn differenceInSeconds(LocalDateTimeColumn column2) {
    return difference(column2, ChronoUnit.SECONDS);
  }

  default FloatColumn differenceInMinutes(LocalDateTimeColumn column2) {
    return difference(column2, ChronoUnit.MINUTES);
  }

  default FloatColumn differenceInHours(LocalDateTimeColumn column2) {
    return difference(column2, ChronoUnit.HOURS);
  }

  default FloatColumn differenceInDays(LocalDateTimeColumn column2) {
    return difference(column2, ChronoUnit.DAYS);
  }

  default FloatColumn differenceInYears(LocalDateTimeColumn column2) {
    return difference(column2, ChronoUnit.YEARS);
  }

  default FloatColumn difference(LocalDateTimeColumn column2, ChronoUnit unit) {

    FloatColumn newColumn = FloatColumn.create(name() + " - " + column2.name());
    for (int r = 0; r < size(); r++) {
      long c1 = this.get(r);
      long c2 = column2.get(r);
      if (c1 == LocalDateTimeColumn.MISSING_VALUE || c2 == LocalDateTimeColumn.MISSING_VALUE) {
        newColumn.add(FloatColumn.MISSING_VALUE);
      } else {
        LocalDateTime value1 = PackedLocalDateTime.asLocalDateTime(c1);
        LocalDateTime value2 = PackedLocalDateTime.asLocalDateTime(c2);
        newColumn.add(unit.between(value1, value2));
      }
    }
    return newColumn;
  }

  long get(int r);
}
