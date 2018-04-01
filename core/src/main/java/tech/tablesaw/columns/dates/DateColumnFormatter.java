package tech.tablesaw.columns.dates;

import javax.annotation.concurrent.Immutable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static tech.tablesaw.api.DateColumn.*;
import static tech.tablesaw.columns.dates.PackedLocalDate.*;

@Immutable
public class DateColumnFormatter {

    private DateTimeFormatter format;
    private String missingString = "";

    public DateColumnFormatter() {
        this.format = null;
    }

    public DateColumnFormatter(DateTimeFormatter format) {
        this.format = format;
    }

    public DateColumnFormatter(DateTimeFormatter format, String missingString) {
        this.format = format;
        this.missingString = missingString;
    }

    public String format(int value) {
        if (value == MISSING_VALUE) {
            return missingString;
        }
        if (format == null) {
            return PackedLocalDate.toDateString(value);
        }
        LocalDate date = asLocalDate(value);
        return format.format(date);
    }

    @Override
    public String toString() {
        return "DateColumnFormatter{" +
                "format=" + format +
                ", missingString='" + missingString + '\'' +
                '}';
    }
}
