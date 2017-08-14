package tech.tablesaw.mapping;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.columns.DateTimeColumnUtils;
import tech.tablesaw.columns.packeddata.PackedLocalDateTime;

public interface DateTimeMapUtils extends DateTimeColumnUtils {

    default LongColumn differenceInMilliseconds(DateTimeColumn column2) {
        return difference(column2, ChronoUnit.MILLIS);
    }

    default LongColumn differenceInSeconds(DateTimeColumn column2) {
        return difference(column2, ChronoUnit.SECONDS);
    }

    default LongColumn differenceInMinutes(DateTimeColumn column2) {
        return difference(column2, ChronoUnit.MINUTES);
    }

    default LongColumn differenceInHours(DateTimeColumn column2) {
        return difference(column2, ChronoUnit.HOURS);
    }

    default LongColumn differenceInDays(DateTimeColumn column2) {
        return difference(column2, ChronoUnit.DAYS);
    }

    default LongColumn differenceInYears(DateTimeColumn column2) {
        return difference(column2, ChronoUnit.YEARS);
    }

    default LongColumn difference(DateTimeColumn column2, ChronoUnit unit) {
        LongColumn newColumn = new LongColumn(name() + " - " + column2.name());

        for (int r = 0; r < size(); r++) {
            long c1 = this.getLong(r);
            long c2 = column2.getLong(r);
            if (c1 == DateTimeColumn.MISSING_VALUE || c2 == DateTimeColumn.MISSING_VALUE) {
                newColumn.append(IntColumn.MISSING_VALUE);
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

    default ShortColumn hour() {
        ShortColumn newColumn = new ShortColumn(name() + "[" + "hour" + "]");
        for (int r = 0; r < size(); r++) {
            long c1 = getLong(r);
            if (c1 != DateTimeColumn.MISSING_VALUE) {
                newColumn.append(PackedLocalDateTime.getHour(c1));
            } else {
                newColumn.append(ShortColumn.MISSING_VALUE);
            }
        }
        return newColumn;
    }

    default ShortColumn minuteOfDay() {
        ShortColumn newColumn = new ShortColumn(name() + "[" + "minute-of-day" + "]");
        for (int r = 0; r < size(); r++) {
            long c1 = getLong(r);
            if (c1 != DateTimeColumn.MISSING_VALUE) {
                newColumn.append((short) PackedLocalDateTime.getMinuteOfDay(c1));
            } else {
                newColumn.append(ShortColumn.MISSING_VALUE);
            }
        }
        return newColumn;
    }

    default IntColumn secondOfDay() {
        IntColumn newColumn = new IntColumn(name() + "[" + "second-of-day" + "]");
        for (int r = 0; r < size(); r++) {
            long c1 = getLong(r);
            if (c1 != DateTimeColumn.MISSING_VALUE) {
                newColumn.append(PackedLocalDateTime.getSecondOfDay(c1));
            } else {
                newColumn.append(IntColumn.MISSING_VALUE);
            }
        }
        return newColumn;
    }

    LocalDateTime get(int r);

    long getLong(int r);
}
