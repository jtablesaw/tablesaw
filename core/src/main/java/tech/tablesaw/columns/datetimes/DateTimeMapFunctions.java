/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.columns.datetimes;

import com.google.common.base.Strings;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.UnsupportedTemporalTypeException;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.columns.strings.StringColumnType;
import tech.tablesaw.columns.temporal.TemporalMapFunctions;
import tech.tablesaw.columns.times.TimeColumnType;

public interface DateTimeMapFunctions extends TemporalMapFunctions<LocalDateTime> {

  default IntColumn hour() {
    IntColumn newColumn = IntColumn.create(name() + "[" + "hour" + "]");
    for (int r = 0; r < size(); r++) {
      if (!isMissing(r)) {
        LocalDateTime ldt =
            LocalDateTime.ofEpochSecond(getLongInternal(r), getIntInternal(r), ZoneOffset.UTC);
        newColumn.append(ldt.getHour());
      } else {
        newColumn.appendMissing();
      }
    }
    return newColumn;
  }

  default IntColumn minuteOfDay() {
    IntColumn newColumn = IntColumn.create(name() + "[" + "minute-of-day" + "]");
    for (int r = 0; r < size(); r++) {
      if (!isMissing(r)) {
        LocalDateTime ldt =
            LocalDateTime.ofEpochSecond(getLongInternal(r), getIntInternal(r), ZoneOffset.UTC);
        newColumn.append(ldt.getHour() * 60 + ldt.getMinute());
      } else {
        newColumn.appendMissing();
      }
    }
    return newColumn;
  }

  default IntColumn secondOfDay() {
    IntColumn newColumn = IntColumn.create(name() + "[" + "second-of-day" + "]");
    for (int r = 0; r < size(); r++) {
      if (!isMissing(r)) {
        LocalDateTime ldt =
            LocalDateTime.ofEpochSecond(getLongInternal(r), getIntInternal(r), ZoneOffset.UTC);
        newColumn.append(ldt.getHour() * 3600 + ldt.getMinute() * 60 + ldt.getSecond());
      } else {
        newColumn.appendMissing();
      }
    }
    return newColumn;
  }

  @Override
  default DateTimeColumn lead(int n) {
    DateTimeColumn column = lag(-n);
    column.setName(name() + " lead(" + n + ")");
    return column;
  }

  @Override
  DateTimeColumn lag(int n);

  /** Returns a TimeColumn containing the time portion of each dateTime in this DateTimeColumn */
  default TimeColumn time() {
    TimeColumn newColumn = TimeColumn.create(this.name() + " time");
    for (int r = 0; r < this.size(); r++) {
      LocalDateTime ldt =
          LocalDateTime.ofEpochSecond(getLongInternal(r), getIntInternal(r), ZoneOffset.UTC);
      if (DateTimeColumn.valueIsMissing(getLongInternal(r))) {
        newColumn.appendInternal(TimeColumnType.missingValueIndicator());
      } else {
        newColumn.append(ldt.toLocalTime());
      }
    }
    return newColumn;
  }

  default IntColumn monthValue() {
    IntColumn newColumn = IntColumn.create(this.name() + " month");
    for (int r = 0; r < this.size(); r++) {
      if (isMissing(r)) {
        newColumn.appendMissing();
      } else {
        LocalDateTime ldt =
            LocalDateTime.ofEpochSecond(getLongInternal(r), getIntInternal(r), ZoneOffset.UTC);
        newColumn.append(ldt.getMonthValue());
      }
    }
    return newColumn;
  }

  /** Returns a StringColumn containing the name of the month for each date/time in this column */
  default StringColumn month() {
    StringColumn newColumn = StringColumn.create(this.name() + " month");
    for (int r = 0; r < this.size(); r++) {
      LocalDateTime ldt =
          LocalDateTime.ofEpochSecond(getLongInternal(r), getIntInternal(r), ZoneOffset.UTC);
      if (DateTimeColumn.valueIsMissing(getLongInternal(r))) {
        newColumn.append(StringColumnType.missingValueIndicator());
      } else {
        newColumn.append(ldt.getMonth().name());
      }
    }
    return newColumn;
  }

