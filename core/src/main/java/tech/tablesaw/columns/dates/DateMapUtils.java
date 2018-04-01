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

package tech.tablesaw.columns.dates;

import com.google.common.base.Preconditions;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.datetimes.PackedLocalDateTime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import static tech.tablesaw.api.DateColumn.*;

/**
 * An interface for mapping operations unique to Date columns
 */
public interface DateMapUtils extends Column {

    static String dateColumnName(Column column1, int value, TemporalUnit unit) {
        return column1.name() + ": " + value + " " + unit.toString() + "(s)";
    }

    default NumberColumn daysUntil(DateColumn column2) {
        return timeUntil(column2, ChronoUnit.DAYS);
    }

    default NumberColumn weeksUntil(DateColumn column2) {
        return timeUntil(column2, ChronoUnit.WEEKS);
    }

    default NumberColumn monthsUntil(DateColumn column2) {
        return timeUntil(column2, ChronoUnit.MONTHS);
    }

    default NumberColumn yearsUntil(DateColumn column2) {
        return timeUntil(column2, ChronoUnit.YEARS);
    }

    default NumberColumn dayOfMonth() {
        NumberColumn newColumn = NumberColumn.create(this.name() + " day of month");
        for (int r = 0; r < this.size(); r++) {
            int c1 = this.getIntInternal(r);
            if (DateColumn.isMissing(c1)) {
                newColumn.append(NumberColumn.MISSING_VALUE);
            } else {
                newColumn.append(PackedLocalDate.getDayOfMonth(c1));
            }
        }
        return newColumn;
    }

    default NumberColumn dayOfYear() {
        NumberColumn newColumn = NumberColumn.create(this.name() + " day of year");
        for (int r = 0; r < this.size(); r++) {
            int c1 = this.getIntInternal(r);
            if (DateColumn.isMissing(c1)) {
                newColumn.append(NumberColumn.MISSING_VALUE);
            } else {
                newColumn.append((short) PackedLocalDate.getDayOfYear(c1));
            }
        }
        return newColumn;
    }

    default NumberColumn monthValue() {
        NumberColumn newColumn = NumberColumn.create(this.name() + " month");

        for (int r = 0; r < this.size(); r++) {
            int c1 = this.getIntInternal(r);
            if (DateColumn.isMissing(c1)) {
                newColumn.append(NumberColumn.MISSING_VALUE);
            } else {
                newColumn.append(PackedLocalDate.getMonthValue(c1));
            }
        }
        return newColumn;
    }

    default StringColumn month() {
        StringColumn newColumn = StringColumn.create(this.name() + " month");

        for (int r = 0; r < this.size(); r++) {
            int c1 = this.getIntInternal(r);
            if (DateColumn.isMissing(c1)) {
                newColumn.append(StringColumn.MISSING_VALUE);
            } else {
                newColumn.append(PackedLocalDate.getMonth(c1).name());
            }
        }
        return newColumn;
    }

    default NumberColumn year() {
        NumberColumn newColumn = NumberColumn.create(this.name() + " year");
        for (int r = 0; r < this.size(); r++) {
            int c1 = this.getIntInternal(r);
            if (DateColumn.isMissing(c1)) {
                newColumn.append(NumberColumn.MISSING_VALUE);
            } else {
                newColumn.append(PackedLocalDate.getYear(c1));
            }
        }
        return newColumn;
    }


    default NumberColumn dayOfWeekValue() {
        NumberColumn newColumn = NumberColumn.create(this.name() + " day of week", this.size());
        for (int r = 0; r < this.size(); r++) {
            int c1 = this.getIntInternal(r);
            if (DateColumn.isMissing(c1)) {
                newColumn.set(r, NumberColumn.MISSING_VALUE);
            } else {
                newColumn.append((short) PackedLocalDate.getDayOfWeek(c1).getValue());
            }
        }
        return newColumn;
    }

    default StringColumn dayOfWeek() {
        StringColumn newColumn = StringColumn.create(this.name() + " day of week");
        for (int r = 0; r < this.size(); r++) {
            int c1 = this.getIntInternal(r);
            if (DateColumn.isMissing(c1)) {
                newColumn.append(StringColumn.MISSING_VALUE);
            } else {
                newColumn.append(PackedLocalDate.getDayOfWeek(c1).toString());
            }
        }
        return newColumn;
    }

