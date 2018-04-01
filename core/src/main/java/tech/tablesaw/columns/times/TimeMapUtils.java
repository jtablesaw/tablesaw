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

package tech.tablesaw.columns.times;

import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.UnsupportedTemporalTypeException;

import static java.time.temporal.ChronoUnit.*;
import static tech.tablesaw.api.TimeColumn.*;

public interface TimeMapUtils extends Column {

    default NumberColumn differenceInMilliseconds(TimeColumn column2) {
        return difference(column2, MILLIS);
    }

    default NumberColumn differenceInSeconds(TimeColumn column2) {
        return difference(column2, SECONDS);
    }

    default NumberColumn differenceInMinutes(TimeColumn column2) {
        return difference(column2, MINUTES);
    }

    default NumberColumn differenceInHours(TimeColumn column2) {
        return difference(column2, HOURS);
    }


    default TimeColumn lead(int n) {
        TimeColumn column = lag(-n);
        column.setName(name() + " lead(" + n + ")");
        return column;
    }

    TimeColumn lag(int n);

    default NumberColumn difference(TimeColumn column2, ChronoUnit unit) {
        NumberColumn newColumn = NumberColumn.create(name() + " - " + column2.name());

        for (int r = 0; r < size(); r++) {
            int c1 = this.getIntInternal(r);
            int c2 = column2.getIntInternal(r);
            if (TimeColumn.isMissing(c1) || TimeColumn.isMissing(c2)) {
                newColumn.append(NumberColumn.MISSING_VALUE);
            } else {
                newColumn.append((int) difference(c1, c2, unit));
            }
        }
        return newColumn;
    }

    default TimeColumn plus(int time, ChronoUnit unit) {
        TimeColumn newColumn = TimeColumn.create("");
        String timeUnitString = "";
        for (int r = 0; r < size(); r++) {
            int c1 = this.getIntInternal(r);
            if (TimeColumn.isMissing(c1)) {
                newColumn.appendInternal(MISSING_VALUE);
            } else {
                switch (unit) {
                    case HOURS:
                        newColumn.appendInternal(PackedLocalTime.plusHours(time, c1));
                        timeUnitString = "hours";
                        break;
                    case MINUTES:
                        newColumn.appendInternal(PackedLocalTime.plusMinutes(time, c1));
                        timeUnitString = "minutes";
                        break;
                    case SECONDS:
                        newColumn.appendInternal(PackedLocalTime.plusSeconds(time, c1));
                        timeUnitString = "seconds";
                        break;
                    case MILLIS:
                        newColumn.appendInternal(PackedLocalTime.plusMilliseconds(time, c1));
                        timeUnitString = "ms";
                        break;
                    default:
                        throw new UnsupportedTemporalTypeException("Type " + unit + " is not currently supported");

                }
            }
        }
        newColumn.setName(name() + " + " + time + " " + timeUnitString + "(s)");
        return newColumn;
    }

    default TimeColumn minus(int time, ChronoUnit unit) {
        TimeColumn newColumn = TimeColumn.create("");
        String timeUnitString = "";
        for (int r = 0; r < size(); r++) {
            int c1 = this.getIntInternal(r);
            if (TimeColumn.isMissing(c1)) {
                newColumn.appendInternal(MISSING_VALUE);
            } else {
                switch (unit) {
                    case HOURS:
                        newColumn.appendInternal(PackedLocalTime.minusHours(time, c1));
                        timeUnitString = "hours";
                        break;
                    case MINUTES:
                        newColumn.appendInternal(PackedLocalTime.minusMinutes(time, c1));
                        timeUnitString = "minutes";
                        break;
                    case SECONDS:
                        newColumn.appendInternal(PackedLocalTime.minusSeconds(time, c1));
                        timeUnitString = "seconds";
                        break;
                    case MILLIS:
                        newColumn.appendInternal(PackedLocalTime.minusMilliseconds(time, c1));
                        timeUnitString = "ms";
                        break;
                    default:
                        throw new UnsupportedTemporalTypeException("Type " + unit + " is not currently supported");
                }
            }
        }
        newColumn.setName(name() + " - " + time + " " + timeUnitString + "(s)");
        return newColumn;
    }

    default TimeColumn with(int time, ChronoUnit unit) {
        TimeColumn newColumn = TimeColumn.create("");
        String timeUnitString = "";
        for (int r = 0; r < size(); r++) {
            int c1 = this.getIntInternal(r);
            if (TimeColumn.isMissing(c1)) {
                newColumn.appendInternal(MISSING_VALUE);
            } else {
                switch (unit) {
                    case HOURS:
                        newColumn.appendInternal(PackedLocalTime.withHour(time, c1));
                        timeUnitString = "hours";
                        break;
                    case MINUTES:
                        newColumn.appendInternal(PackedLocalTime.withMinute(time, c1));
                        timeUnitString = "minutes";
                        break;
                    case SECONDS:
                        newColumn.appendInternal(PackedLocalTime.withSecond(time, c1));
                        timeUnitString = "seconds";
                        break;
                    case MILLIS:
                        newColumn.appendInternal(PackedLocalTime.withMillisecond(time, c1));
                        timeUnitString = "ms";
                        break;
                    default:
                        throw new UnsupportedTemporalTypeException("Type " + unit + " is not currently supported");
                }
            }
        }
        newColumn.setName(name() + " with " + time + " " + timeUnitString + "(s)");
        return newColumn;
    }