  /**
   * Returns a StringColumn with the year and quarter from this column concatenated into a String
   * that will sort lexicographically in temporal order.
   *
   * <p>This simplifies the production of plots and tables that aggregate values into standard
   * temporal units (e.g., you want monthly data but your source data is more than a year long and
   * you don't want months from different years aggregated together).
   */
  default StringColumn yearQuarter() {
    StringColumn newColumn = StringColumn.create(this.name() + " year & quarter");
    for (int r = 0; r < this.size(); r++) {
      long epochSecond = this.getLongInternal(r);
      if (DateTimeColumn.valueIsMissing(epochSecond)) {
        newColumn.append(StringColumnType.missingValueIndicator());
      } else {
        LocalDateTime ldt =
            LocalDateTime.ofEpochSecond(getLongInternal(r), getIntInternal(r), ZoneOffset.UTC);
        String yq = ldt.getYear() + "-" + ldt.get(IsoFields.QUARTER_OF_YEAR);
        newColumn.append(yq);
      }
    }
    return newColumn;
  }

  @Override
  DateTimeColumn plus(long amountToAdd, ChronoUnit unit);

  @Override
  default DateTimeColumn plusYears(long amountToAdd) {
    return plus(amountToAdd, ChronoUnit.YEARS);
  }

  @Override
  default DateTimeColumn plusMonths(long amountToAdd) {
    return plus(amountToAdd, ChronoUnit.MONTHS);
  }

  @Override
  default DateTimeColumn plusWeeks(long amountToAdd) {
    return plus(amountToAdd, ChronoUnit.WEEKS);
  }

  @Override
  default DateTimeColumn plusDays(long amountToAdd) {
    return plus(amountToAdd, ChronoUnit.DAYS);
  }

  @Override
  default DateTimeColumn plusHours(long amountToAdd) {
    return plus(amountToAdd, ChronoUnit.HOURS);
  }

  @Override
  default DateTimeColumn plusMinutes(long amountToAdd) {
    return plus(amountToAdd, ChronoUnit.MINUTES);
  }

  @Override
  default DateTimeColumn plusSeconds(long amountToAdd) {
    return plus(amountToAdd, ChronoUnit.SECONDS);
  }

  @Override
  default DateTimeColumn plusMillis(long amountToAdd) {
    return plus(amountToAdd, ChronoUnit.MILLIS);
  }

  @Override
  default DateTimeColumn plusMicros(long amountToAdd) {
    return plus(amountToAdd, ChronoUnit.MICROS);
  }

  /**
   * Returns a StringColumn with the year and month from this column concatenated into a String that
   * will sort lexicographically in temporal order.
   *
   * <p>This simplifies the production of plots and tables that aggregate values into standard
   * temporal units (e.g., you want monthly data but your source data is more than a year long and
   * you don't want months from different years aggregated together).
   */
  default StringColumn yearMonth() {
    StringColumn newColumn = StringColumn.create(this.name() + " year & month");
    for (int r = 0; r < this.size(); r++) {
      long c1 = this.getLongInternal(r);
      if (DateTimeColumn.valueIsMissing(c1)) {
        newColumn.append(StringColumnType.missingValueIndicator());
      } else {
        LocalDateTime ldt =
            LocalDateTime.ofEpochSecond(getLongInternal(r), getIntInternal(r), ZoneOffset.UTC);
        String ym = String.valueOf(ldt.getYear());
        ym = ym + "-" + Strings.padStart(String.valueOf(ldt.getMonthValue()), 2, '0');
        newColumn.append(ym);
      }
    }
    return newColumn;
  }

  /**
   * Returns a StringColumn with the year and day-of-year derived from this column concatenated into
   * a String that will sort lexicographically in temporal order.
   *
   * <p>This simplifies the production of plots and tables that aggregate values into standard
   * temporal units (e.g., you want monthly data but your source data is more than a year long and
   * you don't want months from different years aggregated together).
   */
  default StringColumn yearDay() {
    StringColumn newColumn = StringColumn.create(this.name() + " year & month");
    for (int r = 0; r < this.size(); r++) {
      long c1 = this.getLongInternal(r);
      if (DateTimeColumn.valueIsMissing(c1)) {
        newColumn.append(StringColumnType.missingValueIndicator());
      } else {
        LocalDateTime ldt =
            LocalDateTime.ofEpochSecond(getLongInternal(r), getIntInternal(r), ZoneOffset.UTC);
        String ym = String.valueOf(ldt.getYear());
        ym = ym + "-" + Strings.padStart(String.valueOf(ldt.getDayOfYear()), 3, '0');
        newColumn.append(ym);
      }
    }
    return newColumn;
  }

