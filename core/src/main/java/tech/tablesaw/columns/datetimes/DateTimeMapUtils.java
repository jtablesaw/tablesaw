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

import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.columns.Column;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public interface DateTimeMapUtils extends Column {

    default NumberColumn differenceInMilliseconds(DateTimeColumn column2) {
        return difference(column2, ChronoUnit.MILLIS);
    }

    default NumberColumn differenceInSeconds(DateTimeColumn column2) {
        return difference(column2, ChronoUnit.SECONDS);
    }

    default NumberColumn differenceInMinutes(DateTimeColumn column2) {
        return difference(column2, ChronoUnit.MINUTES);
    }

    default NumberColumn differenceInHours(DateTimeColumn column2) {
        return difference(column2, ChronoUnit.HOURS);
    }

    default NumberColumn differenceInDays(DateTimeColumn column2) {
        return difference(column2, ChronoUnit.DAYS);
    }

    default NumberColumn differenceInYears(DateTimeColumn column2) {
        return difference(column2, ChronoUnit.YEARS);
    }

    default NumberColumn difference(DateTimeColumn column2, ChronoUnit unit) {
        NumberColumn newColumn = NumberColumn.create(name() + " - " + column2.name());

        for (int r = 0; r < size(); r++) {
            long c1 = this.getLongInternal(r);
            long c2 = column2.getLongInternal(r);
            if (c1 == DateTimeColumn.MISSING_VALUE || c2 == DateTimeColumn.MISSING_VALUE) {
                newColumn.append(DateTimeColumn.MISSING_VALUE);
            } else {
                newColumn.append(difference(c1, c2, unit));
            }
        }
        return newColumn;
    }

    default long difference(long packedLocalDateTime1, long packedLocalDateTime2, ChronoUnit unit) {
        LocalDateTime value1 = PackedLocalDateTime.asLocalDateTime(packedLocalDateTime1);
        LocalDateTime value2 = PackedLocalDateTime.asLocalDateTime(packedLocalDateTime2);
        return unit.between(value1, value2);
    }

    default NumberColumn hour() {
        NumberColumn newColumn = NumberColumn.create(name() + "[" + "hour" + "]");
        for (int r = 0; r < size(); r++) {
            long c1 = getLongInternal(r);
            if (c1 != DateTimeColumn.MISSING_VALUE) {
                newColumn.append(PackedLocalDateTime.getHour(c1));
            } else {
                newColumn.append(NumberColumn.MISSING_VALUE);
            }
        }
        return newColumn;
    }

    default NumberColumn minuteOfDay() {
        NumberColumn newColumn = NumberColumn.create(name() + "[" + "minute-of-day" + "]");
        for (int r = 0; r < size(); r++) {
            long c1 = getLongInternal(r);
            if (c1 != DateTimeColumn.MISSING_VALUE) {
                newColumn.append((short) PackedLocalDateTime.getMinuteOfDay(c1));
            } else {
                newColumn.append(NumberColumn.MISSING_VALUE);
            }
        }
        return newColumn;
    }

    default NumberColumn secondOfDay() {
        NumberColumn newColumn = NumberColumn.create(name() + "[" + "second-of-day" + "]");
        for (int r = 0; r < size(); r++) {
            long c1 = getLongInternal(r);
            if (c1 != DateTimeColumn.MISSING_VALUE) {
                newColumn.append(PackedLocalDateTime.getSecondOfDay(c1));
            } else {
                newColumn.append(NumberColumn.MISSING_VALUE);
            }
        }
        return newColumn;
    }

    LocalDateTime get(int r);

    long getLongInternal(int r);
}