    /**
     * Calculates the temporal difference between each element of the receiver and the respective element of the
     * argument
     * <p>
     * Missing values in either result in a Missing Value for the new column
     */
    default NumberColumn timeUntil(DateColumn end, ChronoUnit unit) {

        NumberColumn newColumn = NumberColumn.create(name() + " - " + end.name());
        for (int r = 0; r < size(); r++) {
            int c1 = getIntInternal(r);
            int c2 = end.getIntInternal(r);
            if (DateColumn.isMissing(c1) || DateColumn.isMissing(c2)) {
                newColumn.append(NumberColumn.MISSING_VALUE);
            } else {
                switch (unit) {
                    case DAYS:
                        newColumn.append(PackedLocalDate.daysUntil(c2, c1));
                        break;
                    case WEEKS:
                        newColumn.append(PackedLocalDate.weeksUntil(c2, c1));
                        break;
                    default:   //TODO implement in PackedLocalDate
                        LocalDate value1 = PackedLocalDate.asLocalDate(c1);
                        LocalDate value2 = PackedLocalDate.asLocalDate(c2);
                        newColumn.append(unit.between(value1, value2));
                        break;
                }
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

    // These functions subtract some amount of time from a date, producing a new date column

    default DateColumn plusMonths(int months) {
        return plus(months, ChronoUnit.MONTHS);
    }

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

    default DateColumn plus(int value, ChronoUnit unit) {

        DateColumn newColumn = DateColumn.create(dateColumnName(this, value, unit));
        DateColumn column1 = (DateColumn) this;

        for (int r = 0; r < column1.size(); r++) {
            int packedDate = column1.getPackedDate(r);
            if (packedDate == MISSING_VALUE) {
                newColumn.appendInternal(MISSING_VALUE);
            } else {
                newColumn.appendInternal(PackedLocalDate.plus(value, unit, packedDate));
            }
        }
        return newColumn;
    }

    // misc functions
    default DateColumn minus(int value, ChronoUnit unit) {
        return plus(-value, unit);
    }

    default DateTimeColumn atStartOfDay() {
        DateTimeColumn newColumn = DateTimeColumn.create(this.name() + " " + " start");
        for (int r = 0; r < this.size(); r++) {
            LocalDate c1 = this.get(r);
            if (c1 == null) {
                newColumn.add(null);
            } else {
                newColumn.add(c1.atStartOfDay());
            }
        }
        return newColumn;
    }

    /**
     * Returns a DateTime column where each value consists of the dates from this column combined with the corresponding
     * times from the other column
     */
    default DateTimeColumn atTime(LocalTime time) {
        Preconditions.checkNotNull(time);
        DateTimeColumn newColumn = DateTimeColumn.create(this.name() + " " + time.toString());
        for (int r = 0; r < this.size(); r++) {
            int c1 = this.getIntInternal(r);
            if (DateColumn.isMissing(c1)) {
                newColumn.appendInternal(DateTimeColumn.MISSING_VALUE);
            } else {
                LocalDate value1 = PackedLocalDate.asLocalDate(c1);
                newColumn.appendInternal(PackedLocalDateTime.pack(value1, time));
            }
        }
        return newColumn;
    }

    /**
     * Returns a DateTime column where each value consists of the dates from this column combined with the corresponding
     * times from the other column
     */
    default DateTimeColumn atTime(TimeColumn timeColumn) {
        DateTimeColumn newColumn = DateTimeColumn.create(this.name() + " " + timeColumn.name());
        for (int r = 0; r < this.size(); r++) {
            int c1 = this.getIntInternal(r);
            int c2 = timeColumn.getIntInternal(r);
            if (DateColumn.isMissing(c1) || DateColumn.isMissing(c2)) {
                newColumn.appendInternal(DateTimeColumn.MISSING_VALUE);
            } else {
                newColumn.appendInternal(PackedLocalDateTime.create(c1, c2));
            }
        }
        return newColumn;
    }

    int getIntInternal(int r);

    LocalDate get(int index);
}
