package tech.tablesaw.columns.times;

import static tech.tablesaw.columns.times.PackedLocalTime.asLocalTime;
import static tech.tablesaw.columns.times.PackedLocalTime.toShortTimeString;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.concurrent.Immutable;
import tech.tablesaw.columns.TemporalColumnFormatter;

@Immutable
public class TimeColumnFormatter extends TemporalColumnFormatter {

  public TimeColumnFormatter(DateTimeFormatter format) {
    super(format);
  }

  public TimeColumnFormatter() {
    super();
  }

  public TimeColumnFormatter(DateTimeFormatter format, String missingValueString) {
    super(format, missingValueString);
  }

  public String format(int value) {
    DateTimeFormatter format = getFormat();
    if (value == TimeColumnType.missingValueIndicator()) {
      return getMissingString();
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
    return "TimeColumnFormatter{"
        + "format="
        + getFormat()
        + ", missingString='"
        + getMissingString()
        + '\''
        + '}';
  }
}
