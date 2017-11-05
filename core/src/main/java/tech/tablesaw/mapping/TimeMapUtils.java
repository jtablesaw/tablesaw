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

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.TimeColumnUtils;
import tech.tablesaw.columns.packeddata.PackedLocalTime;

public interface TimeMapUtils extends TimeColumnUtils {

    default LongColumn differenceInMilliseconds(TimeColumn column2) {
        return difference(column2, ChronoUnit.MILLIS);
    }

    default LongColumn differenceInSeconds(TimeColumn column2) {
        return difference(column2, ChronoUnit.SECONDS);
    }

    default LongColumn differenceInMinutes(TimeColumn column2) {
        return difference(column2, ChronoUnit.MINUTES);
    }

    default LongColumn differenceInHours(TimeColumn column2) {
        return difference(column2, ChronoUnit.HOURS);
    }

    default LongColumn difference(TimeColumn column2, ChronoUnit unit) {
        LongColumn newColumn = new LongColumn(name() + " - " + column2.name());

        for (int r = 0; r < size(); r++) {
            int c1 = this.getIntInternal(r);
            int c2 = column2.getIntInternal(r);
            if (c1 == TimeColumn.MISSING_VALUE || c2 == TimeColumn.MISSING_VALUE) {
                newColumn.append(IntColumn.MISSING_VALUE);
            } else {
                newColumn.append(difference(c1, c2, unit));
            }
        }
        return newColumn;
    }

    default long difference(int packedLocalTime1, int packedLocalTime2, ChronoUnit unit) {
        LocalTime value1 = PackedLocalTime.asLocalTime(packedLocalTime1);
        LocalTime value2 = PackedLocalTime.asLocalTime(packedLocalTime2);
        return unit.between(value1, value2);
    }

    default ShortColumn hour() {
        ShortColumn newColumn = new ShortColumn(name() + "[" + "hour" + "]");
        for (int r = 0; r < size(); r++) {
            int c1 = getIntInternal(r);
            if (c1 != TimeColumn.MISSING_VALUE) {
                newColumn.append(PackedLocalTime.getHour(c1));
            } else {
                newColumn.append(ShortColumn.MISSING_VALUE);
            }
        }
        return newColumn;
    }

    default IntColumn minuteOfDay() {
        IntColumn newColumn = new IntColumn(name() + "[" + "minute-of-day" + "]");
        for (int r = 0; r < size(); r++) {
            int c1 = getIntInternal(r);
            if (c1 != TimeColumn.MISSING_VALUE) {
                newColumn.append(PackedLocalTime.getMinuteOfDay(c1));
            } else {
                newColumn.append(IntColumn.MISSING_VALUE);
            }
        }
        return newColumn;
    }

    default IntColumn secondOfDay() {
        IntColumn newColumn = new IntColumn(name() + "[" + "second-of-day" + "]");
        for (int r = 0; r < size(); r++) {
            int c1 = getIntInternal(r);
            if (c1 != TimeColumn.MISSING_VALUE) {
                newColumn.append(PackedLocalTime.getSecondOfDay(c1));
            } else {
                newColumn.append(IntColumn.MISSING_VALUE);
            }
        }
        return newColumn;
    }

    LocalTime get(int r);

    int getIntInternal(int r);
}
