package tech.tablesaw.columns.datetimes;

import static tech.tablesaw.columns.datetimes.PackedLocalDateTime.asLocalDateTime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.concurrent.Immutable;

@Immutable
public class DateTimeColumnFormatter {

  private final DateTimeFormatter format;
  private String missingValueString = "";

  public DateTimeColumnFormatter() {
    this.format = null;
  }

  public DateTimeColumnFormatter(DateTimeFormatter format) {
    this.format = format;
  }

  public DateTimeColumnFormatter(DateTimeFormatter format, String missingValueString) {
    this.format = format;
    this.missingValueString = missingValueString;
  }

  public String format(long value) {
    if (value == DateTimeColumnType.missingValueIndicator()) {
      return missingValueString;
    }
    if (format == null) {
      return PackedLocalDateTime.toString(value);
    }
    LocalDateTime time = asLocalDateTime(value);
    if (time == null) {
      return "";
    }
    return format.format(time);
  }

  @Override
  public String toString() {
    return "DateTimeColumnFormatter{"
        + "format="
        + format
        + ", missingValueString='"
        + missingValueString
        + '\''
        + '}';
  }
}
