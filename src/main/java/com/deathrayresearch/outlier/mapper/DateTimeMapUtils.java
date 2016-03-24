package com.deathrayresearch.outlier.mapper;

import com.deathrayresearch.outlier.columns.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public interface DateTimeMapUtils extends Column {

  default LongColumn differenceInMilliseconds(LocalDateTimeColumn column2) {
    return difference(column2, ChronoUnit.MILLIS);
  }

  default LongColumn differenceInSeconds(LocalDateTimeColumn column2) {
    return difference(column2, ChronoUnit.SECONDS);
  }

  default LongColumn differenceInMinutes(LocalDateTimeColumn column2) {
    return difference(column2, ChronoUnit.MINUTES);
  }

  default LongColumn differenceInHours(LocalDateTimeColumn column2) {
    return difference(column2, ChronoUnit.HOURS);
  }

  default LongColumn differenceInDays(LocalDateTimeColumn column2) {
    return difference(column2, ChronoUnit.DAYS);
  }

  default LongColumn differenceInYears(LocalDateTimeColumn column2) {
    return difference(column2, ChronoUnit.YEARS);
  }

  default LongColumn difference(LocalDateTimeColumn column2, ChronoUnit unit) {

    LongColumn newColumn = LongColumn.create(name() + " - " + column2.name());
    for (int r = 0; r < size(); r++) {
      long c1 = this.getLong(r);
      long c2 = column2.getLong(r);
      if (c1 == LocalDateTimeColumn.MISSING_VALUE || c2 == LocalDateTimeColumn.MISSING_VALUE) {
        newColumn.add(IntColumn.MISSING_VALUE);
      } else {
        LocalDateTime value1 = PackedLocalDateTime.asLocalDateTime(c1);
        LocalDateTime value2 = PackedLocalDateTime.asLocalDateTime(c2);
        newColumn.add(unit.between(value1, value2));
      }
    }
    return newColumn;
  }

  default CategoryColumn hour() {
    ChronoUnit unit = ChronoUnit.HOURS;
    CategoryColumn newColumn = CategoryColumn.create(name() + "[" + unit.name() + "]");
    for (int r = 0; r < size(); r++) {
      LocalDateTime c1 = get(r);
      String value;
      if (c1 != null) {
        value = String.valueOf(c1.getHour());
        newColumn.set(r, value);
      }
        newColumn.set(r, CategoryColumn.MISSING_VALUE);
    }
    return newColumn;
  }

  LocalDateTime get(int r);

  long getLong(int r);
}
