package tech.tablesaw.columns.times;

import javax.annotation.concurrent.Immutable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static tech.tablesaw.api.TimeColumn.*;
import static tech.tablesaw.columns.times.PackedLocalTime.*;

@Immutable
public class TimeColumnFormatter {

    private final DateTimeFormatter format;
    private String missingString = "";

    public TimeColumnFormatter() {
        this.format = null;
    }

    public TimeColumnFormatter(DateTimeFormatter format) {
        this.format = format;
    }

    public TimeColumnFormatter(DateTimeFormatter format, String missingString) {
        this.format = format;
        this.missingString = missingString;
    }

    public String format(int value) {
        if (value == MISSING_VALUE) {
            return missingString;
        }
        if (format == null) {
            return toShortTimeString(value);
        }
        LocalTime time = asLocalTime(value);
        if (time == null) {
            return "";
        }
        return format.format(time);
    }

    @Override
    public String toString() {
        return "TimeColumnFormatter{" +
                "format=" + format +
                ", missingString='" + missingString + '\'' +
                '}';
    }
}
