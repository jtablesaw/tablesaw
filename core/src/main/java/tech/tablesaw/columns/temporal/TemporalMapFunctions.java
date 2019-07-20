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

package tech.tablesaw.columns.temporal;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.booleans.BooleanColumnType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

import static tech.tablesaw.columns.datetimes.PackedLocalDateTime.asLocalDateTime;

public interface TemporalMapFunctions<T extends Temporal> extends TemporalColumn<T> {

    T min();

    TemporalColumn<T> emptyCopy();

    default LongColumn differenceInMilliseconds(TemporalColumn<T> column2) {
        return difference(column2, ChronoUnit.MILLIS);
    }

    default LongColumn differenceInSeconds(TemporalColumn<T> column2) {
        return difference(column2, ChronoUnit.SECONDS);
    }

    default LongColumn differenceInMinutes(TemporalColumn<T> column2) {
        return difference(column2, ChronoUnit.MINUTES);
    }

    default LongColumn differenceInHours(TemporalColumn<T> column2) {
        return difference(column2, ChronoUnit.HOURS);
    }

    default LongColumn differenceInDays(TemporalColumn<T> column2) {
        return difference(column2, ChronoUnit.DAYS);
    }

    default LongColumn differenceInYears(TemporalColumn<T> column2) {
        return difference(column2, ChronoUnit.YEARS);
    }

    default LongColumn difference(TemporalColumn<T> column2, ChronoUnit unit) {

        LongColumn newColumn = LongColumn.create(name() + " - " + column2.name() + "[" + unit.name() + "]");

        for (int r = 0; r < size(); r++) {
            if (this.isMissing(r) || column2.isMissing(r)) {
                newColumn.appendMissing();
            } else {
                long c1 = this.getLongInternal(r);
                long c2 = column2.getLongInternal(r);
                LocalDateTime value1 = asLocalDateTime(c1);
                LocalDateTime value2 = asLocalDateTime(c2);
                if (value1 != null && value2 != null) {
                    newColumn.append(unit.between(value1, value2));
                } else {
                    newColumn.appendMissing();
                }
            }
        }
        return newColumn;
    }
    
    Column<T> plus(long amountToAdd, ChronoUnit unit);

    default Column<T> plusYears(long amountToAdd) {
        return plus(amountToAdd, ChronoUnit.YEARS);
    }

    default Column<T> plusMonths(long amountToAdd) {
        return plus(amountToAdd, ChronoUnit.MONTHS);
    }

    default Column<T> plusWeeks(long amountToAdd) {
        return plus(amountToAdd, ChronoUnit.WEEKS);
    }

    default Column<T> plusDays(long amountToAdd) {
        return plus(amountToAdd, ChronoUnit.DAYS);
    }

    default Column<T> plusHours(long amountToAdd) {
        return plus(amountToAdd, ChronoUnit.HOURS);
    }

    default Column<T> plusMinutes(long amountToAdd) {
        return plus(amountToAdd, ChronoUnit.MINUTES);
    }

    default Column<T> plusSeconds(long amountToAdd) {
        return plus(amountToAdd, ChronoUnit.SECONDS);
    }

    default Column<T> plusMillis(long amountToAdd) {
        return plus(amountToAdd, ChronoUnit.MILLIS);
    }

    default Column<T> plusMicros(long amountToAdd) {
        return plus(amountToAdd, ChronoUnit.MICROS);
    }

    default BooleanColumn missingValues() {
        BooleanColumn newColumn = BooleanColumn.create(this.name() + " missing?");
        for (int r = 0; r < this.size(); r++) {
            if (isMissing(r)) {
                newColumn.append(BooleanColumnType.BYTE_TRUE);
            } else {
                newColumn.append(BooleanColumnType.BYTE_FALSE);
            }
        }
        return newColumn;
    }

    default String temporalColumnName(Column<T> column1, long value, TemporalUnit unit) {
        return column1.name() + ": " + value + " " + unit.toString() + "(s)";
    }
}