    default long difference(int packedLocalTime1, int packedLocalTime2, ChronoUnit unit) {
        LocalTime value1 = PackedLocalTime.asLocalTime(packedLocalTime1);
        LocalTime value2 = PackedLocalTime.asLocalTime(packedLocalTime2);
        return unit.between(value1, value2);
    }

    default TimeColumn withHour(int hours) {
        return with(hours, HOURS);
    }

    default TimeColumn plusHours(int hours) {
        return plus(hours, HOURS);
    }

    default TimeColumn withMinute(int minutes) {
        return with(minutes, MINUTES);
    }

    default TimeColumn plusMinutes(int minutes) {
        return plus(minutes, MINUTES);
    }

    default TimeColumn withSecond(int seconds) {
        return with(seconds, SECONDS);
    }

    default TimeColumn plusSeconds(int seconds) {
        return plus(seconds, SECONDS);
    }

    default TimeColumn withMillisecond(int millis) {
        return with(millis, MILLIS);
    }

    default TimeColumn plusMilliseconds(int millis) {
        return plus(millis, MILLIS);
    }

    default TimeColumn minusHours(int hours) {
        return minus(hours, HOURS);
    }

    default TimeColumn minusMinutes(int minutes) {
        return minus(minutes, MINUTES);
    }

    default TimeColumn minusSeconds(int seconds) {
        return minus(seconds, SECONDS);
    }

    default TimeColumn minusMilliseconds(int millis) {
        return minus(millis, MILLIS);
    }

    default TimeColumn truncatedTo(ChronoUnit unit) {
        TimeColumn newColumn = TimeColumn.create("");
        for (int r = 0; r < size(); r++) {
            int c1 = this.getIntInternal(r);
            if (TimeColumn.isMissing(c1)) {
                newColumn.appendInternal(MISSING_VALUE);
            } else {
                newColumn.appendInternal(PackedLocalTime.truncatedTo(unit, c1));
            }
        }
        return newColumn;
    }

    default NumberColumn hour() {
        NumberColumn newColumn = NumberColumn.create(name() + "[" + "hour" + "]");
        for (int r = 0; r < size(); r++) {
            int c1 = getIntInternal(r);
            if (!TimeColumn.isMissing(c1)) {
                newColumn.append(PackedLocalTime.getHour(c1));
            } else {
                newColumn.append(NumberColumn.MISSING_VALUE);
            }
        }
        return newColumn;
    }

    default NumberColumn minute() {
        NumberColumn newColumn = NumberColumn.create(name() + "[" + "minute" + "]");
        for (int r = 0; r < size(); r++) {
            int c1 = getIntInternal(r);
            if (!TimeColumn.isMissing(c1)) {
                newColumn.append(PackedLocalTime.getMinute(c1));
            } else {
                newColumn.append(NumberColumn.MISSING_VALUE);
            }
        }
        return newColumn;
    }

    default NumberColumn second() {
        NumberColumn newColumn = NumberColumn.create(name() + "[" + "second" + "]");
        for (int r = 0; r < size(); r++) {
            int c1 = getIntInternal(r);
            if (!TimeColumn.isMissing(c1)) {
                newColumn.append(PackedLocalTime.getSecond(c1));
            } else {
                newColumn.append(NumberColumn.MISSING_VALUE);
            }
        }
        return newColumn;
    }

    default NumberColumn milliseconds() {
        NumberColumn newColumn = NumberColumn.create(name() + "[" + "ms" + "]");
        for (int r = 0; r < size(); r++) {
            int c1 = getIntInternal(r);
            if (!TimeColumn.isMissing(c1)) {
                newColumn.append(PackedLocalTime.getMilliseconds(c1));
            } else {
                newColumn.append(NumberColumn.MISSING_VALUE);
            }
        }
        return newColumn;
    }

    default NumberColumn minuteOfDay() {
        NumberColumn newColumn = NumberColumn.create(name() + "[" + "minute-of-day" + "]");
        for (int r = 0; r < size(); r++) {
            int c1 = getIntInternal(r);
            if (!TimeColumn.isMissing(c1)) {
                newColumn.append(PackedLocalTime.getMinuteOfDay(c1));
            } else {
                newColumn.append(NumberColumn.MISSING_VALUE);
            }
        }
        return newColumn;
    }

    default NumberColumn secondOfDay() {
        NumberColumn newColumn = NumberColumn.create(name() + "[" + "second-of-day" + "]");
        for (int r = 0; r < size(); r++) {
            int c1 = getIntInternal(r);
            if (!TimeColumn.isMissing(c1)) {
                newColumn.append(PackedLocalTime.getSecondOfDay(c1));
            } else {
                newColumn.append(NumberColumn.MISSING_VALUE);
            }
        }
        return newColumn;
    }

    LocalTime get(int r);

    int getIntInternal(int r);
}
