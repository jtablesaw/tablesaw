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

package tech.tablesaw.mapping;

import com.google.common.base.Preconditions;

import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.DateColumnUtils;
import tech.tablesaw.columns.packeddata.PackedLocalDate;
import tech.tablesaw.columns.packeddata.PackedLocalDateTime;
import tech.tablesaw.columns.packeddata.PackedLocalTime;

import static tech.tablesaw.api.DateColumn.MISSING_VALUE;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * An interface for mapping operations unique to Date columns
 */
public interface DateMapUtils extends DateColumnUtils {

    static String dateColumnName(Column column1, int value, TemporalUnit unit) {
        return column1.name() + ": " + value + " " + unit.toString() + "(s)";
    }

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

    /**
     * Calculates the temporal difference between each element of the receiver and the respective element of the
     * argument
     * <p>
     * Missing values in either result in a Missing Value for the new column
     */
    default FloatColumn difference(DateColumn column1, DateColumn column2, ChronoUnit unit) {

        FloatColumn newColumn = new FloatColumn(column1.name() + " - " + column2.name());
        for (int r = 0; r < column1.size(); r++) {
            int c1 = column1.getIntInternal(r);
            int c2 = column2.getIntInternal(r);
            if (c1 == FloatColumn.MISSING_VALUE || c2 == FloatColumn.MISSING_VALUE) {
                newColumn.append(FloatColumn.MISSING_VALUE);
            } else {
                LocalDate value1 = PackedLocalDate.asLocalDate(c1);
                LocalDate value2 = PackedLocalDate.asLocalDate(c2);
                newColumn.append(unit.between(value1, value2));
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

    default DateColumn plus(int value, TemporalUnit unit) {

        DateColumn newColumn = new DateColumn(dateColumnName(this, value, unit));
        DateColumn column1 = (DateColumn) this;

        for (int r = 0; r < column1.size(); r++) {
            LocalDate c1 = column1.get(r);
            if (c1 == null) {
                newColumn.append(c1);
            } else {
                newColumn.append(c1.plus(value, unit));
            }
        }
        return newColumn;
    }

    // misc functions

    default DateColumn minus(int value, TemporalUnit unit) {
        DateColumn column1 = (DateColumn) this;
        DateColumn newColumn = new DateColumn(dateColumnName(column1, value, unit));
        for (int r = 0; r < column1.size(); r++) {
            LocalDate c1 = column1.get(r);
            if (c1 == null) {
                newColumn.append(c1);
            } else {
                newColumn.append(c1.minus(value, unit));
            }
        }
        return newColumn;
    }

    default DateTimeColumn atStartOfDay() {
        DateTimeColumn newColumn = new DateTimeColumn(this.name() + " " + " start");
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
        DateTimeColumn newColumn = new DateTimeColumn(this.name() + " " + time.toString());
        for (int r = 0; r < this.size(); r++) {
            int c1 = this.getIntInternal(r);
            if (c1 == MISSING_VALUE) {
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
        DateTimeColumn newColumn = new DateTimeColumn(this.name() + " " + timeColumn.name());
        for (int r = 0; r < this.size(); r++) {
            int c1 = this.getIntInternal(r);
            int c2 = timeColumn.getIntInternal(r);
            if (c1 == MISSING_VALUE || c2 == TimeColumn.MISSING_VALUE) {
                newColumn.appendInternal(DateTimeColumn.MISSING_VALUE);
            } else {
                LocalDate value1 = PackedLocalDate.asLocalDate(c1);
                newColumn.appendInternal(PackedLocalDateTime.pack(value1, PackedLocalTime.asLocalTime(c2)));
            }
        }
        return newColumn;
    }

    int getIntInternal(int r);

    LocalDate get(int index);
}
