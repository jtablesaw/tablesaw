package com.github.lwhite1.outlier.mapper;

import com.github.lwhite1.outlier.columns.Column;
import com.github.lwhite1.outlier.columns.IntColumn;
import com.github.lwhite1.outlier.columns.LocalDateTimeColumn;
import com.github.lwhite1.outlier.columns.LongColumn;
import com.github.lwhite1.outlier.columns.ShortColumn;
import com.github.lwhite1.outlier.columns.packeddata.PackedLocalDateTime;

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
        newColumn.add(difference(c1, c2, unit));
      }
    }
    return newColumn;
  }

  default long difference(long packedLocalDateTime1, long packedLocalDateTime2, ChronoUnit unit) {
    LocalDateTime value1 = PackedLocalDateTime.asLocalDateTime(packedLocalDateTime1);
    LocalDateTime value2 = PackedLocalDateTime.asLocalDateTime(packedLocalDateTime2);
    return unit.between(value1, value2);
  }

  default ShortColumn hour() {
    ShortColumn newColumn = ShortColumn.create(name() + "[" + "hour" + "]");
    for (int r = 0; r < size(); r++) {
      long c1 = getLong(r);
      if (c1 != LocalDateTimeColumn.MISSING_VALUE) {
        newColumn.add(PackedLocalDateTime.getHour(c1));
      } else {
        newColumn.add(ShortColumn.MISSING_VALUE);
      }
    }
    return newColumn;
  }

  default IntColumn minuteOfDay() {
    IntColumn newColumn = IntColumn.create(name() + "[" + "minute-of-day" + "]");
    for (int r = 0; r < size(); r++) {
      long c1 = getLong(r);
      if (c1 != LocalDateTimeColumn.MISSING_VALUE) {
        newColumn.add(PackedLocalDateTime.getMinuteOfDay(c1));
      } else {
        newColumn.add(IntColumn.MISSING_VALUE);
      }
    }
    return newColumn;
  }

  default IntColumn secondOfDay() {
    IntColumn newColumn = IntColumn.create(name() + "[" + "second-of-day" + "]");
    for (int r = 0; r < size(); r++) {
      long c1 = getLong(r);
      if (c1 != LocalDateTimeColumn.MISSING_VALUE) {
        newColumn.add(PackedLocalDateTime.getSecondOfDay(c1));
      } else {
        newColumn.add(IntColumn.MISSING_VALUE);
      }
    }
    return newColumn;
  }

  LocalDateTime get(int r);

  long getLong(int r);
}