  /**
   * Returns a StringColumn with the year and week-of-year derived from this column concatenated
   * into a String that will sort lexicographically in temporal order.
   *
   * <p>This simplifies the production of plots and tables that aggregate values into standard
   * temporal units (e.g., you want monthly data but your source data is more than a year long and
   * you don't want months from different years aggregated together).
   */
  default StringColumn hourMinute() {
    StringColumn newColumn = StringColumn.create(this.name() + " hour & minute");
    for (int r = 0; r < this.size(); r++) {
      long c1 = this.getLongInternal(r);
      if (DateTimeColumn.valueIsMissing(c1)) {
        newColumn.append(StringColumnType.missingValueIndicator());
      } else {
        LocalDateTime ldt =
            LocalDateTime.ofEpochSecond(getLongInternal(r), getIntInternal(r), ZoneOffset.UTC);
        String hm = Strings.padStart(String.valueOf(ldt.getHour()), 2, '0');
        hm = hm + ":" + Strings.padStart(String.valueOf(ldt.getMinute()), 2, '0');
        newColumn.append(hm);
      }
    }
    return newColumn;
  }

  /**
   * Returns a StringColumn with the year and week-of-year derived from this column concatenated
   * into a String that will sort lexicographically in temporal order.
   *
   * <p>This simplifies the production of plots and tables that aggregate values into standard
   * temporal units (e.g., you want monthly data but your source data is more than a year long and
   * you don't want months from different years aggregated together).
   */
  default StringColumn yearWeek() {
    StringColumn newColumn = StringColumn.create(this.name() + " year & week");
    for (int r = 0; r < this.size(); r++) {
      long c1 = this.getLongInternal(r);
      if (DateTimeColumn.valueIsMissing(c1)) {
        newColumn.append(StringColumnType.missingValueIndicator());
      } else {
        LocalDateTime ldt =
            LocalDateTime.ofEpochSecond(getLongInternal(r), getIntInternal(r), ZoneOffset.UTC);
        String ym = String.valueOf(ldt.getYear());
        ym =
            ym
                + "-"
                + Strings.padStart(
                    String.valueOf(ldt.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)), 2, '0');
        newColumn.append(ym);
      }
    }
    return newColumn;
  }

  /** Returns a DateColumn containing the date portion of each dateTime in this DateTimeColumn */
  default DateColumn date() {
    DateColumn newColumn = DateColumn.create(this.name() + " date");
    for (int r = 0; r < this.size(); r++) {
      if (isMissing(r)) {
        newColumn.appendMissing();
      } else {
        LocalDateTime ldt =
            LocalDateTime.ofEpochSecond(getLongInternal(r), getIntInternal(r), ZoneOffset.UTC);
        newColumn.append(ldt.toLocalDate());
      }
    }
    return newColumn;
  }

  default IntColumn year() {
    IntColumn newColumn = IntColumn.create(this.name() + " year");
    for (int r = 0; r < this.size(); r++) {
      if (isMissing(r)) {
        newColumn.appendMissing();
      } else {
        LocalDateTime ldt =
            LocalDateTime.ofEpochSecond(getLongInternal(r), getIntInternal(r), ZoneOffset.UTC);
        newColumn.append(ldt.getYear());
      }
    }
    return newColumn;
  }

  default StringColumn dayOfWeek() {
    StringColumn newColumn = StringColumn.create(this.name() + " day of week", this.size());
    for (int r = 0; r < this.size(); r++) {
      long c1 = this.getLongInternal(r);
      if (!DateTimeColumn.valueIsMissing(c1)) {
        LocalDateTime ldt =
            LocalDateTime.ofEpochSecond(getLongInternal(r), getIntInternal(r), ZoneOffset.UTC);
        newColumn.set(r, ldt.getDayOfWeek().name());
      }
    }
    return newColumn;
  }

  default IntColumn dayOfWeekValue() {
    IntColumn newColumn = IntColumn.create(this.name() + " day of week value", this.size());
    for (int r = 0; r < this.size(); r++) {
      if (!isMissing(r)) {
        LocalDateTime ldt =
            LocalDateTime.ofEpochSecond(getLongInternal(r), getIntInternal(r), ZoneOffset.UTC);
        newColumn.set(r, (short) ldt.getDayOfWeek().getValue());
      }
    }
    return newColumn;
  }

  default IntColumn dayOfYear() {
    IntColumn newColumn = IntColumn.create(this.name() + " day of year", this.size());
    for (int r = 0; r < this.size(); r++) {
      if (!isMissing(r)) {
        LocalDateTime ldt =
            LocalDateTime.ofEpochSecond(getLongInternal(r), getIntInternal(r), ZoneOffset.UTC);
        newColumn.set(r, (short) ldt.getDayOfYear());
      }
    }
    return newColumn;
  }

  default IntColumn dayOfMonth() {
    IntColumn newColumn = IntColumn.create(this.name() + " day of month", size());
    for (int r = 0; r < this.size(); r++) {
      if (!isMissing(r)) {
        LocalDateTime ldt =
            LocalDateTime.ofEpochSecond(getLongInternal(r), getIntInternal(r), ZoneOffset.UTC);
        newColumn.set(r, ldt.getDayOfMonth());
      }
    }
    return newColumn;
  }

  /**
   * Returns a column containing integers representing the nth group (0-based) that a date falls
   * into.
   *
   * <p>Example: When Unit = ChronoUnit.DAY and n = 5, we form 5 day groups. a Date that is 2 days
   * after the start is assigned to the first ("0") group. A day 7 days after the start is assigned
   * to the second ("1") group.
   *
   * @param unit A ChronoUnit greater than or equal to a minute
   * @param n The number of units in each group.
   * @param start The starting point of the first group; group boundaries are offsets from this
   *     point
   */
  default LongColumn timeWindow(ChronoUnit unit, int n, LocalDateTime start) {
    String newColumnName = "" + n + " " + unit.toString() + " window [" + name() + "]";
    LongColumn numberColumn = LongColumn.create(newColumnName, size());
    for (int i = 0; i < size(); i++) {
      if (getLongInternal(i) == DateTimeColumnType.missingValueIndicator()) {
        numberColumn.set(i, DateTimeColumnType.missingValueIndicator());
        continue;
      }
      LocalDateTime ldt =
          LocalDateTime.ofEpochSecond(getLongInternal(i), getIntInternal(i), ZoneOffset.UTC);
      long result;
      switch (unit) {
        case MINUTES:
          result = ChronoUnit.MINUTES.between(start, ldt) / n;
          numberColumn.set(i, result);
          break;
        case HOURS:
          result = ChronoUnit.HOURS.between(start, ldt) / n;
          numberColumn.set(i, result);
          break;
        case DAYS:
          result = ChronoUnit.DAYS.between(start, ldt) / n;
          numberColumn.set(i, result);
          break;
        case WEEKS:
          result = ChronoUnit.WEEKS.between(start, ldt) / n;
          numberColumn.set(i, result);
          break;
        case MONTHS:
          result = ChronoUnit.MONTHS.between(start, ldt) / n;
          numberColumn.set(i, result);
          break;
        case YEARS:
          result = ChronoUnit.YEARS.between(start, ldt) / n;
          numberColumn.set(i, result);
          break;
        default:
          throw new UnsupportedTemporalTypeException(
              "The ChronoUnit " + unit + " is not supported for timeWindows on dates");
      }
    }
    numberColumn.setPrintFormatter(NumberColumnFormatter.ints());
    return numberColumn;
  }

  default IntColumn minute() {
    IntColumn newColumn = IntColumn.create(name() + "[" + "minute" + "]", size());
    for (int r = 0; r < size(); r++) {
      if (!isMissing(r)) {
        LocalDateTime ldt =
            LocalDateTime.ofEpochSecond(getLongInternal(r), getIntInternal(r), ZoneOffset.UTC);
        newColumn.set(r, ldt.getMinute());
      }
    }
    return newColumn;
  }

  default LongColumn timeWindow(ChronoUnit unit, int n) {
    return timeWindow(unit, n, min());
  }
}
