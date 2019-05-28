package tech.tablesaw.columns.instant;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.concurrent.Immutable;

import tech.tablesaw.columns.times.TimeColumnType;

@Immutable
public class InstantColumnFormatter {

    private final DateTimeFormatter format;
    private final ZoneId zoneId;
    private String missingValueString = "";

    public InstantColumnFormatter() {
        this.format = null;
        this.zoneId = ZoneOffset.UTC;
    }

    public InstantColumnFormatter(ZoneId zoneId) {
        this.format = null;
        this.zoneId = zoneId;
    }

    public InstantColumnFormatter(DateTimeFormatter format) {
        this.format = format;
        this.zoneId = ZoneOffset.UTC;
    }

    public InstantColumnFormatter(DateTimeFormatter format, ZoneId zoneId) {
        this.format = format;
        this.zoneId = zoneId;
    }

    public InstantColumnFormatter(DateTimeFormatter format, String missingValueString) {
        this.format = format;
        this.missingValueString = missingValueString;
        this.zoneId = ZoneOffset.UTC;
    }

    public InstantColumnFormatter(DateTimeFormatter format, ZoneId zoneId, String missingValueString) {
        this.format = format;
        this.missingValueString = missingValueString;
        this.zoneId = zoneId;
    }

    public String format(long value) {
        if (value == TimeColumnType.missingValueIndicator()) {
            return missingValueString;
        }
        if (format == null) {
            return PackedInstant.toString(value);
        }
        ZonedDateTime time = PackedInstant.asInstant(value).atZone(zoneId);
        if (time == null) {
            return "";
        }
        return format.format(time);
    }

    @Override
    public String toString() {
        return "InstantColumnFormatter{" +
                "format=" + format +
                ", missingValueString='" + missingValueString + '\'' +
                '}';
    }
}
